package seminars.factory.Imp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seminars.domain.CommunicationSatellite;
import seminars.domain.Satellite;

import static org.junit.jupiter.api.Assertions.*;

class CommunicationSatelliteFactoryTest {

    private CommunicationSatelliteFactory factory;

    @BeforeEach
    void setUp() {
        factory = new CommunicationSatelliteFactory();
    }

    @Test
    void createSatellite_ShouldCreateWithDefaultBandwidth() {
        String name = "Связь-1";
        double batteryLevel = 0.75;

        Satellite satellite = factory.createSatellite(name, batteryLevel);

        assertNotNull(satellite);
        assertTrue(satellite instanceof CommunicationSatellite);
        CommunicationSatellite comSat = (CommunicationSatellite) satellite;
        assertEquals(name, comSat.getName());
        assertEquals(batteryLevel, comSat.getEnergy().getBatteryLevel(), 0.0001);
        assertEquals(CommunicationSatelliteFactory.DEFAULT_BANDWIDTH, comSat.getBandWidth(), 0.0001);
    }

    @Test
    void createSatelliteWithParameter_ShouldCreateWithGivenBandwidth() {
        String name = "Связь -2";
        double batteryLevel = 0.6;
        double bandwidth = 250.0;

        Satellite satellite = factory.createSatelliteWithParameter(name, batteryLevel, bandwidth);

        assertNotNull(satellite);
        assertTrue(satellite instanceof CommunicationSatellite);
        CommunicationSatellite comSat = (CommunicationSatellite) satellite;
        assertEquals(name, comSat.getName());
        assertEquals(batteryLevel, comSat.getEnergy().getBatteryLevel(), 0.0001);
        assertEquals(bandwidth, comSat.getBandWidth(), 0.0001);
    }
}
