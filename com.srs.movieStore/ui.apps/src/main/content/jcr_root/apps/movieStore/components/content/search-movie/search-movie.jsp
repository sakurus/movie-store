<%--

  Search Movie component.

  Movie search component

--%><%
%><%@include file="/libs/foundation/global.jsp"%><%
%><%@page session="false" %><%
%>

<style>
body {
	background: #000;
}

.movieName {
	background-color: #212427;
	border: 2px solid #22254b;
	padding: 0.5rem 1rem;
	border-radius: 50px;
	font-size: 1rem;
	color: #fff;
	font-family: inhert;
}

.movieName:focus {
	outline: 0;
	background-color: #22254b;
}

.movieName {
	color: #7378c5;
}

.search {
	font: normal normal normal 14px/1 FontAwesome;
	-webkit-font-smoothing: antialiased;
}

.searchbox {
	border: 1px solid #555;
	padding-left: 10% !important;
	padding-right: 10% !important;
	padding-top: 4% !important;
	width: 100% !important;
	box-shadow: rgba(0, 0, 0, 0.3) 0 0 10px;
	border-collapse: collapse;
	background: rgba(255, 255, 255, 0.25);
	width: 65% !important;
}

.mainbox {
    width: 85% !important;
    padding-left: 10% !important;
    padding-right: 10% !important;
    padding-top: 20% !important;
    padding-bottom: 20% !important;
    background-color: #373b69;
}
</style>

<center>
    <br>
    <br>
    <br>
    <br>
    <br>
    <br>
    <div class="mainbox">
	    <div class="searchbox">
	        <span>
	            <p>Welcome to Movie Store</p>
	            <p>Search for your favourite movies and trailers</p>
	        </span>
	
	    <form id="movie" action="/content/movie-results.html" method="GET">
	    <div>
	        <input type="text" name="title" class="movieName" id="movieName" placeholder="Enter Movie Name" />
	        <button type="submit" id="getMovie"><span>Submit</span></button>
	    </div>
	    </form>
	    </div>
    </div>
</center>
