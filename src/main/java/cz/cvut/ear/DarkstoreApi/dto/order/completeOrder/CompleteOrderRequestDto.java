package cz.cvut.ear.DarkstoreApi.dto.order.completeOrder;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import lombok.Data;

import java.util.List;

@Data
public class CompleteOrderRequestDto {
    @JsonProperty("orders")
    @Valid
    private List<CompleteOrderDto> completeOrders;
}
