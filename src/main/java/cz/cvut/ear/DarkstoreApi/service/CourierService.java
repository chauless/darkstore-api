package cz.cvut.ear.DarkstoreApi.service;

import cz.cvut.ear.DarkstoreApi.dto.CourierDto;
import cz.cvut.ear.DarkstoreApi.dto.CreateCourierRequest;
import cz.cvut.ear.DarkstoreApi.exception.CourierNotFoundException;
import cz.cvut.ear.DarkstoreApi.model.Role;
import cz.cvut.ear.DarkstoreApi.model.courier.Courier;
import cz.cvut.ear.DarkstoreApi.repository.CourierRepository;
import cz.cvut.ear.DarkstoreApi.util.mapper.CourierMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class CourierService {
    private final CourierRepository courierRepository;
    private final CourierMapper courierMapper;

    @Transactional
    public List<CourierDto> createCouriers(CreateCourierRequest createCourierRequest) {
        List<Courier> savedCouriers = courierMapper.createCourierDtoToCourier(createCourierRequest.getCouriers());

        savedCouriers.forEach(courier -> courier.setRole(Role.ROLE_COURIER));

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
}
