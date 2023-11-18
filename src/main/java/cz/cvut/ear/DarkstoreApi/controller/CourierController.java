package cz.cvut.ear.DarkstoreApi.controller;

import cz.cvut.ear.DarkstoreApi.dto.CourierDto;
import cz.cvut.ear.DarkstoreApi.dto.CreateCourierRequest;
import cz.cvut.ear.DarkstoreApi.service.CourierService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/couriers")
@RequiredArgsConstructor
@Validated
public class CourierController {
    private final CourierService courierService;

    @PostMapping
    public ResponseEntity<List<CourierDto>> createCouriers(@RequestBody @Valid CreateCourierRequest createCourierRequest) {
        return ResponseEntity.ok(courierService.createCouriers(createCourierRequest));
    }

    @GetMapping
    public ResponseEntity<List<CourierDto>> getCouriers(@RequestParam(required = false, defaultValue = "1") @PositiveOrZero int limit,
                                                        @RequestParam(required = false, defaultValue = "0") @PositiveOrZero int offset) {
        return ResponseEntity.ok(courierService.getCouriers(limit, offset));
    }

    @GetMapping("/{courier_id}")
    public ResponseEntity<CourierDto> getCourierById(@PathVariable(name = "courier_id") long courierId) {
        return ResponseEntity.ok(courierService.getCourier(courierId));
    }
}
