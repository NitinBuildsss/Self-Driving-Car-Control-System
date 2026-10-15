
public class TestMonitor {

    public static void main(String[] args)
            throws InterruptedException {

        CarMonitor monitor = new CarMonitor();

        Thread monitorThread = new Thread(monitor);
        monitorThread.start();

        Thread.sleep(1000);

        monitor.updateSpeed(60);

        Thread.sleep(3000);

        monitor.updateSpeed(120);

        Thread.sleep(3000);

        monitor.stopMonitoring();
        monitorThread.join();

        System.out.println("Test completed.");
    }
}