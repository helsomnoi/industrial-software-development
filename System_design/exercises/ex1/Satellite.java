public abstract class Satellite {
    protected String name;
    protected boolean isActive;
    protected double batteryLevel;

    public Satellite(String name, double batteryLevel) {
        this.name = name;
        this.isActive = false;
        this.batteryLevel = batteryLevel;
        System.out.println("Создан спутник: " + this.name + " (заряд: " + batteryLevel * 100  + ")");
    }

    public String getName() {
        return name;
    }

    public boolean getsActive() {
        return isActive;
    }

    public double getBatteryLevel() {
        return batteryLevel;
    }

    public boolean activate() {
        if (batteryLevel > 0.2 && !isActive) {
            isActive = true;
            System.out.println(name + ": Активация успешна!");
        } else System.out.println(name + ": Ошибка активации (заряд " + batteryLevel * 100 + "%)");
        return isActive;
    }

    public void deactivate() {
        if (isActive) {
            isActive = false;
            System.out.println( name + ": деактивирован");
        }
    }

    public void consumeBattery(double batteryConsumed){
        if (batteryConsumed > 0){
            batteryLevel = Math.max(0.0, batteryLevel - batteryConsumed);
            if (batteryLevel < 0.2) {
                System.out.println("Низкий заряд: " + name + " деактивируется");
                deactivate();
            }
        }

    }

    protected abstract void performMission();

}