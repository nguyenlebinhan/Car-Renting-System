package org.ats.repository.car;

import org.ats.entities.car.CarProducer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CarProducerRepository extends JpaRepository<CarProducer, Integer> {
    boolean existsCarProducerByProducerNameIgnoreCase(String producerName);
    boolean existsCarProducerByProducerNameIgnoreCaseAndProducerIdNot(String producerName, Integer producerId);
    Optional<CarProducer> findCarProducerByProducerNameIgnoreCase(String producerName);
    List<CarProducer> findAllByOrderByProducerNameAsc();

    default boolean existsCarProducerByProducerName(String producerName) {
        return existsCarProducerByProducerNameIgnoreCase(producerName);
    }

    default Optional<CarProducer> findCarProducerByProducerName(String producerName) {
        return findCarProducerByProducerNameIgnoreCase(producerName);
    }
}
