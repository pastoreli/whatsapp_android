package com.pastoreli.whatsapp.controller;

import com.google.firebase.database.DatabaseReference;
import com.pastoreli.whatsapp.config.FirebaseConfig;
import com.pastoreli.whatsapp.helpers.CustomBase64;
import com.pastoreli.whatsapp.model.Message;
import com.pastoreli.whatsapp.model.User;

public class MessageController {

    public void saveMessageData (String idDestinatary, Message message) {
        saveMessageToStorage(message.getIdUser(), idDestinatary, message);
        saveMessageToStorage(idDestinatary, message.getIdUser(), message);
    }

    public void saveGroupMessageData (String idSender, String idDestinatary, Message message) {
        saveMessageToStorage(idSender, idDestinatary, message);
    }

    private void saveMessageToStorage (String idSender, String idDestinatary, Message message) {
        DatabaseReference database = FirebaseConfig.getFirebaseDatabase();
        DatabaseReference messageRef = database.child("messages");

        messageRef.child(idSender)
                .child(idDestinatary)
                .push()
                .setValue(message);
    }

}
