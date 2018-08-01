package larc.ludiconprod.Utils.ChatAndFriends;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import larc.ludiconprod.Activities.BasicFragment;
import larc.ludiconprod.Adapters.ChatAndFriends.ConversationsAdapter;
import larc.ludiconprod.Controller.Persistance;
import larc.ludiconprod.R;
import larc.ludiconprod.Utils.Chat;

/**
 * Created by ancuta on 8/18/2017.
 */

public class ChatAndFriendTab1 extends BasicFragment {
    View v;
    ConversationsAdapter chatAdapter;
    public ListView chatListView;
    ArrayList<Chat> chatList = new ArrayList<>();
    TextView noConversationTV;
    TextView joinActivitiesTV;
    Button discoverActivitiesButton;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.chat_tab, container, false);
        chatList = Persistance.getInstance().getConversation(getActivity());
        chatAdapter = new ConversationsAdapter(chatList, getActivity().getApplicationContext(), getActivity(), getResources(), getLanguage());
        setAdapter();
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        translate();
    }

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

    public void setAdapter() {
        try {

            chatAdapter.notifyDataSetChanged();
            chatListView = (ListView) v.findViewById(R.id.chat_listView);
            chatListView.setAdapter(chatAdapter);
            noConversationTV = (TextView) v.findViewById(R.id.noConversationTV);
            joinActivitiesTV = (TextView) v.findViewById(R.id.joinActivitiesTV);
            discoverActivitiesButton = (Button) v.findViewById(R.id.discoverActivitiesButton);
            ImageView chatImage = (ImageView) v.findViewById(R.id.chatImage);
            noConversationTV.setVisibility(View.INVISIBLE);
            joinActivitiesTV.setVisibility(View.INVISIBLE);
            discoverActivitiesButton.setVisibility(View.INVISIBLE);
            chatImage.setVisibility(View.INVISIBLE);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}

