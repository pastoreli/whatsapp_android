package com.pastoreli.whatsapp.model;

import java.io.Serializable;

public class Chat implements Serializable {

    private String idSender;
    private String idDestinatary;
    private String lastMessage;
    private User displayUser;
    private String isGroup;
    private Group group;

    public Chat() {
        this.setIsGroup("false");
    }

    public String getIdSender() {
        return idSender;
    }

    public void setIdSender(String idSender) {
        this.idSender = idSender;
    }

    public String getIdDestinatary() {
        return idDestinatary;
    }

    public void setIdDestinatary(String idDestinatary) {
        this.idDestinatary = idDestinatary;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public User getDisplayUser() {
        return displayUser;
    }

    public void setDisplayUser(User displayUser) {
        this.displayUser = displayUser;
    }

    public String getIsGroup() {
        return isGroup;
    }

    public void setIsGroup(String isGroup) {
        this.isGroup = isGroup;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }
}
