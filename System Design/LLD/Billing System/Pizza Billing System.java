// Online Java Compiler
// Use this editor to write, compile and run your Java code online
interface Pizza {
    public double getCost();
}
class Margeritta implements Pizza{
    @Override
    public double getCost(){
        return 200;
    }
}

class FarmHouse implements Pizza{
    @Override
    public double getCost(){
        return 500;
    }
}

abstract class PizzaDecorator implements Pizza{
    protected Pizza pizza;
    PizzaDecorator(Pizza pizza){
        this.pizza=pizza;
    }
}

class Cheese extends PizzaDecorator {
    Cheese(Pizza pizza){
        super(pizza);
    }
    public double getCost(){
        return pizza.getCost()+50;
    }
}
class Origano extends PizzaDecorator {
    Origano(Pizza pizza){
        super(pizza);
    }
    public double getCost(){
        return pizza.getCost()+10;
    }
}

class Main {
    public static void main(String[] args) {
        Pizza margerittaPizza = new Margeritta();
        Pizza farmHousePizza = new FarmHouse();
        margerittaPizza = new Cheese(margerittaPizza);
        farmHousePizza = new Cheese(farmHousePizza);
        System.out.println("Chesse Margeritta Pizza Cost " + margerittaPizza.getCost());
        System.out.println("Chesse FarmHouse Pizza Cost " + farmHousePizza.getCost());
                margerittaPizza = new Origano(margerittaPizza);
                farmHousePizza = new Origano(farmHousePizza);

                System.out.println("Chesse Margeritta Pizza Cost " + margerittaPizza.getCost());
        System.out.println("Chesse FarmHouse Pizza Cost " + farmHousePizza.getCost());
        
    }
}

//O/p
// Chesse Margeritta Pizza Cost 250.0
// Chesse FarmHouse Pizza Cost 550.0
// Chesse Margeritta Pizza Cost 260.0
// Chesse FarmHouse Pizza Cost 560.0

// === Code Execution Successful ===
