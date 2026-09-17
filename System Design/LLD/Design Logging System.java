import java.io.FileWriter;
import java.io.IOException;

enum LogLevel {
    DEBUG(1),   // Debug-level messages (least severe)
    INFO(2),    // Informational messages
    ERROR(3)  ; // Error messages indicating failures

    // Numeric value associated with each log level
    private final int value;
    LogLevel(int value) { 
        this.value = value;
    }

    // Getter method to retrieve the numeric value of a log level
    public int getValue() {
        return value;
    }

    // Method to compare log levels based on severity
    public boolean isGreaterOrEqual(LogLevel other) {
        return this.value >= other.value;
    }
}

class LogMessage{
    // Log level of the message (e.g., INFO, DEBUG, ERROR)
    private final LogLevel level;
    // The actual log message content
    private final String message;
    // Timestamp when the log message was created
    private final long timestamp;
    // Constructor to initialize log level and message, setting the timestamp to current time
     // Constructor to initialize log level and message, setting the timestamp to current time
  public LogMessage(LogLevel level, String message) {
    this.level = level;
    this.message = message;
    this.timestamp = System.currentTimeMillis();
  }

  // Returns the log level of the message
  public LogLevel getLevel() {
    return level;
  }

  // Returns the log message content
  public String getMessage() {
    return message;
  }

  // Returns the timestamp of the log creation
  public long getTimestamp() {
    return timestamp;
  }

  // Formats the log message as a string with level, timestamp, and message
  @Override
  public String toString() {
    return "[" + level + "] " + timestamp + " - " + message;
  }
}

// Abstract logger defining the chain behavior
abstract class LogHandler {
      public static final int INFO = 1;
      public static final int DEBUG = 2;
      public static final int ERROR = 3;
      protected int level;
      protected LogHandler nextLogger;
      protected LogAppender appender; // the appender where we need to append the logs
    // Constructor to initialize with appender
  public LogHandler(int level, LogAppender appender) {
    this.level = level;
    this.appender = appender;
  }
    // Set the next logger in the chain
  public void setNextLogger(LogHandler nextLogger) {
    this.nextLogger = nextLogger;
  }
    // Corrected to use LogLevel instead of int for consistency
  public void logMessage(int level, String message) {
      if(this.level>=level){
          LogLevel logLevel = intToLogLevel(level);
          LogMessage logMsg = new LogMessage(logLevel, message);
          // Use the appender to log
          if (appender != null){
              appender.append(logMsg);
              write(message);
          }
      }
      else if(nextLogger != null){
          nextLogger.logMessage(level, message);
      }
          
  }
    // Helper method to convert int level to LogLevel enum
  private LogLevel intToLogLevel(int level) {
    switch (level) {
      case INFO:
        return LogLevel.INFO;
      case DEBUG:
        return LogLevel.DEBUG;
      case ERROR:
        return LogLevel.ERROR;
      default:
        return LogLevel.INFO;
    }
  }
    // Each concrete logger will implement its own writing mechanism
    abstract protected void write(String message);
}

// Logger for INFO level messages
class InfoLogger extends LogHandler {

        public InfoLogger(int level, LogAppender appender) {
            super(level, appender);
        }
    
    @Override
	    protected void write(String message) {
	        System.out.println("DEBUG: " + message);
	    }
}

// Logger for DEBUG level messages
class DebugLogger  extends LogHandler {
    public DebugLogger(int level, LogAppender appender) {
            super(level, appender);
    }
    @Override
	    protected void write(String message) {
	        System.out.println("DEBUG: " + message);
	    }
}

// Logger for ERROR level messages
class ErrorLogger  extends LogHandler {
    public ErrorLogger(int level, LogAppender appender) {
            super(level, appender);
    }
    
    @Override
	    protected void write(String message) {
	        System.out.println("ERROR: " + message);
	    }
}

interface LogAppender{
    void append(LogMessage logMessage);
}

class ConsoleAppender implements LogAppender{
     // Appends a log message to the console
	    @Override
	    public void append(LogMessage logMessage) {
	        System.out.println(logMessage); // Print log to console
	    }
}

class FileAppender implements LogAppender{
        private final String filePath; // Path to the log file

      // Constructor to set the file path
      public FileAppender(String filePath) {
        this.filePath = filePath;
      }
     // Appends a log message to the console
	    @Override
	    public void append(LogMessage logMessage) {
	        try (FileWriter writer = new FileWriter(filePath, true)) {
              writer.write(logMessage.toString() + " ");
                // Write log to file
            } catch (IOException e) {
              e.printStackTrace(); // Print error if file writing fails
            }
	    }
}

class Main {
    public static LogHandler getChainOfLoggers(LogAppender logAppender){
        LogHandler errorLogger = new ErrorLogger(LogHandler.ERROR, logAppender);
        LogHandler debugLogger = new DebugLogger(LogHandler.DEBUG, logAppender);
        LogHandler infoLogger = new InfoLogger(LogHandler.INFO, logAppender);
        infoLogger.setNextLogger(debugLogger);
        debugLogger.setNextLogger(errorLogger);
        return infoLogger;
    }
    public static void main(String[] args) {
        // Select the log appender (console or file)
        LogAppender consoleAppender = new ConsoleAppender();
        // Create the chain of loggers with the console appender
        LogHandler loggerChain = getChainOfLoggers(consoleAppender);
        // Use a single logging approach to avoid duplication
        System.out.println("Logging INFO level message:");
        loggerChain.logMessage(LogHandler.INFO, "This is an information.");
        System.out.println("Logging DEBUG level message:");
        loggerChain.logMessage(LogHandler.DEBUG, "This is a debug level information.");
        System.out.println("Logging ERROR level message:");
        loggerChain.logMessage(LogHandler.ERROR, "This is an error information.");
    }
}

//O/P
// Logging INFO level message:
// [INFO] 1789662172098 - This is an information.
// DEBUG: This is an information.
// Logging DEBUG level message:
// [DEBUG] 1789662172196 - This is a debug level information.
// DEBUG: This is a debug level information.
// Logging ERROR level message:
// [ERROR] 1789662172196 - This is an error information.
// ERROR: This is an error information.

// === Code Execution Successful ===
