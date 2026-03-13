package seminars.services.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import seminars.aop.LogExecutionTime;
import seminars.domain.question.AddSatelliteRequest;
import seminars.domain.question.MissionRequest;
import seminars.domain.question.MissionTargetType;
import seminars.domain.satellites.Satellite;
import seminars.domain.parametrs.SatelliteParam;
import seminars.services.SpaceOperationCenterService;



@RequiredArgsConstructor
@Service
public class SpaceOperationCenterServiceImpl implements SpaceOperationCenterService {
    private final ConstellationServiceImpl constellationService;

    private final SatelliteServiceImpl satelliteService;

    @LogExecutionTime
    public void addSatellite(AddSatelliteRequest request) {
        try {
            constellationService.showConstellationStatus(request.constellationName());
        } catch(Exception e) {
            constellationService.createAndSaveConstellation(request.constellationName());
        }

        for (SatelliteParam param : request.satelliteParams()) {
            Satellite satellite = satelliteService.createSatellite(param);
            constellationService.addSatelliteToConstellation(request.constellationName(), satellite);
        }
    }

    @LogExecutionTime
    public void executeMission(MissionRequest request){
        switch (request.targetType()) {
            case CONSTELLATION -> {
                constellationService.activateAllSatellites(request.constellationName());
                constellationService.executeConstellationMissions(request.constellationName());
            }

            case  SINGLE_SATELLITE -> {
                var constellation = constellationService.getConstellation(request.constellationName());
                var satellite = constellation.getSatellites().stream()
                    .filter(s -> s.getName().equals(request.satelliteName()))
                        .findFirst()
                        .orElseThrow( () -> new RuntimeException("Спутник не найден: " + request.satelliteName()));
                satellite.activate();
                satellite.performMission();
            }
        }
    }
}
