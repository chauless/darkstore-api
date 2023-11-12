package cz.cvut.ear.DarkstoreApi.dto;

import cz.cvut.ear.DarkstoreApi.model.courier.CourierType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.List;

@Data
public class CreateCourierDto {

    @NotNull
    private CourierType type;

    @NotEmpty
    private List<Integer> regions;

    @Valid
    private List<@Pattern(regexp = "([0-1]?[0-9]|2[0-3]):[0-5][0-9]-([0-1]?[0-9]|2[0-3]):[0-5][0-9]") String> workingHour;

    @Email
    private String email;

    @NotNull
    private String password;
}
