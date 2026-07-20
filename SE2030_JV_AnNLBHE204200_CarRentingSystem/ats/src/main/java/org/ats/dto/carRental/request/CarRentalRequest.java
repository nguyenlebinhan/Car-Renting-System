package org.ats.dto.carRental.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CarRentalRequest {
    Integer customerId;

    @NotNull(message = "Vui lòng chọn ít nhất một xe")
    Integer carId;

    @NotNull(message = "Vui lòng chọn ngày nhận xe")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate pickUpDate;

    @NotNull(message = "Vui lòng chọn ngày trả xe")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate returnDate;
}
