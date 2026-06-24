// Enum for product categories
public enum ProductCategory {
	  ELECTRONICS, CLOTHING, GROCERY, FURNITURE, OTHER
}

// Abstract Product class
public abstract class Product {
  private String sku;
  private String name;
  private double price;
  private int quantity;
  private int threshold;
  private ProductCategory category;

  // Getters and setters
  public String getSku() {
    return sku;
  }

  public void setSku(String sku) {
    this.sku = sku;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public double getPrice() {
    return price;
  }

  public void setPrice(double price) {
    this.price = price;
  }

  public int getQuantity() {
    return quantity;
  }

  public void setQuantity(int quantity) {
    this.quantity = quantity;
  }

  public ProductCategory getCategory() {
    return category;
  }

  public void setCategory(ProductCategory category) {
    this.category = category;
  }
}

public class ElectronicsProduct extends Product {
    private String brand;
    private int warrantyPeriod; // in months

    public ElectronicsProduct(String sku, String name, double price, int quantity, int threshold) {
        super();
        setSku(sku);
        setName(name);
        setPrice(price);
        setQuantity(quantity);
        setCategory(ProductCategory.ELECTRONICS);
        setThreshold(threshold);
    }

    // Getters and setters for electronics-specific attributes
    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }
}

public class ClothingProduct extends Product {
    private String size;
    private String color;

    public ClothingProduct(String sku, String name, double price, int quantity,int threshold) {
        super();
        setSku(sku);
        setName(name);
        setPrice(price);
        setQuantity(quantity);
        setCategory(ProductCategory.CLOTHING);
        setThreshold(threshold);
    }

    // Getters and setters for clothing-specific attributes
    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}

public class GroceryProduct extends Product {
    private Date expiryDate;
    private boolean refrigerated;

    public GroceryProduct(String sku, String name, double price, int quantity, int threshold) {
        super();
        setSku(sku);
        setName(name);
        setPrice(price);
        setQuantity(quantity);
        setCategory(ProductCategory.GROCERY);
        setThreshold(threshold);
    }

    // Getters and setters for grocery-specific attributes
    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public boolean isRefrigerated() {
        return refrigerated;
    }

    public void setRefrigerated(boolean refrigerated) {
        this.refrigerated = refrigerated;
    }
}

// Product Factory class implementing Factory Pattern
public class ProductFactory {
    public Product createProduct(ProductCategory category, String sku, String name, double price, int quantity, int threshold) {
        switch (category) {
            case ELECTRONICS:
                return new ElectronicsProduct(sku, name, price, quantity,threshold);
            case CLOTHING:
                return new ClothingProduct(sku, name, price, quantity,threshold);
            case GROCERY:
                return new GroceryProduct(sku, name, price, quantity,threshold);
            default:
                throw new IllegalArgumentException(
                        "Unsupported product category: " + category);
        }
    }
}

public class Warehouse {
  private int id;
  private String name;
  private String location;
  private Map<String, Product> products; // SKU -> Product
  public Warehouse(int id, String name, String location) {
    this.id = id;
    this.name = name;
    this.location = location;
    this.products = new HashMap<>();
  }

  public void setLocation(String location) {
    this.location = location;
  }

  // Add a product to the warehouse
  public void addProduct(Product product, int quantity) {
    String sku = product.getSku();
    if (products.containsKey(sku)) {
      // Product exists, update quantity
      Product existingProduct = products.get(sku);
      existingProduct.addStock(quantity);
    } else {
      // New product, add to inventory
      product.setQuantity(quantity);
      products.put(sku, product);
    }
    System.out.println(quantity + " units of " + product.getName()
        + " (SKU: " + sku + ") added to " + name
        + ". New quantity: " + getAvailableQuantity(sku));
  }

  // Remove a product from the warehouse
  public boolean removeProduct(String sku, int quantity) {
    if (products.containsKey(sku)) {
      Product product = products.get(sku);
      int currentQuantity = product.getQuantity();
      if (currentQuantity >= quantity) {
        // Sufficient inventory to remove
        product.removeStock(quantity);
        System.out.println(quantity + " units of " + product.getName()
            + " (SKU: " + sku + ") removed from " + name
            + ". Remaining quantity: " + product.getQuantity());
        // If quantity becomes zero, consider removing the product entirely
        if (product.getQuantity() == 0) {
          // Remove products with zero quantity
          products.remove(sku);
          System.out.println("Product " + product.getName()
              + " removed from inventory as quantity is now zero.");
        }
        return true;
      } else {
        System.out.println("Error: Insufficient inventory. Requested: "
            + quantity + ", Available: " + currentQuantity);
        return false;
      }
    } else {
      System.out.println(
          "Error: Product with SKU " + sku + " not found in " + name);
      return false;
    }
  }

  // Get available quantity of a product
  public int getAvailableQuantity(String sku) {
    if (products.containsKey(sku)) {
      return products.get(sku).getQuantity();
    }
    return 0; // Product not found
  }

  // Get a product by SKU
  public Product getProductBySku(String sku) {
    return products.get(sku);
  }

  // Get all products in this warehouse
  public Collection<Product> getAllProducts() {
    return products.values();
  }
}

public class InventoryManager {
  // Singleton instance
  private static InventoryManager instance;

  // System components
  private List<Warehouse> warehouses;
  private ProductFactory productFactory;
  private ReplenishmentStrategy replenishmentStrategy;

  // Private constructor to prevent instantiation from outside
  private InventoryManager() {
    // Initialize collections and dependencies
    warehouses = new ArrayList<>();
    productFactory = new ProductFactory();
  }

  // Static method to get the singleton instance with thread safety
  public static synchronized InventoryManager getInstance() {
    if (instance == null) {
      instance = new InventoryManager();
    }
    return instance;
  }

  // Strategy pattern method
  public void setReplenishmentStrategy(ReplenishmentStrategy strategy) {
    this.replenishmentStrategy = strategy;
  }

  // Warehouse management
  public void addWarehouse(Warehouse warehouse) {
    warehouses.add(warehouse);
  }

  public void removeWarehouse(Warehouse warehouse) {
    warehouses.remove(warehouse);
  }

  // Product inventory operations
  public Product getProductBySku(String sku) {
    for (Warehouse warehouse : warehouses) {
      Product product = warehouse.getProductBySku(sku);
      if (product != null) {
        return product;
      }
    }
    return null;
  }

  // Check stock levels and apply replenishment strategy if needed
  public void checkAndReplenish(String sku) {
    Product product = getProductBySku(sku);
    if (product != null) {
      // If product is below threshold, notify observers
      if (product.getQuantity() < product.getThreshold()) {
        notifyObservers(product);
        // Apply current replenishment strategy
        if (replenishmentStrategy != null) {
          replenishmentStrategy.replenish(product);
        }
      }
    }
  }

  // Global inventory check
  public void performInventoryCheck() {
    for (Warehouse warehouse : warehouses) {
      for (Product product : warehouse.getAllProducts()) {
        if (product.getQuantity() < product.getThreshold()) {
          notifyObservers(product);
          if (replenishmentStrategy != null) {
            replenishmentStrategy.replenish(product);
          }
        }
      }
    }
  }
  
   // Observer pattern methods
  public void addObserver(InventoryObserver observer) {
    observers.add(observer);
  }

  public void removeObserver(InventoryObserver observer) {
    observers.remove(observer);
  }

  public void notifyObservers(Product product) {
    for (InventoryObserver observer : observers) {
      observer.update(product);
    }
  }
}

public interface InventoryObserver {
	    void update(Product product);
}

public class SupplierNotifier implements InventoryObserver {
  private String supplierName;
  private String contactEmail;

  public SupplierNotifier(String supplierName, String contactEmail) {
    this.supplierName = supplierName;
    this.contactEmail = contactEmail;
  }

  @Override
  public void update(Product product) {
    if (product.getQuantity() < product.getThreshold()) {
      // Send email notification to supplier
      System.out.println("Notification sent to " + supplierName
          + " for low stock of " + product.getName());
    }
  }
}

// Second concrete observer - Dashboard alert system
public class DashboardAlertSystem implements InventoryObserver {
  private String alertLevel;
  private List<String> adminUsers;

  public DashboardAlertSystem(String alertLevel, List<String> adminUsers) {
    this.alertLevel = alertLevel;
    this.adminUsers = adminUsers;
  }

  @Override
  public void update(Product product) {
    double stockPercentage =
        ((double) product.getQuantity() / product.getThreshold()) * 100;

    if (stockPercentage <= 25) {
      // Critical alert - red notification
      System.out.println("CRITICAL ALERT: " + product.getName()
          + " stock critically low at " + product.getQuantity() + " units ("
          + String.format("%.1f", stockPercentage) + "% of threshold)");
      notifyAdmins(product, "CRITICAL");
    } else if (stockPercentage <= 50) {
      // Warning alert - yellow notification
      System.out.println("WARNING ALERT: " + product.getName()
          + " stock low at " + product.getQuantity() + " units ("
          + String.format("%.1f", stockPercentage) + "% of threshold)");
      notifyAdmins(product, "WARNING");
    }
  }

  private void notifyAdmins(Product product, String level) {
    for (String admin : adminUsers) {
      System.out.println("Dashboard notification sent to admin: " + admin
          + " - " + level + " level alert for " + product.getName());
      // Actual implementation would update dashboard UI and push notifications
    }
  }
}

public class Main {
  public static void main(String[] args) {
    // Get the singleton instance of InventoryManager
    InventoryManager inventoryManager = InventoryManager.getInstance();

    // Create and add warehouses
    Warehouse warehouse1 = new Warehouse("Warehouse 1");
    Warehouse warehouse2 = new Warehouse("Warehouse 2");
    inventoryManager.addWarehouse(warehouse1);
    inventoryManager.addWarehouse(warehouse2);

    // Create products using ProductFactory
    ProductFactory productFactory = new ProductFactory();
    Product laptop = productFactory.createProduct(
        ProductCategory.ELECTRONICS, "SKU123", "Laptop", 1000.0, 50, 25);
    Product tShirt = productFactory.createProduct(
        ProductCategory.CLOTHING, "SKU456", "T-Shirt", 20.0, 200, 100);
    Product apple = productFactory.createProduct(
        ProductCategory.GROCERY, "SKU789", "Apple", 1.0, 100, 200);

    // Add products to warehouses
    warehouse1.addProduct(laptop, 15);
    warehouse1.addProduct(tShirt, 20);
    warehouse2.addProduct(apple, 50);

    // Set replenishment strategy to Just-In-Time
    inventoryManager.setReplenishmentStrategy(new JustInTimeStrategy());

    // Perform inventory check and replenish if needed
    inventoryManager.performInventoryCheck();

    // Switch replenishment strategy to Bulk Order
    inventoryManager.setReplenishmentStrategy(new BulkOrderStrategy());

    // Replenish a specific product if needed
    inventoryManager.checkAndReplenish("SKU123");
  }
}

// Product class modified to work with Builder pattern
public abstract class Product {
  // Core attributes
  private final String sku;
  private final String name;
  private final double price;
  private int quantity; // optional
  private final int threshold; // optional
  private final ProductCategory category;

  // Protected constructor to be used by concrete builders
  protected Product(Builder<?> builder) {
    this.sku = builder.sku;
    this.name = builder.name;
    this.price = builder.price;
    this.quantity = builder.quantity;
    this.threshold = builder.threshold;
    this.category = builder.category;
  }

  // Getters
  public String getSku() {
    return sku;
  }
  public String getName() {
    return name;
  }
  public double getPrice() {
    return price;
  }
  public int getQuantity() {
    return quantity;
  }
  public int getThreshold() {
    return threshold;
  }
  public ProductCategory getCategory() {
    return category;
  }

  // Setters only for mutable properties
  public void setQuantity(int quantity) {
    this.quantity = quantity;
  }

  // Abstract Builder class
  public static abstract class Builder<T extends Builder<T>> {
    // Required parameters
    private final String sku;
    private final String name;
    private final double price;
    private final ProductCategory category;

    // Optional parameters with default values
    private int quantity = 0;
    private int threshold = 10;

    // Constructor with required parameters
    public Builder(
        String sku, String name, double price, ProductCategory category) {
      this.sku = sku;
      this.name = name;
      this.price = price;
      this.category = category;
    }

    // Methods to set optional parameters
    public T quantity(int quantity) {
      this.quantity = quantity;
      return self();
    }

    public T threshold(int threshold) {
      this.threshold = threshold;
      return self();
    }

    // Method to be overridden by subclasses to return this (the current object)
    protected abstract T self();

    // Build method to be implemented by concrete builders
    public abstract Product build();
  }
}

// Electronics Product with Builder
public class ElectronicsProduct extends Product {
  // Electronics-specific attributes
  private final String brand;
  private final int warrantyPeriod;
  private final String modelNumber;
  private final boolean wirelessConnectivity;
  private final int powerConsumption;

  private ElectronicsProduct(ElectronicsBuilder builder) {
    super(builder);
    this.brand = builder.brand;
    this.warrantyPeriod = builder.warrantyPeriod;
    this.modelNumber = builder.modelNumber;
    this.wirelessConnectivity = builder.wirelessConnectivity;
    this.powerConsumption = builder.powerConsumption;
  }

  // Getters for electronics-specific attributes
  public String getBrand() {
    return brand;
  }
  public int getWarrantyPeriod() {
    return warrantyPeriod;
  }
  public String getModelNumber() {
    return modelNumber;
  }
  public boolean hasWirelessConnectivity() {
    return wirelessConnectivity;
  }
  public int getPowerConsumption() {
    return powerConsumption;
  }

  // Concrete Builder for ElectronicsProduct
  public static class ElectronicsBuilder extends Builder<ElectronicsBuilder> {
    // Required electronics parameters
    private final String brand;

    // Optional electronics parameters with default values
    private int warrantyPeriod = 12; // 12 months default
    private String modelNumber = "";
    private boolean wirelessConnectivity = false;
    private int powerConsumption = 0;

    public ElectronicsBuilder(
        String sku, String name, double price, String brand) {
      super(sku, name, price, ProductCategory.ELECTRONICS);
      this.brand = brand;
    }

    // Methods to set optional electronics-specific parameters
    public ElectronicsBuilder warrantyPeriod(int warrantyPeriod) {
      this.warrantyPeriod = warrantyPeriod;
      return this;
    }

    public ElectronicsBuilder modelNumber(String modelNumber) {
      this.modelNumber = modelNumber;
      return this;
    }

    public ElectronicsBuilder wirelessConnectivity(
        boolean wirelessConnectivity) {
      this.wirelessConnectivity = wirelessConnectivity;
      return this;
    }

    public ElectronicsBuilder powerConsumption(int powerConsumption) {
      this.powerConsumption = powerConsumption;
      return this;
    }

    @Override
    protected ElectronicsBuilder self() {
      return this;
    }

    @Override
    public ElectronicsProduct build() {
      return new ElectronicsProduct(this);
    }
  }
}
