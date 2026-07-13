import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

class ReentrantLockExecutorExample {
  private int counter = 0;

  // Create a ReentrantLock instance.
  private final ReentrantLock lock = new ReentrantLock();

  // Method to increment the counter using the lock.
  public void increment() {
    lock.lock();
    try {
      System.out.println(Thread.currentThread().getName() + " acquired the lock.");
      counter++;
      System.out.println(Thread.currentThread().getName() + " incremented counter to: " + counter);
    } finally {
      System.out.println(Thread.currentThread().getName() + " released the lock.");
      lock.unlock();
    }
  }

  public int getCounter() {
    return counter;
  }
}

class Main {
    public static void main(String[] args) {
       ReentrantLockExecutorExample example= new ReentrantLockExecutorExample();
       // Create an ExecutorService with a fixed thread pool of 5 threads.
       ExecutorService executorService = Executors.newFixedThreadPool(5);
       //create 5 tasks
       for (int i=0;i<5;i++){
           executorService.submit(()->{ example.increment();});
       }
       // Shutdown the executor service gracefully.
       executorService.shutdown();
       
       try{
           // Wait for all tasks to finish; if not completed within 5 seconds, then exit.
           if(executorService.awaitTermination(5,TimeUnit.SECONDS)){
               System.out.println("Final counter value: " + example.getCounter());
           }else {
              System.out.println("Timeout: Not all tasks finished.");
            }
       }catch(InterruptedException e){
           System.err.println("Interrupted while waiting for tasks to finish.");
           Thread.currentThread().interrupt();
       }
    }
}
