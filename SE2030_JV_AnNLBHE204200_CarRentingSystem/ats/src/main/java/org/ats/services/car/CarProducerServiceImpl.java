package org.ats.services.car;

import org.ats.dto.car.request.CarProducerRequest;
import org.ats.entities.car.CarProducer;
import org.ats.exceptions.ResourceNotFoundException;
import org.ats.repository.car.CarProducerRepository;
import org.ats.repository.car.CarRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CarProducerServiceImpl implements CarProducerService {
    private final CarProducerRepository producerRepository;
    private final CarRepository carRepository;

    public CarProducerServiceImpl(CarProducerRepository producerRepository, CarRepository carRepository) {
        this.producerRepository = producerRepository;
        this.carRepository = carRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CarProducer> getAllProducers() {
        return producerRepository.findAllByOrderByProducerNameAsc();
    }

    @Override
    @Transactional(readOnly = true)
    public CarProducerRequest getProducerForm(Integer producerId) {
        CarProducer producer = findProducer(producerId);
        return new CarProducerRequest(producer.getProducerId(), producer.getProducerName(),
                producer.getAddress(), producer.getCountry());
    }

    @Override
    @Transactional
    public boolean createProducer(CarProducerRequest request) {
        String producerName = request.getProducerName().trim();
        if (producerRepository.existsCarProducerByProducerNameIgnoreCase(producerName)) {
            return false;
        }

        CarProducer producer = new CarProducer();
        updateFields(producer, request, producerName);
        producerRepository.save(producer);
        return true;
    }

    @Override
    @Transactional
    public boolean updateProducer(Integer producerId, CarProducerRequest request) {
        String producerName = request.getProducerName().trim();
        if (producerRepository.existsCarProducerByProducerNameIgnoreCaseAndProducerIdNot(producerName, producerId)) {
            return false;
        }

        CarProducer producer = findProducer(producerId);
        updateFields(producer, request, producerName);
        return true;
    }

    @Override
    @Transactional
    public boolean deleteProducer(Integer producerId) {
        CarProducer producer = findProducer(producerId);
        if (carRepository.existsByCarProducerProducerId(producerId)) {
            return false;
        }

        producerRepository.delete(producer);
        return true;
    }

    private CarProducer findProducer(Integer producerId) {
        return producerRepository.findById(producerId)
                .orElseThrow(() -> new ResourceNotFoundException("CarProducer", "producerId", producerId));
    }

    private void updateFields(CarProducer producer, CarProducerRequest request, String producerName) {
        producer.setProducerName(producerName);
        producer.setAddress(request.getAddress().trim());
        producer.setCountry(request.getCountry().trim());
    }
}
