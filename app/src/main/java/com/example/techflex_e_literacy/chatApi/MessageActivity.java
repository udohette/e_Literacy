package com.example.techflex_e_literacy.chatApi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.techflex_e_literacy.Adapter.MessageAdapter;
import com.example.techflex_e_literacy.Notifications.Client;
import com.example.techflex_e_literacy.Notifications.Data;
import com.example.techflex_e_literacy.Notifications.MyResponse;
import com.example.techflex_e_literacy.Notifications.Sender;
import com.example.techflex_e_literacy.Notifications.Token;
import com.example.techflex_e_literacy.R;
import com.example.techflex_e_literacy.fragments.APIService;
import com.example.techflex_e_literacy.model.Chat;
import com.example.techflex_e_literacy.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageActivity extends AppCompatActivity {
    CircleImageView profile_image;
    TextView username;

    FirebaseUser fUser;
    DatabaseReference mReference;

    ImageButton btn_send;
    EditText text_send;

    MessageAdapter messageAdapter;
    List<Chat> mChats;
    RecyclerView recyclerView;

    Intent mIntent;

    ValueEventListener seenListener;
    String userid;
    APIService apiService;
    boolean notify = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        profile_image = findViewById(R.id.profile_image);
        username = findViewById(R.id.username);
        btn_send = findViewById(R.id.btn_send);
        text_send = findViewById(R.id.text_send);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
         getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               startActivity(new Intent(MessageActivity.this,ChatAPI.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });
        apiService = Client.getRetrofit("https://fcm.googleapis.com/").create(APIService.class);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        fUser = FirebaseAuth.getInstance().getCurrentUser();
        mIntent = getIntent();
        final String userid = mIntent.getStringExtra("userid");

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notify = true;
                String msg = text_send.getText().toString();
                if (!msg.equals("")){
                    sendMessage(fUser.getUid(),userid,msg);
                }else {
                    Toast.makeText(MessageActivity.this, "Empty Message Not Allowed", Toast.LENGTH_SHORT).show();
                }
                text_send.setText("");

            }
        });



        mReference = FirebaseDatabase.getInstance().getReference("User").child(userid);
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                username.setText(user.getUsername());
                if (user.getImageURL().equals("default")){
                    profile_image.setImageResource(R.mipmap.ic_e_learn_foreground);
                }else {
                    Glide.with(getApplicationContext()).load(user.getImageURL()).into(profile_image);
                }
                readMessage(fUser.getUid(),userid,user.getImageURL());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        seenMessage(userid);
    }
    private void seenMessage(final String userid){
        mReference = FirebaseDatabase.getInstance().getReference("chats");
        seenListener = mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(fUser.getUid()) && chat.getSender().equals(userid)){
                        HashMap<String, Object>hashMap = new HashMap<>();
                        hashMap.put("isseen",true);
                        snapshot.getRef().updateChildren(hashMap);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void sendMessage(String sender, final String receiver, String message){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object>hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);
        hashMap.put("isseen", false);

        reference.child("chats").push().setValue(hashMap);

          //add user to  chat  fragment
        final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(receiver)
                .child(fUser.getUid());

       chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               if (!dataSnapshot.exists()){
                   chatRef.child("id").setValue(userid);
               }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       });
       final String msg = message;
       reference = FirebaseDatabase.getInstance().getReference("User").child(fUser.getUid());
       reference.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               User user = dataSnapshot.getValue(User.class);
               if (notify) {
                   sendNotification(receiver, user.getUsername(), msg);
               }
               notify = false;

           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       });
    }

    private void sendNotification(String receiver, final String username, final String message) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(fUser.getUid(),R.mipmap.ic_e_learn_foreground,username+": "+message,"New Message",userid);
                    Sender sender = new Sender(data,token.getToken());
                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if (response.code() == 200){
                                        Log.v("Message","Failed");
                                    }


                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void readMessage(final String myid,final String userid,final String imageURL){
        mChats = new ArrayList<>();
        mReference = FirebaseDatabase.getInstance().getReference("chats");
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mChats.clear();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(myid) && chat.getSender().equals(userid) ||
                            chat.getReceiver().equals(userid) && chat.getSender().equals(myid)){
                        mChats.add(chat);
                    }
                    messageAdapter = new MessageAdapter(MessageActivity.this,mChats,imageURL);
                    recyclerView.setAdapter(messageAdapter);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void currentUser(String userid){
        SharedPreferences.Editor editor = getSharedPreferences("PREPS",MODE_PRIVATE).edit();
        editor.putString("currentUser",userid);
        editor.apply();
    }
    public void status(String status){
        mReference = FirebaseDatabase.getInstance().getReference("User").child(fUser.getUid());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status",status);
        mReference.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (fUser  != null) {
            status("online");
            currentUser(userid);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mReference.removeEventListener(seenListener);
        if (fUser == null) {
            status("offline");
            currentUser("none");
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
