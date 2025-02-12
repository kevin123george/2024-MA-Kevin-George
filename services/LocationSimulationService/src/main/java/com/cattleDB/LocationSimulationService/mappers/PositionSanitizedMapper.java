package com.cattleDB.LocationSimulationService.mappers;

import com.cattleDB.LocationSimulationService.models.Measurement;
import com.cattleDB.LocationSimulationService.models.PositionSanitized;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface PositionSanitizedMapper {

    PositionSanitizedMapper INSTANCE = Mappers.getMapper(PositionSanitizedMapper.class);

    @Mapping(source = "deviceName", target = "device")
    @Mapping(source = "deviceTime", target = "time")
    @Mapping(source = "latitude", target = "latitude")
    @Mapping(source = "longitude", target = "longitude")
    @Mapping(source = "speed", target = "speed")
    @Mapping(source = "valid", target = "valid")
    @Mapping(source = "beaconName", target = "beacon")
    @Mapping(source = "beaconMajor", target = "major")
    @Mapping(source = "beaconMinor", target = "minor")
    @Mapping(source = "beaconUuid", target = "uuid")
    @Mapping(expression = "java(positionSanitized.getAttributes() != null ? positionSanitized.getAttributes().toString() : null)", target = "attributes")
    Measurement mapToMeasurement(PositionSanitized positionSanitized);

    List<Measurement> mapToMeasurements(List<PositionSanitized> positionSanitizedList);
}