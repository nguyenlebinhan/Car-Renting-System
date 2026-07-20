package org.ats.entities.user;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "Account", uniqueConstraints = {
        @UniqueConstraint(columnNames = "Email"),
        @UniqueConstraint(columnNames = "AccountName")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AccountID", nullable = false)
    Integer accountId;

    @Column(name = "AccountName", nullable = false, unique = true, columnDefinition = "NVARCHAR(100)")
    String accountName;

    @Column(name = "Email", nullable = false, unique = true, columnDefinition = "VARCHAR(200)")
    String email;

    @Column(name = "Password", nullable = false, columnDefinition = "VARCHAR(200)")
    String password;

    @Column(name = "Role", nullable = false, columnDefinition = "NVARCHAR(10)")
    String role;

    @OneToOne(mappedBy = "account")
    @ToString.Exclude
    Customer customer;
}
