public abstract class Satellite {
    protected String name;
    protected SatelliteState state;
    protected EnergySystem energy;

    public Satellite(String name, double batteryLevel) {
        this.name = name;
        this.state = new SatelliteState();
        this.energy = new EnergySystem(batteryLevel);
        System.out.println("Создан спутник: " + this.name + " (заряд: " + batteryLevel * 100  + ")");
    }

    public String getName() {
        return name;
    }

    public boolean getsActive() {
        return state.isActive();
    }

    public double getBatteryLevel() {
        return energy.getBatteryLevel();
    }

    public boolean activate() {
        if (energy.getBatteryLevel() > 0.2 && !state.isActive()) {
            state.setActive(true);
            System.out.println(name + ": Активация успешна!");
        } else System.out.println(name + ": Ошибка активации (заряд " + energy.getBatteryLevel() * 100 + "%)");
        return state.isActive();
    }

    public void deactivate() {
        if (state.isActive()) {
            state.setActive(false);
            System.out.println( name + ": деактивирован");
        }
    }

    public void consumeBattery(double batteryConsumed){
        energy.consume(batteryConsumed);
        if (energy.getBatteryLevel() <0.2 && state.isActive()) {
            System.out.println("Низкий заряд: " + name + " деактивируется");
            deactivate();
        }
    }

    protected abstract void performMission();

}