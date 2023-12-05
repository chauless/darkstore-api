package cz.cvut.ear.DarkstoreApi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import cz.cvut.ear.DarkstoreApi.model.courier.CourierType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.List;

@Data
public class CreateCourierDto {

    @NotNull(message = "Provided invalid type.")
    private CourierType type;

    @NotNull(message = "Provided invalid regions.")
    private List<Integer> regions;

    @JsonProperty("working_hours")
    @Valid
    private List<@Pattern(regexp = "([0-1]?[0-9]|2[0-3]):[0-5][0-9]-([0-1]?[0-9]|2[0-3]):[0-5][0-9]",
            message = "Provided invalid working_hours.") String> workingHours;

    @Email
    private String email;

    @NotBlank
    private String password;
}
