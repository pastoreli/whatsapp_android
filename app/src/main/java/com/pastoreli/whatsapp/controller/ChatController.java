package com.pastoreli.whatsapp.controller;

import com.google.firebase.database.DatabaseReference;
import com.pastoreli.whatsapp.config.FirebaseConfig;
import com.pastoreli.whatsapp.model.Chat;
import com.pastoreli.whatsapp.model.Message;
import com.pastoreli.whatsapp.model.User;

public class ChatController {

    public void saveChatData (Chat chat, User currentUser) {
        saveChatToStorage(chat.getIdSender(), chat.getIdDestinatary(), chat);
        chat.setDisplayUser(currentUser);
        saveChatToStorage(chat.getIdDestinatary(), chat.getIdSender(), chat);
    }

    public void saveChatGroupData (Chat chat) {
        saveChatToStorage(chat.getIdSender(), chat.getIdDestinatary(), chat);
    }

    private void saveChatToStorage (String idSender, String idDestinatary, Chat chat) {
        DatabaseReference database = FirebaseConfig.getFirebaseDatabase();
        DatabaseReference messageRef = database.child("chats");

        messageRef.child(idSender)
                .child(idDestinatary)
                .setValue(chat);
    }

}
