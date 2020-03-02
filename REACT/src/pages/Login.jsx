import React, { Component } from "react";
//import {Redirect} from 'react-router-dom'

import Idm from "../services/Idm";

import "../css/common.css";

function checkLogin(response) {
  var resultCode = response["data"]["resultCode"];
  if (resultCode === 120){
    return true
  }
  else {
    return false
  }
}

class Login extends Component {
  state = {
    email: "",
    password: "",
    isValid: false,
    message: "please enter the email and password",
  };

  handleSubmit = e => {
    e.preventDefault();

    const { handleLogIn } = this.props;
    const { email, password } = this.state;

    Idm.login(email, password)
      .then(response => {
        console.log(response);
        if(checkLogin(response)) {
          this.setState({isValid: true});
          handleLogIn(email, response["data"]["session_id"]);
          }
        else {
          this.setState({message: response['data']['message']})}
      })
      .catch(error => console.log(error));
    
    }
  

  updateField = ({ target }) => {
    const { name, value } = target;

    this.setState({ [name]: value });
  };

  handleRedirect = () => {
    if(this.state.isValid){
      return this.props.history.push('/movies');}
  }

  render() {
    const { email, password } = this.state;

    return (
      <div>
        <h1>Login</h1>
        <div>{this.handleRedirect()}</div>
        <p>{this.state.message}</p>
        <form onSubmit={this.handleSubmit}>
          <label className="label">Email</label>
          <input
            className="input"
            type="email"
            name="email"
            value={email}
            onChange={this.updateField}
          ></input>
          <label className="label">Password</label>
          <input
            className="input"
            type="password"
            name="password"
            value={password}
            onChange={this.updateField}
          ></input>
          <button className="button">Login</button>
        </form>
      </div>
    );
  }
}

export default Login;
