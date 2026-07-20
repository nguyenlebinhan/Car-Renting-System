package org.ats.services.car;

import org.ats.dto.carRental.request.CarRentalRequest;
import org.ats.dto.carRental.response.CarRentalReport;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface CarRentalService {
    long countTotalCarRental();
    CarRentalReport getAllRentals(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);
    CarRentalReport getCarRentalReport(LocalDate startDate, LocalDate endDate,
                                       Integer pageNumber, Integer pageSize);
    CarRentalReport getCustomerHistory(Integer customerId, Integer pageNumber, Integer pageSize);
    Map<Integer, String> getRentalStatuses(Integer customerId, List<Integer> carIds);
    boolean rentCar(CarRentalRequest request);
    void updateRentalStatus(Integer rentalId, String newStatus, LocalDate actualReturnDate);
}
