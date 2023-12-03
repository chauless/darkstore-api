package cz.cvut.ear.DarkstoreApi.service;

import cz.cvut.ear.DarkstoreApi.dto.*;
import cz.cvut.ear.DarkstoreApi.model.order.Order;
import cz.cvut.ear.DarkstoreApi.repository.CourierRepository;
import cz.cvut.ear.DarkstoreApi.repository.OrderGroupRepository;
import cz.cvut.ear.DarkstoreApi.repository.OrderRepository;
import cz.cvut.ear.DarkstoreApi.util.mapper.OrderGroupMapper;
import cz.cvut.ear.DarkstoreApi.util.mapper.OrderMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@SpringBootTest
class OrderServiceTest {

    @MockBean
    private OrderRepository orderRepository;

    @MockBean
    private CourierRepository courierRepository;

    @MockBean
    private OrderMapper orderMapper;

    @MockBean
    private OrderGroupMapper orderGroupMapper;

    @MockBean
    private OrderGroupRepository orderGroupRepository;

    @Test
    @Transactional
    void testCreateOrders() {
        OrderService orderService = new OrderService(orderRepository, courierRepository, orderMapper, orderGroupMapper, orderGroupRepository);

        CreateOrderRequest createOrderRequest = new CreateOrderRequest();
        createOrderRequest.setOrders(Arrays.asList(new CreateOrderDto(), new CreateOrderDto()));

        List<CreateOrderDto> createOrderDtos = Arrays.asList(new CreateOrderDto(), new CreateOrderDto());
        List<Order> mockSavedOrders = Arrays.asList(new Order(), new Order());
        List<OrderDto> expectedOrderDtos = Arrays.asList(new OrderDto(), new OrderDto());

        when(orderMapper.createOrderDtoToOrder(createOrderDtos)).thenReturn(mockSavedOrders);
        when(orderRepository.saveAll(mockSavedOrders)).thenReturn(mockSavedOrders);
        when(orderMapper.orderToOrderDto(mockSavedOrders)).thenReturn(expectedOrderDtos);

        List<OrderDto> resultOrderDtos = orderService.createOrders(createOrderRequest);

        assertNotNull(resultOrderDtos);
        assertEquals(expectedOrderDtos.size(), resultOrderDtos.size());

        ArgumentCaptor<List<Order>> orderListCaptor = ArgumentCaptor.forClass(List.class);
        verify(orderRepository).saveAll(orderListCaptor.capture());
        verify(orderMapper).orderToOrderDto(orderListCaptor.getValue());
    }

    @Test
    void testGetOrders() {
        OrderService orderService = new OrderService(orderRepository, courierRepository, orderMapper, orderGroupMapper, orderGroupRepository);

        int limit = 10;
        int offset = 0;
        List<Order> mockOrders = Arrays.asList(new Order(), new Order());
        List<OrderDto> expectedOrderDtos = Arrays.asList(new OrderDto(), new OrderDto());

        when(orderRepository.findAll()).thenReturn(mockOrders);
        when(orderMapper.orderToOrderDto(anyList())).thenReturn(expectedOrderDtos);

        List<OrderDto> resultOrderDtos = orderService.getOrders(limit, offset);

        assertNotNull(resultOrderDtos);
        assertEquals(expectedOrderDtos.size(), resultOrderDtos.size());

        ArgumentCaptor<List<Order>> orderListCaptor = ArgumentCaptor.forClass(List.class);
        verify(orderRepository).findAll();
        verify(orderMapper).orderToOrderDto(orderListCaptor.capture());
    }

    @Test
    void testGetOrder() {
        OrderService orderService = new OrderService(orderRepository, courierRepository, orderMapper, orderGroupMapper, orderGroupRepository);

        long orderId = 1L;
        Order mockOrder = new Order();
        OrderDto expectedOrderDto = new OrderDto();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(mockOrder));
        when(orderMapper.orderToOrderDto(mockOrder)).thenReturn(expectedOrderDto);

        OrderDto resultOrderDto = orderService.getOrder(orderId);

        assertNotNull(resultOrderDto);

        verify(orderRepository).findById(orderId);
        verify(orderMapper).orderToOrderDto(mockOrder);
    }
}