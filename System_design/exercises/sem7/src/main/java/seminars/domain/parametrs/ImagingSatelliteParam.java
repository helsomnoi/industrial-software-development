package seminars.domain.parametrs;

import lombok.Getter;
import seminars.domain.satellites.SatelliteType;

@Getter
public class ImagingSatelliteParam extends SatelliteParam {
    private double resolution;

    public ImagingSatelliteParam(String name, double batteryLevel, double resolution) {
        super(SatelliteType.IMAGE, name, batteryLevel);
        this.resolution = resolution;
    }
}
