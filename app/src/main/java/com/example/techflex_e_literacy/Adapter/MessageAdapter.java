package com.example.techflex_e_literacy.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.techflex_e_literacy.R;
import com.example.techflex_e_literacy.chatApi.MessageActivity;
import com.example.techflex_e_literacy.model.Chat;
import com.example.techflex_e_literacy.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private static final int  MSG_TYPE_LEFT = 0;
    private static final int MSG_TYPE_RIGHT = 1;
    private Context mContext;
    private List<Chat> mChat;
    private  String imageURL;

    public MessageAdapter(Context mContext, List<Chat> mChat,String imageURL){
        this.mChat = mChat;
        this.mContext = mContext;
        this.imageURL = imageURL;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Chat chat = mChat.get(position);
        holder.show_message.setText(chat.getMessage());
        if (imageURL.equals("default")){
            holder.profile_image.setImageResource(R.mipmap.ic_e_learn_foreground);
        }else {
            Glide.with(mContext).load(imageURL).into(holder.profile_image);
        }
        if (position == mChat.size()-1){
            if (chat.isIsseen()){
                holder.txt_seen.setText("seen");
            }else {
                holder.txt_seen.setText("Delivered");
            }

        }else {
            holder.txt_seen.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView show_message;
        private ImageView profile_image;
        private TextView txt_seen;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            show_message = (itemView).findViewById(R.id.show_message);
            profile_image =itemView.findViewById(R.id.profile_image);
            txt_seen = itemView.findViewById(R.id.txt_seen);
        }
    }

    @Override
    public int getItemViewType(int position) {
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mChat.get(position).getSender().equals(fUser.getUid())){
            return MSG_TYPE_RIGHT;
        }else {
            return MSG_TYPE_LEFT;
        }
}
}
