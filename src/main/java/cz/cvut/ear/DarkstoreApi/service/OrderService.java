package cz.cvut.ear.DarkstoreApi.service;

import cz.cvut.ear.DarkstoreApi.dto.CreateOrderRequest;
import cz.cvut.ear.DarkstoreApi.dto.OrderDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface OrderService {

    /**
     * Accepts a list of order data in JSON format and creates orders. Order characteristics are displayed - weight, region, delivery time and price.
     * Delivery time is a string in the format HH:MM-HH:MM, where HH - hours (from 0 to 23) and MM - minutes (from 0 to 59).
     *
     * @param createOrderRequest The request containing a list of order data in JSON format.
     * @return A list of OrderDto objects representing the created orders.
     */
    ResponseEntity<List<OrderDto>> createOrders(CreateOrderRequest createOrderRequest);

    /**
     * Retrieves a list of orders with optional pagination.
     * If offset or limit are not passed, the default is offset = 0, limit = 1.
     *
     * @param limit The maximum number of orders to retrieve.
     * @param offset The offset for pagination.
     * @return A list of OrderDto objects representing the retrieved orders.
     */
    ResponseEntity<List<OrderDto>> getOrders(int limit, int offset);

    /**
     * Retrieves details of a specific order by ID.
     *
     * @param orderId ID of the order to retrieve.
     * @return An OrderDto object representing the retrieved order.
     */
    ResponseEntity<OrderDto> getOrder(long orderId);

    ResponseEntity<List<OrderDto>> completeOrders();
}
