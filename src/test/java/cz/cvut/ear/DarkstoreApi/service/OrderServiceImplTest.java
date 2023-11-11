package cz.cvut.ear.DarkstoreApi.service;

import cz.cvut.ear.DarkstoreApi.dto.CreateOrderDto;
import cz.cvut.ear.DarkstoreApi.dto.CreateOrderRequest;
import cz.cvut.ear.DarkstoreApi.dto.OrderDto;
import cz.cvut.ear.DarkstoreApi.model.order.Order;
import cz.cvut.ear.DarkstoreApi.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private OrderServiceImpl orderService;

    private CreateOrderDto createOrderDto;
    private Order order;
    private OrderDto orderDto;

    @BeforeEach
    public void setUp() {
        createOrderDto = new CreateOrderDto();
        createOrderDto.setWeight(1.0f);
        createOrderDto.setRegion(1);
        createOrderDto.setCost(100);
        createOrderDto.setDeliveryHours(Arrays.asList("09:00-10:00"));

        order = new Order();
        order.setWeight(1.0f);
        order.setRegion(1);
        order.setCost(100);

        orderDto = new OrderDto();
        orderDto.setWeight(1.0f);
        orderDto.setRegion(1);
        orderDto.setCost(100);
        orderDto.setDeliveryHours(Arrays.asList("09:00-10:00"));
    }

    @Test
    public void testCreateOrders() {
        CreateOrderRequest createOrderRequest = new CreateOrderRequest();
        createOrderRequest.setOrders(Collections.singletonList(createOrderDto));

        when(modelMapper.map(createOrderDto, Order.class)).thenReturn(order);
        when(orderRepository.saveAll(any())).thenReturn(Collections.singletonList(order));
        when(modelMapper.map(order, OrderDto.class)).thenReturn(orderDto);

        ResponseEntity<List<OrderDto>> responseEntity = orderService.createOrders(createOrderRequest);

        assertEquals(200, responseEntity.getStatusCodeValue());
        assertEquals(1, responseEntity.getBody().size());
        assertEquals(orderDto, responseEntity.getBody().get(0));
    }

    @Test
    public void testCreateOrdersWithEmptyRequest() {
        CreateOrderRequest createOrderRequest = new CreateOrderRequest();
        createOrderRequest.setOrders(Collections.emptyList());

        ResponseEntity<List<OrderDto>> responseEntity = orderService.createOrders(createOrderRequest);

        assertEquals(400, responseEntity.getStatusCodeValue());
        assertNull(responseEntity.getBody());
    }

    @Test
    public void testCreateOrdersWithInvalidOrders() {
        CreateOrderDto invalidOrderDto = new CreateOrderDto();
        invalidOrderDto.setWeight(-1.0f);
        invalidOrderDto.setRegion(-1);
        invalidOrderDto.setCost(-100);
        invalidOrderDto.setDeliveryHours(Arrays.asList("25:00-26:00"));

        CreateOrderRequest createOrderRequest = new CreateOrderRequest();
        createOrderRequest.setOrders(Collections.singletonList(invalidOrderDto));

        ResponseEntity<List<OrderDto>> responseEntity = orderService.createOrders(createOrderRequest);

        assertEquals(400, responseEntity.getStatusCodeValue());
        assertNull(responseEntity.getBody());
    }

    @Test
    public void testGetOrders() {
        when(orderRepository.findAll()).thenReturn(Collections.singletonList(order));
        when(modelMapper.map(order, OrderDto.class)).thenReturn(orderDto);

        ResponseEntity<List<OrderDto>> responseEntity = orderService.getOrders(1, 0);

        assertEquals(200, responseEntity.getStatusCodeValue());
        assertEquals(1, responseEntity.getBody().size());
        assertEquals(orderDto, responseEntity.getBody().get(0));
    }

    @Test
    public void testGetOrdersWithNoOrders() {
        when(orderRepository.findAll()).thenReturn(Collections.emptyList());

        ResponseEntity<List<OrderDto>> responseEntity = orderService.getOrders(1, 0);

        assertEquals(404, responseEntity.getStatusCodeValue());
        assertNull(responseEntity.getBody());
    }

    @Test
    public void testGetOrder() {
        long orderId = 1L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(modelMapper.map(order, OrderDto.class)).thenReturn(orderDto);

        ResponseEntity<OrderDto> responseEntity = orderService.getOrder(orderId);

        assertEquals(200, responseEntity.getStatusCodeValue());
        assertEquals(orderDto, responseEntity.getBody());
    }

    @Test
    public void testGetOrderWhenOrderDoesNotExist() {
        long orderId = 1L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        ResponseEntity<OrderDto> responseEntity = orderService.getOrder(orderId);

        assertEquals(404, responseEntity.getStatusCodeValue());
        assertNull(responseEntity.getBody());
    }
}