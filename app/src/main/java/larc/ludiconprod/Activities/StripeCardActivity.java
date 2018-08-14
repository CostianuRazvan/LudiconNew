package larc.ludiconprod.Activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.android.view.CardInputWidget;

import larc.ludiconprod.R;
import me.anwarshahriar.calligrapher.Calligrapher;

import static larc.ludiconprod.BuildConfig.APPLICATION_ID;


public class StripeCardActivity extends BasicActivity {

    Card card;
    ProgressDialog progress;
    private final String PUBLISHABLE_KEY = "pk_test_HGUwV7yQpkur9gUCIxJyQwTD";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stripe_card);

        CardInputWidget mCardInputWidget = (CardInputWidget) findViewById(R.id.card_input_widget);

        Calligrapher calligrapher = new Calligrapher(this);
        this.setTitle("Ludicon");
        calligrapher.setFont(this, "fonts/Quicksand-Medium.ttf", true);

        
        card = new Card(
                "4242424242424242", //card number
                12, //expMonth
                2016,//expYear
                "123"//cvc
        );
        progress = new ProgressDialog(this);
        Button purchase = (Button) findViewById(R.id.button);
        purchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buy();
            }
        });
    }

    private void buy() {
        boolean validation = card.validateCard();
        if (validation) {
            startProgress("Validating Credit Card");
            new Stripe(getApplicationContext()).createToken(
                    card,
                    PUBLISHABLE_KEY,
                    new TokenCallback() {
                        @Override
                        public void onError(Exception error) {
                            Log.d("Stripe", error.toString());
                        }

                        @Override
                        public void onSuccess(Token token) {
                            finishProgress();
                            //charge(token);
                        }
                    });


        } else if (!card.validateNumber()) {
            Log.d("Stripe", "The card number that you entered is invalid");
        } else if (!card.validateExpiryDate()) {
            Log.d("Stripe", "The expiration date that you entered is invalid");
        } else if (!card.validateCVC()) {
            Log.d("Stripe", "The CVC code that you entered is invalid");
        } else {
            Log.d("Stripe", "The card details that you entered are invalid");
        }
    }


    public void onClickSomething(String cardNumber, int cardExpMonth, int cardExpYear, String cardCVC) {
        Card card = new Card(
                cardNumber,
                cardExpMonth,
                cardExpYear,
                cardCVC
        );

        card.validateNumber();
        card.validateCVC();
    }

    private void startProgress(String title) {
        progress.setTitle(title);
        progress.setMessage("Please Wait");
        progress.show();
    }

    private void finishProgress() {
        progress.dismiss();
    }

    }


