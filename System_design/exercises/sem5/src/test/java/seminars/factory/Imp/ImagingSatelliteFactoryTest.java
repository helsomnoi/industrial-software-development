package seminars.factory.Imp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seminars.domain.ImagingSatellite;
import seminars.domain.Satellite;

import static org.junit.jupiter.api.Assertions.*;

class ImagingSatelliteFactoryTest {

    private ImagingSatelliteFactory factory;

    @BeforeEach
    void setUp() {
        factory = new ImagingSatelliteFactory();
    }

    @Test
    void createSatellite_ShouldCreateWithDefaultResolution() {
        String name = "ДЗЗ-1";
        double batteryLevel = 0.8;

        Satellite satellite = factory.createSatellite(name, batteryLevel);

        assertNotNull(satellite);
        assertTrue(satellite instanceof ImagingSatellite);
        ImagingSatellite imagingSat = (ImagingSatellite) satellite;
        assertEquals(name, imagingSat.getName());
        assertEquals(batteryLevel, imagingSat.getEnergy().getBatteryLevel(), 0.0001);
        assertEquals(ImagingSatelliteFactory.DEFAULT_RESOLUTION, imagingSat.getResolution(), 0.0001);
    }

    @Test
    void createSatelliteWithParameter_ShouldCreateWithGivenResolution() {
        String name = "Дзз-2";
        double batteryLevel = 0.9;
        double resolution = 5.5;

        Satellite satellite = factory.createSatelliteWithParameter(name, batteryLevel, resolution);

        assertNotNull(satellite);
        assertTrue(satellite instanceof ImagingSatellite);
        ImagingSatellite imagingSat = (ImagingSatellite) satellite;
        assertEquals(name, imagingSat.getName());
        assertEquals(batteryLevel, imagingSat.getEnergy().getBatteryLevel(), 0.001);
        assertEquals(resolution, imagingSat.getResolution(), 0.001);
    }
}