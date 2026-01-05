interface Feedable{
    void feed(Animal animal);
}

public class Feeder extends Employee implements Feedable{
    public Feeder(int id, String name, String schedule) {
        super(id, name, schedule);
    }

    public void feed(Animal animal){
        double foodAmount = animal.getFoodAmountNeeded();
        String food = animal.getFoodName();
        animal.eating(foodAmount, food);
    }
}