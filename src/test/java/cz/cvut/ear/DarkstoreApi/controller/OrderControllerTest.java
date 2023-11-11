package cz.cvut.ear.DarkstoreApi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.cvut.ear.DarkstoreApi.dto.CreateOrderDto;
import cz.cvut.ear.DarkstoreApi.dto.CreateOrderRequest;
import cz.cvut.ear.DarkstoreApi.dto.OrderDto;
import cz.cvut.ear.DarkstoreApi.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@WebMvcTest(OrderController.class)
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderService orderService;

    @Test
    public void testCreateOrders() throws Exception {
        // Arrange
        CreateOrderDto createOrderDto = new CreateOrderDto();
        createOrderDto.setWeight(1.0f);
        createOrderDto.setRegion(1);
        createOrderDto.setDeliveryHours(Arrays.asList("10:00-12:00"));
        createOrderDto.setCost(100);

        CreateOrderRequest createOrderRequest = new CreateOrderRequest();
        createOrderRequest.setOrders(Arrays.asList(createOrderDto));

        OrderDto orderDto = new OrderDto();
        orderDto.setOrderId(1L);
        orderDto.setWeight(1.0f);
        orderDto.setRegion(1);
        orderDto.setDeliveryHours(Arrays.asList("10:00-12:00"));
        orderDto.setCost(100);

        List<OrderDto> orderDtoList = Arrays.asList(orderDto);

        when(orderService.createOrders(any(CreateOrderRequest.class))).thenReturn(orderDtoList);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createOrderRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(orderDtoList)));

        verify(orderService, times(1)).createOrders(any(CreateOrderRequest.class));
    }

    @Test
    public void testCreateOrdersBadRequest() throws Exception {
        // Arrange
        CreateOrderDto createOrderDto = new CreateOrderDto();
        createOrderDto.setWeight(-1.0f);
        createOrderDto.setRegion(-1);
        createOrderDto.setDeliveryHours(Arrays.asList("25:00-27:00"));
        createOrderDto.setCost(-100);

        CreateOrderRequest createOrderRequest = new CreateOrderRequest();
        createOrderRequest.setOrders(Arrays.asList(createOrderDto));

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createOrderRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        verify(orderService, times(0)).createOrders(any(CreateOrderRequest.class));
    }

    @Test
    public void testGetOrders() throws Exception {
        // Arrange
        OrderDto orderDto = new OrderDto();
        orderDto.setOrderId(1L);
        orderDto.setWeight(1.0f);
        orderDto.setRegion(1);
        orderDto.setDeliveryHours(Arrays.asList("10:00-12:00"));
        orderDto.setCost(100);

        List<OrderDto> orderDtoList = Arrays.asList(orderDto);

        when(orderService.getOrders(anyInt(), anyInt())).thenReturn(orderDtoList);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/orders")
                        .param("limit", "1")
                        .param("offset", "0"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(orderDtoList)));

        verify(orderService, times(1)).getOrders(anyInt(), anyInt());
    }

    @Test
    public void testGetOrdersWithNonValidParameters() throws Exception {
        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/orders")
                        .param("limit", "asdfasf")
                        .param("offset", "wefw"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        verify(orderService, times(0)).getOrders(anyInt(), anyInt());
    }

    @Test
    public void testGetOrderById() throws Exception {
        // Arrange
        OrderDto orderDto = new OrderDto();
        orderDto.setOrderId(1L);
        orderDto.setWeight(1.0f);
        orderDto.setRegion(1);
        orderDto.setDeliveryHours(Arrays.asList("10:00-12:00"));
        orderDto.setCost(100);

        when(orderService.getOrder(anyLong())).thenReturn(orderDto);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/orders/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(orderDto)));

        verify(orderService, times(1)).getOrder(anyLong());
    }

    @Test
    public void testGetOrderByIdNotFound() throws Exception {
        // Arrange
        when(orderService.getOrder(anyLong())).thenReturn(null);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/orders/222"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        verify(orderService, times(1)).getOrder(anyLong());
    }
}