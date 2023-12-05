package cz.cvut.ear.DarkstoreApi.dto.order.createOrder;

import jakarta.validation.Valid;
import lombok.Data;

import java.util.List;

@Data
public class CreateOrderRequest {

    @Valid
    private List<CreateOrderDto> orders;
}
