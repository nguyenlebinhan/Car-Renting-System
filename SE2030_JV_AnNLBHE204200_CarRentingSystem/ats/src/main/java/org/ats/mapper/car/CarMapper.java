package org.ats.mapper.car;

import org.ats.dto.car.response.CarDTO;
import org.ats.entities.car.Car;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CarMapper {

    @Mapping(source = "carProducer.producerName",target = "carProducerName")
    @Mapping(source = "carProducer.producerId", target = "producerId")
    CarDTO toCarDTO(Car car);
}
