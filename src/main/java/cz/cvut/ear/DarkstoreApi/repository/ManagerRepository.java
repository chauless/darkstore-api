package cz.cvut.ear.DarkstoreApi.repository;

import cz.cvut.ear.DarkstoreApi.model.Manager;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ManagerRepository extends JpaRepository<Manager, Integer> {
}
