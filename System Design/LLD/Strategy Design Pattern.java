interface PaymentStrategy {
    void processPayment();
}

class CreditCardPayment implements PaymentStrategy {
    public void processPayment() {
        System.out.println("Processing credit card payment...");
    }
}

class PayPalPayment implements PaymentStrategy {
    public void processPayment() {
        System.out.println("Processing PayPal payment...");
    }
}

class CryptoPayment implements PaymentStrategy {
    public void processPayment() {
        System.out.println("Processing crypto payment...");
    }
}

class StripePayment implements PaymentStrategy {
    public void processPayment() {
        System.out.println("Processing Stripe payment...");
    }
}

class PaymentProcessor {
    private PaymentStrategy paymentStrategy;

    public PaymentProcessor(PaymentStrategy paymentStrategy) {
        this.paymentStrategy = paymentStrategy;
    }

    public void processPayment() {
        paymentStrategy.processPayment();
    }

    public void setPaymentStrategy(PaymentStrategy paymentStrategy) {
        this.paymentStrategy = paymentStrategy;
    }
}

public class Main {
    public static void main(String[] args) {
        PaymentProcessor processor =
                new PaymentProcessor(new CreditCardPayment());

        processor.processPayment();

        processor.setPaymentStrategy(new PayPalPayment());
        processor.processPayment();

        processor.setPaymentStrategy(new CryptoPayment());
        processor.processPayment();

        processor.setPaymentStrategy(new StripePayment());
        processor.processPayment();
    }
}
