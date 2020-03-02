import React, { Component } from "react";
import { Route, Switch } from "react-router-dom";

import Login from "./pages/Login";
import Movies from "./pages/Movies";
import Home from "./pages/Home";
import Register from "./pages/Register";
import Search from "./pages/Search";
import Details from "./pages/detail";
import Cart from "./pages/Cart";
import Complete from "./pages/Complete";
import OrderHistory from "./pages/OrderHistory";

class Content extends Component {
  render() {
    const { handleLogIn } = this.props;

    return (
      <div className="content">
        <Switch>
          <Route
            path="/login"
            component={props => <Login handleLogIn={handleLogIn} {...props} />}
          />
          <Route path="/orderHistory" component={OrderHistory} />
          <Route path="/complete" component={Complete} />
          <Route path="/details" component={Details} />
          <Route path="/register" component={Register} />
          <Route path="/movies" component={Movies}/>
          <Route path="/search" component={Search} />
          <Route path="/cart" component={Cart} />
          <Route path="/" component={Home} />
        </Switch>
      </div>
    );
  }
}

export default Content;
