package seminars;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import seminars.domain.Satellite;
import seminars.factory.Imp.CommunicationSatelliteFactory;
import seminars.factory.Imp.ImagingSatelliteFactory;
import seminars.repository.ConstellationRepository;
import seminars.services.SpaceOperationCenterService;

@SpringBootApplication
public class SatelliteApplication implements CommandLineRunner {

    private final ConstellationRepository constellationRepository;
    private final SpaceOperationCenterService operationService;
    private final CommunicationSatelliteFactory communicationSatelliteFactory;
    private final ImagingSatelliteFactory imagingSatelliteFactory;

    public SatelliteApplication(ConstellationRepository constellationRepository,
                       SpaceOperationCenterService operationService,
                       CommunicationSatelliteFactory communicationSatelliteFactory,
                       ImagingSatelliteFactory imagingSatelliteFactory) {
        this.constellationRepository = constellationRepository;
        this.operationService = operationService;
        this.communicationSatelliteFactory = communicationSatelliteFactory;
        this.imagingSatelliteFactory = imagingSatelliteFactory;
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

        Satellite communication_1 = communicationSatelliteFactory.createSatelliteWithParameter("Связь-1", 0.85, 500);
        Satellite communication_2 = communicationSatelliteFactory.createSatelliteWithParameter("Связь-2", 0.75, 1000);

        Satellite imaging_1 = imagingSatelliteFactory.createSatelliteWithParameter("ДЗЗ-1", 0.92, 2.5);
        Satellite imaging_2 = imagingSatelliteFactory.createSatelliteWithParameter("ДЗЗ-2", 0.45, 1);
        Satellite imaging_3 = imagingSatelliteFactory.createSatelliteWithParameter("ДЗЗ-3", 0.15, 0.5);
        System.out.println("-".repeat(15));

        operationService.createAndSaveConstellation("Орбита-1");
        operationService.createAndSaveConstellation("Орбита-2");

        System.out.println("-".repeat(15));
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
