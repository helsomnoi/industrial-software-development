package seminars.domain.satellites;

import lombok.Builder;
import lombok.Getter;

@Builder
public class EnergySystem {
    @Getter
    private double batteryLevel;

    private final double LOW_BATTERY_THRESHOLD;
    private final double MAX_BATTERY;
    private final double MIN_BATTERY;

//    public EnergySystem(double initbatteryLevel) {
//        this.batteryLevel = Math.max(MIN_BATTERY_RATE, Math.min(initbatteryLevel,  MAX_BATTERY_LEVEL));
//    }

    public void consume(double batteryConsumed){
        if (batteryConsumed < 0 || batteryLevel < MIN_BATTERY){
            return;
        }
        batteryLevel = Math.max(0.0, batteryLevel - batteryConsumed);
    }

    public boolean hasSufficientPower(){
        return batteryLevel > LOW_BATTERY_THRESHOLD;
    }

    @Override
    public String toString(){
        return String.format("EnergySystem{ batteryLevel=%.2f}", batteryLevel);
    }
}