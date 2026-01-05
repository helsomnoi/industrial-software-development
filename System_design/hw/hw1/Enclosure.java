import java.util.List;
import java.util.ArrayList;

public class Enclosure {
    private int id;
    private String kind;
    private List<Animal> animals;
    private int maxAmount;
    private int degreeOfPurity;

    public Enclosure(int id, String kind, int maxAmount, int degreeOfPurity) {
        this.id = id;
        this.kind = kind;
        this.maxAmount = maxAmount;
        this.degreeOfPurity = Math.max(0, Math.min(degreeOfPurity, 10));
        animals = new ArrayList<>();
    }

    public int getId() { return id; }

    public String getKind() { return kind; }

    public int getMaxAmount() { return maxAmount; }

    public int getDegreeOfPurity() { return degreeOfPurity; }

    public void setDegreeOfPurity(int degreeOfPurity) { this.degreeOfPurity = Math.max(0, Math.min(degreeOfPurity, 10)); }

    public List<Animal> getAnimals() {
        return animals;
    }

    public void setMaxAmount(int maxAmount) { this.maxAmount = maxAmount; }

    public boolean addAnimal(Animal animal){
        if (animals.size() + 1 <= maxAmount && animal!=null && !animals.contains(animal)){
            animals.add(animal);
            System.out.println("В вольер " + id + " добавлен " + animal.getClass() + " id - " + animal.getId() +
                    " по имени " + animal.getName());
            return true;
        } else {
            System.out.println("В вольере нет места!");
            return false;
        }
    }

    public boolean deleteAnimal(int animalId){
        Animal animalToRemove = null;
        for (Animal animal : animals) {
            if (animal.getId() == animalId) {
                animalToRemove = animal;
                break;
            }
        }
        if (animalToRemove != null) {
            animals.remove(animalToRemove);
            System.out.println("Животное " + animalId + " убрано из вольера " + id);
            return true;
        }
        System.out.println("В вольере нет указанного животного!");
        return false;
    }

    public void processDailyPollution() {
        int totalPollution = 0;
        for (Animal animal : animals) {
            totalPollution += animal.getPollutionRate();
        }
        degreeOfPurity = Math.max(0, degreeOfPurity - totalPollution);
    }
}