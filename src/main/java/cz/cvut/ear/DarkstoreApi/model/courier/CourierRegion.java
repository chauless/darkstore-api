package cz.cvut.ear.DarkstoreApi.model.courier;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "courier_regions")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CourierRegion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "region")
    private int region;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "courier_id")
    private Courier courier;
}
