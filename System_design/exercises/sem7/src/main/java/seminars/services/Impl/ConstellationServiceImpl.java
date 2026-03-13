package seminars.services.Impl;

import org.springframework.stereotype.Service;
import seminars.domain.satellites.Satellite;
import seminars.domain.satellites.SatelliteConstellation;
import seminars.repository.ConstellationRepository;
import seminars.services.ConstellationService;
import seminars.services.SpaceOperationCenterService;

import java.util.List;
import java.util.Map;

@Service
public class ConstellationServiceImpl implements ConstellationService {
    private final ConstellationRepository repository;

    public ConstellationServiceImpl(ConstellationRepository repository) {
        this.repository = repository;
    }

    @Override
    public void createAndSaveConstellation(String name){
        SatelliteConstellation constellation = new SatelliteConstellation(name);
        repository.addConstellation(constellation);
    }

    @Override
    public void addSatelliteToConstellation(String constellationName, Satellite satellite){
        SatelliteConstellation constellation = repository.getConstellation(constellationName);
        constellation.addSatellite(satellite);
        System.out.println("Добавлен спутник " + satellite.getName() +
                " в группировку " + constellationName);
    }

    @Override
    public void executeConstellationMissions(String constellationName){
        SatelliteConstellation constellation = repository.getConstellation(constellationName);
        System.out.println("\n=== ВЫПОЛНЕНИЕ МИССИЙ ГРУППИРОВКИ: " + constellationName + " ===");
        constellation.executeAllMission();
    }

    @Override
    public void activateAllSatellites(String constellationName){
        SatelliteConstellation constellation = repository.getConstellation(constellationName);
        System.out.println("\n=== АКТИВАЦИЯ СПУТНИКОВ В ГРУППИРОВКЕ: " + constellationName + " ===");

        for (Satellite satellite : constellation.getSatellites()){
            satellite.activate();
        }
    }

    @Override
    public void showConstellationStatus(String constellationName){
        SatelliteConstellation constellation = repository.getConstellation(constellationName);
        System.out.println("\n=== СТАТУС ГРУППИРОВКИ: " + constellationName + " ===");
        List<Satellite> satellites = constellation.getSatellites();
        System.out.println("Количество спутников: " + satellites.size());
        for (Satellite satellite : satellites){
            System.out.println(satellite.getState());
        }
    }

    public SatelliteConstellation getConstellation(String constellationName){
        return this.repository.getConstellation(constellationName);
    }

    public Map<String, SatelliteConstellation> getAllConstellations(){
        return this.repository.getAllConstellations();
    }
}
