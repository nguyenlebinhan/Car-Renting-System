package org.ats.utils;

import org.ats.entities.user.Account;
import org.ats.repository.account.AccountRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class AuthUtils {

    private final AccountRepository accountRepository;

    public AuthUtils(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Account loggedInAccount(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return accountRepository.findAccountByAccountNameIgnoreCase(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found with account name: " + authentication.getName()));
    }
}
