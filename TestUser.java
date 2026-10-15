
public class TestUser {

    public static void main(String[] args) {

        CarService car = new CarService();

        try {
            car.setSpeed(60);
            car.setSpeed(150);
        } catch (CarOperationException e) {
            System.out.println("Error: " + e.getMessage());
        }

        System.out.println(
            "Current speed: " + car.getSpeed() + " km/h."
        );
    }
}