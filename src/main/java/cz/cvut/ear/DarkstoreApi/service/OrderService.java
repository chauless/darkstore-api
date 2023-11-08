package cz.cvut.ear.DarkstoreApi.service;

import cz.cvut.ear.DarkstoreApi.dto.CreateOrderRequest;
import cz.cvut.ear.DarkstoreApi.dto.OrderDto;

import java.util.List;

public interface OrderService {

    /**
     * Accepts a list of order data in JSON format and creates orders. Order characteristics are displayed - weight, region, delivery time and price.
     * Delivery time is a string in the format HH:MM-HH:MM, where HH - hours (from 0 to 23) and MM - minutes (from 0 to 59).
     *
     * @param createOrderRequest The request containing a list of order data in JSON format.
     * @return A list of OrderDto objects representing the created orders.
     */
    List<OrderDto> createOrders(CreateOrderRequest createOrderRequest);

    List<OrderDto> getOrders(int limit, int offset);

    OrderDto getOrder(long orderId);
}
