package com.pastoreli.whatsapp.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.pastoreli.whatsapp.R;
import com.pastoreli.whatsapp.activity.ChatActivity;
import com.pastoreli.whatsapp.adapter.ChatsAdapter;
import com.pastoreli.whatsapp.adapter.ContactsAdapter;
import com.pastoreli.whatsapp.config.FirebaseConfig;
import com.pastoreli.whatsapp.helpers.CustomBase64;
import com.pastoreli.whatsapp.helpers.RecyclerItemClickListener;
import com.pastoreli.whatsapp.helpers.UserFirebaseHelper;
import com.pastoreli.whatsapp.model.Chat;
import com.pastoreli.whatsapp.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatsFragment extends Fragment {

    private RecyclerView recyclerChats;

    private ChatsAdapter chatsAdapter;

    private DatabaseReference fireBaseRef = FirebaseConfig.getFirebaseDatabase();
    private DatabaseReference chatRef;
    private ChildEventListener ChildEventListenerChats;
    private FirebaseUser currentUser;

    private List<Chat> chatList = new ArrayList<>();
    private String search = "";


    public ChatsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chats, container, false);

        currentUser = UserFirebaseHelper.getCurrentUser();

        recyclerChats = view.findViewById(R.id.recyclerChats);

        recyclerConfig();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        recoverChats();
    }

    public void recoverChats () {

        String userId = UserFirebaseHelper.getUserId();

        chatRef = fireBaseRef.child("chats")
            .child(userId);

        ChildEventListenerChats = chatRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Chat chat = snapshot.getValue(Chat.class);
                if((chat.getDisplayUser()!= null && chat.getDisplayUser().getName().toUpperCase().contains(search))
                        || chat.getLastMessage().toUpperCase().contains(search)
                        || (chat.getGroup() != null && chat.getGroup().getName().toUpperCase().contains(search))) {
                    chatList.add(chat);
                }
                chatsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void searchChats (String text) {
        search = text.toUpperCase();
        chatList.clear();
        recoverChats();
    }

    public void recyclerConfig() {

        chatsAdapter = new ChatsAdapter(chatList, getActivity());

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerChats.setLayoutManager(layoutManager);
        recyclerChats.setHasFixedSize(true);
        recyclerChats.setAdapter(chatsAdapter);

        recyclerChats.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getActivity(),
                        recyclerChats,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {

                                Chat chat = chatList.get(position);

                                Intent intent = new Intent(getContext(), ChatActivity.class);

                                if("true".equals(chat.getIsGroup()))
                                    intent.putExtra("groupChat", chat.getGroup());
                                else
                                    intent.putExtra("contactChat", chat.getDisplayUser());

                                startActivity(intent);
                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                            }

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            }
                        }
                )
        );

    }

    @Override
    public void onStop() {
        super.onStop();
        chatRef.removeEventListener(ChildEventListenerChats);
        chatList.clear();
    }
}