package com.plan_it.mobile.plan_it;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by Kristian on 13/12/2015.
 */
public class BudgetListAdapter extends ArrayAdapter<Members> {
    ArrayList<Members> budgetList;
    Context context;
    int resource;
    BudgetHolder member;
    public BudgetListAdapter(Context context, int resource, ArrayList<Members> budgetList){
        super(context, resource, budgetList);
        this.context = context;
        this.resource = resource;
        this.budgetList = budgetList;
    }

    public View getView (final int i, View view, ViewGroup parent) {
        View rowView = view;
       // final int i = position;
        if(rowView == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            rowView = inflater.inflate(resource, parent, false);

            member = new BudgetHolder();

            member.txtBudgetMember = (TextView) rowView.findViewById(R.id.budget_list_friend_id);
            member.imgBudgetProfilePic = (ImageView) rowView.findViewById(R.id.budget_list_profile_image);
            member.isPaying = (CheckBox) rowView.findViewById(R.id.budget_list_isPaying);
        }
        else
        {
            member = (BudgetHolder)rowView.getTag();
        }
        member.txtBudgetMember.setTag(i);
        member.txtBudgetMember.setText(budgetList.get(i).memberName);
        member.imgBudgetProfilePic.setImageResource(R.drawable.ic_account_circle_blue_24dp);
        member.isPaying.setChecked(budgetList.get(i).isPaying);
            member.isPaying.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    try {
                        ((BudgetListActivity) context).setPaying(budgetList.get(i).memberId, budgetList.get(i).isPaying);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        rowView.setTag(member);
        return rowView;
    }


    static class BudgetHolder
    {
        TextView txtBudgetMember;
        ImageView imgBudgetProfilePic;
        CheckBox isPaying;
    }

}
