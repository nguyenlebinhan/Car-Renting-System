package org.ats.entities.car;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.ats.entities.user.Customer;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "CarRental")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CarRental {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CarRenID", nullable = false)
    Integer carRentalId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CustomerID", nullable = false)
    @ToString.Exclude
    Customer customer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CarID", nullable = false)
    @ToString.Exclude
    Car car;

    @Column(name = "PickUpDate", nullable = false)
    LocalDate pickUpDate;

    @Column(name = "ReturnDate", nullable = false)
    LocalDate returnDate;

    @Column(name = "RentPrice", nullable = false,columnDefinition = "DECIMAL(10)")
    BigDecimal rentPrice;

    @Column(name = "Status", nullable = false, columnDefinition = "NVARCHAR(10)")
    String status;



    @OneToMany(mappedBy = "carRental", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @ToString.Exclude
    List<Review> reviews = new ArrayList<>();
}
