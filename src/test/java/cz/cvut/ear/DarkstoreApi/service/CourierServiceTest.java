package cz.cvut.ear.DarkstoreApi.service;

import cz.cvut.ear.DarkstoreApi.dto.CourierDto;
import cz.cvut.ear.DarkstoreApi.dto.CourierMetaInfo;
import cz.cvut.ear.DarkstoreApi.dto.CourierMetaInfoRequestDto;
import cz.cvut.ear.DarkstoreApi.dto.CreateCourierRequest;
import cz.cvut.ear.DarkstoreApi.model.courier.Courier;
import cz.cvut.ear.DarkstoreApi.model.order.Order;
import cz.cvut.ear.DarkstoreApi.repository.CourierRepository;
import cz.cvut.ear.DarkstoreApi.repository.OrderRepository;
import cz.cvut.ear.DarkstoreApi.util.mapper.CourierMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@SpringBootTest
public class CourierServiceTest {

    @Mock
    private CourierRepository courierRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CourierMapper courierMapper;

    @InjectMocks
    private CourierService courierService;

    @Test
    @Transactional
    void testCreateCouriers() {
        CourierService courierService = new CourierService(courierRepository, orderRepository, courierMapper);

        CreateCourierRequest createCourierRequest = new CreateCourierRequest();

        List<Courier> mockSavedCouriers = Arrays.asList(new Courier(), new Courier());
        List<CourierDto> expectedCourierDtos = Arrays.asList(new CourierDto(), new CourierDto());

        when(courierMapper.createCourierDtoToCourier(anyList())).thenReturn(mockSavedCouriers);
        when(courierRepository.saveAll(anyList())).thenReturn(mockSavedCouriers);
        when(courierMapper.courierToCourierDto(anyList())).thenReturn(expectedCourierDtos);

        List<CourierDto> resultCourierDtos = courierService.createCouriers(createCourierRequest);

        assertNotNull(resultCourierDtos);
        assertEquals(expectedCourierDtos.size(), resultCourierDtos.size());
    }

    @Test
    void testGetCouriers() {
        CourierService courierService = new CourierService(courierRepository, null, courierMapper);

        int limit = 10;
        int offset = 0;
        List<Courier> mockCouriers = Arrays.asList(new Courier(), new Courier());
        List<CourierDto> expectedCourierDtos = Arrays.asList(new CourierDto(), new CourierDto());

        when(courierRepository.findAll()).thenReturn(mockCouriers);
        when(courierMapper.courierToCourierDto(anyList())).thenReturn(expectedCourierDtos);

        List<CourierDto> resultCourierDtos = courierService.getCouriers(limit, offset);

        assertNotNull(resultCourierDtos);
        assertEquals(expectedCourierDtos.size(), resultCourierDtos.size());

        verify(courierRepository).findAll();
        verify(courierMapper).courierToCourierDto(mockCouriers);
    }

    @Test
    void testGetCourier() {
        CourierService courierService = new CourierService(courierRepository, null, courierMapper);

        long courierId = 1L;
        Courier mockCourier = new Courier();
        CourierDto expectedCourierDto = new CourierDto();

        when(courierRepository.findById(courierId)).thenReturn(Optional.of(mockCourier));
        when(courierMapper.courierToCourierDto(mockCourier)).thenReturn(expectedCourierDto);

        CourierDto resultCourierDto = courierService.getCourier(courierId);

        assertNotNull(resultCourierDto);

        verify(courierRepository).findById(courierId);
        verify(courierMapper).courierToCourierDto(mockCourier);
    }

    @Test
    void testGetCourierMetaInfo() {
        CourierService courierService = new CourierService(courierRepository, orderRepository, null);

        long courierId = 1L;
        LocalDateTime startDate = LocalDateTime.of(2023, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2023, 1, 31, 23, 59);

        CourierMetaInfoRequestDto courierMetaInfoRequestDto = new CourierMetaInfoRequestDto();
        courierMetaInfoRequestDto.setStartDate(LocalDate.of(2023, 1, 1));
        courierMetaInfoRequestDto.setEndDate(LocalDate.of(2023, 1, 31));

        Courier mockCourier = new Courier();
        List<Order> mockOrders = Arrays.asList(new Order(), new Order());

        when(courierRepository.findById(courierId)).thenReturn(java.util.Optional.of(mockCourier));
        when(orderRepository.findByOrderGroupCourierAndCompleteTimeBetween(mockCourier, startDate, endDate))
                .thenReturn(mockOrders);

        CourierMetaInfo resultCourierMetaInfo = courierService.getCourierMetaInfo(courierId, courierMetaInfoRequestDto);

        assertNotNull(resultCourierMetaInfo);

        ArgumentCaptor<Courier> courierCaptor = ArgumentCaptor.forClass(Courier.class);
        ArgumentCaptor<LocalDateTime> startDateCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        ArgumentCaptor<LocalDateTime> endDateCaptor = ArgumentCaptor.forClass(LocalDateTime.class);

        verify(courierRepository).findById(courierId);
        verify(orderRepository).findByOrderGroupCourierAndCompleteTimeBetween(
                courierCaptor.capture(), startDateCaptor.capture(), endDateCaptor.capture());

        assertEquals(mockCourier, courierCaptor.getValue());
        assertEquals(startDate, startDateCaptor.getValue());
    }
}
