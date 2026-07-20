package org.ats.services.car;

import org.ats.dto.review.request.ReviewRequest;
import org.ats.dto.review.response.ReviewDTO;
import org.ats.dto.review.response.ReviewPageResponse;

import java.util.List;
import java.util.Map;

public interface ReviewService {
    ReviewPageResponse getCustomerReviews(Integer customerId, Integer pageNumber, Integer pageSize);
    Map<Integer, ReviewDTO> getReviewsByRentalIds(Integer customerId, List<Integer> rentalIds);
    ReviewRequest prepareCreateForm(Integer customerId, Integer rentalId);
    ReviewRequest getReviewForm(Integer customerId, Integer reviewId);
    void createReview(Integer customerId, ReviewRequest request);
    void updateReview(Integer customerId, Integer reviewId, ReviewRequest request);
    void deleteReview(Integer customerId, Integer reviewId);
}
