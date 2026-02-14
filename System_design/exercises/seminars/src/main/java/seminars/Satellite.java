package seminars;

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

    public SatelliteState getState() {
        return state;
    }

    public EnergySystem getEnergy() {
        return energy;
    }

    public boolean activate() {
        if (state.activate(energy.hasSufficientPower())) {
            System.out.println(name + ": Активация успешна!");
            return true;
        } else System.out.println(name + ": Ошибка активации (заряд " + (int)(getEnergy().getBatteryLevel() * 100) + "%)");
        return false;
    }

    public void deactivate() {
        if (state.isActive()) {
            state.deactivate();
            System.out.println( name + ": деактивирован");
        }
    }

    protected abstract void performMission();

}