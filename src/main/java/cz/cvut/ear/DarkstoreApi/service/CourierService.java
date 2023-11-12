package cz.cvut.ear.DarkstoreApi.service;

import cz.cvut.ear.DarkstoreApi.dto.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface CourierService {

    ResponseEntity<List<CourierDto>> createCouriers(CreateCourierRequest createCourierRequest);

    ResponseEntity<List<CourierDto>> getCouriers(int limit, int offset);

    ResponseEntity<CourierDto> getCourier(long courierId);
}
