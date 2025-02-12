package com.cattleDB.LocationSimulationService.mappers;

import com.cattleDB.LocationSimulationService.models.Measurement;
import com.cattleDB.LocationSimulationService.models.Position;
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
public class PositionToMeasurementMapperImpl implements PositionToMeasurementMapper {

    @Override
    public Measurement toMeasurement(Position position) {
        if ( position == null ) {
            return null;
        }

        Measurement measurement = new Measurement();

        measurement.setValid( position.isValid() );
        if ( position.getDeviceName() != null ) {
            measurement.setDevice( position.getDeviceName() );
        }
        else {
            measurement.setDevice( "Unknown Device" );
        }
        measurement.setLatitude( position.getLatitude() );
        measurement.setLongitude( position.getLongitude() );
        measurement.setBeacon( position.getBeaconName() );
        measurement.setMajor( position.getBeaconMajor() );
        measurement.setMinor( position.getBeaconMinor() );
        measurement.setUuid( position.getBeaconUuid() );
        measurement.setSpeed( position.getSpeed() );

        measurement.setTime( position.getDeviceTime() != null ? position.getDeviceTime() : OffsetDateTime.now() );
        measurement.setGeofences( "AMS" );
        measurement.setAltitude( parseAltitude(position.getAttributes()) );
        measurement.setAttributes( convertAttributesToString(position.getAttributes()) );
        measurement.setCreatedAt( OffsetDateTime.now() );

        return measurement;
    }

    @Override
    public List<Measurement> toMeasurementList(List<Position> positions) {
        if ( positions == null ) {
            return null;
        }

        List<Measurement> list = new ArrayList<Measurement>( positions.size() );
        for ( Position position : positions ) {
            list.add( toMeasurement( position ) );
        }

        return list;
    }
}
