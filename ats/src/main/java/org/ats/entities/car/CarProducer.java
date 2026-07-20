package org.ats.entities.car;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "CarProducer")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CarProducer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ProducerID", nullable = false)
    Integer producerId;

    @Column(name = "ProducerName", nullable = false, unique = true, columnDefinition = "NVARCHAR(100)")
    String producerName;

    @Column(name = "Address", nullable = false, columnDefinition = "NVARCHAR(200)")
    String address;

    @Column(name = "Country", nullable = false, columnDefinition = "NVARCHAR(100)")
    String country;

    @OneToMany(mappedBy = "carProducer")
    @Builder.Default
    @ToString.Exclude
    List<Car> cars = new ArrayList<>();
}
