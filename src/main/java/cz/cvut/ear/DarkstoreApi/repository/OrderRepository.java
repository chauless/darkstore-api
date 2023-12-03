package cz.cvut.ear.DarkstoreApi.repository;

import cz.cvut.ear.DarkstoreApi.model.courier.Courier;
import cz.cvut.ear.DarkstoreApi.model.courier.WorkingHour;
import cz.cvut.ear.DarkstoreApi.model.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findOrdersByOrderGroupCourierEmail(String email);
    List<Order> findByOrderGroupCourierAndCompleteTimeBetween(Courier courier, LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT o FROM Order o " +
            " WHERE o.status = 'CREATED'" +
            " AND o.region IN :regions" +
            " AND o.weight <= :maxWeight" +
            " AND EXISTS (" +
            "     SELECT 1 FROM WorkingHour wh" +
            "     WHERE wh IN :workingHours" +
            "     AND o.deliveryHour.start >= wh.start" +
            "     AND o.deliveryHour.finish <= wh.finish" +
            " )"
    )
    List<Order> findPotentialOrders(@Param("regions") List<Integer> regions,
                                    @Param("maxWeight") int maxWeight,
                                    @Param("workingHours") List<WorkingHour> workingHours);

}
