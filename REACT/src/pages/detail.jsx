import React, { Component } from "react";
import Cookies from "js-cookie";


import billing from "../services/billing";
import movies from "../services/movie";


import "../css/movieDetails.css"

class Details extends Component {
    state = {
        message: '',
        number: 0,  
        movie: '',
        is_get_movie: false
    };

    handleSubmit = e => {
        e.preventDefault();
    
        const { number} = this.state;

        var email =  Cookies.get("email");
        var movie_id = this.props.location.pathname.substring(9);
    
        billing.insert(email, movie_id, number)
          .then(response => {
            console.log(response);
            var message = response['data']['message'];
            this.setState({message: message })

          })
          .catch(error => console.log(error));
        
        }

    getMovieDetails = () => {
        var query_string = this.props.location.pathname;
        query_string = query_string.substring(9);
        console.log(query_string);
        movies.get(query_string)
        .then(response => {
            console.log(response);
            var res_movie = response['data']['movie'];
            console.log(res_movie);
            this.setState({is_get_movie: true});
            this.setState({movie: res_movie});
            }).catch(error => console.log(error));
    }

    renderMovieDetails = () => {
        var img_poster = "http://image.tmdb.org/t/p/w300" + this.state.movie['poster_path'];
        var title = this.state.movie['title'];
        var director = this.state.movie['director'];
        var rating = this.state.movie['rating'];
        var budget =  new Intl.NumberFormat().format(this.state.movie['budget']);
        var genre_list = [];
        genre_list = this.state.movie['genres'];
        console.log(genre_list);
        var genre = '';
        if(typeof genre_list != 'undefined'){
            for (var i  = 0; i < genre_list.length; i++) {
                genre += genre_list[i]['name'] + ', ';
            }
        }
        var year = this.state.movie['year'];
        var overview = this.state.movie['overview'];

        return (
    <div className='outbound' >
        <div className='col-30'>
            <img src={img_poster} alt="Avengers: Endgame" width="100%" height="auto"></img>
        </div>
        <div className='col-60'>
            <div >
                <h2>{title}</h2>
                <p className='cont'> Year: {year}</p>
                <p className='cont'> Director: {director}</p>
                <p className='cont'> Genre: {genre}</p>
                <p className='cont'> Rating: {rating}</p>
                <p className='cont'> Budget: {budget}</p>
                <p className='cont'> {overview}</p>
            </div>  
        </div>
    </div>
        )
    }

    updateField = ({ target }) => {
        const { name, value } = target;
    
        this.setState({ [name]: value });
      };



  render() {
    if (this.state.is_get_movie === false) {
        this.getMovieDetails();}

    const { number } = this.state;

    return (
        <div>
            <div className="page_name">
                <h1>Movie Detail</h1>
            </div>
            {this.renderMovieDetails()}
            <div className='form'>
                <form  onSubmit={this.handleSubmit}>
                <label>Number</label>
                <input
                    className="input_number"
                    type="number"
                    name="number"
                    value={number}
                    onChange={this.updateField}
                ></input>
                <button className='button'>Add to Cart</button>
                </form>
                <div className='anno'> {this.state.message}</div>
            </div>
        </div>
    );
  }
}

export default Details;
