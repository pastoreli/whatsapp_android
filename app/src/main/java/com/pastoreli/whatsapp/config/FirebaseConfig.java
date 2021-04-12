package com.pastoreli.whatsapp.config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FirebaseConfig {

    private static FirebaseAuth authentication;
    private static DatabaseReference firebase;
    private  static StorageReference storage;

    public static DatabaseReference getFirebaseDatabase () {
        if(firebase == null)
            firebase = FirebaseDatabase.getInstance().getReference();

        return firebase;
    }

    public static FirebaseAuth getFirebaseAuthentication () {
        if(authentication == null)
            authentication = FirebaseAuth.getInstance();

        return authentication;
    }

    public static StorageReference getFirebaseStorage () {
        if(storage == null)
            storage= FirebaseStorage.getInstance().getReference();

        return storage;
    }

}
