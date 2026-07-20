package org.ats.services.customer;

import org.ats.dto.customer.request.ProfileUpdateRequest;
import org.ats.dto.customer.request.UserCreationRequest;
import org.ats.dto.customer.response.CustomerProfileResponse;
import org.ats.dto.customer.response.ProfileResponse;
import org.ats.entities.user.Account;

public interface CustomerService {
    ProfileResponse getAllInformation(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);
    UserCreationRequest getCustomerForm(Integer customerId);
    boolean createCustomer(UserCreationRequest request);
    boolean updateCustomer(Integer customerId, UserCreationRequest request);
    boolean deleteCustomer(Integer customerId);
    long countAllCustomer();
    CustomerProfileResponse getOwnProfile(Account loggedInAccount);
    boolean updateCustomerProfile(ProfileUpdateRequest request);
}
