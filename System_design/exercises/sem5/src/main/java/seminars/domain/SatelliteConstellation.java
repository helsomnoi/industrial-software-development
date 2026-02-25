package seminars.domain;

import java.util.ArrayList;
import java.util.List;

public class SatelliteConstellation {
    private String constellationName;
    private List<Satellite> satellites;

    public SatelliteConstellation(String collectionName){
        this.constellationName = collectionName;
        this.satellites = new ArrayList<>();
        System.out.println("Создана спутниковая группировка: " + this.constellationName);
    }

    public List<Satellite> getSatellites() {
        return satellites;
    }

    public String getConstellationName() {
        return constellationName;
    }

    public void addSatellite(Satellite satelliteNew){
        if (!satellites.contains(satelliteNew) && satelliteNew!=null) {
            satellites.add(satelliteNew);
            System.out.println(satelliteNew.name + " добавлен в группировку '" + constellationName + "'");
        }
    }

    public void executeAllMission(){
        for (Satellite satelliteOne : this.satellites){
            satelliteOne.performMission();
        }
        return;
    }

    public String toString() {
        return satellites.toString();
    }
}