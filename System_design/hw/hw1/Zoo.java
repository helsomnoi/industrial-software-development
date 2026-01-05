import java.util.List;
import java.util.ArrayList;

public class Zoo implements ZooInfoProvider, AnimalInfoProvider, EnclosureInfoProvider, EmployeeInfoProvider{
    private String name;
    private String city;
    private List<Animal> allAnimals;
    private List<Enclosure> allEnclosures;
    private List<Employee> allEmployees;

    public Zoo(String name, String city) {
        this.name = name;
        this.city = city;
        allAnimals = new ArrayList<>();
        allEnclosures = new ArrayList<>();
        allEmployees = new ArrayList<>();
        System.out.println("Основан зоопарк: " + this.name + " в г. " + this.city);
    }

    @Override
    public String getName() { return name; }

    @Override
    public String getCity() { return city; }

    @Override
    public List<Animal> getAnimals() { return allAnimals; }

    @Override
    public List<Enclosure> getEnclosures() { return allEnclosures; }

    @Override
    public List<Employee> getEmployees() { return allEmployees; }

    public void addAnimal(Animal animal){
        if (!allAnimals.contains(animal) && animal!=null) {
            allAnimals.add(animal);
            System.out.println(animal.getName() + " ID " + animal.getId() + " добавлен в " + name);
        } else{
            System.out.println("Ошибка доабвления животного");
        }
    }

    public void deleteAnimal(Animal animal){
        if (allAnimals.contains(animal) && animal!=null) {
            allAnimals.remove(animal);
            System.out.println(animal.getName() + " ID " + animal.getId() + " убран из зоопарка ");
        } else{
            System.out.println("Ошибка удаления животного");
        }
    }

    public void addEnclosure(Enclosure enclosure){
        if (!allEnclosures.contains(enclosure) && enclosure!=null) {
            allEnclosures.add(enclosure);
            System.out.println( "Вольер с  ID " + enclosure.getId() + " добавлен в " + name);
        } else{
            System.out.println("Ошибка добавления животного");
        }
    }

    public void deleteEnclosure(Enclosure enclosure){
        if (allEnclosures.contains(enclosure) && enclosure!=null) {
            allEnclosures.remove(enclosure);
            System.out.println("Вольер с ID " + enclosure.getId() + " удален из зоопарка ");
        } else{
            System.out.println("Ошибка удаления вольера");
        }
    }

    public void addEmployee(Employee employee){
        if (!allEmployees.contains(employee) && employee!=null) {
            allEmployees.add(employee);
            System.out.println( "Работник " + employee.getName() + " с ID " + employee.getId() + " добавлен в " + name);
        } else{
            System.out.println("Ошибка добавления работника");
        }
    }

    public void deleteEmployee(Employee employee){
        if (allEmployees.contains(employee) && employee!=null) {
            allEmployees.remove(employee);
            System.out.println("Вольер с ID " + employee.getId() + " удален из зоопарка");
        } else{
            System.out.println("Ошибка удаления работника");
        }
    }

    public void feedAllAnimals(Feeder feeder){
        System.out.println("Работник " + feeder.getName() + " отправляется кормить животных!");
        for (Animal animal : allAnimals){
            feeder.feed(animal);
        }
    }

    public void clearAllEnclosures(Cleaner cleaner){
        System.out.println("Работник " + cleaner.getName() + " идет убирать в вольерах!");
        for (Enclosure enclosure : allEnclosures){
            cleaner.clean(enclosure);
        }
    }
    
    public void healAllAnimals(Veterinarian healer){
        System.out.println("Работник " + healer.getName() + " идет лечить животных!");
        for (Animal animal : allAnimals){
            healer.heal(animal);
        }
    }

    public void passDay() {
        System.out.println("--- Прошел еще один день в зоопарке ---");
        for (Enclosure enclosure : allEnclosures) {
            enclosure.processDailyPollution(); // Вызываем метод загрязнения

            // Если вольер слишком грязный, здоровье животных начинает падать
            if (enclosure.getDegreeOfPurity() < 20) {
                for (Animal animal : enclosure.getAnimals()) {
                    animal.setHealth(animal.getHealth() - 10);
                }
            }
        }
    }

}