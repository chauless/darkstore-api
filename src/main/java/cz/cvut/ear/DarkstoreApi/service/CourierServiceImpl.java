package cz.cvut.ear.DarkstoreApi.service;

import cz.cvut.ear.DarkstoreApi.dto.CourierDto;
import cz.cvut.ear.DarkstoreApi.dto.CreateCourierRequest;
import cz.cvut.ear.DarkstoreApi.exception.CourierNotFoundException;
import cz.cvut.ear.DarkstoreApi.model.Role;
import cz.cvut.ear.DarkstoreApi.model.courier.Courier;
import cz.cvut.ear.DarkstoreApi.repository.CourierRepository;
import cz.cvut.ear.DarkstoreApi.util.CourierMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class CourierServiceImpl implements CourierService {
    private final CourierRepository courierRepository;

    @Override
    public ResponseEntity<List<CourierDto>> createCouriers(CreateCourierRequest createCourierRequest) {
        List<Courier> savedCouriers = createCourierRequest.getCouriers().stream()
                .map(CourierMapper::mapToCourier)
                .peek(courier -> courier.setRole(Role.ROLE_COURIER))
                .collect(Collectors.toList());

        savedCouriers = courierRepository.saveAll(savedCouriers);

        List<CourierDto> courierDtos = savedCouriers.stream()
                .map(CourierMapper::mapToDto)
                .toList();

        return ResponseEntity.ok(courierDtos);
    }

    @Override
    public ResponseEntity<List<CourierDto>> getCouriers(int limit, int offset) {
        return ResponseEntity.ok(courierRepository.findAll().stream()
                .map(CourierMapper::mapToDto)
                .toList());
    }

    @Override
    public ResponseEntity<CourierDto> getCourier(long courierId) {
        return courierRepository.findById(courierId)
                .map(courier -> ResponseEntity.ok(CourierMapper.mapToDto(courier)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
