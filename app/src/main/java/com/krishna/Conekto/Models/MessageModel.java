package com.krishna.Conekto.Models;

public class MessageModel {
    private String sender;
    private String receiver;
    private String message;
    private String time;
    private boolean isSeen;

    public MessageModel() {
    }

    public MessageModel(String sender_id, String receiver_id, String message, String time, boolean isSeen) {
        this.sender = sender_id;
        this.receiver = receiver_id;
        this.message = message;
        this.time = time;
        this.isSeen = isSeen;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isSeen() {
        return isSeen;
    }

    public void setSeen(boolean seen) {
        isSeen = seen;
    }
}
