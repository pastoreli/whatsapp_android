package com.pastoreli.whatsapp.controller;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pastoreli.whatsapp.config.FirebaseConfig;
import com.pastoreli.whatsapp.helpers.CustomBase64;
import com.pastoreli.whatsapp.helpers.UserFirebaseHelper;
import com.pastoreli.whatsapp.model.User;

import java.util.HashMap;
import java.util.Map;

public class UserController {

    public void saveUserData (User user) {
        String idUser = CustomBase64.Base64Encode(user.getEmail());
        user.setIdUser(idUser);

        DatabaseReference firebase = FirebaseConfig.getFirebaseDatabase();
        firebase.child("users")
                .child(user.getIdUser())
                .setValue(user);
    }

    public void updateUserData (User user) {
        String idUser = UserFirebaseHelper.getUserId();
        DatabaseReference database = FirebaseConfig.getFirebaseDatabase();

        DatabaseReference userRef = database.child("users")
                .child(idUser);

        Map<String, Object> userValues = convertUserToMap(user);

        userRef.updateChildren(userValues);

    }

    public Map<String, Object> convertUserToMap (User user) {
        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("email", user.getEmail());
        userMap.put("name", user.getName());
        userMap.put("photo", user.getPhoto());

        return userMap;
    }

}
