package cz.cvut.ear.DarkstoreApi.model.courier;

import cz.cvut.ear.DarkstoreApi.dto.CourierSummaryDto;
import cz.cvut.ear.DarkstoreApi.model.User;
import cz.cvut.ear.DarkstoreApi.model.order.OrderGroup;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "couriers")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@NamedNativeQuery(
        name = "Courier.findSummary",
        query = "SELECT id, type FROM Courier",
        resultSetMapping = "CourierSummaryDtoMapping"
)
@SqlResultSetMapping(
        name = "CourierSummaryDtoMapping",
        classes = @ConstructorResult(
                targetClass = CourierSummaryDto.class,
                columns = {
                        @ColumnResult(name = "id", type = Long.class),
                        @ColumnResult(name = "type", type = String.class)
                }
        )
)
public class Courier extends User {

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private CourierType type;

    @OneToMany(mappedBy = "courier", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<WorkingHour> workingHours;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "couriers_regions",
            joinColumns = @JoinColumn(name = "courier_id"),
            inverseJoinColumns = @JoinColumn(name = "region_id")
    )
    private List<CourierRegion> regions;

    @OneToMany(mappedBy = "courier", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderGroup> orderGroups = new ArrayList<>();
}
