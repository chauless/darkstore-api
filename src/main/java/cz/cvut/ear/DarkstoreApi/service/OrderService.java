package cz.cvut.ear.DarkstoreApi.service;

import cz.cvut.ear.DarkstoreApi.dto.CreateOrderRequest;
import cz.cvut.ear.DarkstoreApi.dto.OrderDto;

import java.util.List;

public interface OrderService {
    List<OrderDto> createOrders(CreateOrderRequest createOrderRequest);

    List<OrderDto> getOrders(int limit, int offset);

    OrderDto getOrder(long orderId);
}
