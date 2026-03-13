package seminars.factory;

import seminars.domain.satellites.Satellite;
import seminars.domain.satellites.SatelliteType;
import seminars.domain.parametrs.SatelliteParam;

public interface SatelliteFactory {
    Satellite createSatelliteWithParameter(SatelliteParam param);

    boolean isSatelliteTypeSupported(SatelliteType type);
}
