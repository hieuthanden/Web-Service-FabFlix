import React, { Component } from "react";
import Cookies from "js-cookie";
import Axios from "axios";

import NavBar from "./NavBar";
import Content from "./Content";

class App extends Component {
  state = {
    loggedIn: this.checkedLoggedIn()
  };

  handleLogIn = (email, session_id) => {
    const { common } = Axios.defaults.headers;

    Cookies.set("email", email);
    Cookies.set("session_id", session_id);

    common["email"] = email;
    common["session_id"] = session_id;

    this.setState({ loggedIn: true });
    
  };

  handleLogOut = () => {
    const { common } = Axios.defaults.headers;

    Cookies.remove("email");
    Cookies.remove("session_id");

    delete common["email"];
    delete common["session_id"];

    this.setState({ loggedIn: false });
  };

  checkedLoggedIn() {
    return (
      Cookies.get("email") !== undefined &&
      Cookies.get("session_id") !== undefined
    );
  }

  render() {
    const { loggedIn } = this.state;

    return (
      <div className="app">
        <NavBar handleLogOut={this.handleLogOut} loggedIn={loggedIn} />
        <Content handleLogIn={this.handleLogIn} />
      </div>
    );
  }
}

export default App;
