package cz.cvut.ear.DarkstoreApi.service;

import cz.cvut.ear.DarkstoreApi.dto.*;
import cz.cvut.ear.DarkstoreApi.exception.OrderNotFoundException;
import cz.cvut.ear.DarkstoreApi.exception.OrderNotReadyForCompletionException;
import cz.cvut.ear.DarkstoreApi.model.order.Order;
import cz.cvut.ear.DarkstoreApi.model.order.OrderStatus;
import cz.cvut.ear.DarkstoreApi.repository.OrderRepository;
import cz.cvut.ear.DarkstoreApi.util.mapper.OrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    @Transactional
    public List<OrderDto> createOrders(CreateOrderRequest createOrderRequest) {
        List<CreateOrderDto> createOrderDtos = createOrderRequest.getOrders();

        List<Order> savedOrders = orderMapper.createOrderDtoToOrder(createOrderDtos);

        savedOrders.forEach(order -> order.setStatus(OrderStatus.CREATED));

        savedOrders = orderRepository.saveAll(savedOrders);

        return orderMapper.orderToOrderDto(savedOrders);
    }

    public List<OrderDto> getOrders(int limit, int offset) {
        return orderMapper.orderToOrderDto(orderRepository.findAll().stream().skip(offset).limit(limit).toList());
    }

    public OrderDto getOrder(long orderId) {
        return orderMapper.orderToOrderDto(orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order with id " + orderId + " not found.")));
    }

    @Transactional
    public List<OrderDto> completeOrders(CompleteOrderRequestDto completeOrderRequestDto) {
        List<CompleteOrderDto> completeOrdersDto = completeOrderRequestDto.getCompleteOrders();

        List<Order> completedOrders = completeOrdersDto.stream().map(completeOrder -> {
            Order order = orderRepository.findById(completeOrder.getOrderId()).orElseThrow(() ->
                    new OrderNotFoundException("Order with id " + completeOrder.getOrderId() + " not found."));

            isOrderValid(order);

            order.setCompleteTime(LocalDateTime.parse(completeOrder.getCompleteTime(), DateTimeFormatter.ISO_DATE_TIME));
            order.setStatus(OrderStatus.FINISHED);
            return order;
        }).toList();

        return orderRepository.saveAll(completedOrders).stream()
                .map(orderMapper::orderToOrderDto).collect(Collectors.toList());
    }

    private void isOrderValid(Order order) {
        if (order.getStatus() != OrderStatus.ASSIGNED) {
            throw new OrderNotReadyForCompletionException("Order must have status assigned to be completed.");
        }

        if (order.getCourier() == null) {
            throw new OrderNotReadyForCompletionException("Order must have courier assigned to be completed.");
        }
    }
}
