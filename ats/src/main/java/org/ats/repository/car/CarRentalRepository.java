package org.ats.repository.car;

import org.ats.entities.car.CarRental;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CarRentalRepository extends JpaRepository<CarRental, Integer> {
    @Override
    @EntityGraph(attributePaths = {"car", "car.carProducer", "customer"})
    Page<CarRental> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"car", "car.carProducer", "customer"})
    Page<CarRental> findByPickUpDateBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);

    @EntityGraph(attributePaths = {"car", "car.carProducer", "customer"})
    Page<CarRental> findByCustomerCustomerId(Integer customerId, Pageable pageable);

    @EntityGraph(attributePaths = {"car", "customer"})
    Optional<CarRental> findByCarRentalIdAndCustomerCustomerId(Integer carRentalId, Integer customerId);

    @EntityGraph(attributePaths = {"car", "car.carProducer", "customer"})
    List<CarRental> findByCustomerCustomerIdAndCarCarIdInOrderByCarRentalIdDesc(
            Integer customerId, List<Integer> carIds);

    boolean existsByCarCarId(Integer carId);
    boolean existsByCustomerCustomerId(Integer customerId);

    @Query("""
            select count(cr) from CarRental cr
            where cr.car.carId = :carId
              and cr.status in :statuses
              and cr.pickUpDate <= :returnDate
              and cr.returnDate >= :pickUpDate
            """)
    long countOverlappingRentals( Integer carId, LocalDate pickUpDate, LocalDate returnDate, List<String> statuses);

    @Query("""
            select count(cr) from CarRental cr
            where cr.car.carId = :carId
              and cr.status in :statuses
              and cr.carRentalId != :rentalId
            """)
    long countOtherCurrentRentals( Integer carId, Integer rentalId, List<String> statuses);
}
