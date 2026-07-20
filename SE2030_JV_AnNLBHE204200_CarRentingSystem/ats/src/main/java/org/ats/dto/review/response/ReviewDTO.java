package org.ats.dto.review.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDTO {
    private Integer reviewId;
    private Integer carRentalId;
    private Integer carId;
    private String carName;
    private LocalDate pickUpDate;
    private LocalDate returnDate;
    private int reviewStar;
    private String comment;
}
