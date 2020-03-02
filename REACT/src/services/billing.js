import Socket from "../util/Socket";

import { billingEPs } from "../Config.json";

const { insertEP, updateEP, deleteEP, retrieveEP, placeOrderEP, retrieveOrderEP, completeOrderEP } = billingEPs;

async function insert(email, movie_id, quantity) {
    const payLoad = {
      email: email,
      movie_id: movie_id,
      quantity: quantity 
    };
    return await Socket.POST(insertEP, payLoad);
  }

async function update(email, movie_id, quantity) {
    const payLoad = {
      email: email,
      movie_id: movie_id,
      quantity: quantity 
    };
    return await Socket.POST(updateEP, payLoad);
  }

async function deleteMovie(email, movie_id) {
    const payLoad = {
      email: email,
      movie_id: movie_id,
    };
    return await Socket.POST(deleteEP, payLoad);
  }

  async function retrieve(email) {
    const payLoad = {
      email: email
    };
    return await Socket.POST(retrieveEP, payLoad);
  }

  async function placeOrder(email) {
    const payLoad = {
      email: email
    };
    return await Socket.POST(placeOrderEP, payLoad);
  }

  async function retrieveOrder(email) {
    const payLoad = {
      email: email
    };
    return await Socket.POST(retrieveOrderEP, payLoad);
  }

  async function complete(query) {
    var url = completeOrderEP;
      url += query;
      console.log("request Complete: " + url);
      return await Socket.GET(url);
    };

  export default {
    insert, update, deleteMovie, retrieve, placeOrder, retrieveOrder, complete
  };