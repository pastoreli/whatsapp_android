package com.pastoreli.whatsapp.activity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.AdapterView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.pastoreli.whatsapp.R;
import com.pastoreli.whatsapp.adapter.ContactsAdapter;
import com.pastoreli.whatsapp.adapter.SelectedGroupAdapter;
import com.pastoreli.whatsapp.config.FirebaseConfig;
import com.pastoreli.whatsapp.helpers.RecyclerItemClickListener;
import com.pastoreli.whatsapp.helpers.UserFirebaseHelper;
import com.pastoreli.whatsapp.model.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GroupActivity extends AppCompatActivity {

    private ContactsAdapter contactsAdapter;
    private SelectedGroupAdapter selectedGroupAdapter;
    private Toolbar toolbar;
    private FloatingActionButton fabNextStep;

    private DatabaseReference fireBaseRef = FirebaseConfig.getFirebaseDatabase();
    private DatabaseReference userRef;
    private FirebaseUser currentUser;
    private ValueEventListener valueEventListenerMembers;

    private RecyclerView recyclerSelectedMembers, recyclerMembers;
    private List<User> memberList = new ArrayList<>();
    private List<User> memberSelectedList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_group);

        currentUser = UserFirebaseHelper.getCurrentUser();

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Novo grupo");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerSelectedMembers = findViewById(R.id.recyclerSelectedMembers);
        recyclerMembers = findViewById(R.id.recyclerMembers);

        configRecyclerMembers();
        configRecyclerSelectedMembers();

        fabNextStep = findViewById(R.id.fabNextStep);
        fabNextStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RegisterGroupActivity.class);
                intent.putExtra("members", (Serializable) memberSelectedList);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        recoverUsers();
    }

    @Override
    public void onStop() {
        super.onStop();
        userRef.removeEventListener(valueEventListenerMembers);
    }

    public void recoverUsers () {

        userRef = fireBaseRef.child("users");

        valueEventListenerMembers = userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                memberList.clear();

                for( DataSnapshot data : snapshot.getChildren() ) {
                    User user = data.getValue(User.class);

                    user.setIdUser(data.getKey());

                    if(currentUser.getEmail().equals(user.getEmail()))
                        continue;

                    memberList.add(user);
                }

                contactsAdapter.notifyDataSetChanged();
                updateToolbar();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

    }

    public void updateToolbar () {
        int totalSelected = memberSelectedList.size();
        int total = memberList.size() + totalSelected;
        toolbar.setSubtitle(totalSelected + " de " + total + " selecionados");
    }

    public void configRecyclerMembers () {
        contactsAdapter = new ContactsAdapter(memberList, getApplicationContext());

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerMembers.setLayoutManager(layoutManager);
        recyclerMembers.setHasFixedSize(true);
        recyclerMembers.setAdapter(contactsAdapter);

        recyclerMembers.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getApplicationContext(),
                        recyclerMembers,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {

                                User selectedUser = memberList.get(position);
                                memberList.remove(selectedUser);
                                contactsAdapter.notifyDataSetChanged();

                                memberSelectedList.add(selectedUser);
                                selectedGroupAdapter.notifyDataSetChanged();
                                updateToolbar();

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

    public void configRecyclerSelectedMembers () {
        selectedGroupAdapter = new SelectedGroupAdapter(memberSelectedList, getApplicationContext());

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(
                getApplicationContext(),
                LinearLayoutManager.HORIZONTAL,
                false
        );
        recyclerSelectedMembers.setLayoutManager(layoutManager);
        recyclerSelectedMembers.setHasFixedSize(true);
        recyclerSelectedMembers.setAdapter(selectedGroupAdapter);

        recyclerSelectedMembers.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getApplicationContext(),
                        recyclerSelectedMembers,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {

                                User selectedUser = memberSelectedList.get(position);
                                memberSelectedList.remove(selectedUser);
                                selectedGroupAdapter.notifyDataSetChanged();

                                memberList.add(selectedUser);
                                contactsAdapter.notifyDataSetChanged();
                                updateToolbar();

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

}