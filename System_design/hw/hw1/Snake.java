public class Snake extends Animal {
    private double length;
    private String voice;

    public Snake(int id, String name, int health, int age, int weight, double foodAmountNeeded, String foodName,
                  String schedule, int pollutionRate,  double length){
        super(id, name, health, age, weight, foodAmountNeeded, foodName, schedule, pollutionRate);
        this.length = length;
        this.voice = "Шшссшсш";
    }

    public double getLength() {
        return length;
    }

    public String getVoice() {
        System.out.println("Змея шипит: " + voice);
        return voice;
    }

    public void eating(double amountOfFood, String food){
        if (amountOfFood >= foodAmountNeeded && food.equals(foodName)){
            this.setHealth(health + 5);
            System.out.println("Змея " + name + " съела " + amountOfFood + " " + foodName);
            System.out.println("Здоровье "+ name + ": " + health);
        } else {
            System.out.println("Недостаточно еды или нужна другая");
            this.setHealth(health - 3);
            System.out.println("Здоровье "+ name + ": " + health);
        }
    }
}