package seminars;

import seminars.repository.ConstellationRepository;
import seminars.services.SpaceOperationCenterService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        System.out.println("ЗАПУСК СИСТЕМЫ УПРАВЛЕНИЯ СПУТНИКОВОЙ ГРУППИРОВКОЙ");
        System.out.println("=".repeat(25));

        ConfigurableApplicationContext context = SpringApplication.run(Main.class, args);

        ConstellationRepository constellationRepository = context.getBean(ConstellationRepository.class);
        SpaceOperationCenterService operationService = context.getBean(SpaceOperationCenterService.class);;

        System.out.println("СОЗДАНИЕ СПЕЦИАЛИЗИРОВАННЫХ СПУТНИКОВ:");
        System.out.println("-".repeat(15));

        // Спутники связи
        CommunicationSatellite communication_1 = new CommunicationSatellite("Связь-1", 0.85, 500);
        CommunicationSatellite communication_2 = new CommunicationSatellite("Связь-2", 0.75, 1000);

        // Спутники дистанционного зондирования Земли
        ImagingSatellite imaging_1 = new ImagingSatellite("ДЗЗ-1", 0.92, 2.5);
        ImagingSatellite imaging_2 = new ImagingSatellite("ДЗЗ-2", 0.45, 1);
        ImagingSatellite imaging_3 = new ImagingSatellite("ДЗЗ-3", 0.15, 0.5);
        System.out.println("-".repeat(15));

        // Создание группировки
        operationService.createAndSaveConstellation("Орбита-1");
        operationService.createAndSaveConstellation("Орбита-2");

        // Добавляем спутники
        System.out.println("-".repeat(15));
        System.out.println("ДОБАВЛЕНИЕ СПУТНИКОВ:");
        operationService.addSatelliteToConstellation("Орбита-1", communication_1);
        operationService.addSatelliteToConstellation("Орбита-1", imaging_1);
        operationService.addSatelliteToConstellation("Орбита-1", imaging_2);

        operationService.addSatelliteToConstellation("Орбита-2", communication_2);
        operationService.addSatelliteToConstellation("Орбита-2", imaging_3);
        System.out.println("-".repeat(10));

        // Проверяем начальное состояние
        operationService.showConstellationStatus("Орбита-1");
        operationService.showConstellationStatus("Орбита-2");

        operationService.activateAllSatellites("Орбита-1");

        operationService.executeConstellationMissions("Орбита-1");

        operationService.showConstellationStatus("Орбита-1");

        System.out.println(constellationRepository.getAllConstellations().toString());
    }
}