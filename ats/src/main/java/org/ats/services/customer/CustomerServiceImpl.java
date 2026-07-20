package org.ats.services.customer;

import org.ats.dto.customer.request.ProfileUpdateRequest;
import org.ats.dto.customer.request.UserCreationRequest;
import org.ats.dto.customer.response.CustomerProfileResponse;
import org.ats.dto.customer.response.ProfileResponse;
import org.ats.entities.user.Account;
import org.ats.entities.user.Customer;
import org.ats.exceptions.ResourceNotFoundException;
import org.ats.mapper.user.CustomerMapper;
import org.ats.repository.account.AccountRepository;
import org.ats.repository.car.CarRentalRepository;
import org.ats.repository.customer.CustomerRepository;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
public class CustomerServiceImpl implements CustomerService {
    private static final Set<String> SORT_FIELDS = Set.of("customerId", "fullName", "birthday", "mobile");

    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;
    private final CustomerMapper customerMapper;
    private final PasswordEncoder passwordEncoder;
    private final CarRentalRepository rentalRepository;

    public CustomerServiceImpl(AccountRepository accountRepository, CustomerRepository customerRepository,
                               CustomerMapper customerMapper, PasswordEncoder passwordEncoder,
                               CarRentalRepository rentalRepository) {
        this.accountRepository = accountRepository;
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
        this.passwordEncoder = passwordEncoder;
        this.rentalRepository = rentalRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public ProfileResponse getAllInformation(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        int page = pageNumber == null || pageNumber < 0 ? 0 : pageNumber;
        int size = pageSize == null ? 20 : Math.max(1, Math.min(pageSize, 100));
        String field = SORT_FIELDS.contains(sortBy) ? sortBy : "fullName";
        Sort.Direction direction = "desc".equalsIgnoreCase(sortOrder) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Page<CustomerProfileResponse> result = customerRepository
                .findAll(PageRequest.of(page, size, Sort.by(direction, field)))
                .map(customerMapper::toCustomerProfileResponse);
        return new ProfileResponse(result.getContent(), result.getNumber(), result.getSize(),
                result.getTotalElements(), result.getTotalPages(), result.isLast());
    }

    @Override
    @Transactional(readOnly = true)
    public UserCreationRequest getCustomerForm(Integer customerId) {
        Customer customer = findCustomer(customerId);
        Account account = customer.getAccount();
        return new UserCreationRequest(customer.getCustomerId(), account.getAccountName(), account.getEmail(), null,
                account.getRole(), customer.getFullName(), customer.getMobile(), customer.getBirthday(),
                customer.getIdentityCard(), customer.getLicenceNumber(), customer.getLicenceDate());
    }

    @Override
    @Transactional
    public boolean createCustomer(UserCreationRequest request) {
        String accountName = normalize(request.getAccountName());
        String email = normalize(request.getEmail());
        String identityCard = normalize(request.getIdentityCard());
        String licenceNumber = normalize(request.getLicenceNumber());
        if (request.getPassword() == null || request.getPassword().isBlank() || request.getPassword().length() < 6
                || accountName == null || email == null || identityCard == null || licenceNumber == null
                || accountRepository.existsAccountByAccountNameIgnoreCase(accountName)
                || accountRepository.existsAccountByEmail(email)
                || customerRepository.existsByIdentityCard(identityCard)
                || customerRepository.existsByLicenceNumber(licenceNumber)) {
            return false;
        }
        Account account = Account.builder()
                .accountName(accountName)
                .email(email.toLowerCase())
                .password(passwordEncoder.encode(request.getPassword()))
                .role("CUSTOMER")
                .build();
        accountRepository.save(account);
        Customer customer = customerMapper.toCustomer(request);
        applyProfile(request.getCustomerName(), request.getMobile(), request.getBirthday(), identityCard,
                licenceNumber, request.getLicenceDate(), customer);
        customer.setAccount(account);
        customerRepository.save(customer);
        account.setCustomer(customer);
        return true;
    }

    @Override
    @Transactional
    public boolean updateCustomer(Integer customerId, UserCreationRequest request) {
        Customer customer = findCustomer(customerId);
        Account account = customer.getAccount();
        String accountName = normalize(request.getAccountName());
        String email = normalize(request.getEmail());
        String identityCard = normalize(request.getIdentityCard());
        String licenceNumber = normalize(request.getLicenceNumber());
        if (accountName == null || email == null || identityCard == null || licenceNumber == null
                || accountRepository.existsAccountByAccountNameIgnoreCaseAndAccountIdNot(
                        accountName, account.getAccountId())
                || accountRepository.existsAccountByEmailIgnoreCaseAndAccountIdNot(email, account.getAccountId())
                || customerRepository.existsByIdentityCardAndCustomerIdNot(identityCard, customerId)
                || customerRepository.existsByLicenceNumberAndCustomerIdNot(licenceNumber, customerId)) {
            return false;
        }
        account.setAccountName(accountName);
        account.setEmail(email.toLowerCase());
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            if (request.getPassword().length() < 6) return false;
            account.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        applyProfile(request.getCustomerName(), request.getMobile(), request.getBirthday(),
                identityCard, licenceNumber, request.getLicenceDate(), customer);
        return true;
    }

    @Override
    @Transactional
    public boolean deleteCustomer(Integer customerId) {
        Customer customer = findCustomer(customerId);
        if (rentalRepository.existsByCustomerCustomerId(customerId)) return false;
        customerRepository.delete(customer);
        customerRepository.flush();
        return true;
    }

    @Override
    public long countAllCustomer() {
        return customerRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerProfileResponse getOwnProfile(Account loggedInAccount) {
        Customer customer = customerRepository.findByAccountEmailIgnoreCase(loggedInAccount.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "email", loggedInAccount.getEmail()));
        return customerMapper.toCustomerProfileResponse(customer);
    }

    @Override
    @Transactional
    public boolean updateCustomerProfile(ProfileUpdateRequest request) {
        Customer customer = findCustomer(request.getCustomerId());
        if (customerRepository.existsByIdentityCardAndCustomerIdNot(request.getIdentityCard(), request.getCustomerId())
                || customerRepository.existsByLicenceNumberAndCustomerIdNot(request.getLicenceNumber(), request.getCustomerId())) {
            return false;
        }
        applyProfile(request.getCustomerName(), request.getMobile(), request.getBirthday(),
                request.getIdentityCard(), request.getLicenceNumber(), request.getLicenceDate(), customer);
        return true;
    }

    private Customer findCustomer(Integer id) {
        return customerRepository.findByCustomerId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "customerId", id));
    }

    private void applyProfile(String name, String mobile, java.time.LocalDate birthday, String identityCard,
                              String licenceNumber, java.time.LocalDate licenceDate, Customer customer) {
        customer.setFullName(name.trim());
        customer.setMobile(mobile.trim());
        customer.setBirthday(birthday);
        customer.setIdentityCard(identityCard.trim());
        customer.setLicenceNumber(licenceNumber.trim());
        customer.setLicenceDate(licenceDate);
    }

    private String normalize(String value) {
        if (value == null) return null;
        String normalized = value.trim();
        return normalized.isEmpty() ? null : normalized;
    }
}
