package larc.ludiconprod.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Base64;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import larc.ludiconprod.Activities.ActivityDetailsActivity;
import larc.ludiconprod.Activities.InviteFriendsActivity;
import larc.ludiconprod.Activities.UserProfileActivity;
import larc.ludiconprod.Controller.HTTPResponseController;
import larc.ludiconprod.Controller.Persistance;
import larc.ludiconprod.R;
import larc.ludiconprod.Utils.Friend;

/**
 * Created by ancuta on 8/4/2017.
 */

public class InviteFriendsAdapter extends BaseAdapter implements ListAdapter {

    public static Bitmap decodeBase64(String input) {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    class ViewHolder {

        CircleImageView friendProfileImage;
        TextView friendName;
        TextView friendLevel;
        Button inviteButton;


    }

    private ArrayList<Friend> list = new ArrayList<>();
    private Context context;
    private Activity activity;
    final ListView listView;
    private Resources resources;
    private InviteFriendsActivity fragment;

    public InviteFriendsAdapter(ArrayList<Friend> list, Context context, Activity activity, Resources resources, InviteFriendsActivity fragment) {
        this.list = list;
        this.context = context;
        this.activity = activity;
        this.resources = resources;
        this.fragment = fragment;

        this.listView = (ListView) activity.findViewById(R.id.events_listView2); // era v.
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (list.size() > 0) {

            final InviteFriendsAdapter.ViewHolder holder;

            final Friend currentFriend = list.get(position);
            // Initialize the view
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.invite_friends_card, null);


                holder = new InviteFriendsAdapter.ViewHolder();
                final Typeface typeFace = Typeface.createFromAsset(activity.getAssets(),
                        "fonts/Quicksand-Medium.ttf");
                final Typeface typeFaceBold = Typeface.createFromAsset(activity.getAssets(), "fonts/Quicksand-Bold.ttf");
                holder.friendProfileImage = (CircleImageView) view.findViewById(R.id.profileImageInvitedFriend);
                holder.friendName = (TextView) view.findViewById(R.id.friendName);
                holder.friendName.setTypeface(typeFace);
                holder.friendLevel = (TextView) view.findViewById(R.id.friendLevel);
                holder.friendLevel.setTypeface(typeFace);
                holder.inviteButton = (Button) view.findViewById(R.id.inviteFriendButton);
                holder.inviteButton.setTypeface(typeFaceBold);
                view.setTag(holder);
            } else {
                holder = (InviteFriendsAdapter.ViewHolder) view.getTag();
            }

            view.setBackgroundColor(Color.parseColor("#f7f9fc"));
            holder.friendProfileImage.setOnClickListener(null);
            view.setOnClickListener(null);



            final View currView = view;
            if (currentFriend.numberOfOffliners != -1) {

                if (!currentFriend.profileImage.equals("")) {
                    Bitmap bitmap = decodeBase64(currentFriend.profileImage);
                    holder.friendProfileImage.setImageBitmap(bitmap);
                } else {
                    holder.friendProfileImage.setImageResource(R.drawable.ph_user);
                }
                ViewGroup.LayoutParams params = holder.friendLevel.getLayoutParams();
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                holder.friendLevel.setLayoutParams(params);
                if (currentFriend.userName.length() < 16) {
                    holder.friendName.setText(currentFriend.userName);
                } else {
                    String names[] = currentFriend.userName.split(" ");
                    String displayName="";
                    String auxString;
                    Boolean nextRow = false;
                    for (int i = 0; i < names.length; i++) {
                        auxString = displayName + names[i];
                        if(auxString.length() < 16) {
                            if(i == 0) {
                                displayName = displayName + names[i];
                            }
                            else {
                                displayName = displayName + " " + names[i];
                            }
                        }
                        else {
                            if(!nextRow){

                                displayName = displayName + "\n" + names[i];
                                nextRow = true;
                            }else{
                                displayName = displayName + " " + names[i];
                            }
                        }

//                        if( i == 1) {
//                            displayName = displayName + names[i];
//                        }else{
//                            displayName = displayName + " " + names[i];
//                        }

                    }
                    holder.friendName.setText(displayName);
                }

                if (!currentFriend.userID.equals(Persistance.getInstance().getUserInfo(activity).id) && Persistance.getInstance().getUserInfo(activity).id.equals(ActivityDetailsActivity.creatorID )) {

                    holder.inviteButton.setText(R.string.remove);
                    holder.inviteButton.setBackgroundResource(R.drawable.green_button_selector);
                    holder.inviteButton.setVisibility(View.VISIBLE);
                    holder.inviteButton.setEnabled(true);
                    holder.inviteButton.setTextColor(Color.parseColor("#ffffff"));
                    holder.inviteButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            HashMap<String, String> params = new HashMap<String, String>();
                            HashMap<String, String> headers = new HashMap<String, String>();
                            headers.put("authKey", Persistance.getInstance().getUserInfo(activity).authKey);

                            //set urlParams

                            params.put("eventId", ActivityDetailsActivity.eventID);
                            params.put("userId", currentFriend.userID);

                            HTTPResponseController.getInstance().kickUser(params, headers, activity, position);

                        }
                    });
                } else {
                    holder.inviteButton.setVisibility(View.INVISIBLE);
                    holder.inviteButton.setEnabled(false);
                }


            } else
                if (position == 0) {
                    ViewGroup.LayoutParams params = holder.friendLevel.getLayoutParams();
                    params.height = 0;
                    holder.friendLevel.setLayoutParams(params);
                    holder.friendName.setText(currentFriend.userName);
                    holder.friendProfileImage.setImageResource(R.drawable.ic_invite);
                    holder.friendName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    final Typeface typeFaceBold = Typeface.createFromAsset(activity.getAssets(), "fonts/Quicksand-Bold.ttf");
                    holder.friendName.setTypeface(typeFaceBold);


                    holder.inviteButton.setVisibility(View.INVISIBLE);
                    holder.inviteButton.setEnabled(true);

                    holder.friendName.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Friend friend = new Friend();
                            if( Locale.getDefault().getLanguage().startsWith("en")) {
                                friend.userName = Persistance.getInstance().getUserInfo(activity).lastName + activity.getResources().getString(R.string.is_friend);
                            } else if (Locale.getDefault().getLanguage().startsWith("ro")){
                                friend.userName = activity.getResources().getString(R.string.is_friend) + Persistance.getInstance().getUserInfo(activity).lastName;
                            }else if (Locale.getDefault().getLanguage().startsWith("fr")){
                                friend.userName = activity.getResources().getString(R.string.is_friend) + Persistance.getInstance().getUserInfo(activity).lastName;
                            }
                            friend.offlineFriend = true;
                            friend.profileImage = "";
                            InviteFriendsActivity.friendsList.add(1, friend);
                            InviteFriendsActivity.numberOfOfflineFriends++;
                            InviteFriendsActivity.inviteFriendsAdapter.notifyDataSetChanged();
                        }
                    });

                    holder.friendProfileImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Friend friend = new Friend();
                            if( Locale.getDefault().getLanguage().startsWith("en")) {
                                friend.userName = Persistance.getInstance().getUserInfo(activity).lastName + activity.getResources().getString(R.string.is_friend);
                            } else if (Locale.getDefault().getLanguage().startsWith("ro")){
                                friend.userName = activity.getResources().getString(R.string.is_friend) + Persistance.getInstance().getUserInfo(activity).lastName;
                            }else if (Locale.getDefault().getLanguage().startsWith("fr")){
                                friend.userName = activity.getResources().getString(R.string.is_friend) + Persistance.getInstance().getUserInfo(activity).lastName;
                            }
                            friend.offlineFriend = true;
                            friend.profileImage = "";
                            InviteFriendsActivity.friendsList.add(1, friend);
                            InviteFriendsActivity.numberOfOfflineFriends++;
                            InviteFriendsActivity.inviteFriendsAdapter.notifyDataSetChanged();
                        }
                    });

                } else
                    if (currentFriend.offlineFriend) {
                        ViewGroup.LayoutParams params = holder.friendLevel.getLayoutParams();
                        params.height = 0;
                        holder.friendLevel.setLayoutParams(params);

                        if (currentFriend.userName.length() < 16) {
                            holder.friendName.setText(currentFriend.userName);
                        } else {
                            String names[] = currentFriend.userName.split(" ");
                            String displayName = names[0] + "\n";
                            for (int i = 1; i < names.length; i++) {
                                displayName = displayName + names[i];

                            }
                            holder.friendName.setText(displayName);
                        }
                        holder.friendProfileImage.setImageResource(R.drawable.ph_user);

                        holder.inviteButton.setText(R.string.remove);
                        holder.inviteButton.setBackgroundResource(R.drawable.green_button_selector);
                        holder.inviteButton.setVisibility(View.VISIBLE);
                        holder.inviteButton.setEnabled(true);
                        holder.inviteButton.setTextColor(Color.parseColor("#ffffff"));
                        if (currentFriend.isOfflineParticipant && !Persistance.getInstance().getUserInfo(activity).id.equals(ActivityDetailsActivity.creatorID) && !currentFriend.userID.equals(Persistance.getInstance().getUserInfo(activity).id)) {
                            holder.inviteButton.setVisibility(View.INVISIBLE);
                            holder.inviteButton.setEnabled(false);
                        }
                        holder.inviteButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (!currentFriend.isOfflineParticipant) {
                                    InviteFriendsActivity.numberOfOfflineFriends--;
                                    InviteFriendsActivity.friendsList.remove(position);
                                    InviteFriendsActivity.inviteFriendsAdapter.notifyDataSetChanged();
                                } else {
                                    HashMap<String, String> params = new HashMap<String, String>();
                                    HashMap<String, String> headers = new HashMap<String, String>();
                                    headers.put("authKey", Persistance.getInstance().getUserInfo(activity).authKey);

                                    //set urlParams

                                    params.put("eventId", ActivityDetailsActivity.eventID);
                                    params.put("userId", currentFriend.userID);
                                    int numberOfOfflineFriends = -1;
                                    for (int i = 0; i < InviteFriendsActivity.participantList.size(); i++) {
                                        if (InviteFriendsActivity.participantList.get(i).userID.equals(currentFriend.userID)) {
                                            numberOfOfflineFriends++;
                                        }
                                    }
                                    params.put("numberOfOffliners", String.valueOf(1));
                                    params.put("action", "0");

                                    HTTPResponseController.getInstance().removeOffline(params, headers, activity, position);
                                }
                            }
                        });

                    } else
                        if (!currentFriend.offlineFriend) {
                            if (!currentFriend.profileImage.equals("")) {
                                Bitmap bitmap = decodeBase64(currentFriend.profileImage);
                                holder.friendProfileImage.setImageBitmap(bitmap);
                            }

                            ViewGroup.LayoutParams params = holder.friendLevel.getLayoutParams();
                            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                            holder.friendLevel.setLayoutParams(params);
                            if (currentFriend.isAlreadyInvited == 1) {
                                holder.inviteButton.setVisibility(View.INVISIBLE);
                                holder.inviteButton.setEnabled(false);

                            } else
                                if (!currentFriend.isInvited) {
                                    holder.inviteButton.setBackgroundResource(R.drawable.green_button_selector);
                                    holder.inviteButton.setText(R.string.invite);
                                    holder.inviteButton.setEnabled(true);
                                    holder.inviteButton.setVisibility(View.VISIBLE);
                                    holder.inviteButton.setTextColor(Color.parseColor("#ffffff"));

                                } else {
                                    holder.inviteButton.setBackgroundResource(R.drawable.transparent_button);
                                    holder.inviteButton.setText(R.string.invited);
                                    holder.inviteButton.setEnabled(true);
                                    holder.inviteButton.setVisibility(View.VISIBLE);
                                    holder.inviteButton.setTextColor(Color.parseColor("#660c3855"));
                                }

                            if (currentFriend.userName.length() < 16) {
                                holder.friendName.setText(currentFriend.userName);
                            } else {
                                String names[] = currentFriend.userName.split(" ");
                                String displayName = names[0] + "\n";
                                for (int i = 1; i < names.length; i++) {
                                    displayName = displayName + names[i];

                                }
                                holder.friendName.setText(displayName);
                            }
                            holder.friendLevel.setText(String.valueOf(currentFriend.level));
                            holder.inviteButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (currentFriend.isInvited) {
                                        holder.inviteButton.setBackgroundResource(R.drawable.green_button_selector);
                                        holder.inviteButton.setText(R.string.invite);
                                        holder.inviteButton.setTextColor(Color.parseColor("#ffffff"));
                                        InviteFriendsActivity.friendsList.get(position).isInvited = false;
                                    } else {
                                        holder.inviteButton.setBackgroundResource(R.drawable.transparent_button);
                                        holder.inviteButton.setText(R.string.invited);
                                        holder.inviteButton.setTextColor(Color.parseColor("#660c3855"));
                                        InviteFriendsActivity.friendsList.get(position).isInvited = true;
                                    }
                                }
                            });
                        }

            if (!currentFriend.offlineFriend && position != -1 && !currentFriend.userName.equals(activity.getResources().getString(R.string.add_offline_friends))) {

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String id = currentFriend.userID;
                        Activity ac = activity;
                        if (Persistance.getInstance().getUserInfo(ac).id.equals(id)) {
                            Toast.makeText(ac, R.string.its_you, Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Intent intent = new Intent(ac, UserProfileActivity.class);
                        intent.putExtra("UserId", id);
                        activity.startActivity(intent);
                    }
                });
                holder.friendProfileImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String id = currentFriend.userID;
                        Activity ac = activity;
                        if (Persistance.getInstance().getUserInfo(ac).id.equals(id)) {
                            Toast.makeText(ac, R.string.its_you, Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Intent intent = new Intent(ac, UserProfileActivity.class);
                        intent.putExtra("UserId", id);
                        activity.startActivity(intent);
                    }
                });
            }
            if (!currentFriend.isOfflineParticipant) {
                holder.friendLevel.setText(String.valueOf(currentFriend.level));
            }
        }
        return view;

    }
}
