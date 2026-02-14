package seminars.services;

import seminars.Satellite;
import seminars.SatelliteConstellation;
import seminars.repository.ConstellationRepository;

import org.springframework.stereotype.Service;

@Service
public class SpaceOperationCenterService {
    private final ConstellationRepository repository;

    public SpaceOperationCenterService(ConstellationRepository repository) {
        this.repository = repository;
    }

    public void createAndSaveConstellation(String name){
        SatelliteConstellation constellation = new SatelliteConstellation(name);
        repository.addConstellation(constellation);
    }

    public void addSatelliteToConstellation(String constellationName, Satellite satellite){
        SatelliteConstellation constellation = repository.getConstellation(constellationName);
        constellation.addSatellite(satellite);
        System.out.println("Добавлен спутник " + satellite.getName() +
                " в группировку " + constellationName);
    }

    public void executeConstellationMissions(String constellationName){
        SatelliteConstellation constellation = repository.getConstellation(constellationName);
        System.out.println("\n=== ВЫПОЛНЕНИЕ МИССИЙ ГРУППИРОВКИ: " + constellationName + " ===");
        constellation.executeAllMission();
    }

    public void activateAllSatellites(String constellationName){
        SatelliteConstellation constellation = repository.getConstellation(constellationName);
        System.out.println("\n=== АКТИВАЦИЯ СПУТНИКОВ В ГРУППИРОВКЕ: " + constellationName + " ===");

        for (Satellite satellite : constellation.getSatellites()){
            satellite.activate();
        }
    }

    public void showConstellationStatus(String constellationName){
        SatelliteConstellation constellation = repository.getConstellation(constellationName);
        System.out.println("\n=== СТАТУС ГРУППИРОВКИ: " + constellationName + " ===");

        System.out.println("Количество спутников: " + constellation.getConstellationName());

        for (Satellite satellite : constellation.getSatellites()){
            System.out.println(satellite.getState());
        }
    }

}
