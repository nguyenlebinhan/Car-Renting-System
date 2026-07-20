package org.ats.security.service;

import org.ats.entities.user.Account;
import org.ats.repository.account.AccountRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final AccountRepository accountRepository;

    public UserDetailsServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String accountName) throws UsernameNotFoundException {
        Account account = accountRepository.findAccountByAccountNameIgnoreCase(accountName)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with account name: " + accountName));
        return UserDetailImpl.build(account);
    }
}
