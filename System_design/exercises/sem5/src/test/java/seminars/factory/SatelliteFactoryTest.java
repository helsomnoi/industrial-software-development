package seminars.factory;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import seminars.domain.CommunicationSatellite;
import seminars.domain.ImagingSatellite;
import seminars.domain.Satellite;
import seminars.factory.Imp.CommunicationSatelliteFactory;
import seminars.factory.Imp.ImagingSatelliteFactory;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Тесты фабрик спутников")
public class SatelliteFactoryTest {

    public static SatelliteFactory communicationFactory;
    public static SatelliteFactory imagingFactory;


    @BeforeAll
    static void beforeAll() {
        communicationFactory = new CommunicationSatelliteFactory();
        imagingFactory = new ImagingSatelliteFactory();
    }

    @Test
    @DisplayName("Фабрика спутников связи создает спутник с дефолтным параметром")
    void communicationSatelliteFactoryCreateSatelliteWithDefaultParameter() {
        String name = "Связь-1";
        double batteryLevel = 0.8;

        Satellite satellite = communicationFactory.createSatellite(name, batteryLevel);

        assertNotNull(satellite);
        assertInstanceOf(CommunicationSatellite.class, satellite);
        assertEquals(name, satellite.getName());
        assertEquals(batteryLevel, satellite.getEnergy().getBatteryLevel(), 0.001);
    }

    @Test
    @DisplayName("Фабрика спутников ДЗЗ создает спутник с дефолтным параметром")
    void imagingSatelliteFactoryCreateSatelliteWithDefaultParameter() {
        String name = "ДЗЗ-1";
        double batteryLevel = 0.8;

        Satellite satellite = imagingFactory.createSatellite(name, batteryLevel);

        assertNotNull(satellite);
        assertInstanceOf(ImagingSatellite.class, satellite);
        assertEquals(name, satellite.getName());
        assertEquals(batteryLevel, satellite.getEnergy().getBatteryLevel(), 0.001);
    }

    @Test
    @DisplayName("Активация спутников с достаточным зарядом")
    void setellitesWithHightBatteryActivated() {
        Satellite commSatellite = communicationFactory.createSatellite("Связь-1", 0.45);
        Satellite imagingSatellite = imagingFactory.createSatellite("ДЗЗ-1", 0.8);

        assertTrue(commSatellite.activate());
        assertTrue(commSatellite.getState().isActive());


        assertTrue(imagingSatellite.activate());
        assertTrue(imagingSatellite.getState().isActive());
    }

    @Test
    @DisplayName("Спутники с низким зарядом не могут быть активированы")
    void setellitesWithLowBatteryCannotBeActivated() {
        Satellite commSatellite = communicationFactory.createSatellite("Слабый-Связь-1", 0.15);
        Satellite imagingSatellite = imagingFactory.createSatellite("Слабый-ДЗЗ-1", 0.05);

        assertFalse(commSatellite.activate());
        assertFalse(commSatellite.getState().isActive());


        assertFalse(imagingSatellite.activate());
        assertFalse(imagingSatellite.getState().isActive());
    }





}
