package movieStore.core.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.tika.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import movieStore.core.config.MoviesApiConfigurationHandler;
import movieStore.core.service.MovieService;

@Component(label = "Movie Store Service Implementation", name = "Move Store Service", description = "Movie store service to fetch the details", 
	immediate = true, metatype = false)
@Service(MovieService.class)


public class MovieServiceImpl implements MovieService {

	private static final Logger LOG = LoggerFactory.getLogger(MovieServiceImpl.class);
	
	@Reference
	private MoviesApiConfigurationHandler moviesApi;
	
	@Activate
	protected void activate(final ComponentContext ctx) {
		try {
			LOG.info("MovieServiceImpl :: Activate");
		} catch (final Exception exception) {
			LOG.error("Exception in Activate method in CartServiceImpl :", exception);
		}
	}
	
	@Override
	public String getTrailers(String title, SlingHttpServletRequest request)
			throws HttpException, IOException, JSONException {
		String sResponse = "";
    	JSONObject resultJson = new JSONObject();
    	JSONArray movArray = new JSONArray();

    	//Get movie details
    	String movieDetails = getImdb(title, request);

    	JSONObject moviesJson = new JSONObject(movieDetails);
		JSONArray moviesList = (JSONArray) moviesJson.get("d");
		LOG.debug("Movies List :: "+moviesList.toString());

		//Looping through movies list
		for (int i = 0; i < moviesList.length(); i++) {
			JSONObject movie = moviesList.getJSONObject(i);
			if (movie.has("qid")) {
				String type = movie.getString("qid");
				//Filtering only movies details from list
				if (!type.isEmpty() && type.equalsIgnoreCase("movie")) {
					JSONObject movJson = new JSONObject();
					JSONObject moviePoster = movie.getJSONObject("i");
					int year = movie.getInt("y");
					String movieYear = Integer.toString(year);
					String movieName = movie.getString("l");
					
					movJson.put("name", movieName);
					movJson.put("id", movie.getString("id"));
					movJson.put("year", movieYear);
					movJson.put("type", type);
					movJson.put("poster", moviePoster.getString("imageUrl"));

					//Get videos corresponding to movie 
					String videos = getVideos(movieName, movieYear, request);
					
					JSONObject videosJson = new JSONObject(videos);
					JSONArray vidList = (JSONArray) videosJson.get("items");
					LOG.debug("Videos list :: "+vidList.toString());
					
					JSONArray vidArray = new JSONArray();
					JSONObject vidObject = new JSONObject();
					ArrayList<Integer> viewsArr = new ArrayList<Integer>();
					//Looping through videos 
					for (int j = 0; j < vidList.length(); j++) {
						JSONObject video = vidList.getJSONObject(j);
						if (video.has("id")) {
							String videoId = video.getString("id");
							String videoType = video.getString("type");
							String videoTitle = video.getString("title");
							//Filtering only for videos and title with movie year to get accurate content
							if (!videoType.isEmpty() && videoType.equalsIgnoreCase("video") && !videoId.isEmpty() 
									&& videoTitle.contains(movieYear)) {
								int views = video.getInt("views");
								viewsArr.add(views);
								String vidUrl = "https://www.youtube.com/embed/"+videoId;
								vidObject.put(Integer.toString(views), vidUrl);
							}
						}
					}
					//Sorting views to filter for top viewed content
					viewsArr.sort(null);
					Collections.reverse(viewsArr);
					//Looping to get top videos , value can be configured from OSGI : default value is 3
					int noOfVideos = moviesApi.getNumberOfVideos();
					for (int k=0; k < noOfVideos; k++) {
						String view = viewsArr.get(k).toString();
						String url = vidObject.getString(view);
						vidArray.put(url);
					}
					
					movJson.put("videos", vidArray);
					LOG.debug("Movie object for "+movieName+" :: "+movJson.toString());
					movArray.put(movJson);
				}
			}
		}
		resultJson.put("movies", movArray);
		
		sResponse = resultJson.toString();
		LOG.debug("Processed movie response :: "+sResponse);
		return sResponse;
	}

	@Override
	public String getImdb(String title, SlingHttpServletRequest servletRequest) throws HttpException, IOException {
		String sResponse = "";
    	
		//Mock response
		/*sResponse = "{\"d\":[{\"i\":{\"height\":2048,\"imageUrl\":\"https://m.media-amazon.com/images/M/MV5BMTczNTI2ODUwOF5BMl5BanBnXkFtZTcwMTU0NTIzMw@@._V1_.jpg\",\"width\":1382},\"id\":\"tt0371746\",\"l\":\"Iron Man\",\"q\":\"feature\",\"qid\":\"movie\",\"rank\":918,\"s\":\"Robert Downey Jr., Gwyneth Paltrow\",\"y\":2008},{\"id\":\"nm10946073\",\"l\":\"Ironman\",\"rank\":226450},{\"i\":{\"height\":1100,\"imageUrl\":\"https://m.media-amazon.com/images/M/MV5BMjE5MzcyNjk1M15BMl5BanBnXkFtZTcwMjQ4MjcxOQ@@._V1_.jpg\",\"width\":770},\"id\":\"tt1300854\",\"l\":\"Iron Man 3\",\"q\":\"feature\",\"qid\":\"movie\",\"rank\":2090,\"s\":\"Robert Downey Jr., Guy Pearce\",\"y\":2013},{\"i\":{\"height\":1768,\"imageUrl\":\"https://m.media-amazon.com/images/M/MV5BZGVkNDAyM2EtYzYxYy00ZWUxLTgwMjgtY2VmODE5OTk3N2M5XkEyXkFqcGdeQXVyNTgzMDMzMTg@._V1_.jpg\",\"width\":1200},\"id\":\"tt1228705\",\"l\":\"Iron Man 2\",\"q\":\"feature\",\"qid\":\"movie\",\"rank\":2561,\"s\":\"Robert Downey Jr., Mickey Rourke\",\"y\":2010},{\"i\":{\"height\":1882,\"imageUrl\":\"https://m.media-amazon.com/images/M/MV5BNDJjMDI0YzQtOWM2OC00NmJhLTk3YWMtYmY5NDBkZmVlM2NjXkEyXkFqcGdeQXVyODc0OTEyNDU@._V1_.jpg\",\"width\":1280},\"id\":\"tt0115218\",\"l\":\"Iron Man\",\"q\":\"TV series\",\"qid\":\"tvSeries\",\"rank\":29002,\"s\":\"Robert Hays, John Reilly\",\"y\":1994,\"yr\":\"1994-1996\"},{\"i\":{\"height\":2048,\"imageUrl\":\"https://m.media-amazon.com/images/M/MV5BZTQ2OGNiNTMtMzQ1Zi00MzVhLTk2NjYtNjdmMmYxZjM0YzEyXkEyXkFqcGdeQXVyMjQwMDg0Ng@@._V1_.jpg\",\"width\":1536},\"id\":\"nm11370151\",\"l\":\"Mexican IronMan\",\"rank\":526344,\"s\":\"Actor, Pop Culture Breakdown with Overlord DVD (2021-2022)\"},{\"i\":{\"height\":400,\"imageUrl\":\"https://m.media-amazon.com/images/M/MV5BNzE1Nzg2NjgxNV5BMl5BanBnXkFtZTYwMzg4MDQ1._V1_.jpg\",\"width\":266},\"id\":\"nm0315932\",\"l\":\"Ghostface Killah\",\"rank\":52130,\"s\":\"Soundtrack, Iron Man (2008)\"},{\"i\":{\"height\":1500,\"imageUrl\":\"https://m.media-amazon.com/images/M/MV5BMDFjZmEyZTAtMGRmOC00M2FlLTkyNTEtMjE1YzM3YTlhOTUwXkEyXkFqcGdeQXVyMjY4MzQzNDk@._V1_.jpg\",\"width\":1057},\"id\":\"tt1707807\",\"l\":\"Iron Man\",\"q\":\"TV series\",\"qid\":\"tvSeries\",\"rank\":48798,\"s\":\"Adrian Pasdar, Natalia Rosminati\",\"y\":2010,\"yr\":\"2010-2010\"}],\"q\":\"ironman\",\"v\":1}";
    	return sResponse;*/
    	
		//Checking for cached response
    	String cacheKey = title+"Imdb";
    	String movieDetailsFromCache ="";
    	Object movieDetailsObjFromCache = servletRequest.getSession().getAttribute(cacheKey);
    	if (movieDetailsObjFromCache != null) {
    		movieDetailsFromCache = movieDetailsObjFromCache.toString();
    	}
    	
    	if (movieDetailsFromCache.isEmpty()) {
    		//Making the API call
	    	String titleParam = URLEncoder.encode(title, "utf-8");
	    	String imdbApi = moviesApi.getImdbApiUrl(); //"https://online-movie-database.p.rapidapi.com/auto-complete";
	    	String apiHeader = "X-RapidAPI-Key";
	    	String hostHeader = "X-RapidAPI-Host";
	    	String apiKey = moviesApi.getApiKey();
	    	String apiHost = moviesApi.getImdbApiHost(); //"online-movie-database.p.rapidapi.com";
	    	
	    	StringBuilder imdbApiUrl = new StringBuilder();
	    	imdbApiUrl.append(imdbApi).append("?q=").append(titleParam);
	    	
	    	HttpMethod request = null;
	    	request = new GetMethod(imdbApiUrl.toString());
	    	request.addRequestHeader(apiHeader, apiKey);
	    	request.addRequestHeader(hostHeader, apiHost);
	    	
	    	HttpClient httpClient = new HttpClient();
	    	int status = httpClient.executeMethod(request);
	    	LOG.debug("Imdb response status "+status);
	    	InputStream inputStreamResponse = request.getResponseBodyAsStream();
	    	sResponse = IOUtils.toString(inputStreamResponse, "UTF-8");
	    	if (status == 200) {
	    		servletRequest.getSession().setAttribute(cacheKey, sResponse);
	    	}
	    	LOG.debug("Imdb API response :: "+sResponse);
    	} else {
    		sResponse = movieDetailsFromCache;
    		LOG.debug("Imdb Cache response :: "+sResponse);
    	}
    	
    	return sResponse;
	}

	@Override
	public String getVideos(String movie, String year, SlingHttpServletRequest servletRequest)
			throws HttpException, IOException {
		String sResponse = "";
    	
		//Checking for cached response
    	String cacheKey = movie+"Video";
    	String videoDetailsFromCache ="";
    	Object videoDetailsObjFromCache = servletRequest.getSession().getAttribute(cacheKey);
    	if (videoDetailsObjFromCache != null) {
    		videoDetailsFromCache = videoDetailsObjFromCache.toString();
    	}
    	
    	if (videoDetailsFromCache.isEmpty()) {
    		//Making the API call
    		
    		//Appending movie name with year and trailer to get accurate results
	    	String movieParam = URLEncoder.encode(movie+" "+year+" Trailer", "utf-8");
	    	String videoApi = moviesApi.getYoutubeApiUrl(); //"https://youtube-search-results.p.rapidapi.com/youtube-search";
	    	String apiHeader = "X-RapidAPI-Key";
	    	String hostHeader = "X-RapidAPI-Host";
	    	String apiKey = moviesApi.getApiKey();
	    	String apiHost = moviesApi.getYoutubeApiHost(); //"youtube-search-results.p.rapidapi.com";
	    	
	    	StringBuilder videoApiUrl = new StringBuilder();
	    	videoApiUrl.append(videoApi).append("?q=").append(movieParam);
	    	
	    	HttpMethod request = null;
	    	request = new GetMethod(videoApiUrl.toString());
	    	request.addRequestHeader(apiHeader, apiKey);
	    	request.addRequestHeader(hostHeader, apiHost);
	    	
	    	HttpClient httpClient = new HttpClient();
	    	int status = httpClient.executeMethod(request);
	    	LOG.debug("Videos response status :: "+status);
	    	InputStream inputStreamResponse = request.getResponseBodyAsStream();
	    	sResponse = IOUtils.toString(inputStreamResponse, "UTF-8");
	    	if (status == 200) {
	    		servletRequest.getSession().setAttribute(cacheKey, sResponse);
	    	}
	    	LOG.debug("Youtube API response :: "+sResponse);
    	} else {
    		sResponse = videoDetailsFromCache;
    		LOG.debug("Youtube Cache response :: "+sResponse);
    	}
    	
    	return sResponse;
	}
}
