interface Healable{
    void heal(Animal animal);
    boolean checkHealth(Animal animal);
}

public class Veterinarian extends Employee implements Healable {
    public Veterinarian(int id, String name, String schedule) {
        super(id, name, schedule);
    }

    @Override
    public void heal(Animal animal) {
        if (checkHealth(animal)) {
            animal.setHealth(100);
            System.out.println("Животное " + animal.getName() + " вылечено ветеринаром " + getName());
        } else {
            System.out.println("Животное " + animal.getName() + " уже здорово.");
        }
    }

    @Override
    public boolean checkHealth(Animal animal) {
        return animal.isIllness();
    }
}