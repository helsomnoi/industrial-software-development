package seminars;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import seminars.domain.parametrs.CommunicationSatelliteParam;
import seminars.domain.parametrs.ImagingSatelliteParam;
import seminars.domain.question.AddSatelliteRequest;
import seminars.domain.question.MissionRequest;
import seminars.domain.question.MissionTargetType;
import seminars.domain.satellites.SatelliteType;
import seminars.repository.ConstellationRepository;
import seminars.services.SpaceOperationCenterService;

import java.util.List;
import java.util.Set;

@SpringBootApplication
public class SatelliteApplication implements CommandLineRunner {

    private final SpaceOperationCenterService operationService;
    private final ConstellationRepository constellationRepository;

    public SatelliteApplication(SpaceOperationCenterService operationService,
                                ConstellationRepository constellationRepository) {
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

        // Добавление спутников в группировку "Орбита-1"
        System.out.println("ДОБАВЛЕНИЕ СПУТНИКОВ В ГРУППИРОВКУ 'Орбита-1':");
        var orbit1Request = new AddSatelliteRequest("Орбита-1", List.of(
                new CommunicationSatelliteParam("Связь-1", 0.85, 500),
                new ImagingSatelliteParam("ДЗЗ-1", 0.92, 2.5),
                new ImagingSatelliteParam("ДЗЗ-2", 0.45, 1.0)
        ));
        operationService.addSatellite(orbit1Request);

        // Добавление спутников в группировку "Орбита-2"
        System.out.println("ДОБАВЛЕНИЕ СПУТНИКОВ В ГРУППИРОВКУ 'Орбита-2':");
        var orbit2Request = new AddSatelliteRequest("Орбита-2", List.of(
                new CommunicationSatelliteParam("Связь-2", 0.75, 1000),
                new ImagingSatelliteParam("ДЗЗ-3", 0.15, 0.5)
        ));
        operationService.addSatellite(orbit2Request);

        // Вывод состояния группировок
        System.out.println("\nСОСТОЯНИЕ ГРУППИРОВОК ПОСЛЕ ДОБАВЛЕНИЯ:");
        printConstellationStatus("Орбита-1");
        printConstellationStatus("Орбита-2");

        // Выполнение миссии для всей группировки "Орбита-1"
        System.out.println("\nВЫПОЛНЕНИЕ МИССИИ ДЛЯ ГРУППИРОВКИ 'Орбита-1':");
        MissionRequest missionRequest = new MissionRequest(
                "Орбита-1",
                null, // для группировки имя спутника не требуется
                MissionTargetType.CONSTELLATION,
                Set.of(SatelliteType.COMMUNICATION, SatelliteType.IMAGE) // задействовать все типы
        );
        operationService.executeMission(missionRequest);

        // Итоговое состояние
        System.out.println("\nСОСТОЯНИЕ ГРУППИРОВОК ПОСЛЕ МИССИИ:");
        printConstellationStatus("Орбита-1");
        printConstellationStatus("Орбита-2");

        System.out.println(constellationRepository.getAllConstellations());
    }

    private void printConstellationStatus(String constellationName) {
        var constellation = constellationRepository.getConstellation(constellationName);
        if (constellation != null) {
            System.out.println("Группировка " + constellationName + ":");
            constellation.getSatellites().forEach(sat ->
                    System.out.println("  - " + sat.getName() +
                            ", заряд: " + sat.getEnergy().getBatteryLevel() +
                            ", активен: " + sat.getState().isActive()));
        } else {
            System.out.println("Группировка " + constellationName + " не найдена");
        }
    }
}