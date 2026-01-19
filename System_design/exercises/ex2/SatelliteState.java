public class SatelliteState  {
    private boolean isActive;

    public SatelliteState(){
        isActive = false;
    }

    public boolean isActive() {
        return isActive;
    }
    public void setActive(boolean active) {
        this.isActive = active;
    }
}