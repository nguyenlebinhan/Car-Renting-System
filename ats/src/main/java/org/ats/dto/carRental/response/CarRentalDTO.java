package org.ats.dto.carRental.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarRentalDTO {
    Integer carRentalId;
    Integer customerId;
    Integer carId;
    String carName;
    String producerName;
    int carModelYear;
    String color;
    int capacity;
    String description;
    LocalDate pickUpDate;
    LocalDate returnDate;
    BigDecimal rentPrice;
    String status;
    String customerName;
}
