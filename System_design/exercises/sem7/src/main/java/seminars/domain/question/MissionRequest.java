package seminars.domain.question;

import seminars.domain.parametrs.SatelliteParam;
import seminars.domain.satellites.SatelliteType;

import java.util.List;
import java.util.Set;

public record MissionRequest(String constellationName,
                             String satelliteName,
                             MissionTargetType targetType,
                             Set<SatelliteType> targetTypes)  {}

