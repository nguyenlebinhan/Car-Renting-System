package org.ats.repository.account;

import org.ats.entities.user.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {
    @EntityGraph(attributePaths = "customer")
    Optional<Account> findAccountByAccountNameIgnoreCase(String accountName);
    boolean existsAccountByAccountNameIgnoreCase(String accountName);
    boolean existsAccountByAccountNameIgnoreCaseAndAccountIdNot(String accountName, Integer accountId);

    @EntityGraph(attributePaths = "customer")
    Optional<Account> findAccountByEmailIgnoreCase(String email);
    default Optional<Account> findAccountByEmail(String email) {
        return findAccountByEmailIgnoreCase(email);
    }
    boolean existsAccountByEmailIgnoreCase(String email);
    boolean existsAccountByEmailIgnoreCaseAndAccountIdNot(String email, Integer accountId);
    default boolean existsAccountByEmail(String email) {
        return existsAccountByEmailIgnoreCase(email);
    }
}
