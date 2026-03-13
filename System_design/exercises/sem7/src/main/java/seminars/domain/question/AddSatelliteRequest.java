package seminars.domain.question;

import seminars.domain.parametrs.SatelliteParam;

import java.util.List;

public record AddSatelliteRequest(String constellationName, List<SatelliteParam> satelliteParams)  {}
