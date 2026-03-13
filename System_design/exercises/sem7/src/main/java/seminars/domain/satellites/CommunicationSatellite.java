package seminars.domain.satellites;

import lombok.Getter;

@Getter
public class CommunicationSatellite  extends Satellite {
    private double bandwidth;

    public CommunicationSatellite(String name, double batteryLevel, double bandwidth){
        super(name, batteryLevel);
        this.bandwidth = bandwidth;
    }

    @Override
    public void performMission(){
        if (state.isActive()) {
            System.out.println(name + ": Передача данных со скоростью " + bandwidth + " Мбит/с");
            sendData(bandwidth);
            energy.consume(0.05);
        } else System.out.println(name + ": Не может отправить данные - не активен");
    }

    private void sendData(double data){
        if (state.isActive()){
            System.out.println(name + ": Отправил " + data + " Мбит данных!");
        }
    }

    public String toString(){
        return "CommunicationSatellite{" + "bandWidth=" + bandwidth + ", name=" + name
                + ", state=" + state
                + ", energy=" + energy + '}';
    }
}