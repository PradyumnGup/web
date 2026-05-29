// Online Java Compiler
// Use this editor to write, compile and run your Java code online
class Logger{
    private static volatile Logger logger;
    private Logger(){} //private to prevent external instantiation
    public static Logger getLogger(){
        
        if(logger==null){
            synchronized(Logger.class){
                if(logger==null){
                    logger=new Logger();
                }
            }
        }
        
        return logger;
    }
    public void log(String msg){
        System.out.print("Log Msg"+ msg);
    }
}
class Main {
    public static void main(String[] args) {
        Logger logger= Logger.getLogger();
        logger.log("Hi");
    }
}
