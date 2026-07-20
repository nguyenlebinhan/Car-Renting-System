package org.ats.mapper.user;

import org.ats.dto.customer.request.ProfileUpdateRequest;
import org.ats.dto.customer.request.UserCreationRequest;
import org.ats.dto.customer.response.CustomerProfileResponse;
import org.ats.entities.user.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    @Mapping(source = "fullName", target = "customerName")
    @Mapping(source = "account.accountName", target = "accountName")
    @Mapping(source = "account.email", target = "email")
    CustomerProfileResponse toCustomerProfileResponse(Customer customer);

    @Mapping(source = "customerName", target = "fullName")
    @Mapping(target = "customerId", ignore = true)
    @Mapping(target = "account", ignore = true)
    @Mapping(target = "carRentals", ignore = true)
    Customer toCustomer(UserCreationRequest request);

    @Mapping(source = "customerName", target = "fullName")
    @Mapping(target = "account", ignore = true)
    @Mapping(target = "carRentals", ignore = true)
    Customer toCustomer(ProfileUpdateRequest request);
}
