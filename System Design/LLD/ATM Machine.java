import java.util.*;
// Online Java Compiler
// Use this editor to write, compile and run your Java code online
 enum CashType {
	    BILL_100(100),
	    BILL_50(50),
	    BILL_20(20),
	    BILL_10(10),
	    BILL_5(5),
	    BILL_1(1);
	    
	    public final int value;
	    
	    CashType(int value) {
	        this.value = value;
	    }
}

// Enum representing different transaction types
 enum TransactionType {
    WITHDRAW_CASH,
    CHECK_BALANCE
}

 class Card {
	    private String cardNumber;
	    private int pin;
	    private String accountNumber;
	     // Constructor
	    public Card(String cardNumber, int pin, String accountNumber) {
	        this.cardNumber = cardNumber;
	        this.pin = pin;
	        this.accountNumber = accountNumber;
	     }
	    // Getters and Setters
	     public String getCardNumber() {
	        return cardNumber;
	     }

	     public boolean validatePin(int enteredPin) {
	        return this.pin == enteredPin;
	     }

	     public String getAccountNumber() {
	        return accountNumber;
	     }
}

class Account {
	    private String accountNumber;
	    private double balance;
        public Account(String accountNumber, double initialBalance) {
	        this.accountNumber = accountNumber;
	        this.balance = initialBalance;
	    }
	    
	    public boolean withdraw(double amount) {
	        if (balance >= amount) {
	            balance -= amount;
	            return true;
	        }
	        return false;
	    }
	    
	    public void deposit(double amount) {
	        balance += amount;
	    }
	    
	    public double getBalance() {
	        return balance;
	    }
	    
	    public String getAccountNumber() {
	        return accountNumber;
	    }
}


class ATMMachineInventory{
        private Map<CashType,Integer> cashInventory=new HashMap<>();
        public ATMMachineInventory(){
	        this.cashInventory=new HashMap<>();
	        initializeRepository();
	    }
	    void initializeRepository(){
	        cashInventory.put(CashType.BILL_100, 10);
            cashInventory.put(CashType.BILL_50, 10);
            cashInventory.put(CashType.BILL_20, 20);
            cashInventory.put(CashType.BILL_10, 30);
            cashInventory.put(CashType.BILL_5, 20);
            cashInventory.put(CashType.BILL_1, 50);
	    }
	    // Get total cash available in the ATM
          public int getTotalCash() {
            int total = 0;
            for (Map.Entry<CashType, Integer> entry : cashInventory.entrySet()) {
              total += entry.getKey().value * entry.getValue();
            }
            return total;
          }
        
          // Check if ATM has sufficient cash for a withdrawal
          public boolean hasSufficientCash(int amount) {
            return getTotalCash() >= amount;
          }
        
          // Dispense cash for a withdrawal
          public Map<CashType, Integer> dispenseCash(int amount) {
            if (!hasSufficientCash(amount)) {
              return null;
            }
            Map<CashType, Integer> dispensedCash = new HashMap<>();
            int remainingAmount = amount;
            // Dispense from largest denomination to smallest
            for (CashType cashType : CashType.values()) {
              int count = Math.min(
                  remainingAmount / cashType.value, cashInventory.get(cashType));
              if (count > 0) {
                dispensedCash.put(cashType, count);
                remainingAmount -= count * cashType.value;
                cashInventory.put(cashType, cashInventory.get(cashType) - count);
              }
            }
            // If we couldn't make exact change
            if (remainingAmount > 0) {
              // Rollback the transaction
              for (Map.Entry<CashType, Integer> entry : dispensedCash.entrySet()) {
                cashInventory.put(entry.getKey(),
                    cashInventory.get(entry.getKey()) + entry.getValue());
              }
              return null;
            }
            return dispensedCash;
          }
        
          // Add cash to inventory (for maintenance/refill)
          public void addCash(CashType cashType, int count) {
            cashInventory.put(cashType, cashInventory.get(cashType) + count);
          }
}

// ATM State Factory (Singleton)
 class ATMStateFactory {
    private static ATMStateFactory instance = null;
    
    private ATMStateFactory() {}
    
    public static ATMStateFactory getInstance() {
        if (instance == null) {
            instance = new ATMStateFactory();
        }
        return instance;
    }
    
    public ATMState createIdleState() {
        return new IdleState();
    }
    
    public ATMState createHasCardState() {
        return new HasCardState();
    }
    
    public ATMState createSelectOperationState() {
        return new SelectOperationState();
    }
    
    public ATMState createTransactionState() {
        return new TransactionState();
    }
}
	
interface ATMState{
    ATMState next(ATMMachineContext context);
    
    String getState();
        
    
}

class IdleState implements ATMState {
    @Override
    public ATMState next(ATMMachineContext context) {
        return context.getStateFactory().createHasCardState(); // FIX: was returning `this`
    }
 
    @Override
    public String getState() {
        return "IdleState";
    }
}
 
// FIX: added `public` modifiers to satisfy interface
class HasCardState implements ATMState {
    @Override
    public ATMState next(ATMMachineContext context) {
        if (context.getCurrentCard() == null) {
            return context.getStateFactory().createIdleState();
        }
        return context.getStateFactory().createSelectOperationState(); // FIX: return new state
    }
 
    @Override
    public String getState() {
        return "HasCardState";
    }
}
 
class SelectOperationState implements ATMState {
    @Override
    public ATMState next(ATMMachineContext context) {
        if (context.getCurrentCard() == null) {
            return context.getStateFactory().createIdleState();
        }
        return context.getStateFactory().createTransactionState(); // FIX: return new state
    }
 
    @Override
    public String getState() {
        return "SelectOperationState";
    }
}
 
class TransactionState implements ATMState {
    @Override
    public ATMState next(ATMMachineContext context) {
        if (context.getCurrentCard() == null) {
            return context.getStateFactory().createIdleState();
        }
        return context.getStateFactory().createSelectOperationState(); // FIX: return SelectOperation for another tx
    }
 
    @Override
    public String getState() {
        return "TransactionState";
    }
}

class ATMMachineContext{
    private  ATMState currentState;
    private  ATMStateFactory stateFactory;
    private  ATMMachineInventory atmInventory;
    private  Card currentCard;
    private  Account currentAccount;
    private  TransactionType selectedOperation;
    private Map<String, Account> accounts;
    
    public ATMMachineContext(){
        this.atmInventory=new ATMMachineInventory();
        this.currentCard=null;
        this.currentAccount=null;
        this.stateFactory = ATMStateFactory.getInstance();  
        this.currentState = stateFactory.createIdleState();
        this.accounts = new HashMap<>();
    }
    
    public void advanceState(){
        ATMState nextState=currentState.next(this);
        this.currentState=nextState;
        System.out.println("Current state: " + currentState.getState());
    }
    
    // Card insertion operation
      public void insertCard(Card card) {
        if (currentState instanceof IdleState) {
          System.out.println("Card inserted");
          this.currentCard = card;
          advanceState();
        } else {
          System.out.println(
              "Cannot insert card in " + currentState.getState());
        }
      }
    
      // PIN authentication operation
      public void enterPin(int pin) {
        if (currentState instanceof HasCardState) {
          if (currentCard.validatePin(pin)) {
            System.out.println("PIN authenticated successfully");
            currentAccount = accounts.get(currentCard.getAccountNumber());
            advanceState();
          } else {
            System.out.println("Invalid PIN. Please try again");
            // Could implement PIN retry logic here
          }
        } else {
          System.out.println("Cannot enter PIN in " + currentState.getState());
        }
      }
    
      // Select operation (withdrawal, balance check, etc.)
      public void selectOperation(TransactionType transactionType) {
        if (currentState instanceof SelectOperationState) {
          System.out.println("Selected operation: " + transactionType);
          this.selectedOperation = transactionType;
          advanceState();
        } else {
          System.out.println(
              "Cannot select operation in " + currentState.getState());
        }
      }
      
      // Perform the selected transaction
      public void performTransaction(double amount) {
        if (currentState instanceof TransactionState) {
          try {
            if (selectedOperation == TransactionType.WITHDRAW_CASH) {
              performWithdrawal(amount);
            } else if (selectedOperation == TransactionType.CHECK_BALANCE) {
              checkBalance();
            }
            // Ask if user wants another transaction
            advanceState();
          } catch (Exception e) {
            System.out.println("Transaction failed: " + e.getMessage());
            // Go back to select operation state
            currentState = stateFactory.createSelectOperationState();
          }
        } else {
          System.out.println(
              "Cannot perform transaction in " + currentState.getState());
        }
      }
      
      // Return card to user
      public void returnCard() {
        if (currentState instanceof HasCardState
            || currentState instanceof SelectOperationState
            || currentState instanceof TransactionState) {
          System.out.println("Card returned to customer");
          resetATM();
        } else {
          System.out.println("No card to return in " + currentState.getState());
        }
      }
    
      // Cancel current transaction
      public void cancelTransaction() {
        if (currentState instanceof TransactionState
            || currentState instanceof TransactionState) {
          System.out.println("Transaction cancelled");
          returnCard();
        } else {
          System.out.println(
              "No transaction to cancel in " + currentState.getState());
        }
      }
      
      // Helper method to perform withdrawal
      private void performWithdrawal(double amount) throws Exception {
        // Check if user has sufficient balance
        if (!currentAccount.withdraw(amount)) {
          throw new Exception("Insufficient funds in account");
        }
        // Check if ATM has sufficient cash
        if (!atmInventory.hasSufficientCash((int) amount)) {
          // Rollback the account withdrawal
          currentAccount.deposit(amount);
          throw new Exception("Insufficient cash in ATM");
        }
        Map<CashType, Integer> dispensedCash =
            atmInventory.dispenseCash((int) amount);
        if (dispensedCash == null) {
          // Rollback the account withdrawal
          currentAccount.deposit(amount);
          throw new Exception("Unable to dispense exact amount");
        }
        System.out.println("Transaction successful. Please collect your cash:");
        for (Map.Entry<CashType, Integer> entry : dispensedCash.entrySet()) {
          System.out.println(entry.getValue() + " x $" + entry.getKey().value);
        }
      }
  
    // Helper method to check balance
      private void checkBalance() {
        System.out.println(
            "Your current balance is: $" + currentAccount.getBalance());
      }
  
    // Reset ATM state
      private void resetATM() {
        this.currentCard = null;
        this.currentAccount = null;
        this.selectedOperation = null;
        this.currentState = stateFactory.createIdleState();
      }
    
      // Getters and setters
      public ATMState getCurrentState() {
        return currentState;
      }
    
      public void setCurrentState(ATMState state) {
        this.currentState = state;
      }
    
      public Card getCurrentCard() {
        return currentCard;
      }
    
      public Account getCurrentAccount() {
        return currentAccount;
      }
    
      public ATMMachineInventory getATMInventory() {
        return atmInventory;
      }
    
      public TransactionType getSelectedOperation() {
        return selectedOperation;
      }
    
      public ATMStateFactory getStateFactory() {
        return stateFactory;
      }
    
      // Add an account to the ATM (for demo purposes)
      public void addAccount(Account account) {
        accounts.put(account.getAccountNumber(), account);
      }
    
      // Get account by number
      public Account getAccount(String accountNumber) {
        return accounts.get(accountNumber);
      }
}

class Main {
    public static void main(String[] args) {
        ATMMachineContext atm=new ATMMachineContext();
    // Add sample accounts
        atm.addAccount(new Account("123456", 1000.0));
        atm.addAccount(new Account("654321", 500.0));
        
        try {
            // Sample workflow
            System.out.println("=== Starting ATM Demo ===");
            
            // Insert card
            atm.insertCard(new Card("123456", 1234, "654321"));
            
            // Enter PIN
            atm.enterPin(1234);
            
            // Select operation
            atm.selectOperation(TransactionType.WITHDRAW_CASH);
            
            // Perform transaction
            atm.performTransaction(100.0);
            
            // Select another operation
            atm.selectOperation(TransactionType.CHECK_BALANCE);
            
            // Perform balance check
            atm.performTransaction(0.0);
            
            // Return card
            atm.returnCard();
            
            System.out.println("=== ATM Demo Completed ===");
	            
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }   
    }
}
