package cz.cvut.ear.DarkstoreApi.model.courier;

import cz.cvut.ear.DarkstoreApi.model.order.Order;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "couriers")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Courier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private CourierType type;

    @OneToMany(mappedBy = "courier", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Order> orders;

    @OneToMany(mappedBy = "courier", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<WorkingHour> workingHour;

    @OneToMany(mappedBy = "courier", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<CourierRegion> region;
}
