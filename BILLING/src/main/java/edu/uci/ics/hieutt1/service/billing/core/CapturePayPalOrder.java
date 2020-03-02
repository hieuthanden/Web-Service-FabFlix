package edu.uci.ics.hieutt1.service.billing.core;

import com.braintreepayments.http.HttpResponse;
import com.braintreepayments.http.exceptions.HttpException;
import com.paypal.orders.Capture;
import com.paypal.orders.Order;
import com.paypal.orders.OrdersCaptureRequest;
import com.paypal.orders.PurchaseUnit;
import java.io.IOException;

public class CapturePayPalOrder {
    private static PayPalOrderClient orderClient;
    private static String order_id;

    public CapturePayPalOrder(PayPalOrderClient payPalOrderClient, String order_id) {
        orderClient = payPalOrderClient;
        CapturePayPalOrder.order_id = order_id;
    }

    public String capture() {
        Order order = null;
        OrdersCaptureRequest request = new OrdersCaptureRequest(order_id);
        String capture_id = null;

        try {
            HttpResponse<Order> response = orderClient.client.execute(request);
            order = (Order)response.result();
            capture_id = ((Capture)((PurchaseUnit)order.purchaseUnits().get(0)).payments().captures().get(0)).id();
            System.out.println("Capture ID: " + capture_id);
            ((Capture)((PurchaseUnit)order.purchaseUnits().get(0)).payments().captures().get(0)).links().forEach((link) -> {
                System.out.println(link.rel() + " => " + link.method() + ":" + link.href());
            });
        } catch (IOException var6) {
            if (var6 instanceof HttpException) {
                HttpException he = (HttpException)var6;
                System.out.println(he.getMessage());
                he.headers().forEach((x) -> {
                    System.out.println(x + " :" + he.headers().header(x));
                });
            }
        }

        return capture_id;
    }
}