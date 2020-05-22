import React, { Component } from "react";
import { Route } from "react-router-dom";

import movies from "../services/movie";

import "../css/search.css"

import SearchBar from "./SearchBar";

function renderThumb(title, year, director, rating, thumb, movie_id) {
  var img_poster = "https://image.tmdb.org/t/p/w300_and_h450_bestv2/" + thumb;
  return '<div style="float:left; width:49.9999%; padding:10px">' +
  '<div style="float:left; width:29.9999%; padding:10px"><a href=/details/' + movie_id + '><img src="' +
  img_poster + '" alt="' + title + '" width="100%" height="auto"></img></div>' +
  '<div style="float:left; width:69.9999%; padding:10px">' +
  '<div style="content: ""; display: table; clear: both"><h2>' +
     title + '</h2><p style="padding: 5px; font-size: 18px"> Year: ' +
     year + '</p><p style="padding: 5px; font-size: 18px"> Director: ' +
     director + '</p><p style="padding: 5px; font-size: 18px""> Rating: ' + 
     rating + '</p><a style="display: flex; justify-content: center; margin-top: 10px; width: 130px; padding:10px; border-radius: 3px; border-style: none;font-size: 20px;color: #d6c6c6;text-decoration: none; background-color: #2e2c2c; box-shadow: inset 0px 0px 5px -2px #000000;cursor: pointer" href="/details/' +
     movie_id + '">View details</a>' +
    '</div></div></div>'
}


class Search extends Component {
  state = {
    movies: "",
    is_get_movie: false
};

  getPopularMovie = () => {
    var query_string = this.props.location.search;
    console.log('browns: ' + query_string.substring(9));
    if (query_string.substring(1, 8) === 'keyword'){
        query_string = query_string.substring(9);
        query_string = query_string.replace('&', '?');
        movies.browse(query_string)
        .then(response => {
        var res_movie = response['data']['movies'];
        var list_movie = [];
        for (var i = 0; i < res_movie.length; i++){
            list_movie.push(res_movie[i]);
        }
        this.setState({movies: list_movie});
        this.setState({is_get_movie: true});
        }).catch(error => console.log(error));

    }
    else {
        movies.search(query_string)
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
  }

  renderPopularMovies = () => {
    var movies = this.state.movies;
    var thumb_list = '';
    for (var i = 0; i < movies.length; i++)
    {
      if( movies[0] !== undefined){
        thumb_list += renderThumb(movies[i]['title'], 
        movies[i]['year'],
        movies[i]['director'],
        movies[i]['rating'],
        movies[i]['poster_path'],
        movies[i]['movie_id']);
      }
    }
    return thumb_list;
  }
  
  set_get_movie = () => {
    this.setState({is_get_movie: false})
  }

  view_details = () => {
    return this.props.history.push('/details');
  }


  render() {
    const { is_get_movie} = this.state;
    if (this.state.is_get_movie === false) {
        this.getPopularMovie();}

    return (
    <div>
      <div>
        <Route path="/" component={props => <SearchBar set_get_movie={this.set_get_movie} 
            is_get_movie={is_get_movie}        {...props} />} />
        
      </div>

      <div className="container">
        <h1 className ='label'>Search Result</h1>
        <div className="row" dangerouslySetInnerHTML={{ __html: this.renderPopularMovies()}}>
        </div>
      </div>
    </div>  
    );
  }
}

export default Search;
