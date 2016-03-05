package larc.ludicon.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

import larc.ludicon.R;
import larc.ludicon.UserInfo.User;

public class ChatListActivity extends AppCompatActivity {

    private static final String FIREBASE_URL = "https://ludicon.firebaseio.com/";


    public class Chat1to1{
        String userUID;
        String chatID;
        String userName;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        Firebase firebaseRef = new Firebase(FIREBASE_URL).child("users").child(User.uid).child("chats");
        firebaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                List<Chat1to1> chatList = new ArrayList<>();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Chat1to1 chat = new Chat1to1();
                    chat.userUID = data.getKey().toString();
                    chat.chatID = data.getValue().toString();
                    chatList.add(chat);
                }
                MyCustomAdapter adapter = new MyCustomAdapter(chatList, getApplicationContext());
                ListView listView = (ListView) findViewById(R.id.chat_list);
                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }
    public class MyCustomAdapter extends BaseAdapter implements ListAdapter {

        private List<Chat1to1> list = new ArrayList<>();
        private Context context;

        public MyCustomAdapter(List<Chat1to1> list, Context context) {
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
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.chat1to1_layout, null);
            }

            final TextView textName = (TextView) view.findViewById(R.id.friend_name);
            final ImageView imageView = (ImageView) view.findViewById(R.id.friend_photo);
            Button chatButton = (Button) view.findViewById(R.id.gotoChat);

            // Set friend's name
            Firebase firebaseRef = new Firebase(FIREBASE_URL).child("users").child(list.get(position).userUID).child("name");
            firebaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    textName.setText(snapshot.getValue().toString());
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                }
            });

            // Set Friend Image
            Firebase userRef = User.firebaseRef.child("users").child(list.get(position).userUID).child("profileImageURL");
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if(snapshot.getValue() != null) {
                            new DownloadImageTask(imageView).execute(snapshot.getValue().toString());
                    }
                    else
                        imageView.setImageResource(R.drawable.logo);
                }
                @Override
                public void onCancelled(FirebaseError firebaseError) {
                }
            });

            // Buttons behaviour
            chatButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {

                    Firebase userRef = User.firebaseRef.child("users").child(list.get(position).userUID).child("name");
                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                                //textName.setText(snapshot.getValue().toString());
                                Intent intent = new Intent(getApplicationContext(), ChatTemplateActivity.class);
                                intent.putExtra("uid", list.get(position).userUID);
                                intent.putExtra("firstConnection", false);
                                //intent.putExtra("otherName", snapshot.getValue().toString());
                                intent.putExtra("chatID",list.get(position).chatID);
                                startActivity(intent);
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {
                        }
                    });
                }
            });
            return view;
        }
    }
    // Method which downloads Image from URL
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
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
}