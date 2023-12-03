package cz.cvut.ear.DarkstoreApi.controller;

import cz.cvut.ear.DarkstoreApi.dto.*;
import cz.cvut.ear.DarkstoreApi.service.CourierService;
import cz.cvut.ear.DarkstoreApi.service.OrderService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/couriers")
@RequiredArgsConstructor
@Validated
public class CourierController {
    private final CourierService courierService;
    private final OrderService orderService;

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @PostMapping
    public ResponseEntity<List<CourierDto>> createCouriers(@RequestBody @Valid CreateCourierRequest createCourierRequest) {
        return ResponseEntity.ok(courierService.createCouriers(createCourierRequest));
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @GetMapping
    public ResponseEntity<List<CourierDto>> getCouriers(@RequestParam(required = false, defaultValue = "1") @PositiveOrZero int limit,
                                                        @RequestParam(required = false, defaultValue = "0") @PositiveOrZero int offset) {
        return ResponseEntity.ok(courierService.getCouriers(limit, offset));
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @GetMapping("/{courier_id}")
    public ResponseEntity<CourierDto> getCourierById(@PathVariable(name = "courier_id") long courierId) {
        return ResponseEntity.ok(courierService.getCourier(courierId));
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @GetMapping("/meta-info/{courier_id}")
    public ResponseEntity<CourierMetaInfo> getCourierMetaInfoById(@PathVariable(name = "courier_id") long courierId,
                                                                  @RequestBody CourierMetaInfoRequestDto courierMetaInfoRequestDto) {
        return ResponseEntity.ok(courierService.getCourierMetaInfo(courierId, courierMetaInfoRequestDto));
    }

    @PreAuthorize("hasRole('ROLE_COURIER')")
    @GetMapping("/meta-info")
    public ResponseEntity<CourierMetaInfo> getCourierMetaInfo(@RequestBody CourierMetaInfoRequestDto courierMetaInfoRequestDto,
                                                              Authentication authentication) {
        return ResponseEntity.ok(courierService.getCourierMetaInfo(authentication, courierMetaInfoRequestDto));
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @GetMapping("/assignments")
    public ResponseEntity<List<OrderGroupDto>> getAssignments(@RequestParam(required = false, defaultValue = "1") @PositiveOrZero int limit,
                                                              @RequestParam(required = false, defaultValue = "0") @PositiveOrZero int offset) {
        return ResponseEntity.ok(orderService.getOrderGroups(limit, offset));
    }
}
