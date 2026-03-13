package seminars.domain.satellites;

import lombok.Getter;
import seminars.constants.EnergySystemConstants;

@Getter
public abstract class Satellite {
    protected String name;
    protected SatelliteState state;
    protected EnergySystem energy;

    public Satellite(String name, double batteryLevel) {
        this.name = name;
        this.state = new SatelliteState();
        this.energy = EnergySystem.builder()
                .batteryLevel(batteryLevel)
                .LOW_BATTERY_THRESHOLD(EnergySystemConstants.LOW_BATTERY_THRESHOLD)
                .MAX_BATTERY(EnergySystemConstants.MAX_BATTERY)
                .MIN_BATTERY(EnergySystemConstants.MIN_BATTERY)
                .build();
        System.out.println("Создан спутник: " + this.name + " (заряд: " + batteryLevel * 100  + ")");
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

    public abstract void performMission();

}