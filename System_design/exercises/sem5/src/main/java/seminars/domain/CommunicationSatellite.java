package seminars.domain;

public class CommunicationSatellite  extends Satellite {
    private double bandWidth;

    public CommunicationSatellite(String name, double batteryLevel, double widthBand){
        super(name, batteryLevel);
        this.bandWidth = widthBand;
    }

    public double getBandWidth(){
        return bandWidth;
    }

    @Override
    public void performMission(){
        if (state.isActive()) {
            System.out.println(name + ": Передача данных со скоростью " + bandWidth + " Мбит/с");
            sendData(bandWidth);
            energy.consume(0.05);
        } else System.out.println(name + ": Не может отправить данные - не активен");
    }

    private void sendData(double data){
        if (state.isActive()){
            System.out.println(name + ": Отправил " + data + " Мбит данных!");
        }
    }

    public String toString(){
        return "seminars.domain.ImagingSatellite{" + "bandWidth=" + bandWidth + ", name=" + name
                + ", state=" + state
                + ", energy=" + energy + '}';
    }
}