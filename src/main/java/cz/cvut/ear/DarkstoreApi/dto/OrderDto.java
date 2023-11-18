package cz.cvut.ear.DarkstoreApi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.List;

@Data
public class OrderDto {

    private Long orderId;

    private float weight;

    private int region;

    @JsonProperty("delivery_hours")
    private List<String> deliveryHours;

    private int cost;

    private String completeTime;
}
