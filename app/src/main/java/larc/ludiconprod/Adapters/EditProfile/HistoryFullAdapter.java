package larc.ludiconprod.Adapters.EditProfile;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.TreeMap;

import larc.ludiconprod.R;
import larc.ludiconprod.Service.History;
import larc.ludiconprod.Utils.util.Sport;

public class HistoryFullAdapter extends RecyclerView.Adapter<HistoryFullAdapter.ViewHolder> {

    ArrayList<History> historyArrayList;
    TreeMap<String, Integer> iconCodes;

    public void addAllItems(ArrayList<History> items) {
        historyArrayList.addAll(items);
        notifyDataSetChanged();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        ImageView icon;
        TextView date;
        TextView hour;
        TextView eventDescription;

        public ViewHolder(View v) {
            super(v);
            icon = (ImageView) v.findViewById(R.id.IVSportIconHistory);
            date = (TextView) v.findViewById(R.id.TVDateHistory);
            hour = (TextView) v.findViewById(R.id.TVHour);
            eventDescription = (TextView) v.findViewById(R.id.TVEventDescriptionHistory);
        }
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        Sport sport = new Sport();
        String text;

        text = sport.getSportName(historyArrayList.get(position).getSportName(), "en");
        text = "Played " + text + " with " + historyArrayList.get(position).getNumberOfParticipants() + " guys";

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(historyArrayList.get(position).getEventDate() * 1000L);
        cal.setTimeZone(TimeZone.getTimeZone("GMT+3"));

        holder.icon.setImageResource(iconCodes.get(historyArrayList.get(position).getSportName()));
        holder.date.setText((new DateFormatSymbols().getMonths()[cal.get(Calendar.MONTH)]
                + "-" + cal.get(Calendar.DAY_OF_MONTH) + ""));
        holder.hour.setText((cal.get(Calendar.HOUR_OF_DAY) + ":00"));
        holder.eventDescription.setText(text);

    }


    public HistoryFullAdapter(ArrayList<History> historyArrayList, TreeMap<String, Integer> iconCodes) {
        this.historyArrayList = historyArrayList;
        this.iconCodes = iconCodes;
    }


    @NonNull
    public HistoryFullAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                            int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.history_full_adapter, parent, false);

        return new ViewHolder(v);

    }

    @Override
    public int getItemCount() {
        return historyArrayList.size();
    }
}
