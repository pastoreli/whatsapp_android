package com.pastoreli.whatsapp.controller;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.pastoreli.whatsapp.config.FirebaseConfig;
import com.pastoreli.whatsapp.helpers.CustomBase64;
import com.pastoreli.whatsapp.model.Chat;
import com.pastoreli.whatsapp.model.Group;
import com.pastoreli.whatsapp.model.User;

public class GroupController {

    private DatabaseReference database = FirebaseConfig.getFirebaseDatabase();

    public String generateGroupId () {
        DatabaseReference groupRef = database.child("groups");

        return groupRef.push().getKey();
    }

    public void saveGroup (Group group) {

        ChatController chatController = new ChatController();

        DatabaseReference groupRef = database.child("groups");
        groupRef.child(group.getId()).setValue(group);

        for(User member : group.getMembers()) {
            Log.i("SAVE_GROUP", member.getIdUser());
            String idSender = member.getIdUser();
            String idDestinatary = group.getId();

            Chat chat = new Chat();
            chat.setIsGroup("true");
            chat.setIdSender(idSender);
            chat.setIdDestinatary(idDestinatary);
            chat.setLastMessage("");
            chat.setGroup(group);

            chatController.saveChatGroupData(chat);
        }

    }

}
