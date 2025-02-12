package com.cattleDB.LocationSimulationService.mappers;

import com.cattleDB.LocationSimulationService.models.Measurement;
import com.cattleDB.LocationSimulationService.models.Position;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring", imports = OffsetDateTime.class)
public interface PositionToMeasurementMapper {

    PositionToMeasurementMapper INSTANCE = Mappers.getMapper(PositionToMeasurementMapper.class);

    @Mapping(target = "id", ignore = true) // Let JPA handle the ID
    @Mapping(target = "valid", source = "valid")
    @Mapping(target = "device", source = "deviceName", defaultValue = "Unknown Device")
    @Mapping(target = "time", expression = "java(position.getDeviceTime() != null ? position.getDeviceTime() : OffsetDateTime.now())")
    @Mapping(target = "geofences", constant = "AMS")
    @Mapping(target = "latitude", source = "latitude")
    @Mapping(target = "longitude", source = "longitude")
    @Mapping(target = "altitude", expression = "java(parseAltitude(position.getAttributes()))")
    @Mapping(target = "beacon", source = "beaconName")
    @Mapping(target = "major", source = "beaconMajor")
    @Mapping(target = "minor", source = "beaconMinor")
    @Mapping(target = "uuid", source = "beaconUuid")
    @Mapping(target = "speed", source = "speed")
    @Mapping(target = "attributes", expression = "java(convertAttributesToString(position.getAttributes()))")
    @Mapping(target = "createdAt", expression = "java(OffsetDateTime.now())")
    Measurement toMeasurement(Position position);

    // New method for mapping a list of Position objects
    List<Measurement> toMeasurementList(List<Position> positions);

    // Helper methods for custom mappings
    default Double parseAltitude(Map<String, String> attributes) {
        String altitudeStr = attributes.getOrDefault("altitude", "0.0");
        return Double.parseDouble(altitudeStr);
    }

    default String convertAttributesToString(Map<String, String> attributes) {
        if (attributes == null || attributes.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        attributes.forEach((key, value) -> sb.append(key).append(": ").append(value).append(", "));
        // Remove the trailing comma and space
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 2);
        }
        return sb.toString();
    }
}
