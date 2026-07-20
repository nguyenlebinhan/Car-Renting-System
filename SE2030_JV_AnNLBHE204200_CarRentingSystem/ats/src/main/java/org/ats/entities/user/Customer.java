package org.ats.entities.user;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.ats.entities.car.CarRental;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Customer")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CustomerID", nullable = false)
    Integer customerId;

    @Column(name = "FullName", nullable = false, columnDefinition = "NVARCHAR(200)")
    String fullName;

    @Column(name = "Mobile", nullable = false, columnDefinition = "VARCHAR(15)")
    String mobile;

    @Column(name = "Birthday", nullable = false)
    LocalDate birthday;

    @Column(name = "IdentityCard", nullable = false, unique = true, columnDefinition = "VARCHAR(20)")
    String identityCard;

    @Column(name = "LicenceNumber", nullable = false, unique = true, columnDefinition = "VARCHAR(20)")
    String licenceNumber;

    @Column(name = "LicenceDate", nullable = false)
    LocalDate licenceDate;

    @OneToOne(fetch = FetchType.LAZY, optional = false,cascade = CascadeType.ALL,orphanRemoval = true)
    @JoinColumn(name = "AccountID", nullable = false, unique = true)
    @ToString.Exclude
    Account account;

    @OneToMany(mappedBy = "customer")
    @Builder.Default
    @ToString.Exclude
    List<CarRental> carRentals = new ArrayList<>();
}
