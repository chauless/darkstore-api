package cz.cvut.ear.DarkstoreApi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.cvut.ear.DarkstoreApi.dto.order.completeOrder.CompleteOrderRequestDto;
import cz.cvut.ear.DarkstoreApi.dto.order.createOrder.CreateOrderDto;
import cz.cvut.ear.DarkstoreApi.dto.order.createOrder.CreateOrderRequest;
import cz.cvut.ear.DarkstoreApi.dto.order.OrderDto;
import cz.cvut.ear.DarkstoreApi.exception.OrderNotFoundException;
import cz.cvut.ear.DarkstoreApi.model.order.Order;
import cz.cvut.ear.DarkstoreApi.service.OrderService;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Autowired
    private ObjectMapper objectMapper;

    private CreateOrderDto createOrderDto;
    private Order order;
    private OrderDto orderDto;

    @BeforeEach
    public void setUp() {
        createOrderDto = new CreateOrderDto();
        createOrderDto.setWeight(1.0f);
        createOrderDto.setRegion(1);
        createOrderDto.setCost(100);
        createOrderDto.setDeliveryHour("09:00-10:00");

        order = new Order();
        order.setWeight(1.0f);
        order.setRegion(1);
        order.setCost(100);

        orderDto = new OrderDto();
        orderDto.setWeight(1.0f);
        orderDto.setRegion(1);
        orderDto.setCost(100);
        orderDto.setDeliveryHour("09:00-10:00");
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    public void testCreateOrders() throws Exception {
        CreateOrderRequest createOrderRequest = new CreateOrderRequest();
        createOrderRequest.setOrders(Collections.singletonList(createOrderDto));

        OrderDto orderDto = new OrderDto();

        List<OrderDto> orderDtoList = Arrays.asList(orderDto);

        ResponseEntity<List<OrderDto>> responseEntity = ResponseEntity.ok(orderDtoList);
        when(orderService.createOrders(any(CreateOrderRequest.class))).thenReturn(responseEntity.getBody());

        mockMvc.perform(MockMvcRequestBuilders.post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createOrderRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(orderDtoList)));

        verify(orderService, times(1)).createOrders(any(CreateOrderRequest.class));
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    public void testCreateOrdersJson() throws Exception {
        OrderDto orderDto1 = new OrderDto();
        OrderDto orderDto2 = new OrderDto();
        List<OrderDto> orderDtoList = Arrays.asList(orderDto1, orderDto2);

        when(orderService.createOrders(any(CreateOrderRequest.class))).thenReturn(orderDtoList);

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"orders\": [{\"weight\": 1.0, \"region\": 1, \"deliveryHour\": \"10:00-11:00\", \"cost\": 100}, {\"weight\": 2.0, \"region\": 2, \"deliveryHour\": \"12:00-13:00\", \"cost\": 200}]}"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    public void testCreateOrdersBadRequest() throws Exception {
        CreateOrderDto createOrderDto = new CreateOrderDto();
        createOrderDto.setWeight(-1.0f);
        createOrderDto.setRegion(-1);
        createOrderDto.setDeliveryHour("09:00-10:00");
        createOrderDto.setCost(-100);

        CreateOrderRequest createOrderRequest = new CreateOrderRequest();
        createOrderRequest.setOrders(Arrays.asList(createOrderDto));

        mockMvc.perform(MockMvcRequestBuilders.post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createOrderRequest)))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError());

        verify(orderService, times(0)).createOrders(any(CreateOrderRequest.class));
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    public void testCreateOrdersBadRequestJson() throws Exception {
        when(orderService.createOrders(any(CreateOrderRequest.class))).thenThrow(ConstraintViolationException.class);

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"orders\": [{\"weight\": -1.0, \"region\": 1, \"deliveryHour\": \"10:00-11:00\", \"cost\": 100}, {\"weight\": 2.0, \"region\": 2, \"deliveryHour\": \"12:00-13:00\", \"cost\": 200}]}"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    public void testCreateOrdersInternalServerError() throws Exception {
        when(orderService.createOrders(any(CreateOrderRequest.class))).thenThrow(RuntimeException.class);

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"orders\": [{\"weight\": 1.0, \"region\": 1, \"deliveryHour\": \"10:00-11:00\", \"cost\": 100}, {\"weight\": 2.0, \"region\": 2, \"deliveryHour\": \"12:00-13:00\", \"cost\": 200}]}"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    @Disabled
    public void testGetOrders() throws Exception {
        OrderDto orderDto = new OrderDto();

        List<OrderDto> orderDtoList = Arrays.asList(orderDto);

        ResponseEntity<List<OrderDto>> responseEntity = ResponseEntity.ok(orderDtoList);

        mockMvc.perform(MockMvcRequestBuilders.get("/orders")
                        .param("limit", "1")
                        .param("offset", "0"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(orderService, times(1)).getOrders(anyInt(), anyInt());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    public void testGetOrdersWithNonValidParameters() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/orders")
                        .param("limit", "asdfasf")
                        .param("offset", "wefw"))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError());

        verify(orderService, times(0)).getOrders(anyInt(), anyInt());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    public void testGetOrderById() throws Exception {
        OrderDto orderDto = new OrderDto();

        ResponseEntity<OrderDto> responseEntity = ResponseEntity.ok(orderDto);
        when(orderService.getOrder(anyLong())).thenReturn(responseEntity.getBody());

        mockMvc.perform(MockMvcRequestBuilders.get("/orders/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(orderDto)));

        verify(orderService, times(1)).getOrder(anyLong());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    public void testGetOrderByIdNotFound() throws Exception {
        when(orderService.getOrder(anyLong())).thenThrow(OrderNotFoundException.class);

        mockMvc.perform(MockMvcRequestBuilders.get("/orders/100"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        verify(orderService, times(1)).getOrder(anyLong());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    public void testGetOrderByIdJson() throws Exception {
        OrderDto orderDto = new OrderDto();
        when(orderService.getOrder(anyLong())).thenReturn(orderDto);

        mockMvc.perform(get("/orders/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    public void testGetOrderByIdNotFoundJson() throws Exception {
        when(orderService.getOrder(anyLong())).thenThrow(OrderNotFoundException.class);

        mockMvc.perform(get("/orders/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    public void testGetOrderByIdInternalServerError() throws Exception {
        when(orderService.getOrder(anyLong())).thenThrow(RuntimeException.class);

        mockMvc.perform(get("/orders/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    public void testCompleteOrders() throws Exception {
        OrderDto orderDto1 = new OrderDto();
        OrderDto orderDto2 = new OrderDto();
        List<OrderDto> orderDtoList = Arrays.asList(orderDto1, orderDto2);

        when(orderService.completeOrders(any(CompleteOrderRequestDto.class))).thenReturn(orderDtoList);

        mockMvc.perform(post("/orders/complete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"orders\": [{\"order_id\": 1, \"complete_time\": \"2022-12-12T10:00:00Z\"}, {\"order_id\": 2, \"complete_time\": \"2022-12-12T11:00:00Z\"}]}"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    public void testCompleteOrdersBadRequest() throws Exception {
        when(orderService.completeOrders(any(CompleteOrderRequestDto.class))).thenThrow(ConstraintViolationException.class);

        mockMvc.perform(post("/orders/complete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"orders\": [{\"order_id\": 1, \"complete_time\": \"invalid_time\"}, {\"order_id\": 2, \"complete_time\": \"2022-12-12T11:00:00Z\"}]}"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    public void testCompleteOrdersInternalServerError() throws Exception {
        when(orderService.completeOrders(any(CompleteOrderRequestDto.class))).thenThrow(RuntimeException.class);

        mockMvc.perform(post("/orders/complete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"orders\": [{\"order_id\": 1, \"complete_time\": \"2022-12-12T10:00:00Z\"}, {\"order_id\": 2, \"complete_time\": \"2022-12-12T11:00:00Z\"}]}"))
                .andExpect(status().isInternalServerError());
    }
}