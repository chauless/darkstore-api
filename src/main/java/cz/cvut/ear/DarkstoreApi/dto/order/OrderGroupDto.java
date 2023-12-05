package cz.cvut.ear.DarkstoreApi.dto.order;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class OrderGroupDto {
    private long id;

    @JsonProperty("courier_id")
    private long courierId;

    private List<OrderDto> orders;
}
