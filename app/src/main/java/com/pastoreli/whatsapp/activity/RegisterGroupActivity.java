package com.pastoreli.whatsapp.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.pastoreli.whatsapp.R;
import com.pastoreli.whatsapp.adapter.ContactsAdapter;
import com.pastoreli.whatsapp.adapter.SelectedGroupAdapter;
import com.pastoreli.whatsapp.config.FirebaseConfig;
import com.pastoreli.whatsapp.controller.GroupController;
import com.pastoreli.whatsapp.helpers.RecyclerItemClickListener;
import com.pastoreli.whatsapp.helpers.UserFirebaseHelper;
import com.pastoreli.whatsapp.model.Group;
import com.pastoreli.whatsapp.model.User;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class RegisterGroupActivity extends AppCompatActivity {

    private static final int GALLERY_SELECTION = 200;

    private SelectedGroupAdapter selectedGroupAdapter;

    private TextView textTotalMembers;
    private RecyclerView recyclerGroupMembers;
    private CircleImageView imageGroup;
    private FloatingActionButton fabSavaGroup;
    private EditText editGroupName;

    private List<User> memberSelectedList = new ArrayList<>();
    private Group group;

    private StorageReference storageReference;
    private String currentUserId;

    private GroupController groupController = new GroupController();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_group);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Novo grupo");
        toolbar.setSubtitle("Defina o nome");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        textTotalMembers = findViewById(R.id.textTotalMembers);
        recyclerGroupMembers = findViewById(R.id.recyclerGroupMembers);
        imageGroup = findViewById(R.id.imageGroup);
        fabSavaGroup = findViewById(R.id.fabSaveGroup);
        editGroupName = findViewById(R.id.editGroupName);

        storageReference = FirebaseConfig.getFirebaseStorage();
        currentUserId = UserFirebaseHelper.getUserId();

        group = new Group();
        group.setId(groupController.generateGroupId());

        if( getIntent().getExtras() != null ) {
            List<User> members = (List<User>) getIntent().getExtras().getSerializable("members");
            memberSelectedList.addAll(members);
            textTotalMembers.setText("Participantes: " + memberSelectedList.size());
        }

        imageGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if( intent.resolveActivity(getPackageManager()) != null ) {
                    startActivityForResult( intent, GALLERY_SELECTION );
                }

            }
        });

        fabSavaGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String groupName = editGroupName.getText().toString();
                User currentUser = UserFirebaseHelper.getUserSessionData();
                currentUser.setIdUser(currentUserId);
                memberSelectedList.add( currentUser );
                group.setMembers( memberSelectedList );
                group.setName(groupName);

                groupController.saveGroup(group);

                Intent intent = new Intent(RegisterGroupActivity.this, ChatActivity.class);
                intent.putExtra("groupChat", group);
                startActivity(intent);

            }
        });

        configRecycler();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if( resultCode == RESULT_OK ) {
            Bitmap image = null;

            try {

                Uri selectedImageLocal = data.getData();
                image = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageLocal);

                if ( image != null ) {

                    imageGroup.setImageBitmap(image);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.JPEG, 70, baos );
                    byte[] imageData = baos.toByteArray();

                    final StorageReference imageRef = storageReference
                            .child("images")
                            .child("groups")
                            .child( group.getId() + ".jpeg");

                    UploadTask uploadTask = imageRef.putBytes( imageData );

                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            showToast("Erro ao fazer upload da imagem.");
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            showToast("Sucesso ao fazer upload da imagem.");

                            imageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    String uri = task.getResult().toString();
                                    group.setPhoto(uri);
//                                    updateUserPicture( uri );
                                }
                            });

                        }
                    });

                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public void configRecycler () {
        selectedGroupAdapter = new SelectedGroupAdapter(memberSelectedList, getApplicationContext());

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(
                getApplicationContext(),
                LinearLayoutManager.HORIZONTAL,
                false
        );
        recyclerGroupMembers.setLayoutManager(layoutManager);
        recyclerGroupMembers.setHasFixedSize(true);
        recyclerGroupMembers.setAdapter(selectedGroupAdapter);
    }

    public void showToast (String text) {
        Toast.makeText(
                RegisterGroupActivity.this,
                text,
                Toast.LENGTH_SHORT
        ).show();
    }
}