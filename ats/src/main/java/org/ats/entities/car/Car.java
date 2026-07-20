package org.ats.entities.car;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Car")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CarID", nullable = false)
    Integer carId;

    @Column(name = "CarName", nullable = false, columnDefinition = "NVARCHAR(200)")
    String carName;

    @Column(name = "CarModelYear", nullable = false)
    int carModelYear;

    @Column(name = "Color", nullable = false, columnDefinition = "NVARCHAR(50)")
    String color;

    @Column(name = "Capacity", nullable = false)
    int capacity;

    @Column(name = "Description", nullable = false, columnDefinition = "NVARCHAR(1000)")
    String description;

    @Column(name = "ImportDate", nullable = false)
    LocalDate importDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ProducerID", nullable = false)
    @ToString.Exclude
    CarProducer carProducer;

    @Column(name = "RentPrice", nullable = false, columnDefinition = "DECIMAL(10)")
    BigDecimal rentPrice;

    @Column(name = "Status", nullable = false, columnDefinition = "NVARCHAR(10)")
    String status;

    @OneToMany(mappedBy = "car")
    @Builder.Default
    @ToString.Exclude
    List<CarRental> carRentals = new ArrayList<>();
}
