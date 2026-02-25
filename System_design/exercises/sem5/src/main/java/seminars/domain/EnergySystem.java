package seminars.domain;

import lombok.Builder;

@Builder
public class EnergySystem {
    private double batteryLevel;
    private final double LOW_BATTERY_THRESHOLD;
    private final double MAX_BATTERY;
    private final double MIN_BATTERY;

//    public EnergySystem(double initbatteryLevel) {
//        this.batteryLevel = Math.max(MIN_BATTERY_RATE, Math.min(initbatteryLevel,  MAX_BATTERY_LEVEL));
//    }

    public double getBatteryLevel(){
        return batteryLevel;
    }

    public boolean consume(double batteryConsumed){
        if (batteryConsumed < 0 || batteryLevel < MIN_BATTERY){
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
        return String.format("EnergySystem{ batteryLevel=%.2f}", batteryLevel);
    }
}