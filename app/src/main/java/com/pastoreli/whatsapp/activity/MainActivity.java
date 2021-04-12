package com.pastoreli.whatsapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;
import com.pastoreli.whatsapp.R;
import com.pastoreli.whatsapp.config.FirebaseConfig;
import com.pastoreli.whatsapp.fragment.ChatsFragment;
import com.pastoreli.whatsapp.fragment.ContactsFragment;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth authentication;

    private MaterialSearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        authentication = FirebaseConfig.getFirebaseAuthentication();

        Toolbar toolbar = findViewById(R.id.toolbarMain);
        toolbar.setTitle(R.string.toolbar_main);
        setSupportActionBar( toolbar );

        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(),
                FragmentPagerItems.with(this)
                        .add(R.string.tab_chats, ChatsFragment.class)
                        .add(R.string.tab_contacts, ContactsFragment.class)
                        .create()
        );

        ViewPager viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(adapter);

        SmartTabLayout viewPagerTab = findViewById(R.id.viewPagerTab);
        viewPagerTab.setViewPager( viewPager );

        searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if(newText != null) {

                    int currentItem = viewPager.getCurrentItem();

                    switch ( currentItem ) {
                        case 0 :
                            ChatsFragment chatsFragment = (ChatsFragment) adapter.getPage(0);
                            chatsFragment.searchChats(newText);
                            break;

                        case 1 :
                            ContactsFragment contactsFragment = (ContactsFragment) adapter.getPage(1);
                            contactsFragment.searchUsers(newText);
                            break;
                    }

                }

                return true;
            }
        });

    }

    public void signOutUser () {

        try {
            authentication.signOut();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch ( item.getItemId() ) {
            case R.id.menuSettings:
                forward(SettingsActivity.class);
                break;

            case R.id.menuSignOut:
                signOutUser();
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        MenuItem item = menu.findItem(R.id.menuSearch);
        searchView.setMenuItem(item);

        return super.onCreateOptionsMenu(menu);
    }

    public void forward (Class classRef) {
        Intent intent = new Intent(MainActivity.this, classRef);
        startActivity(intent);
    }
}