package seminars.factory.Impl;

import org.springframework.stereotype.Component;
import seminars.domain.CommunicationSatellite;
import seminars.domain.ImagingSatellite;
import seminars.domain.Satellite;
import seminars.domain.SatelliteType;
import seminars.domain.parametrs.CommunicationSatelliteParam;
import seminars.domain.parametrs.SatelliteParam;
import seminars.factory.SatelliteFactory;

@Component
public class CommunicationSatelliteFactory implements SatelliteFactory {
    @Override
    public Satellite createSatelliteWithParameter(SatelliteParam param) {
        if (param instanceof CommunicationSatelliteParam communicationParam) {
            return new CommunicationSatellite(
                    communicationParam.getName(),
                    communicationParam.getBatteryLevel(),
                    communicationParam.getBandwidth()
            );
        }
        throw new RuntimeException("Данный тип параметров не поддерживается");
    }

    @Override
    public boolean isSatelliteTypeSupported(SatelliteType type){
        return SatelliteType.COMMUNICATION.equals(type);
    }
}
