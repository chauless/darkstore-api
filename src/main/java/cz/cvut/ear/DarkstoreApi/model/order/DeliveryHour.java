package cz.cvut.ear.DarkstoreApi.model.order;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

@Entity
@Table(name = "delivery_hours")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryHour {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "start")
    private LocalTime start;

    @Column(name = "finish")
    private LocalTime finish;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    public DeliveryHour(String start, String finish) {
        this.start = LocalTime.parse(start);
        this.finish = LocalTime.parse(finish);
    }
}
