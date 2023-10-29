package cz.cvut.ear.DarkstoreApi.service;

import cz.cvut.ear.DarkstoreApi.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class CourierServiceImpl implements CourierService {

    @Override
    public CreateCourierResponse createCouriers(CreateCourierRequest createCourierRequest) {
        return null;
    }

    @Override
    public GetCouriersResponse getCouriers(int limit, int offset) {
        return null;
    }

    @Override
    public CourierDto getCourier(long courierId) {
        return null;
    }
}
