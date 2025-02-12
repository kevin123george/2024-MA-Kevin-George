package com.cattleDB.LocationSimulationService.mappers;

import com.cattleDB.LocationSimulationService.models.LearningMeasurement;
import com.cattleDB.LocationSimulationService.models.Measurement;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-12-08T00:21:06+0100",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.2 (Oracle Corporation)"
)
@Component
public class MeasurementToLearningMeasurementMapperImpl implements MeasurementToLearningMeasurementMapper {

    @Override
    public LearningMeasurement toLearningMeasurement(Measurement measurement) {
        if ( measurement == null ) {
            return null;
        }

        LearningMeasurement learningMeasurement = new LearningMeasurement();

        learningMeasurement.setCnxTime( measurement.getTime() );

        learningMeasurement.setWifiAp( measurement.getDevice() != null ? substringDevice(measurement.getDevice()) : "Default-Wifi-AP" );
        learningMeasurement.setClientId( extractClientId(measurement.getAttributes()) );
        learningMeasurement.setCreatedAt( OffsetDateTime.now() );

        return learningMeasurement;
    }

    @Override
    public List<LearningMeasurement> toLearningMeasurementList(List<Measurement> measurements) {
        if ( measurements == null ) {
            return null;
        }

        List<LearningMeasurement> list = new ArrayList<LearningMeasurement>( measurements.size() );
        for ( Measurement measurement : measurements ) {
            list.add( toLearningMeasurement( measurement ) );
        }

        return list;
    }
}
