public class Lion extends Animal {
    private String voice = "Рррргх";

    public Lion(int id, String name, int health, int age, int weight, double foodAmountNeeded, String foodName, String schedule, int pollutionRate) {
        super(id, name, health, age, weight, foodAmountNeeded, foodName, schedule, pollutionRate);
    }

    public String getVoice() {
        System.out.println("Лев рычит: " + voice);
        return voice;
    }

    @Override
    public void eating(double amountOfFood, String food) {
        if (amountOfFood >= foodAmountNeeded && food.equals(foodName)) {
            setHealth(health + 20);
            System.out.println("Лев " + name + " сыт. Здоровье: " + health);
        } else {
            setHealth(health - 5);
            System.out.println("Лев " + name + " недоволен едой.");
        }
    }
}