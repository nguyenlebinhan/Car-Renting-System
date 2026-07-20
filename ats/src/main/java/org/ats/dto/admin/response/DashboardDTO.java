package org.ats.dto.admin.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DashboardDTO {
    long numberOfCustomer;
    long totalCars;
    long totalCarRentals;
}
