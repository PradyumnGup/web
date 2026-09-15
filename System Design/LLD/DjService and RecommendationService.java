import java.util.*;



class Song{
    private final  String id;
    private final String title;
    private final String artist;
    private final String genre;
    private final String language;
    private final boolean explicit;
    
    public Song(
            String id,
            String title,
            String artist,
            String genre,
            String language,
            boolean explicit) {

        this.id = id;
        this.title = title;
        this.artist = artist;
        this.genre = genre;
        this.language = language;
        this.explicit = explicit;
    }
    
    public String getId() {
        return id;
    }

    public String getGenre() {
        return genre;
    }

    public String getLanguage() {
        return language;
    }

    public boolean isExplicit() {
        return explicit;
    }
    public String getArtist(){
        return artist;
    }
    public String getTitle(){
        return title;
    }
}

class Playlist{
    private final List<Song>songs;
    public Playlist(List<Song>songs){
        this.songs=List.copyOf(songs);
    }
    List<Song> getSongs(){
        return songs;
    }
}

interface SongProvider{
    List<Song>getSongs(String userId);
}

class DJSongProvider implements SongProvider {

    // private final DJService djService;

    // public DJSongProvider(DJService djService) {
    //     this.djService = djService;
    // }
    @Override
    public List<Song>getSongs(String userId){
        // return djService.getSongs(userId);
        return List.of( new Song( "DJ1", "Rock Song 1", "Artist A", "Rock", "English", false ), new Song( "DJ2", "Pop Song 1", "Artist B", "Pop", "English", false ), new Song( "DJ3", "Jazz Song", "Artist C", "Jazz", "English", false ), new Song( "DJ4", "Hindi Pop", "Artist D", "Pop", "Hindi", false ), new Song( "DJ5", "Rock Song 2", "Artist E", "Rock", "English", true ) );
    }
}

class RecommendationSongProvider implements SongProvider {

    // private final RecommendationService recommendationService;

    // public RecommendationSongProvider(
    //         RecommendationService recommendationService) {

    //     this.recommendationService = recommendationService;
    // }

    @Override
    public List<Song> getSongs(String userId) {
        // return recommendationService.getSongs(userId);
        return List.of( new Song( "R1", "Recommended Rock", "Artist F", "Rock", "English", false ), new Song( "R2", "Recommended Pop", "Artist G", "Pop", "Hindi", false ), new Song( "R3", "Recommended Jazz", "Artist H", "Jazz", "English", false ), new Song( "R4", "Recommended Rock 2", "Artist I", "Rock", "English", false ), new Song( "R5", "Recommended Pop 2", "Artist J", "Pop", "English", false ) );
    }
}

interface DJService {

    List<Song> getSongs(String userId);
}

interface RecommendationService {

    List<Song> getSongs(String userId);
}

class MixRatio {

    private final int djPercentage;
    private final int recommendationPercentage;

    public MixRatio(
            int djPercentage,
            int recommendationPercentage) {

        if (djPercentage < 0 ||
            recommendationPercentage < 0 ||
            djPercentage + recommendationPercentage != 100) {

            throw new IllegalArgumentException(
                    "Invalid mix ratio");
        }

        this.djPercentage = djPercentage;
        this.recommendationPercentage = recommendationPercentage;
    }

    public int getDjPercentage() {
        return djPercentage;
    }

    public int getRecommendationPercentage() {
        return recommendationPercentage;
    }
}



interface SongMixer {

    List<Song> mix(
            List<Song> djSongs,
            List<Song> recommendedSongs,
            MixRatio ratio,
            int playlistSize);
}

class ProportionalSongMixer implements SongMixer {

    @Override
    public List<Song> mix(
            List<Song> djSongs,
            List<Song> recommendedSongs,
            MixRatio ratio,
            int playlistSize) {
        int djCount =
                playlistSize * ratio.getDjPercentage() / 100;
        int recommendationCount = playlistSize-djCount;
        List<Song> result = new ArrayList<>();
        addSongs(result,djSongs,djCount);
        addSongs(result,recommendedSongs,recommendationCount);
        return result;
    }

    private void addSongs(
            List<Song> result,
            List<Song> songs,
            int count) {
                    for (int i = 0;
                 i < count && i < songs.size();
                 i++) {
                        result.add(songs.get(i));
                 }
            }
}

class UserPreferences{
    private final Set<String> preferredGenres;
    private final Set<String> preferredLanguages;
    private final boolean allowExplicit;

    public UserPreferences(
            Set<String> preferredGenres,
            Set<String> preferredLanguages,
            boolean allowExplicit) {

        this.preferredGenres = preferredGenres;
        this.preferredLanguages = preferredLanguages;
        this.allowExplicit = allowExplicit;
    }

    public Set<String> getPreferredGenres() {
        return preferredGenres;
    }

    public Set<String> getPreferredLanguages() {
        return preferredLanguages;
    }

    public boolean isAllowExplicit() {
        return allowExplicit;
    }
}

interface SongFilter{
    List<Song> filter(List<Song>songs,UserPreferences preferences);
}

class GenreFilter implements SongFilter {

    @Override
    public List<Song> filter(
            List<Song> songs,
            UserPreferences preferences) {
        Set<String> genres =
                preferences.getPreferredGenres();
        if (genres == null || genres.isEmpty()) {
            return songs;
        }
        return songs.stream()
                .filter(song->genres.contains(song.getGenre()))
                .toList();
    }
}

class LanguageFilter implements SongFilter {

    @Override
    public List<Song> filter(
            List<Song> songs,
            UserPreferences preferences) {

        Set<String> languages =
                preferences.getPreferredLanguages();

        if (languages == null || languages.isEmpty()) {
            return songs;
        }

        return songs.stream()
                .filter(song ->
                        languages.contains(song.getLanguage()))
                .toList();
    }
}

class ExplicitContentFilter implements SongFilter {

    @Override
    public List<Song> filter(
            List<Song> songs,
            UserPreferences preferences) {

        if (preferences.isAllowExplicit()) {
            return songs;
        }

        return songs.stream()
                .filter(song -> !song.isExplicit())
                .toList();
    }
}

class PlaylistService{
    private final SongProvider djProvider;
    private final SongProvider recommendationProvider;
    private final SongMixer songMixer;
    private final List<SongFilter> filters;
    
    public PlaylistService(
        SongProvider djProvider,
        SongProvider recommendationProvider,
        SongMixer songMixer,
        List<SongFilter> filters) {
    
        this.djProvider = djProvider;
        this.recommendationProvider = recommendationProvider;
        this.songMixer = songMixer;
        this.filters = List.copyOf(filters);
    }

    public Playlist createPlaylist(
            String userId,
            UserPreferences preferences,
            MixRatio ratio,
            int playlistSize) {
                List<Song> djSongs = djProvider.getSongs(userId);
                List<Song> recommendedSongs =
                    recommendationProvider.getSongs(userId);
                List<Song> mixedSongs = songMixer.mix(
                        djSongs,
                        recommendedSongs,
                        ratio,
                        playlistSize);
                List<Song> filteredSongs = mixedSongs;
                for (SongFilter filter : filters) {
                    filteredSongs =
                            filter.filter(
                                    filteredSongs,
                                    preferences);
                }
                return new Playlist(filteredSongs);
            }
}

class Main {
    public static void main(String[] args) {
        // 1. Configure filters 
        List<SongFilter> filters = List.of( new GenreFilter(), new LanguageFilter(), new ExplicitContentFilter() ); 
        // 2. Configure song providers
        SongProvider djProvider = new DJSongProvider();
        SongProvider recommendationProvider = new RecommendationSongProvider(); 
        // 3. Create playlist service 
        PlaylistService playlistService = new PlaylistService( djProvider, recommendationProvider, new ProportionalSongMixer(), filters ); // 4. Configure mixing ratio
        MixRatio ratio = new MixRatio(60, 40); 
        // 5. Configure user preferences
        Set<String> preferredGenres = Set.of( "Rock", "Pop" ); Set<String> preferredLanguages = Set.of( "English", "Hindi" ); UserPreferences preferences = new UserPreferences( preferredGenres, preferredLanguages, true ); 
        // 6. Create playlist 
        Playlist playlist = playlistService.createPlaylist( "user123", preferences, ratio, 20 ); 
        // 7. Print playlist 
        System.out.println("Generated Playlist:"); 
        for (Song song : playlist.getSongs()) 
        { 
            System.out.println( song.getTitle() + " - " + 
                                song.getArtist() + " [" + 
                                song.getGenre() + ", " + 
                                song.getLanguage() + "]" ); 
        }
        
    }
}
