<%--

  Movie Details component.

  Movie details component

--%>
<%
%><%@include file="/libs/foundation/global.jsp"%>
<%
%><%@page session="false"%>
<%
%><%@page import="org.apache.sling.api.SlingHttpServletRequest"%>
<%
%><%@page import="java.util.List"%>
<%
%><%@page import="java.util.ArrayList"%>
<%
    String title = (String)request.getParameter("title");
%>

<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<script type="text/javascript">
    $(document).ready(function(){
        const queryString = window.location.search;
        const urlParams = new URLSearchParams(queryString);
        const title = urlParams.get('title');
        const loaderDiv = document.getElementById("movieLoader");

        $.ajax({
        url:"/services/gettrailers?title="+title,
        type:"GET",
        success: function (data) {
            loaderDiv.style.display = "none";
            const moviesList = data.movies;
            moviesList.map((movie) => {
                const movieName = movie.name;
                const moviePoster = movie.poster;
                const movieYear = movie.year;
                const movieType = movie.type;
                const movieId = movie.id;
                const videos = movie.videos;
                let videoD = "";
                for (const video of videos) {
                    videoD = videoD.concat("<li><iframe src=\""+video+"\"></iframe></li>");
                }
                const vd = "<input id='"+movieId+"' value='"+videoD+"' type=\"hidden\">";
               const movieD = "<li><img src="+moviePoster+"> <h2>"+movieName+" ("+movieYear+")</h2><span class='reg-text' onClick=\"getVideos('"+movieId+"')\">Get movie videos</span></li><div id='"+movieId+"trailers"+"'></div>";
                document.getElementById('movies').innerHTML += movieD;
                document.getElementById('videos').innerHTML += vd;
            })
        }, error: function (error) {
            console.log(error);
            loaderDiv.style.display = "none";
            const errorMsg = "<li><h2>An error occoured while fetching movie details, please try again</h2></li>";
            document.getElementById('movies').innerHTML += errorMsg;
        }
     });
    });

    function getVideos(divId) {
        const trailerDiv = divId+"trailers";
        console.log(trailerDiv);
        const videos = document.getElementById(divId).value;
        document.getElementById(trailerDiv).innerHTML += videos;
    }
</script>

<style>
    body {
        background: #000;
    }
    
    .movies {
        width: 100%;
        display: flex;
        flex-wrap: wrap;
    }
    
    .movies li {
        flex: 1;
        display: flex;
        flex-direction: column;
        align-items: center;
        list-style: none;
        margin: 10px;
        padding: 15px;
        border-radius: 12px;
        text-align: center;
        background: #272727;
    }
    
    .movies img {
        max-width: 300px;
        border-radius: 12px;
    }
    
    .movies li h2 {
        color: #fff;
        font-size: 1.8em;
        padding: 15px 10px 0;
        margin-top: auto;
        font-family: "Oswald", sans-serif;
    }
    
    .reg-text {
        color: #fff;
    }
</style>
<c:set var="title" value="<%= title %>" />

<div>
    <div id="movieLoader" class="reg-text">
        Getting details for <span id="movieName">${title}</span> <img
            src="/content/dam/movieStore/loading.gif">
    </div>
    <div id="movies" class="movies"></div>
    <div id="videos"></div>
</div>