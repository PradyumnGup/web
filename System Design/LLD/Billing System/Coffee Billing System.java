// Online Java Compiler
// Use this editor to write, compile and run your Java code online
interface Coffee {
    String getDescription();
    double getCost();
}
class Espresso implements Coffee {
    @Override
    public String getDescription() {
        return "Espresso";
    }
    @Override
    public double getCost() {
        return 2.00;
    }
}

class Cappuccino implements Coffee {
    @Override
    public String getDescription() {
        return "Cappuccino";
    }
    @Override
    public double getCost() {
        return 3.00;
    }
}

abstract class CoffeeDecorator implements Coffee {
    protected Coffee coffee;
    public CoffeeDecorator(Coffee coffee) {
        this.coffee = coffee;
    }
    @Override
    public String getDescription() {
        return coffee.getDescription();
    }
    @Override
    public double getCost() {
        return coffee.getCost();
    }
}

class MilkDecorator extends CoffeeDecorator {
    public MilkDecorator(Coffee coffee) {
        super(coffee);
    }
    @Override
    public String getDescription() {
        return coffee.getDescription() + ", Milk";
    }
    @Override
    public double getCost() {
        return coffee.getCost() + 0.50;
    }
}
class SugarDecorator extends CoffeeDecorator {
    public SugarDecorator(Coffee coffee) {
        super(coffee);
    }
    @Override
    public String getDescription() {
        return coffee.getDescription() + ", Sugar";
    }
    @Override
    public double getCost() {
        return coffee.getCost() + 0.25;
    }
}

class VanillaDecorator extends CoffeeDecorator {
    public VanillaDecorator(Coffee coffee) {
        super(coffee);
    }
    @Override
    public String getDescription() {
        return coffee.getDescription() + ", Vanilla";
    }
    @Override
    public double getCost() {
        return coffee.getCost() + 0.75;
    }
}

class Main {
    public static void main(String[] args) {
        Coffee coffee = new Espresso();
        coffee = new MilkDecorator(coffee);
        coffee = new SugarDecorator(coffee);
        System.out.println("Order: " + coffee.getDescription());
        System.out.println("Total Cost: $" + coffee.getCost());
        Coffee anotherCoffee = new Cappuccino();
        anotherCoffee = new VanillaDecorator(anotherCoffee);
        System.out.println("Order: " + anotherCoffee.getDescription());
        System.out.println("Total Cost: $" + anotherCoffee.getCost());
        
    }
}

//O/p
// Order: Espresso, Milk, Sugar
// Total Cost: $2.75
// Order: Cappuccino, Vanilla
// Total Cost: $3.75

// === Code Execution Successful ===
  
