package larc.ludiconprod.Utils.ChatAndFriends;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import larc.ludiconprod.Activities.BasicFragment;
import larc.ludiconprod.R;

/**
 * Created by ancuta on 8/18/2017.
 */

public class ChatAndFriendTab2 extends BasicFragment {

    TextView noConversationTV;
    TextView joinActivitiesTV;
    Button discoverActivitiesButton;


    private void translate() {
        if (getLanguage().equalsIgnoreCase("ro")) {
            noConversationTV.setText(R.string.ro_no_conversations);
            joinActivitiesTV.setText(R.string.ro_join_activities);
            discoverActivitiesButton.setText(R.string.ro_discover_activities);
        } else {
            noConversationTV.setText(R.string.en_no_conversations);
            joinActivitiesTV.setText(R.string.en_join_activities);
            discoverActivitiesButton.setText(R.string.en_discover_activities);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.friend_tab, container, false);


        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        noConversationTV = (TextView) view.findViewById(R.id.noFriendsTV);
        joinActivitiesTV = (TextView) view.findViewById(R.id.joinActivitiesFriendsTV);
        discoverActivitiesButton = (Button) view.findViewById(R.id.discoverActivitiesFriendsButton);
        translate();
    }
}