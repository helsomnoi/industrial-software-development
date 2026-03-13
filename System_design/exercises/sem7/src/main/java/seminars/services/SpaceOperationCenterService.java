package seminars.services;

import seminars.domain.question.AddSatelliteRequest;
import seminars.domain.question.MissionRequest;

public interface SpaceOperationCenterService {
    void addSatellite(AddSatelliteRequest request);
    void executeMission(MissionRequest request);
}
