package org.ats.repository.car;

import org.ats.entities.car.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Integer> {

    @EntityGraph(attributePaths = {"carRental", "carRental.car"})
    Page<Review> findByCarRentalCustomerCustomerId(Integer customerId, Pageable pageable);

    @EntityGraph(attributePaths = {"carRental", "carRental.car", "carRental.customer"})
    Optional<Review> findByReviewIdAndCarRentalCustomerCustomerId(Integer reviewId, Integer customerId);

    @EntityGraph(attributePaths = {"carRental", "carRental.car"})
    List<Review> findByCarRentalCustomerCustomerIdAndCarRentalCarRentalIdIn(
            Integer customerId, List<Integer> rentalIds);

    boolean existsByCarRentalCarRentalId(Integer carRentalId);
}
