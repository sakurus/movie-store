$(document).ready(function(){
    const queryString = window.location.search;
    const urlParams = new URLSearchParams(queryString);
    const title = urlParams.get('title');
    const loaderDiv = document.getElementById("movieLoader");

    $.ajax({
    url:"http://localhost:4502/services/gettrailers?title="+title,
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
           const movieD = "<li><img src="+moviePoster+"> <h2>"+movieName+" ("+movieYear+")</h2><span onClick=\"getVideos('"+movieId+"')\">Get movie videos</span></li><div id='"+movieId+"trailers"+"'></div>";
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