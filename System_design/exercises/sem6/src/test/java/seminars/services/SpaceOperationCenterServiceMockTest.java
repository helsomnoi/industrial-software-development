package seminars.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import seminars.domain.Satellite;
import seminars.domain.SatelliteConstellation;
import seminars.repository.ConstellationRepository;
import seminars.services.Impl.SpaceOperationCenterServiceImpl;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Мок-тесты ImplSpaceOperationCenterService")
class SpaceOperationCenterServiceMockTest {

    private static final String CONSTELLATION_NAME = "TestConstellation";
    private static final String NON_EXISTENT_CONSTELLATION = "NonExistentConstellation";

    @Mock
    private ConstellationRepository mockRepository;

    @Mock
    private SatelliteConstellation mockConstellation;

    @Mock
    private Satellite mockSatellite1;

    @Mock
    private Satellite mockSatellite2;

    private SpaceOperationCenterServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new SpaceOperationCenterServiceImpl(mockRepository);
    }

    @Test
    @DisplayName("Создание и сохранение группировки")
    void createAndSaveConstellation() {
        service.createAndSaveConstellation(CONSTELLATION_NAME);

        verify(mockRepository, times(1)).addConstellation(any(SatelliteConstellation.class));

        verify(mockRepository).addConstellation(argThat(constellation ->
                constellation.getConstellationName().equals(CONSTELLATION_NAME)
        ));
    }

    @Test
    @DisplayName("Создание и сохранение группировки с null именем")
    void createAndSaveConstellation_NullName() {
        service.createAndSaveConstellation(null);

        verify(mockRepository, times(1)).addConstellation(any(SatelliteConstellation.class));
    }

    @Test
    @DisplayName("Добавление спутника в существующую группировку")
    void addSatelliteToConstellation_ExistingConstellation() {
        when(mockRepository.getConstellation(CONSTELLATION_NAME)).thenReturn(mockConstellation);

        service.addSatelliteToConstellation(CONSTELLATION_NAME, mockSatellite1);

        verify(mockRepository, times(1)).getConstellation(CONSTELLATION_NAME);
        verify(mockConstellation, times(1)).addSatellite(mockSatellite1);
    }

    @Test
    @DisplayName("Добавление спутника в несуществующую группировку")
    void addSatelliteToConstellation_NonExistentConstellation() {
        when(mockRepository.getConstellation(NON_EXISTENT_CONSTELLATION))
                .thenThrow(new RuntimeException("Группировка не найдена: " + NON_EXISTENT_CONSTELLATION));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> service.addSatelliteToConstellation(NON_EXISTENT_CONSTELLATION, mockSatellite1));

        assertTrue(exception.getMessage().contains("Группировка не найдена"));
        verify(mockRepository, times(1)).getConstellation(NON_EXISTENT_CONSTELLATION);
        verify(mockConstellation, never()).addSatellite(any());
    }

    @Test
    @DisplayName("Добавление нескольких спутников в группировку")
    void addSatelliteToConstellation_MultipleSatellites() {
        when(mockRepository.getConstellation(CONSTELLATION_NAME)).thenReturn(mockConstellation);

        service.addSatelliteToConstellation(CONSTELLATION_NAME, mockSatellite1);
        service.addSatelliteToConstellation(CONSTELLATION_NAME, mockSatellite2);

        verify(mockRepository, times(2)).getConstellation(CONSTELLATION_NAME);
        verify(mockConstellation, times(1)).addSatellite(mockSatellite1);
        verify(mockConstellation, times(1)).addSatellite(mockSatellite2);
    }

    @Test
    @DisplayName("Выполнение миссий группировки")
    void executeConstellationMissions_ValidConstellation() {
        when(mockRepository.getConstellation(CONSTELLATION_NAME)).thenReturn(mockConstellation);

        service.executeConstellationMissions(CONSTELLATION_NAME);

        verify(mockRepository, times(1)).getConstellation(CONSTELLATION_NAME);
        verify(mockConstellation, times(1)).executeAllMission();
    }

    @Test
    @DisplayName("Выполнение миссий несуществующей группировки")
    void executeConstellationMissions_NonExistentConstellation() {
        when(mockRepository.getConstellation(NON_EXISTENT_CONSTELLATION))
                .thenThrow(new RuntimeException("Группировка не найдена"));

        assertThrows(RuntimeException.class,
                () -> service.executeConstellationMissions(NON_EXISTENT_CONSTELLATION));

        verify(mockRepository, times(1)).getConstellation(NON_EXISTENT_CONSTELLATION);
        verify(mockConstellation, never()).executeAllMission();
    }

    @Test
    @DisplayName("Активация всех спутников")
    void activateAllSatellites_ConstellationWithSatellites() {
        List<Satellite> satellites = Arrays.asList(mockSatellite1, mockSatellite2);

        when(mockRepository.getConstellation(CONSTELLATION_NAME)).thenReturn(mockConstellation);
        when(mockConstellation.getSatellites()).thenReturn(satellites);

        service.activateAllSatellites(CONSTELLATION_NAME);

        verify(mockRepository, times(1)).getConstellation(CONSTELLATION_NAME);
        verify(mockConstellation, times(1)).getSatellites();
        verify(mockSatellite1, times(1)).activate();
        verify(mockSatellite2, times(1)).activate();
    }

    @Test
    @DisplayName("Активация всех спутников в пустой группировке")
    void activateAllSatellites_EmptyConstellation() {
        List<Satellite> emptySatellites = List.of();

        when(mockRepository.getConstellation(CONSTELLATION_NAME)).thenReturn(mockConstellation);
        when(mockConstellation.getSatellites()).thenReturn(emptySatellites);

        service.activateAllSatellites(CONSTELLATION_NAME);

        verify(mockRepository, times(1)).getConstellation(CONSTELLATION_NAME);
        verify(mockConstellation, times(1)).getSatellites();
        verify(mockSatellite1, never()).activate();
        verify(mockSatellite2, never()).activate();
    }

    @Test
    @DisplayName("Активация спутников в несуществующей группировке")
    void activateAllSatellites_NonExistentConstellation() {
        when(mockRepository.getConstellation(NON_EXISTENT_CONSTELLATION))
                .thenThrow(new RuntimeException("Группировка не найдена"));

        assertThrows(RuntimeException.class,
                () -> service.activateAllSatellites(NON_EXISTENT_CONSTELLATION));

        verify(mockRepository, times(1)).getConstellation(NON_EXISTENT_CONSTELLATION);
        verify(mockConstellation, never()).getSatellites();
    }

    @Test
    @DisplayName("Показ статуса группировки")
    void showConstellationStatus_ValidConstellation() {
        List<Satellite> satellites = Arrays.asList(mockSatellite1, mockSatellite2);

        when(mockRepository.getConstellation(CONSTELLATION_NAME)).thenReturn(mockConstellation);
        when(mockConstellation.getSatellites()).thenReturn(satellites);

        service.showConstellationStatus(CONSTELLATION_NAME);

        verify(mockRepository, times(1)).getConstellation(CONSTELLATION_NAME);
        verify(mockConstellation, times(1)).getSatellites();
        verify(mockSatellite1, times(1)).getState();
        verify(mockSatellite2, times(1)).getState();
    }

    @Test
    @DisplayName("Показ статуса несуществующей группировки")
    void showConstellationStatus_NonExistentConstellation() {
        when(mockRepository.getConstellation(NON_EXISTENT_CONSTELLATION))
                .thenThrow(new RuntimeException("Группировка не найдена"));

        assertThrows(RuntimeException.class,
                () -> service.showConstellationStatus(NON_EXISTENT_CONSTELLATION));

        verify(mockRepository, times(1)).getConstellation(NON_EXISTENT_CONSTELLATION);
        verify(mockConstellation, never()).getConstellationName();
    }
}