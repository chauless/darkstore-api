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
import cz.cvut.ear.DarkstoreApi.service.security.JwtService;
import cz.cvut.ear.DarkstoreApi.util.mapper.CourierMapper;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final AuthenticationManager authenticationManager;

    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public List<CourierDto> createCouriers(CreateCourierRequest createCourierRequest) {
        List<Courier> savedCouriers = courierMapper.createCourierDtoToCourier(createCourierRequest.getCouriers());

        for (int i = 0; i < savedCouriers.size(); i++) {
            Courier courier = savedCouriers.get(i);
            courier.setPassword(passwordEncoder.encode(courier.getPassword()));
        }

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

        List<Order> orders = orderRepository.findByOrderGroupCourierAndCompleteTimeBetween(courier, startDate, endDate);

        if (orders.isEmpty()) {
            return new CourierMetaInfo(0, 0);
        }

        int earnings = getEarnings(courier, orders);
        double rate = getRate(courier, orders, startDate, endDate);

        return new CourierMetaInfo(earnings, rate);
    }

    public CourierMetaInfo getCourierMetaInfo(Authentication authentication, CourierMetaInfoRequestDto courierMetaInfoRequestDto) {
        String email = authentication.getName();

        Courier courier = courierRepository.findByEmail(email)
                .orElseThrow(() -> new CourierNotFoundException("Courier with email " + email + " not found."));

        return getCourierMetaInfo(courier.getId(), courierMetaInfoRequestDto);
    }

    private int getEarnings(Courier courier, List<Order> orders) {
        int courierEarningsCoefficient = calculateEarningsCoefficient(courier);

        BigDecimal earnings = BigDecimal.ZERO;

        for (Order order : orders) {
            earnings = earnings.add(getAdjustedCost(order).multiply(BigDecimal.valueOf(courierEarningsCoefficient)));
        }

        earnings = earnings.setScale(0, RoundingMode.HALF_UP);

        return earnings.intValue();
    }

    private BigDecimal getAdjustedCost(Order order) {
        BigDecimal adjustedCost = BigDecimal.valueOf(order.getCost());
        if (isFirstOrderInGroup(order)) {
            adjustedCost = adjustedCost.multiply(BigDecimal.valueOf(0.8));
        }
        return adjustedCost;
    }

    private boolean isFirstOrderInGroup(Order order) {
        Hibernate.initialize(order.getOrderGroup());
        return order.getOrderGroup().getOrders().get(0).getId().equals(order.getId());
    }

    private double getRate(Courier courier, List<Order> orders, LocalDateTime startDate, LocalDateTime endDate) {
        int courierRateCoefficient = calculateRateCoefficient(courier);

        long numberOfHoursBetweenDates = ChronoUnit.HOURS.between(startDate, endDate);

        BigDecimal rate = BigDecimal.valueOf(orders.size())
                .divide(BigDecimal.valueOf(numberOfHoursBetweenDates), 2, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(courierRateCoefficient));

        rate = rate.setScale(2, RoundingMode.HALF_UP);

        return rate.doubleValue();
    }

    private int calculateEarningsCoefficient(Courier courier) {
        int earningsCoefficient;
        switch (courier.getType()) {
            case FOOT -> earningsCoefficient = 2;
            case BIKE -> earningsCoefficient = 3;
            case AUTO -> earningsCoefficient = 4;
            default -> earningsCoefficient = -1;
        }

        return earningsCoefficient;
    }

    private int calculateRateCoefficient(Courier courier) {
        int rateCoefficient;
        switch (courier.getType()) {
            case FOOT -> rateCoefficient = 3;
            case BIKE -> rateCoefficient = 2;
            case AUTO -> rateCoefficient = 1;
            default -> rateCoefficient = -1;
        }

        return rateCoefficient;
    }
}
