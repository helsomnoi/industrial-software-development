package seminars.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import seminars.domain.CommunicationSatellite;
import seminars.domain.Satellite;
import seminars.domain.SatelliteConstellation;
import seminars.repository.ConstellationRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DisplayName("Unit-тесты SpaceOperationCenterService")
class SpaceOperationCenterServiceUnitTest {

    private static final String CONSTELLATION_NAME = "TestConstellation";
    private static final String CONSTELLATION_EMPTY = "EmptyConstellation";
    private static final String SATELLITE_NAME_1 = "Satellite1";
    private static final double SATELLITE_BATTERY_LEVEL = 0.4;
    private static final double SATELLITE_BANDWIDTH = 100;
    private static final String SATELLITE_NAME_2 = "Satellite2";

    private ConstellationRepository repository;
    private SpaceOperationCenterService service;

    @BeforeEach
    void setUp() {
        repository = new ConstellationRepository();
        service = new SpaceOperationCenterService(repository);
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
    void createAndSaveConstellation_nullConstellation() {
        service.createAndSaveConstellation(null);

        assertTrue(repository.containsConstellation(null));

        SatelliteConstellation savedConstellation = repository.getConstellation(null);
        assertNull(savedConstellation.getConstellationName());
    }

    @Test
    @DisplayName("Добавление спутника в существующую группировку")
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
    @DisplayName("Добавление спутника в несуществующую группировку")
    void addSatelliteToConstellation_EmptyConstellation() {
        Satellite satellite = new CommunicationSatellite(SATELLITE_NAME_1, SATELLITE_BATTERY_LEVEL, SATELLITE_BANDWIDTH);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> service.addSatelliteToConstellation(CONSTELLATION_EMPTY, satellite));

        assertTrue(exception.getMessage().contains("Группировка не найдена"));
    }

    @Test
    @DisplayName("Добавление спутника с null именем")
    void addSatelliteToConstellation_SatelliteNameNull() {
        service.createAndSaveConstellation(CONSTELLATION_NAME);
        Satellite satellite = new CommunicationSatellite(null, SATELLITE_BATTERY_LEVEL, SATELLITE_BANDWIDTH);

        service.addSatelliteToConstellation(CONSTELLATION_NAME, satellite);

        SatelliteConstellation constellation = repository.getConstellation(CONSTELLATION_NAME);
        assertEquals(1, constellation.getSatellites().size());
        assertNull(constellation.getSatellites().get(0).getName());
    }

    @Test
    @DisplayName("Добавление нескольких спутников в группировку")
    void addSatelliteToConstellation_MultipleSatellites() {
        service.createAndSaveConstellation(CONSTELLATION_NAME);
        Satellite satellite1 = new CommunicationSatellite(SATELLITE_NAME_1, SATELLITE_BATTERY_LEVEL, SATELLITE_BANDWIDTH);
        Satellite satellite2 = new CommunicationSatellite(SATELLITE_NAME_2, SATELLITE_BATTERY_LEVEL, SATELLITE_BANDWIDTH);

        service.addSatelliteToConstellation(CONSTELLATION_NAME, satellite1);
        service.addSatelliteToConstellation(CONSTELLATION_NAME, satellite2);

        SatelliteConstellation constellation = repository.getConstellation(CONSTELLATION_NAME);
        List<Satellite> satellites = constellation.getSatellites();

        assertEquals(2, satellites.size());
        assertEquals(SATELLITE_NAME_1, satellites.get(0).getName());
        assertEquals(SATELLITE_NAME_2, satellites.get(1).getName());
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
    @DisplayName("Выполнение миссий после активации")
    void executeConstellationMissions() {
        service.createAndSaveConstellation(CONSTELLATION_NAME);

        Satellite satellite1 = new CommunicationSatellite(SATELLITE_NAME_1, SATELLITE_BATTERY_LEVEL, SATELLITE_BANDWIDTH);
        Satellite satellite2 = new CommunicationSatellite(SATELLITE_NAME_2, SATELLITE_BATTERY_LEVEL, SATELLITE_BANDWIDTH);

        service.addSatelliteToConstellation(CONSTELLATION_NAME, satellite1);
        service.addSatelliteToConstellation(CONSTELLATION_NAME, satellite2);

        service.activateAllSatellites(CONSTELLATION_NAME);
        service.executeConstellationMissions(CONSTELLATION_NAME);

        SatelliteConstellation constellation = repository.getConstellation(CONSTELLATION_NAME);
        assertEquals(2, constellation.getSatellites().size());
    }

    @Test
    @DisplayName("Выполнение миссий без активации")
    void executeConstellationMissions_WithoutActivation() {
        service.createAndSaveConstellation(CONSTELLATION_NAME);

        Satellite satellite1 = new CommunicationSatellite(SATELLITE_NAME_1, SATELLITE_BATTERY_LEVEL, SATELLITE_BANDWIDTH);
        Satellite satellite2 = new CommunicationSatellite(SATELLITE_NAME_2, SATELLITE_BATTERY_LEVEL, SATELLITE_BANDWIDTH);

        service.addSatelliteToConstellation(CONSTELLATION_NAME, satellite1);
        service.addSatelliteToConstellation(CONSTELLATION_NAME, satellite2);

        assertDoesNotThrow(() -> service.executeConstellationMissions(CONSTELLATION_NAME));
    }

    @Test
    @DisplayName("Выполнение миссий в пустой группировке")
    void executeConstellationMissions_EmptyConstellation() {
        service.createAndSaveConstellation(CONSTELLATION_EMPTY);

        assertDoesNotThrow(() -> service.executeConstellationMissions(CONSTELLATION_EMPTY));
    }

    @Test
    @DisplayName("Показ статуса группировки")
    void showConstellationStatus() {
        service.createAndSaveConstellation(CONSTELLATION_NAME);
        Satellite satellite = new CommunicationSatellite(SATELLITE_NAME_1, SATELLITE_BATTERY_LEVEL, SATELLITE_BANDWIDTH);

        service.addSatelliteToConstellation(CONSTELLATION_NAME, satellite);
        service.activateAllSatellites(CONSTELLATION_NAME);

        assertDoesNotThrow(() -> service.showConstellationStatus(CONSTELLATION_NAME));
    }

    @Test
    @DisplayName("Получение несуществующей группировки")
    void getNonExistentConstellation() {
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> service.showConstellationStatus("NonExistent"));

        assertTrue(exception.getMessage().contains("Группировка не найдена"));
    }

    @Test
    @DisplayName("Активация несуществующей группировки")
    void activateNonExistentConstellation() {
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> service.activateAllSatellites("NonExistent"));

        assertTrue(exception.getMessage().contains("Группировка не найдена"));
    }

    @Test
    @DisplayName("Выполнение миссий несуществующей группировки")
    void executeMissionsNonExistentConstellation_ShouldThrowException() {
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> service.executeConstellationMissions("NonExistent"));

        assertTrue(exception.getMessage().contains("Группировка не найдена"));
    }
}