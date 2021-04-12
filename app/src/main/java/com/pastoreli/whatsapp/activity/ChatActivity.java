package com.pastoreli.whatsapp.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.pastoreli.whatsapp.R;
import com.pastoreli.whatsapp.adapter.MessagesAdapter;
import com.pastoreli.whatsapp.config.FirebaseConfig;
import com.pastoreli.whatsapp.controller.ChatController;
import com.pastoreli.whatsapp.controller.MessageController;
import com.pastoreli.whatsapp.helpers.CustomBase64;
import com.pastoreli.whatsapp.helpers.UserFirebaseHelper;
import com.pastoreli.whatsapp.model.Chat;
import com.pastoreli.whatsapp.model.Group;
import com.pastoreli.whatsapp.model.Message;
import com.pastoreli.whatsapp.model.User;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ChatActivity extends AppCompatActivity {

    private static final int CAMERA_SELECTION = 100;
    private static final int GALLERY_SELECTION = 200;

    private CircleImageView circleImagePhoto;
    private TextView textNameChat;
    private EditText editMessage;
    private ImageView imageCamera, imageGallery;
    private RecyclerView recyclerMessages;

    private MessagesAdapter messagesAdapter;

    private User senderUser;
    private User destinataryUser;
    private Group group;

    private String idUserSender;
    private String idUserDestinatary;
    private List<Message> messages = new ArrayList<>();

    private MessageController messageController;
    private ChatController chatController;
    private DatabaseReference database;
    private DatabaseReference messagesDatabase;
    private StorageReference storage;

    private ChildEventListener childEventListenerMessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        messageController = new MessageController();
        chatController = new ChatController();

        circleImagePhoto = findViewById(R.id.circleImagePhoto);
        textNameChat = findViewById(R.id.textNameChat);
        editMessage = findViewById(R.id.editMessage);
        imageCamera = findViewById(R.id.imageCamera);
        imageGallery = findViewById(R.id.imageGallery);
        recyclerMessages = findViewById(R.id.recyclerMessages);

        Bundle bundle = getIntent().getExtras();

        senderUser = UserFirebaseHelper.getUserSessionData();

        if(bundle != null) {

            if(bundle.containsKey("groupChat")) {
                group = (Group) bundle.getSerializable("groupChat");

                textNameChat.setText(group.getName());

                String photo = group.getPhoto();

                if(photo != null) {
                    Glide.with(ChatActivity.this)
                            .load(photo)
                            .into(circleImagePhoto);
                }

                idUserDestinatary = group.getId();
            } else {
                destinataryUser = (User) bundle.getSerializable("contactChat");
                textNameChat.setText(destinataryUser.getName());

                String photo = destinataryUser.getPhoto();

                if(photo != null) {
                    Glide.with(ChatActivity.this)
                            .load(photo)
                            .into(circleImagePhoto);
                }

                idUserDestinatary = CustomBase64.Base64Encode(destinataryUser.getEmail());
            }

        }

        idUserSender = UserFirebaseHelper.getUserId();

        database = FirebaseConfig.getFirebaseDatabase();
        storage = FirebaseConfig.getFirebaseStorage();

        messagesDatabase = database.child("messages")
                .child(idUserSender)
                .child(idUserDestinatary);

        configImageCamera();
        configImageGallery();
        configRecycler();

    }

    @Override
    protected void onStart() {
        super.onStart();
        recoverMessages();
    }

    public void sendMessage (View view) {

        String messageText = editMessage.getText().toString();

        if( !messageText.isEmpty() ) {

            if(destinataryUser == null) {

                for (User member : group.getMembers()) {

                    String idSender = CustomBase64.Base64Encode(member.getEmail());
                    String idUserSession = idUserSender;

                    Message message = new Message();
                    message.setIdUser( idUserSession );
                    message.setMessage(messageText);
                    message.setName(senderUser.getName());

                    messageController.saveGroupMessageData(idSender, idUserDestinatary, message);
                    saveChat( idSender, idUserDestinatary, message, true);

                }

            } else {
                Message message = new Message();
                message.setIdUser( idUserSender );
                message.setMessage(messageText);

                messageController.saveMessageData(idUserDestinatary, message);
                saveChat(idUserSender, idUserDestinatary, message, false);
            }

            editMessage.getText().clear();

        }

    }

    public void saveChat (String idSender, String idDestinatary, Message message, boolean isGroup) {

        Chat chat = new Chat();
        chat.setIdSender(idSender);
        chat.setIdDestinatary(idDestinatary);
        chat.setLastMessage(message.getMessage());

        if( isGroup ) {
            chat.setIsGroup("true");
            chat.setGroup(group);

            chatController.saveChatGroupData(chat);
        } else {
            chat.setDisplayUser(destinataryUser);

            FirebaseUser currentUser = UserFirebaseHelper.getCurrentUser();
            User user = new User();
            user.setName(currentUser.getDisplayName());
            user.setEmail(currentUser.getEmail());
            user.setPhoto(currentUser.getPhotoUrl().toString());

            chatController.saveChatData(chat, user);
        }

    }

    public void configRecycler () {

        messagesAdapter = new MessagesAdapter(messages, getApplicationContext());

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerMessages.setLayoutManager(layoutManager);
        recyclerMessages.setHasFixedSize(true);
        recyclerMessages.setAdapter(messagesAdapter);

    }

    public void recoverMessages () {
        childEventListenerMessages = messagesDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.i("REFATCH", "image");
                Message message = snapshot.getValue(Message.class);
                messages.add(message);
                messagesAdapter.notifyDataSetChanged();
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

    public void configImageCamera () {
        imageCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if( intent.resolveActivity(getPackageManager()) != null ) {
                    startActivityForResult(intent, CAMERA_SELECTION);
                }
            }
        });
    }

    public void configImageGallery () {
        imageGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if( intent.resolveActivity(getPackageManager()) != null ) {
                    startActivityForResult(intent, GALLERY_SELECTION);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if( resultCode == RESULT_OK ) {

            Bitmap image = null;

            try {

                switch (requestCode) {
                    case CAMERA_SELECTION:
                        image = (Bitmap) data.getExtras().get("data");
                        break;
                    case GALLERY_SELECTION:
                        Uri selectedImageLocal = data.getData();
                        image = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageLocal);
                        break;
                }

                if( image != null ) {


                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] imageData = baos.toByteArray();

                    String imageName = UUID.randomUUID().toString();

                    final StorageReference imageRef = storage.child("images")
                            .child("photos")
                            .child(idUserSender)
                            .child(imageName);

                    UploadTask uploadTask = imageRef.putBytes( imageData );

                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            showToast("Erro ao fazer upload da imagem.");
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            imageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    String uri = task.getResult().toString();


                                    if(destinataryUser == null) {

                                        for (User member : group.getMembers()) {

                                            String idSender = CustomBase64.Base64Encode(member.getEmail());
                                            String idUserSession = idUserSender;

                                            Message message = new Message();
                                            message.setIdUser( idUserSession );
                                            message.setName(senderUser.getName());
                                            message.setImage(uri);
                                            message.setMessage("image.jpeg");

                                            messageController.saveGroupMessageData(idSender, idUserDestinatary, message);
                                            saveChat( idSender, idUserDestinatary, message, true);

                                        }

                                    } else {
                                        Message message = new Message();
                                        message.setIdUser( idUserSender );
                                        message.setImage(uri);
                                        message.setMessage("image.jpeg");

                                        messageController.saveMessageData(idUserDestinatary, message);
                                        saveChat(idUserSender, idUserDestinatary, message, false);

                                    }


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

    public void showToast (String text) {
        Toast.makeText(
                ChatActivity.this,
                text,
                Toast.LENGTH_SHORT
        ).show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        messages.clear();
        messagesDatabase.removeEventListener(childEventListenerMessages);
    }
}