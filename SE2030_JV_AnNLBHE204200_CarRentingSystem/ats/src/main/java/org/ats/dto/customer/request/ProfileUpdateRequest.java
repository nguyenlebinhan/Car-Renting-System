package org.ats.dto.customer.request;

import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProfileUpdateRequest {
    Integer customerId;

    @NotBlank(message = "Vui lòng nhập họ và tên")
    @Size(max = 200, message = "Họ và tên tối đa 200 ký tự")
    String customerName;

    @NotBlank(message = "Vui lòng nhập số điện thoại")
    @Pattern(regexp = "^(0|\\+84)[0-9]{9,10}$", message = "Số điện thoại không đúng định dạng")
    String mobile;

    @NotNull(message = "Vui lòng chọn ngày sinh")
    @Past(message = "Ngày sinh phải trước ngày hiện tại")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate birthday;

    @NotBlank(message = "Vui lòng nhập CCCD/CMND")
    @Size(max = 20, message = "CCCD/CMND tối đa 20 ký tự")
    String identityCard;

    @NotBlank(message = "Vui lòng nhập số giấy phép lái xe")
    @Size(max = 20, message = "Số giấy phép tối đa 20 ký tự")
    String licenceNumber;

    @NotNull(message = "Vui lòng chọn ngày cấp giấy phép")
    @PastOrPresent(message = "Ngày cấp không được ở tương lai")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate licenceDate;
}
