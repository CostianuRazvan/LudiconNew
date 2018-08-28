package larc.ludiconprod.Adapters.CouponsActivity;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Downloader;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import larc.ludiconprod.Activities.CouponsActivity;
import larc.ludiconprod.Adapters.MainActivity.MyAdapter;
import larc.ludiconprod.Controller.HTTPResponseController;
import larc.ludiconprod.Controller.Persistance;
import larc.ludiconprod.Dialogs.ConfirmationDialog;
import larc.ludiconprod.R;
import larc.ludiconprod.Utils.Coupon;
import larc.ludiconprod.Utils.Quest;

public class CouponsAdapter extends BaseAdapter implements ListAdapter {

    private ArrayList<Quest> list = new ArrayList<>();
    private Context context;
    private Activity activity;
    private Resources resources;
    private CouponsActivity fragment;
    private final ListView listView;

    public static class ViewHolder {
        ImageView locationImage;
        TextView title;
        TextView location;
        TextView description;
        TextView validDate;
        TextView ludicoinsCode;
        TextView pointsToWin;
        TextView progressValue;
    }

    public CouponsAdapter(ArrayList<Quest> list, Context context, Activity activity, Resources resources, CouponsActivity fragment) {
        this.list = list;
        this.context = context;
        this.activity = activity;
        this.resources = resources;
        this.fragment = fragment;

        this.listView = (ListView) activity.findViewById(R.id.couponsList);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int pos) {
        return list.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return 0;
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {
        if (list.size() > 0) {
            final CouponsAdapter.ViewHolder holder;

            final Quest currentQuest = list.get(position);

            // Initialize the view
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.coupon_card, null);

                Typeface typeFace = Typeface.createFromAsset(activity.getAssets(), "fonts/Quicksand-Medium.ttf");
                Typeface typeFaceBold = Typeface.createFromAsset(activity.getAssets(), "fonts/Quicksand-Bold.ttf");

                holder = new CouponsAdapter.ViewHolder();
                holder.locationImage = (ImageView) view.findViewById(R.id.locationImage);
                holder.title = (TextView) view.findViewById(R.id.title);
                holder.title.setTypeface(typeFace);
                holder.location = (TextView) view.findViewById(R.id.location);
                holder.location.setTypeface(typeFace);
                holder.description = (TextView) view.findViewById(R.id.description);
                holder.description.setTypeface(typeFace);
                holder.validDate = (TextView) view.findViewById(R.id.validDate);
                holder.validDate.setTypeface(typeFace);
                holder.pointsToWin =  (TextView) view.findViewById(R.id.pointsToWinCoupons);
                holder.pointsToWin.setTypeface(typeFace);

                ((TextView) view.findViewById(R.id.getItText)).setTypeface(typeFaceBold);

                view.setTag(holder);
            } else {
                holder = (CouponsAdapter.ViewHolder) view.getTag();
            }

            final View currView = view;
            final Typeface typeFace = Typeface.createFromAsset(activity.getAssets(), "fonts/Quicksand-Medium.ttf");
            final Typeface typeFaceBold = Typeface.createFromAsset(activity.getAssets(), "fonts/Quicksand-Bold.ttf");

            view.findViewById(R.id.couponGetIt).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currView.setBackgroundColor(Color.parseColor("#f5f5f5"));

                    final ConfirmationDialog confirmationDialog = new ConfirmationDialog(activity);
                    confirmationDialog.show();
                    confirmationDialog.title.setText(R.string.confirm);
                    confirmationDialog.title.setTypeface(typeFaceBold);
                    confirmationDialog.message.setText(R.string.are_you_sure_you_want_to_reedem_this_coupon);
                    confirmationDialog.message.setTypeface(typeFace);

                    confirmationDialog.confirm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            HashMap<String, String> params = new HashMap<String, String>();
                            HashMap<String, String> headers = new HashMap<String, String>();
                            headers.put("authKey", Persistance.getInstance().getUserInfo(activity).authKey);

                            params.put("userId", Persistance.getInstance().getUserInfo(activity).id);
                            params.put("questId", currentQuest.questId);
                            HTTPResponseController.getInstance().startQuest(params, headers, activity, fragment.onRequestSuccessListener(), fragment);
                            confirmationDialog.dismiss();
                        }
                    });




                    confirmationDialog.dismiss.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            confirmationDialog.dismiss();
                        }
                    });
                }
            });

            holder.title.setText(currentQuest.title);
            if (!currentQuest.picture.equals("")) {
                Bitmap bitmap = MyAdapter.decodeBase64(currentQuest.picture);
                holder.locationImage.setImageBitmap(bitmap);
            } else {
                holder.locationImage.setImageResource(R.drawable.ph_company);
            }

            holder.location.setText("Created by Ludicon"); //currentQuest.companyName);
            holder.title.setText(currentQuest.title);
            holder.description.setText(currentQuest.description);

            holder.pointsToWin.setText("And gain " + currentQuest.points);

            SpannableStringBuilder spanTxt = new SpannableStringBuilder( holder.pointsToWin.getText());
            spanTxt.setSpan(new ForegroundColorSpan(Color.parseColor("#400c3855")), 0, 9, 0);
            spanTxt.setSpan(new ForegroundColorSpan(Color.parseColor("#02b9ad")), 9, spanTxt.length(), 0);
            holder.pointsToWin.setText(spanTxt);

            if(currentQuest.expiryDate == 0){
                holder.validDate.setText(activity.getResources().getString(R.string.unlimited_time));
            }
            else {
                Date date = new Date(currentQuest.expiryDate * 1000);
                SimpleDateFormat fmt = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());

                holder.validDate.setText(activity.getResources().getString(R.string.valid_till) + " " + fmt.format(date));
            }
        }

        return view;
    }

}
