import java.util.concurrent.Semaphore;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Lock;

 class SemaphoreVsLockExample{ 
	    private final Semaphore semaphore = new Semaphore(3); // Allows up to 3 threads concurrently 
	    private final Lock lock = new ReentrantLock(); 
	    // Using Semaphore 
	    public void accessWithSemaphore() { 
	        try { 
	            semaphore.acquire(); // Acquire a permit; up to 3 threads can acquire concurrently 
	            System.out.println(Thread.currentThread().getName() + " accessing resource with Semaphore"); 
	            Thread.sleep(1000); // Simulate work 
	        } catch (InterruptedException e) { 
	            e.printStackTrace(); 
	        } finally { 
	            System.out.println(Thread.currentThread().getName() + " releasing Semaphore permit"); 
	            semaphore.release(); // Release the permit 
	        } 
	    } 
	    // Using Lock 
	    public void accessWithLock() { 
	        lock.lock(); // Acquire the lock (only one thread can hold it) 
	        try { 
	            System.out.println(Thread.currentThread().getName() + " accessing resource with Lock"); 
	            Thread.sleep(1000); // Simulate work 
	        } catch (InterruptedException e) { 
	            e.printStackTrace(); 
	        } finally { 
	            System.out.println(Thread.currentThread().getName() + " unlocking Lock"); 
	            lock.unlock(); // Release the lock 
	        } 
	    } 
}
class Main {
    public static void main(String[] args) {
         SemaphoreVsLockExample example = new SemaphoreVsLockExample(); 
	        // Create and start threads with descriptive names 
	        for (int i = 1; i <= 5; i++) { 
	            Thread semaphoreThread = new Thread(example::accessWithSemaphore, "SemaphoreThread-" + i); 
	            Thread lockThread = new Thread(example::accessWithLock, "LockThread-" + i); 
	            semaphoreThread.start(); 
	            lockThread.start(); 
	        } 
    }
}
