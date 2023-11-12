package cz.cvut.ear.DarkstoreApi.repository;

import cz.cvut.ear.DarkstoreApi.model.courier.Courier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourierRepository extends JpaRepository<Courier, Long>{

}
