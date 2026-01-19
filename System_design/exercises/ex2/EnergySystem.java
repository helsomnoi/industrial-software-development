public class EnergySystem {
    private double batteryLevel;

    public EnergySystem(double batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    public double getBatteryLevel(){
        return batteryLevel;
    }

    public void consume(double batteryConsumed){
        if (batteryConsumed > 0){
            batteryLevel = Math.max(0.0, batteryLevel - batteryConsumed);
        }
    }
}