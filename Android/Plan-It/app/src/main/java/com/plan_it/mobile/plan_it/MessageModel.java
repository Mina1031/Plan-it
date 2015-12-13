package com.plan_it.mobile.plan_it;

/**
 * Created by Kevin on 13-Dec-2015.
 */
public class MessageModel {
    int userId;
    String friendlyName;
    String message;
    String time;
    MessageModel(int userId, String friendlyName, String message, String time)
    {
        this.friendlyName = friendlyName;
        this.userId = userId;
        this.message = message;
        this.time = time;
    }
}
