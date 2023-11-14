package cz.cvut.ear.DarkstoreApi.service;

import cz.cvut.ear.DarkstoreApi.dto.*;
import cz.cvut.ear.DarkstoreApi.model.order.Order;
import cz.cvut.ear.DarkstoreApi.model.order.OrderStatus;
import cz.cvut.ear.DarkstoreApi.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ModelMapper modelMapper;

    @Override
    public ResponseEntity<List<OrderDto>> createOrders(CreateOrderRequest createOrderRequest) {
        List<CreateOrderDto> createOrderDtos = createOrderRequest.getOrders();

        List<Order> savedOrders = createOrderDtos.stream()
                .map(createOrderDto -> modelMapper.map(createOrderDto, Order.class))
                .collect(Collectors.toList());

        savedOrders = orderRepository.saveAll(savedOrders);

        if (savedOrders.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        List<OrderDto> orderDtos = savedOrders.stream()
                .map(order -> modelMapper.map(order, OrderDto.class))
                .collect(Collectors.toList());

        return ResponseEntity.ok(orderDtos);
    }

    @Override
    public ResponseEntity<List<OrderDto>> getOrders(int limit, int offset) {
        List<OrderDto> orderDtos = orderRepository.findAll().stream()
                .skip(offset)
                .limit(limit)
                .map(order -> modelMapper.map(order, OrderDto.class))
                .toList();

        if (orderDtos.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        return ResponseEntity.ok(orderDtos);
    }

    @Override
    public ResponseEntity<OrderDto> getOrder(long orderId) {
        Optional<Order> order = orderRepository.findById(orderId);

        if (order.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        OrderDto orderDto = modelMapper.map(order.get(), OrderDto.class);
        return ResponseEntity.ok(orderDto);
    }

    @Override
    public ResponseEntity<List<OrderDto>> completeOrders(CompleteOrderRequestDto completeOrderRequestDto) {
        List<Order> completedOrders = new ArrayList<>();
        List<CompleteOrder> completeOrders = completeOrderRequestDto.getCompleteOrders();

        for(CompleteOrder completeOrder : completeOrders) {
            Optional<Order> order = orderRepository.findById(completeOrder.getOrderId());

            if(order.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            if (!isOrderValid(order.get(), completeOrder)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }

            order.get().setCompleteTime(LocalDateTime.parse(completeOrder.getCompleteTime(), DateTimeFormatter.ISO_DATE_TIME));
            order.get().setStatus(OrderStatus.FINISHED);
            completedOrders.add(order.get());
        }

        orderRepository.saveAll(completedOrders);

        List<OrderDto> orderDtos = completedOrders.stream()
                .map(order -> modelMapper.map(order, OrderDto.class))
                .collect(Collectors.toList());

        return ResponseEntity.ok(orderDtos);
    }

    private boolean isOrderValid(Order order, CompleteOrder completeOrder) {
        if (order.getStatus() != OrderStatus.ASSIGNED || order.getCourier().getId() != completeOrder.getCourierId()) {
            return false;
        }
        return true;
    }
}
