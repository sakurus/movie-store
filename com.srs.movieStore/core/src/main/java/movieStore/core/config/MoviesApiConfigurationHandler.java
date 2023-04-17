package movieStore.core.config;

public interface MoviesApiConfigurationHandler {
	
	String getImdbApiUrl();
	
	String getYoutubeApiUrl();
	
	String getApiKey();
	
	String getImdbApiHost();
	
	String getYoutubeApiHost();
	
	int getNumberOfVideos();

}
