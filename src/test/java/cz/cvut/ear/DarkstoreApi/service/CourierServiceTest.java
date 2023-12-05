package cz.cvut.ear.DarkstoreApi.service;

import cz.cvut.ear.DarkstoreApi.dto.user.courier.CourierDto;
import cz.cvut.ear.DarkstoreApi.dto.user.courier.metaInfo.CourierMetaInfo;
import cz.cvut.ear.DarkstoreApi.dto.user.courier.metaInfo.CourierMetaInfoRequestDto;
import cz.cvut.ear.DarkstoreApi.dto.user.courier.createCourier.CreateCourierRequest;
import cz.cvut.ear.DarkstoreApi.exception.CourierNotFoundException;
import cz.cvut.ear.DarkstoreApi.model.courier.Courier;
import cz.cvut.ear.DarkstoreApi.model.order.Order;
import cz.cvut.ear.DarkstoreApi.repository.CourierRepository;
import cz.cvut.ear.DarkstoreApi.repository.OrderRepository;
import cz.cvut.ear.DarkstoreApi.util.mapper.CourierMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CourierServiceTest {

    @Mock
    private CourierRepository courierRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CourierMapper courierMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private CourierService courierService;

    private CreateCourierRequest createCourierRequest;
    private List<Courier> couriers;
    private List<CourierDto> courierDtos;

    @BeforeEach
    public void setUp() {
        createCourierRequest = new CreateCourierRequest();
        couriers = List.of(new Courier(), new Courier());
        courierDtos = List.of(new CourierDto(), new CourierDto());
    }

    @Test
    public void testCreateCouriersWhenValidRequestThenCreateAndSaveCouriers() {
        when(courierMapper.createCourierDtoToCourier(createCourierRequest.getCouriers())).thenReturn(couriers);
        when(courierRepository.saveAll(any(List.class))).thenReturn(couriers);
        when(courierMapper.courierToCourierDto(couriers)).thenReturn(courierDtos);

        List<CourierDto> result = courierService.createCouriers(createCourierRequest);

        assertEquals(courierDtos, result);
    }

    @Test
    public void testCreateCouriersWhenEmptyRequestThenReturnEmptyList() {
        createCourierRequest.setCouriers(Collections.emptyList());
        when(courierMapper.createCourierDtoToCourier(createCourierRequest.getCouriers())).thenReturn(Collections.emptyList());

        List<CourierDto> result = courierService.createCouriers(createCourierRequest);

        assertEquals(Collections.emptyList(), result);
    }

    @Test
    public void testCreateCouriersWhenNullRequestThenThrowException() {
        assertThrows(NullPointerException.class, () -> courierService.createCouriers(null));
    }

    @Test
    @Disabled
    public void testGetCourierMetaInfoWhenValidCourierIdAndRequestThenReturnCourierMetaInfo() {
        long courierId = 1L;
        CourierMetaInfoRequestDto requestDto = new CourierMetaInfoRequestDto();
        requestDto.setStartDate(LocalDate.now().minusDays(1));
        requestDto.setEndDate(LocalDate.now());

        Courier courier = new Courier();
        courier.setId(courierId);

        Order order = new Order();
        order.setCompleteTime(LocalDateTime.now());

        when(courierRepository.findById(courierId)).thenReturn(java.util.Optional.of(courier));
        when(orderRepository.findByOrderGroupCourierAndCompleteTimeBetween(any(), any(), any())).thenReturn(List.of(order));

        CourierMetaInfo result = courierService.getCourierMetaInfo(courierId, requestDto);

        assertEquals(0, result.getEarnings());
        assertEquals(0, result.getRate());
    }

    @Test
    public void testGetCourierMetaInfoWhenCourierNotFoundThenThrowException() {
        long courierId = 1L;
        CourierMetaInfoRequestDto requestDto = new CourierMetaInfoRequestDto();
        requestDto.setStartDate(LocalDate.now().minusDays(1));
        requestDto.setEndDate(LocalDate.now());

        when(courierRepository.findById(courierId)).thenReturn(java.util.Optional.empty());

        assertThrows(CourierNotFoundException.class, () -> courierService.getCourierMetaInfo(courierId, requestDto));
    }

    @Test
    public void testGetCourierMetaInfoWhenStartDateAfterEndDateThenThrowException() {
        long courierId = 1L;
        CourierMetaInfoRequestDto requestDto = new CourierMetaInfoRequestDto();
        requestDto.setStartDate(LocalDate.now());
        requestDto.setEndDate(LocalDate.now().minusDays(1));

        Courier courier = new Courier();
        courier.setId(courierId);
    }
}