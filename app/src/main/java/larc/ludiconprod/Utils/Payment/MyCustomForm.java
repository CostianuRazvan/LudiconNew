package larc.ludiconprod.Utils.Payment;

import android.content.Context;

import com.stripe.android.model.Card;


public class MyCustomForm {

    Context context;

    public MyCustomForm(Context context) {
        this.context = context;
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

    public void testMyCard() {
        Card card = new Card("4242424242424242", 12, 2019, "123");
// Remember to validate the card object before you use it to save time.
        if (!card.validateCard()) {
            // Do not continue token creation.
        }


    }
}
