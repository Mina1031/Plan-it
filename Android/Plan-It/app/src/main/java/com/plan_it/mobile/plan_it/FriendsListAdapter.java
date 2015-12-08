package com.plan_it.mobile.plan_it;

/**
 * Created by Kevin on 30-Nov-2015.
 */

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class FriendsListAdapter extends ArrayAdapter<FriendListModel> {
    ArrayList<FriendListModel> friendsList;
    Context context;
    int resource;
    FriendHolder friend;

    public FriendsListAdapter(Context context, int resource, ArrayList<FriendListModel> friendsList) {
        super(context, resource, friendsList);
        this.context = context;
        this.resource = resource;
        this.friendsList = friendsList;
    }

    public View getView(final int position, View view, ViewGroup parent) {
        View rowView = view;
         final int i = position;
        if(rowView == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            rowView = inflater.inflate(resource, parent, false);

            friend = new FriendHolder();

            friend.txtFriendID = (TextView) rowView.findViewById(R.id.friends_list_friend_id);
            friend.imgProfilePic = (ImageView) rowView.findViewById(R.id.friends_list_profile_image);
            friend.imgIsFavourite = (ImageView) rowView.findViewById(R.id.friends_list_favourite_star);
            friend.removeFriend = (ImageView)rowView.findViewById(R.id.friends_list_remove);

        }
        else
        {
            friend = (FriendHolder)rowView.getTag();
        }
        friend.txtFriendID.setTag(position);
        friend.txtFriendID.setText(friendsList.get(position).FriendID);
        friend.imgProfilePic.setImageResource(friendsList.get(position).ProfilePic);
        friend.removeFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RemoveFriend(LoginActivity.userID, friendsList.get(position).UserID);
            }
        });
        rowView.setTag(friend);
        if(friendsList.get(position).IsFavourite)
        {
            friend.imgIsFavourite.setImageResource(R.drawable.ic_favorite_blue_48dp);
            friend.imgIsFavourite.setTag(R.drawable.ic_favorite_blue_48dp);
        }
        else
        {
            friend.imgIsFavourite.setImageResource(R.drawable.ic_favorite_border_blue_48dp);
            friend.imgIsFavourite.setTag(R.drawable.ic_favorite_border_blue_48dp);
        }

        return rowView;
    }
    static class FriendHolder
    {
        ImageView imgProfilePic;
        TextView txtFriendID;
        ImageView imgIsFavourite;
        ImageView removeFriend;
    }
    public void RemoveFriend(int id, int friendId)
    {
        RequestParams jdata = new RequestParams();
        jdata.put("userID", friendId);
        jdata.put("id", id);
        Log.d("Removing User: ", Integer.toString(id));
        Toast.makeText(context, "ID: " + Integer.toString(id), Toast.LENGTH_LONG).show();
        RestClient.delete("/user/" + id + "/friend/" + friendId, jdata, LoginActivity.token, new JsonHttpResponseHandler() {
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Toast.makeText(context, "Success, " + response + " has been Added to your list", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] header, Throwable throwable, JSONObject response) {
                Toast.makeText(context, "Failure, Unable to add: " + response, Toast.LENGTH_LONG).show();
            }
        });
    }
    private int GetImageResource(ImageView imageView)
    {
        return (Integer)imageView.getTag();
    }
}
