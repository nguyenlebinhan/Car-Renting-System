package org.ats.services.car;

import org.ats.dto.carRental.request.CarRentalRequest;
import org.ats.dto.carRental.response.CarRentalDTO;
import org.ats.dto.carRental.response.CarRentalReport;
import org.ats.entities.car.Car;
import org.ats.entities.car.CarRental;
import org.ats.entities.user.Customer;
import org.ats.exceptions.ResourceNotFoundException;
import org.ats.mapper.car.CarRentalMapper;
import org.ats.repository.car.CarRentalRepository;
import org.ats.repository.car.CarRepository;
import org.ats.repository.customer.CustomerRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class CarRentalServiceImpl implements CarRentalService {
    private static final List<String> BLOCKING_STATUSES = List.of(
            "PENDING", "CONFIRMED", "ACTIVE", "OVERDUE");
    private static final List<String> OCCUPYING_STATUSES = List.of(
            "CONFIRMED", "ACTIVE", "OVERDUE");
    private static final Set<String> RENTAL_STATUSES = Set.of(
            "PENDING", "CONFIRMED", "ACTIVE", "COMPLETED", "OVERDUE", "CANCELLED");
    private static final Set<String> SORT_FIELDS = Set.of(
            "carRentalId", "pickUpDate", "returnDate", "rentPrice", "status");

    private final CarRentalRepository rentalRepository;
    private final CarRepository carRepository;
    private final CustomerRepository customerRepository;
    private final CarRentalMapper carRentalMapper;

    public CarRentalServiceImpl(CarRentalRepository rentalRepository, CarRepository carRepository,
                                CustomerRepository customerRepository, CarRentalMapper carRentalMapper) {
        this.rentalRepository = rentalRepository;
        this.carRepository = carRepository;
        this.customerRepository = customerRepository;
        this.carRentalMapper = carRentalMapper;
    }

    @Override
    public long countTotalCarRental() {
        return rentalRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public CarRentalReport getAllRentals(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        String field = SORT_FIELDS.contains(sortBy) ? sortBy : "pickUpDate";
        Sort sortByAnyOrder = "asc".equalsIgnoreCase(sortOrder) ? Sort.by(field).ascending() : Sort.by(field).descending();
        Pageable rentalPages = PageRequest.of(pageNumber, pageSize, sortByAnyOrder);
        Page<CarRentalDTO> carRentalPage = rentalRepository.findAll(rentalPages).map(carRentalMapper::toDTO);
        return new CarRentalReport(carRentalPage.getContent(),carRentalPage.getNumber(),carRentalPage.getSize(),carRentalPage.getTotalElements(),carRentalPage.getTotalPages(),carRentalPage.isLast());
    }

    @Override
    @Transactional(readOnly = true)
    public CarRentalReport getCarRentalReport(LocalDate startDate, LocalDate endDate,
                                              Integer pageNumber, Integer pageSize) {
        if (startDate == null || endDate == null || endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("Khoảng thời gian báo cáo không hợp lệ.");
        }
        Sort sort = Sort.by(Sort.Order.desc("rentPrice"), Sort.Order.desc("pickUpDate"));
        Pageable pageable = PageRequest.of(pageNumber,pageSize,sort);
        Page<CarRentalDTO> carRentalPage =rentalRepository.findByPickUpDateBetween(startDate, endDate, pageable).map(carRentalMapper::toDTO);
        return new CarRentalReport(carRentalPage.getContent(),carRentalPage.getNumber(),carRentalPage.getSize(),carRentalPage.getTotalElements(),carRentalPage.getTotalPages(),carRentalPage.isLast());
    }

    @Override
    @Transactional(readOnly = true)
    public CarRentalReport getCustomerHistory(Integer customerId, Integer pageNumber, Integer pageSize) {
        Sort sort = Sort.by(Sort.Order.desc("pickUpDate"), Sort.Order.desc("carRentalId"));
        Pageable pageable = PageRequest.of(pageNumber,pageSize,sort);
        Page<CarRentalDTO> carRentalPage = rentalRepository.findByCustomerCustomerId(customerId, pageable).map(carRentalMapper::toDTO);
        return new CarRentalReport(carRentalPage.getContent(),carRentalPage.getNumber(),carRentalPage.getSize(),carRentalPage.getTotalElements(),carRentalPage.getTotalPages(),carRentalPage.isLast());
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Integer, String> getRentalStatuses(Integer customerId, List<Integer> carIds) {
        Map<Integer, String> result = new LinkedHashMap<>();
        if (customerId == null || carIds == null || carIds.isEmpty()) return result;
        rentalRepository.findByCustomerCustomerIdAndCarCarIdInOrderByCarRentalIdDesc(customerId, carIds)
                .forEach(rental -> result.putIfAbsent(rental.getCar().getCarId(), rental.getStatus()));
        return result;
    }

    @Override
    @Transactional
    public boolean rentCar(CarRentalRequest request) {
        if (request.getCustomerId() == null || request.getCarId() == null
                || request.getPickUpDate() == null || request.getReturnDate() == null
                || request.getPickUpDate().isBefore(LocalDate.now())
                || !request.getReturnDate().isAfter(request.getPickUpDate())) {
            return false;
        }

        Customer customer = customerRepository.findByCustomerId(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "customerId", request.getCustomerId()));
        Integer id = request.getCarId();

        Car car = carRepository.findByCarId(id).orElseThrow(() -> new ResourceNotFoundException("Car","carId",request.getCarId()));
        if (!"AVAILABLE".equalsIgnoreCase(car.getStatus())
                || rentalRepository.countOverlappingRentals(car.getCarId(), request.getPickUpDate(),
                request.getReturnDate(), BLOCKING_STATUSES) > 0) {
            return false;

        }

        long rentalDays = Math.max(1, ChronoUnit.DAYS.between(request.getPickUpDate(), request.getReturnDate()));
        CarRental rentals =  CarRental.builder()
                .car(car)
                .customer(customer)
                .pickUpDate(request.getPickUpDate())
                .returnDate(request.getReturnDate())
                .rentPrice(car.getRentPrice().multiply(BigDecimal.valueOf(rentalDays)))
                .status("PENDING")
                .build();
        rentalRepository.save(rentals);
        return true;
    }

    @Override
    @Transactional
    public void updateRentalStatus(Integer rentalId, String newStatus, LocalDate actualReturnDate) {
        newStatus = newStatus == null ? "" : newStatus.trim().toUpperCase(Locale.ROOT);
        if (!RENTAL_STATUSES.contains(newStatus)) {
            throw new IllegalArgumentException("Trạng thái giao dịch không hợp lệ.");
        }
        CarRental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new ResourceNotFoundException("CarRental", "carRentalId", rentalId));
        if (!isAllowedTransition(rental.getStatus(), newStatus)) {
            throw new IllegalArgumentException("Không thể chuyển trạng thái từ " + rental.getStatus() + " sang " + newStatus + ".");
        }
        if ("ACTIVE".equals(newStatus) && !LocalDate.now().equals(rental.getPickUpDate())) {
            throw new IllegalArgumentException("Chỉ có thể bắt đầu thuê xe đúng ngày nhận xe.");
        }
        if ("COMPLETED".equals(newStatus)
                && "ACTIVE".equals(rental.getStatus())
                && !LocalDate.now().equals(rental.getReturnDate())) {
            throw new IllegalArgumentException("Chỉ có thể hoàn tất giao dịch đúng ngày trả xe dự kiến.");
        }
        if ("OVERDUE".equals(newStatus)) {
            if (!LocalDate.now().isAfter(rental.getReturnDate())) {
                throw new IllegalArgumentException("Chỉ có thể cập nhật quá hạn sau ngày trả xe dự kiến.");
            }
            if (actualReturnDate == null || !actualReturnDate.isAfter(rental.getReturnDate())) {
                throw new IllegalArgumentException("Ngày trả thực tế phải sau ngày trả xe dự kiến.");
            }
            long actualRentalDays = Math.max(1,
                    ChronoUnit.DAYS.between(rental.getPickUpDate(), actualReturnDate));
            rental.setRentPrice(rental.getCar().getRentPrice()
                    .multiply(BigDecimal.valueOf(actualRentalDays)));
        }
        rental.setStatus(newStatus);
        Car car = rental.getCar();
        if ("CONFIRMED".equals(newStatus) || "ACTIVE".equals(newStatus) || "OVERDUE".equals(newStatus)) {
            car.setStatus("RENTED");
        } else if ("COMPLETED".equals(newStatus) || "CANCELLED".equals(newStatus)) {
            if (rentalRepository.countOtherCurrentRentals(car.getCarId(), rentalId, OCCUPYING_STATUSES) == 0
                    && !"INACTIVE".equalsIgnoreCase(car.getStatus())
                    && !"MAINTENANCE".equalsIgnoreCase(car.getStatus())) {
                car.setStatus("AVAILABLE");
            }
        }
    }

    private boolean isAllowedTransition(String current, String next) {
        if (Objects.equals(current, next)) return true;
        return switch (current) {
            case "PENDING" -> "CONFIRMED".equals(next) || "CANCELLED".equals(next);
            case "CONFIRMED" -> "ACTIVE".equals(next) || "CANCELLED".equals(next);
            case "ACTIVE" -> "COMPLETED".equals(next) || "OVERDUE".equals(next);
            case "OVERDUE" -> "COMPLETED".equals(next);
            default -> false;
        };
    }



}
