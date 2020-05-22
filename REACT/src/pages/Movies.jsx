import React, { Component } from "react";
import { Route } from "react-router-dom";
import movies from "../services/movie";

import "../css/thumbnail.css"

import SearchBar from "./SearchBar";

function renderThumb(title, thumb, movie_id) {
  var img_poster = "https://image.tmdb.org/t/p/w300_and_h450_bestv2" + thumb;
  return '<div class="responsive"><div class="gallery"><a href=/details/' + movie_id + '><img src="'
   + img_poster + '" alt="' + title + '" width="300" height="450"></a><div class="desc">' +
  title + '</div></div></div>';
}

class Movies extends Component {
  constructor(props) {
    super(props);
    this.state = {
    movies: "",
    is_get_movie: false
    }
};

  getPopularMovie = () => {
    movies.search('?orderby=year&direction=DESC')
    .then(response => {
      var res_movie = response['data']['movies'];
      var list_movie = [];
      for (var i = 0; i < res_movie.length; i++){
        list_movie.push(res_movie[i]);
      }
      this.setState({movies: list_movie});
      this.setState({is_get_movie: true});
    })
    .catch(error => console.log(error));

  }

  renderPopularMovies = () => {
    var movies = this.state.movies;
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
          <Route path="/" component={SearchBar} />
        <h2> Top Rating</h2>
          <div className="gallery" dangerouslySetInnerHTML={{ __html: this.renderPopularMovies()}}  />
        </div>

    );
  }
}

export default Movies;
