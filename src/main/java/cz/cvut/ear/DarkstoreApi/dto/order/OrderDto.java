package cz.cvut.ear.DarkstoreApi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class OrderDto {

    @JsonProperty("order_id")
    private Long orderId;

    private float weight;

    private int region;

    @JsonProperty("delivery_hour")
    private String deliveryHour;

    private int cost;

    private String completeTime;
}
