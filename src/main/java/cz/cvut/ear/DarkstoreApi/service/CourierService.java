package cz.cvut.ear.DarkstoreApi.service;

import cz.cvut.ear.DarkstoreApi.dto.CourierDto;
import cz.cvut.ear.DarkstoreApi.dto.CreateCourierRequest;
import cz.cvut.ear.DarkstoreApi.model.Role;
import cz.cvut.ear.DarkstoreApi.model.courier.Courier;
import cz.cvut.ear.DarkstoreApi.repository.CourierRepository;
import cz.cvut.ear.DarkstoreApi.util.mapper.CourierMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class CourierService {
    private final CourierRepository courierRepository;
    private final CourierMapper courierMapper;

    public ResponseEntity<List<CourierDto>> createCouriers(CreateCourierRequest createCourierRequest) {
        List<Courier> savedCouriers = courierMapper.createCourierDtoToCourier(createCourierRequest.getCouriers());

        savedCouriers.forEach(courier -> courier.setRole(Role.ROLE_COURIER));

        savedCouriers = courierRepository.saveAll(savedCouriers);

        List<CourierDto> courierDtos = courierMapper.courierToCourierDto(savedCouriers);

        return ResponseEntity.ok(courierDtos);
    }

    public ResponseEntity<List<CourierDto>> getCouriers(int limit, int offset) {
        return ResponseEntity.ok(courierMapper.courierToCourierDto(courierRepository.findAll().stream()
                .skip(offset)
                .limit(limit)
                .toList()));
    }

    public ResponseEntity<CourierDto> getCourier(long courierId) {
        return courierRepository.findById(courierId)
                .map(courier -> ResponseEntity.ok(courierMapper.courierToCourierDto(courier)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
