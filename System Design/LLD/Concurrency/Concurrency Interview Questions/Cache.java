// Online Java Compiler
// Use this editor to write, compile and run your Java code online
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CompletionException;
import java.util.*;

interface CacheStorage<K, V> { 
    void put(K key, V value) throws Exception; 
    V get(K key) throws Exception; 
    void remove(K key) throws Exception; 
    boolean containsKey(K key) throws Exception; 
    int size() throws Exception; 
    int getCapacity(); 
}

interface DBStorage<K, V> { 
    void write(K key, V value) throws Exception; 
    V read(K key) throws Exception; 
    void delete(K key) throws Exception; 
}

class InMemoryCacheStorage<K, V> implements CacheStorage<K, V> { 
    private final Map<K, V> cache; 
    private final int capacity; 
  
    public InMemoryCacheStorage(int capacity) { 
        this.capacity = capacity; 
        this.cache = new ConcurrentHashMap<>(); 
    } 
  
    @Override 
    public void put(K key, V value) throws Exception { 
        cache.put(key, value); 
    } 
  
    @Override 
    public V get(K key) throws Exception { 
        if (!cache.containsKey(key)) { 
            throw new Exception("Key not in cache: " + key); 
        } 
        return cache.get(key); 
    } 
 
    @Override 
    public void remove(K key) throws Exception { 
        if (!cache.containsKey(key)) { 
            throw new Exception("Key not in cache: " + key); 
        } 
        cache.remove(key); 
    } 
  
    @Override 
    public boolean containsKey(K key) throws Exception { 
        return cache.containsKey(key); 
    } 
 
    @Override 
    public int size() throws Exception { 
        return cache.size(); 
    } 
 
    @Override 
    public int getCapacity() { 
        return capacity; 
    } 
}

class SimpleDBStorage<K, V> implements DBStorage<K, V> { 
    // This is a simple mock database using a ConcurrentHashMap. 
    private final Map<K, V> database = new ConcurrentHashMap<>(); 
  
    @Override 
    public void write(K key, V value) throws Exception { 
        database.put(key, value); 
    } 
 
    @Override 
    public V read(K key) throws Exception { 
        if (!database.containsKey(key)) { 
            throw new Exception("Key not found in DB: " + key); 
        } 
        return database.get(key); 
    } 
  
    @Override 
    public void delete(K key) throws Exception { 
        if (!database.containsKey(key)) { 
            throw new Exception("Key not found in DB: " + key); 
        } 
        database.remove(key); 
    } 
}

interface WritePolicy<K, V> { 
    /** 
     - Write a key/value pair to both cache storage and DB storage concurrently. 
     - This is the write‑through policy. 
     */ 
    void write(K key, V value, CacheStorage<K, V> cacheStorage, DBStorage<K, V> dbStorage) throws Exception; 
}

class WriteThroughPolicy<K, V> implements WritePolicy<K, V> { 
    @Override 
    public void write(K key, V value, CacheStorage<K, V> cacheStorage, DBStorage<K, V> dbStorage) throws Exception { 
        // Write to both cache and db concurrently and wait for both to complete. 
        CompletableFuture<Void> cacheFuture = CompletableFuture.runAsync(() -> { 
            try { 
                cacheStorage.put(key, value); 
            } catch (Exception e) { 
                throw new CompletionException(e); 
            } 
        }); 
  
        CompletableFuture<Void> dbFuture = CompletableFuture.runAsync(() -> { 
            try { 
                dbStorage.write(key, value); 
            } catch (Exception e) { 
                throw new CompletionException(e); 
            } 
        });  
        CompletableFuture.allOf(cacheFuture, dbFuture).join(); 
    } 
}

interface EvictionAlgorithm<K> { 
    /** 
     - Notifies the eviction algorithm that the given key was accessed. 
     */ 
    void keyAccessed(K key) throws Exception; 
 
    /** 
     - Selects and removes one key to be evicted (from the cache). 
     */ 
    K evictKey() throws Exception; 
}

class DoublyLinkedListNode<K> {

    private final K value;

    DoublyLinkedListNode<K> next;
    DoublyLinkedListNode<K> prev;

    public DoublyLinkedListNode(K value) {
        this.value = value;
        this.next = null;
        this.prev = null;
    }

    public K getValue() {
        return value;
    }
}

class DoublyLinkedList<K> {

    /*
     * Same concept as your C++ code:
     *
     * head = MRU
     * tail = LRU
     *
     * head <-> C <-> B <-> A <-> tail
     */

    private final DoublyLinkedListNode<K> head;
    private final DoublyLinkedListNode<K> tail;

    public DoublyLinkedList() {

        head = new DoublyLinkedListNode<>(null);
        tail = new DoublyLinkedListNode<>(null);

        head.next = tail;
        tail.prev = head;
    }

    /*
     * Same as your C++ insertAfterHead()
     */
    public void insertAfterHead(DoublyLinkedListNode<K> node) {

        DoublyLinkedListNode<K> headNext = head.next;

        head.next = node;

        node.prev = head;
        node.next = headNext;

        headNext.prev = node;
    }

     /*
     * Same as your C++ deleteNodeBeforeTail()
     */
    public void deleteNode(DoublyLinkedListNode<K> node) {

        DoublyLinkedListNode<K> prevNode = node.prev;
        DoublyLinkedListNode<K> nextNode = node.next;

        prevNode.next = nextNode;
        nextNode.prev = prevNode;

        node.prev = null;
        node.next = null;
    }

    /*
     * Same as:
     *
     * tail->prev
     *
     * in your C++ implementation.
     */
    public DoublyLinkedListNode<K> getLRUNode() {
        return tail.prev == head ? null : tail.prev;
    }
}

class LRUEvictionAlgorithm<K> implements EvictionAlgorithm<K> { 
    // A custom doubly linked list to track the LRU order. 
    private final DoublyLinkedList<K> dll; 
    // Map of key to its node in the linked list. 
    private final Map<K, DoublyLinkedListNode<K>> keyToNodeMap; 
 
    public LRUEvictionAlgorithm() { 
        this.dll = new DoublyLinkedList<>(); 
        this.keyToNodeMap = new HashMap<>(); 
    } 
  
    @Override 
    public synchronized void keyAccessed(K key) throws Exception { 
        if (keyToNodeMap.containsKey(key)) { 
           /*
             * Existing key.
             *
             * Same as:
             *
             * Node* node = mp[key];
             * deleteNodeBeforeTail(node);
             * insertAfterHead(node);
             */
            DoublyLinkedListNode<K> node = keyToNodeMap.get(key); 
            dll.deleteNode(node);

            dll.insertAfterHead(node); 
        } else { 
            /*
             * New key.
             *
             * Same as:
             *
             * Node* newNode = new Node(key,value);
             * mp[key] = newNode;
             * insertAfterHead(newNode);
             */
            DoublyLinkedListNode<K> newNode =
                    new DoublyLinkedListNode<>(key);

            keyToNodeMap.put(key, newNode);

            dll.insertAfterHead(newNode);
        } 
    } 
  
    @Override 
    public synchronized K evictKey() throws Exception { 
        // Evict the least recently used key (from the head). 
         DoublyLinkedListNode<K> nodeToEvict =
                dll.getLRUNode(); 
        if (nodeToEvict == null) { 
            return null; 
        } 
        K evictKey = nodeToEvict.getValue(); 
        dll.deleteNode(nodeToEvict); 
        keyToNodeMap.remove(evictKey); 
        return evictKey; 
    } 
}   

class KeyBasedExecutor{
    private final ExecutorService[] executors;
    private final int numExecutors;

    public KeyBasedExecutor(int numExecutors){
        this.numExecutors=numExecutors;
        this.executors=new ExecutorService[numExecutors];
        for (int i = 0; i < numExecutors; i++) { 
            executors[i]=Executors.newSingleThreadExecutor();
        }
    }
    /** 
     - Dispatch a task for a given key so that all tasks for that key run on the same single-thread executor. 
     */ 
    public <T> CompletableFuture<T> submitTask(Object key, Supplier<T> task) { 
        int index = getExecutorIndexForKey(key); 
        ExecutorService executor = executors[index]; 
        return CompletableFuture.supplyAsync(task, executor); 
    } 

     /** 
     - Determines the executor index for a key using a basic mod hash. 
     */ 
    public int getExecutorIndexForKey(Object key) { 
        return Math.abs(key.hashCode() % numExecutors); 
    } 

     /** 
     - Shuts down all executors. 
     */ 

    public void shutdown() { 
        for (ExecutorService executor : executors) { 
            executor.shutdown(); 
        } 
    } 
}

class Cache<K, V> { 
    private final CacheStorage<K, V> cacheStorage; 
    private final DBStorage<K, V> dbStorage; 
    private final WritePolicy<K, V> writePolicy; 
    private final EvictionAlgorithm<K> evictionAlgorithm; 
    private final KeyBasedExecutor keyBasedExecutor; 
 
    /** 
     - Constructs the cache. 
     - 
     - @param cacheStorage  The in-memory cache (with limited capacity). 
     - @param dbStorage     The underlying persistent storage (database). 
     - @param writePolicy   The write-through policy. 
     - @param evictionAlgorithm  The eviction strategy (custom LRU implementation). 
     - @param numExecutors  Number of single-thread executors for key-based dispatch. 
     */ 
    public Cache(CacheStorage<K, V> cacheStorage, DBStorage<K, V> dbStorage, 
                 WritePolicy<K, V> writePolicy, EvictionAlgorithm<K> evictionAlgorithm, 
                 int numExecutors) { 
        this.cacheStorage = cacheStorage; 
        this.dbStorage = dbStorage; 
        this.writePolicy = writePolicy; 
        this.evictionAlgorithm = evictionAlgorithm; 
        this.keyBasedExecutor = new KeyBasedExecutor(numExecutors); 
    } 

    /** 
     - Reads data from the cache for the given key. 
     - Updates the eviction algorithm as the key is accessed. 
     */ 

     public CompletableFuture<V> accessData(K key){
        return keyBasedExecutor.submitTask(key,()->{
            try{
                if (!cacheStorage.containsKey(key)) { 
                    throw new Exception("Key not found in cache: " + key); 
                }
                evictionAlgorithm.evictKey(); 
                return cacheStorage.get(key);
            }catch (Exception e) { 
                throw new CompletionException(e); 
            } 
        });
     }

    /** 
     - Writes (or updates) data in both the cache and DB storage using the write-through policy. 
     - If the key is new and the cache is at capacity, evicts the least recently used key from the cache. 
     */ 
    public CompletableFuture<Void> updateData(K key, V value) { 
        return keyBasedExecutor.submitTask(key,()->{
            try{
                if (cacheStorage.containsKey(key)) { 
                    writePolicy.write(key, value, cacheStorage, dbStorage);
                    evictionAlgorithm.keyAccessed(key);
                }
                else{

                    if (cacheStorage.size() >= cacheStorage.getCapacity()) { 
                        K evictedKey = evictionAlgorithm.evictKey(); 
                        if (evictedKey != null) { 
                            // Removal on the evicted key's executor to maintain ordering. 
                            int currentIndex = keyBasedExecutor.getExecutorIndexForKey(key); 
                            int evictedIndex = keyBasedExecutor.getExecutorIndexForKey(evictedKey);
                            if (currentIndex == evictedIndex) { 
                                cacheStorage.remove(evictedKey); 
                            }else{
                                CompletableFuture<Void> removalFuture = keyBasedExecutor.submitTask(evictedKey, () -> { 
                                    try { 
                                        cacheStorage.remove(evictedKey); 
                                        return null; 
                                    } catch (Exception ex) { 
                                        throw new CompletionException(ex); 
                                    } 
                                }); 
                                removalFuture.join(); 
                            }
                        }
                    }
                     // Write the new key/value concurrently to both storages. 
                    writePolicy.write(key, value, cacheStorage, dbStorage); 
                    evictionAlgorithm.keyAccessed(key); 
                }
                return null;
            }catch (Exception e) { 
                throw new CompletionException(e); 
            } 
        });
    }
    /** 
     - Shuts down all executors. 
     */ 
    public void shutdown() { 
        keyBasedExecutor.shutdown(); 
    } 
}




class Main {
    public static void main(String[] args) {
        try{
            // Set a small capacity for the in-memory cache (e.g., 5 items) 
            CacheStorage<String, String> cacheStorage = new InMemoryCacheStorage<>(5); 
            // The underlying persistent store (DB storage) can be assumed to have large or unlimited capacity. 
            DBStorage<String, String> dbStorage = new SimpleDBStorage<>(); 
            // Create the write-through policy (writes concurrently to both storages). 
            WritePolicy<String, String> writePolicy = new WriteThroughPolicy<>(); 
            // Create the LRU eviction algorithm. 
            EvictionAlgorithm<String> evictionAlg = new LRUEvictionAlgorithm<>(); 
            // Create the cache with 4 executor threads to guarantee per-key ordering. 
            Cache<String, String> cache = new Cache<>(cacheStorage, dbStorage, writePolicy, evictionAlg, 4); 
 
            // Demonstrate write operations. 
            cache.updateData("A", "Apple").join(); 
            cache.updateData("B", "Banana").join(); 
            cache.updateData("C", "Cherry").join(); 
            cache.updateData("D", "Durian").join(); 
            cache.updateData("E", "Elderberry").join(); 
 
            // At this point, the in-memory cache is at capacity. 
            // The next write will trigger eviction (of the least recently used key) from the cache. 
            cache.updateData("F", "Fig").join(); 

            // Demonstrate read operations. 
            try { 
                String valueA = cache.accessData("A").join(); 
                System.out.println("A: " + valueA); 
            } catch(Exception e) { 
                System.out.println("A is evicted or not found in cache."); 
            } 

            String valueF = cache.accessData("F").join(); 
            System.out.println("F: " + valueF); 
 
            // Update an existing key and then read it to demonstrate read-your-own-writes. 
            cache.updateData("B", "Blueberry").join(); 
            String valueB = cache.accessData("B").join(); 
            System.out.println("B: " + valueB); 

            // Shut down executors when finished. 
            cache.shutdown();  

        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
