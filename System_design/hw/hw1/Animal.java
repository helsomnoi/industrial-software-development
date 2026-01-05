public abstract class Animal {
    protected  int id;
    protected  String name;
    protected  int health;
    protected int age;
    protected  int weight;
    protected double foodAmountNeeded;
    protected String foodName;
    protected String Schedule;
    protected int pollutionRate;

    public  Animal(int id, String name,  int health, int age,
                   int weight, double foodAmountNeeded,
                   String foodName, String Schedule, int pollutionRate) {
        this.id = id;
        this.name = name;
        this.health = Math.max(0, Math.min(health, 100));
        this.age = age;
        this.weight = weight;
        this.foodAmountNeeded = foodAmountNeeded;
        this.foodName = foodName;
        this.Schedule = Schedule;
        this.pollutionRate = pollutionRate;
    }

    public int getId() { return id; }

    public String getName() { return name; }

    public int getHealth() { return health; }

    public void setHealth(int health) { this.health = Math.max(0, Math.min(health, 100)); }

    public int getAge() { return age; }

    public int getWeight() { return weight; }

    public double getFoodAmountNeeded() { return foodAmountNeeded; }

    public String getFoodName() { return foodName; }

    public String getSchedule() { return Schedule; }

    public int getPollutionRate() { return pollutionRate; }

    public boolean isIllness() {
        return health < 50;
    }

    public abstract void eating(double amountOfFood, String food);
}