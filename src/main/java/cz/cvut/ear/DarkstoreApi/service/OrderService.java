package cz.cvut.ear.DarkstoreApi.service;

import cz.cvut.ear.DarkstoreApi.dto.*;
import cz.cvut.ear.DarkstoreApi.exception.OrderNotFoundException;
import cz.cvut.ear.DarkstoreApi.exception.OrderNotReadyForCompletionException;
import cz.cvut.ear.DarkstoreApi.model.Role;
import cz.cvut.ear.DarkstoreApi.model.courier.Courier;
import cz.cvut.ear.DarkstoreApi.model.courier.CourierRegion;
import cz.cvut.ear.DarkstoreApi.model.courier.CourierType;
import cz.cvut.ear.DarkstoreApi.model.courier.WorkingHour;
import cz.cvut.ear.DarkstoreApi.model.order.DeliveryHour;
import cz.cvut.ear.DarkstoreApi.model.order.Order;
import cz.cvut.ear.DarkstoreApi.model.order.OrderGroup;
import cz.cvut.ear.DarkstoreApi.model.order.OrderStatus;
import cz.cvut.ear.DarkstoreApi.repository.CourierRepository;
import cz.cvut.ear.DarkstoreApi.repository.OrderGroupRepository;
import cz.cvut.ear.DarkstoreApi.repository.OrderRepository;
import cz.cvut.ear.DarkstoreApi.util.mapper.OrderGroupMapper;
import cz.cvut.ear.DarkstoreApi.util.mapper.OrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CourierRepository courierRepository;
    private final OrderMapper orderMapper;
    private final OrderGroupMapper orderGroupMapper;
    private final OrderGroupRepository orderGroupRepository;

    @Transactional
    public List<OrderDto> createOrders(CreateOrderRequest createOrderRequest) {
        List<CreateOrderDto> createOrderDtos = createOrderRequest.getOrders();

        List<Order> savedOrders = orderMapper.createOrderDtoToOrder(createOrderDtos);

        savedOrders.forEach(order -> order.setStatus(OrderStatus.CREATED));

        savedOrders = orderRepository.saveAll(savedOrders);

        return orderMapper.orderToOrderDto(savedOrders);
    }

    public List<OrderDto> getOrders(Authentication authentication, int limit, int offset) {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        if (authorities.contains(new SimpleGrantedAuthority("ROLE_MANAGER"))) {
            return orderMapper.orderToOrderDto(getOrders(limit, offset));
        } else if (authorities.contains(new SimpleGrantedAuthority("ROLE_COURIER"))) {
            return orderMapper.orderToOrderDto(getOrdersForCourier(authentication.getName(), limit, offset));
        } else {
            throw new IllegalStateException("Unexpected value: " + authorities);
        }
    }

    public List<Order> getOrders(int limit, int offset) {
        return orderRepository.findAll().stream().skip(offset).limit(limit).toList();
    }

    public List<Order> getOrdersForCourier(String email, int limit, int offset) {
        return orderRepository.findOrdersByOrderGroupCourierEmail(email).stream().skip(offset).limit(limit).toList();
    }

    public OrderDto getOrder(long orderId) {
        return orderMapper.orderToOrderDto(orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order with id " + orderId + " not found.")));
    }

    @Transactional
    public List<OrderDto> completeOrders(CompleteOrderRequestDto completeOrderRequestDto) {
        List<CompleteOrderDto> completeOrdersDto = completeOrderRequestDto.getCompleteOrders();

        List<Order> completedOrders = completeOrdersDto.stream().map(completeOrder -> {
            Order order = orderRepository.findById(completeOrder.getOrderId()).orElseThrow(() ->
                    new OrderNotFoundException("Order with id " + completeOrder.getOrderId() + " not found."));

            isOrderValid(order);

            order.setCompleteTime(LocalDateTime.parse(completeOrder.getCompleteTime(), DateTimeFormatter.ISO_DATE_TIME));
            order.setStatus(OrderStatus.FINISHED);
            return order;
        }).toList();

        return orderRepository.saveAll(completedOrders).stream()
                .map(orderMapper::orderToOrderDto).collect(Collectors.toList());
    }

    private void isOrderValid(Order order) {
        if (order.getStatus() != OrderStatus.ASSIGNED) {
            throw new OrderNotReadyForCompletionException("Order must have status assigned to be completed.");
        }
    }

    @Transactional
    public void deleteAllOrderGroups() {
        orderGroupRepository.deleteAll();
    }

    @Transactional
    public List<OrderGroupDto> assignOrders() {
        List<OrderGroup> orderGroups = new ArrayList<>();

        List<Courier> couriers = courierRepository.findAllSortedByType();

        for (Courier courier : couriers) {
            List<Order> availableOrders = getAvailableOrders(courier);
            orderGroups.addAll(assignOrdersToCourier(courier, getCourierProperties(courier.getType()), availableOrders));
        }

        return orderGroupMapper.orderGroupToOrderGroupDto(orderGroups);
    }

    private List<Order> getAvailableOrders(Courier courier) {
        List<Integer> regions = extractRegions(courier);
        List<Order> availableOrders = orderRepository.findPotentialOrders(regions, getMaxWeight(courier.getType()), courier.getWorkingHours());

        return groupAndSortOrders(availableOrders);
    }

    private List<Integer> extractRegions(Courier courier) {
        return courier.getRegions().stream().map(CourierRegion::getRegion).toList();
    }


    // TODO: move groupings and sorting to database
    private List<Order> groupAndSortOrders(List<Order> orders) {
        return orders.stream()
                .collect(Collectors.groupingBy(
                        Order::getRegion,
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                ordersInRegion -> {
                                    ordersInRegion.sort(
                                            Comparator.comparing(Order::getWeight)
                                                    .thenComparing(order -> order.getDeliveryHour().getStart())
                                                    .thenComparing(Order::getCost, Comparator.reverseOrder())
                                    );
                                    return ordersInRegion;
                                }
                        )
                ))
                .entrySet().stream()
                .sorted(Map.Entry.<Integer, List<Order>>comparingByValue(Comparator.comparingInt(List::size)).reversed())
                .flatMap(entry -> entry.getValue().stream())
                .collect(Collectors.toList());
    }

    private int getMaxWeight(CourierType courierType) {
        return switch (courierType) {
            case FOOT -> 10;
            case BIKE -> 20;
            case AUTO -> 40;
        };
    }

    private void assignOrderToCourier(Courier courier, OrderGroup orderGroup, Order order) {
        order.setOrderGroup(orderGroup);
        order.setStatus(OrderStatus.ASSIGNED);

        orderGroup.getOrders().add(order);

        if (orderGroup.getCourier() == null) {
            orderGroup.setCourier(courier);
            courier.getOrderGroups().add(orderGroup);
        }

        courierRepository.save(courier);
    }

    private List<OrderGroup> assignOrdersToCourier(Courier courier, CourierProperties properties, List<Order> orders) {
        HashMap<WorkingHour, List<Order>> workingHourOrdersMap = mapOrdersToWorkingHours(orders, courier.getWorkingHours());

        List<OrderGroup> orderGroups = new ArrayList<>();

        workingHourOrdersMap.forEach((workingHour, availableOrders) -> {
            List<OrderGroup> tempOrderGroups = processOrdersForWorkingHour(courier, properties, availableOrders, workingHour);
            orderGroups.addAll(tempOrderGroups);
        });

        return orderGroups;
    }

    private List<OrderGroup> processOrdersForWorkingHour(Courier courier, CourierProperties courierProperties, List<Order> orders, WorkingHour workingHour) {
        List<OrderGroup> orderGroups = new ArrayList<>();

        LocalTime currentTime = workingHour.getStart();
        LocalTime finishTime = workingHour.getFinish();

        for (Order order : orders) {
            boolean isNeedCreateNewOrderGroup = true;

            for (OrderGroup orderGroup : orderGroups) {
                if (!isEnoughTimeForOrder(orderGroup, order, currentTime, finishTime, courierProperties)) {
                    isNeedCreateNewOrderGroup = false;
                    break;
                }

                if (canAddOrderToGroup(order, orderGroup, courierProperties)) {
                    assignOrderToCourier(courier, orderGroup, order);
                    currentTime = currentTime.plusMinutes(courierProperties.durationOfDeliveryInMinutes);
                    isNeedCreateNewOrderGroup = false;
                    break;
                }
            }

            if (isNeedCreateNewOrderGroup) {
                OrderGroup orderGroup = new OrderGroup();
                assignOrderToCourier(courier, orderGroup, order);
                orderGroups.add(orderGroup);
            }
            courierRepository.save(courier);
        }

        return orderGroups;
    }

    private boolean isEnoughTimeForOrder(OrderGroup orderGroup, Order order, LocalTime currentTime, LocalTime finishTime, CourierProperties properties) {
        if (orderGroup.getOrders().isEmpty()) {
            return !currentTime.plusMinutes(properties.durationOfFirstDeliveryInMinutes).isAfter(finishTime);
        } else {
            if (orderGroup.getOrders().get(orderGroup.getOrders().size() - 1).getRegion() != order.getRegion()) {
                return !currentTime.plusMinutes(properties.durationOfFirstDeliveryInMinutes).isAfter(finishTime);
            } else {
                return !currentTime.plusMinutes(properties.durationOfDeliveryInMinutes).isAfter(finishTime);
            }
        }
    }

    private boolean canAddOrderToGroup(Order order, OrderGroup orderGroup, CourierProperties courierProperties) {
        if (orderGroup.getOrders().size() == courierProperties.orderGroupMaxOrders) return false;

        HashSet<Integer> regionsInOrderGroup = orderGroup.getOrders().stream()
                .map(Order::getRegion).collect(Collectors.toCollection(HashSet::new));

        if (regionsInOrderGroup.size() == courierProperties.orderGroupMaxRegions
                && !regionsInOrderGroup.contains(order.getRegion())) {
            return false;
        }

        return !(getOccupiedCapacity(orderGroup) + order.getWeight() > courierProperties.orderGroupMaxLoad);
    }

    private HashMap<WorkingHour, List<Order>> mapOrdersToWorkingHours(List<Order> orders, List<WorkingHour> workingHours) {
        return workingHours.stream()
                .collect(Collectors.toMap(
                        workingHour -> workingHour,
                        workingHour -> orders.stream()
                                .filter(order -> isWithinWorkingHour(order.getDeliveryHour(), workingHour))
                                .collect(Collectors.toList()),
                        (existing, replacement) -> existing,
                        HashMap::new
                ));
    }

    private boolean isWithinWorkingHour(DeliveryHour deliveryHour, WorkingHour workingHour) {
        return !deliveryHour.getStart().isBefore(workingHour.getStart())
                && !deliveryHour.getStart().isAfter(workingHour.getFinish());
    }

    private float getOccupiedCapacity(OrderGroup orderGroup) {
        float capacity = 0;
        for (Order order : orderGroup.getOrders()) {
            capacity += order.getWeight();
        }
        return capacity;
    }

    public List<OrderGroupDto> getOrderGroups(int limit, int offset) {
        return orderGroupRepository.findAll().stream().skip(offset).limit(limit).toList().stream()
                .map(orderGroupMapper::orderGroupToOrderGroupDto).collect(Collectors.toList());
    }

    private CourierProperties getCourierProperties(CourierType courierType) {
        int orderGroupMaxOrders;
        int orderGroupMaxRegions;
        int orderGroupMaxLoad;
        int durationOfFirstDeliveryInMinutes;
        int durationOfDeliveryInMinutes;

        switch (courierType) {
            case FOOT -> {
                orderGroupMaxOrders = 2;
                orderGroupMaxRegions = 1;
                orderGroupMaxLoad = 10;
                durationOfFirstDeliveryInMinutes = 25;
                durationOfDeliveryInMinutes = 10;
            }
            case BIKE -> {
                orderGroupMaxOrders = 4;
                orderGroupMaxRegions = 2;
                orderGroupMaxLoad = 20;
                durationOfFirstDeliveryInMinutes = 12;
                durationOfDeliveryInMinutes = 8;
            }
            case AUTO -> {
                orderGroupMaxOrders = 7;
                orderGroupMaxRegions = 3;
                orderGroupMaxLoad = 40;
                durationOfFirstDeliveryInMinutes = 8;
                durationOfDeliveryInMinutes = 4;
            }
            default -> throw new IllegalStateException("Unexpected value: " + courierType);
        }

        return new CourierProperties(orderGroupMaxOrders,
                orderGroupMaxRegions,
                orderGroupMaxLoad,
                durationOfFirstDeliveryInMinutes,
                durationOfDeliveryInMinutes);
    }

    private static class CourierProperties {
        int orderGroupMaxOrders;
        int orderGroupMaxRegions;
        int orderGroupMaxLoad;
        int durationOfFirstDeliveryInMinutes;
        int durationOfDeliveryInMinutes;

        public CourierProperties(int orderGroupMaxOrders,
                                 int orderGroupMaxRegions,
                                 int orderGroupMaxLoad,
                                 int durationOfFirstDeliveryInMinutes,
                                 int durationOfDeliveryInMinutes) {
            this.orderGroupMaxOrders = orderGroupMaxOrders;
            this.orderGroupMaxRegions = orderGroupMaxRegions;
            this.orderGroupMaxLoad = orderGroupMaxLoad;
            this.durationOfFirstDeliveryInMinutes = durationOfFirstDeliveryInMinutes;
            this.durationOfDeliveryInMinutes = durationOfDeliveryInMinutes;
        }
    }
}
