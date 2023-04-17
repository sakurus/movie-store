package movieStore.core.service;

import java.io.IOException;

import org.apache.commons.httpclient.HttpException;
import org.apache.sling.api.SlingHttpServletRequest;
import org.json.JSONException;

public interface MovieService {

	String getTrailers(String title, SlingHttpServletRequest request) throws HttpException, IOException, JSONException;

	String getImdb(String title, SlingHttpServletRequest servletRequest) throws HttpException, IOException;
	
	String getVideos(String movie, String year, SlingHttpServletRequest servletRequest) throws HttpException, IOException;
}
