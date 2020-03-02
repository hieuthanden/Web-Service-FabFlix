import React, { Component } from "react";


import "../css/search.css";


class SearchBar extends Component {
constructor(props) {
    super(props);
    this.state = {
        advance: false,
        search: '',
        search_option: 'keyword',
        sort_by: 'title',
        title: '',
        year: '',
        director: '',
        genre: '',
        direction: 'asc',
        limit: '10'
    };

}

  handleSubmit = e => {
    e.preventDefault();
     
    const {set_get_movie, is_get_movie } = this.props;
    if(typeof set_get_movie != 'undefined' && is_get_movie) {
      set_get_movie();}

    var url = '';
    if(this.state.advance === false) {
      url = '/search?' + this.state.search_option + '=' + this.state.search;
    }
    else{
      url = '/search?';
      if (this.state.title !== '') {
        url += 'title=' + this.state.title + '&';
      }
      if (this.state.year !== '') {
        url += 'year=' + this.state.year + '&';
      }
      if (this.state.director !== '') {
        url += 'director=' + this.state.director + '&';
      }
      if (this.state.genre !== '') {
        url += 'genre=' + this.state.genre + '&';
      }
      url = url.substring(0, url.length -1 )
    }
    url += '&orderby=' + this.state.sort_by;
    url += '&direction=' + this.state.direction;
    url += '&limit=' + this.state.limit;
    return this.props.history.push(url)

  }

  updateField = ({ target }) => {
    const { name, value } = target;
    this.setState({ [name]: value });
  };
  
  advance_change = e => {
    e.preventDefault();
    if(this.state.advance === false) {
      this.setState({advance: true});}
    else {
      this.setState({advance: false});}
  }


  render() {
    const { search, search_option, sort_by, advance, title, year, director, genre, direction, limit } = this.state;

    return (
      <div className='search_box'>
        {!advance && (
        <div>
        <form onSubmit={this.handleSubmit}>
          <label className="label">Search</label>
          <div className='search_choise'>
            <select className='dropdown' name="search_option" value={search_option} onChange={this.updateField}>
                <option value="keyword">Keyword</option>
                <option value="title">Title</option>
                <option value="director">Director</option>
                <option value="year">Year</option>
                <option value="genre">Genre</option>
            </select>
            <select className='dropdown sortby' name="sort_by" value={sort_by} onChange={this.updateField}>
                <option value="title">Sort by: Title</option>
                <option value="year">Sort by: Year</option>
                <option value="rating">Sort by: Rating</option>
            </select>
            <select className='dropdown sortby' name="direction" value={direction} onChange={this.updateField}>
                <option value="asc">Direction: Ascending</option>
                <option value="desc">Direction: Descending</option>
            </select>
            <select className='dropdown sortby' name="limit" value={limit} onChange={this.updateField}>
                <option value="10">Limit: 10</option>
                <option value="25">Limit: 25</option>
                <option value="50">Limit: 50</option>
                <option value="100">Limit: 100</option>
            </select>
            <button onClick={this.advance_change} className="dropdown adv">Advanced Search</button>
          </div>
          <div>
            <input
              className="input"
              type="search"
              name="search"
              value={search}
              onChange={this.updateField}
            ></input>
          </div>
          <button className="button">Search</button>
        </form>
        </div>
        )}
        {advance && (
        <div>
        <form onSubmit={this.handleSubmit}>
          <label className="label">Search</label>
          <div className='search_choise'>
            <select className='dropdown sortby' name="sort_by" value={sort_by} onChange={this.updateField}>
                <option value="title">Sort by: Title</option>
                <option value="year">Sort by: Year</option>
                <option value="rating">Sort by: Rating</option>
            </select>
            <select className='dropdown sortby' name="direction" value={direction} onChange={this.updateField}>
                <option value="asc">Direction: Ascending</option>
                <option value="desc">Direction: Descending</option>
            </select>
            <select className='dropdown sortby' name="limit" value={limit} onChange={this.updateField}>
                <option value="10">Limit: 10</option>
                <option value="25">Limit: 25</option>
                <option value="50">Limit: 50</option>
                <option value="100">Limit: 100</option>
            </select>
            <button onClick={this.advance_change} className="dropdown adv">Basic Search</button>
          </div>
          <div>
            <input
              placeholder="Title..."
              className="input"
              type="search"
              name="title"
              value={title}
              onChange={this.updateField}
            ></input>
            <input
              placeholder="Year..."
              className="input"
              type="search"
              name="year"
              value={year}
              onChange={this.updateField}
            ></input>
            <input
              placeholder="Director..."
              className="input"
              type="search"
              name="director"
              value={director}
              onChange={this.updateField}
            ></input>
            <input
              placeholder="Genre..."
              className="input"
              type="search"
              name="genre"
              value={genre}
              onChange={this.updateField}
            ></input>
          </div>
          <button className="button">Search</button>
        </form>
        </div>
        )}
      </div>
    );
  }
}

export default SearchBar
