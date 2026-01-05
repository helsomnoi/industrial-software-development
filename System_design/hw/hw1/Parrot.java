public class Parrot extends Animal {
    private String voice;

    public Parrot(int id, String name, int health, int age, int weight, double foodAmountNeeded, String foodName,
                  String schedule, int pollutionRate, String voice){
        super(id, name, health, age, weight, foodAmountNeeded, foodName, schedule, pollutionRate);
        this.voice = voice;
    }

    public String getVoice() {
        System.out.println("Попугай говорит: " + voice);
        return voice;
    }

    public void eating(double amountOfFood, String food){
        if (amountOfFood >= foodAmountNeeded && food.equals(foodName)){
            this.setHealth(health + 3);
            System.out.println("Попугай " + name + " съел " + amountOfFood + " " + foodName);
            System.out.println("Здоровье "+ name + ": " + health);
        } else {
            System.out.println("Недостаточно еды или нужна другая");
            this.setHealth(health - 1);
            System.out.println("Здоровье "+ name + ": " + health);
        }
    }
}