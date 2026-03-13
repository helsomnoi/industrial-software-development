package seminars.services.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import seminars.domain.satellites.Satellite;
import seminars.domain.parametrs.SatelliteParam;
import seminars.factory.SatelliteFactory;
import seminars.services.SatelliteService;

import java.util.List;

@RequiredArgsConstructor
@Service
public class SatelliteServiceImpl implements SatelliteService {
    private final List<SatelliteFactory> factories;

    @Override
    public Satellite createSatellite(SatelliteParam param){
        SatelliteFactory factory = factories.stream()
                .filter(satelliteFactory -> satelliteFactory
                        .isSatelliteTypeSupported(param.getType()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Данный тип параметров не поддержривается"));
        return factory.createSatelliteWithParameter(param);
    }
}
