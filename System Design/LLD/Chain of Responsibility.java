// Online Java Compiler
// Use this editor to write, compile and run your Java code online
abstract class Approver {
  protected Approver nextApprover;
  // Set the next handler in the chain
  public void setNextApprover(Approver nextApprover) {
    this.nextApprover = nextApprover;
  }
  // Abstract method to process the leave request
  public abstract void processLeaveRequest(int leaveDays);
}

class Supervisor extends Approver {
  @Override
  public void processLeaveRequest(int leaveDays) {
    if (leaveDays <= 3) {
      System.out.println("Supervisor approved the leave.");
    } else if (nextApprover != null) {
      nextApprover.processLeaveRequest(leaveDays);
    }
  }
}

class Manager extends Approver {
  @Override
  public void processLeaveRequest(int leaveDays) {
    if (leaveDays <= 7) {
      System.out.println("Manager approved the leave.");
    } else if (nextApprover != null) {
      nextApprover.processLeaveRequest(leaveDays);
    }
  }
}

class Director extends Approver {
  @Override
  public void processLeaveRequest(int leaveDays) {
    if (leaveDays <= 14) {
      System.out.println("Director approved the leave.");
    } else if (nextApprover != null) { // Pass on if not handled
      nextApprover.processLeaveRequest(leaveDays);
    } else {
      System.out.println("Leave request denied. Too many days!");
    }
  }
}

class HR extends Approver {
    @Override
    public void processLeaveRequest(int leaveDays) {
        System.out.println("HR: Leave request requires further discussion.");
    }
}

public class Main {
  public static void main(String[] args) {
    // Create handler instances
    Approver supervisor = new Supervisor();
    Approver manager = new Manager();
    Approver director = new Director();
    Approver hr = new HR();
    // Set up the chain: Supervisor -> Manager -> Director -> HR
    supervisor.setNextApprover(manager);
    manager.setNextApprover(director);
    director.setNextApprover(hr); // Now HR handles any unprocessed request
    // Process a leave request that exceeds Director's approval limit
    int leaveDays = 20;
    System.out.println("Employee requests " + leaveDays + " days of leave.");
    supervisor.processLeaveRequest(leaveDays);
  }
}
