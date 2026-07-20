package org.ats.dto.review.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRequest {
    private Integer reviewId;

    @NotNull(message = "Giao dịch thuê xe không được để trống.")
    private Integer carRentalId;

    @NotNull(message = "Vui lòng chọn số sao.")
    @Min(value = 1, message = "Đánh giá phải từ 1 đến 5 sao.")
    @Max(value = 5, message = "Đánh giá phải từ 1 đến 5 sao.")
    private Integer reviewStar;

    @NotBlank(message = "Nội dung đánh giá không được để trống.")
    @Size(max = 500, message = "Nội dung đánh giá không được vượt quá 500 ký tự.")
    private String comment;
}
