import React, { Component } from "react";
import Cookies from "js-cookie";

import billing from "../services/billing";



import "../css/cart.css"



class Cart extends Component {
    state = {
        message: '',
        number: [0,0,0,0,0],
        movie_id: '',
        cart: '',
        is_get_cart: false
    };

  getCart = () => {
    var email =  Cookies.get("email");
    billing.retrieve(email)
          .then(response => {
            console.log(response);
            var message = response['data']['message'];
            var cart = response['data']['items']
            this.setState({is_get_cart: true});
            this.setState({message: message})
            this.setState({cart: cart})
          })
          .catch(error => console.log(error));
        
        }

    handleUpdate = (i, movie_id) => {
      var number = this.state.number;
      var email =  Cookies.get("email");
      if (number[i] !== 0) {
          billing.update(email, movie_id, number[i])
          .then(response => {
            console.log(response);
            number[i] = 0;
            this.setState({number: number});
            this.setState({is_get_cart: false});
          }
        )
      }
    }

    handleDelete = (movie_id) => {
      var email =  Cookies.get("email");
      billing.deleteMovie(email, movie_id)
          .then(response => {
            console.log(response);
            this.setState({is_get_cart: false});
          }
        )

      }
    
    handlePlaceOrder = () => {
      var email =  Cookies.get("email");
      billing.placeOrder(email)
          .then(response => {
            console.log(response);
            if (typeof response !== 'undefined'){
              var approve_url = response['data']['approve_url'];
              window.open(approve_url);
            }
          }
          )
    }

    renderOneItem = (i, img_poster, title, unit_price, discount, quantity, total_price, movie_id) => {
      var number = this.state.number[i]
      var discount_percent = discount*100;
      return ( 
          <div className="cart-item"> 
            <div className="item-thumb">
              <img src={img_poster} alt="Avengers: Endgame" width="100%" height="auto"></img>
            </div>
            <div className="item-info">
              <h3>{title}</h3>
              <div className='cont'>Unit Price: ${unit_price} </div>
              <div className='cont'>Discount: {discount_percent}% </div>
              <div className='cont'>quantity: {quantity} </div>
              <div className='cont'>Total: ${total_price} </div>
            </div>
            <div className="item-func">
            <div className='form'>
                <label>Number</label>
                <input
                    className="input_number"
                    type="number"
                    name={i}
                    value={number}
                    onChange={this.updateField}
                ></input>
                <button className='item_button' onClick={() =>this.handleUpdate(i, movie_id)}>Update Item</button>
                <button className='item_button' onClick={() =>this.handleDelete(movie_id)}>Remove Item</button>
            </div>
            
            </div>
          </div>);

    }

    renderItems = () => {
    var allItems = [];
    var items = this.state.cart;
    var final_price = 0.0;
    if (typeof items != 'undefined') {
      for (var i = 0; i < items.length; i++) {
        var one_item = items[i];
        var img_poster = "http://image.tmdb.org/t/p/w300" + one_item["poster_path"];
        var title = one_item['movie_title'];
        var unit_price = one_item['unit_price'];
        var discount = one_item['discount'];
        var quantity = one_item['quantity'];
        var movie_id = one_item['movie_id'];
        var total_price = (unit_price*(1.0 - discount)*quantity);
        final_price += total_price;
        allItems.push(this.renderOneItem(i, img_poster, title, unit_price, discount, quantity, total_price.toFixed(2), movie_id));
      }
      final_price = final_price.toFixed(2)
    }
    return (
      <div>
      <div className='cont-header'> The final price: ${final_price} </div>
      <button className='checkout_button' onClick={() =>this.handlePlaceOrder()}>Place your order</button>
      <div>{allItems}</div>
      </div>
      );

  }

  updateField = ({ target }) => {
    const { name, value } = target;
    var number = this.state.number;
    number[name] = value;
    this.setState({ number:number });
  };
  

  render() {
    if (this.state.is_get_cart === false) {
      this.getCart();}

    return (
        <div className='cart-content'>
        <h2> Cart</h2>
        {this.renderItems()}
        </div>

    );
  }
}

export default Cart;
