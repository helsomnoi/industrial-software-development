public class Main {
    public static void main(String[] args) {
        System.out.println("ЗАПУСК СИСТЕМЫ УПРАВЛЕНИЯ СПУТНИКОВОЙ ГРУППИРОВКОЙ");
        System.out.println("=".repeat(25));

        System.out.println("СОЗДАНИЕ СПЕЦИАЛИЗИРОВАННЫХ СПУТНИКОВ:");
        System.out.println("-".repeat(15));
        CommunicationSatellite communication_1 = new CommunicationSatellite("Связь-1", 0.85, 500);
        CommunicationSatellite communication_2 = new CommunicationSatellite("Связь-2", 0.75, 1000);
        ImagingSatellite imaging_1 = new ImagingSatellite("ДЗЗ-1", 0.90, 2.5);
        ImagingSatellite imaging_2 = new ImagingSatellite("ДЗЗ-2", 0.45, 1);
        ImagingSatellite imaging_3 = new ImagingSatellite("ДЗЗ-3", 0.15, 0.5);
        System.out.println("-".repeat(15));

        SatelliteConstellation satelliteConstellation = new SatelliteConstellation("RU Basic");
        System.out.println("-".repeat(15));
        System.out.println("ФОРМИРОВАНИЕ ГРУППИРОВКИ:");
        satelliteConstellation.addSatellite(communication_1);
        satelliteConstellation.addSatellite(communication_2);
        satelliteConstellation.addSatellite(imaging_1);
        satelliteConstellation.addSatellite(imaging_2);
        satelliteConstellation.addSatellite(imaging_3);
        System.out.println("-".repeat(15));
        System.out.println(satelliteConstellation.getSatellites());
        System.out.println("-".repeat(15));

        System.out.println("АКТИВАЦИЯ СПУТНИКОВ:");
        System.out.println("-".repeat(10));
        for (Satellite satellite : satelliteConstellation.getSatellites()) {
            satellite.activate();
        }
        System.out.println("-".repeat(10));

        System.out.println("ВЫПОЛНЕНИЕ МИССИЙ ГРУППИРОВКИ " + satelliteConstellation.getCollectionName());
        System.out.println("=".repeat(20));
        satelliteConstellation.executeAllMission();
        System.out.println("-".repeat(15));
        System.out.println(satelliteConstellation.getSatellites());

        System.out.println("ДЕКТИВАЦИЯ СПУТНИКОВ:");
        System.out.println("-".repeat(10));
        for (Satellite satellite : satelliteConstellation.getSatellites()) {
            satellite.deactivate();
        }
        System.out.println("-".repeat(10));

        System.out.println(satelliteConstellation.getSatellites());
    }
}