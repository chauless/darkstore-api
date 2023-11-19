package cz.cvut.ear.DarkstoreApi.service;

import cz.cvut.ear.DarkstoreApi.dto.CourierDto;
import cz.cvut.ear.DarkstoreApi.dto.CourierMetaInfo;
import cz.cvut.ear.DarkstoreApi.dto.CourierMetaInfoRequestDto;
import cz.cvut.ear.DarkstoreApi.dto.CreateCourierRequest;
import cz.cvut.ear.DarkstoreApi.exception.CourierNotFoundException;
import cz.cvut.ear.DarkstoreApi.model.Role;
import cz.cvut.ear.DarkstoreApi.model.courier.Courier;
import cz.cvut.ear.DarkstoreApi.model.order.Order;
import cz.cvut.ear.DarkstoreApi.repository.CourierRepository;
import cz.cvut.ear.DarkstoreApi.repository.OrderRepository;
import cz.cvut.ear.DarkstoreApi.util.mapper.CourierMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;


@Service
@RequiredArgsConstructor
public class CourierService {
    private final CourierRepository courierRepository;
    private final OrderRepository orderRepository;
    private final CourierMapper courierMapper;

    @Transactional
    public List<CourierDto> createCouriers(CreateCourierRequest createCourierRequest) {
        List<Courier> savedCouriers = courierMapper.createCourierDtoToCourier(createCourierRequest.getCouriers());

        savedCouriers.forEach(courier -> courier.setRole(Role.ROLE_COURIER));

        savedCouriers = courierRepository.saveAll(savedCouriers);

        return courierMapper.courierToCourierDto(savedCouriers);
    }

    public List<CourierDto> getCouriers(int limit, int offset) {
        return courierMapper.courierToCourierDto(courierRepository.findAll().stream().skip(offset).limit(limit).toList());
    }

    public CourierDto getCourier(long courierId) {
        return courierRepository.findById(courierId)
                .map(courierMapper::courierToCourierDto)
                .orElseThrow(() -> new CourierNotFoundException("Courier with id " + courierId + " not found."));
    }

    public CourierMetaInfo getCourierMetaInfo(long courierId, CourierMetaInfoRequestDto courierMetaInfoRequestDto) {
        LocalDateTime startDate = courierMetaInfoRequestDto.getStartDate().atStartOfDay();
        LocalDateTime endDate = courierMetaInfoRequestDto.getEndDate().atStartOfDay();

        Courier courier = courierRepository.findById(courierId)
                .orElseThrow(() -> new CourierNotFoundException("Courier with id " + courierId + " not found."));

        List<Order> orders = orderRepository.findByCourierAndCompleteTimeBetween(courier, startDate, endDate);

        if (orders.isEmpty()) {
            return new CourierMetaInfo(0, 0);
        }

        int earnings = 0;

        for (Order order : orders) {
            earnings += order.getCost() * courier.getEarningsCoefficient();
        }

        long numberOfHoursBetweenDates = ChronoUnit.HOURS.between(startDate, endDate);

        BigDecimal rate = BigDecimal.valueOf(orders.size())
                .divide(BigDecimal.valueOf(numberOfHoursBetweenDates), 2, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(courier.getRateCoefficient()));

        rate = rate.setScale(2, RoundingMode.HALF_UP);

        return new CourierMetaInfo(earnings, rate.doubleValue());
    }
}
