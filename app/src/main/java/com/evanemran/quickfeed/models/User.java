package com.evanemran.quickfeed.models;

public class User {
    String userId;
    String userName;
    String userFullName;
    String userMail;
    String userPhone;
    String userPhoto;
    String userBio;

    public User(String userId, String userName, String userFullName, String userMail, String userPhone, String userPhoto) {
        this.userId = userId;
        this.userName = userName;
        this.userFullName = userFullName;
        this.userMail = userMail;
        this.userPhone = userPhone;
        this.userPhoto = userPhoto;
    }

    public User() {
    }

    public String getUserBio() {
        return userBio;
    }

    public void setUserBio(String userBio) {
        this.userBio = userBio;
    }

    public String getUserMail() {
        return userMail;
    }

    public void setUserMail(String userMail) {
        this.userMail = userMail;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getUserPhoto() {
        return userPhoto;
    }

    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }
}
