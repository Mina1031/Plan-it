package com.plan_it.mobile.plan_it;

/**
 * Created by Kristian on 21/10/2015.
 */

import android.app.Activity;
import android.content.Context;
import android.media.Image;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.lang.reflect.Member;
import java.util.ArrayList;

public class AttendeeListAdapter extends ArrayAdapter<Members> {
   // private final Activity context;
    ArrayList<Members> memberList;
    Context context;
    int resource;
    MemberHolder member;

    public AttendeeListAdapter(Context context, int resource, ArrayList<Members> memberList){
        super(context, resource, memberList);
        this.context = context;
        this.resource = resource;
        this.memberList = memberList;
    }

    public View getView(int position, View view, ViewGroup parent) {
        View rowView = view;
        final int i = position;
        if(rowView == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            rowView = inflater.inflate(resource, parent, false);

            member = new MemberHolder();

            member.deleteAttendee = (ImageView) rowView.findViewById(R.id.ibDeleteInvitee);
            member.txtAttendeeName = (TextView) rowView.findViewById(R.id.tv_attendeeName);
            member.imgProfilePic = (ImageView) rowView.findViewById(R.id.iv_profilePic);
            member.imgAttendeeStatus = (ImageView) rowView.findViewById(R.id.iv_status);

            rowView.setTag(member);
        }
        else
        {
            member = (MemberHolder)rowView.getTag();
        }
        member.txtAttendeeName.setTag(position);
        member.txtAttendeeName.setText(memberList.get(position).memberName);
        member.imgProfilePic.setImageResource(R.drawable.no_image);

        if(memberList.get(position).status == MemberStatus.ATTENDING)
        {
            member.imgAttendeeStatus.setImageResource(R.drawable.ic_thumb_up_green_24dp);
        }
        else if(memberList.get(position).status == MemberStatus.INVITED)
        {
            member.imgAttendeeStatus.setImageResource(R.drawable.ic_thumb_up_black_24dp);
        }
        else if (memberList.get(position).status == MemberStatus.DECLINED || memberList.get(position).status == MemberStatus.LEFT){
            member.imgAttendeeStatus.setImageResource(R.drawable.ic_thumb_down_red_24dp);
        }
        else if (memberList.get(position).status == MemberStatus.OWNER){
            member.imgAttendeeStatus.setImageResource(R.drawable.ic_favorite_border_black_48dp);
        }

        return rowView;
    }

    static class MemberHolder
    {
        TextView txtAttendeeName;
        ImageView imgProfilePic;
        ImageView imgAttendeeStatus;
        ImageView deleteAttendee;
    }
}
