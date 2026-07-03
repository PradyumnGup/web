import java.util.*;

// ─────────────────────────────────────────────
// ENUMS
// ─────────────────────────────────────────────

enum DurationType {
    HOURS,
    DAYS
}

// ─────────────────────────────────────────────
// FEE STRATEGIES
// ─────────────────────────────────────────────

interface ParkingFeeStrategy {
    double calculateFee(String vehicleType, int duration, DurationType durationType);
}

class BasicHourlyRateStrategy implements ParkingFeeStrategy {
    @Override
    public double calculateFee(String vehicleType, int duration, DurationType durationType) {
        switch (vehicleType.toLowerCase()) {
            case "car":   return durationType == DurationType.HOURS ? duration * 10.0 : duration * 10.0 * 24;
            case "bike":  return durationType == DurationType.HOURS ? duration * 5.0  : duration * 5.0  * 24;
            case "auto":  return durationType == DurationType.HOURS ? duration * 8.0  : duration * 8.0  * 24;
            default:      return durationType == DurationType.HOURS ? duration * 15.0 : duration * 15.0 * 24;
        }
    }
}

class PremiumRateStrategy implements ParkingFeeStrategy {
    @Override
    public double calculateFee(String vehicleType, int duration, DurationType durationType) {
        switch (vehicleType.toLowerCase()) {
            case "car":   return durationType == DurationType.HOURS ? duration * 15.0 : duration * 15.0 * 24;
            case "bike":  return durationType == DurationType.HOURS ? duration * 8.0  : duration * 8.0  * 24;
            case "auto":  return durationType == DurationType.HOURS ? duration * 12.0 : duration * 12.0 * 24;
            default:      return durationType == DurationType.HOURS ? duration * 20.0 : duration * 20.0 * 24;
        }
    }
}

// ─────────────────────────────────────────────
// VEHICLE HIERARCHY
// ─────────────────────────────────────────────

abstract class Vehicle {
    private String licensePlate;
    private String vehicleType;
    private ParkingFeeStrategy parkingFeeStrategy;

    public Vehicle(String licensePlate, String vehicleType, ParkingFeeStrategy feeStrategy) {
        this.licensePlate       = licensePlate;
        this.vehicleType        = vehicleType;
        this.parkingFeeStrategy = feeStrategy;
    }

    public String getVehicleType()  { return vehicleType;  }
    public String getLicensePlate() { return licensePlate; }

    public double calculateFee(int duration, DurationType durationType) {
        return parkingFeeStrategy.calculateFee(vehicleType, duration, durationType);
    }
}

class CarVehicle extends Vehicle {
    public CarVehicle(String licensePlate, ParkingFeeStrategy feeStrategy) {
        super(licensePlate, "Car", feeStrategy);
    }
}

class BikeVehicle extends Vehicle {
    public BikeVehicle(String licensePlate, ParkingFeeStrategy feeStrategy) {
        super(licensePlate, "Bike", feeStrategy);
    }
}

class AutoVehicle extends Vehicle {
    public AutoVehicle(String licensePlate, ParkingFeeStrategy feeStrategy) {
        super(licensePlate, "Auto", feeStrategy);
    }
}

class OtherVehicle extends Vehicle {
    public OtherVehicle(String licensePlate, ParkingFeeStrategy feeStrategy) {
        super(licensePlate, "Other", feeStrategy);
    }
}

// ─────────────────────────────────────────────
// VEHICLE FACTORY
// ─────────────────────────────────────────────

class VehicleFactory {
    public static Vehicle createVehicle(String vehicleType, String licensePlate, ParkingFeeStrategy feeStrategy) {
        switch (vehicleType.toLowerCase()) {
            case "car":   return new CarVehicle(licensePlate, feeStrategy);
            case "bike":  return new BikeVehicle(licensePlate, feeStrategy);
            case "auto":  return new AutoVehicle(licensePlate, feeStrategy);
            default:      return new OtherVehicle(licensePlate, feeStrategy);
        }
    }
}

// ─────────────────────────────────────────────
// PAYMENT STRATEGIES
// ─────────────────────────────────────────────

interface PaymentStrategy {
    void processPayment(double amount);
}

class CreditCardPayment implements PaymentStrategy {
    @Override
    public void processPayment(double amount) {
        System.out.println("Processing credit card payment of $" + amount);
    }
}

class CashPayment implements PaymentStrategy {
    @Override
    public void processPayment(double amount) {
        System.out.println("Processing cash payment of $" + amount);
    }
}

class PaymentProcessor {
    private PaymentStrategy paymentStrategy;

    public PaymentProcessor(PaymentStrategy paymentStrategy) {
        this.paymentStrategy = paymentStrategy;
    }

    public void processPayment(double amount) {
        if (amount > 0) {
            paymentStrategy.processPayment(amount);
        } else {
            System.out.println("Invalid payment amount.");
        }
    }

    public void setPaymentStrategy(PaymentStrategy paymentStrategy) {
        this.paymentStrategy = paymentStrategy;
    }
}

// ─────────────────────────────────────────────
// PARKING SPOTS
// ─────────────────────────────────────────────

abstract class ParkingSpot {
    private int     spotNumber;
    private String  spotType;
    private Vehicle vehicle;
    private boolean isOccupied;

    public ParkingSpot(int spotNumber, String spotType) {
        this.spotNumber = spotNumber;
        this.spotType   = spotType;
        this.isOccupied = false;
    }

    public abstract boolean canParkVehicle(Vehicle vehicle);

    public void parkVehicle(Vehicle vehicle) {
        if (isOccupied)
            throw new IllegalStateException("Spot " + spotNumber + " is already occupied.");
        if (!canParkVehicle(vehicle))
            throw new IllegalArgumentException("Spot not suitable for " + vehicle.getVehicleType());
        this.vehicle    = vehicle;
        this.isOccupied = true;
    }

    public void vacate() {
        if (!isOccupied)
            throw new IllegalStateException("Spot " + spotNumber + " is already vacant.");
        this.isOccupied = false;
        this.vehicle    = null;
    }

    public int     getSpotNumber() { return spotNumber; }
    public String  getSpotType()   { return spotType;   }
    public Vehicle getVehicle()    { return vehicle;    }
    public boolean isOccupied()    { return isOccupied; }
}

class CarParkingSpot extends ParkingSpot {
    public CarParkingSpot(int spotNumber) {
        super(spotNumber, "Car");
    }
    @Override
    public boolean canParkVehicle(Vehicle vehicle) {
        return "car".equalsIgnoreCase(vehicle.getVehicleType());
    }
}

class BikeParkingSpot extends ParkingSpot {
    public BikeParkingSpot(int spotNumber) {
        super(spotNumber, "Bike");
    }
    @Override
    public boolean canParkVehicle(Vehicle vehicle) {
        return "bike".equalsIgnoreCase(vehicle.getVehicleType());
    }
}

class OtherParkingSpot extends ParkingSpot {
    public OtherParkingSpot(int spotNumber) {
        super(spotNumber, "Other");
    }
    @Override
    public boolean canParkVehicle(Vehicle vehicle) {
        return "other".equalsIgnoreCase(vehicle.getVehicleType());
    }
}

// ─────────────────────────────────────────────
// PARKING FLOOR
// ─────────────────────────────────────────────

class ParkingFloor {
    private List<ParkingSpot> spots;
    private int floorNumber;

    public ParkingFloor(int floorNumber) {
        this.floorNumber = floorNumber;
        this.spots       = new ArrayList<>();
    }

    public void addParkingSpot(ParkingSpot spot) {
        this.spots.add(spot);
    }

    public ParkingSpot findAvailableSpot(String vehicleType) {
        for (ParkingSpot spot : spots) {
            if (!spot.isOccupied() && spot.getSpotType().equalsIgnoreCase(vehicleType)) {
                return spot;
            }
        }
        return null;
    }

    public List<ParkingSpot> getParkingSpots() { return spots;       }
    public int               getFloorNumber()  { return floorNumber; }
}

// ─────────────────────────────────────────────
// PARKING LOT (multi-floor)
// ─────────────────────────────────────────────

class ParkingLot {
    private List<ParkingFloor> floors;

    public ParkingLot(List<ParkingFloor> floors) {
        this.floors = floors;
    }

    public ParkingSpot findAvailableSpot(String vehicleType) {
        for (ParkingFloor floor : floors) {
            ParkingSpot spot = floor.findAvailableSpot(vehicleType);
            if (spot != null) return spot;
        }
        return null;
    }

    public ParkingSpot parkVehicle(Vehicle vehicle) {
        ParkingSpot spot = findAvailableSpot(vehicle.getVehicleType());
        if (spot != null) {
            spot.parkVehicle(vehicle);
            System.out.println(vehicle.getVehicleType() + " [" + vehicle.getLicensePlate()
                    + "] parked in spot " + spot.getSpotNumber());
            return spot;
        }
        System.out.println("No parking spots available for " + vehicle.getVehicleType() + "!");
        return null;
    }

    public void vacateSpot(ParkingSpot spot, Vehicle vehicle) {
        if (spot != null && spot.isOccupied() && spot.getVehicle().equals(vehicle)) {
            spot.vacate();
            System.out.println(vehicle.getVehicleType() + " [" + vehicle.getLicensePlate()
                    + "] vacated spot " + spot.getSpotNumber());
        } else {
            System.out.println("Invalid operation: spot already vacant or vehicle mismatch.");
        }
    }

    public ParkingSpot getSpotByNumber(int spotNumber) {
        for (ParkingFloor floor : floors) {
            for (ParkingSpot spot : floor.getParkingSpots()) {
                if (spot.getSpotNumber() == spotNumber) return spot;
            }
        }
        return null;
    }

    public List<ParkingFloor> getFloors() { return floors; }
}

// ─────────────────────────────────────────────
// PARKING LOT BUILDER
// ─────────────────────────────────────────────

class ParkingLotBuilder {
    private List<ParkingFloor> floors;

    public ParkingLotBuilder() {
        this.floors = new ArrayList<>();
    }

    public ParkingLotBuilder addFloor(ParkingFloor floor) {
        floors.add(floor);
        return this;
    }

    /**
     * Creates a floor with car spots, bike spots, and optionally other spots.
     * otherSpotCounts is variadic — each value = count of "Other" spots to add.
     */
    public ParkingLotBuilder createFloor(int floorNumber, int numOfCarSpots,
                                          int numOfBikeSpots, int... otherSpotCounts) {
        ParkingFloor floor = new ParkingFloor(floorNumber);

        for (int i = 0; i < numOfCarSpots; i++) {
            floor.addParkingSpot(new CarParkingSpot(i + 1));
        }

        for (int i = 0; i < numOfBikeSpots; i++) {
            floor.addParkingSpot(new BikeParkingSpot(numOfCarSpots + i + 1));
        }

        int spotOffset = numOfCarSpots + numOfBikeSpots;
        for (int count : otherSpotCounts) {
            for (int j = 0; j < count; j++) {
                floor.addParkingSpot(new OtherParkingSpot(spotOffset + j + 1));
            }
            spotOffset += count;
        }

        floors.add(floor);
        return this;
    }

    public ParkingLot build() {
        return new ParkingLot(floors);
    }
}

// ─────────────────────────────────────────────
// MAIN
// ─────────────────────────────────────────────

public class Main {
    public static void main(String[] args) {

        // Build a multi-floor parking lot
        ParkingLot parkingLot = new ParkingLotBuilder()
                .createFloor(1, 2, 2)       // Floor 1: 2 car spots, 2 bike spots
                .createFloor(2, 3, 1, 1)    // Floor 2: 3 car spots, 1 bike spot, 1 other spot
                .build();

        displayLot(parkingLot);

        // Fee strategies
        ParkingFeeStrategy basic   = new BasicHourlyRateStrategy();
        ParkingFeeStrategy premium = new PremiumRateStrategy();

        // Create vehicles via factory
        Vehicle car1  = VehicleFactory.createVehicle("Car",  "CAR-001", basic);
        Vehicle car2  = VehicleFactory.createVehicle("Car",  "CAR-002", basic);
        Vehicle car3  = VehicleFactory.createVehicle("Car",  "CAR-003", premium);
        Vehicle bike1 = VehicleFactory.createVehicle("Bike", "BIKE-001", premium);
        Vehicle bike2 = VehicleFactory.createVehicle("Bike", "BIKE-002", basic);
        Vehicle other = VehicleFactory.createVehicle("Other","OTH-001",  basic);

        System.out.println("\n--- Parking Vehicles ---");
        ParkingSpot spot1 = parkingLot.parkVehicle(car1);
        ParkingSpot spot2 = parkingLot.parkVehicle(car2);
        ParkingSpot spot3 = parkingLot.parkVehicle(car3);   // goes to floor 2
        ParkingSpot spot4 = parkingLot.parkVehicle(bike1);
        ParkingSpot spot5 = parkingLot.parkVehicle(bike2);
        ParkingSpot spot6 = parkingLot.parkVehicle(other);

        displayLot(parkingLot);

        // Payment
        System.out.println("\n--- Processing Payments ---");
        Scanner scanner = new Scanner(System.in);
        System.out.println("Select payment method:  1. Credit Card   2. Cash");
        int choice = scanner.nextInt();
        PaymentProcessor processor = new PaymentProcessor(getPaymentStrategy(choice));

        if (spot1 != null) {
            double fee = car1.calculateFee(2, DurationType.HOURS);
            System.out.print("Car1 fee ($" + fee + "): ");
            processor.processPayment(fee);
            parkingLot.vacateSpot(spot1, car1);
        }

        if (spot4 != null) {
            double fee = bike1.calculateFee(3, DurationType.HOURS);
            System.out.print("Bike1 fee ($" + fee + "): ");
            processor.setPaymentStrategy(getPaymentStrategy(choice));
            processor.processPayment(fee);
            parkingLot.vacateSpot(spot4, bike1);
        }

        if (spot6 != null) {
            double fee = other.calculateFee(1, DurationType.DAYS);
            System.out.print("Other fee ($" + fee + "): ");
            processor.processPayment(fee);
            parkingLot.vacateSpot(spot6, other);
        }

        System.out.println("\n--- Final Lot Status ---");
        displayLot(parkingLot);
        scanner.close();
    }

    private static PaymentStrategy getPaymentStrategy(int choice) {
        switch (choice) {
            case 2:  return new CashPayment();
            default: return new CreditCardPayment();
        }
    }

    private static void displayLot(ParkingLot parkingLot) {
        System.out.println("\n=== Parking Lot Status ===");
        for (ParkingFloor floor : parkingLot.getFloors()) {
            System.out.println("Floor " + floor.getFloorNumber() + ":");
            for (ParkingSpot spot : floor.getParkingSpots()) {
                String status = spot.isOccupied()
                        ? "OCCUPIED by " + spot.getVehicle().getLicensePlate()
                        : "FREE";
                System.out.println("  Spot " + spot.getSpotNumber()
                        + " [" + spot.getSpotType() + "] -> " + status);
            }
        }
        System.out.println("==========================");
    }
}
