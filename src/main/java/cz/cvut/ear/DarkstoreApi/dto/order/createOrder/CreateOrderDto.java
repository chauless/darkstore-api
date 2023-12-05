package cz.cvut.ear.DarkstoreApi.dto.order.createOrder;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CreateOrderDto {

    @Positive
    private float weight;

    @Positive
    private int region;

    @JsonProperty("delivery_hour")
    @Pattern(regexp = "^(0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]-(0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$",
            message = "Provided invalid delivery_hours.")
    @Valid
    private String deliveryHour;

    @Positive
    private int cost;
}
