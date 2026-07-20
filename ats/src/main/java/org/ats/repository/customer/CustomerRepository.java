package org.ats.repository.customer;

import org.ats.entities.user.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    @Override
    @EntityGraph(attributePaths = "account")
    Page<Customer> findAll(Pageable pageable);

    @EntityGraph(attributePaths = "account")
    Optional<Customer> findByCustomerId(Integer customerId);

    @EntityGraph(attributePaths = "account")
    Optional<Customer> findByAccountEmailIgnoreCase(String email);

    boolean existsByIdentityCardAndCustomerIdNot(String identityCard, Integer customerId);
    boolean existsByLicenceNumberAndCustomerIdNot(String licenceNumber, Integer customerId);
    boolean existsByIdentityCard(String identityCard);
    boolean existsByLicenceNumber(String licenceNumber);
}
