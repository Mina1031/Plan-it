package com.plan_it.mobile.plan_it;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Kevin on 13-Dec-2015.
 */
public class MessageAdapter extends ArrayAdapter<MessageModel>{
    ArrayList<MessageModel> messages = new ArrayList<>();
    Context context;
    int resource;
    View view;
    MessageHolder messageHolder;

    public MessageAdapter(Context context, int resource,  ArrayList messages){
        super(context, resource, messages);
        this.messages = messages;
        this.resource = resource;
        this.context = context;
    }
    public View getView(int position, View view, ViewGroup parent) {
        this.view = view;
        View rowView = view;

        if (rowView == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            rowView = inflater.inflate(resource, parent, false);
            messageHolder = new MessageHolder();

            messageHolder.friendlyName = (TextView) rowView.findViewById(R.id.txt_message_user);
            messageHolder.message = (TextView) rowView.findViewById(R.id.txt_message_received);
            messageHolder.time = (TextView) rowView.findViewById(R.id.txt_message_time_stamp);
            messageHolder.imgUser = (ImageView) rowView.findViewById(R.id.img_message_user);

            messageHolder.friendlyName.setText(messages.get(position).friendlyName);
            messageHolder.imgUser.setImageResource(R.drawable.ic_account_circle_blue_24dp);
            messageHolder.message.setText(messages.get(position).message);
            messageHolder.time.setText(messages.get(position).time);

        }
        else{messageHolder = (MessageHolder)rowView.getTag();}
        return rowView;
    }
    static class MessageHolder
    {
        TextView friendlyName;
        TextView time;
        TextView message;
        ImageView imgUser;
    }
}
