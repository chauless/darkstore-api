package cz.cvut.ear.DarkstoreApi.model.order;

import cz.cvut.ear.DarkstoreApi.model.courier.Courier;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "order_group")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderGroup {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    Courier courier;

    @OneToMany(mappedBy = "orderGroup", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
    private List<Order> orders = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OrderGroup that = (OrderGroup) o;

        return id == that.id;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

    @PreRemove
    private void removeOrderGroupFromOrders() {
        for (Order order : orders) {
            order.setOrderGroup(null);
            if (order.getStatus() == OrderStatus.ASSIGNED) {
                order.setStatus(OrderStatus.CREATED);
            }
        }
    }
}
