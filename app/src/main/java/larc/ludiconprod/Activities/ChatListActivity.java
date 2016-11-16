package larc.ludiconprod.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import larc.ludiconprod.Adapters.LeftPanelItemClicker;
import larc.ludiconprod.Adapters.LeftSidePanelAdapter;
import larc.ludiconprod.R;
import larc.ludiconprod.UserInfo.User;
import larc.ludiconprod.Utils.util.ChatNotifier;
import larc.ludiconprod.Utils.util.DateManager;

public class ChatListActivity extends Activity {

    // Left side panel
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private ProgressDialog dialog;
    private int TIMEOUT = 1;
    public static boolean isForeground = false;

    private static final String FIREBASE_URL = "https://ludicon.firebaseio.com/";

    public class Chat1to1{
        String userUID;
        String chatID;
        String userName;
        String friendPhoto;
        String lastTimeOnline;
        String lastMessageText;
        String lastMessageAuthor;
    }
    @Override
    public void onStart() {
        super.onStart();
        isForeground = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        isForeground = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        isForeground = true;
    }

    Object waitForFriends = new Object();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_chat_list);

        // Left side panel initializing
        mDrawerList = (ListView) findViewById(R.id.leftMenu);
        initializeLeftSidePanel();

        User.setImage();

        dialog = ProgressDialog.show(ChatListActivity.this, "", "Loading. Please wait", true);

        // User picture and name for HEADER MENU
        TextView userName = (TextView) findViewById(R.id.userName);
        userName.setText(User.getFirstName(getApplicationContext()) + " " + User.getLastName(getApplicationContext()));

        final ImageButton createNewChat = (ImageButton)findViewById(R.id.header_button);
        createNewChat.setVisibility(View.VISIBLE);
        createNewChat.setBackgroundResource(R.drawable.admin_add2);

        createNewChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent friendListIntent = new Intent(getApplicationContext(), FriendsActivity.class);
                ChatListActivity.this.startActivity(friendListIntent);
            }
        });

        TextView hello_message = (TextView) findViewById(R.id.hello_message_activity);
        hello_message.setText("");
        ImageView userPic = (ImageView) findViewById(R.id.userPicture);
        Drawable d = new BitmapDrawable(getResources(), User.image);
        userPic.setImageDrawable(d);
        userPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                ChatListActivity.this.startActivity(mainIntent);
            }
        });


        // Delete chat notifications
        ChatNotifier chatNotifier = new ChatNotifier();
        synchronized (chatNotifier.lock) {
            for (int i = chatNotifier.chatNotificationFirstIndex; i <= chatNotifier.chatNotificationIndex; ++i) {
                chatNotifier.deleteNotification(getSystemService(NOTIFICATION_SERVICE), i);
            }
            chatNotifier.chatNotificationIndex = 0;
            chatNotifier.chatNotificationFirstIndex = 0;
        }


        DatabaseReference firebaseRef = FirebaseDatabase.getInstance().getReference().child("users").child(User.uid).child("chats");
        firebaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                final List<Chat1to1> chatList = new ArrayList<>();
                final long size = snapshot.getChildrenCount();
                for (DataSnapshot data : snapshot.getChildren()) {
                    final Chat1to1 chat = new Chat1to1();
                    chat.userUID = data.getKey().toString();
                    chat.chatID = data.getValue().toString();

                    DatabaseReference chatRef = User.firebaseRef.child("chat").child(chat.chatID).child("Messages");
                    chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            for(DataSnapshot data : snapshot.getChildren())
                            {
                                for(DataSnapshot snap : data.getChildren())
                                {
                                    if (snap.getKey().toString().equalsIgnoreCase("author"))
                                        chat.lastMessageAuthor = snap.getValue().toString().split(" ")[0];
                                    if (snap.getKey().toString().equalsIgnoreCase("message"))
                                        chat.lastMessageText = snap.getValue().toString();
                                }
                            }
                            getChatInfo(chatList,chat,size);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }




                // Dismiss loading dialog after  2 * TIMEOUT * chatList.size() ms
                Timer timer = new Timer();
                TimerTask delayedThreadStartTask = new TimerTask() {
                    @Override
                    public void run() {

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                dialog.dismiss();
                            }
                        }).start();
                    }
                };
                timer.schedule(delayedThreadStartTask, TIMEOUT * 6 * chatList.size());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });



    }

    public void getChatInfo(final List<Chat1to1> chatList, final Chat1to1 chat, final long size)
    {
        DatabaseReference firebaseRef = FirebaseDatabase.getInstance().getReference().child("users").child(chat.userUID);
        firebaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                for (DataSnapshot data : snapshot.getChildren()) {
                    if ((data.getKey()).compareTo("name") == 0) {
                        String name = data.getValue().toString();
                        chat.userName = name;
                    }
                    if ((data.getKey()).compareTo("profileImageURL") == 0)
                        if (data.getValue() != null) {
                            //new DownloadImageTask(imageView).execute(data.getValue().toString());
                            chat.friendPhoto = data.getValue().toString();
                        } else {
                            chat.friendPhoto = "";
                        }

                    if  ((data.getKey()).compareTo("lastLogInTime") == 0)
                        if (data.getValue() != null) {
                            chat.lastTimeOnline= DateManager.convertFromSecondsToText((long)data.getValue());
                        }
                        else{
                            chat.lastTimeOnline="";
                        }
                }

                chatList.add(chat);
                if(chatList.size()==size){
                    MyCustomAdapter adapter = new MyCustomAdapter(chatList, getApplicationContext());
                    ListView listView = (ListView) findViewById(R.id.chat_list);
                    listView.setAdapter(adapter);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }


    public class MyCustomAdapter extends BaseAdapter implements ListAdapter {

        private List<Chat1to1> list = new ArrayList<>();
        private Context context;
        //private final Map<String,Boolean> states = new HashMap<String,Boolean>();


        class ViewHolder {
             TextView textName;
             ImageView imageView;
             Button chatButton;
             TextView lastLoginView;
             TextView lastMessage;
        };


        public MyCustomAdapter(List<Chat1to1> list, Context context) {
            //list.remove(0);
            this.list = list;
            this.context = context;
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
        public View getView(final int position, View convertView, ViewGroup parent) {

            View view = convertView;
            ViewHolder holder = null;
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.chat1to1_layout, null);
                holder = new ViewHolder();

                holder.textName = (TextView) view.findViewById(R.id.friend_name);
                holder.imageView = (ImageView) view.findViewById(R.id.friend_photo);
                holder.lastLoginView = (TextView) view.findViewById(R.id.lastOnline);
                holder.lastMessage = (TextView) view.findViewById(R.id.lastMessage);
                //holder.chatButton = (Button) view.findViewById(R.id.gotoChat);

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        DatabaseReference userRef = User.firebaseRef.child("users").child(list.get(position).userUID).child("name");
                        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                Intent intent = new Intent(getApplicationContext(), ChatTemplateActivity.class);
                                intent.putExtra("uid", list.get(position).userUID);
                                intent.putExtra("firstConnection", false);
                                intent.putExtra("chatID", list.get(position).chatID);

                                startActivity(intent);
                            }

                            @Override
                            public void onCancelled(DatabaseError firebaseError) {
                            }
                        });
                    }
                });

                view.setTag(holder);
            }
            else {
                holder = (ViewHolder)view.getTag();
            }
            String lastMessageAuthor = "";
            if (!list.get(position).lastMessageAuthor.equalsIgnoreCase("Ludicon")) {
                if (!list.get(position).userName.split(" ")[0].equalsIgnoreCase(list.get(position).lastMessageAuthor))
                    lastMessageAuthor = "You";
                else lastMessageAuthor = list.get(position).lastMessageAuthor;
                holder.lastMessage.setText(lastMessageAuthor + ": " + list.get(position).lastMessageText);
            }
            else
            {
                holder.lastMessage.setText("");
            }
            holder.textName.setText(list.get(position).userName);

            String lastOnline = list.get(position).lastTimeOnline;
            if(lastOnline != null) {
                lastOnline = lastOnline.replace(",", "");
                lastOnline = lastOnline.replace("PM", "");
                lastOnline = lastOnline.replace("AM", "");

                holder.lastLoginView.setText(lastOnline);
            }
            else{
                holder.lastLoginView.setText("Unknown");
            }

            if (list.get(position).friendPhoto != "") {
                //new DownloadImageTask(imageView).execute(data.getValue().toString());
                Picasso.with(context).load(list.get(position).friendPhoto).into(holder.imageView);
                holder.imageView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                        intent.putExtra("uid", list.get(position).userUID);
                        startActivity(intent);
                    }
                });
            } else {
                holder.imageView.setImageResource(R.drawable.logo);
            }

            return view;
        }
    }


    // Left side menu

    public void initializeLeftSidePanel() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_chats);
        mDrawerList = (ListView) findViewById(R.id.leftMenu);

        // Set the adapter for the list view
        mDrawerList.setAdapter(new LeftSidePanelAdapter(this, ChatListActivity.this));
        // Set the list's click listener
        LeftPanelItemClicker.OnItemClick(mDrawerList, getApplicationContext(), ChatListActivity.this);

        final ImageButton showPanel = (ImageButton) findViewById(R.id.showPanel);
        showPanel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                mDrawerLayout.openDrawer(Gravity.LEFT);
            }
        });

        // Toggle efect on left side panel
        mDrawerToggle = new android.support.v4.app.ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Delete the history stack and point to Main activity
    @Override
    public void onBackPressed() {
        Intent toMain = new Intent(this,MainActivity.class);
        toMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(toMain);
    }

}
