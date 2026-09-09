import java.util.*;

// ===============================
// Capability Interfaces
// ===============================

interface AudioCapability {
    void playAudio(String audio);
}

interface ScreenCapability {
    void display(String message);
}

interface BatteryCapability {
    int getBatteryPercentage();
    boolean isCharging();
}


// ===============================
// Audio Implementations
// ===============================

class AudioEnabled implements AudioCapability {

    @Override
    public void playAudio(String audio) {
        System.out.println("Playing audio: " + audio);
    }
}


// ===============================
// Screen Implementations
// ===============================

class ScreenEnabled implements ScreenCapability {

    @Override
    public void display(String message) {
        System.out.println("Displaying on screen: " + message);
    }
}


// ===============================
// Battery Implementations
// ===============================

class BatteryAvailable implements BatteryCapability {

    private int batteryPercentage;
    private boolean charging;

    public BatteryAvailable(int batteryPercentage, boolean charging) {

        if (batteryPercentage < 0 || batteryPercentage > 100) {
            throw new IllegalArgumentException(
                "Battery percentage must be between 0 and 100"
            );
        }

        this.batteryPercentage = batteryPercentage;
        this.charging = charging;
    }

    @Override
    public int getBatteryPercentage() {
        return batteryPercentage;
    }

    @Override
    public boolean isCharging() {
        return charging;
    }

    public void setBatteryPercentage(int batteryPercentage) {
        if (batteryPercentage < 0 || batteryPercentage > 100) {
            throw new IllegalArgumentException(
                "Battery percentage must be between 0 and 100"
            );
        }

        this.batteryPercentage = batteryPercentage;
    }

    public void setCharging(boolean charging) {
        this.charging = charging;
    }
}


// ===============================
// No Battery - Null Object Pattern
// ===============================

class NoBattery implements BatteryCapability {

    @Override
    public int getBatteryPercentage() {
        return 0;
    }

    @Override
    public boolean isCharging() {
        return false;
    }
}


// ===============================
// Alexa Device
// ===============================

class AlexaDevice {

    private String deviceName;

    private AudioCapability audio;
    private ScreenCapability screen;
    private BatteryCapability battery;

    public AlexaDevice(
            String deviceName,
            AudioCapability audio,
            ScreenCapability screen,
            BatteryCapability battery) {

        this.deviceName = deviceName;
        this.audio = audio;
        this.screen = screen;
        this.battery = battery;
    }


    // ===============================
    // Show Battery Status
    // ===============================

    public void show() {

        System.out.println("\nDevice: " + deviceName);

        if (battery instanceof NoBattery) {

            System.out.println("Battery not available");

        } else {

            System.out.println(
                "Battery: " +
                battery.getBatteryPercentage() +
                "%"
            );

            if (battery.isCharging()) {
                System.out.println("Charging");
            } else {
                System.out.println("Not Charging");
            }
        }
    }


    // ===============================
    // Audio Capability
    // ===============================

    public void playAudio(String audioMessage) {

        if (audio == null) {
            System.out.println("Audio capability not available");
            return;
        }

        audio.playAudio(audioMessage);
    }


    // ===============================
    // Screen Capability
    // ===============================

    public void display(String message) {

        if (screen == null) {
            System.out.println("Screen capability not available");
            return;
        }

        screen.display(message);
    }
}


// ===============================
// Factory
// ===============================

class AlexaDeviceFactory {

    public static AlexaDevice createAudioDevice(
            String name,
            boolean hasBattery,
            int batteryPercentage,
            boolean charging) {

        BatteryCapability battery;

        if (hasBattery) {
            battery = new BatteryAvailable(
                batteryPercentage,
                charging
            );
        } else {
            battery = new NoBattery();
        }

        return new AlexaDevice(
            name,
            new AudioEnabled(),
            null,
            battery
        );
    }


    public static AlexaDevice createScreenDevice(
            String name,
            boolean hasBattery,
            int batteryPercentage,
            boolean charging) {

        BatteryCapability battery;

        if (hasBattery) {
            battery = new BatteryAvailable(
                batteryPercentage,
                charging
            );
        } else {
            battery = new NoBattery();
        }

        return new AlexaDevice(
            name,
            null,
            new ScreenEnabled(),
            battery
        );
    }


    public static AlexaDevice createAudioScreenDevice(
            String name,
            boolean hasBattery,
            int batteryPercentage,
            boolean charging) {

        BatteryCapability battery;

        if (hasBattery) {
            battery = new BatteryAvailable(
                batteryPercentage,
                charging
            );
        } else {
            battery = new NoBattery();
        }

        return new AlexaDevice(
            name,
            new AudioEnabled(),
            new ScreenEnabled(),
            battery
        );
    }
}


// ===============================
// Main
// ===============================

public class Main {

    public static void main(String[] args) {

        // ============================================
        // 1. Audio device + Battery + Charging
        // ============================================

        AlexaDevice device1 =
            AlexaDeviceFactory.createAudioDevice(
                "Echo Dot",
                true,
                80,
                true
            );

        device1.show();


        // ============================================
        // 2. Audio device + No Battery
        // ============================================

        AlexaDevice device2 =
            AlexaDeviceFactory.createAudioDevice(
                "Echo",
                false,
                0,
                false
            );

        device2.show();


        // ============================================
        // 3. Screen device + Battery + Not Charging
        // ============================================

        AlexaDevice device3 =
            AlexaDeviceFactory.createScreenDevice(
                "Echo Show",
                true,
                65,
                false
            );

        device3.show();


        // ============================================
        // 4. Audio + Screen + Battery + Charging
        // ============================================

        AlexaDevice device4 =
            AlexaDeviceFactory.createAudioScreenDevice(
                "Echo Show 15",
                true,
                90,
                true
            );

        device4.show();


        // ============================================
        // Testing capabilities
        // ============================================

        device1.playAudio("Hello Alexa");

        device3.display("Welcome to Alexa");

        device4.playAudio("Playing music");

        device4.display("Now Playing");
    }
}
```

// ### Expected output

// ```text
// Device: Echo Dot
// Battery: 80%
// Charging

// Device: Echo
// Battery not available

// Device: Echo Show
// Battery: 65%
// Not Charging

// Device: Echo Show 15
// Battery: 90%
// Charging

// Playing audio: Hello Alexa
// Displaying on screen: Welcome to Alexa
// Playing audio: Playing music
// Displaying on screen: Now Playing
// ```

// ### Why this design is good for the interview

// The key class is:

// ```java
// class AlexaDevice {
//     private AudioCapability audio;
//     private ScreenCapability screen;
//     private BatteryCapability battery;
// }
// ```

// We're **composing capabilities** rather than creating subclasses such as:

// ```text
// AudioAlexa
// BatteryAudioAlexa
// ChargingBatteryAudioAlexa
// ScreenBatteryAlexa
// AudioScreenBatteryAlexa
// AudioScreenChargingBatteryAlexa
// ...
// ```

// That would become a combinatorial mess.

// The design uses:

// * **Interface Segregation Principle** → `AudioCapability`, `ScreenCapability`, `BatteryCapability`
* **Composition over inheritance** → `AlexaDevice` contains capabilities
* **Strategy-like design** → battery behavior is supplied through `BatteryCapability`
* **Null Object Pattern** → `NoBattery` represents absence of a battery without null handling
* **Factory Pattern** → `AlexaDeviceFactory` creates the appropriate combinations

### One improvement I'd make in an actual interview

I would **not use**:

```java
if (battery instanceof NoBattery)
```

because the interviewer may challenge it.

A cleaner design is to put the display behavior directly inside the battery interface:

```java
interface BatteryCapability {
    String getStatus();
}
```

Then:

```java
class BatteryAvailable implements BatteryCapability {

    private int percentage;
    private boolean charging;

    @Override
    public String getStatus() {
        if (charging) {
            return "Charging and Battery: " + percentage + "%";
        }

        return "Not Charging and Battery: " + percentage + "%";
    }
}
```

and:

```java
class NoBattery implements BatteryCapability {

    @Override
    public String getStatus() {
        return "Battery not available";
    }
}
```

Then `show()` becomes simply:

```java
public void show() {
    System.out.println(battery.getStatus());
}
```

**This second version is what I would actually present in an interview** because it demonstrates polymorphism better and avoids `instanceof`/conditional logic.
