package cz.cvut.ear.DarkstoreApi.model.courier;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

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

    @ManyToMany(mappedBy = "regions", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Courier> courier;

    public CourierRegion(Integer region) {
        this.region = region;
    }
}
