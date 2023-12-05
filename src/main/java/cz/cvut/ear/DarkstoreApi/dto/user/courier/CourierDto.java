package cz.cvut.ear.DarkstoreApi.dto.user.courier;

import com.fasterxml.jackson.annotation.JsonProperty;
import cz.cvut.ear.DarkstoreApi.model.courier.CourierType;
import lombok.Data;

import java.util.List;

@Data
public class CourierDto {
    private Long courierId;

    private String email;

    private CourierType type;

    private List<Integer> regions;

    @JsonProperty("working_hours")
    private List<String> workingHours;
}
