import React, { Component } from "react";

import billing from "../services/billing";

class Complete extends Component {
    state = {
        is_complete: false,
        message: ""
      };

    getComplete = () => {
        var query_string = this.props.location.pathname;
        query_string = query_string.substring(10);
        billing.complete(query_string)
              .then(response => {
                console.log(response);
              })
              .catch(error => console.log(error));
            
            }


    render() {
        if (this.state.is_complete === false) {
          this.getComplete();}
    
        return (
            <div >

            </div>
    
        );
      }
}
export default Complete;