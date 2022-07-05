package com.evanemran.quickfeed.globals;

import com.evanemran.quickfeed.models.User;

public class GlobalUser {
    User user;
    private static final GlobalUser globalUser = new GlobalUser();
    public static GlobalUser getInstance() {
        return globalUser;
    }
    private GlobalUser() {
    }
    public void setData(User newUser) {
        this.user = newUser;
    }
    public User getData() {
        return user;
    }
}
