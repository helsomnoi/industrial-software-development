package seminars.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import seminars.domain.satellites.CommunicationSatellite;
import seminars.domain.satellites.ImagingSatellite;
import seminars.domain.satellites.Satellite;
import seminars.domain.parametrs.CommunicationSatelliteParam;
import seminars.domain.parametrs.ImagingSatelliteParam;
import seminars.domain.parametrs.SatelliteParam;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("Тесты сервиса спутников")
class SatelliteServiceTest {
    private static final String NAME = "Test";
    private static final double BATTERY_LEVEL = 0.6;
    private static final double RESOLUTION = 0.1;
    private static final double BANDWIDTH = 500.0;

    @Autowired
    private SatelliteService satelliteService;

    @Test
    @DisplayName("Создание спутника ДЗЗ")
    void createImageSatelliteTest() {
        SatelliteParam imagingParam = new ImagingSatelliteParam(NAME, BATTERY_LEVEL, RESOLUTION);

        Satellite satellite = satelliteService.createSatellite(imagingParam);

        assertNotNull(satellite);
        assertInstanceOf(ImagingSatellite.class, satellite);
        ImagingSatellite imagingSatellite = (ImagingSatellite) satellite;
        assertEquals(NAME, satellite.getName());
        assertEquals(BATTERY_LEVEL, satellite.getEnergy().getBatteryLevel());
        assertEquals(RESOLUTION, imagingSatellite.getResolution());

    }

    @Test
    @DisplayName("Создание спутника связи")
    void createCommSatelliteTest() {
        SatelliteParam commParam = new CommunicationSatelliteParam(NAME, BATTERY_LEVEL, BANDWIDTH);

        Satellite satellite = satelliteService.createSatellite(commParam);

        assertNotNull(satellite);
        assertInstanceOf(CommunicationSatellite.class, satellite);
        CommunicationSatellite commSatellite = (CommunicationSatellite) satellite;
        assertEquals(NAME, satellite.getName());
        assertEquals(BATTERY_LEVEL, satellite.getEnergy().getBatteryLevel());
        assertEquals(BANDWIDTH, commSatellite.getBandwidth());

    }

}
