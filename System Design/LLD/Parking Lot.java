import java.util.*;

enum DurationType{
    HOURS,
    DAYS
}

interface ParkingFeeStrategy {
    double calculateFee(String vehicleType, int duration, DurationType durationType);
}
 
class BasicHourlyRateStrategy implements ParkingFeeStrategy {
    @Override
    public double calculateFee(String vehicleType, int duration, DurationType durationType) {
        switch (vehicleType.toLowerCase()) {
            case "car":
                return durationType == DurationType.HOURS ? duration * 10.0 : duration * 10.0 * 24;
            case "bike":
                return durationType == DurationType.HOURS ? duration * 5.0  : duration * 5.0  * 24;
            case "auto":
                return durationType == DurationType.HOURS ? duration * 8.0  : duration * 8.0  * 24;
            default:
                return durationType == DurationType.HOURS ? duration * 15.0 : duration * 15.0 * 24;
        }
    }
}
 
class PremiumRateStrategy implements ParkingFeeStrategy {
    @Override
    public double calculateFee(String vehicleType, int duration, DurationType durationType) {
        switch (vehicleType.toLowerCase()) {
            case "car":
                return durationType == DurationType.HOURS ? duration * 15.0 : duration * 15.0 * 24;
            case "bike":
                return durationType == DurationType.HOURS ? duration * 8.0  : duration * 8.0  * 24;
            case "auto":
                return durationType == DurationType.HOURS ? duration * 12.0 : duration * 12.0 * 24;
            default:
                return durationType == DurationType.HOURS ? duration * 20.0 : duration * 20.0 * 24;
        }
    }
}

abstract class Vehicle{
    private String licensePlate; // Stores the vehicle's license plate number
    private String vehicleType; // Stores the type of vehicle (e.g., car, bike, truck)
    private ParkingFeeStrategy parkingFeeStrategy;// Strategy for calculating parking fees
    // Constructor to initialize a vehicle with its license plate, type, and fee strategy
    public Vehicle(String licensePlate, String vehicleType, ParkingFeeStrategy feeStrategy) {  
            this.licensePlate = licensePlate;  
            this.vehicleType = vehicleType;  
            this.parkingFeeStrategy = feeStrategy;  
        }   
    // Getter method to retrieve the vehicle type
    public String getVehicleType() {  
        return vehicleType;  
    }  
    // Getter method to retrieve the vehicle's license plate number
    public String getLicensePlate() {  
        return licensePlate;  
    }  
    
    public double calculateFee(int duration,DurationType durationType){
        return parkingFeeStrategy.calculateFee(vehicleType,duration,durationType);
    }
}

class CarVehicle extends Vehicle {
    public CarVehicle(String licensePlate, String vehicleType, ParkingFeeStrategy feeStrategy) {
        super(licensePlate, vehicleType, feeStrategy);
    }
}
 
class BikeVehicle extends Vehicle {
    public BikeVehicle(String licensePlate, String vehicleType, ParkingFeeStrategy feeStrategy) {
        super(licensePlate, vehicleType, feeStrategy);
    }
}



class VehicleFactory{
    public static Vehicle createVehicle(String vehicleType, String licensePlate, ParkingFeeStrategy feeStrategy){
        if (vehicleType.equalsIgnoreCase("Car")) {
            return new CarVehicle(licensePlate, vehicleType, feeStrategy);
        } else if(vehicleType.equalsIgnoreCase("Bike")) {
            return new BikeVehicle(licensePlate, vehicleType, feeStrategy);
        }
        else {
            throw new IllegalArgumentException("Unknown vehicle type: " + vehicleType);
        }
    }
}

interface PaymentStrategy{
    void processPayment(double amount);
}

class CreditCardPayment implements PaymentStrategy{
    public CreditCardPayment(double fee) {
    }
    
    @Override
    public void processPayment(double amount){
        System.out.println("Processing credit card payment of $" + amount);
    }
}

class CashPayment implements PaymentStrategy{
    public CashPayment(double fee) {
    }
    
    @Override
    public void processPayment(double amount){
        System.out.println("Processing credit card payment of $" + amount);
    }
}

class PaymentProcessor{
    private double amount;
    private PaymentStrategy paymentStrategy;
    
    public PaymentProcessor(double amount, PaymentStrategy paymentStrategy) {
        this.amount = amount;
        this.paymentStrategy = paymentStrategy;
    }
    
    
    public void processPayment(double amount){
        if (amount > 0) {
            paymentStrategy.processPayment(amount);  // Delegating to strategy
        } else {
            System.out.println("Invalid payment amount.");
        }
    }
    
    public void setPaymentStrategy(PaymentStrategy paymentStrategy) {
        this.paymentStrategy = paymentStrategy;
    }
}

abstract class ParkingSpot{
    private int spotNumber;
    private String spotType;
    private Vehicle vehicle;
    private boolean isOccupied;
    
    // Constructor to initialize parking spot with spot number and type
      public ParkingSpot(int spotNumber, String spotType) {
        this.spotNumber = spotNumber;
        this.isOccupied = false;
        this.spotType = spotType;
      }
  
    public abstract boolean canParkVehicle(Vehicle vehicle);
    
    public void parkVehicle(Vehicle vehicle){
        if (isOccupied) {
          throw new IllegalStateException("Spot is already occupied.");
        }
        if(!canParkVehicle(vehicle)){
            throw new IllegalArgumentException("This spot is not suitable for" + vehicle.getVehicleType());
        }
        this.vehicle=vehicle;
        this.isOccupied=true;
    }
    
    public void vacate(){
        if (!isOccupied) {
          throw new IllegalStateException("Spot is already vacant.");
        }
        
        this.isOccupied=false;
        this.vehicle=null;
    }
    
    // Getter for spot number
  public int getSpotNumber() {
    return spotNumber;
  }
  // Getter for the vehicle parked in the spot
  public Vehicle getVehicle() {
    return vehicle;
  }
  // Getter for spot type
  public String getSpotType() {
    return spotType;
  }
   // Method to check if the spot is occupied
  public boolean isOccupied() {
    return isOccupied;
  }
    
}

class CarParkingSpot extends ParkingSpot{
    public CarParkingSpot(int spotNumber) {
        super(spotNumber, "Car");
    }
    
    @Override
    public boolean canParkVehicle(Vehicle vehicle){
        return "CAR".equalsIgnoreCase(vehicle.getVehicleType());
    }
}

class BikeParkingSpot extends ParkingSpot {
    public BikeParkingSpot(int spotNumber) {
        super(spotNumber, "Bike");
    }
    @Override
    public boolean canParkVehicle(Vehicle vehicle) {
        return "Bike".equalsIgnoreCase(vehicle.getVehicleType());
    }
}

class ParkingLot{
    private List<ParkingSpot>parkingSpots;
    // Constructor to initialize the parking lot with parking spots
      public ParkingLot(List<ParkingSpot> parkingSpots) {
        this.parkingSpots = parkingSpots;
      }
      
       // Method to find an available spot based on vehicle type
      public ParkingSpot findAvailableSpot(String vehicleType) {
        for (ParkingSpot spot : parkingSpots) {
          if (!spot.isOccupied() && spot.getSpotType().equals(vehicleType)) {
            return spot; // Found an available spot for the vehicle type
          }
        }
        return null; // No available spot found for the given vehicle type
      }
      
      public ParkingSpot parkVehicle(Vehicle vehicle){
          ParkingSpot spot=findAvailableSpot(vehicle.getVehicleType());
          if(spot!=null){
              spot.parkVehicle(vehicle); // Mark the spot as occupied
              System.out.println(
                  "Vehicle parked successfully in spot: " + spot.getSpotNumber());
              return spot;
          }
          System.out.println(
            "No parking spots available for " + vehicle.getVehicleType() + "!");
          return null;
      }
      
       // Method to vacate a parking spot
      public void vacateSpot(ParkingSpot spot, Vehicle vehicle) {
        if (spot != null && spot.isOccupied()
            && spot.getVehicle().equals(vehicle)) {
          spot.vacate(); // Free the spot
          System.out.println(vehicle.getVehicleType()
              + " vacated the spot: " + spot.getSpotNumber());
        } else {
          System.out.println("Invalid operation! Either the spot is already vacant "
                             + "or the vehicle does not match.");
        }
      }
      
      // Method to find a spot by its number
  public ParkingSpot getSpotByNumber(int spotNumber) {
    for (ParkingSpot spot : parkingSpots) {
      if (spot.getSpotNumber() == spotNumber) {
        return spot;
      }
    }
    return null; // Spot not found
  }
  // Getter for parking spots
  public List<ParkingSpot> getParkingSpots() {
    return parkingSpots;
  }
  
}

public class Main {
  public static void main(String[] args) {
    // Initialize parking spots
    List<ParkingSpot> parkingSpots = new ArrayList<>();
    parkingSpots.add(new CarParkingSpot(1));
    parkingSpots.add(new CarParkingSpot(2));
    parkingSpots.add(new BikeParkingSpot(3));
    parkingSpots.add(new BikeParkingSpot(4));
    // Initialize parking lot
    ParkingLot parkingLot = new ParkingLot(parkingSpots);
    // Create fee strategies
    ParkingFeeStrategy basicHourlyRateStrategy = new BasicHourlyRateStrategy();
    ParkingFeeStrategy premiumRateStrategy = new PremiumRateStrategy();
    // Create vehicles using Factory Pattern with fee strategies
     Vehicle car1 = VehicleFactory.createVehicle("Car", "CAR123", basicHourlyRateStrategy);
        Vehicle car2 = VehicleFactory.createVehicle("Car", "CAR345", basicHourlyRateStrategy);

        Vehicle bike1 = VehicleFactory.createVehicle("Bike", "BIKE456", premiumRateStrategy);
        Vehicle bike2 = VehicleFactory.createVehicle("Bike", "BIKE123", premiumRateStrategy);

    // Park vehicles
    ParkingSpot carSpot = parkingLot.parkVehicle(car1);
    ParkingSpot bikeSpot = parkingLot.parkVehicle(bike1);
    
    Scanner scanner = new Scanner(System.in);
    System.out.println("Select payment method for your vehicle:");
    System.out.println("1. Credit Card");
    System.out.println("2. Cash");
    int paymentMethod = scanner.nextInt();
    PaymentProcessor paymentProcessor=null;
    // Process payments using Strategy Patterns
    if (carSpot != null) {
      // Calculate fee using the specific strategy for the vehicle
      double carFee = car1.calculateFee(2, DurationType.HOURS);
      paymentProcessor= new PaymentProcessor(carFee,getPaymentStrategy(paymentMethod, carFee));
      
      paymentProcessor.processPayment(carFee);
      parkingLot.vacateSpot(carSpot, car1);
    }
    if (bikeSpot != null) {
      // Calculate fee using the specific strategy for the vehicle
      double bikeFee = bike1.calculateFee(3, DurationType.HOURS);
      paymentProcessor.setPaymentStrategy(getPaymentStrategy(paymentMethod, bikeFee));
      paymentProcessor.processPayment(bikeFee);
      parkingLot.vacateSpot(bikeSpot, bike1);
    }
    scanner.close();
  }
  private static PaymentStrategy getPaymentStrategy(
      int paymentMethod, double fee) {
    switch (paymentMethod) {
      case 1:
        return new CreditCardPayment(fee);
      case 2:
        return new CashPayment(fee);
      default:
        System.out.println("Invalid choice! Default to Credit card payment.");
        return new CreditCardPayment(fee);
    }
  }
}
