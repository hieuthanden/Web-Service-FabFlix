import React, { Component } from "react";

import Idm from "../services/Idm";

import "../css/common.css";


class Register extends Component {
    state = {
        email: "",
        password: "",
        message: "password must contain lowercase, uppercase, and number"
      };
  handleSubmit = e => {
    e.preventDefault();

    const { email, password } = this.state;

    Idm.register(email, password)
      .then(response => {
        console.log(response);
        this.setState({message: response['data']['message']})
      })
      .catch(error => console.log(error));
    //return this.props.history.push('/login');
  };
  updateField = ({ target }) => {
    const { name, value } = target;

    this.setState({ [name]: value });
  };    
  render() {
    const { email, password } = this.state;
    return (
      <div>
        <h1>Register</h1>
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
          <button className="button common">Register</button>
        </form>
      </div>
    );
  }
}

export default Register;