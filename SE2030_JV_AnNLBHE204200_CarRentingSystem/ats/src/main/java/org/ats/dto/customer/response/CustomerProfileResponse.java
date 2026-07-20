package org.ats.dto.customer.response;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerProfileResponse {
    Integer customerId;
    String customerName;
    String mobile;
    LocalDate birthday;
    String identityCard;
    String licenceNumber;
    LocalDate licenceDate;
    String accountName;
    String email;
}
