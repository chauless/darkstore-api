package cz.cvut.ear.DarkstoreApi.util.mapper;

import cz.cvut.ear.DarkstoreApi.dto.OrderDto;
import cz.cvut.ear.DarkstoreApi.dto.OrderGroupDto;
import cz.cvut.ear.DarkstoreApi.model.order.DeliveryHour;
import cz.cvut.ear.DarkstoreApi.model.order.Order;
import cz.cvut.ear.DarkstoreApi.model.order.OrderGroup;
import jdk.jfr.Name;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(uses = OrderMapper.class)
public interface OrderGroupMapper {
    OrderGroupMapper INSTANCE = Mappers.getMapper(OrderGroupMapper.class);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "courierId", source = "courier.id")
    @Mapping(source = "orders", target = "orders")
    OrderGroupDto orderGroupToOrderGroupDto(OrderGroup orderGroup);

    List<OrderGroupDto> orderGroupToOrderGroupDto(List<OrderGroup> orderGroups);

}
