package org.ats.security.service;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.ats.entities.user.Account;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@NoArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDetailImpl implements UserDetails, CredentialsContainer {

    private static final long serialVersionUID = 1L;

    Integer id;
    String accountName;
    String password;

    private Collection<? extends GrantedAuthority> authorities;

    public UserDetailImpl(Integer id, String accountName, String password, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.accountName = accountName;
        this.password = password;
        this.authorities = authorities;
    }


    public static UserDetailImpl build(Account account) {
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + account.getRole()));

        return new UserDetailImpl(
                account.getAccountId(),
                account.getAccountName(),
                account.getPassword(),
                authorities
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getUsername() {
        return accountName;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }

    @Override
    public void eraseCredentials() {
        this.password = null;
    }
}

