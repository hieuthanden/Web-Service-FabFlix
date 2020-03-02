package edu.uci.ics.hieutt1.service.billing.core;

import com.braintreepayments.http.HttpResponse;
import com.braintreepayments.http.exceptions.HttpException;
import com.braintreepayments.http.serializer.Json;
import com.paypal.orders.Capture;
import com.paypal.orders.Order;
import com.paypal.orders.OrdersGetRequest;
import com.paypal.orders.PurchaseUnit;
import edu.uci.ics.hieutt1.service.billing.model.data.TransactionModel;
import edu.uci.ics.hieutt1.service.billing.model.data.TransactionModel.Amount;
import edu.uci.ics.hieutt1.service.billing.model.data.TransactionModel.Transaction_fee;
import java.io.IOException;

public class RetrievePayPalOrder {
    private static PayPalOrderClient orderClient;
    private static String order_id;

    public RetrievePayPalOrder(PayPalOrderClient payPalOrderClient, String order_id) {
        orderClient = payPalOrderClient;
        RetrievePayPalOrder.order_id = order_id;
    }

    public TransactionModel retrieve() {
        TransactionModel transactionModel = new TransactionModel();

        try {
            OrdersGetRequest request = new OrdersGetRequest(order_id);
            HttpResponse<Order> response = orderClient.client.execute(request);
            transactionModel.setCapture_id(((Capture)((PurchaseUnit)((Order)response.result()).purchaseUnits().get(0)).payments().captures().get(0)).id());
            transactionModel.setState(((Order)response.result()).status());
            Amount amount = new Amount();
            amount.setTotal(((Capture)((PurchaseUnit)((Order)response.result()).purchaseUnits().get(0)).payments().captures().get(0)).amount().value());
            amount.setCurrency(((Capture)((PurchaseUnit)((Order)response.result()).purchaseUnits().get(0)).payments().captures().get(0)).amount().currencyCode());
            transactionModel.setAmount(amount);
            Transaction_fee transaction_fee = new Transaction_fee();
            transaction_fee.setValue(((Capture)((PurchaseUnit)((Order)response.result()).purchaseUnits().get(0)).payments().captures().get(0)).sellerReceivableBreakdown().paypalFee().value());
            transaction_fee.setCurrency(((Capture)((PurchaseUnit)((Order)response.result()).purchaseUnits().get(0)).payments().captures().get(0)).sellerReceivableBreakdown().paypalFee().currencyCode());
            transactionModel.setTransaction_fee(transaction_fee);
            transactionModel.setCreate_time(((Order)response.result()).createTime());
            transactionModel.setUpdate_time(((Order)response.result()).updateTime());
            System.out.println("Full response body:" + (new Json()).serialize(response.result()));
        } catch (IOException var6) {
            if (var6 instanceof HttpException) {
                HttpException he = (HttpException)var6;
                System.out.println(he.getMessage());
                he.headers().forEach((x) -> {
                    System.out.println(x + " :" + he.headers().header(x));
                });
            }
        }

        return transactionModel;
    }
}