package com.example.Conekto.Models;

public class UsersDetailsModel {
    private String fullname;
    private String email;
    private String password;
    private String Image;
    private String uid;
    private String status;

    public UsersDetailsModel() {
    }

    public UsersDetailsModel(String uid, String fullname, String email, String Image, String status) {
        this.fullname = fullname;
        this.Image = Image;
        this.email = email;
        this.uid = uid;
        this.status = status;

    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getImage() {
        return Image;
    }

    public String getUid() {
        return uid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
