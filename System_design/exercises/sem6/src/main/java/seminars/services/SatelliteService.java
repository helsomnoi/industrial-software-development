package seminars.services;

import seminars.domain.Satellite;
import seminars.domain.parametrs.SatelliteParam;

public interface SatelliteService {
    Satellite createSatellite(SatelliteParam param);
}
