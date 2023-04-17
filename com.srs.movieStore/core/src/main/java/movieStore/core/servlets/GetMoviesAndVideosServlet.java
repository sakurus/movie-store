package movieStore.core.servlets;

import java.io.IOException;
import javax.servlet.Servlet;
import org.osgi.service.component.annotations.Reference;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import movieStore.core.service.MovieService;

@Component(service={Servlet.class}, property={"sling.servlet.methods=get", 
		"sling.servlet.paths=/services/gettrailers"})
public class GetMoviesAndVideosServlet extends SlingAllMethodsServlet
{

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(GetMoviesAndVideosServlet.class);
    
    @Reference
    private MovieService movieService;
    
    @Override
    public void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
    	
    	response.setCharacterEncoding("UTF-8");
    	response.setContentType("application/json");
    	String title = request.getParameter("title");
    	
    	/*Mock response
    	 * String sResponse = "{\"movies\":[{\"year\":\"2008\",\"name\":\"Iron Man\",\"videos\":[\"https://www.youtube.com/embed/8ugaeA-nMTc\",\"https://www.youtube.com/embed/7b3NFdsf-Tk\",\"https://www.youtube.com/embed/VPzBhDHmcm8\"],\"id\":\"tt0371746\",\"type\":\"movie\",\"poster\":\"https://m.media-amazon.com/images/M/MV5BMTczNTI2ODUwOF5BMl5BanBnXkFtZTcwMTU0NTIzMw@@._V1_.jpg\"},{\"year\":\"2013\",\"name\":\"Iron Man 3\",\"videos\":[\"https://www.youtube.com/embed/aV8H7kszXqo\",\"https://www.youtube.com/embed/1mKwAYaAZNg\",\"https://www.youtube.com/embed/kEIVPiTuYkQ\"],\"id\":\"tt1300854\",\"type\":\"movie\",\"poster\":\"https://m.media-amazon.com/images/M/MV5BMjE5MzcyNjk1M15BMl5BanBnXkFtZTcwMjQ4MjcxOQ@@._V1_.jpg\"},{\"year\":\"2010\",\"name\":\"Iron Man 2\",\"videos\":[\"https://www.youtube.com/embed/nS8aKzfIyGY\",\"https://www.youtube.com/embed/wKtcmiifycU\",\"https://www.youtube.com/embed/BoohRoVA9WQ\"],\"id\":\"tt1228705\",\"type\":\"movie\",\"poster\":\"https://m.media-amazon.com/images/M/MV5BZGVkNDAyM2EtYzYxYy00ZWUxLTgwMjgtY2VmODE5OTk3N2M5XkEyXkFqcGdeQXVyNTgzMDMzMTg@._V1_.jpg\"}]}";
    	response.getWriter().write(sResponse);*/
    	
    	String movieDetails = "";
    	
    	//Check for response from session 
    	String movieDetailsFromCache ="";
    	Object movieDetailsObjFromCache = request.getSession().getAttribute(title);
    	if (movieDetailsObjFromCache != null) {
    		movieDetailsFromCache = movieDetailsObjFromCache.toString();
    	}
    	if (movieDetailsFromCache.isEmpty()) {
    		try {
				movieDetails = movieService.getTrailers(title, request);
			} catch (Exception je) {
				LOG.error("JSONException whilie fetching movie detals :: "+je);
			}
    		LOG.debug("Data from source API :: "+movieDetails);
    		request.getSession().setAttribute(title, movieDetails);
    	} else {
    		LOG.debug("Data from cache :: "+movieDetails);
    		movieDetails = movieDetailsFromCache;
    	}
    	response.getWriter().write(movieDetails);
    }

}
