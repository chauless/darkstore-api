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

    @Named("convertToDeliveryHour")
    static DeliveryHour convertToDeliveryHour(String deliveryHour) {
        String[] hours = deliveryHour.split("-");
        return new DeliveryHour(hours[0], hours[1]);
    }

    @Named("deliveryHourToString")
    static String deliveryHourToString(DeliveryHour deliveryHour) {
        return deliveryHour.getStart() + "-" + deliveryHour.getFinish();
    }

//    @Mapping(target = "id", ignore = true)
//    @Mapping(target = "orderGroup", ignore = true)
    @Mapping(source = "weight", target = "weight")
    @Mapping(source = "region", target = "region")
    @Mapping(source = "deliveryHour", target = "deliveryHour", qualifiedByName = "convertToDeliveryHour")
    @Mapping(source = "cost", target = "cost")
    Order createOrderDtoToOrder(CreateOrderDto createOrderDto);

//    default List<String> map(List<DeliveryHour> value) {
//        return value.stream().map(deliveryHour -> deliveryHour.getStart()
//                + "-" + deliveryHour.getFinish()).collect(Collectors.toList());
//    }

    @AfterMapping
    default void setOrder(@MappingTarget Order order) {
//        order.getDeliveryHour().forEach(deliveryHour -> deliveryHour.setOrder(order));
        order.getDeliveryHour().setOrder(order);
    }

    @Named("idToOrderId")
    static Long idToOrderId(long id) {
        return id;
    }

    @Mapping(source = "id", target = "orderId", qualifiedByName = "idToOrderId")
    @Mapping(source = "weight", target = "weight")
    @Mapping(source = "region", target = "region")
    @Mapping(source = "deliveryHour", target = "deliveryHour", qualifiedByName = "deliveryHourToString")
    @Mapping(source = "cost", target = "cost")
    @Mapping(source = "completeTime", target = "completeTime")
    OrderDto orderToOrderDto(Order order);


    List<Order> createOrderDtoToOrder(List<CreateOrderDto> createOrderDto);

    List<OrderDto> orderToOrderDto(List<Order> order);
}
