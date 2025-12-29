import java.util.List;
import java.util.ArrayList;

public class SatelliteConstellation {
    private String collectionName;
    private List<Satellite> satellites;

    public SatelliteConstellation(String collectionName){
        this.collectionName = collectionName;
        this.satellites = new ArrayList<>();
        System.out.println("Создана спутниковая группировка: " + this.collectionName);
    }

    public List<Satellite> getSatellites() {
        return satellites;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public void addSatellite(Satellite satelliteNew){
        if (!satellites.contains(satelliteNew) && satelliteNew!=null) {
            satellites.add(satelliteNew);
            System.out.println(satelliteNew.name + " добавлен в группировку '" + collectionName + "'");
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