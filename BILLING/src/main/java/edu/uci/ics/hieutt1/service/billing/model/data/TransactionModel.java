package edu.uci.ics.hieutt1.service.billing.model.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class TransactionModel {
    @JsonProperty(
            value = "capture_id",
            required = true
    )
    String capture_id;
    @JsonProperty(
            value = "state",
            required = true
    )
    String state;
    @JsonProperty(
            value = "amount",
            required = true
    )
    TransactionModel.Amount amount;
    @JsonProperty(
            value = "transaction_fee",
            required = true
    )
    TransactionModel.Transaction_fee transaction_fee;
    @JsonProperty(
            value = "create_time",
            required = true
    )
    String create_time;
    @JsonProperty(
            value = "update_time",
            required = true
    )
    String update_time;
    @JsonProperty(
            value = "items",
            required = true
    )
    TransactionModel.ItemModel[] items;

    public TransactionModel() {
    }

    public String getCapture_id() {
        return this.capture_id;
    }

    public void setCapture_id(String capture_id) {
        this.capture_id = capture_id;
    }

    public String getState() {
        return this.state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public TransactionModel.Amount getAmount() {
        return this.amount;
    }

    public void setAmount(TransactionModel.Amount amount) {
        this.amount = amount;
    }

    public TransactionModel.Transaction_fee getTransaction_fee() {
        return this.transaction_fee;
    }

    public void setTransaction_fee(TransactionModel.Transaction_fee transaction_fee) {
        this.transaction_fee = transaction_fee;
    }

    public String getCreate_time() {
        return this.create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getUpdate_time() {
        return this.update_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }

    public TransactionModel.ItemModel[] getItems() {
        return this.items;
    }

    public void setItems(TransactionModel.ItemModel[] items) {
        this.items = items;
    }

    public static class ItemModel {
        @JsonProperty(
                value = "email",
                required = true
        )
        private String email;
        @JsonProperty(
                value = "movie_id",
                required = true
        )
        private String movie_id;
        @JsonProperty(
                value = "quantity",
                required = true
        )
        private Integer quantity;
        @JsonProperty(
                value = "unit_price",
                required = true
        )
        private Float unit_price;
        @JsonProperty(
                value = "discount",
                required = true
        )
        private Float discount;
        @JsonProperty(
                value = "sale_date",
                required = true
        )
        private String sale_date;

        public ItemModel() {
        }

        public String getEmail() {
            return this.email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getMovie_id() {
            return this.movie_id;
        }

        public void setMovie_id(String movie_id) {
            this.movie_id = movie_id;
        }

        public Integer getQuantity() {
            return this.quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }

        public Float getUnit_price() {
            return this.unit_price;
        }

        public void setUnit_price(Float unit_price) {
            this.unit_price = unit_price;
        }

        public Float getDiscount() {
            return this.discount;
        }

        public void setDiscount(Float discount) {
            this.discount = discount;
        }

        public String getSale_date() {
            return this.sale_date;
        }

        public void setSale_date(String sale_date) {
            this.sale_date = sale_date;
        }
    }

    public static class Transaction_fee {
        @JsonProperty(
                value = "value",
                required = true
        )
        private String value;
        @JsonProperty(
                value = "currency",
                required = true
        )
        private String currency;

        public Transaction_fee() {
        }

        public String getValue() {
            return this.value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getCurrency() {
            return this.currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }
    }

    public static class Amount {
        @JsonProperty(
                value = "total",
                required = true
        )
        private String total;
        @JsonProperty(
                value = "currency",
                required = true
        )
        private String currency;

        public Amount() {
        }

        public String getTotal() {
            return this.total;
        }

        public void setTotal(String total) {
            this.total = total;
        }

        public String getCurrency() {
            return this.currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }
    }
}