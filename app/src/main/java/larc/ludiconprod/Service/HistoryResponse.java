package larc.ludiconprod.Service;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class HistoryResponse {

    @SerializedName("pastEvents")
    private ArrayList<History> historyArrayList;

    public ArrayList<History> getHistoryArrayList() {
        return historyArrayList;
    }

}
