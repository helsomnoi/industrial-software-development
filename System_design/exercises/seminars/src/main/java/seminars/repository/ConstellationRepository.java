package seminars.repository;

import seminars.SatelliteConstellation;

import org.springframework.stereotype.Repository;
import java.util.HashMap;
import java.util.Map;

@Repository
public class ConstellationRepository {
    private final Map<String, SatelliteConstellation> constellations = new HashMap<>();

    public void addConstellation(SatelliteConstellation constellation) {
        constellations.put(constellation.getConstellationName(), constellation);
        System.out.println("Сохранена группировака: " + constellation.getConstellationName());
    }

    public SatelliteConstellation getConstellation(String name){
        SatelliteConstellation constellation = constellations.get(name);
        if (constellation == null){
            throw new RuntimeException("Группировка не найдена: " + name);
        }
        return constellation;
    }

    public Map<String, SatelliteConstellation> getAllConstellations() {
        return new HashMap<>(constellations);
    }

    public boolean containsConstellation(String name){
        return constellations.containsKey(name);
    }

    public void removeConstellation(String name){
        constellations.remove(name);
        System.out.println("Удалена группировка " + name);
    }
}
