package larc.ludiconprod.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Button;

import com.stripe.Stripe;
import com.stripe.android.SourceCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Source;
import com.stripe.android.model.SourceParams;
import com.stripe.android.view.CardInputWidget;
import com.stripe.model.Charge;
import com.stripe.model.Customer;
import com.stripe.model.Token;
import com.stripe.net.RequestOptions;

import java.util.HashMap;
import java.util.Map;

import larc.ludiconprod.R;

public class StripeCall implements Runnable {

    Context context;
    Card card;

    public StripeCall(Context context, Card card) {
        this.context = context;
        this.card = card;
    }

    Source mThreeDSource;

    public void setmThreeDSource(com.stripe.android.model.Source source) {
        mThreeDSource = source;
    }

    @Override
    public void run() {
        try {

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            Stripe.apiKey = "sk_test_c6tKqZLFEz62OfDnOZjC7tKm";


           /* Log.d("Intra aici", "Da");
            com.stripe.android.Stripe mStripe = new com.stripe.android.Stripe(context);

            Map<String, Object> sepaParams = new HashMap<String, Object>();
            sepaParams.put("iban", "DE89370400440532013000");

            Map<String, Object> ownerParams = new HashMap<String, Object>();
            ownerParams.put("name", "Dorel");

            String cardSourceId = "1";
            SourceParams threeDParams = SourceParams.createThreeDSecureParams(
                    1000L, // some price: this represents 10.00 EUR
                    "EUR", // a currency
                    "www.viata.ro", // your redirect
                    cardSourceId);


            mStripe.createSource(threeDParams, new SourceCallback() {
                @Override
                public void onError(Exception error) {
                    error.printStackTrace();
                }

                @Override
                public void onSuccess(com.stripe.android.model.Source source) {
                    setmThreeDSource(source);
                    Log.d("SourceSuccess", source.getRedirect().getReturnUrl());
                }
            });
            String externalUrl = mThreeDSource.getRedirect().getUrl();
            // We suggest popping up a dialog asking the user
            // to tap to go to their browser so they are not
            // surprised when they leave your application.
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(externalUrl));
            context.startActivity(browserIntent);


            Map<String, Object> customerParams = new HashMap<String, Object>();
            customerParams.put("email", "paying.user@example.com");
            customerParams.put("source", mThreeDSource.getId());
            Customer customer;

            customer = Customer.create(customerParams);
*/
            Map<String, Object> tokenParams = new HashMap<String, Object>();
            Map<String, Object> cardParams = new HashMap<String, Object>();

            cardParams.put("number", card.getNumber());
            cardParams.put("cvc", card.getCVC());
            cardParams.put("exp_month", card.getExpMonth());
            cardParams.put("exp_year", card.getExpYear());
            tokenParams.put("card", cardParams);

            Log.d("API", Stripe.getApiBase());

            Token token = Token.create(tokenParams);

            //TODO To add Refund + Payout

            Map<String, Object> chargeParams = new HashMap<String, Object>();
            chargeParams.put("amount", 100000);
            chargeParams.put("currency", "eur");
            chargeParams.put("source", token.getId());
            chargeParams.put("receipt_email", "killermessi2010@gmail.com");

            Charge charge = Charge.create(chargeParams); // Works
            Log.d("Params", charge.getAmount() + "");

/*
            Map<String, Object> sepaParams = new HashMap<String, Object>();
            sepaParams.put("iban", "DE89370400440532013000");

            Map<String, Object> ownerParams = new HashMap<String, Object>();
            ownerParams.put("name", "Jenny Rosen");

            Map<String, Object> sourceParams = new HashMap<String, Object>();
            sourceParams.put("type", "sepa_debit");
            sourceParams.put("sepa_debit", sepaParams);
            sourceParams.put("currency", "eur");
            sourceParams.put("owner", ownerParams);

            com.stripe.model.Source source = com.stripe.model.Source.create(sourceParams);

             */

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
