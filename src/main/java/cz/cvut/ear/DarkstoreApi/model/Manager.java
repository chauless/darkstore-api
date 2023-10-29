package cz.cvut.ear.DarkstoreApi.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "managers")
@Getter
@Setter
@AllArgsConstructor
public class Manager extends User {
}
