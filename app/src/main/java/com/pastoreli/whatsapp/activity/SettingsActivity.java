package com.pastoreli.whatsapp.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import de.hdodenhof.circleimageview.CircleImageView;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.pastoreli.whatsapp.R;
import com.pastoreli.whatsapp.config.FirebaseConfig;
import com.pastoreli.whatsapp.controller.UserController;
import com.pastoreli.whatsapp.helpers.Permission;
import com.pastoreli.whatsapp.helpers.UserFirebaseHelper;
import com.pastoreli.whatsapp.model.User;

import java.io.ByteArrayOutputStream;

public class SettingsActivity extends AppCompatActivity {

    private String[] requiredPermissions = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    private ImageButton imageButtonCamera, imageButtonGallery;
    private CircleImageView circleImageViewProfile;
    private EditText editProfileName;
    private ImageView imageSave;

    private static final int CAMERA_SELECTION = 100;
    private static final int GALLERY_SELECTION = 200;

    private StorageReference storageReference;
    private String currentUserId;
    private UserController userController;

    private User sessionUserData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Permission.validatePermission(requiredPermissions, this, 1);

        storageReference = FirebaseConfig.getFirebaseStorage();
        currentUserId = UserFirebaseHelper.getUserId();
        sessionUserData = UserFirebaseHelper.getUserSessionData();
        FirebaseUser currentUser = UserFirebaseHelper.getCurrentUser();
        userController = new UserController();

        imageButtonCamera = findViewById(R.id.imageButtonCamera);
        imageButtonGallery =  findViewById(R.id.imageButtonGallery);
        circleImageViewProfile = findViewById(R.id.circleImageViewProfile);
        editProfileName = findViewById(R.id.editProfileName);
        imageSave = findViewById(R.id.imageSave);

        Toolbar toolbar = findViewById(R.id.toolbarMain);
        toolbar.setTitle(R.string.toolbar_settings);
        setSupportActionBar( toolbar );

        // enable return button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Uri userPhotoUri = currentUser.getPhotoUrl();
        if( userPhotoUri != null ) {
            Glide.with(SettingsActivity.this)
                    .load( userPhotoUri )
                    .into( circleImageViewProfile );

        } else {
            circleImageViewProfile.setImageResource(R.drawable.padrao);
        }

        editProfileName.setText( currentUser.getDisplayName() );

        imageButtonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if( intent.resolveActivity(getPackageManager()) != null ) {
                    startActivityForResult( intent, CAMERA_SELECTION );
                }

            }
        });

        imageButtonGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if( intent.resolveActivity(getPackageManager()) != null ) {
                    startActivityForResult( intent, GALLERY_SELECTION );
                }

            }
        });

        imageSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editProfileName.getText().toString();
                if(name.isEmpty())
                    showToast("Preencha o seu nome!");
                else {
                    boolean nameResult = UserFirebaseHelper.updateUserName(name);
                    if(nameResult) {
                        sessionUserData.setName(name);
                        userController.updateUserData(sessionUserData);

                        showToast("Sucesso ao atualizar nome de usuário");
                    }
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

                switch ( requestCode ) {
                    case CAMERA_SELECTION:
                        image = (Bitmap) data.getExtras().get("data");
                        break;
                    case GALLERY_SELECTION:
                        Uri selectedImageLocal = data.getData();
                        image = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageLocal);
                        break;
                }

                if ( image != null ) {

                    circleImageViewProfile.setImageBitmap(image);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.JPEG, 70, baos );
                    byte[] imageData = baos.toByteArray();

                    final StorageReference imageRef = storageReference
                            .child("images")
                            .child("profile")
                            .child(currentUserId)
                            .child("profile.jpeg");

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
                                    Uri uri = task.getResult();
                                    updateUserPicture( uri );
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

    public void updateUserPicture ( Uri uri ) {
        if(UserFirebaseHelper.updateUserPicture(uri)) {
            sessionUserData.setPhoto(uri.toString());
            userController.updateUserData(sessionUserData);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for( int permissionResult : grantResults ) {
            if( permissionResult == PackageManager.PERMISSION_DENIED) {
                alertPermissionValidate();
            }
        }

    }

    private void alertPermissionValidate () {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissões Negadas");
        builder.setMessage("Para utilizar o app é necessário aceitar as permissões.");
        builder.setCancelable(false);
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void showToast (String text) {
        Toast.makeText(
                SettingsActivity.this,
                text,
                Toast.LENGTH_SHORT
        ).show();
    }

}