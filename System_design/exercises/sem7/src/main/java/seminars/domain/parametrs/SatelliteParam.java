package seminars.domain.parametrs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import seminars.domain.satellites.SatelliteType;

@AllArgsConstructor
@Getter
public abstract class SatelliteParam {
    private SatelliteType type;
    private String name;
    private double batteryLevel;
}
