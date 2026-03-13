package seminars.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import seminars.domain.satellites.SatelliteConstellation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Тест выполнения репозитория")
class ConstellationRepositoryUnitTest {

    private static final String CONSTELLATION_1 = "testConstellation1";
    private static final String CONSTELLATION_2 = "testConstellation2";

    private ConstellationRepository repository;

    @BeforeEach
    void setup(){
        repository = new ConstellationRepository();
    }

    @Test
    @DisplayName("Добавление нескольких группировок должно сохранять их все")
    void testRepository() {
        SatelliteConstellation constellation1 = new SatelliteConstellation(CONSTELLATION_1);
        SatelliteConstellation constellation2 = new SatelliteConstellation(CONSTELLATION_2);

        repository.addConstellation(constellation1);
        repository.addConstellation(constellation2);

        assertTrue(repository.containsConstellation(CONSTELLATION_1));
        assertTrue(repository.containsConstellation(CONSTELLATION_2));
        assertEquals(2, repository.getAllConstellations().size());
    }

    @Test
    @DisplayName("Получение существующей группировки возврщает правильный объект")
    void getConstellation() {
        SatelliteConstellation constellation = new SatelliteConstellation(CONSTELLATION_1);
        repository.addConstellation(constellation);

        assertEquals(constellation, repository.getConstellation(CONSTELLATION_1));
    }

    @Test
    @DisplayName("Получение несуществующей группировки выбрасывает исключение")
    void getConstellation_NonExistent() {
        String nonExistingName = "nonExistingConstellation";

        RuntimeException exception = org.junit.jupiter.api.Assertions.assertThrows(
                RuntimeException.class, () -> repository.getConstellation(nonExistingName));

        assertTrue(exception.getMessage().contains("Группировка не найдена: " + nonExistingName));
    }

    @Test
    @DisplayName("Удаление существующей группировки")
    void removeConstellation_ExistingConstellation_RemovesFromRepository() {
        SatelliteConstellation constellation = new SatelliteConstellation(CONSTELLATION_1);
        repository.addConstellation(constellation);

        repository.removeConstellation(CONSTELLATION_1);

        org.junit.jupiter.api.Assertions.assertFalse(repository.containsConstellation(CONSTELLATION_1));
        assertEquals(0, repository.getAllConstellations().size());
    }

    @Test
    @DisplayName("Удаление несуществующей группировки не вызывает ошибок")
    void removeConstellation_NonExistingConstellation_DoesNothing() {
        repository.addConstellation(new SatelliteConstellation(CONSTELLATION_1));
        int sizeBefore = repository.getAllConstellations().size();

        repository.removeConstellation("nonExisting");

        assertEquals(sizeBefore, repository.getAllConstellations().size());
        assertTrue(repository.containsConstellation(CONSTELLATION_1));
    }

    @Test
    @DisplayName("Добавление группировки с null именем")
    void addConstellation_NullName_HandlesCorrectly() {
        SatelliteConstellation constellation = new SatelliteConstellation(null);

        org.junit.jupiter.api.Assertions.assertDoesNotThrow(
                () -> repository.addConstellation(constellation)
        );

        assertTrue(repository.containsConstellation(null));
    }

    @Test
    @DisplayName("Добавление дублирующейся группировки перезаписывает существующую")
    void addConstellation_DuplicateName_OverwritesExisting() {
        SatelliteConstellation constellation1 = new SatelliteConstellation(CONSTELLATION_1);
        SatelliteConstellation constellation2 = new SatelliteConstellation(CONSTELLATION_1);

        repository.addConstellation(constellation1);
        repository.addConstellation(constellation2);

        assertEquals(1, repository.getAllConstellations().size());
        assertEquals(constellation2, repository.getConstellation(CONSTELLATION_1));
    }

    @Test
    @DisplayName("Пустой репозиторий")
    void emptyRepository_ReturnsEmptyMap() {
        assertTrue(repository.getAllConstellations().isEmpty());
        assertEquals(0, repository.getAllConstellations().size());
    }
}

