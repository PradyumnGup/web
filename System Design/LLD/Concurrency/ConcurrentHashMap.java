// Online Java Compiler
// Use this editor to write, compile and run your Java code online
import java.util.concurrent.*;
import java.util.*;

class Main {
    public static void main(String[] args) throws InterruptedException {
        final Map<Integer,String> hashMap = Collections.synchronizedMap(new HashMap<>());
        final ConcurrentHashMap<Integer,String> concurrentMap = new ConcurrentHashMap<>();
        
        Thread hashMapUpdater= new Thread(()->{
            // No explicit manual lock is needed for put() as synchronizedMap wraps each operation.
            for (int i = 1; i <= 5; i++) {
                hashMap.put(i, "Value " + i);
                 try {
                   Thread.sleep(50); // simulate some processing time
                 } catch (InterruptedException e) {
                   Thread.currentThread().interrupt();
                 }
            }
        });
        
        Thread hashMapIterator= new Thread(()->{
            // Wait a short while so that some entries are added.
             try {
               Thread.sleep(50); // simulate some processing time
             } catch (InterruptedException e) {
               Thread.currentThread().interrupt();
             }
             
             synchronized(hashMap){
                 for(Map.Entry<Integer,String>entry:hashMap.entrySet()){
                     System.out.println(
                      "hashMap Iteration - Key: " + entry.getKey() + ", Value: " + entry.getValue());
                 }
             }
        });
        
        // Start both threads and wait for them to finish.
        hashMapUpdater.start();
        hashMapIterator.start();
        hashMapUpdater.join();
        hashMapIterator.join();
        System.out.println("Final hashMap: " + hashMap);
    
        // ----- Example 2: Using ConcurrentHashMap -----
        
        Thread concurrentMapUpdater = new Thread(() -> {
          for (int i = 1; i <= 5; i++) {
            concurrentMap.put(i, "Value " + i);
            try {
              Thread.sleep(50); // simulate some processing time
            } catch (InterruptedException e) {
              Thread.currentThread().interrupt();
            }
          }
        });
    
        Thread concurrentMapIterator = new Thread(() -> {
          // Wait a short while so that some entries are added.
          try {
            Thread.sleep(25);
          } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
          }
    
          // With ConcurrentHashMap, iteration is safe without any external synchronization.
          for (Map.Entry<Integer, String> entry : concurrentMap.entrySet()) {
            System.out.println(
                "concurrentMap Iteration - Key: " + entry.getKey() + ", Value: " + entry.getValue());
          }
        });
    
        // Start both threads and wait for them to finish.
        concurrentMapUpdater.start();
        concurrentMapIterator.start();
        concurrentMapUpdater.join();
        concurrentMapIterator.join();
        System.out.println("Final concurrentMap: " + concurrentMap);
    }
}
