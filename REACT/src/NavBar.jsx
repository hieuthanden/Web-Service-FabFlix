import React, { Component, Fragment } from "react";
import { NavLink } from "react-router-dom";
import {Redirect} from 'react-router-dom'

import "./css/style.css";



class NavBar extends Component {
  state = {
    redirect: false
  }
  
  logOut = () => {
    const { handleLogOut} = this.props;
    handleLogOut();
    this.setState({redirect: true})
  }

  handleRedirect = () => {
    if(this.state.redirect){
      this.setState({redirect: false});
      return <Redirect push to='/login' />;}
  }

  render() {
    const { loggedIn } = this.props;

    return (
      <nav className="nav-bar">
        <div>{this.handleRedirect()}</div>
        {!loggedIn && (
          <Fragment>
            <NavLink className="nav-link" to="/login">
              Login
            </NavLink>
            <NavLink className="nav-link" to="/register">
              Register
            </NavLink>
          </Fragment>
        )
        }
        {loggedIn && (
          <Fragment>
            <NavLink className="nav-link" to="/movies">
              Movies
            </NavLink>
            <NavLink className="nav-link" to="/cart">
              Cart
            </NavLink>
            <NavLink className="nav-link" to="/orderHistory">
              Order History
            </NavLink>
            <button onClick= {this.logOut} className="nav-button" >
              Log Out
            </button>
          </Fragment>
        )}
      </nav>
    );
  }
}

export default NavBar;
