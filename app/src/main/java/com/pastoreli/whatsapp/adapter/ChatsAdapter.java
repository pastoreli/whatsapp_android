package com.pastoreli.whatsapp.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.pastoreli.whatsapp.R;
import com.pastoreli.whatsapp.model.Chat;
import com.pastoreli.whatsapp.model.Group;
import com.pastoreli.whatsapp.model.User;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.MyViewHolder> {

    private List<Chat> chatList;
    private Context context;
    public ChatsAdapter(List<Chat> chatList, Context context) {
        this.chatList = chatList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemList = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_contacts, parent, false);
        return new ChatsAdapter.MyViewHolder(itemList);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Chat chat = chatList.get(position);

        if("true".equals(chat.getIsGroup())) {
            Group group = chat.getGroup();
            holder.textName.setText(group.getName());
            holder.textLastMessage.setText(chat.getLastMessage());

            if(group.getPhoto() == null) {
                holder.imageUser.setImageResource(R.drawable.padrao);
            } else {
                Glide.with(context)
                    .load( group.getPhoto() )
                    .into( holder.imageUser );
            }

        } else {
            holder.textName.setText(chat.getDisplayUser().getName());
            holder.textLastMessage.setText(chat.getLastMessage());

            String userPhoto = chat.getDisplayUser().getPhoto();

            if(userPhoto == null) {
                holder.imageUser.setImageResource(R.drawable.padrao);
            } else {
                Glide.with(context)
                    .load( userPhoto )
                    .into( holder.imageUser );
            }
        }
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView textName, textLastMessage;
        CircleImageView imageUser;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            textName = itemView.findViewById(R.id.textName);
            textLastMessage = itemView.findViewById(R.id.textEmail);
            imageUser = itemView.findViewById(R.id.imageUser);

        }
    }

}
