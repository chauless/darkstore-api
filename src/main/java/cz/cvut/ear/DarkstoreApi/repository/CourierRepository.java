package cz.cvut.ear.DarkstoreApi.repository;

import cz.cvut.ear.DarkstoreApi.dto.CourierSummaryDto;
import cz.cvut.ear.DarkstoreApi.model.courier.Courier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourierRepository extends JpaRepository<Courier, Long>{
    @Query("SELECT c FROM Courier c" +
            " ORDER BY CASE c.type WHEN cz.cvut.ear.DarkstoreApi.model.courier.CourierType.FOOT THEN 1" +
            " WHEN cz.cvut.ear.DarkstoreApi.model.courier.CourierType.BIKE THEN 2" +
            " WHEN cz.cvut.ear.DarkstoreApi.model.courier.CourierType.AUTO THEN 3" +
            " END")
    List<Courier> findAllSortedByType();

    @Query(nativeQuery = true, name = "Courier.findSummary")
    List<CourierSummaryDto> findCourierSummary();
}
