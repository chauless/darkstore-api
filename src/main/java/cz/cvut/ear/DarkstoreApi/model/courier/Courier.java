package cz.cvut.ear.DarkstoreApi.model.courier;

import cz.cvut.ear.DarkstoreApi.model.User;
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
public class Courier extends User {

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private CourierType type;

    @OneToMany(mappedBy = "courier", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Order> orders;

    @OneToMany(mappedBy = "courier", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<WorkingHour> workingHours;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "couriers_regions",
            joinColumns = @JoinColumn(name = "courier_id"),
            inverseJoinColumns = @JoinColumn(name = "region_id"))
    private List<CourierRegion> regions;
}
