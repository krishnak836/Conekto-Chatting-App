package com.example.Conekto.Models;

public class MessageModel {
    private String sender;
    private String receiver;
    private String message;
    private String status;
    private String time;

    public MessageModel() {
    }

    public MessageModel(String sender_id, String receiver_id, String message, String time, String status) {
        this.sender = sender_id;
        this.receiver = receiver_id;
        this.message = message;
        this.time = time;
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
}
