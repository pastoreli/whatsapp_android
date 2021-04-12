package com.pastoreli.whatsapp.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.pastoreli.whatsapp.R;
import com.pastoreli.whatsapp.activity.ChatActivity;
import com.pastoreli.whatsapp.activity.GroupActivity;
import com.pastoreli.whatsapp.adapter.ContactsAdapter;
import com.pastoreli.whatsapp.config.FirebaseConfig;
import com.pastoreli.whatsapp.helpers.RecyclerItemClickListener;
import com.pastoreli.whatsapp.helpers.UserFirebaseHelper;
import com.pastoreli.whatsapp.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactsFragment extends Fragment {

    private RecyclerView recyclerContacts;

    private ContactsAdapter contactsAdapter;

    private DatabaseReference fireBaseRef = FirebaseConfig.getFirebaseDatabase();
    private DatabaseReference userRef;
    private ValueEventListener valueEventListenerUsers;
    private FirebaseUser currentUser;

    private List<User> userList = new ArrayList<>();
    private String search = "";

    public ContactsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_contacts, container, false);

        currentUser = UserFirebaseHelper.getCurrentUser();

        recyclerContacts = view.findViewById(R.id.recyclerContacts);

        recyclerConfig();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        recoverUsers();
    }

    public void recoverUsers () {

        userRef = fireBaseRef.child("users");

        valueEventListenerUsers = userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();

                User userGroup = new User();
                userGroup.setName("Novo grupo");
                userGroup.setEmail("");
                userList.add(userGroup);

                for( DataSnapshot data : snapshot.getChildren() ) {
                    User user = data.getValue(User.class);

                    if(!user.getName().toUpperCase().contains(search) && !user.getEmail().toUpperCase().contains(search))
                        continue;

                    user.setIdUser(data.getKey());

                    if(currentUser.getEmail().equals(user.getEmail()))
                        continue;

                    userList.add(user);
                }

                contactsAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

    }

    public void searchUsers (String text) {
        search = text.toUpperCase();
        userList.clear();
        recoverUsers();
    }

    public void recyclerConfig() {

        contactsAdapter = new ContactsAdapter(userList, getActivity());

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerContacts.setLayoutManager(layoutManager);
        recyclerContacts.setHasFixedSize(true);
        recyclerContacts.setAdapter(contactsAdapter);

        recyclerContacts.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getActivity(),
                        recyclerContacts,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {

                                User user = userList.get(position);
                                Intent intent = null;

                                if(user.getEmail().isEmpty()) {
                                    intent = new Intent(getContext(), GroupActivity.class);
                                } else {
                                    intent = new Intent(getContext(), ChatActivity.class);
                                    intent.putExtra("contactChat", user);
                                }

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
        userRef.removeEventListener(valueEventListenerUsers);
    }

}