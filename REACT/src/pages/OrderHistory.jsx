import React, { Component } from "react";
import Cookies from "js-cookie";

import billing from "../services/billing";
import movies from "../services/movie";




import "../css/orderHistory.css"



class OrderHistory extends Component {
    state = {
        movies: [],
        message: '',
        movie_id: '',
        orders: '',
        is_get_history: false
    };

    getHistory = () => {
    var email =  Cookies.get("email");
    billing.retrieveOrder(email)
          .then(response => {
            console.log(response);
            if(typeof response != 'undefined'){
                var message = response['data']['message'];
                var orders = response['data']['transaction']
                this.setState({is_get_history: true});
                this.setState({message: message})
                this.setState({orders: orders})
            }
          })
          .catch(error => console.log(error));
        
        }


    get_title = (movie_index, movie_id) => {
         
         movies.get(movie_id)
        .then(response => {
           var title= '';
            title = response['data']['movie']['title'];
            var movies = this.state.movies
            movies[movie_index] = title;
            this.setState({movies: movies});
        });
          
    }

    renderItem = (movie_index, movie_id, quantity, unit_price, discount) => {
      var discount_percent = discount*100;
      if(this.state.movies[movie_index] === null || typeof this.state.movies[movie_index] === 'undefined'){
        this.get_title(movie_index, movie_id);
      }
      var title = this.state.movies[movie_index];
      return (
        <div className= 'his-item'>
              <div className='his_title'>{title}</div>
              <div className='cont'>Quantity: {quantity} </div>
              <div className='cont'>Unit_price: ${unit_price} </div>
              <div className='cont'>Discount: {discount_percent}% </div>
            </div>
      )
    }

    renderOneOrder = (movie_index, time,state, amount, items) => {
      
      var items_return = []
      var num_of_items = items.length;
      for (var i = 0; i < items.length; i++) {
        var one_item = items[i];
        var unit_price = one_item['unit_price'];
        var discount = one_item['discount'];
        var quantity = one_item['quantity'];
        var movie_id = one_item['movie_id'];
        items_return.push(this.renderItem(movie_index, movie_id, quantity, unit_price, discount));
        movie_index += 1;
      }

      return ( 
            <div className='his-50'>
              <div className='his_time'>{time}</div>
              <div className='cont'>State: {state} </div>
              <div className='cont'>Amount: ${amount} </div>
              <div className='cont'>Number of items: {num_of_items} </div>
              <div>{items_return}</div>
      </div>);

    }

    renderOrders = () => {
    var allOthers = [];
    var movie_index = 0;
    var orders = this.state.orders;
    if (typeof orders != 'undefined') {
      for (var i = 0; i < orders.length; i++) {
        var one_order = orders[i];
        var time = one_order['update_time'];
        var state = one_order['state'];
        var amount = one_order['amount']['total'];
        var items = one_order['items'];
        allOthers.push(this.renderOneOrder(movie_index, time,state, amount, items));
        movie_index += 1;
      }
    }
    return (
      <div>{allOthers}</div>

      );

  }

  updateField = ({ target }) => {
    const { name, value } = target;
    var number = this.state.number;
    number[name] = value;
    this.setState({ number:number });
  };
  

  render() {
    if (this.state.is_get_history === false) {
      this.getHistory();}

    return (
        <div>
        <h2> Order History</h2>
        {this.renderOrders()}
        </div>

    );
  }
}

export default OrderHistory;
