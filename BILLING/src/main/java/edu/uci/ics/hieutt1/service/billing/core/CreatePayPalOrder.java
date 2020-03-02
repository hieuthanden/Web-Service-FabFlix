//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package edu.uci.ics.hieutt1.service.billing.core;

import com.braintreepayments.http.HttpResponse;
import com.braintreepayments.http.exceptions.HttpException;
import com.paypal.orders.AmountWithBreakdown;
import com.paypal.orders.ApplicationContext;
import com.paypal.orders.LinkDescription;
import com.paypal.orders.Order;
import com.paypal.orders.OrderRequest;
import com.paypal.orders.OrdersCreateRequest;
import com.paypal.orders.PurchaseUnitRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CreatePayPalOrder {
    private static PayPalOrderClient orderClient;
    private static String total_price;

    public CreatePayPalOrder(PayPalOrderClient payPalOrderClient, String total_price) {
        orderClient = payPalOrderClient;
        CreatePayPalOrder.total_price = total_price;
    }

    public static String[] create() {
        Order order = null;
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.checkoutPaymentIntent("CAPTURE");
        ApplicationContext applicationContext = (new ApplicationContext()).returnUrl("http://localhost:9905/api/billing/order/complete").cancelUrl("http://localhost:3000/cart");
        orderRequest.applicationContext(applicationContext);
        List<PurchaseUnitRequest> purchaseUnits = new ArrayList();
        purchaseUnits.add((new PurchaseUnitRequest()).amountWithBreakdown((new AmountWithBreakdown()).currencyCode("USD").value(total_price)));
        orderRequest.purchaseUnits(purchaseUnits);
        OrdersCreateRequest request = (new OrdersCreateRequest()).requestBody(orderRequest);

        try {
            HttpResponse<Order> response = orderClient.client.execute(request);
            order = (Order)response.result();
            System.out.println("Order ID: " + order.id());
            order.links().forEach((link) -> {
                System.out.println(link.rel() + " => " + link.method() + ":" + link.href());
            });
            String[] re_string = new String[]{order.id(), ((LinkDescription)order.links().get(1)).href()};
            return re_string;
        } catch (IOException var7) {
            System.err.println("*******COULD NOT CREATE ORDER*******");
            if (var7 instanceof HttpException) {
                HttpException he = (HttpException)var7;
                System.out.println(he.getMessage());
                he.headers().forEach((x) -> {
                    System.out.println(x + " :" + he.headers().header(x));
                });
            }

            return null;
        }
    }
}
