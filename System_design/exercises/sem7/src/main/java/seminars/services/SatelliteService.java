package seminars.services;

import seminars.domain.satellites.Satellite;
import seminars.domain.parametrs.SatelliteParam;

public interface SatelliteService {
    Satellite createSatellite(SatelliteParam param);
}
