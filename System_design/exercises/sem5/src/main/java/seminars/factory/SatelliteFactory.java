package seminars.factory;

import seminars.domain.Satellite;
import seminars.domain.SatelliteState;

public interface SatelliteFactory {
    Satellite createSatellite(String name, double batteryLevel);

    Satellite createSatelliteWithParameter(String name, double batteryLevel, double parameter);
}
