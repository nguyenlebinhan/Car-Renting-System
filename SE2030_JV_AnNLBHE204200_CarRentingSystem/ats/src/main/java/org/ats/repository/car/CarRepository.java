package org.ats.repository.car;

import org.ats.entities.car.Car;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CarRepository extends JpaRepository<Car, Integer> {
    @Override
    @EntityGraph(attributePaths = "carProducer")
    Page<Car> findAll(Pageable pageable);

    @EntityGraph(attributePaths = "carProducer")
    Optional<Car> findByCarId(Integer carId);

    boolean existsCarByCarNameIgnoreCase(String carName);
    boolean existsCarByCarNameIgnoreCaseAndCarIdNot(String carName, Integer carId);
    boolean existsByCarProducerProducerId(Integer producerId);
}
