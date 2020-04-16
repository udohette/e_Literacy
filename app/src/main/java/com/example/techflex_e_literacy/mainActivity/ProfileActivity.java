package com.example.techflex_e_literacy.mainActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.techflex_e_literacy.R;
import com.example.techflex_e_literacy.model.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private StorageReference storageReference;
    private static final int IMAGE_REQUEST = 1;
    private Uri imageURL;
    private StorageTask uploadTask;

    private CircleImageView image_profile;
    private TextView username,email,back_button;
    private TextView dept;
    private TextView state;
    private TextView study_center;
    private FirebaseUser fuser;

    private DatabaseReference mReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        String userid = getIntent().getStringExtra("userid");

        image_profile = findViewById(R.id.profile_image);
        username = findViewById(R.id.username);
        dept = findViewById(R.id.dept);
        state = findViewById(R.id.state);
        study_center = findViewById(R.id.study_center);
        storageReference = FirebaseStorage.getInstance().getReference("uploads");
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        mReference = FirebaseDatabase.getInstance().getReference("User").child(userid);

        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    User user = dataSnapshot.getValue(User.class);
                    username.setText(String.format("Name: %s", "@"+user.getUsername()));
                    dept.setText(String.format("Department: %s", user.getDepartment()));
                    state.setText(String.format("State: %s", user.getState()));
                    study_center.setText(String.format("Study Center: %s", user.getStudy_center()));
                    if (user.getImageURL().equals("default")) {
                        image_profile.setImageResource(R.mipmap.ic_e_learn_foreground);
                    } else {
                        Glide.with(ProfileActivity.this).load(user.getImageURL()).into(image_profile);
                    }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}