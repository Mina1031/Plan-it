package com.plan_it.mobile.plan_it;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Kevin on 13-Dec-2015.
 */
public class MessageAdapter extends ArrayAdapter<MessageModel>{
    ArrayList<MessageModel> messages = new ArrayList<>();
    Context context;
    int resource;
    MessageHolder messageHolder = new MessageHolder();

    public MessageAdapter(Context context, int resource,  ArrayList messages){
        super(context, resource, messages);
        this.messages = messages;
        this.resource = resource;
        this.context = context;
    }
    public View getView(int position, View view, ViewGroup parent) {
        View rowView = view;

        if (rowView == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            rowView = inflater.inflate(resource, parent, false);
            messageHolder = new MessageHolder();

            //setting the layout
            messageHolder.txtMessageLayout = (LinearLayout)rowView.findViewById(R.id.txt_message_layout);
            messageHolder.userID = (TextView)rowView.findViewById(R.id.txt_message_user_id);
            messageHolder.friendlyName = (TextView) rowView.findViewById(R.id.txt_message_user);
            messageHolder.message = (TextView) rowView.findViewById(R.id.txt_message_received);
            messageHolder.time = (TextView) rowView.findViewById(R.id.txt_message_time_stamp);
            messageHolder.imgUser = (ImageView) rowView.findViewById(R.id.img_message_user);
            messageHolder.imgUserRight = (ImageView) rowView.findViewById(R.id.img_message_user_right);
        }
        else
        {
            messageHolder = (MessageHolder)rowView.getTag();
        }

        if(messages.get(position).userId != LoginActivity.userID)
        {
            //setting layout
            messageHolder.txtMessageLayout.setBackgroundResource(R.drawable.txt_message_received);

            //setting text and images
            messageHolder.friendlyName.setText(messages.get(position).friendlyName);
            messageHolder.imgUser.setImageResource(R.drawable.ic_account_circle_blue_24dp);
            messageHolder.message.setText(messages.get(position).message);
            messageHolder.time.setText(messages.get(position).time);
            messageHolder.userID.setText(Integer.toString(messages.get(position).userId));
        }
        else if(messages.get(position).userId == LoginActivity.userID)
        {
            //setting layout
            messageHolder.txtMessageLayout.setBackgroundResource(R.drawable.txt_bubble_sent);
            messageHolder.imgUserRight.setVisibility(View.VISIBLE);
            messageHolder.imgUser.setVisibility(View.GONE);
            messageHolder.friendlyName.setGravity(Gravity.RIGHT);
            messageHolder.message.setGravity(Gravity.RIGHT);
            messageHolder.time.setGravity(Gravity.LEFT);

            //setting text and images
            messageHolder.userID.setText(Integer.toString(messages.get(position).userId));
            messageHolder.friendlyName.setText(messages.get(position).friendlyName);
            messageHolder.imgUserRight.setImageResource(R.drawable.ic_account_circle_black_24dp);
            messageHolder.message.setText(messages.get(position).message);
            messageHolder.time.setText(messages.get(position).time);
        }
        rowView.setTag(messageHolder);
        return rowView;
    }
    static class MessageHolder
    {
        TextView userID;
        TextView friendlyName;
        TextView time;
        TextView message;
        ImageView imgUser;
        ImageView imgUserRight;
        LinearLayout txtMessageLayout;
    }
}
