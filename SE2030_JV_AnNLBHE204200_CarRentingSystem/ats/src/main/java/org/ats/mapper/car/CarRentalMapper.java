package org.ats.mapper.car;

import org.ats.dto.carRental.response.CarRentalDTO;
import org.ats.entities.car.CarRental;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface CarRentalMapper {


    @Mapping(target = "customerId",source = "customer.customerId")
    @Mapping(target = "carId",source = "car.carId")
    @Mapping(target = "carName",source = "car.carName")
    @Mapping(target = "producerName",source = "car.carProducer.producerName")
    @Mapping(target = "carModelYear",source = "car.carModelYear")
    @Mapping(target = "color",source = "car.color")
    @Mapping(target = "capacity",source = "car.capacity")
    @Mapping(target = "description",source = "car.description")
    @Mapping(target = "customerName",source = "customer.fullName")
    CarRentalDTO toDTO(CarRental carRental);
}
