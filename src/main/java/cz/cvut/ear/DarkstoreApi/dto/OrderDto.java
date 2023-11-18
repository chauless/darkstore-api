package cz.cvut.ear.DarkstoreApi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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
