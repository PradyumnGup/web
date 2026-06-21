// ProxyVideoService.java
import java.util.HashMap;
import java.util.Map;

// VideoServiceInterface.java
interface VideoServiceInterface {
    void playVideo(String userType, String videoName);
}
// RealVideoService.java
class RealVideoService implements VideoServiceInterface {
    @Override
    public void playVideo(String userType, String videoName) {
        System.out.println("Streaming video: " + videoName);
    }
}

class ProxyVideoService implements VideoServiceInterface {
  private RealVideoService realVideoService;
  private Map<String, String> cachedVideos = new HashMap<>();
  private Map<String, Integer> requestCounts = new HashMap<>();
  public ProxyVideoService(RealVideoService realVideoService) {
    this.realVideoService = realVideoService;
  }

  @Override
  public void playVideo(String userType, String videoName) {
    // Check user permissions
    if (!userType.equals("premium") && videoName.startsWith("Premium")) {
      System.out.println(
          "Access denied: Premium video requires a premium account.");
      return;
    }

    // Limit requests
    requestCounts.put(userType, requestCounts.getOrDefault(userType, 0) + 1);
    if (requestCounts.get(userType) > 5) {
      System.out.println("Access denied: Too many requests.");
      return;
    }

    // Caching logic
    if (cachedVideos.containsKey(videoName)) {
      System.out.println("Streaming cached video: " + videoName);
    } else {
      realVideoService.playVideo(userType, videoName);
      cachedVideos.put(videoName, videoName);
    }
  }
}

public class Main {
    public static void main(String[] args) {
        RealVideoService realService = new RealVideoService();
        ProxyVideoService proxyService = new ProxyVideoService(realService);
        // Free user trying to watch a video
        proxyService.playVideo("free", "Free Video 1");
        // Premium user trying to watch a video
        proxyService.playVideo("premium", "Premium Video 1");
        // Unauthorized user
        proxyService.playVideo("guest", "Video 1");
        // Too many requests
        for (int i = 0; i < 6; i++) {
            proxyService.playVideo("free", "Free Video 2");
        }
    }
}
