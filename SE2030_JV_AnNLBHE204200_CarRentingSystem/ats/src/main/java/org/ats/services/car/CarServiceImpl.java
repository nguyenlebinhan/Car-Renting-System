package org.ats.services.car;

import org.ats.dto.car.request.CarCreationRequest;
import org.ats.dto.car.response.CarDTO;
import org.ats.dto.car.response.CarResponse;
import org.ats.entities.car.Car;
import org.ats.entities.car.CarProducer;
import org.ats.exceptions.ResourceNotFoundException;
import org.ats.mapper.car.CarMapper;
import org.ats.repository.car.CarProducerRepository;
import org.ats.repository.car.CarRentalRepository;
import org.ats.repository.car.CarRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
public class CarServiceImpl implements CarService {
    private static final Set<String> SORT_FIELDS = Set.of(
            "carId", "carName", "carModelYear", "color", "capacity", "importDate", "rentPrice", "status");

    private final CarRepository carRepository;
    private final CarMapper carMapper;
    private final CarProducerRepository producerRepository;
    private final CarRentalRepository rentalRepository;

    public CarServiceImpl(CarRepository carRepository, CarMapper carMapper,
                          CarProducerRepository producerRepository, CarRentalRepository rentalRepository) {
        this.carRepository = carRepository;
        this.carMapper = carMapper;
        this.producerRepository = producerRepository;
        this.rentalRepository = rentalRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public CarResponse getAllCarInfo(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        String field = SORT_FIELDS.contains(sortBy) ? sortBy : "carName";
        Sort sortByAnyOrder = "desc".equalsIgnoreCase(sortOrder) ? Sort.by(field).descending() : Sort.by(field).ascending();
        Pageable pageable = PageRequest.of(pageNumber,pageSize,sortByAnyOrder);
        Page<CarDTO> result = carRepository.findAll(pageable).map(carMapper::toCarDTO);
        return new CarResponse(result.getContent(), result.getNumber(), result.getSize(), result.getTotalElements(), result.getTotalPages(), result.isLast());
    }

    @Override
    @Transactional(readOnly = true)
    public CarCreationRequest getCarForm(Integer carId) {
        Car car = carRepository.findByCarId(carId)
                .orElseThrow(() -> new ResourceNotFoundException("Car", "carId", carId));
        return new CarCreationRequest(car.getCarId(), car.getCarName(), car.getCarModelYear(),
                car.getColor(), car.getCapacity(), car.getDescription(), car.getImportDate(),
                car.getRentPrice(), car.getStatus(), car.getCarProducer().getProducerId());
    }

    @Override
    public List<CarProducer> getAllProducers() {
        return producerRepository.findAllByOrderByProducerNameAsc();
    }

    @Override
    public long countAllCars() {
        return carRepository.count();
    }

    @Override
    @Transactional
    public boolean createNewCar(CarCreationRequest request) {
        if (carRepository.existsCarByCarNameIgnoreCase(request.getCarName().trim())) {
            return false;
        }
        Car car = new Car();
        CarProducer producer = producerRepository.findById(request.getProducerId())
                .orElseThrow(() -> new ResourceNotFoundException("CarProducer", "producerId", request.getProducerId()));
        car.setCarName(request.getCarName().trim());
        car.setCarModelYear(request.getCarModelYear());
        car.setColor(request.getColor().trim());
        car.setCapacity(request.getCapacity());
        car.setDescription(request.getDescription().trim());
        car.setImportDate(request.getImportDate());
        car.setRentPrice(request.getRentPrice());
        car.setStatus(request.getStatus());
        car.setCarProducer(producer);
        carRepository.save(car);
        return true;
    }

    @Override
    @Transactional
    public boolean updateCar(Integer carId, CarCreationRequest request) {
        if (carRepository.existsCarByCarNameIgnoreCaseAndCarIdNot(request.getCarName().trim(), carId)) {
            return false;
        }
        Car car = carRepository.findByCarId(carId)
                .orElseThrow(() -> new ResourceNotFoundException("Car", "carId", carId));
        CarProducer producer = producerRepository.findById(request.getProducerId())
                .orElseThrow(() -> new ResourceNotFoundException("CarProducer", "producerId", request.getProducerId()));
        car.setCarName(request.getCarName().trim());
        car.setCarModelYear(request.getCarModelYear());
        car.setColor(request.getColor().trim());
        car.setCapacity(request.getCapacity());
        car.setDescription(request.getDescription().trim());
        car.setImportDate(request.getImportDate());
        car.setRentPrice(request.getRentPrice());
        car.setStatus(request.getStatus());
        car.setCarProducer(producer);
        return true;
    }

    @Override
    @Transactional
    public String deleteOrRetireCar(Integer carId) {
        Car car = carRepository.findByCarId(carId)
                .orElseThrow(() -> new ResourceNotFoundException("Car", "carId", carId));
        if (rentalRepository.existsByCarCarId(carId)) {
            car.setStatus("INACTIVE");
            return "Xe đã có giao dịch thuê nên được chuyển sang trạng thái INACTIVE.";
        }
        carRepository.delete(car);
        return "Đã xóa xe thành công.";
    }




}
