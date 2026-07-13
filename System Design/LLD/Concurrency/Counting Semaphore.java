import java.util.concurrent.Semaphore;

public class Main {
    private static final Semaphore resourcePool = new Semaphore(3); // Semaphore with 3 permits

    public static void main(String[] args) {
        for (int i = 1; i <= 5; i++) {
            final int threadNum = i;
            Thread t = new Thread(() -> accessResource("Thread-" + threadNum));
            t.start();
        }
    }

    private static void accessResource(String threadName) {
        try {
            System.out.println(threadName + " is attempting to acquire a permit.");
            resourcePool.acquire(); // Acquire a permit
            System.out.println(threadName + " acquired a permit.");
            Thread.sleep(2000); // Simulate resource usage
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            resourcePool.release(); // Release the permit
            System.out.println(threadName + " released the permit.");
        }
    }
}
