package com.plan_it.mobile.plan_it;

/**
 * Created by Kevin on 30-Nov-2015.
 */

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

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

    public View getView(int position, View view, ViewGroup parent) {
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

            rowView.setTag(friend);
        }
        else
        {
            friend = (FriendHolder)rowView.getTag();
        }
        friend.txtFriendID.setTag(position);
        friend.txtFriendID.setText(friendsList.get(position).FriendID);
        friend.imgProfilePic.setImageResource(friendsList.get(position).ProfilePic);

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

        friend.imgIsFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(GetImageResource(friend.imgIsFavourite) == R.drawable.ic_favorite_border_blue_48dp)
                {
                    friend.imgIsFavourite.setImageResource(R.drawable.ic_favorite_blue_48dp);
                    friendsList.get(i).IsFavourite = true;
                    Toast.makeText(context, "Added to favourites",
                            Toast.LENGTH_LONG).show();
                }
                else
                {
                    friend.imgIsFavourite.setImageResource(R.drawable.ic_favorite_border_blue_48dp);
                    friendsList.get(i).IsFavourite = false;

                    Toast.makeText(context, "Removed from favourites",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        return rowView;
    }
    static class FriendHolder
    {
        ImageView imgProfilePic;
        TextView txtFriendID;
        ImageView imgIsFavourite;
    }
    private int GetImageResource(ImageView imageView)
    {
        return (Integer)imageView.getTag();
    }
}
