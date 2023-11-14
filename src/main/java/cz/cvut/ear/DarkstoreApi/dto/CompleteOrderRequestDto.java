package cz.cvut.ear.DarkstoreApi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import lombok.Data;

import java.util.List;

@Data
public class CompleteOrderRequestDto {

    @JsonProperty("complete_info")
    @Valid
    private List<CompleteOrder> completeOrders;
}
