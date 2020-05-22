import React, { Component } from "react";
import movies from "../services/movie";

import "../css/thumbnail.css"

import SearchBar from "./SearchBar";

function renderThumb(title, thumbm, movie_id) {
  var img_poster = "http://image.tmdb.org/t/p/w500" + thumb;
  return '<div class="responsive"><div class="gallery"><a href=/details/' + movie_id + '><img src="'
   + img_poster + '" alt="' + title + '" width="400" height="600"></a><div class="desc">' +
  title + '</div></div></div>';
}

class Search extends Component {
  state = {
    movies: "",
    movie_thumb: "",
    is_get_movie: false
};

  getPopularMovie = () => {
    movies.search({orderby: "rating", direction: "DESC"})
    .then(response => {
      console.log(response['data']['movies'][0]['movie_id']);
      var res_movie = response['data']['movies'];
      var list_movie = [];
      for (var i = 0; i < res_movie.length; i++){
        list_movie.push(res_movie[i]['movie_id']);
      }
      this.setState({movies: list_movie});
      this.setState({is_get_movie: true});
    })
    .catch(error => console.log(error));

    if(this.state.movies.length !== 0){
      movies.thumbnail(this.state.movies)
      .then(response => {
        console.log(response);
        this.setState({movie_thumb: response['data']['thumbnails']})
      })
      .catch(error => console.log(error));

    }
  }

  renderPopularMovies = () => {
    var movies = this.state.movie_thumb;
    var thumb_list = '';
    for (var i = 0; i < movies.length; i++)
    {
      if( movies[0] !== undefined){
        thumb_list += renderThumb(movies[i]['title'], movies[i]['poster_path'], movies[i]['movie_id']);
      }
    }
    return thumb_list;
  }

  render() {
    if (this.state.is_get_movie === false) {
      this.getPopularMovie();}

    return (
      <div>
        <SearchBar />
        <h1 className ='label'>Movies</h1>
        <h2> Top Rating</h2>
          <div class="gallery" dangerouslySetInnerHTML={{ __html: this.renderPopularMovies()}}  />
      </div>
    );
  }
}

export default Search;
