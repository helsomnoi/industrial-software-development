package seminars.factory;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import seminars.domain.satellites.CommunicationSatellite;
import seminars.domain.satellites.ImagingSatellite;
import seminars.domain.satellites.Satellite;
import seminars.domain.parametrs.CommunicationSatelliteParam;
import seminars.domain.parametrs.ImagingSatelliteParam;
import seminars.factory.Impl.CommunicationSatelliteFactory;
import seminars.factory.Impl.ImagingSatelliteFactory;

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
    @DisplayName("Фабрика спутников связи создает спутник с заданными параметрами")
    void communicationSatelliteFactoryCreateSatelliteWithDefaultParameter() {
        String name = "Связь-1";
        double batteryLevel = 0.8;
        double bandwidth = 500.0;

        Satellite satellite = communicationFactory.createSatelliteWithParameter(
                new CommunicationSatelliteParam(name, batteryLevel, bandwidth)
        );

        assertNotNull(satellite);
        assertInstanceOf(CommunicationSatellite.class, satellite);
        assertEquals(name, satellite.getName());
        assertEquals(batteryLevel, satellite.getEnergy().getBatteryLevel(), 0.001);
        CommunicationSatellite commSat =  (CommunicationSatellite) satellite;
        assertEquals(bandwidth, commSat.getBandwidth(), 0.001);
    }

    @Test
    @DisplayName("Фабрика спутников ДЗЗ создает спутник с заданными параметром")
    void imagingSatelliteFactoryCreateSatelliteWithDefaultParameter() {
        String name = "ДЗЗ-1";
        double batteryLevel = 0.8;
        double resolution = 100.0;

        Satellite satellite = imagingFactory.createSatelliteWithParameter(
                new ImagingSatelliteParam(name, batteryLevel, resolution)
        );

        assertNotNull(satellite);
        assertInstanceOf(ImagingSatellite.class, satellite);
        assertEquals(name, satellite.getName());
        assertEquals(batteryLevel, satellite.getEnergy().getBatteryLevel(), 0.001);
        ImagingSatellite imgSat =  (ImagingSatellite) satellite;
        assertEquals(resolution, imgSat.getResolution(), 0.001);
    }

    @Test
    @DisplayName("Активация спутников с достаточным зарядом")
    void satellitesWithHightBatteryActivated() {
        Satellite commSatellite = communicationFactory.createSatelliteWithParameter(
                new CommunicationSatelliteParam("Связь-1", 0.45, 500.0)
        );
        Satellite imagingSatellite = imagingFactory.createSatelliteWithParameter(
                new ImagingSatelliteParam("ДЗЗ-1", 0.8, 100)
        );

        assertTrue(commSatellite.activate());
        assertTrue(commSatellite.getState().isActive());


        assertTrue(imagingSatellite.activate());
        assertTrue(imagingSatellite.getState().isActive());
    }

    @Test
    @DisplayName("Спутники с низким зарядом не могут быть активированы")
    void satellitesWithLowBatteryCannotBeActivated() {
        Satellite commSatellite = communicationFactory.createSatelliteWithParameter(
                new CommunicationSatelliteParam("Связь-1", 0.05, 500.0)
        );
        Satellite imagingSatellite = imagingFactory.createSatelliteWithParameter(
                new ImagingSatelliteParam("ДЗЗ-1", 0.15, 100)
        );

        assertFalse(commSatellite.activate());
        assertFalse(commSatellite.getState().isActive());


        assertFalse(imagingSatellite.activate());
        assertFalse(imagingSatellite.getState().isActive());
    }





}
