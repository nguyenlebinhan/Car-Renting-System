package org.ats.services.car;

import org.ats.dto.car.request.CarProducerRequest;
import org.ats.entities.car.CarProducer;

import java.util.List;

public interface CarProducerService {
    List<CarProducer> getAllProducers();
    CarProducerRequest getProducerForm(Integer producerId);
    boolean createProducer(CarProducerRequest request);
    boolean updateProducer(Integer producerId, CarProducerRequest request);
    boolean deleteProducer(Integer producerId);
}
