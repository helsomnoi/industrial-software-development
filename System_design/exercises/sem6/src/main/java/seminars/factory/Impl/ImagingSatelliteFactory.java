package seminars.factory.Impl;

import org.springframework.stereotype.Component;
import seminars.domain.ImagingSatellite;
import seminars.domain.Satellite;
import seminars.domain.SatelliteType;
import seminars.domain.parametrs.ImagingSatelliteParam;
import seminars.domain.parametrs.SatelliteParam;
import seminars.factory.SatelliteFactory;

@Component
public class ImagingSatelliteFactory implements SatelliteFactory {
    @Override
    public Satellite createSatelliteWithParameter(SatelliteParam param) {
        if (param instanceof ImagingSatelliteParam imagingParam) {
            return new ImagingSatellite(
                    imagingParam.getName(),
                    imagingParam.getBatteryLevel(),
                    imagingParam.getResolution()
                    );
        }
        throw new RuntimeException("Данный тип параметров не поддерживается");
    }

    @Override
    public boolean isSatelliteTypeSupported(SatelliteType type){
        return SatelliteType.IMAGE.equals(type);
    }
}
