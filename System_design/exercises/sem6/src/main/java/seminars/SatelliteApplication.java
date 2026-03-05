package seminars;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import seminars.domain.Satellite;
import seminars.domain.parametrs.CommunicationSatelliteParam;
import seminars.domain.parametrs.ImagingSatelliteParam;
import seminars.repository.ConstellationRepository;
import seminars.services.SatelliteService;
import seminars.services.SpaceOperationCenterService;

@SpringBootApplication
public class SatelliteApplication implements CommandLineRunner {

    private final SatelliteService satelliteService;
    private final SpaceOperationCenterService operationService;
    private final ConstellationRepository constellationRepository;

    public SatelliteApplication(SatelliteService satelliteService,
                                SpaceOperationCenterService operationService,
                                ConstellationRepository constellationRepository) {
        this.satelliteService = satelliteService;
        this.operationService = operationService;
        this.constellationRepository = constellationRepository;
    }

    public static void main(String[] args) {
        SpringApplication.run(SatelliteApplication.class, args);
    }

    @Override
    public void run(String... args) {
        System.out.println("ЗАПУСК СИСТЕМЫ УПРАВЛЕНИЯ СПУТНИКОВОЙ ГРУППИРОВКОЙ");
        System.out.println("=".repeat(25));

        System.out.println("СОЗДАНИЕ СПЕЦИАЛИЗИРОВАННЫХ СПУТНИКОВ:");
        System.out.println("-".repeat(15));

        Satellite communication_1 = satelliteService.createSatellite(
                new CommunicationSatelliteParam("Связь-1", 0.85, 500)
        );
        Satellite communication_2 = satelliteService.createSatellite(
                new CommunicationSatelliteParam("Связь-2", 0.75, 1000)
        );

        Satellite imaging_1 = satelliteService.createSatellite(
                new ImagingSatelliteParam("ДЗЗ-1", 0.92, 2.5)
        );
        Satellite imaging_2 = satelliteService.createSatellite(
                new ImagingSatelliteParam("ДЗЗ-2", 0.45, 1.0)
        );
        Satellite imaging_3 = satelliteService.createSatellite(
                new ImagingSatelliteParam("ДЗЗ-3", 0.15, 0.5)
        );
        System.out.println("-".repeat(15));

        operationService.createAndSaveConstellation("Орбита-1");
        operationService.createAndSaveConstellation("Орбита-2");

        System.out.println("ДОБАВЛЕНИЕ СПУТНИКОВ:");
        operationService.addSatelliteToConstellation("Орбита-1", communication_1);
        operationService.addSatelliteToConstellation("Орбита-1", imaging_1);
        operationService.addSatelliteToConstellation("Орбита-1", imaging_2);
        operationService.addSatelliteToConstellation("Орбита-2", communication_2);
        operationService.addSatelliteToConstellation("Орбита-2", imaging_3);
        System.out.println("-".repeat(10));

        operationService.showConstellationStatus("Орбита-1");
        operationService.showConstellationStatus("Орбита-2");

        operationService.activateAllSatellites("Орбита-1");
        operationService.executeConstellationMissions("Орбита-1");
        operationService.showConstellationStatus("Орбита-1");

        System.out.println(constellationRepository.getAllConstellations());
    }
}