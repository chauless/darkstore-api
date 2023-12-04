package cz.cvut.ear.DarkstoreApi.controller;

import cz.cvut.ear.DarkstoreApi.dto.*;
import cz.cvut.ear.DarkstoreApi.service.CourierService;
import cz.cvut.ear.DarkstoreApi.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CourierController.class)
public class CourierControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CourierService courierService;

    @MockBean
    private OrderService orderService;

    @Test
    public void testCreateCouriers() throws Exception {
        CourierDto courierDto = new CourierDto();
        courierDto.setCourierId(1L);
        when(courierService.createCouriers(any(CreateCourierRequest.class))).thenReturn(Collections.singletonList(courierDto));

        mockMvc.perform(MockMvcRequestBuilders.post("/couriers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].courierId").value(1L));
    }

    @Test
    public void testCreateCouriersValidationError() throws Exception {
        when(courierService.createCouriers(any(CreateCourierRequest.class))).thenThrow(new IllegalArgumentException("Invalid request"));

        mockMvc.perform(MockMvcRequestBuilders.post("/couriers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetCouriers() throws Exception {
        CourierDto courierDto = new CourierDto();
        courierDto.setCourierId(1L);
        when(courierService.getCouriers(any(Integer.class), any(Integer.class))).thenReturn(Collections.singletonList(courierDto));

        mockMvc.perform(MockMvcRequestBuilders.get("/couriers")
                        .param("limit", "1")
                        .param("offset", "0"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].courierId").value(1L));
    }

    @Test
    public void testGetCourierById() throws Exception {
        CourierDto courierDto = new CourierDto();
        courierDto.setCourierId(1L);
        when(courierService.getCourier(any(Long.class))).thenReturn(courierDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/couriers/1"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.courierId").value(1L));
    }

    @Test
    public void testGetCourierByIdNotFoundError() throws Exception {
        when(courierService.getCourier(any(Long.class))).thenThrow(new IllegalArgumentException("Courier not found"));

        mockMvc.perform(MockMvcRequestBuilders.get("/couriers/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetCourierMetaInfoById() throws Exception {
        CourierMetaInfo courierMetaInfo = new CourierMetaInfo(100, 4.5);
        when(courierService.getCourierMetaInfo(anyLong(), any(CourierMetaInfoRequestDto.class))).thenReturn(courierMetaInfo);

        mockMvc.perform(MockMvcRequestBuilders.get("/couriers/meta-info/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"start_date\":\"2022-01-01\",\"end_date\":\"2022-01-31\"}"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.earnings").value(100))
                .andExpect(MockMvcResultMatchers.jsonPath("$.rate").value(4.5));
    }

    @Test
    public void testGetCourierMetaInfoByIdNotFoundError() throws Exception {
        when(courierService.getCourierMetaInfo(anyLong(), any(CourierMetaInfoRequestDto.class))).thenThrow(new IllegalArgumentException("Courier not found"));

        mockMvc.perform(MockMvcRequestBuilders.get("/couriers/meta-info/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"start_date\":\"2022-01-01\",\"end_date\":\"2022-01-31\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetCourierMetaInfoByIdBadRequest() throws Exception {
        when(courierService.getCourierMetaInfo(anyLong(), any(CourierMetaInfoRequestDto.class))).thenThrow(new IllegalArgumentException("Invalid request"));

        mockMvc.perform(MockMvcRequestBuilders.get("/couriers/meta-info/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"start_date\":\"2022-01-01\"}")) // Missing end_date
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetAssignmentsBadRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/couriers/assignments")
                        .param("limit", "-1")
                        .param("offset", "-1"))
                .andExpect(status().isBadRequest());
    }
}