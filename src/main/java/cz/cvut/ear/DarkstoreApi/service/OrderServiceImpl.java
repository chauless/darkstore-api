package cz.cvut.ear.DarkstoreApi.service;

import cz.cvut.ear.DarkstoreApi.dto.CreateOrderDto;
import cz.cvut.ear.DarkstoreApi.dto.CreateOrderRequest;
import cz.cvut.ear.DarkstoreApi.dto.OrderDto;
import cz.cvut.ear.DarkstoreApi.model.order.Order;
import cz.cvut.ear.DarkstoreApi.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<OrderDto> createOrders(CreateOrderRequest createOrderRequest) {
        List<CreateOrderDto> createOrderDtos = createOrderRequest.getOrders();

        List<Order> savedOrders = createOrderDtos.stream()
                .map(createOrderDto -> modelMapper.map(createOrderDto, Order.class))
                .collect(Collectors.toList());

        savedOrders = orderRepository.saveAll(savedOrders);

        List<OrderDto> orderDtos = savedOrders.stream()
                .map(order -> modelMapper.map(order, OrderDto.class))
                .collect(Collectors.toList());

        return orderDtos;
    }

    @Override
    public List<OrderDto> getOrders(int limit, int offset) {
        return null;
    }

    @Override
    public OrderDto getOrder(long orderId) {
        return null;
    }
}
