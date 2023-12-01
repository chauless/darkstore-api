package cz.cvut.ear.DarkstoreApi.model.order;

import cz.cvut.ear.DarkstoreApi.model.courier.Courier;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "weight")
    private float weight;

    @Column(name = "region")
    private int region;

    @Column(name = "cost")
    private int cost;

    @OneToOne(mappedBy = "order", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private DeliveryHour deliveryHour;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private OrderStatus status;

    @Column(name = "complete_time")
    private LocalDateTime completeTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_group_id")
    private OrderGroup orderGroup;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Order order = (Order) o;

        if (Float.compare(weight, order.weight) != 0) return false;
        if (region != order.region) return false;
        if (cost != order.cost) return false;
        if (!id.equals(order.id)) return false;
        return deliveryHour.equals(order.deliveryHour);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + (weight != 0.0f ? Float.floatToIntBits(weight) : 0);
        result = 31 * result + region;
        result = 31 * result + cost;
        result = 31 * result + deliveryHour.hashCode();
        return result;
    }
}
