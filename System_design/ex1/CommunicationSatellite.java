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
        if (isActive) {
            System.out.println(name + ": Передача данных со скоростью " + bandWidth + " Мбит/с");
            sendData(bandWidth);
            consumeBattery(0.05);
        } else System.out.println(name + ": Не может отправить данные - не активен");
    }

    private void sendData(double data){
        if (isActive){
            System.out.println(name + ": Отправил " + data + " Мбит данных!");
        }
        return;
    }

    public String toString(){
        return "ImagingSatellite{" + "bandWidth=" + bandWidth + ", name=" + name + ", isActive=" + isActive
                + ", batteryLevel=" + batteryLevel + '}';
    }
}