package seminars.factory;

import seminars.domain.Satellite;
import seminars.domain.SatelliteState;
import seminars.domain.SatelliteType;
import seminars.domain.parametrs.SatelliteParam;

public interface SatelliteFactory {
    Satellite createSatelliteWithParameter(SatelliteParam param);

    boolean isSatelliteTypeSupported(SatelliteType type);
}
