package org.ats.entities.car;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "Review")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    Integer reviewId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CarRenID", nullable = false)
    @ToString.Exclude
    CarRental carRental;

    @Column(name = "ReviewStar", nullable = false)
    int reviewStar;

    @Column(name = "Comment", nullable = false, columnDefinition = "NVARCHAR(500)")
    String comment;

}
