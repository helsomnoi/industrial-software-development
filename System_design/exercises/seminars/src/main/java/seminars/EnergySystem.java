package seminars;

public class EnergySystem {
    private double batteryLevel;

    private static final double LOW_BATTERY_THRESHOLD = 0.2;
    private static final double MAX_BATTERY_LEVEL = 1.0;
    private static final double MIN_BATTERY_RATE = 0.0;

    public EnergySystem(double initbatteryLevel) {
        this.batteryLevel = Math.max(MIN_BATTERY_RATE, Math.min(initbatteryLevel,  MAX_BATTERY_LEVEL));
    }

    public double getBatteryLevel(){
        return batteryLevel;
    }

    public boolean consume(double batteryConsumed){
        if (batteryConsumed < 0 || batteryLevel < MIN_BATTERY_RATE){
            return false;
        }
        batteryLevel = Math.max(0.0, batteryLevel - batteryConsumed);
        return true;
    }

    public boolean hasSufficientPower(){
        return batteryLevel > LOW_BATTERY_THRESHOLD;
    }

    @Override
    public String toString(){
        return "EnergySystem{ " + "batteryLevel=" + batteryLevel + " }";
    }
}