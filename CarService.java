
public class CarService {

    private int speed;

    public CarService() {
        speed = 0;
    }

    public void setSpeed(int speed)
            throws CarOperationException {

        if (speed < 0 || speed > 120) {
            throw new CarOperationException(
                "Speed must be between 0 and 120 km/h."
            );
        }

        this.speed = speed;
        System.out.println("Car speed set to " + speed + " km/h.");
    }

    public int getSpeed() {
        return speed;
    }
}