package cz.cvut.ear.DarkstoreApi.service;

import cz.cvut.ear.DarkstoreApi.dto.*;

public interface CourierService {

    CreateCourierResponse createCouriers(CreateCourierRequest createCourierRequest);

    GetCouriersResponse getCouriers(int limit, int offset);

    CourierDto getCourier(long courierId);
}
