package movieStore.core.config;

import java.util.Dictionary;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(label = "Movies Api Configuration Handler", description = "API configurations", enabled = true, immediate = true, metatype = true)
@Service(MoviesApiConfigurationHandler.class)
public class MoviesApiConfigurationHandlerImpl implements MoviesApiConfigurationHandler {

	private static final Logger LOG = LoggerFactory.getLogger(MoviesApiConfigurationHandlerImpl.class);
	
	private String imdbApiUrl;
	
	@Property(name = "imdb.url", value = "", label = "IMDB Api URL", description = "IMDB API URL to fetch movies data")
	public static final String IMDB_API_URL = "imdb.url";
	
	private String youtubeApiUrl;
	
	@Property(name = "youtube.url", value = "", label = "YouTube Api URL", description = "YouTube API URL to fetch videos")
	public static final String YOUTUBE_API_URL = "youtube.url";
	
	private String apiKey;
	
	@Property(name = "apiKey", value = "", label = "Api Key", description = "API Key for validation")
	public static final String API_KEY = "apiKey";
	
	private String imdbApiHost;
	
	@Property(name = "imdbApiHost", value = "", label = "IMDB Api Host", description = "IMDB API Host details")
	public static final String IMDB_API_HOST = "imdbApiHost";
	
	private String youtubeApiHost;
	
	@Property(name = "youtubeApiHost", value = "", label = "YouTube Api Host", description = "YouTube API Host details")
	public static final String YOUTUBE_API_HOST = "youtubeApiHost";
	
	private Integer noOfVideos;
	
	@Property(name = "noOfVideos", intValue = 3, label = "Number of videos to display on page", description = "Number of videos to be displayed on page per movie")
	public static final String VIDEOS_COUNT = "noOfVideos";
	
	@Activate
	protected void activate(ComponentContext ctx) {
		try {
			final Dictionary<?, ?> properties = ctx.getProperties();

	        imdbApiUrl = PropertiesUtil.toString(properties.get(IMDB_API_URL), "");
	        youtubeApiUrl = PropertiesUtil.toString(properties.get(YOUTUBE_API_URL), "");
	        apiKey = PropertiesUtil.toString(properties.get(API_KEY), "");
	        imdbApiHost = PropertiesUtil.toString(properties.get(IMDB_API_HOST), "");
	        youtubeApiHost = PropertiesUtil.toString(properties.get(YOUTUBE_API_HOST), "");
	        noOfVideos = PropertiesUtil.toInteger(properties.get(VIDEOS_COUNT), 3);
		} catch (final Exception exception) {
			LOG.error("Exception in Activate method of MoviesApiConfigurationHandlerImpl OsgiService: ", exception);
		}
	}
	
	@Override
	public String getImdbApiUrl() {
		return imdbApiUrl;
	}

	@Override
	public String getYoutubeApiUrl() {
		return youtubeApiUrl;
	}

	@Override
	public String getApiKey() {
		return apiKey;
	}

	@Override
	public String getImdbApiHost() {
		return imdbApiHost;
	}
	
	@Override
	public String getYoutubeApiHost() {
		return youtubeApiHost;
	}

	@Override
	public int getNumberOfVideos() {
		return noOfVideos;
	}

}
