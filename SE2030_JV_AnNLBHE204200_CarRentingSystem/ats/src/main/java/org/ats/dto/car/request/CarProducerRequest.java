package org.ats.dto.car.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CarProducerRequest {
    private Integer producerId;

    @NotBlank(message = "Vui lòng nhập tên hãng xe")
    @Size(max = 100, message = "Tên hãng xe tối đa 100 ký tự")
    private String producerName;

    @NotBlank(message = "Vui lòng nhập địa chỉ")
    @Size(max = 200, message = "Địa chỉ tối đa 200 ký tự")
    private String address;

    @NotBlank(message = "Vui lòng nhập quốc gia")
    @Size(max = 100, message = "Quốc gia tối đa 100 ký tự")
    private String country;
}
