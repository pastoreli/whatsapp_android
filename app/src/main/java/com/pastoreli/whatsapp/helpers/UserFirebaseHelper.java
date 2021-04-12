package com.pastoreli.whatsapp.helpers;

import android.net.Uri;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.pastoreli.whatsapp.config.FirebaseConfig;
import com.pastoreli.whatsapp.model.User;

import androidx.annotation.NonNull;

public class UserFirebaseHelper {

    public static String getUserId () {
        FirebaseAuth user = FirebaseConfig.getFirebaseAuthentication();
        String email = user.getCurrentUser().getEmail();
        return CustomBase64.Base64Encode( email );
    }

    public static FirebaseUser getCurrentUser () {
        FirebaseAuth user = FirebaseConfig.getFirebaseAuthentication();
        return user.getCurrentUser();
    }

    public static boolean updateUserPicture (Uri uri) {

        try {


            FirebaseUser user = getCurrentUser();
            UserProfileChangeRequest profile = new UserProfileChangeRequest
                    .Builder()
                    .setPhotoUri(uri)
                    .build();

            user.updateProfile( profile ).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(!task.isSuccessful())
                        Log.d("PROFILE", "Erro ao atualizar foto do usuário");
                }
            });
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean updateUserName (String name) {

        try {


            FirebaseUser user = getCurrentUser();
            UserProfileChangeRequest profile = new UserProfileChangeRequest
                    .Builder()
                    .setDisplayName(name)
                    .build();

            user.updateProfile( profile ).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(!task.isSuccessful())
                        Log.d("PROFILE", "Erro ao atualizar nome de usuário");
                }
            });
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static User getUserSessionData () {
        FirebaseUser firebaseUser = getCurrentUser();

        User user = new User();
        user.setEmail(firebaseUser.getEmail());
        user.setName(firebaseUser.getDisplayName());
        if( firebaseUser.getPhotoUrl() == null )
            user.setPhoto("");
        else
            user.setPhoto(firebaseUser.getPhotoUrl().toString());

        return user;
    }

}
