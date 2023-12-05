package cz.cvut.ear.DarkstoreApi.util.mapper;

import cz.cvut.ear.DarkstoreApi.dto.order.OrderGroupDto;
import cz.cvut.ear.DarkstoreApi.model.order.OrderGroup;
import org.mapstruct.*;

import java.util.List;

@Mapper(uses = OrderMapper.class)
public interface OrderGroupMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "courierId", source = "courier.id")
    @Mapping(source = "orders", target = "orders")
    OrderGroupDto orderGroupToOrderGroupDto(OrderGroup orderGroup);

    List<OrderGroupDto> orderGroupToOrderGroupDto(List<OrderGroup> orderGroups);

}
