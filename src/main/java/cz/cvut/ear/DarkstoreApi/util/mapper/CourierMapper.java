package cz.cvut.ear.DarkstoreApi.util.mapper;

import cz.cvut.ear.DarkstoreApi.dto.user.courier.CourierDto;
import cz.cvut.ear.DarkstoreApi.dto.user.courier.createCourier.CreateCourierDto;
import cz.cvut.ear.DarkstoreApi.model.courier.Courier;
import cz.cvut.ear.DarkstoreApi.model.courier.CourierRegion;
import cz.cvut.ear.DarkstoreApi.model.courier.WorkingHour;
import org.mapstruct.*;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Mapper
public interface CourierMapper {

    @Named("integerToCourierRegion")
    static List<CourierRegion> integerToCourierRegion(List<Integer> regions) {
        return regions.stream().map(CourierRegion::new).collect(Collectors.toList());
    }

    @Named("convertToWorkingHours")
    static List<WorkingHour> convertToWorkingHours(List<String> workingHours) {
        return workingHours.stream().map(workingHour -> {
            String[] hours = workingHour.split("-");
            return new WorkingHour(LocalTime.parse(hours[0]), LocalTime.parse(hours[1]));
        }).collect(Collectors.toList());
    }

    @Named("courierRegionToInteger")
    static List<Integer> courierRegionToInteger(List<CourierRegion> regions) {
        return regions.stream().map(CourierRegion::getRegion).collect(Collectors.toList());
    }

    @Named("workingHourToString")
    static List<String> workingHourToString(List<WorkingHour> workingHours) {
        return workingHours.stream().map(workingHour -> workingHour.getStart()
                + "-" + workingHour.getFinish()).collect(Collectors.toList());
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orderGroups", ignore = true)
    @Mapping(source = "type", target = "type")
    @Mapping(source = "regions", target = "regions", qualifiedByName = "integerToCourierRegion")
    @Mapping(source = "workingHours", target = "workingHours", qualifiedByName = "convertToWorkingHours")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "password", target = "password")
    Courier createCourierDtoToCourier(CreateCourierDto createCourierDto);

    @AfterMapping
    default void setCourier(@MappingTarget Courier courier) {
        courier.getWorkingHours().forEach(workingHour -> workingHour.setCourier(courier));
    }

    @Mapping(source = "id", target = "courierId")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "type", target = "type")
    @Mapping(source = "regions", target = "regions", qualifiedByName = "courierRegionToInteger")
    @Mapping(source = "workingHours", target = "workingHours", qualifiedByName = "workingHourToString")
    CourierDto courierToCourierDto(Courier courier);

    List<Courier> createCourierDtoToCourier(List<CreateCourierDto> createCourierDtos);

    List<CourierDto> courierToCourierDto(List<Courier> couriers);
}