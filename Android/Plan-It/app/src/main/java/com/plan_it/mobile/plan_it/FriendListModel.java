package com.plan_it.mobile.plan_it;

/**
 * Created by Kevin on 01-Dec-2015.
 */
public class FriendListModel {
    public int UserID;
    public String FriendID;
    public Integer ProfilePic;
    public Boolean IsFavourite;

    FriendListModel(int UserID, String FriendID, int ProfilePic, Boolean IsFavourite) {
        this.UserID = UserID;
        this.FriendID = FriendID;
        this.ProfilePic = ProfilePic;
        this.IsFavourite = IsFavourite;
    }

}