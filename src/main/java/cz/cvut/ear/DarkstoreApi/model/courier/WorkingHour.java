package cz.cvut.ear.DarkstoreApi.model.courier;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

@Entity
@Table(name = "working_hours")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WorkingHour {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "start")
    private LocalTime start;

    @Column(name = "finish")
    private LocalTime finish;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "courier_id", nullable = false)
    private Courier courier;

    public WorkingHour(LocalTime start, LocalTime finish, Courier courier) {
        this.start = start;
        this.finish = finish;
        this.courier = courier;
    }

    public WorkingHour(LocalTime start, LocalTime finish) {
        this.start = start;
        this.finish = finish;
    }
}