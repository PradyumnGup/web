// Online Java Compiler
// Use this editor to write, compile and run your Java code online
import java.util.*;

enum Coin {
    ONE_RUPEE(1),
    TWO_RUPEES(2),
    FIVE_RUPEES(5),
    TEN_RUPEES(10);
    // Value of the coin in Indian rupees
    public int value;
    // Constructor to initialize the coin's value
    Coin(int value) {
        this.value = value;
    }
}

enum ItemType {
    COKE,
    PEPSI,
    JUICE,
    SODA
}

class Item {
    // Type of the item (e.g., COKE, PEPSI, JUICE, SODA)
    private ItemType type;
    
    // Price of the item
    private int price;
    // Getters and Setters
    public ItemType getType() {
        return type;
    }
    public void setType(ItemType type) {
        this.type = type;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}

// Class representing a slot in the vending machine that holds multiple items
    class ItemShelf {
	    // Code to identify the slot
	    private int code;
	    // List of items stored in the slot
	    private List<Item> items;
	    // Flag to indicate if the shelf is sold out
	    private boolean isSoldOut;

	    // Constructor to initialize the item shelf
	    public ItemShelf(int code) {
	        this.code = code;
	        this.items = new ArrayList<>(); // Initialize the list of items
	        this.isSoldOut = false;
	    }

	    // Getters and Setters
	    public int getCode() {
	        return code;
	    }

	    public void setCode(int code) {
	        this.code = code;
	    }

	    public List<Item> getItems() {
	        return items;
	    }

	    public boolean checkIsSoldOut() {
	        return isSoldOut;
	    }

	    public void setIsSoldOut(boolean isSoldOut) {
	        this.isSoldOut = isSoldOut;
	    }

	    public void setItems(List<Item> items) {
	        this.items = items;
	        if(isSoldOut) setIsSoldOut(false); // Update the sold-out status when items are set
	    }

	    // Add an item to the shelf
	    public void addItem(Item item) {
	        items.add(item);
	        if(isSoldOut) setIsSoldOut(false); // Update sold-out status after adding an item
	    }

	    // Remove an item from the shelf
	    public void removeItem(Item item) {
	        items.remove(item);
	        if(items.isEmpty()) setIsSoldOut(true); // Update sold-out status after removing an item
	    }

       
  	}

class Inventory{
    // Array to hold item shelves in the inventory
  ItemShelf[] inventory = null;

  // Constructor for Inventory
  public Inventory(int itemCount) {
    inventory = new ItemShelf[itemCount]; // Initialize the array with the given item count
    initialEmptyInventory(); // Initialize the inventory with empty shelves
  }

  // Getter for the inventory array
  public ItemShelf[] getInventory() {
    return inventory;
  }
  
  // Method to initialize the inventory with empty shelves
  public void initialEmptyInventory() {
    int startCode = 101; // Starting code for item shelves
    for (int i = 0; i < inventory.length; i++) {
      ItemShelf space = new ItemShelf(startCode); // Create a new item shelf with a code
      inventory[i] = space; // Add the shelf to the inventory
      startCode++; // Increment the code for the next shelf
    }
  }
  
  public void addItem(Item item, int codeNumber){
      for(ItemShelf itemshelf : inventory){
          if(codeNumber == itemshelf.getCode()){
            itemshelf.addItem(item);
            return;
          }
      }
  }
  
  // Method to get and remove an item from the inventory by its code number
  public Item getItem(int codeNumber) throws Exception {
    for (ItemShelf itemShelf : inventory) {
      if (itemShelf.getCode() == codeNumber) {
        if (itemShelf.checkIsSoldOut()) {
          throw new Exception("Item already sold out");
        } else {
          // Get and remove the first item from the shelf
          Item item = itemShelf.getItems().get(0); // Get the first item
          return item;
        }
      }
    }
    throw new Exception("Invalid Code");
  }

  // Method to update an item shelf as sold out by its code number
  public void updateSoldOutItem(int codeNumber) {
    for (ItemShelf itemShelf : inventory) {
      if (itemShelf.getCode() == codeNumber) {
        if (itemShelf.getItems().isEmpty())
          itemShelf.setIsSoldOut(true); // Mark the shelf as sold out
      }
    }
  }
  
  public void removeItem(Item item, int codeNumber){
      for(ItemShelf itemshelf : inventory){
          if(codeNumber == itemshelf.getCode()){
            itemshelf.removeItem(itemshelf.getItems().get(0));
            return;
          }
      }
  }
  
  public boolean hasItems() { 
        for(ItemShelf itemShelf : inventory){ 
            if(!itemShelf.checkIsSoldOut()) return true; 
        } 
        return false;  
  } 
  
}

interface VendingMachineState{
    String getStateName();
    VendingMachineState next(VendingMachineContext context);
}

class IdleState implements VendingMachineState{
    
    @Override
    public String getStateName(){
        return "idleState";
    }
    
    @Override
    public VendingMachineState next(VendingMachineContext context){
        // Check if inventory has items
        if (!context.getInventory().hasItems()) {
          return new OutOfStockState();
        }
        
        if (!context.getCoinList().isEmpty()) {
            return new HasCoinState();
        }
        
        return this;
    }
    
}
class HasCoinState implements VendingMachineState{
    
    @Override
    public String getStateName(){
        return "HasCoinState";
    }
    @Override
    public VendingMachineState next(VendingMachineContext context){
        if (!context.getInventory().hasItems()) {
            return new OutOfStockState();
        }
        if (context.getCoinList().isEmpty()) {
            return new IdleState();
        }
        if (context.getCurrentState() instanceof HasCoinState) {
            return new SelectionState();
        }
        return this;
    }
}

class SelectionState implements VendingMachineState{
    @Override
    public String getStateName(){
        return "SelectionState";
    }
    @Override
    public VendingMachineState next(VendingMachineContext context){
        // If inventory has no items, transition to OutOfStock
        if (!context.getInventory().hasItems()) {
            return new OutOfStockState();
        }	        
        // If no money left, go back to idle
        if (context.getCoinList().isEmpty()) {
            return new IdleState();
        }
        
        // If an item has been selected, transition to dispense state
        if (context.getSelectedItemCode() > 0) {
            return new DispenseState();
        }
        return this;
    }
}

class DispenseState implements VendingMachineState {
    public DispenseState() {
        System.out.println("Vending machine is now in Dispense State");
    }
    
    @Override
    public String getStateName() {
        return "DispenseState";
    }
    
    @Override
    public VendingMachineState next(VendingMachineContext context) {
        // Dispense the selected product
        return new IdleState();
 	}
}

class OutOfStockState implements VendingMachineState {
    public OutOfStockState() {
        System.out.println("Vending machine is now in Out of Stock State");
    }
    
    @Override
    public String getStateName() {
        return "OutOfStockState";
    }
    
    @Override
    public VendingMachineState next(VendingMachineContext context) {
        // If inventory has items again, return to idle state
        if (context.getInventory().hasItems()) {
            return new IdleState();
        }
        
        // Otherwise, remain in out of stock state
        return this;
    }
}

class VendingMachineContext{
    private VendingMachineState currentState; 
    private List<Coin>coinList; 
    private Inventory inventory; // Inventory to store items
    private int selectedItemCode; // Code of the selected item
    
    public VendingMachineContext() {
        this.inventory = new Inventory(10);  // FIX 8: initialize inventory
        this.coinList = new ArrayList<>();
        this.selectedItemCode = 0;
        this.currentState = new IdleState();
    }
    
    public void advanceState(){
        VendingMachineState next=currentState.next(this);
        currentState=next;
        return;
    }
    
    public void clickOnInsertCoinButton(Coin coin) {
        if(currentState instanceof IdleState || currentState instanceof HasCoinState){
            coinList.add(coin);
            advanceState();
        }
        else {
          System.out.println("Cannot insert coin in " + currentState.getStateName());
        }
    }
    
    public void clickOnStartProductSelectionButton(int codeNumber) {
        if(currentState instanceof HasCoinState){
            advanceState();
            selectProduct(codeNumber);
        }
    }
    public void selectProduct(int codeNumber){
        if(currentState instanceof SelectionState){
            try{
                Item item=inventory.getItem(codeNumber);
                
                int price=item.getPrice();
                int balance=getBalance();
                
                if(balance<price){
                    System.out.println(
                      "Insufficient amount. Product price: " + item.getPrice() + ", paid: " + balance);
                  return;
                }
                
                setSelectedItemCode(codeNumber);
                advanceState();
                dispenseItem(codeNumber);
                
                if (balance >= item.getPrice()) { // Return change if applicable
                  int change = balance - item.getPrice();
                  System.out.println("Returning change: " + change);
                }
            }
            catch(Exception e){
                System.out.println("Error: " + e.getMessage());
                
            }
        }
        else{
            System.out.println("Products can only be selected in Selection state");
        }
    }
    
    public void dispenseItem(int codeNumber){
        if(currentState instanceof DispenseState){
            try{
                Item item=inventory.getItem(codeNumber);
                inventory.removeItem(item, codeNumber);
                inventory.updateSoldOutItem(codeNumber);
                
                resetBalance();
                resetSelection();
                advanceState(); // Move to the next state
            }catch(Exception e){
                    System.out.println("Failed to Dispense the Product with code : " + codeNumber);
            }
        }
        else {
          System.out.println("System cannot dispense in : " + currentState);
        }
    }
    
    // Updates the inventory by adding a new item
    public void updateInventory(Item item, int codeNumber) {
        if (currentState instanceof IdleState) { // Only update inventory in Idle state
          try {
            inventory.addItem(item, codeNumber); // Add the item to inventory
            System.out.println("Added " + item.getType() + " to slot " + codeNumber);
          } catch (Exception e) {
            System.out.println("Error updating inventory: " + e.getMessage());
          }
        } else {
          System.out.println("Inventory can only be updated in Idle state");
        }
    }
    
    // Getters and setters for context properties
    public Inventory getInventory() {
        return inventory;
     }
    
    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }
    
    public List<Coin> getCoinList() {
        return coinList;
    }
    
    public void setCoinList(List<Coin> coinList) {
        this.coinList = coinList;
    }
    
    public int getSelectedItemCode() {
        return selectedItemCode;
    }
    
    public void setSelectedItemCode(int codeNumber) {
        this.selectedItemCode = codeNumber;
    }
    
    // Calculates the total balance from inserted coins
    public int getBalance() {
        int balance = 0;
        for (Coin coin : coinList) {
          balance += coin.value; // Sum up the coin values
        }
        return balance;
    }
    
    // Resets the product selection
    public void resetSelection() {
        this.selectedItemCode = 0;
    }
      
    public void resetBalance() {
        coinList.clear();
    }
    public VendingMachineState getCurrentState() { return currentState; }
    
}
public class Main {
  public static void main(String args[]) {
    VendingMachineContext vendingMachine = new VendingMachineContext();
    try {
      System.out.println("|");
      System.out.println("Filling up the inventory");
      System.out.println("|");
      fillUpInventory(vendingMachine);
      displayInventory(vendingMachine);
      System.out.println("|");
      System.out.println("Select Payment Method:");
      System.out.println("1. Coin Payment");
      System.out.println("2. Card Payment");
      System.out.print("Enter your choice: ");
      Scanner scanner = new Scanner(System.in);
      int paymentChoice = scanner.nextInt();
      scanner.nextLine(); // Consume the newline character
      switch (paymentChoice) {
        case 1:
          System.out.println("Inserting coins");
          vendingMachine.clickOnInsertCoinButton(Coin.TEN_RUPEES);
          vendingMachine.clickOnInsertCoinButton(Coin.FIVE_RUPEES);
          break;
        case 2:
          System.out.println("Making card payment");
          System.out.print("Enter card number: ");
          String cardNumber = scanner.nextLine();
          System.out.print("Enter expiry date (MM/YY): ");
          String expiryDate = scanner.nextLine();
          System.out.print("Enter CVV: ");
          String cvv = scanner.nextLine();
        //   vendingMachine.clickOnCardPaymentButton(cardNumber, expiryDate, cvv);
          break;
        default:
          System.out.println("Invalid payment choice.");
          return;
      }
      System.out.println("|");
      System.out.println("Clicking on ProductSelectionButton");
      System.out.println("|");
      vendingMachine.clickOnStartProductSelectionButton(102);
      displayInventory(vendingMachine);
      scanner.close();
    } catch (Exception e) {
      System.out.println("Error: " + e.getMessage());
      displayInventory(vendingMachine);
    }
  }
    
    // Method to fill up the inventory of the vending machine
  private static void fillUpInventory(VendingMachineContext vendingMachine) {
    for (int i = 0; i < 10; i++) {
      Item newItem = new Item();
      int codeNumber = 101 + i; // Shelf code
      // Set item type and price based on the index range
      if (i >= 0 && i < 3) {
        newItem.setType(ItemType.COKE);
        newItem.setPrice(12);
      } else if (i >= 3 && i < 5) {
        newItem.setType(ItemType.PEPSI);
        newItem.setPrice(9);
      } else if (i >= 5 && i < 7) {
        newItem.setType(ItemType.JUICE);
        newItem.setPrice(13);
      } else if (i >= 7 && i < 10) {
        newItem.setType(ItemType.SODA);
        newItem.setPrice(7);
      }
      // Update the inventory with multiple same items per shelf
      for (int j = 0; j < 5; j++) {
        // Add 5 items to each shelf
        vendingMachine.updateInventory(newItem, codeNumber);
      }
    }
  }
  
  // Method to display the current inventory of the vending machine
  private static void displayInventory(VendingMachineContext vendingMachine) {
    ItemShelf[] slots = vendingMachine.getInventory().getInventory();
    for (ItemShelf slot : slots) {
      List<Item> items = slot.getItems(); // Get the list of items in the shelf
      if (!items.isEmpty()) {
        System.out.println("CodeNumber: " + slot.getCode() + " Items: ");
        for (Item item : items) { // Display all items in the shelf
          System.out.println(
              "    - Item: " + item.getType().name() + ", Price: " + item.getPrice());
        }
        System.out.println("SoldOut: " + slot.checkIsSoldOut());
      } else {
        // Display empty shelf information
        System.out.println("CodeNumber: " + slot.getCode() + " Items: EMPTY"
            + " SoldOut: " + slot.checkIsSoldOut());
      }
    }
  
  }
  
  
  
}
