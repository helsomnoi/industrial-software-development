public class Pike extends Animal {

    public Pike(int id, String name, int health, int age, int weight, double foodAmountNeeded, String foodName, String schedule,  int pollutionRate){
        super(id, name, health, age, weight, foodAmountNeeded, foodName, schedule, pollutionRate);
    }

    public void eating(double amountOfFood, String food){
        if (amountOfFood >= foodAmountNeeded && food.equals(foodName)){
            this.setHealth(health + 3);
            System.out.println("Щука " + name + " съела " + amountOfFood + " " + foodName);
            System.out.println("Здоровье "+ name + ": " + health);
        } else {
            System.out.println("Недостаточно еды или нужна другая");
            this.setHealth(health - 1);
            System.out.println("Здоровье "+ name + ": " + health);
        }
    }
}