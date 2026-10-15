
public class CarMonitor implements Runnable {

    private int speed;
    private volatile boolean running = true;

    // Update speed safely between threads
    public synchronized void updateSpeed(int speed) {
        this.speed = speed;
    }

    // Read speed safely between threads
    public synchronized int getSpeed() {
        return speed;
    }

    // Stop the background monitor
    public void stopMonitoring() {
        running = false;
    }

    @Override
    public void run() {

        while (running) {

            int currentSpeed = getSpeed();

            System.out.println(
                    "Background Monitor: Speed = "
                            + currentSpeed + " km/h"
            );

            if (currentSpeed > 100) {
                System.out.println(
                        "Warning: High vehicle speed!"
                );
            }

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        System.out.println("Vehicle monitoring stopped.");
    }
}
