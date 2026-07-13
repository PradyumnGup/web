import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class ReadWriteLogExample {
  private int logValue = 0; 

  // Create a ReentrantLock instance.
  private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
  
  private void simulateWork(){
      int sum=0;
      for(int i=0;i<50000;i++){
          sum+=i;
      }
      
  }
  
  public void writeValue(String taskName,int newValue){
      rwLock.writeLock().lock();;
      try{
          System.out.println(taskName + " (write): Acquired write lock."); 
          simulateWork();
          logValue=newValue;
          System.out.println(taskName + " (write): Updated logValue to " + logValue);
      }
      finally{
          System.out.println(taskName + " (write): Released write lock.");
          rwLock.writeLock().unlock();
      }
  }

  public void readValue(String taskName){
      rwLock.readLock().lock();;
      try{
          System.out.println(taskName + " (read): Acquired read lock. Reading logValue: " + logValue); 
          simulateWork();
          System.out.println(taskName + " (read): Finished reading.");
      }
      finally{
          System.out.println(taskName + " (read): Released read lock."); 
          rwLock.readLock().unlock();
      }
  }

  
}

class Main {
    public static void main(String[] args) {
       ReadWriteLogExample logExample= new ReadWriteLogExample();
       // Create an ExecutorService with a fixed thread pool of 5 threads.
       ExecutorService executor = Executors.newFixedThreadPool(4);
       // Submit two concurrent reader tasks. 
        executor.submit(() -> logExample.readValue("Reader-2")); 
        executor.submit(() -> logExample.readValue("Reader-3")); 

        // Submit a writer task. 
        executor.submit(() -> logExample.writeValue("Writer-1", 100)); 

        // Submit two additional reader tasks. 
        executor.submit(() -> logExample.readValue("Reader-4")); 
        executor.submit(() -> logExample.readValue("Reader-5")); 

        // Submit a second writer task. 
        executor.submit(() -> logExample.writeValue("Writer-2", 200)); 

        // Submit a final reader task. 
        executor.submit(() -> logExample.readValue("Reader-6")); 

        // Shut down the executor. 
       // Shutdown the executor service gracefully.
       executor.shutdown();
       
       try { 
            if (!executor.awaitTermination(10, TimeUnit.SECONDS)) { 
                System.out.println("Timeout waiting for tasks to finish."); 
            } 
        } catch (InterruptedException e) { 
            Thread.currentThread().interrupt(); 
        } 
    }
}
