package larc.ludiconprod.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import larc.ludiconprod.R;

public class PaymentDialog extends Dialog {

    String language;
    Activity activity;

    private TextView title;
    private TextView message;
    public Button confirm;
    public Button dismiss;

    public PaymentDialog(Activity activity, String language) {
        super(activity);
        this.activity = activity;
        this.language = language;
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.payment_dialog);
        title = (TextView) findViewById(R.id.CMtitle);
        message = (TextView) findViewById(R.id.CMmessage);
        confirm = (Button) findViewById(R.id.CMconfirm);
        dismiss = (Button) findViewById(R.id.CMdismiss);

        final Typeface typeFace = Typeface.createFromAsset(activity.getAssets(), "fonts/Quicksand-Medium.ttf");
        final Typeface typeFaceBold = Typeface.createFromAsset(activity.getAssets(), "fonts/Quicksand-Bold.ttf");
        title.setTypeface(typeFaceBold);
        message.setTypeface(typeFace);
        confirm.setTypeface(typeFace);
        dismiss.setTypeface(typeFace);

        translate();
    }

    private void translate() {
        if (language.equalsIgnoreCase("ro")) {
            title.setText(R.string.ro_confirm);
            String messageString = "";
            messageString += getContext().getResources().getString(R.string.ro_payment_message);
            messageString += " 20 " + getContext().getResources().getString(R.string.ro_sum_message);
            message.setText(messageString);
            confirm.setText(R.string.ro_yes);
            dismiss.setText(R.string.ro_no);
        } else {
            title.setText(R.string.en_confirm);
            String messageString = "";
            messageString += getContext().getResources().getString(R.string.en_payment_message);
            messageString += " 20 " + getContext().getResources().getString(R.string.en_sum_message);
            message.setText(messageString);
            confirm.setText(R.string.en_yes);
            dismiss.setText(R.string.en_no);
        }
    }
}
