package org.ats.dto.car.request;

import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CarCreationRequest {
    Integer carId;

    @NotBlank(message = "Vui lòng nhập tên xe")
    @Size(max = 200, message = "Tên xe tối đa 200 ký tự")
    String carName;

    @Min(value = 1886, message = "Năm sản xuất không hợp lệ")
    @Max(value = 2100, message = "Năm sản xuất không hợp lệ")
    int carModelYear;

    @NotBlank(message = "Vui lòng nhập màu xe")
    @Size(max = 50, message = "Màu xe tối đa 50 ký tự")
    String color;

    @Positive(message = "Số chỗ phải lớn hơn 0")
    int capacity;

    @NotBlank(message = "Vui lòng nhập mô tả")
    @Size(max = 1000, message = "Mô tả tối đa 1000 ký tự")
    String description;

    @NotNull(message = "Vui lòng chọn ngày nhập xe")
    @PastOrPresent(message = "Ngày nhập xe không được ở tương lai")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate importDate = LocalDate.now();

    @NotNull(message = "Vui lòng nhập giá thuê")
    @DecimalMin(value = "0.01", message = "Giá thuê phải lớn hơn 0")
    BigDecimal rentPrice;

    @NotNull(message = "Vui lòng chọn trạng thái")
    String status = "AVAILABLE";

    @NotNull(message = "Vui lòng chọn hãng xe")
    Integer producerId;
}
