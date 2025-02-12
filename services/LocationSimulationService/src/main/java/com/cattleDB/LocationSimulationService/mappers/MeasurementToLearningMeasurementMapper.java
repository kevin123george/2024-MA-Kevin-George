package com.cattleDB.LocationSimulationService.mappers;



import com.cattleDB.LocationSimulationService.models.LearningMeasurement;
import com.cattleDB.LocationSimulationService.models.Measurement;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.time.OffsetDateTime;
import java.util.List;

@Mapper(componentModel = "spring", imports = OffsetDateTime.class)
public interface MeasurementToLearningMeasurementMapper {

    MeasurementToLearningMeasurementMapper INSTANCE = Mappers.getMapper(MeasurementToLearningMeasurementMapper.class);

    @Mapping(target = "id", ignore = true) // Let JPA handle the ID
    @Mapping(target = "wifiAp", expression = "java(measurement.getDevice() != null ? substringDevice(measurement.getDevice()) : \"Default-Wifi-AP\")")
    @Mapping(target = "cnxTime", source = "time")
    @Mapping(target = "clientId", expression = "java(extractClientId(measurement.getAttributes()))")
    @Mapping(target = "createdAt", expression = "java(OffsetDateTime.now())")
    LearningMeasurement toLearningMeasurement(Measurement measurement);

    // Map a list of Measurements to a list of LearningMeasurements
    List<LearningMeasurement> toLearningMeasurementList(List<Measurement> measurements);

    // Custom method to handle SUBSTRING(device, 1, 20) with default value
    default String substringDevice(String device) {
        if (device == null || device.isEmpty()) {
            return "Default-Wifi-AP"; // Default value if device is null or empty
        }
        return device.length() > 20 ? device.substring(0, 20) : device;
    }

    // Helper method for extracting clientId from the attributes field
    default String extractClientId(String attributes) {
        if (attributes == null || attributes.isEmpty()) {
            return "Unknown Client";
        }

        // Assuming attributes are in "key: value, key: value" format
        for (String pair : attributes.split(", ")) {
            String[] keyValue = pair.split(": ");
            if (keyValue.length == 2 && keyValue[0].trim().equalsIgnoreCase("clientId")) {
                return keyValue[1].trim();
            }
        }

        return "Unknown Client";
    }
}
