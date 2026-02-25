package seminars.factory.Imp;

import org.springframework.stereotype.Component;
import seminars.domain.CommunicationSatellite;
import seminars.domain.ImagingSatellite;
import seminars.domain.Satellite;
import seminars.factory.SatelliteFactory;

@Component
public class ImagingSatelliteFactory implements SatelliteFactory {
    public static final double DEFAULT_RESOLUTION = 10.0;

    @Override
    public Satellite createSatellite(String name, double batteryLevel) {
        return new ImagingSatellite(name, batteryLevel, DEFAULT_RESOLUTION);
    }

    @Override
    public Satellite createSatelliteWithParameter(String name, double batteryLevel, double parameter) {
        return new ImagingSatellite(name, batteryLevel, parameter);
    }
}
