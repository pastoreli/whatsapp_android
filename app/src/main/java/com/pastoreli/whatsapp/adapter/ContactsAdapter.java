package com.pastoreli.whatsapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.pastoreli.whatsapp.R;
import com.pastoreli.whatsapp.model.User;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.MyViewHolder> {


    private List<User> userList;
    private Context context;
    public ContactsAdapter(List<User> userList, Context context) {
        this.userList = userList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemList = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_contacts, parent, false);
        return new MyViewHolder(itemList);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        boolean isGroup = userList.get(position).getEmail().isEmpty();

        holder.textName.setText(userList.get(position).getName());
        holder.textEmail.setText(userList.get(position).getEmail());

        String userPhoto = userList.get(position).getPhoto();

        if( isGroup ) {
            holder.imageUser.setImageResource(R.drawable.icone_grupo);
            holder.textEmail.setVisibility(View.GONE);
        } else if(userPhoto == null)
            holder.imageUser.setImageResource(R.drawable.padrao);
        else {
            Glide.with(context)
                .load( userPhoto )
                .into( holder.imageUser );
        }

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView textName, textEmail;
        CircleImageView imageUser;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            textName = itemView.findViewById(R.id.textName);
            textEmail = itemView.findViewById(R.id.textEmail);
            imageUser = itemView.findViewById(R.id.imageUser);

        }
    }

}
