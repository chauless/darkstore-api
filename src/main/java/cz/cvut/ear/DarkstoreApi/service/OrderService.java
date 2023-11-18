package cz.cvut.ear.DarkstoreApi.service;

import cz.cvut.ear.DarkstoreApi.dto.*;
import cz.cvut.ear.DarkstoreApi.model.order.Order;
import cz.cvut.ear.DarkstoreApi.model.order.OrderStatus;
import cz.cvut.ear.DarkstoreApi.repository.OrderRepository;
import cz.cvut.ear.DarkstoreApi.util.mapper.OrderMapper;
import lombok.RequiredArgsConstructor;
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
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    public ResponseEntity<List<OrderDto>> createOrders(CreateOrderRequest createOrderRequest) {
        List<CreateOrderDto> createOrderDtos = createOrderRequest.getOrders();

        List<Order> savedOrders = orderMapper.createOrderDtoToOrder(createOrderDtos);

        savedOrders.forEach(order -> order.setStatus(OrderStatus.CREATED));

        savedOrders = orderRepository.saveAll(savedOrders);

        if (savedOrders.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        List<OrderDto> orderDtos = orderMapper.orderToOrderDto(savedOrders);

        return ResponseEntity.ok(orderDtos);
    }

    public ResponseEntity<List<OrderDto>> getOrders(int limit, int offset) {
        List<OrderDto> orderDtos = orderMapper.orderToOrderDto(orderRepository.findAll().stream()
                .skip(offset)
                .limit(limit)
                .toList());

        if (orderDtos.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        return ResponseEntity.ok(orderDtos);
    }

    public ResponseEntity<OrderDto> getOrder(long orderId) {
        Optional<Order> order = orderRepository.findById(orderId);

        if (order.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        OrderDto orderDto = orderMapper.orderToOrderDto(order.get());
        return ResponseEntity.ok(orderDto);
    }

    public ResponseEntity<List<OrderDto>> completeOrders(CompleteOrderRequestDto completeOrderRequestDto) {
        List<Order> completedOrders = new ArrayList<>();
        List<CompleteOrder> completeOrders = completeOrderRequestDto.getCompleteOrders();

//        List<Order> ordersToComplete = completeOrders.stream()
//                .map(completeOrder -> orderRepository.findById(completeOrder.getOrderId()))
//                .filter(Optional::isPresent)
//                .map(Optional::get)
//                .toList();



        for(CompleteOrder completeOrder : completeOrders) {
            Optional<Order> order = orderRepository.findById(completeOrder.getOrderId());

            if(order.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

//            if (!isOrderValid(order.get(), completeOrder)) {
//                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
//            }

            order.get().setCompleteTime(LocalDateTime.parse(completeOrder.getCompleteTime(), DateTimeFormatter.ISO_DATE_TIME));
            order.get().setStatus(OrderStatus.FINISHED);
            completedOrders.add(order.get());
        }

        orderRepository.saveAll(completedOrders);

        List<OrderDto> orderDtos = completedOrders.stream()
                .map(orderMapper::orderToOrderDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(orderDtos);
    }

    private boolean isOrderValid(Order order, CompleteOrder completeOrder) {
        if (order.getStatus() != OrderStatus.ASSIGNED/** || order.getCourier().getId() != completeOrder.getCourierId()*/) {
            return false;
        }
        return true;
    }
}
