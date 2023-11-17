package cz.cvut.ear.DarkstoreApi.util;

import cz.cvut.ear.DarkstoreApi.dto.CourierDto;
import cz.cvut.ear.DarkstoreApi.dto.CreateCourierDto;
import cz.cvut.ear.DarkstoreApi.model.courier.Courier;
import cz.cvut.ear.DarkstoreApi.model.courier.CourierRegion;
import cz.cvut.ear.DarkstoreApi.model.courier.WorkingHour;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

public class CourierMapper {
    public static Courier mapToCourier(CreateCourierDto createCourierDto) {
        Courier courier = new Courier();
        courier.setType(createCourierDto.getType());
        courier.setWorkingHours(convertToWorkingHours(createCourierDto.getWorkingHour(), courier));
        courier.setRegions(convertToCourierRegions(createCourierDto.getRegions()));
        courier.setEmail(createCourierDto.getEmail());
        courier.setPassword(createCourierDto.getPassword());
        return courier;
    }

    private static List<WorkingHour> convertToWorkingHours(List<String> workingHours, Courier courier) {
        return workingHours.stream().map(workingHour -> {
            String[] hours = workingHour.split("-");
            return new WorkingHour(LocalTime.parse(hours[0]), LocalTime.parse(hours[1]), courier);
        }).collect(Collectors.toList());
    }

    private static List<CourierRegion> convertToCourierRegions(List<Integer> regions) {
        return regions.stream()
                .map(CourierRegion::new)
                .collect(Collectors.toList());
    }

    public static CourierDto mapToDto(Courier courier) {
        CourierDto courierDto = new CourierDto();
        courierDto.setCourierId(courier.getId());
        courierDto.setType(courier.getType());
        courierDto.setWorkingHour(courier.getWorkingHours().stream()
                .map(workingHour -> workingHour.getStart() + "-" + workingHour.getFinish())
                .collect(Collectors.toList()));
        courierDto.setRegions(courier.getRegions().stream()
                .map(CourierRegion::getRegion)
                .collect(Collectors.toList()));
        courierDto.setEmail(courier.getEmail());
        return courierDto;
    }
}
