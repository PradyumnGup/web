
import java.time.Instant;
import java.util.*;
import java.time.temporal.ChronoUnit;
import java.util.Random;

enum Size{
    SMALL,
    MEDIUM,
    LARGE
}
class Compartment{
    private int id;
    private Size size;
    private Boolean occupied;
    
     public Compartment(int id,Size size) {
         this.id=id;
        this.size = size;
        this.occupied = false;
    }

    public Size getSize() {
        return size;
    }
    public void markOccupied(){
        this.occupied=true;
    }
    public Boolean isOccupied(){
        return occupied;
    }
    
    public void markFree() {
        this.occupied = false;
    }
    public int getId(){
       return this.id; 
    }
    public void open() {
        System.out.println("Compartment with id "+ getId()+ " is opened");
    }
    
}

class AccessToken{
    final private Compartment compartment;
    final private String code;
    final private Instant expiration;
    
    public AccessToken(String code, Instant expiration, Compartment compartment) {
        this.code = code;
        this.expiration = expiration;
        this.compartment = compartment;
    }
    public Compartment getCompartment() {
        return compartment;
    }

    public String getCode() {
        return code;
    }

    public Boolean isExpired(){
        //basically current system time is > expiration of this access token.
        return !Instant.now().isBefore(expiration);
    }
}

class Locker{
    final private Compartment[] compartments;
    final private Map<String, AccessToken> accessTokenMapping;
    final private Random random;

    public Locker(Compartment[] compartments) {
        this.compartments = compartments;
        this.accessTokenMapping = new HashMap<>();
        this.random = new Random();
    }
    //return accesToken code to the end user
    public String depositPackage(Size size){
        Compartment compartment = getAvailableCompartment(size);
        if (compartment == null) {
            throw new RuntimeException("No available compartment of size" + size);
        }
        
        compartment.open();
        System.out.println("Package deposited");
        compartment.markOccupied();
        AccessToken accessToken = generateAccessToken(compartment);
        accessTokenMapping.put(accessToken.getCode(),accessToken);
        return accessToken.getCode();
    }
    //User retrieves package through access token code provided to them
    //during deposit
    
    public void pickUp(String tokenCode){
        if (tokenCode == null || tokenCode.isEmpty()) {
            throw new RuntimeException("Invalid access token code");
        }
        AccessToken accessToken = accessTokenMapping.get(tokenCode);
        
        if (accessToken == null) {
            throw new RuntimeException("Invalid access token code");
        }
        if(accessToken.isExpired()){
            throw new RuntimeException("Access token has expired");
        }
        
        Compartment compartment = accessToken.getCompartment();
        
        compartment.open();
        System.out.println("Package picked up");
        clearDeposit(accessToken);
    }
    
    private Compartment getAvailableCompartment(Size size){
        for(Compartment compartment:compartments){
            if(compartment.getSize()==size && !compartment.isOccupied()){
                return compartment;
            }
        }
        return null;
    }
    
    private AccessToken generateAccessToken(Compartment compartment){
        String code = String.format("%06d",random.nextInt(1_000_000));
        Instant expiration = Instant.now().plus(7,ChronoUnit.DAYS);
        return new AccessToken(code, expiration, compartment);
    }
    private void clearDeposit(AccessToken accessToken){
        Compartment compartment = accessToken.getCompartment();
        
        compartment.markFree();
        //remove from accesstoken map
        accessTokenMapping.remove(accessToken.getCode());
    }
    public void openExpiredCompartments() {
        for (AccessToken accessToken : accessTokenMapping.values()) {
            if (accessToken.isExpired()) {
                Compartment compartment = accessToken.getCompartment();
                compartment.open();
            }
        }
    }
}

class Main {
    public static void main(String[] args) {
        Compartment[] compartments = new Compartment[15] ;
        for(int i=0;i<5;i++){
            Compartment compartment = new Compartment(i+1,Size.SMALL);
            compartments[i]=compartment;
        }
        for(int i=5;i<10;i++){
            Compartment compartment = new Compartment(i+1,Size.MEDIUM);
            compartments[i]=compartment;
        }
        for(int i=10;i<15;i++){
            Compartment compartment = new Compartment(i+1,Size.LARGE);
            compartments[i]=compartment;
        }
        Locker locker = new Locker(compartments);
        String tokenCode1 = locker.depositPackage(Size.SMALL);
        System.out.println("Token code for is 1 "+tokenCode1);
        String tokenCode2 = locker.depositPackage(Size.MEDIUM);
        System.out.println("Token code for 2 is "+tokenCode2);
        String tokenCode3 = locker.depositPackage(Size.LARGE);
        System.out.println("Token code for 3 is "+tokenCode3);
        locker.pickUp(tokenCode1);
        locker.pickUp(tokenCode2);
        locker.pickUp(tokenCode3);
        
    }
}

//O/p
// Compartment with id 1 is opened
// Package deposited
// Token code for is 1 691009
// Compartment with id 6 is opened
// Package deposited
// Token code for 2 is 940944
// Compartment with id 11 is opened
// Package deposited
// Token code for 3 is 142137
// Compartment with id 1 is opened
// Package picked up
// Compartment with id 6 is opened
// Package picked up
// Compartment with id 11 is opened
// Package picked up

// === Code Execution Successful ===
