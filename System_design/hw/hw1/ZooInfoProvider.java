import java.util.List;

public interface ZooInfoProvider{
    public String getName();
    public String getCity();
    List<Animal> getAnimals();
    List<Employee> getEmployees();
    List<Enclosure> getEnclosures();
}