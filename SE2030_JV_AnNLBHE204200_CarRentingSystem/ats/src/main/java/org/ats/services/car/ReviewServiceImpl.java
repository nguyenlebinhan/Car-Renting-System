package org.ats.services.car;

import org.ats.dto.review.request.ReviewRequest;
import org.ats.dto.review.response.ReviewDTO;
import org.ats.dto.review.response.ReviewPageResponse;
import org.ats.entities.car.CarRental;
import org.ats.entities.car.Review;
import org.ats.exceptions.ResourceNotFoundException;
import org.ats.repository.car.CarRentalRepository;
import org.ats.repository.car.ReviewRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final CarRentalRepository rentalRepository;

    public ReviewServiceImpl(ReviewRepository reviewRepository, CarRentalRepository rentalRepository) {
        this.reviewRepository = reviewRepository;
        this.rentalRepository = rentalRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public ReviewPageResponse getCustomerReviews(Integer customerId, Integer pageNumber, Integer pageSize) {
        int page = Math.max(pageNumber == null ? 0 : pageNumber, 0);
        int size = Math.min(Math.max(pageSize == null ? 10 : pageSize, 1), 100);
        Page<ReviewDTO> reviews = reviewRepository.findByCarRentalCustomerCustomerId(
                customerId, PageRequest.of(page, size, Sort.by("reviewId").descending()))
                .map(this::toDTO);
        return new ReviewPageResponse(reviews.getContent(), reviews.getNumber(), reviews.getSize(),
                reviews.getTotalElements(), reviews.getTotalPages(), reviews.isLast());
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Integer, ReviewDTO> getReviewsByRentalIds(Integer customerId, List<Integer> rentalIds) {
        Map<Integer, ReviewDTO> result = new LinkedHashMap<>();
        if (rentalIds == null || rentalIds.isEmpty()) return result;
        reviewRepository.findByCarRentalCustomerCustomerIdAndCarRentalCarRentalIdIn(customerId, rentalIds)
                .forEach(review -> result.put(review.getCarRental().getCarRentalId(), toDTO(review)));
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public ReviewRequest prepareCreateForm(Integer customerId, Integer rentalId) {
        CarRental rental = findOwnedRental(customerId, rentalId);
        validateReviewableRental(rental);
        if (reviewRepository.existsByCarRentalCarRentalId(rentalId)) {
            throw new IllegalArgumentException("Giao dịch này đã được đánh giá.");
        }
        return new ReviewRequest(null, rentalId, null, "");
    }

    @Override
    @Transactional(readOnly = true)
    public ReviewRequest getReviewForm(Integer customerId, Integer reviewId) {
        Review review = findOwnedReview(customerId, reviewId);
        return new ReviewRequest(review.getReviewId(), review.getCarRental().getCarRentalId(),
                review.getReviewStar(), review.getComment());
    }

    @Override
    @Transactional
    public void createReview(Integer customerId, ReviewRequest request) {
        validateRequest(request);
        CarRental rental = findOwnedRental(customerId, request.getCarRentalId());
        validateReviewableRental(rental);
        if (reviewRepository.existsByCarRentalCarRentalId(rental.getCarRentalId())) {
            throw new IllegalArgumentException("Giao dịch này đã được đánh giá.");
        }
        Review review = new Review();
        review.setCarRental(rental);
        copyReviewFields(review, request);
        reviewRepository.save(review);
    }

    @Override
    @Transactional
    public void updateReview(Integer customerId, Integer reviewId, ReviewRequest request) {
        validateRequest(request);
        Review review = findOwnedReview(customerId, reviewId);
        if (!review.getCarRental().getCarRentalId().equals(request.getCarRentalId())) {
            throw new IllegalArgumentException("Không thể thay đổi giao dịch của đánh giá.");
        }
        copyReviewFields(review, request);
    }

    @Override
    @Transactional
    public void deleteReview(Integer customerId, Integer reviewId) {
        reviewRepository.delete(findOwnedReview(customerId, reviewId));
    }

    private CarRental findOwnedRental(Integer customerId, Integer rentalId) {
        return rentalRepository.findByCarRentalIdAndCustomerCustomerId(rentalId, customerId)
                .orElseThrow(() -> new ResourceNotFoundException("CarRental", "carRentalId", rentalId));
    }

    private Review findOwnedReview(Integer customerId, Integer reviewId) {
        return reviewRepository.findByReviewIdAndCarRentalCustomerCustomerId(reviewId, customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "reviewId", reviewId));
    }

    private void validateReviewableRental(CarRental rental) {
        if (!"COMPLETED".equalsIgnoreCase(rental.getStatus())) {
            throw new IllegalArgumentException("Chỉ có thể đánh giá giao dịch đã hoàn tất.");
        }
    }

    private void validateRequest(ReviewRequest request) {
        if (request == null || request.getCarRentalId() == null || request.getReviewStar() == null
                || request.getReviewStar() < 1 || request.getReviewStar() > 5
                || request.getComment() == null || request.getComment().trim().isEmpty()
                || request.getComment().trim().length() > 500) {
            throw new IllegalArgumentException("Thông tin đánh giá không hợp lệ.");
        }
    }

    private void copyReviewFields(Review review, ReviewRequest request) {
        review.setReviewStar(request.getReviewStar());
        review.setComment(request.getComment().trim());
    }

    private ReviewDTO toDTO(Review review) {
        CarRental rental = review.getCarRental();
        return ReviewDTO.builder()
                .reviewId(review.getReviewId())
                .carRentalId(rental.getCarRentalId())
                .carId(rental.getCar().getCarId())
                .carName(rental.getCar().getCarName())
                .pickUpDate(rental.getPickUpDate())
                .returnDate(rental.getReturnDate())
                .reviewStar(review.getReviewStar())
                .comment(review.getComment())
                .build();
    }
}
