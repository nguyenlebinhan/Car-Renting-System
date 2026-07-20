package org.ats.dto.car.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarDTO {
    Integer carId;
    String carName;
    int carModelYear;
    String color;
    int capacity;
    String description;
    LocalDate importDate;
    BigDecimal rentPrice;
    String status;
    Integer producerId;
    String carProducerName;
}
