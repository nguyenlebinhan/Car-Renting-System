package org.ats.services.car;

import org.ats.dto.car.request.CarCreationRequest;
import org.ats.dto.car.response.CarResponse;
import org.ats.entities.car.CarProducer;

import java.util.List;

public interface CarService {
    CarResponse getAllCarInfo(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);
    CarCreationRequest getCarForm(Integer carId);
    List<CarProducer> getAllProducers();
    long countAllCars();
    boolean createNewCar(CarCreationRequest request);
    boolean updateCar(Integer carId, CarCreationRequest request);
    String deleteOrRetireCar(Integer carId);
}
