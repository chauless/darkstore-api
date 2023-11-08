package cz.cvut.ear.DarkstoreApi.service;

import cz.cvut.ear.DarkstoreApi.dto.CreateOrderRequest;
import cz.cvut.ear.DarkstoreApi.dto.OrderDto;
import cz.cvut.ear.DarkstoreApi.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    @Override
    public List<OrderDto> createOrders(CreateOrderRequest createOrderRequest) {
        return null;
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
