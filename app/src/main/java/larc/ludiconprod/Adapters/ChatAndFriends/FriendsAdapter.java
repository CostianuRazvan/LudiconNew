package larc.ludiconprod.Adapters.ChatAndFriends;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import larc.ludiconprod.Activities.ChatActivity;
import larc.ludiconprod.Activities.ChatAndFriendsActivity;
import larc.ludiconprod.Activities.UserProfileActivity;
import larc.ludiconprod.R;
import larc.ludiconprod.Utils.Friend;

import static larc.ludiconprod.Activities.ChatAndFriendsActivity.isOnChatPage;

/**
 * Created by ancuta on 8/18/2017.
 */

public class FriendsAdapter extends BaseAdapter implements ListAdapter {
    public static Bitmap decodeBase64(String input) {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    class ViewHolder {

        CircleImageView friendsImage;
        TextView friendsName;
        TextView friendsLevel;
        TextView friendsMutualFriends;
        Button chatFriends;


    }

    private ArrayList<Friend> list = new ArrayList<>();
    private Context context;
    private Activity activity;
    private Resources resources;
    private ChatAndFriendsActivity fragment;
    final ListView listView;

    public FriendsAdapter(ArrayList<Friend> list, Context context, Activity activity, Resources resources, ChatAndFriendsActivity fragment) {
        this.list = list;
        this.context = context;
        this.activity = activity;
        this.resources = resources;
        this.fragment = fragment;

        this.listView = (ListView) activity.findViewById(R.id.events_listView2); // era v.
    }

    public void setListOfEvents(ArrayList<Friend> newList) {
        this.list = newList;
        this.notifyDataSetChanged();
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


        if (list.size() > 0) {
            final FriendsAdapter.ViewHolder holder;

            final Friend currentFriend = list.get(position);

            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.friends_list_card, null);
                holder = new FriendsAdapter.ViewHolder();
                holder.friendsImage = (CircleImageView) view.findViewById(R.id.friendsImage);
                holder.friendsName = (TextView) view.findViewById(R.id.friendsName);
                holder.friendsLevel = (TextView) view.findViewById(R.id.friendsLevel);
                holder.friendsMutualFriends = (TextView) view.findViewById(R.id.friendsMutualFriends);
                holder.chatFriends = (Button) view.findViewById(R.id.chatFriends);

                Typeface typeFace = Typeface.createFromAsset(fragment.getActivity().getAssets(), "fonts/Quicksand-Medium.ttf");
                Typeface typeFaceBold = Typeface.createFromAsset(fragment.getActivity().getAssets(), "fonts/Quicksand-Bold.ttf");

                holder.friendsName.setTypeface(typeFace);
                holder.friendsLevel.setTypeface(typeFace);
                holder.friendsMutualFriends.setTypeface(typeFace);
                holder.chatFriends.setTypeface(typeFaceBold);

                view.setTag(holder);
            } else {
                holder = (FriendsAdapter.ViewHolder) view.getTag();
            }
            //clear layout
            holder.friendsImage.setImageResource(R.drawable.ph_user);

            if (!currentFriend.profileImage.equalsIgnoreCase("")) {
                Bitmap bitmap = decodeBase64(currentFriend.profileImage);
                holder.friendsImage.setImageBitmap(bitmap);
            }
            holder.friendsName.setText(currentFriend.userName);
            holder.friendsLevel.setText(String.valueOf(currentFriend.level));
            if (currentFriend.numberOfMutuals != 0) {
                if (currentFriend.numberOfMutuals == 1) {
                    holder.friendsMutualFriends.setText(currentFriend.numberOfMutuals + " " + activity.getResources().getString(R.string.mutual_follower));
                } else {
                    holder.friendsMutualFriends.setText(currentFriend.numberOfMutuals + " " + activity.getResources().getString(R.string.mutual_followers));
                }
            } else {
                holder.friendsMutualFriends.setText(R.string.no_mutual_followings);
            }
            final View currView = view;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    currView.setBackgroundColor(Color.parseColor("#f5f5f5"));
                    Intent intent = new Intent(activity, UserProfileActivity.class);
                    intent.putExtra("UserId", currentFriend.userID);
                    isOnChatPage = false;
                    activity.startActivity(intent);
                    //currView.setBackgroundColor(Color.parseColor("#f7f9fc"));
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            currView.setBackgroundColor(Color.parseColor("#f7f9fc"));
                        }
                    }, 1000);
                }
            });
            holder.chatFriends.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(activity, ChatActivity.class);
                    intent.putExtra("otherParticipantName", currentFriend.userName + ",");
                    ArrayList<String> myList = new ArrayList<String>();
                    myList.add(currentFriend.profileImage);
                    intent.putExtra("otherParticipantImage", myList);
                    intent.putExtra("chatId", "isNot");
                    intent.putExtra("groupChat", 0);
                    intent.putExtra("UserId", currentFriend.userID);
                    ArrayList<String> userIdList = new ArrayList<String>();
                    userIdList.add(currentFriend.userID);
                    intent.putExtra("otherParticipantId", userIdList);
                    activity.startActivity(intent);
                    isOnChatPage = false;
                }
            });


        }
        return view;
    }


}
