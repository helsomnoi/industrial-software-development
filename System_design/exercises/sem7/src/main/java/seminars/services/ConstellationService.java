package seminars.services;

import seminars.domain.satellites.Satellite;
import seminars.domain.satellites.SatelliteConstellation;

import java.util.Map;

public interface ConstellationService {
    void createAndSaveConstellation(String name);
    void addSatelliteToConstellation(String constellationName, Satellite satellite);
    void executeConstellationMissions(String constellationName);
    void activateAllSatellites(String constellationName);
    void showConstellationStatus(String constellationName);
    SatelliteConstellation getConstellation(String constellationName);
    Map<String, SatelliteConstellation> getAllConstellations();
}
