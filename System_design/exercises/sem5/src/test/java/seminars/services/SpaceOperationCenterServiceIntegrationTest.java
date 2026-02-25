package seminars.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import seminars.domain.CommunicationSatellite;
import seminars.domain.Satellite;
import seminars.domain.SatelliteConstellation;
import seminars.repository.ConstellationRepository;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DisplayName("Интеграционные тесты SpaceOperationCenterService")
class SpaceOperationCenterServiceIntegrationTest {

    private static final String CONSTELLATION_NAME = "TestConstellation";
    private static final String CONSTELLATION_NAME_2 = "TestConstellation2";
    private static final String CONSTELLATION_EMPTY = "EmptyConstellation";
    private static final String SATELLITE_NAME_1 = "Satellite1";
    private static final String SATELLITE_NAME_2 = "Satellite2";
    private static final double SATELLITE_BATTERY_LEVEL = 0.4;
    private static final double SATELLITE_BANDWIDTH = 100;

    @Autowired
    private ConstellationRepository repository;

    @Autowired
    private SpaceOperationCenterService service;

    @BeforeEach
    void setUp() {
        Map<String, SatelliteConstellation> allConstellations = repository.getAllConstellations();
        String[] keys = allConstellations.keySet().toArray(new String[0]);
        for (String name : keys) {
            repository.removeConstellation(name);
        }
    }

    @Test
    @DisplayName("Создание и сохранение группировки")
    void createAndSaveConstellation() {
        service.createAndSaveConstellation(CONSTELLATION_NAME);

        assertTrue(repository.containsConstellation(CONSTELLATION_NAME));

        SatelliteConstellation savedConstellation = repository.getConstellation(CONSTELLATION_NAME);
        assertEquals(CONSTELLATION_NAME, savedConstellation.getConstellationName());
        assertTrue(savedConstellation.getSatellites().isEmpty());
    }

    @Test
    @DisplayName("Создание группировки с null именем")
    void createAndSaveConstellation_NullName() {
        service.createAndSaveConstellation(null);

        assertTrue(repository.containsConstellation(null));

        SatelliteConstellation savedConstellation = repository.getConstellation(null);
        assertNull(savedConstellation.getConstellationName());
    }

    @Test
    @DisplayName("Добавление спутника в группировку")
    void addSatelliteToConstellation() {
        service.createAndSaveConstellation(CONSTELLATION_NAME);
        Satellite satellite = new CommunicationSatellite(SATELLITE_NAME_1, SATELLITE_BATTERY_LEVEL, SATELLITE_BANDWIDTH);

        service.addSatelliteToConstellation(CONSTELLATION_NAME, satellite);

        SatelliteConstellation constellation = repository.getConstellation(CONSTELLATION_NAME);
        List<Satellite> satellites = constellation.getSatellites();

        assertEquals(1, satellites.size());
        assertEquals(SATELLITE_NAME_1, satellites.get(0).getName());
    }

    @Test
    @DisplayName("Добавление спутника с null именем")
    void addSatelliteToConstellation_SatelliteWithNullName() {
        service.createAndSaveConstellation(CONSTELLATION_NAME);
        Satellite satellite = new CommunicationSatellite(null, SATELLITE_BATTERY_LEVEL, SATELLITE_BANDWIDTH);

        service.addSatelliteToConstellation(CONSTELLATION_NAME, satellite);

        SatelliteConstellation constellation = repository.getConstellation(CONSTELLATION_NAME);
        assertEquals(1, constellation.getSatellites().size());
        assertNull(constellation.getSatellites().get(0).getName());
    }

    @Test
    @DisplayName("Добавление нескольких спутников в группировку")
    void addMultipleSatellites() {
        service.createAndSaveConstellation(CONSTELLATION_NAME);

        Satellite firstSatellite = new CommunicationSatellite(SATELLITE_NAME_1, SATELLITE_BATTERY_LEVEL, SATELLITE_BANDWIDTH);
        Satellite secondSatellite = new CommunicationSatellite(SATELLITE_NAME_2, SATELLITE_BATTERY_LEVEL, SATELLITE_BANDWIDTH);

        service.addSatelliteToConstellation(CONSTELLATION_NAME, firstSatellite);
        service.addSatelliteToConstellation(CONSTELLATION_NAME, secondSatellite);

        SatelliteConstellation constellation = repository.getConstellation(CONSTELLATION_NAME);
        List<Satellite> satellites = constellation.getSatellites();

        assertEquals(2, satellites.size());
        assertEquals(SATELLITE_NAME_1, satellites.get(0).getName());
        assertEquals(SATELLITE_NAME_2, satellites.get(1).getName());
    }

    @Test
    @DisplayName("Добавление спутника в несуществующую группировку")
    void addSatelliteToNonExistentConstellation() {
        Satellite satellite = new CommunicationSatellite(SATELLITE_NAME_1, SATELLITE_BATTERY_LEVEL, SATELLITE_BANDWIDTH);

        assertThrows(RuntimeException.class,
                () -> service.addSatelliteToConstellation("NonExistent", satellite));
    }

    @Test
    @DisplayName("Активация всех спутников в группировке")
    void activateAllSatellites() {
        service.createAndSaveConstellation(CONSTELLATION_NAME);

        Satellite firstSatellite = new CommunicationSatellite(SATELLITE_NAME_1, SATELLITE_BATTERY_LEVEL, SATELLITE_BANDWIDTH);
        Satellite secondSatellite = new CommunicationSatellite(SATELLITE_NAME_2, SATELLITE_BATTERY_LEVEL, SATELLITE_BANDWIDTH);

        service.addSatelliteToConstellation(CONSTELLATION_NAME, firstSatellite);
        service.addSatelliteToConstellation(CONSTELLATION_NAME, secondSatellite);

        service.activateAllSatellites(CONSTELLATION_NAME);

        SatelliteConstellation constellation = repository.getConstellation(CONSTELLATION_NAME);
        List<Satellite> satellites = constellation.getSatellites();

        assertTrue(satellites.get(0).getState().isActive());
        assertTrue(satellites.get(1).getState().isActive());
    }

    @Test
    @DisplayName("Активация спутников в пустой группировке")
    void activateAllSatellites_EmptyConstellation() {
        service.createAndSaveConstellation(CONSTELLATION_EMPTY);

        assertDoesNotThrow(() -> service.activateAllSatellites(CONSTELLATION_EMPTY));
    }

    @Test
    @DisplayName("Активация спутников в несуществующей группировке")
    void activateAllSatellites_NonExistentConstellation() {
        assertThrows(RuntimeException.class,
                () -> service.activateAllSatellites("NonExistent"));
    }

    @Test
    @DisplayName("Выполнение миссий после активации")
    void executeConstellationMissions_AfterActivation() {
        service.createAndSaveConstellation(CONSTELLATION_NAME);

        Satellite satellite = new CommunicationSatellite(SATELLITE_NAME_1, SATELLITE_BATTERY_LEVEL, SATELLITE_BANDWIDTH);

        service.addSatelliteToConstellation(CONSTELLATION_NAME, satellite);
        service.activateAllSatellites(CONSTELLATION_NAME);

        assertDoesNotThrow(() -> service.executeConstellationMissions(CONSTELLATION_NAME));
    }

    @Test
    @DisplayName("Выполнение миссий без активации")
    void executeConstellationMissions_WithoutActivation() {
        service.createAndSaveConstellation(CONSTELLATION_NAME);

        Satellite satellite = new CommunicationSatellite(SATELLITE_NAME_1, SATELLITE_BATTERY_LEVEL, SATELLITE_BANDWIDTH);

        service.addSatelliteToConstellation(CONSTELLATION_NAME, satellite);

        assertDoesNotThrow(() -> service.executeConstellationMissions(CONSTELLATION_NAME));
    }

    @Test
    @DisplayName("Выполнение миссий в пустой группировке")
    void executeConstellationMissions_EmptyConstellation() {
        service.createAndSaveConstellation(CONSTELLATION_EMPTY);

        assertDoesNotThrow(() -> service.executeConstellationMissions(CONSTELLATION_EMPTY));
    }

    @Test
    @DisplayName("Выполнение миссий в несуществующей группировке")
    void executeConstellationMissions_NonExistentConstellation() {
        assertThrows(RuntimeException.class,
                () -> service.executeConstellationMissions("NonExistent"));
    }

    @Test
    @DisplayName("Показ статуса существующей группировки")
    void showConstellationStatus_ExistingConstellation() {
        service.createAndSaveConstellation(CONSTELLATION_NAME);

        Satellite satellite = new CommunicationSatellite(SATELLITE_NAME_1, SATELLITE_BATTERY_LEVEL, SATELLITE_BANDWIDTH);

        service.addSatelliteToConstellation(CONSTELLATION_NAME, satellite);
        service.activateAllSatellites(CONSTELLATION_NAME);

        assertDoesNotThrow(() -> service.showConstellationStatus(CONSTELLATION_NAME));
    }

    @Test
    @DisplayName("Показ статуса несуществующей группировки")
    void showConstellationStatus_NonExistentConstellation() {
        assertThrows(RuntimeException.class,
                () -> service.showConstellationStatus("NonExistent"));
    }

    @Test
    @DisplayName("Полный цикл работы с группировкой")
    void completeWorkflow() {
        // Создание
        service.createAndSaveConstellation(CONSTELLATION_NAME);

        // Добавление спутников
        Satellite firstSatellite = new CommunicationSatellite(SATELLITE_NAME_1, SATELLITE_BATTERY_LEVEL, SATELLITE_BANDWIDTH);
        Satellite secondSatellite = new CommunicationSatellite(SATELLITE_NAME_2, SATELLITE_BATTERY_LEVEL, SATELLITE_BANDWIDTH);

        service.addSatelliteToConstellation(CONSTELLATION_NAME, firstSatellite);
        service.addSatelliteToConstellation(CONSTELLATION_NAME, secondSatellite);

        // Активация
        service.activateAllSatellites(CONSTELLATION_NAME);

        // Выполнение миссий
        service.executeConstellationMissions(CONSTELLATION_NAME);

        // Проверка статуса
        service.showConstellationStatus(CONSTELLATION_NAME);

        // Финальная проверка состояния
        SatelliteConstellation finalConstellation = repository.getConstellation(CONSTELLATION_NAME);
        assertEquals(2, finalConstellation.getSatellites().size());
        assertTrue(finalConstellation.getSatellites().get(0).getState().isActive());
        assertTrue(finalConstellation.getSatellites().get(1).getState().isActive());
    }

    @Test
    @DisplayName("Работа с несколькими группировками")
    void multipleConstellations() {
        service.createAndSaveConstellation(CONSTELLATION_NAME);
        service.createAndSaveConstellation(CONSTELLATION_NAME_2);

        Satellite satellite1 = new CommunicationSatellite(SATELLITE_NAME_1, SATELLITE_BATTERY_LEVEL, SATELLITE_BANDWIDTH);
        service.addSatelliteToConstellation(CONSTELLATION_NAME, satellite1);

        Satellite satellite2 = new CommunicationSatellite(SATELLITE_NAME_2, SATELLITE_BATTERY_LEVEL, SATELLITE_BANDWIDTH);
        service.addSatelliteToConstellation(CONSTELLATION_NAME_2, satellite2);

        service.activateAllSatellites(CONSTELLATION_NAME);

        SatelliteConstellation constellation1 = repository.getConstellation(CONSTELLATION_NAME);
        SatelliteConstellation constellation2 = repository.getConstellation(CONSTELLATION_NAME_2);

        assertEquals(1, constellation1.getSatellites().size());
        assertEquals(SATELLITE_NAME_1, constellation1.getSatellites().get(0).getName());
        assertTrue(constellation1.getSatellites().get(0).getState().isActive());

        assertEquals(1, constellation2.getSatellites().size());
        assertEquals(SATELLITE_NAME_2, constellation2.getSatellites().get(0).getName());
        assertFalse(constellation2.getSatellites().get(0).getState().isActive());
    }
}