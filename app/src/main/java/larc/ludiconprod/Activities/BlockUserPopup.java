package larc.ludiconprod.Activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import larc.ludiconprod.Controller.HTTPResponseController;
import larc.ludiconprod.Controller.Persistance;
import larc.ludiconprod.R;
import larc.ludiconprod.UserProfile;
import larc.ludiconprod.Utils.Message;

public class BlockUserPopup extends Activity{

    TextView textBlockUser;
    Button cancel;
    Button block;
    TextView blockUser;
    public String ChatId;
    public String isUserBlock;
    public int isBlocked;
    public String chatIdEvent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_block_user);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        textBlockUser = (TextView) findViewById(R.id.textBlockUser);
        cancel = (Button) findViewById(R.id.cancel);
        block = (Button) findViewById(R.id.block);
        blockUser = (TextView) findViewById(R.id.blockUser);

        final String usersId = (String) getIntent().getSerializableExtra("userId");
        isBlocked = (Integer) getIntent().getSerializableExtra("isBlocked");
        isUserBlock = (String) getIntent().getSerializableExtra("isUserBlock");
        chatIdEvent = (String) getIntent().getSerializableExtra("chatIdEvent");

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (isUserBlock.equals("true")) {
            if (isBlocked == 0) {
                textBlockUser.setText(getResources().getString(R.string.block_this_user));
                block.setText(getResources().getString(R.string.block));
                blockUser.setText(getResources().getString(R.string.once_you_block_this_user));
            } else if (isBlocked == 1) {
                textBlockUser.setText(getResources().getString(R.string.unblock_this_user));
                block.setText(getResources().getString(R.string.unblock));
                blockUser.setText(getResources().getString(R.string.once_you_unblock_this_user));
            }
        }else if (isUserBlock.equals("false")){
            if (isBlocked == 0) {
                textBlockUser.setText(getResources().getString(R.string.block_this_group_chat));
                block.setText(getResources().getString(R.string.block));
                blockUser.setText(getResources().getString(R.string.once_you_block_this_group_chat));
            } else if (isBlocked == 1) {
                textBlockUser.setText(getResources().getString(R.string.unblock_this_group_chat));
                block.setText(getResources().getString(R.string.unblock));
                blockUser.setText(getResources().getString(R.string.once_you_unblock_this_group_chat));
            }
        }

        block.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isUserBlock.equals("true")) {

                    final DatabaseReference myNode = FirebaseDatabase.getInstance().getReference().child("users").child(Persistance.getInstance().getUserInfo(BlockUserPopup.this).id).child("talkbuddies");
                    myNode.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot myChatParticipants) {
                            String chatId;
                            if (myChatParticipants.hasChild(usersId)) {
                                chatId = myChatParticipants.child(usersId).getValue().toString();
                                ChatId = chatId;

                                if (isBlocked == 0) {
                                    final DatabaseReference firebaseRef = FirebaseDatabase.getInstance().getReference().child("users").child(Persistance.getInstance().getUserInfo(BlockUserPopup.this).id).child("chats").child(ChatId);
                                    firebaseRef.child("blk").setValue("1");

                                    final DatabaseReference firebaseRef2 = FirebaseDatabase.getInstance().getReference().child("chats").child(ChatId).child("users").child(Persistance.getInstance().getUserInfo(BlockUserPopup.this).id);
                                    firebaseRef2.child("bl_date").setValue(String.valueOf(System.currentTimeMillis() / 1000));

                                } else if (isBlocked == 1) {
                                    final DatabaseReference firebaseRef = FirebaseDatabase.getInstance().getReference().child("users").child(Persistance.getInstance().getUserInfo(BlockUserPopup.this).id).child("chats").child(ChatId);
                                    firebaseRef.child("blk").setValue("0");

                                    final DatabaseReference firebaseRef2 = FirebaseDatabase.getInstance().getReference().child("chats").child(ChatId).child("users").child(Persistance.getInstance().getUserInfo(BlockUserPopup.this).id);
                                    firebaseRef2.child("bl_date").removeValue();
                                }

                            } else {

                                final DatabaseReference chatNode = FirebaseDatabase.getInstance().getReference().child("chats");
                                HashMap<String, Object> values = new HashMap<String, Object>();
                                HashMap<String, Object> usersMap = new HashMap<String, Object>();
                                HashMap<String, Object> myValue = new HashMap<String, Object>();
                                HashMap<String, Object> otherValue = new HashMap<String, Object>();
                                myValue.put("image", Persistance.getInstance().getUserInfo(BlockUserPopup.this).profileImage);
                                myValue.put("name", Persistance.getInstance().getUserInfo(BlockUserPopup.this).firstName + " " + Persistance.getInstance().getUserInfo(BlockUserPopup.this).lastName);
                                otherValue.put("image", getIntent().getStringExtra("otherParticipantImage"));
                                otherValue.put("name", getIntent().getStringExtra("otherParticipantName").substring(0, getIntent().getStringExtra("otherParticipantName").length() - 1));
                                usersMap.put(Persistance.getInstance().getUserInfo(BlockUserPopup.this).id, myValue);
                                usersMap.put(usersId, otherValue);
                                values.put("users", usersMap);
                                String chat = chatNode.push().getKey();
                                ChatId = chat;
                                chatNode.child(chat).setValue(values);
                                FirebaseDatabase.getInstance().getReference().child("users").child(Persistance.getInstance().getUserInfo(BlockUserPopup.this).id).child("talkbuddies").child(usersId).setValue(chat);
                                FirebaseDatabase.getInstance().getReference().child("users").child(usersId).child("talkbuddies").child(Persistance.getInstance().getUserInfo(BlockUserPopup.this).id).setValue(chat);

                                final DatabaseReference firebaseRef = FirebaseDatabase.getInstance().getReference().child("users").child(Persistance.getInstance().getUserInfo(BlockUserPopup.this).id).child("chats").child(ChatId);
                                firebaseRef.child("blk").setValue("1");

                                final DatabaseReference firebaseRef2 = FirebaseDatabase.getInstance().getReference().child("chats").child(ChatId).child("users").child(Persistance.getInstance().getUserInfo(BlockUserPopup.this).id);
                                firebaseRef2.child("bl_date").setValue(String.valueOf(System.currentTimeMillis() / 1000));
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });

                }else if (isUserBlock.equals("false")){
                    if (isBlocked == 0) {
                        final DatabaseReference firebaseRef = FirebaseDatabase.getInstance().getReference().child("users").child(Persistance.getInstance().getUserInfo(BlockUserPopup.this).id).child("chats").child(chatIdEvent);
                        firebaseRef.child("blk").setValue("1");

                        final DatabaseReference firebaseRef2 = FirebaseDatabase.getInstance().getReference().child("chats").child(chatIdEvent).child("users").child(Persistance.getInstance().getUserInfo(BlockUserPopup.this).id);
                        firebaseRef2.child("bl_date").setValue(String.valueOf(System.currentTimeMillis() / 1000));

                    } else if (isBlocked == 1) {
                        final DatabaseReference firebaseRef = FirebaseDatabase.getInstance().getReference().child("users").child(Persistance.getInstance().getUserInfo(BlockUserPopup.this).id).child("chats").child(chatIdEvent);
                        firebaseRef.child("blk").setValue("0");

                        final DatabaseReference firebaseRef2 = FirebaseDatabase.getInstance().getReference().child("chats").child(chatIdEvent).child("users").child(Persistance.getInstance().getUserInfo(BlockUserPopup.this).id);
                        firebaseRef2.child("bl_date").removeValue();
                    }

                    HashMap<String, String> params = new HashMap<String, String>();
                    HashMap<String, String> headers = new HashMap<String, String>();
                    HashMap<String, String> urlParams = new HashMap<String, String>();
                    headers.put("authKey", Persistance.getInstance().getUserInfo(BlockUserPopup.this).authKey);

                    //set urlParams
                    urlParams.put("eventId", String.valueOf(getIntent().getSerializableExtra("eventId")));
                    urlParams.put("userId", Persistance.getInstance().getUserInfo(BlockUserPopup.this).id);
                    //HTTPResponseController.getInstance().getEventDetails(params, headers, BlockUserPopup.this, urlParams);

                }
                finish();
            }
        });
    }
}
