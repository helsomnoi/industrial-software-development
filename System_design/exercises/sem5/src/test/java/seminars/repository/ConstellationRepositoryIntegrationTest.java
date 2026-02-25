package seminars.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import seminars.domain.SatelliteConstellation;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("Интеграционные тесты репозитория")
class ConstellationRepositoryIntegrationTest {

    private static final String CONSTELLATION_1 = "testConstellation1";
    private static final String CONSTELLATION_2 = "testConstellation2";

    @Autowired
    private ConstellationRepository repository;

    @BeforeEach
    void setUp() {
        Map<String, SatelliteConstellation> allConstellations = repository.getAllConstellations();
        String[] keys = allConstellations.keySet().toArray(new String[0]);
        for (String name : keys) {
            repository.removeConstellation(name);
        }
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
    @DisplayName("Получение существующей группировки должно возвращать правильный объект")
    void getExistingConstellation_ShouldReturnCorrectObject() {
        SatelliteConstellation expectedConstellation = new SatelliteConstellation(CONSTELLATION_1);
        repository.addConstellation(expectedConstellation);

        SatelliteConstellation actualConstellation = repository.getConstellation(CONSTELLATION_1);

        assertEquals(expectedConstellation, actualConstellation);
        assertEquals(CONSTELLATION_1, actualConstellation.getConstellationName());
    }

    @Test
    @DisplayName("Получение несуществующей группировки должно выбрасывать исключение")
    void getNonExistingConstellation_ShouldThrowException() {
        String nonExistingName = "nonExisting";

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> repository.getConstellation(nonExistingName));

        assertTrue(exception.getMessage().contains("Группировка не найдена: " + nonExistingName));
    }

    @Test
    @DisplayName("Удаление существующей группировки должно удалять её из репозитория")
    void removeExistingConstellation_ShouldRemoveIt() {
        repository.addConstellation(new SatelliteConstellation(CONSTELLATION_1));
        repository.addConstellation(new SatelliteConstellation(CONSTELLATION_2));

        repository.removeConstellation(CONSTELLATION_1);

        assertFalse(repository.containsConstellation(CONSTELLATION_1));
        assertTrue(repository.containsConstellation(CONSTELLATION_2));
        assertEquals(1, repository.getAllConstellations().size());
    }

    @Test
    @DisplayName("Удаление несуществующей группировки не должно влиять на репозиторий")
    void removeNonExistingConstellation_ShouldNotAffectRepository() {
        repository.addConstellation(new SatelliteConstellation(CONSTELLATION_1));
        int sizeBefore = repository.getAllConstellations().size();

        repository.removeConstellation("nonExisting");

        assertEquals(sizeBefore, repository.getAllConstellations().size());
        assertTrue(repository.containsConstellation(CONSTELLATION_1));
    }

    @Test
    @DisplayName("Добавление группировки с null именем должно работать")
    void addConstellationWithNullName_ShouldWork() {
        SatelliteConstellation constellation = new SatelliteConstellation(null);

        assertDoesNotThrow(() -> repository.addConstellation(constellation));
        assertTrue(repository.containsConstellation(null));
        assertEquals(1, repository.getAllConstellations().size());
    }

    @Test
    @DisplayName("Пустой репозиторий должен возвращать ноль")
    void emptyRepository_ShouldReturnEmptyMap() {
        assertTrue(repository.getAllConstellations().isEmpty());
        assertEquals(0, repository.getAllConstellations().size());
    }
}
