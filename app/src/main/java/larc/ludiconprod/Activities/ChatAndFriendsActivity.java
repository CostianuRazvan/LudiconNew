package larc.ludiconprod.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.IntentCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Logger;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Struct;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.TimeZone;

import larc.ludiconprod.Adapters.ChatAndFriends.ConversationsAdapter;
import larc.ludiconprod.Adapters.ChatAndFriends.FriendsAdapter;
import larc.ludiconprod.Controller.HTTPResponseController;
import larc.ludiconprod.Controller.Persistance;
import larc.ludiconprod.R;
import larc.ludiconprod.Utils.Chat;
import larc.ludiconprod.Utils.ChatAndFriends.ChatAndFriendsViewPagerAdapter;
import larc.ludiconprod.Utils.ChatComparator.ChatComparator;
import larc.ludiconprod.Utils.Friend;
import larc.ludiconprod.Utils.ui.SlidingTabLayout;

import static larc.ludiconprod.Activities.ActivitiesActivity.deleteCachedInfo;
import static larc.ludiconprod.Activities.ChatActivity.isOnChat1to1;
import static larc.ludiconprod.Activities.Main.bottomBar;

/**
 * Created by ancuta on 8/18/2017.
 */

public class ChatAndFriendsActivity extends Fragment implements Response.ErrorListener {
    ViewPager pager;
    private Context mContext;
    ChatAndFriendsViewPagerAdapter adapter;
    SlidingTabLayout tabs;
    ConversationsAdapter chatAdapter;
    public static FriendsAdapter friendsAdapter;
    public static ArrayList<Friend> friends = new ArrayList<>();
    private View v;
    CharSequence Titles[];
    int Numboftabs = 2;
    ArrayList<Chat> chatList = new ArrayList<>();
    ArrayList<Chat> chatListClone = new ArrayList<>();
    static public ChatAndFriendsActivity currentFragment;
    FragmentActivity activity;
    public static ListView chatListView;
    public static ListView friendsListView;
    public Boolean isFirstTimeSetChat = false;
    public static Boolean isFirstTimeSetFriends = false;
    public static ArrayList<CountDownTimer> threadsList = new ArrayList<>();
    public static ChatAndFriendsActivity currentChatAndFriends;
    public int counterOfChats = 0;
    public String keyOfLastChat;
    public Double valueOfLastChat;
    public int numberOfChatsPage;
    public static ProgressBar progressBarFriends;
    int numberOfTotalChatsArrived;
    Boolean isLastPage = false;
    Boolean addedSwipe = false;
    Boolean addedSwipeFriends = false;
    public static Boolean isOnChatPage = true;
    String lastMessageSeen;
    Boolean isAlreadyProcess = false;
    public static int NumberOfRefreshFriends = 0;
    private int dp56;
    public boolean shouldRequestPage = false;

    SwipeRefreshLayout mSwipeRefreshLayout;

    public ChatAndFriendsActivity() {
        currentFragment = this;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = inflater.getContext();
        v = inflater.inflate(R.layout.chat_and_friends_activity, container, false);

        // ?? Laur
        while (activity == null) {
            activity = getActivity();
        }

        currentChatAndFriends = this;
        threadsList.clear();
        friends.clear();
        isFirstTimeSetFriends = false;
        isOnChatPage = true;
        NumberOfRefreshFriends = 0;
        isFirstTimeSetChat = false;

        try {

            super.onCreate(savedInstanceState);
            Titles = new CharSequence[]{getResources().getString(R.string.conversations), getResources().getString(R.string.following1)};
            adapter = new ChatAndFriendsViewPagerAdapter(activity.getSupportFragmentManager(), Titles, Numboftabs);

            // Assigning ViewPager View and setting the adapter
            pager = (ViewPager) v.findViewById(R.id.pager);
            pager.setAdapter(adapter);

            // Assiging the Sliding Tab Layout View
            tabs = (SlidingTabLayout) v.findViewById(R.id.tabs);
            tabs.setDistributeEvenly(false); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width

            // Setting Custom Color for the Scroll bar indicator of the Tab View
            tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
                @Override
                public int getIndicatorColor(int position) {
                    return getResources().getColor(R.color.tabsScrollColor);
                }
            });

            // Setting the ViewPager For the SlidingTabsLayout
            tabs.setViewPager(pager);

            View vChat = inflater.inflate(R.layout.chat_tab, container, false);
            TextView noConversationTV = (TextView) vChat.findViewById(R.id.noConversationTV);
            TextView joinActivitiesTV = (TextView) vChat.findViewById(R.id.joinActivitiesTV);
            Button discoverActivitiesButton = (Button) vChat.findViewById(R.id.discoverActivitiesButton);
            ImageView chatImage = (ImageView) vChat.findViewById(R.id.chatImage);

            noConversationTV.setVisibility(View.INVISIBLE);
            joinActivitiesTV.setVisibility(View.INVISIBLE);
            discoverActivitiesButton.setVisibility(View.INVISIBLE);
            chatImage.setVisibility(View.INVISIBLE);

            DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
            connectedRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    boolean connected = snapshot.getValue(Boolean.class);
                    if (!connected) {
                        onInternetLost();
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                }
            });

            chatAdapter = new ConversationsAdapter(chatList, activity.getApplicationContext(), activity, getResources(), currentFragment);
            getConversations();

            mSwipeRefreshLayout = (SwipeRefreshLayout) vChat.findViewById(R.id.swipe_refreshChat);
            mSwipeRefreshLayout.setRefreshing(true);

            friendsAdapter = new FriendsAdapter(friends, activity, activity, getResources(), this);
            getFriends("0");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return v;
    }

    public void getConversations() {
        final DatabaseReference firebaseRef = FirebaseDatabase.getInstance().getReference();

        DatabaseReference currUser = firebaseRef.child("users").child(Persistance.getInstance().getUserInfo(activity).id);
        final DatabaseReference generalChats =  firebaseRef.child("chats");

        // Check if user has any chats
        currUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (!snapshot.child("chats").exists()) {
                    chatAdapter.setChatList(chatList);
                    setChatAdapter();
                    Persistance.getInstance().setConversation(activity, chatList);

                    addedSwipe = false;
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        final long currentDate = System.currentTimeMillis() / 1000L;

        // Get all the chat id's
        currUser.child("chats").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                final ArrayList<String> chatIds = new ArrayList<String>();

                for (final DataSnapshot uniqueKeySnapshot : dataSnapshot.getChildren()) {

                    String chatKey = uniqueKeySnapshot.getKey();
                    if( uniqueKeySnapshot.hasChild("blk") && uniqueKeySnapshot.child("blk").getValue().toString().equals("1")){
                        // chat is blocked
                    }
                    else {
                        chatIds.add(chatKey);
                    }

                    generalChats.child(chatKey).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(final DataSnapshot dataSnapshotChat) {
                            if (dataSnapshotChat.hasChild("last_message_date")) {
                                double lastMessageDate = Double.valueOf(dataSnapshotChat.child("last_message_date").getValue().toString());
                                String eventId = dataSnapshotChat.child("event_id").getValue() == null ? null : dataSnapshotChat.child("event_id").getValue().toString();

                                long difference = currentDate - (long)lastMessageDate;

                                // One week, no chat => archive
                                long maxAcceptedDiff =  60 * 60 * 24 * 7;

                                // Show the events one week after they happened
                                if(eventId == null || (eventId != null && difference < maxAcceptedDiff)) {
                                    final Chat chat = new Chat();
                                    chat.chatId = dataSnapshotChat.getKey();
                                    chat.lastMessageTime = Double.valueOf(dataSnapshotChat.child("last_message_date").getValue().toString());
                                    if (dataSnapshotChat.child("event_id").getValue() != null) {
                                        chat.eventId = dataSnapshotChat.child("event_id").getValue().toString();
                                    }

                                    // Get Chat name
                                    String names = "";
                                    if (chat.eventId != null) {
                                        names = "Group:";

                                        if (dataSnapshotChat.child("event_info").getValue() != null) {
                                            if (dataSnapshotChat.child("event_info").child("date").getValue() != null) {
                                                try {
                                                    chat.eventDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dataSnapshotChat.child("event_info").child("date").getValue().toString());
                                                } catch (ParseException e) {
                                                }
                                            }

                                            if (dataSnapshotChat.child("event_info").child("place_name").getValue() != null) {
                                                chat.eventLocation = dataSnapshotChat.child("event_info").child("place_name").getValue().toString();
                                            }

                                            if (dataSnapshotChat.child("event_info").child("sport_code").getValue() != null) {
                                                chat.eventSport = dataSnapshotChat.child("event_info").child("sport_code").getValue().toString();
                                            }

                                            if (chat.eventDate != null) {
                                                DateFormat df = new SimpleDateFormat("d MMM yyyy");

                                                names = df.format(chat.eventDate) + " - " + chat.eventSport + " ";
                                            }
                                        }
                                    }

                                    int counterOfNames = 0;

                                    if (chat.eventDate == null) {
                                        for (DataSnapshot users : dataSnapshotChat.child("users").getChildren()) {

                                            if (!users.getKey().equalsIgnoreCase(Persistance.getInstance().getUserInfo(activity).id)) {
                                                if (counterOfNames == 0) {
                                                    if (users.hasChild("name") && users.child("name").getValue().toString().trim().compareToIgnoreCase("") != 0) {
                                                        names += users.child("name").getValue().toString() + ",";
                                                        counterOfNames++;
                                                    } else {
                                                        counterOfNames++;
                                                    }
                                                } else if (counterOfNames == 1) {
                                                    if (users.hasChild("name") && users.child("name").getValue().toString().trim().compareToIgnoreCase("") != 0) {
                                                        if (dataSnapshot.child("users").getChildrenCount() > 3) {
                                                            names += users.child("name").getValue().toString() + "..";
                                                        } else {
                                                            names += users.child("name").getValue().toString() + ",";
                                                        }
                                                        counterOfNames++;
                                                    } else {
                                                        counterOfNames++;
                                                    }
                                                }
                                                if (users.hasChild("image")) {
                                                    chat.image.add(users.child("image").getValue().toString());
                                                } else {
                                                    chat.image.add("");
                                                }
                                                chat.otherParticipantId.add(users.getKey().toString());
                                            }
                                        }
                                    }

                                    chat.participantName = names;
                                    chat.lastMessage = "test";

                                    DatabaseReference lastMessageRef = dataSnapshotChat.child("messages").getRef();
                                    Query lastMessage = lastMessageRef.orderByKey().limitToLast(1);
                                    lastMessage.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshotM) {
                                            for (DataSnapshot childMsg : dataSnapshotM.getChildren()) {
                                                chat.lastMessage = childMsg.child("message").getValue().toString();
                                                chat.lastMessageId = childMsg.getKey().toString();

                                                if( uniqueKeySnapshot.hasChild("blk") && uniqueKeySnapshot.child("blk").getValue().toString().equals("1")) {

                                                }else {
                                                    chatList.add(chat);
                                                    chatIds.remove(dataSnapshotChat.getKey());
                                                }

                                                if (chatIds.size() == 0) {
                                                    System.out.println("hoooray");

                                                    Collections.sort(chatList, new ChatComparator());

                                                    chatAdapter.setChatList(chatList);
                                                    setChatAdapter();
                                                    Persistance.getInstance().setConversation(activity, chatList);

                                                    addedSwipe = false;
                                                    mSwipeRefreshLayout.setRefreshing(false);
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

                                    if (!dataSnapshotChat.hasChild("messages")) {
                                        chatIds.remove(dataSnapshotChat.getKey());
                                    }
                                }
                                else{
                                    chatIds.remove(dataSnapshotChat.getKey());
                                }
                            }
                            else {
                                chatIds.remove(dataSnapshotChat.getKey());
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public void setChatAdapter() {
        try {
            chatAdapter.notifyDataSetChanged();

            mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refreshChat);
            chatListView = (ListView) v.findViewById(R.id.chat_listView);

            if (!isFirstTimeSetChat) {
                chatListView.setAdapter(chatAdapter);
                isFirstTimeSetChat = true;
            }
            TextView noConversationTV = (TextView) v.findViewById(R.id.noConversationTV);
            TextView joinActivitiesTV = (TextView) v.findViewById(R.id.joinActivitiesTV);
            Button discoverActivitiesButton = (Button) v.findViewById(R.id.discoverActivitiesButton);
            ImageView chatImage = (ImageView) v.findViewById(R.id.chatImage);

            ArrayList<Chat> currentList = chatAdapter.getChatList();
            if (currentList.size() == 0) {
                noConversationTV.setVisibility(View.VISIBLE);
                joinActivitiesTV.setVisibility(View.VISIBLE);
                discoverActivitiesButton.setVisibility(View.VISIBLE);
                discoverActivitiesButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bottomBar.setDefaultTab(R.id.tab_activities);
                    }
                });
                chatImage.setVisibility(View.VISIBLE);
                chatListView.setVisibility(View.INVISIBLE);
            } else {
                noConversationTV.setVisibility(View.INVISIBLE);
                joinActivitiesTV.setVisibility(View.INVISIBLE);
                discoverActivitiesButton.setVisibility(View.INVISIBLE);
                chatImage.setVisibility(View.INVISIBLE);
                chatListView.setVisibility(View.VISIBLE);
            }

            addedSwipe = false;


            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    if (!addedSwipe) {
                        chatListClone = (ArrayList) chatList.clone();
                        chatList.clear();

                        counterOfChats = 0;

                        chatAdapter.setChatList(chatListClone);
                        chatAdapter.notifyDataSetChanged();

                        addedSwipe = false;
                        mSwipeRefreshLayout.setRefreshing(true);
                        getConversations();
                        addedSwipe = true;
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setFriendsAdapter() {
        try {
            friendsAdapter.notifyDataSetChanged();
            final SwipeRefreshLayout mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refreshFriends);
            friendsListView = (ListView) v.findViewById(R.id.friends_listView);
            progressBarFriends = (ProgressBar) v.findViewById(R.id.progressBarFriends);
            progressBarFriends.setAlpha(0f);

            if (!isFirstTimeSetFriends) {
                friendsListView.setAdapter(friendsAdapter);
                isFirstTimeSetFriends = true;
            }

            TextView noFriendsTV = (TextView) v.findViewById(R.id.noFriendsTV);
            TextView joinActivitiesFriendsTV = (TextView) v.findViewById(R.id.joinActivitiesFriendsTV);
            Button discoverActivitiesFriendsButton = (Button) v.findViewById(R.id.discoverActivitiesFriendsButton);
            ImageView friendsImage = (ImageView) v.findViewById(R.id.friendsImage);

            if (friends.size() == 0) {
                noFriendsTV.setVisibility(View.VISIBLE);
                joinActivitiesFriendsTV.setVisibility(View.VISIBLE);
                discoverActivitiesFriendsButton.setVisibility(View.VISIBLE);
                discoverActivitiesFriendsButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bottomBar.setDefaultTab(R.id.tab_activities);
                    }
                });
                friendsImage.setVisibility(View.VISIBLE);
            } else {
                noFriendsTV.setVisibility(View.INVISIBLE);
                joinActivitiesFriendsTV.setVisibility(View.INVISIBLE);
                discoverActivitiesFriendsButton.setVisibility(View.INVISIBLE);
                friendsImage.setVisibility(View.INVISIBLE);
            }
            if (friendsListView != null) {
                friendsListView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(final View v, MotionEvent event) {
                        if (v != null && friendsListView.getChildCount() > 0) {
                            if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
                                if (friendsListView.getLastVisiblePosition() == friendsListView.getAdapter().getCount() - 1 &&
                                        friendsListView.getChildAt(friendsListView.getChildCount() - 1).getBottom() <= friendsListView.getHeight()) {
                                    progressBarFriends.setAlpha(1f);
                                    getFriends(String.valueOf(NumberOfRefreshFriends));
                                }
                            }
                        }
                        return false;
                    }
                });
            }

            if (!addedSwipeFriends) {
                mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        addedSwipeFriends = false;
                        NumberOfRefreshFriends = 0;
                        mSwipeRefreshLayout.setRefreshing(false);
                        getFriends("0");
                    }
                });
                addedSwipeFriends = true;

                int last = friendsListView.getLastVisiblePosition();
                int count = friendsAdapter.getCount();
                if (last + 1 < count) {
                    friendsListView.smoothScrollToPosition(last + 1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getFriends(String pageNumber) {
        HashMap<String, String> params = new HashMap<String, String>();
        HashMap<String, String> headers = new HashMap<String, String>();
        HashMap<String, String> urlParams = new HashMap<String, String>();
        headers.put("authKey", Persistance.getInstance().getUserInfo(activity).authKey);
        urlParams.put("userId", Persistance.getInstance().getUserInfo(activity).id);
        urlParams.put("pageNumber", pageNumber);

        HTTPResponseController.getInstance().getFriends(params, headers, activity, urlParams, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (chatAdapter != null) chatAdapter.notifyDataSetChanged();
        if (friendsAdapter != null) friendsAdapter.notifyDataSetChanged();
    }

    private void onInternetRefresh() {
        addedSwipeFriends = false;
        NumberOfRefreshFriends = 0;
        SwipeRefreshLayout mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refreshFriends);
        mSwipeRefreshLayout.setRefreshing(false);
        getFriends("0");

        for (int i = 0; i < threadsList.size(); i++) {
            threadsList.get(i).cancel();
        }
        threadsList.clear();
        counterOfChats = 0;
        keyOfLastChat = null;
        valueOfLastChat = 0.0;
        numberOfChatsPage = 0;
        numberOfTotalChatsArrived = 0;
        isLastPage = false;
        addedSwipe = false;
        mSwipeRefreshLayout.setRefreshing(false);
    }

    private void onInternetLost() {
        v.findViewById(R.id.internetRefresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setOnClickListener(null);
                onInternetRefresh();
            }
        });
        RelativeLayout ll = (RelativeLayout) v.findViewById(R.id.noInternetLayout);

        ll.getLayoutParams().height = this.dp56;
        ll.setLayoutParams(ll.getLayoutParams());
    }

    public String trimMessage(String json, String key) {
        String trimmedString = null;

        try {
            JSONObject obj = new JSONObject(json);
            trimmedString = obj.getString(key);
            if(trimmedString.equalsIgnoreCase("Invalid Auth Key provided.")){
                deleteCachedInfo();
                Intent intent =new Intent(activity,LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
                activity.startActivity(intent);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        return trimmedString;
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        if(error.getMessage() != null) {
            if (error.getMessage().contains("error")) {
                String json = trimMessage(error.getMessage(), "error");
                if (json != null) {
                    Toast.makeText(super.getContext(), json, Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(super.getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
        Log.d("Response", error.toString());
        if (error instanceof NetworkError) {
            this.onInternetLost();
        }
    }
}
