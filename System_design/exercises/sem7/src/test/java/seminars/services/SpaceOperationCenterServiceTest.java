package seminars.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import seminars.domain.parametrs.CommunicationSatelliteParam;
import seminars.domain.parametrs.ImagingSatelliteParam;
import seminars.domain.question.AddSatelliteRequest;
import seminars.domain.question.MissionRequest;
import seminars.domain.question.MissionTargetType;
import seminars.domain.satellites.Satellite;
import seminars.domain.satellites.SatelliteConstellation;
import seminars.domain.satellites.SatelliteType;
import seminars.repository.ConstellationRepository;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("Интеграционные тесты SpaceOperationCenterService")
public class SpaceOperationCenterServiceTest {

    @Autowired
    private SpaceOperationCenterService spaceOperationCenterService;

    @Autowired
    private ConstellationRepository constellationRepository;

    private String uniqueName(String base) {
        return base + "_" + System.currentTimeMillis();
    }

    @Test
    @DisplayName("Добавление спутников к группировке через фасад")
    void addSatelliteTest() {
        String constellationName = uniqueName("TestConstellation");
        String commSatName = "CommSat-1";
        String imgSatName = "ImgSat-1";

        var commParam = new CommunicationSatelliteParam(commSatName, 0.9, 500.0);
        var imgParam = new ImagingSatelliteParam(imgSatName, 0.8, 2.5);

        var request = new AddSatelliteRequest(
                constellationName,
                List.of(commParam, imgParam)
        );

        spaceOperationCenterService.addSatellite(request);

        // Проверяем, что группировка создана и содержит два спутника
        SatelliteConstellation constellation = constellationRepository.getConstellation(constellationName);
        assertNotNull(constellation, "Группировка должна существовать");
        assertEquals(2, constellation.getSatellites().size(), "Должны быть 2 спутника");

        // Проверяем имена спутников
        List<String> satelliteNames = constellation.getSatellites().stream()
                .map(Satellite::getName)
                .toList();
        assertTrue(satelliteNames.contains(commSatName));
        assertTrue(satelliteNames.contains(imgSatName));
    }

    @Test
    @DisplayName("Выполнение миссии для всей группировки")
    void executeMissionForConstellationTest() {
        // Подготовка: создаём группировку с двумя спутниками
        String constellationName = uniqueName("MissionConstellation");
        var commParam = new CommunicationSatelliteParam("Comm-1", 0.9, 500.0);
        var imgParam = new ImagingSatelliteParam("Img-1", 0.8, 2.5);
        var addRequest = new AddSatelliteRequest(constellationName, List.of(commParam, imgParam));
        spaceOperationCenterService.addSatellite(addRequest);

        // Запрос на миссию для всей группировки (задействованы все типы)
        MissionRequest missionRequest = new MissionRequest(
                constellationName,
                null,
                MissionTargetType.CONSTELLATION,
                Set.of(SatelliteType.COMMUNICATION, SatelliteType.IMAGE)
        );

        spaceOperationCenterService.executeMission(missionRequest);

        // Проверяем, что все спутники стали активными (предполагаем, что isActive() отражает выполнение миссии)
        SatelliteConstellation constellation = constellationRepository.getConstellation(constellationName);
        assertNotNull(constellation);
        for (Satellite satellite : constellation.getSatellites()) {
            assertTrue(satellite.getState().isActive(), "Спутник " + satellite.getName() + " должен быть активен после миссии");
        }
    }

    @Test
    @DisplayName("Выполнение миссии для одного спутника")
    void executeMissionForSingleSatelliteTest() {
        // Подготовка: создаём группировку с двумя спутниками
        String constellationName = uniqueName("SingleSatConstellation");
        String targetSatelliteName = "TargetSat";
        var commParam = new CommunicationSatelliteParam(targetSatelliteName, 0.9, 500.0);
        var imgParam = new ImagingSatelliteParam("OtherSat", 0.8, 2.5);
        var addRequest = new AddSatelliteRequest(constellationName, List.of(commParam, imgParam));
        spaceOperationCenterService.addSatellite(addRequest);

        // Запрос на миссию только для одного спутника
        MissionRequest missionRequest = new MissionRequest(
                constellationName,
                targetSatelliteName,
                MissionTargetType.SINGLE_SATELLITE,
                Set.of(SatelliteType.COMMUNICATION) // тип можно указать, но он не обязан использоваться
        );

        spaceOperationCenterService.executeMission(missionRequest);

        // Проверяем, что целевой спутник активирован, а другой — нет
        SatelliteConstellation constellation = constellationRepository.getConstellation(constellationName);
        assertNotNull(constellation);
        for (Satellite satellite : constellation.getSatellites()) {
            if (satellite.getName().equals(targetSatelliteName)) {
                assertTrue(satellite.getState().isActive(), "Целевой спутник должен быть активен");
            } else {
                assertFalse(satellite.getState().isActive(), "Другой спутник не должен быть активен");
            }
        }
    }
}