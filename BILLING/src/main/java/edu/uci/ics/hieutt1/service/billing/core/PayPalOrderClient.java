package edu.uci.ics.hieutt1.service.billing.core;

import com.paypal.core.PayPalEnvironment;
import com.paypal.core.PayPalHttpClient;
import com.paypal.core.PayPalEnvironment.Sandbox;

public class PayPalOrderClient {
    private static final String clientId = "ATaKzCsEEUAtrwGjPxLTJ-R-ByNdKdTR6ZJft1hsybQ2e8tsVExBOugE3210R4iTYCjFForBRbjJg2fa";
    private static final String clientSecret = "EOCjC62I-BIDXuMmZ48LGkyAwNnG1Ygc1mfbl3xkwvwLC2VxULByIw7ONl01v2nU55Vymn7K2EZOIQfw";
    public PayPalEnvironment environment = new Sandbox("ATaKzCsEEUAtrwGjPxLTJ-R-ByNdKdTR6ZJft1hsybQ2e8tsVExBOugE3210R4iTYCjFForBRbjJg2fa", "EOCjC62I-BIDXuMmZ48LGkyAwNnG1Ygc1mfbl3xkwvwLC2VxULByIw7ONl01v2nU55Vymn7K2EZOIQfw");
    public PayPalHttpClient client;

    public PayPalOrderClient() {
        this.client = new PayPalHttpClient(this.environment);
    }
}