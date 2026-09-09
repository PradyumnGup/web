// Online Java Compiler
import java.util.*;
// Use this editor to write, compile and run your Java code online
enum Direction {
    IDLE,
    UP,
    DOWN
}

enum ElevatorState{
    IDLE,         // The elevator is not moving, waiting for requests
    MOVING,       // The elevator is in motion (either up or down)
    STOPPED,      // The elevator has temporarily stopped (e.g., at a floor)
    MAINTENANCE   // The elevator is out of service and undergoing maintenance
}


class Building {
  private String name; // Name of the building
  private int numberOfFloors; // Total number of floors in the building
  private ElevatorController
      elevatorController; // Controller to manage all elevators in the building
 public Building(String name, int numberOfFloors, int numberOfElevators) {
        this.name = name; // Assign the building's name
        this.numberOfFloors = numberOfFloors; // Set the total number of floors
    // Initialize the elevator controller with the specified number of elevators
    // and floors
        this.elevatorController =
            new ElevatorController(numberOfElevators, numberOfFloors);
    }
    
    // Getters and Setters for the Building
    public String getName() {
        return name;
    }
    
    public int getNumberOfFloors() {
        return numberOfFloors;
    }
    
    public ElevatorController getElevatorController() {
        return elevatorController;
}
}


class Elevator {
  private int id; // Unique identifier for the elevator
  private int currentFloor;
  // Current direction of the elevator (UP, DOWN, or IDLE)
  private Direction direction;
    // Current operational state of the elevator (IDLE, MOVING, etc.)
  private ElevatorState state;
    // List of observers to monitor elevator events
  private List<ElevatorObserver> observers;
    // Queue to manage all requests (both internal and external)
  private Queue<ElevatorRequest> requests;
    // Constructor to initialize the elevator
  public Elevator(int id) {
    this.id = id;
    this.currentFloor = 1; // Default initial floor
    this.direction = Direction.IDLE;
    this.state = ElevatorState.IDLE;
    this.observers = new ArrayList<>();
    this.requests = new LinkedList<>();
  }
  // Add an observer to monitor elevator events
  public void addObserver(ElevatorObserver observer) {
    observers.add(observer);
  }

  // Remove an observer
  public void removeObserver(ElevatorObserver observer) {
    observers.remove(observer);
  }

     // Notify all observers about a state change
  private void notifyStateChange(ElevatorState state) {
      for(ElevatorObserver observer : observers){
            observer.onElevatorStateChange(this, state);
      }
  }
    // Notify all observers about a floor change
  private void notifyFloorChange(int floor) {
      for(ElevatorObserver observer : observers){
            observer.onElevatorFloorChange(this, floor);
      }
  }
      // Set a new state for the elevator and notify observers
  public void setState(ElevatorState newState) {
      this.state = newState;
      notifyStateChange(newState);
  }
    // Set the direction of the elevator
  public void setDirection(Direction newDirection) {
      this.direction = newDirection;
  }
    // Add a new floor request to the queue
  public void addRequest(ElevatorRequest elevatorRequest) {
      // Avoid duplicate requests
    if (!requests.contains(elevatorRequest)) {
      requests.add(elevatorRequest);
    }
      int requestedFloor = elevatorRequest.getFloor();
    // If elevator is idle, determine direction and start moving
    if (state == ElevatorState.IDLE && !requests.isEmpty()) {
      if (requestedFloor > currentFloor) {
        direction = Direction.UP;
      } else if (requestedFloor < currentFloor) {
        direction = Direction.DOWN;
      }
      setState(ElevatorState.MOVING);
  }
  }
  // Move the elevator to the next stop as decided by the scheduling strategy
  public void moveToNextStop(int nextStop) {
    // Only move if the elevator is currently in the MOVING state
    if (state != ElevatorState.MOVING)
      return;
    while (currentFloor != nextStop) {
      // Update floor based on direction
      if (direction == Direction.UP) {
        currentFloor++;
      } else {
        currentFloor--;
      }
      // Notify observers about the floor change
      notifyFloorChange(currentFloor);
      // Complete arrival once the target floor is reached
      if (currentFloor == nextStop) {
        completeArrival();
        return;
      }
    }
  }
    private void completeArrival() {
        // Stop the elevator and notify observers
        setState(ElevatorState.STOPPED);
        // Remove the current floor from the requests queue
    requests.removeIf((request) -> request.getFloor() == currentFloor);
        // If no more requests, set state to IDLE
        if(requests.isEmpty()){
            
            direction = Direction.IDLE;
            setState(ElevatorState.IDLE);
        }else {
          // Otherwise, continue moving after a brief stop
          setState(ElevatorState.MOVING);
        }
        
    }

    // Get the elevator's ID
  public int getId() {
    return id;
  }
  // Get the elevator's current floor
  public int getFloor() {
    return currentFloor;
  }
  // Get the elevator's current direction
  public Direction getDirection() {
    return direction;
  }
  // Get the elevator's current state
  public ElevatorState getState() {
    return state;
  }
  // Get a copy of the current requests queue to prevent external modification
  public Queue<ElevatorRequest> getRequestsQueue() {
    return new LinkedList<>(requests);
  }
  // Get a list of all destination floors for display purposes
  public List<ElevatorRequest> getDestinationFloors() {
    return new ArrayList<>(requests);
  }
}


class Floor {
    private int floorNumber;
    
    public Floor(int floorNumber) {
        this.floorNumber = floorNumber;
    }
    
    public int getFloorNumber() {
        return floorNumber;
    }
}

    // Observer interface for handling elevator events
interface ElevatorObserver{
    // Called when an elevator's state changes
    void onElevatorStateChange(Elevator elevator,ElevatorState state );
    // Called when an elevator's Floor changes
    void onElevatorFloorChange(Elevator elevator,int floor);
}

class ElevatorDisplay implements ElevatorObserver{
    @Override
	    public void onElevatorStateChange(Elevator elevator, ElevatorState state) {
	        // Display the new state of the elevator
	        System.out.println("Elevator " + elevator.getId() + " state changed to " + state);
	    }

	    @Override
	    public void onElevatorFloorChange(Elevator elevator, int floor) {
	        // Display the elevator's movement to a new floor
	        System.out.println("Elevator " + elevator.getId() + " moved to floor " + floor);
	    }
}

// Command Pattern for Request Processing
interface ElevatorCommand {
    // Method to execute the command
    public void execute();
}

class ElevatorRequest implements ElevatorCommand{
    private int elevatorId;
    private Direction requestDirection;
    private int floor;
     private ElevatorController controller; // Reference to the ElevatorController to handle the request
    private boolean isInternalRequest; // Distinguishes internal vs external requests
    
    // Constructor to initialize the elevator request
    public ElevatorRequest(int elevatorId, int floor, boolean isInternalRequest, Direction direction,ElevatorController controller) {
        this.elevatorId = elevatorId;
        this.floor = floor;
        this.isInternalRequest = isInternalRequest;
        this.requestDirection = direction;
           this.controller=controller;
    }

    // Execute method to process the request via the controller
    @Override
    public void execute(){
        if(isInternalRequest){
            controller.requestFloor(elevatorId,floor);
        }else{
            controller.requestElevator(elevatorId,floor,requestDirection);
        }
    }



    // Getters and Setters for the ElevatorRequest
    public Direction getDirection() {
        return requestDirection;
    }

    public int getFloor() {
        return floor;
    }

    public boolean checkIsInternalRequest(){
        return isInternalRequest;
    }
}

// Strategy Pattern for Scheduling
interface SchedulingStrategy {
    // Determines the next stop for the given elevator
    Integer getNextStop(Elevator elevator);
}

    // First-Come-First-Served Algorithm
class FCFSSchedulingStrategy implements SchedulingStrategy{
    
    @Override
    public Integer getNextStop(Elevator elevator){
        Direction elevatorDirection = elevator.getDirection();
        int currentFloor=elevator.getFloor();
        // Retrieve the FIFO queue of floor requests
        Queue<ElevatorRequest>requestQueue = elevator.getRequestsQueue();

        if(requestQueue.isEmpty()){
            return currentFloor;
        }
        int nextRequestedFloor = requestQueue.poll().getFloor();

        if(nextRequestedFloor==currentFloor){
            return currentFloor;
        }
        if(elevatorDirection==Direction.IDLE){
            elevator.setDirection(nextRequestedFloor>currentFloor?Direction.UP:Direction.DOWN);
        }
        else if(elevatorDirection == Direction.UP
        && nextRequestedFloor < currentFloor)
        {
            elevator.setDirection(Direction.DOWN);
        } 
        else if (nextRequestedFloor > currentFloor) {
            elevator.setDirection(Direction.UP);
        }

        // Return the next requested floor
        return nextRequestedFloor;
    }
}

    // Scan Scheduling Strategy for handling elevator requests
class ScanSchedulingStrategy implements SchedulingStrategy {
  @Override
  public Integer getNextStop(Elevator elevator) {
      
      Direction elevatorDirection = elevator.getDirection();
      int currentFloor = elevator.getFloor();
      Queue<ElevatorRequest> requests = elevator.getRequestsQueue();
      
      if(requests.isEmpty()){
          return currentFloor;
      }
      PriorityQueue<ElevatorRequest> upQueue = new PriorityQueue<>();// Min-heap for upward requests
      PriorityQueue<ElevatorRequest> downQueue = new PriorityQueue<>((a,b) -> b.getFloor() - a.getFloor());// Max-heap for downward requests

       // Categorize requests based on their relative position to the current floor
      while(!requests.isEmpty()){
          ElevatorRequest elevatorRequest = requests.poll();
          int floor = elevator.getFloor();
          if(floor > currentFloor){
              upQueue.add(elevatorRequest);
          }
          else{
              downQueue.add(elevatorRequest);
          }
      }

      // Handle the case when the elevator is IDLE
    if (elevatorDirection == Direction.IDLE) {
        // Determine the nearest request and set direction accordingly
        int nearestUpwardRequest = upQueue.isEmpty() ? -1 : upQueue.poll().getFloor();
        int nearestDownwardRequest =
          downQueue.isEmpty() ? -1 : downQueue.peek().getFloor();

        if (nearestUpwardRequest == -1) {
            elevator.setDirection(Direction.DOWN);
            return downQueue.poll().getFloor();
      } else if (nearestDownwardRequest == -1) {
            elevator.setDirection(Direction.UP);
            return upQueue.poll().getFloor();
      } 
        else{
            // Choose the closest request
            if (Math.abs(nearestUpwardRequest - currentFloor)
            < Math.abs(nearestDownwardRequest - currentFloor)) {
                  elevator.setDirection(Direction.UP);
                  return upQueue.poll().getFloor();
            }else{
                  elevator.setDirection(Direction.DOWN);
                  return downQueue.poll().getFloor();
            }
        }
    }

      // Handle movement in the UP direction
    if (elevatorDirection == Direction.UP) {
      return !upQueue.isEmpty() ? upQueue.poll().getFloor()
                                : switchDirection(elevator, downQueue);
    }
    // Handle movement in the DOWN direction
    else {
      return !downQueue.isEmpty() ? downQueue.poll().getFloor()
                                  : switchDirection(elevator, upQueue);
    }
  }
    // Helper method to switch the elevator's direction when no further requests
  // exist in the current direction
  private int switchDirection(
      Elevator elevator, PriorityQueue<ElevatorRequest> requestsQueue) {
    elevator.setDirection(elevator.getDirection() == Direction.UP
            ? Direction.DOWN
            : Direction.UP);
    return requestsQueue.isEmpty() ? elevator.getFloor()
                                   : requestsQueue.poll().getFloor();
  }
}

  // Look SchedulingStrategy for handling elevator requests
class LookSchedulingStrategy implements SchedulingStrategy {
  @Override
  public Integer getNextStop(Elevator elevator) {
      int currentFloor = elevator.getFloor();
        Queue<ElevatorRequest> requests = elevator.getRequestsQueue();
        // If there are no pending requests, remain on the current floor.
        if (requests == null || requests.isEmpty()) {
            return currentFloor;
        }
       // Determine the primary target from the first request in the queue.
        ElevatorRequest primaryRequest = requests.peek();
         int primaryFloor = primaryRequest.getFloor();
        // Determine the travel direction based on the primary target.
        Direction travelDirection;
        if (primaryFloor > currentFloor) {
            travelDirection = Direction.UP;
        } else if (primaryFloor < currentFloor) {
            travelDirection = Direction.DOWN;
        } else {
            return currentFloor; // Already at the requested floor.
        }

      // Look for any request along the journey from currentFloor to primaryFloor.
        // For upward movement, we need the smallest floor greater than currentFloor and <=
        // primaryFloor. For downward movement, we need the largest floor less than currentFloor and >=
        // primaryFloor.
        Integer candidate = null;


        for (ElevatorRequest req : requests) {
            int reqFloor = req.getFloor();
            // Check if the request is within the range between currentFloor and primaryFloor.
            if (travelDirection == Direction.UP && reqFloor > currentFloor && reqFloor <= primaryFloor) {
                 // For internal requests we always consider; for external requests, only if they are going
                // UP.
                if (req.checkIsInternalRequest()
                        || (!req.checkIsInternalRequest() && req.getDirection() == Direction.UP)) {
                            // Choose the candidate that is closest to the current floor (i.e. the smallest floor
                    // greater than currentFloor).
                        if (candidate == null || reqFloor < candidate) {
                            candidate = reqFloor;
                        }
                    }
            }
            else if (travelDirection == Direction.DOWN && reqFloor < currentFloor
                    && reqFloor >= primaryFloor) {
                // For downward movement, consider the request if internal or if external with direction
                // DOWN.
                if (req.checkIsInternalRequest()
                        || (!req.checkIsInternalRequest() && req.getDirection() == Direction.DOWN)) {
                    // For a downward journey, we choose the candidate that is closest to the current floor
                    // (i.e. the largest floor less than currentFloor).
                    if (candidate == null || reqFloor > candidate) {
                        candidate = reqFloor;
                    }
                }
            }
        }
      // If a candidate was found in the path, return that as the next stop;
        // otherwise, fall back to the primary target.
        return (candidate != null) ? candidate : primaryFloor;
  }
}

// Elevator Controller class to manage elevators and floor requests
class ElevatorController{
    // List of all elevators in the system
  private List<Elevator> elevators;
  // List of all floors in the building
  private List<Floor> floors;
  // Strategy to determine the scheduling of elevators
  private SchedulingStrategy schedulingStrategy;
  // ID of the current elevator (used for internal operations)
  private int currentElevatorId;
     // Constructor to initialize elevators and floors
  public ElevatorController(int numberOfElevators, int numberOfFloors) {
        this.elevators = new ArrayList<>();
        this.floors = new ArrayList<>();
        this.schedulingStrategy = new ScanSchedulingStrategy(); // Default strategy
        // Initialize elevators with unique IDs
        for (int i = 1; i <= numberOfElevators; i++) {
          elevators.add(new Elevator(i));
        }
        // Initialize floors
        for (int i = 1; i <= numberOfFloors; i++) {
          floors.add(new Floor(i));
        }
  }

    // Set the scheduling strategy dynamically
      public void setSchedulingStrategy(SchedulingStrategy strategy) {
        this.schedulingStrategy = strategy;
      }

     // Handle external elevator requests from a specific floor
      public void requestElevator(int elevatorId, int floorNumber, Direction direction) {
          System.out.println(
        "External request: Floor " + floorNumber + ", Direction " + direction);
          // Find the elevator by its ID
          Elevator selectedElevator = getElevatorById(elevatorId);
          if (selectedElevator != null) {
              selectedElevator.addRequest(new ElevatorRequest(elevatorId, floorNumber, false, direction,this));
              System.out.println("Assigned elevator " + selectedElevator.getId()
          + " to floor " + floorNumber);
          }else{
              // If no suitable elevator is found
          System.out.println("No elevator available for floor " + floorNumber);
          }
      
      }
     // Handle internal elevator requests to a specific floor
      public void requestFloor(int elevatorId, int floorNumber) {
          // Find the elevator by its ID
        Elevator elevator = getElevatorById(elevatorId);
        System.out.println("Internal request: Elevator " + elevator.getId()
            + " to floor " + floorNumber);
          // Determine the direction of the request
        Direction direction = floorNumber > elevator.getFloor()? Direction.UP : Direction.DOWN;
          // Add the request to the elevator
        elevator.addRequest(
        new ElevatorRequest(elevatorId, floorNumber, true, direction,this));
      }
    
    // Perform a simulation step by moving all elevators
      public void step() {
          for(Elevator elevator:elevators){
              // Only process elevators with pending requests
              if (!elevator.getRequestsQueue().isEmpty()) {
                // Use the scheduling strategy to find the next stop
                  int nextStop = schedulingStrategy.getNextStop(elevator);
                  // Move the elevator to the next stop if needed
                  if(elevator.getFloor()!=nextStop){
                      elevator.moveToNextStop(nextStop);
                  }
              }
          }
      }
    // Find an elevator by its ID
  private Elevator getElevatorById(int elevatorId) {
    for (Elevator elevator : elevators) {
      if (elevator.getId() == elevatorId)
        return elevator;
    }
    return null; // Return null if no matching elevator is found
  }

    // Get the list of all elevators
  public List<Elevator> getElevators() {
    return elevators;
  }

  // Get the list of all floors
  public List<Floor> getFloors() {
    return floors;
  }

  // Set the ID of the current elevator
  public void setCurrentElevator(int elevatorId) {
    this.currentElevatorId = elevatorId;
  }
}


class Main {
    public static void main(String[] args) {
         // Initialize a building with 10 floors and 3 elevators
        Building building = new Building("Office Tower", 10, 3);
        ElevatorController controller = building.getElevatorController();
        // Create an ElevatorDisplay to observe and display elevator events
        ElevatorDisplay display = new ElevatorDisplay();
        for (Elevator elevator : controller.getElevators()) {
            elevator.addObserver(display); // Add the display as an observer for all elevators
        }
        // Simulate elevator requests using a command-line interface
        Scanner scanner = new Scanner(System.in);
        boolean running = true;
        // Display simulation details and options
        System.out.println("Elevator System Simulation");
        System.out.println("Building: " + building.getName());
        System.out.println("Floors: " + building.getNumberOfFloors());
        System.out.println("Elevators: " + controller.getElevators().size());
        // Main loop for user interactions
        while (running) {
            System.out.println("nSelect an option:");
            System.out.println("1. Request elevator (external)");
            System.out.println("2. Request floor (internal)");
            System.out.println("3. Simulate next step");
            System.out.println("4. Change scheduling strategy");
            System.out.println("5. Exit simulation");
            int choice = scanner.nextInt(); // Read user's menu choice
            switch (choice) {
                case 1:
                    // Handle external elevator request
                    // Handle internal elevator floor request
                    System.out.print("Enter elevator ID: ");
                    int externalElevatorId = scanner.nextInt();
                    controller.setCurrentElevator(externalElevatorId); // Set the selected elevator
                    System.out.print("Enter floor number: ");
                    int floorNum = scanner.nextInt();
                    System.out.print("Direction (1 for UP, 2 for DOWN): ");
                    int dirChoice = scanner.nextInt();
                    Direction dir = dirChoice == 1 ? Direction.UP : Direction.DOWN;
                    controller.requestElevator(externalElevatorId, floorNum, dir);
                    break;
                case 2:
                    // Handle internal elevator floor request
                    System.out.print("Enter elevator ID: ");
                    int elevatorId = scanner.nextInt();
                    controller.setCurrentElevator(elevatorId); // Set the selected elevator
                    System.out.print("Enter destination floor: ");
                    int destFloor = scanner.nextInt();
                    controller.requestFloor(elevatorId, destFloor);
                    break;
                case 3:
                    // Simulate the next step in the system
                    System.out.println("Simulating next step...");
                    controller.step(); // Perform the simulation step
                    displayElevatorStatus(
                            controller.getElevators()); // Display elevator statuses
                    break;
                case 4:
                    // Change the scheduling strategy
                    System.out.println("Select strategy:");
                    System.out.println("1. SCAN Algorithm");
                    System.out.println("2. FCFS Algorithm");
                    System.out.println("3. Look Algorithm");
                    int strategyChoice = scanner.nextInt();
                    if (strategyChoice == 1) {
                        controller.setSchedulingStrategy(new ScanSchedulingStrategy());
                        System.out.println("Strategy set to SCAN Algorithm");
                    } else {
                        controller.setSchedulingStrategy(new FCFSSchedulingStrategy());
                        System.out.println("Strategy set to Nearest Elevator First");
                    }
                    break;
                case 5:
                    // Exit the simulation
                    running = false;
                    break;
                default:
                    // Handle invalid choices
                    System.out.println("Invalid choice!");
            }
        }
        scanner.close(); // Close the scanner to release resources
        System.out.println("Simulation ended"); // End of simulation
    }


    
     // Display the status of all elevators in the system
    private static void displayElevatorStatus(List<Elevator> elevators) {
        System.out.println("nElevator Status:");
        for (Elevator elevator : elevators) {
            // Print details of each elevator, including current floor, direction, and
            // state
            System.out.println("Elevator " + elevator.getId() + ": Floor "
                    + elevator.getFloor() + ", Direction "
                    + elevator.getDirection() + ", State " + elevator.getState()
                    + ", Destinations " + elevator.getDestinationFloors());
        }
    }

    
}
