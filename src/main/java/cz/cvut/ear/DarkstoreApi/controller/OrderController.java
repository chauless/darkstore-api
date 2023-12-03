package cz.cvut.ear.DarkstoreApi.controller;

import cz.cvut.ear.DarkstoreApi.dto.CompleteOrderRequestDto;
import cz.cvut.ear.DarkstoreApi.dto.CreateOrderRequest;
import cz.cvut.ear.DarkstoreApi.dto.OrderDto;
import cz.cvut.ear.DarkstoreApi.dto.OrderGroupDto;
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
@RequestMapping("/orders")
@RequiredArgsConstructor
@Validated
public class OrderController {

    private final OrderService orderService;

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @PostMapping
    public ResponseEntity<List<OrderDto>> createOrders(@RequestBody @Valid CreateOrderRequest createOrderRequest) {
        return ResponseEntity.ok(orderService.createOrders(createOrderRequest));
    }

    @GetMapping
    public ResponseEntity<List<OrderDto>> getOrders(@RequestParam(required = false, defaultValue = "1") @PositiveOrZero int limit,
                                                    @RequestParam(required = false, defaultValue = "0") @PositiveOrZero int offset,
                                                    Authentication authentication) {
        return ResponseEntity.ok(orderService.getOrders(authentication, limit, offset));
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @GetMapping("/{order_id}")
    public ResponseEntity<OrderDto> getOrderById(@PathVariable(name = "order_id") long orderId) {
        return ResponseEntity.ok(orderService.getOrder(orderId));
    }

    @PreAuthorize("hasRole('ROLE_COURIER')")
    @PostMapping("/complete")
    public ResponseEntity<List<OrderDto>> completeOrders(@RequestBody @Valid CompleteOrderRequestDto completeOrderRequestDto) {
        return ResponseEntity.ok(orderService.completeOrders(completeOrderRequestDto));
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @PostMapping("/assign")
    public ResponseEntity<List<OrderGroupDto>> assignOrders() {
        orderService.deleteAllOrderGroups();
        return ResponseEntity.ok(orderService.assignOrders());
    }
}
