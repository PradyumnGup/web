// Online Java Compiler
// Use this editor to write, compile and run your Java code online

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

interface IPublisher{
    String getId();
    void publish(String topicId,Message message)throws IllegalArgumentException;
}

interface ISubscriber{
    String getId();
    void onMessage(Message message)throws InterruptedException;
}

class SimpleSubscriber implements ISubscriber{
    private final String id;
    public SimpleSubscriber(String id){
        this.id=id;
    }

    @Override
    public String getId(){
        return id;
    }
    @Override
    public void onMessage(Message message) throws InterruptedException {
        // Processing the received message.
        System.out.println("Subscriber " + id + " received: " + message.getMessage());
        // Simulate processing delay if desired
	    Thread.sleep(500);
    }
}

class SimplePublisher implements IPublisher {
	    private final String id;
	    private final KafkaController kafkaController;

	    public SimplePublisher(String id, KafkaController kafkaController) {
	        this.id = id;
	        this.kafkaController = kafkaController;
	    }

	    @Override
	    public String getId() {
	        return id;
	    }
	    @Override
        public void publish(String topicId, Message message) throws IllegalArgumentException {
            kafkaController.publish(this, topicId, message);
            System.out.println("Publisher " + id + " published: " + message.getMessage() + " to topic " + topicId);
        }
}

class Message {
    private final String message;
    public Message(String message) {
        this.message = message;
    }
    public String getMessage() {
        return message;
    }
}

class TopicPublisher {
	    private final Topic topic;
	    private final IPublisher publisher;

	    public TopicPublisher(Topic topic, IPublisher publisher) {
	        this.topic = topic;
	        this.publisher = publisher;
	    }
	    public Topic getTopic() {
	        return topic;
	    }

	    public IPublisher getPublisher() {
	        return publisher;
	    }
}

class TopicSubscriber {
    private final Topic topic;
    private final ISubscriber subscriber;
    private final AtomicInteger offset;

    public TopicSubscriber(Topic topic, ISubscriber subscriber) {
        this.topic = topic;
        this.subscriber = subscriber;
        this.offset = new AtomicInteger(0);
    }

    public Topic getTopic() {
        return topic;
    }

    public ISubscriber getSubscriber() {
        return subscriber;
    }

    public AtomicInteger getOffset() {
        return offset;
    }
}



class Topic{
    private final String  topicId;// Name of the topic, used for identification/display purposes.
    private final String  topicName;// Unique identifier for the topic.
	    // List to store all messages published to this topic.
	    // This list is exposed to the outside using an immutable getter.
    private final List<Message>messages;

    public Topic(final String topicName, final String topicId) {
        this.topicName = topicName;
        this.topicId = topicId;
        this.messages = new ArrayList<>();
    }

    public String getTopicName() {
        return topicName;
    }

    public String getTopicId() {
        return topicId;
    }

    public synchronized void addMessage(Message message){
        messages.add(message);
    }

    public synchronized List<Message> getMessages() {
        return Collections.unmodifiableList(messages);
    }

    // Getters Section End
}

 class TopicPublisherController {
    private final Topic topic; //We could have used TopicPublisher also directly 
    private final IPublisher publisher;
    public TopicPublisherController(Topic topic, IPublisher publisher) {
        this.topic = topic;
        this.publisher = publisher;
    }
    // Synchronized publish method ensures thread-safe publishing for this topic.
    public synchronized void publish(Message message, KafkaController controller) {
        controller.publish(publisher, topic.getTopicId(), message);
        System.out.println("Publisher " + publisher.getId() + " published to topic " + topic.getTopicName());
    }
}

class TopicSubscriberController implements Runnable {
	    private final TopicSubscriber topicSubscriber;
	    public TopicSubscriberController(TopicSubscriber topicSubscriber) {
	        this.topicSubscriber = topicSubscriber;
	    }

	    @Override
	    public void run() {
	        Topic topic = topicSubscriber.getTopic();
	        ISubscriber subscriber = topicSubscriber.getSubscriber();
	        while (true) {
	            Message messageToProcess = null;
	            
	            synchronized (topicSubscriber) {
	                // Wait until there is a new message (offset is less than the number of messages)
	                while (topicSubscriber.getOffset().get() >= topic.getMessages().size()) {
	                    try {
	                        topicSubscriber.wait();
	                    } catch (InterruptedException e) {
	                        Thread.currentThread().interrupt();
	                        return;
	                    }
	                }
	                // Retrieve the next message and increment the offset
	                int currentOffset = topicSubscriber.getOffset().getAndIncrement();
	                messageToProcess = topic.getMessages().get(currentOffset);
	            }
	            // Process the message outside of the synchronized block & this processing can also be done in parallel in some other threads
	            try {
	                subscriber.onMessage(messageToProcess);
	            } catch (InterruptedException e) {
	                Thread.currentThread().interrupt();
	                return;
	            }
	        }
	    }
}

class KafkaController {
  // Map of topic IDs to Topic objects.
  private final Map<String, Topic> topics;
  // Map of topic IDs to their list of TopicSubscriber associations.
  private final Map<String, List<TopicSubscriber>> topicSubscribers;
  // ExecutorService to run subscriber tasks concurrently.
  private final ExecutorService subscriberExecutor;
  private final AtomicInteger topicIdCounter;

  public KafkaController() {
    topics = new ConcurrentHashMap<>();
    topicSubscribers = new ConcurrentHashMap<>();
    // Using a cached thread pool to dynamically manage threads.
    subscriberExecutor = Executors.newCachedThreadPool();
    topicIdCounter = new AtomicInteger(0);
  }

  public Topic createTopic(String topicName) {
    String topicId = String.valueOf(topicIdCounter.incrementAndGet());
    Topic topic = new Topic(topicName, topicId);
    topics.put(topicId, topic);
    topicSubscribers.put(topicId, new CopyOnWriteArrayList<>());
    System.out.println("Created topic: " + topicName + " with id: " + topicId);
    return topic;
  }

  public void subscribe(ISubscriber subscriber, String topicId) {
    Topic topic = topics.get(topicId);
    if (topic == null) {
      System.err.println("Topic with id " + topicId + " does not exist");
      return;
    }
    TopicSubscriber ts = new TopicSubscriber(topic, subscriber);
    topicSubscribers.get(topicId).add(ts);
    // Submit the subscriber task to the executor.
    subscriberExecutor.submit(new TopicSubscriberController(ts));
    System.out.println(
        "Subscriber " + subscriber.getId() + " subscribed to topic: " + topic.getTopicName());
  }

  public void publish(IPublisher publisher, String topicId, Message message) {
    Topic topic = topics.get(topicId);
    if (topic == null) {
      throw new IllegalArgumentException("Topic with id " + topicId + " does not exist");
    }
    topic.addMessage(message);
    // wake up each subscriber on its own monitor
    List<TopicSubscriber> subs = topicSubscribers.get(topicId);
    for (TopicSubscriber topicSubscriber : subs) {
      synchronized (topicSubscriber) {
        topicSubscriber.notify();
      }
    }
    System.out.println(
        "Message \"" + message.getMessage() + "\" published to topic: " + topic.getTopicName());
  }

  // Resets the offset for the given subscriber on the specified topic.
  public void resetOffset(String topicId, ISubscriber subscriber, int newOffset) {
    List<TopicSubscriber> subscribers = topicSubscribers.get(topicId);
    if (subscribers == null) {
      System.err.println("Topic with id " + topicId + " does not exist");
      return;
    }
    for (TopicSubscriber ts : subscribers) {
      if (ts.getSubscriber().getId().equals(subscriber.getId())) {
        ts.getOffset().set(newOffset);
        // Notify in case the subscriber thread is waiting.
        synchronized (ts) {
          ts.notify();
        }
        System.out.println("Offset for subscriber " + subscriber.getId() + " on topic "
            + ts.getTopic().getTopicName() + " reset to " + newOffset);
        break;
      }
    }
  }

  // Shutdown the ExecutorService gracefully.
  public void shutdown() {
    subscriberExecutor.shutdown();
    try {
      if (!subscriberExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
        subscriberExecutor.shutdownNow();
      }
    } catch (InterruptedException e) {
      subscriberExecutor.shutdownNow();
    }
  }
}



class Main {
    public static void main(String[] args) {
        KafkaController kafkaController = new KafkaController();
	        // Create topics.
	        Topic topic1 = kafkaController.createTopic("Topic1");
	        Topic topic2 = kafkaController.createTopic("Topic2");

	        // Create subscribers.
	        SimpleSubscriber subscriber1 = new SimpleSubscriber("Subscriber1");
	        SimpleSubscriber subscriber2 = new SimpleSubscriber("Subscriber2");
	        SimpleSubscriber subscriber3 = new SimpleSubscriber("Subscriber3");
	        // Subscribe: subscriber1 subscribes to both topics,
	        // subscriber2 subscribes to topic1, and subscriber3 subscribes to topic2.
	        kafkaController.subscribe(subscriber1, topic1.getTopicId());
	        kafkaController.subscribe(subscriber1, topic2.getTopicId());
	        kafkaController.subscribe(subscriber2, topic1.getTopicId());
	        kafkaController.subscribe(subscriber3, topic2.getTopicId());
	        // Create publishers.
	        SimplePublisher publisher1 = new SimplePublisher("Publisher1", kafkaController);
	        SimplePublisher publisher2 = new SimplePublisher("Publisher2", kafkaController);
	        // Publish some messages.
	        publisher1.publish(topic1.getTopicId(), new Message("Message m1"));
	        publisher1.publish(topic1.getTopicId(), new Message("Message m2"));
	        publisher2.publish(topic2.getTopicId(), new Message("Message m3"));

	        // Allow time for subscribers to process messages.
	        try {
	            Thread.sleep(5000);
	        } catch (InterruptedException e) {
	            e.printStackTrace();
	        }
	        publisher2.publish(topic2.getTopicId(), new Message("Message m4"));
	        publisher1.publish(topic1.getTopicId(), new Message("Message m5"));
	        // Reset offset for subscriber1 on topic1 (for example, to re-process messages).
	        kafkaController.resetOffset(topic1.getTopicId(), subscriber1, 0);
	        // Allow some time before shutting down.
	        try {
	            Thread.sleep(5000);
	        } catch (InterruptedException e) {
	            e.printStackTrace();
	        }
	        kafkaController.shutdown();
    }
}

//Output
// Created topic: Topic1 with id: 1
// Created topic: Topic2 with id: 2
// Subscriber Subscriber1 subscribed to topic: Topic1
// Subscriber Subscriber1 subscribed to topic: Topic2
// Subscriber Subscriber2 subscribed to topic: Topic1
// Subscriber Subscriber3 subscribed to topic: Topic2
// Message "Message m1" published to topic: Topic1
// Subscriber Subscriber1 received: Message m1
// Subscriber Subscriber2 received: Message m1
// Publisher Publisher1 published: Message m1 to topic 1
// Message "Message m2" published to topic: Topic1
// Publisher Publisher1 published: Message m2 to topic 1
// Message "Message m3" published to topic: Topic2
// Subscriber Subscriber3 received: Message m3
// Publisher Publisher2 published: Message m3 to topic 2
// Subscriber Subscriber1 received: Message m3
// Subscriber Subscriber1 received: Message m2
// Subscriber Subscriber2 received: Message m2
// Message "Message m4" published to topic: Topic2
// Publisher Publisher2 published: Message m4 to topic 2
// Message "Message m5" published to topic: Topic1
// Publisher Publisher1 published: Message m5 to topic 1
// Subscriber Subscriber3 received: Message m4
// Subscriber Subscriber1 received: Message m4
// Subscriber Subscriber2 received: Message m5
// Subscriber Subscriber1 received: Message m1
// Offset for subscriber Subscriber1 on topic Topic1 reset to 0
// Subscriber Subscriber1 received: Message m2
// Subscriber Subscriber1 received: Message m5

// === Code Execution Successful ===
