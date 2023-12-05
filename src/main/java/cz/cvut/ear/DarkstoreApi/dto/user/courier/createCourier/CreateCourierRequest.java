package cz.cvut.ear.DarkstoreApi.dto.user.courier.createCourier;

import jakarta.validation.Valid;
import lombok.Data;

import java.util.List;

@Data
public class CreateCourierRequest {
    @Valid
    private List<CreateCourierDto> couriers;
}
