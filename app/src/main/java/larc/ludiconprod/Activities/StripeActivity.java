package larc.ludiconprod.Activities;

import android.os.Bundle;

import com.stripe.android.model.Card;
import com.stripe.android.view.CardInputWidget;

import larc.ludiconprod.R;

public class StripeActivity extends BasicActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stripe);
        CardInputWidget mCardInputWidget = (CardInputWidget) findViewById(R.id.card_input_widget);
        Card cardToSave = mCardInputWidget.getCard();


       // cardToSave.setName("Customer Name");
      //  cardToSave.setAddressZip("12345");


    }


}
