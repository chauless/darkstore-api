package cz.cvut.ear.DarkstoreApi.service;

import cz.cvut.ear.DarkstoreApi.dto.order.completeOrder.CompleteOrderDto;
import cz.cvut.ear.DarkstoreApi.dto.order.completeOrder.CompleteOrderRequestDto;
import cz.cvut.ear.DarkstoreApi.dto.order.OrderDto;
import cz.cvut.ear.DarkstoreApi.dto.order.OrderGroupDto;
import cz.cvut.ear.DarkstoreApi.dto.order.createOrder.CreateOrderDto;
import cz.cvut.ear.DarkstoreApi.dto.order.createOrder.CreateOrderRequest;
import cz.cvut.ear.DarkstoreApi.exception.OrderNotFoundException;
import cz.cvut.ear.DarkstoreApi.exception.OrderNotReadyForCompletionException;
import cz.cvut.ear.DarkstoreApi.model.order.Order;
import cz.cvut.ear.DarkstoreApi.model.order.OrderStatus;
import cz.cvut.ear.DarkstoreApi.repository.CourierRepository;
import cz.cvut.ear.DarkstoreApi.repository.OrderGroupRepository;
import cz.cvut.ear.DarkstoreApi.repository.OrderRepository;
import cz.cvut.ear.DarkstoreApi.util.mapper.OrderGroupMapper;
import cz.cvut.ear.DarkstoreApi.util.mapper.OrderMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CourierRepository courierRepository;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private OrderGroupMapper orderGroupMapper;

    @Mock
    private OrderGroupRepository orderGroupRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    void testCompleteOrdersWhenOrderIdDoesNotExistThenThrowOrderNotFoundException() {
        OrderService orderService = new OrderService(orderRepository, courierRepository, orderMapper, orderGroupMapper, orderGroupRepository);

        CompleteOrderRequestDto completeOrderRequestDto = new CompleteOrderRequestDto();
        completeOrderRequestDto.setCompleteOrders(Arrays.asList(new CompleteOrderDto(), new CompleteOrderDto()));

        when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> orderService.completeOrders(completeOrderRequestDto));

        verify(orderRepository, times(1)).findById(anyLong());
    }

    @Test
    void testCompleteOrdersWhenOrderStatusIsNotAssignedThenThrowOrderNotReadyForCompletionException() {
        OrderService orderService = new OrderService(orderRepository, courierRepository, orderMapper, orderGroupMapper, orderGroupRepository);

        CompleteOrderRequestDto completeOrderRequestDto = new CompleteOrderRequestDto();
        completeOrderRequestDto.setCompleteOrders(Arrays.asList(new CompleteOrderDto(), new CompleteOrderDto()));

        Order mockOrder = new Order();
        mockOrder.setStatus(OrderStatus.CREATED);

        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(mockOrder));

        assertThrows(OrderNotReadyForCompletionException.class, () -> orderService.completeOrders(completeOrderRequestDto));

        verify(orderRepository, times(1)).findById(anyLong());
    }

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
    @Disabled
    void testGetOrders() {
        OrderService orderService = new OrderService(orderRepository, courierRepository, orderMapper, orderGroupMapper, orderGroupRepository);

        int limit = 10;
        int offset = 0;
        List<Order> mockOrders = Arrays.asList(new Order(), new Order());
        List<OrderDto> expectedOrderDtos = Arrays.asList(new OrderDto(), new OrderDto());

        when(orderRepository.findAll()).thenReturn(mockOrders);
        when(orderMapper.orderToOrderDto(anyList())).thenReturn(expectedOrderDtos);

        List<Order> resultOrderDtos = orderService.getOrders(limit, offset);

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

    @Test
    @Disabled
    void testAssignOrdersWhenOrdersExistThenReturnOrderGroupDtoList() {
        List<Order> mockOrders = Arrays.asList(new Order(), new Order());
        when(orderRepository.findAll()).thenReturn(mockOrders);
        when(courierRepository.findAllSortedByType()).thenReturn(Collections.emptyList());

        List<OrderGroupDto> result = orderService.assignOrders();

        verify(orderRepository, times(1)).findAll();
        verify(courierRepository, times(1)).findAllSortedByType();
        assertNotNull(result);
        assertEquals(mockOrders.size(), result.size());
    }

    @Test
    @Disabled
    void testAssignOrdersWhenNoOrdersThenReturnEmptyList() {
        when(orderRepository.findAll()).thenReturn(Collections.emptyList());

        List<OrderGroupDto> result = orderService.assignOrders();

        verify(orderRepository, times(1)).findAll();
        verify(courierRepository, times(0)).findAllSortedByType();
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    @Disabled
    void testAssignOrdersWhenOrdersExistThenOrdersAreAssigned() {
        List<Order> mockOrders = Arrays.asList(new Order(), new Order());
        when(orderRepository.findAll()).thenReturn(mockOrders);
        when(courierRepository.findAllSortedByType()).thenReturn(Collections.emptyList());

        orderService.assignOrders();

        verify(orderRepository, times(1)).findAll();
        verify(courierRepository, times(1)).findAllSortedByType();
    }

    @Test
    @Disabled
    void testAssignOrdersWhenNoOrdersThenNoAssignments() {
        when(orderRepository.findAll()).thenReturn(Collections.emptyList());

        orderService.assignOrders();

        verify(orderRepository, times(1)).findAll();
        verify(courierRepository, times(0)).findAllSortedByType();
    }

    @Test
    @Disabled
    void testCompleteOrdersWhenGivenCompleteOrderRequestDtoThenUpdateOrderStatusAndSetCompletionTime() {
        OrderService orderService = new OrderService(orderRepository, courierRepository, orderMapper, orderGroupMapper, orderGroupRepository);

        CompleteOrderRequestDto completeOrderRequestDto = new CompleteOrderRequestDto();
        completeOrderRequestDto.setCompleteOrders(Arrays.asList(new CompleteOrderDto(), new CompleteOrderDto()));

        List<CompleteOrderDto> completeOrderDtos = Arrays.asList(new CompleteOrderDto(), new CompleteOrderDto());
        List<Order> mockCompletedOrders = Arrays.asList(new Order(), new Order());
        List<OrderDto> expectedOrderDtos = Arrays.asList(new OrderDto(), new OrderDto());

        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(mockCompletedOrders.get(0)), Optional.of(mockCompletedOrders.get(1)));
        when(orderRepository.saveAll(mockCompletedOrders)).thenReturn(mockCompletedOrders);
        when(orderMapper.orderToOrderDto(anyList())).thenReturn(expectedOrderDtos);

        List<OrderDto> resultOrderDtos = orderService.completeOrders(completeOrderRequestDto);

        assertNotNull(resultOrderDtos);
        assertEquals(expectedOrderDtos.size(), resultOrderDtos.size());

        ArgumentCaptor<List<Order>> orderListCaptor = ArgumentCaptor.forClass(List.class);
        verify(orderRepository, times(2)).findById(anyLong());
        verify(orderRepository).saveAll(orderListCaptor.capture());
        verify(orderMapper).orderToOrderDto(orderListCaptor.getValue());

        for (Order order : orderListCaptor.getValue()) {
            assertEquals(OrderStatus.FINISHED, order.getStatus());
            assertNotNull(order.getCompleteTime());
        }
    }
}