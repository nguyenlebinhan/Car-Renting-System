package org.ats.services.account;

import org.ats.entities.user.Account;
import org.ats.entities.user.Customer;
import org.ats.repository.account.AccountRepository;
import org.ats.repository.customer.CustomerRepository;
import org.ats.security.request.RegisterRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final CustomerRepository customerRepository;

    public AccountServiceImpl(AccountRepository accountRepository,
                              PasswordEncoder passwordEncoder,
                              CustomerRepository customerRepository) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
        this.customerRepository = customerRepository;
    }

    @Override
    @Transactional
    public Boolean register(RegisterRequest request) {
        if (accountRepository.existsAccountByAccountNameIgnoreCase(request.getAccountName().trim())
                || accountRepository.existsAccountByEmail(request.getEmail())
                || customerRepository.existsByIdentityCard(request.getIdentityCard())
                || customerRepository.existsByLicenceNumber(request.getLicenceNumber())) {
            return false;
        }

        Account account = Account.builder()
                .accountName(request.getAccountName().trim())
                .email(request.getEmail().trim().toLowerCase())
                .password(passwordEncoder.encode(request.getPassword()))
                .role("CUSTOMER")
                .build();
        accountRepository.save(account);

        Customer customer = Customer.builder()
                .fullName(request.getFullName().trim())
                .mobile(request.getMobile().trim())
                .birthday(request.getBirthday())
                .identityCard(request.getIdentityCard().trim())
                .licenceNumber(request.getLicenceNumber().trim())
                .licenceDate(request.getLicenceDate())
                .account(account)
                .build();
        customerRepository.save(customer);
        account.setCustomer(customer);
        return true;
    }
}
