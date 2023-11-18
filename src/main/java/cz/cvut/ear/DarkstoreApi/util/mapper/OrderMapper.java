package cz.cvut.ear.DarkstoreApi.util.mapper;

import cz.cvut.ear.DarkstoreApi.dto.CreateOrderDto;
import cz.cvut.ear.DarkstoreApi.dto.OrderDto;
import cz.cvut.ear.DarkstoreApi.model.order.DeliveryHour;
import cz.cvut.ear.DarkstoreApi.model.order.Order;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

@Mapper
public interface OrderMapper {
    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "weight", target = "weight")
    @Mapping(source = "region", target = "region")
    @Mapping(source = "deliveryHours", target = "deliveryHours", qualifiedByName = "convertToDeliveryHours")
    @Mapping(source = "cost", target = "cost")
    Order createOrderDtoToOrder(CreateOrderDto createOrderDto);

    @Named("convertToDeliveryHours")
    static List<DeliveryHour> convertToDeliveryHours(List<String> deliveryHours) {
        return deliveryHours.stream().map(deliveryHour -> {
            String[] hours = deliveryHour.split("-");
            return new DeliveryHour(hours[0], hours[1]);
        }).collect(Collectors.toList());
    }

    @AfterMapping
    default void setOrder(@MappingTarget Order order) {
        order.getDeliveryHours().forEach(deliveryHour -> deliveryHour.setOrder(order));
    }

    @Mapping(source = "id", target = "orderId")
    @Mapping(source = "weight", target = "weight")
    @Mapping(source = "region", target = "region")
    @Mapping(source = "deliveryHours", target = "deliveryHours", qualifiedByName = "deliveryHoursToString")
    @Mapping(source = "cost", target = "cost")
    @Mapping(source = "completeTime", target = "completeTime")
    OrderDto orderToOrderDto(Order order);

    @Named("deliveryHoursToString")
    static List<String> deliveryHoursToString(List<DeliveryHour> deliveryHours) {
        return deliveryHours.stream().map(deliveryHour -> deliveryHour.getStart() + "-" + deliveryHour.getFinish()).collect(Collectors.toList());
    }




    List<Order> createOrderDtoToOrder(List<CreateOrderDto> createOrderDto);

    List<OrderDto> orderToOrderDto(List<Order> order);
}
