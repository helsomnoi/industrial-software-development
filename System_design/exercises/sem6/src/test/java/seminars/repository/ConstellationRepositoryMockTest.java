package seminars.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import seminars.domain.SatelliteConstellation;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Мок тест выполнения репозитория")
class ConstellationRepositoryMockTest {

    private static final String CONSTELLATION_1 = "testConstellation1";
    private static final String CONSTELLATION_2 = "testConstellation2";

    @Mock
    private ConstellationRepository repository;

    @Test
    @DisplayName("Добавление нескольких группировок должно сохранять их все")
    void testRepository() {
        SatelliteConstellation constellation1 = new SatelliteConstellation(CONSTELLATION_1);
        SatelliteConstellation constellation2 = new SatelliteConstellation(CONSTELLATION_2);

        Map<String, SatelliteConstellation> constellations = Map.of(
                CONSTELLATION_1, constellation1,
                CONSTELLATION_2, constellation2);

        when(repository.containsConstellation(CONSTELLATION_1)).thenReturn(true);
        when(repository.containsConstellation(CONSTELLATION_2)).thenReturn(true);

        when(repository.getAllConstellations()).thenReturn(constellations);

        assertTrue(repository.containsConstellation(CONSTELLATION_1));
        assertTrue(repository.containsConstellation(CONSTELLATION_2));
        assertEquals(2, repository.getAllConstellations().size());
    }
}
