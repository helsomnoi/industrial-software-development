package seminars.services;

import seminars.domain.Satellite;

public interface SpaceOperationCenterService {
    void createAndSaveConstellation(String name);
    void addSatelliteToConstellation(String constellationName, Satellite satellite);
    void executeConstellationMissions(String constellationName);
    void activateAllSatellites(String constellationName);
    void showConstellationStatus(String constellationName);
}
