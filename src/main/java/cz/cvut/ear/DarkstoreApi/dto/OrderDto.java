package cz.cvut.ear.DarkstoreApi.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.List;

@Data
public class OrderDto {

    private Long orderId;

    @Positive
    private float weight;

    @Positive
    private int region;

    @Valid
    private List<@Pattern(regexp = "^(0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]-(0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$") String> deliveryHours;

    @Positive
    private int cost;

    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}(\\.\\d+)?(Z|[+-]\\d{2}:\\d{2})$")
    private String completedTime;
}
