package org.ats.security.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoginRequest {
    @NotBlank(message = "Trường thông tin trên không được để trống")
    String accountName;

    @Size(min = 5, message = "Mật khẩu phải nhiều hơn 5 kí tự")
    @NotBlank(message = "Trường thông tin trên không được để trống")
    String password;
}

