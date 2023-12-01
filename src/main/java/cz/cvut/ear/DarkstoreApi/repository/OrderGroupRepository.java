package cz.cvut.ear.DarkstoreApi.repository;

import cz.cvut.ear.DarkstoreApi.model.order.OrderGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderGroupRepository extends JpaRepository<OrderGroup, Long> {

}
