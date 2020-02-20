package com.example.techflex_e_literacy.fragments;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import static android.app.Activity.RESULT_OK;


public class ProfileFragment extends Fragment {
    private StorageReference storageReference;
    private static final int IMAGE_REQUEST = 1;
    private Uri imageURL;
    private StorageTask uploadTask;

    private CircleImageView image_profile;
    private TextView username;
    private TextView dept;
    private TextView state;
    private TextView study_center;

    private DatabaseReference mReference;
    private FirebaseUser fuser;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile,container,false);
        image_profile =view.findViewById(R.id.profile_image);
        username = view.findViewById(R.id.username);
        dept =  view.findViewById(R.id.dept);
        state = view.findViewById(R.id.state);
        study_center = view.findViewById(R.id.study_center);
        storageReference = FirebaseStorage.getInstance().getReference("uploads");

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        mReference = FirebaseDatabase.getInstance().getReference("User").child(fuser.getUid());
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (isAdded()){
                    User user = dataSnapshot.getValue(User.class);
                    username.setText(String.format("Name: %s", user.getUsername()));
                    dept.setText(String.format("Department: %s", user.getDepartment()));
                    state.setText(String.format("State: %s", user.getState()));
                    study_center.setText(String.format("Study Center: %s", user.getStudy_center()));
                    if (user.getImageURL().equals("default")){
                        image_profile.setImageResource(R.mipmap.ic_e_learn_foreground);
                    }else {
                        Glide.with(getContext()).load(user.getImageURL()).into(image_profile);
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        image_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImage();
            }
        });
        return view;
    }

    private void openImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,IMAGE_REQUEST);
    }
    public String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return  mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
    public void uploadImage(){
        final ProgressDialog pf = new ProgressDialog(getContext());
        pf.setMessage("Uploading");
        pf.show();
        if (imageURL != null){
         final StorageReference fileReference = storageReference.child(System.currentTimeMillis()+"."+getFileExtension(imageURL));
          uploadTask = fileReference.putFile(imageURL);
          uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot,Task<Uri>>() {
              @Override
              public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                  if (!task.isSuccessful()){
                      throw task.getException();
                  }
                  return fileReference.getDownloadUrl();
              }
          }).addOnCompleteListener(new OnCompleteListener<Uri>() {
              @Override
              public void onComplete(@NonNull Task<Uri> task) {
                  if (task.isSuccessful()){
                      Uri download = task.getResult();
                      String mUri = download.toString();
                      mReference = FirebaseDatabase.getInstance().getReference("User").child(fuser.getUid());
                      HashMap<String, Object> map = new HashMap<>();
                      map.put("imageURL",mUri);
                      mReference.updateChildren(map);
                      pf.dismiss();
                  }else {
                      Toast.makeText(getContext(),"Failed", Toast.LENGTH_SHORT).show();
                      pf.dismiss();
                  }

              }
          }).addOnFailureListener(new OnFailureListener() {
              @Override
              public void onFailure(@NonNull Exception e) {
                  Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                  pf.dismiss();
              }
          });
        }else {
            Toast.makeText(getContext(), "No  Image Selected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            imageURL = data.getData();
            if (uploadTask != null && uploadTask.isInProgress()){
                Toast.makeText(getContext(), "Upload in  Progress", Toast.LENGTH_SHORT).show();
            }else {
                uploadImage();
            }
        }
    }
}
