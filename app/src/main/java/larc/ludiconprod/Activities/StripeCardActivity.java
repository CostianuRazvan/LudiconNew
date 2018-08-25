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
import com.stripe.android.view.CardMultilineWidget;

import larc.ludiconprod.R;
import me.anwarshahriar.calligrapher.Calligrapher;


public class StripeCardActivity extends BasicActivity {

    Card card;
    ProgressDialog progress;
    private final String PUBLISHABLE_KEY = "pk_test_HGUwV7yQpkur9gUCIxJyQwTD";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stripe_card);

        CardMultilineWidget mCardInputWidget = (CardMultilineWidget) findViewById(R.id.card_input_widget);

        Calligrapher calligrapher = new Calligrapher(this);
        this.setTitle("Ludicon");
        calligrapher.setFont(this, "fonts/Quicksand-Medium.ttf", true);


        card = new Card(
                "4242424242424242", //card number
                12, //expMonth
                2019,//expYear
                "123"//cvc
        );
        Button purchase = (Button) findViewById(R.id.BTNPurchase);
        purchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Thread thread = new Thread(new StripeCall(getApplicationContext(), card));
                thread.start();
                try {
                    thread.join();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

}


