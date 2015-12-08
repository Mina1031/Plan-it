package com.plan_it.mobile.plan_it;

/**
 * Created by Kevin on 10-Nov-2015.
 */
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class ItemListAdapter extends RecyclerView.Adapter<ItemListAdapter.ItemListViewHolder>{
    public static View view;
    private Context context;
    private List<Item> mItems;
    String actCost;
    String estCost;
    String itemName;
    String provider;
    String claimer;
    EditText itemText;
    TextView whoseBringing;
    EditText estCostText;
    EditText actCostText;
    ImageView editEstCostButton;
    ImageView editActCostButton;
    ImageView deleteItem;
    ImageView editItemButton;
    ImageView claimButton;

    public class ItemListViewHolder extends RecyclerView.ViewHolder {
        CardView cv;

        String currency = "$";
        int itemId;


        public ItemListViewHolder(final View itemView) {
            super(itemView);
            deleteItem = (ImageView) itemView.findViewById(R.id.item_list_delete_button);
            cv = (CardView) itemView.findViewById(R.id.item_list_card_view);
            itemText = (EditText) itemView.findViewById(R.id.item_list_text_item);
            whoseBringing = (TextView) itemView.findViewById(R.id.item_list_text_supplying);
            estCostText = (EditText) itemView.findViewById(R.id.item_list_text_estimated_cost);
            actCostText = (EditText) itemView.findViewById(R.id.item_list_text_actual_cost);
            editEstCostButton = (ImageView) itemView.findViewById(R.id.item_list_estimated_cost_edit);
            editActCostButton = (ImageView) itemView.findViewById(R.id.item_list_actual_cost_edit);
            editItemButton = (ImageView) itemView.findViewById(R.id.item_list_edit_itemname);
            view = itemView;
            whoseBringing.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    try {
                        whoseBringing.setText(getUserName());
                        claimItem(itemId);
                        Toast.makeText(view.getContext(), "CLAIMED", Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            });
            deleteItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        deleteItem(itemId);
                        Toast.makeText(view.getContext(), "DELETED", Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            });
            itemText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        try {
                            itemName = itemText.getText().toString();
                            updateListItem(itemName, itemId);
                            Toast.makeText(view.getContext(), "Successfully Updated", Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        return true;
                    }
                    return false;
                }
            });
            estCostText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {

                        try {
                            estCost = estCostText.getText().toString();
                            updateListItem(estCost, itemId);
                            Toast.makeText(view.getContext(), "Successfully Updated", Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        return true;
                    }
                    return false;
                }
            });
            actCostText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {

                        try {
                            actCost = actCostText.getText().toString();
                            updateListItem(actCost, itemId);
                            Toast.makeText(view.getContext(), "Successfully Updated", Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        return true;
                    }
                    return false;
                }
            });

        }

        public void bind(Item item){
            itemId = item.listID;
            itemText.setText(item.item);
            provider = item.providing;
            whoseBringing.setText(provider);
            estCostText.setText(Double.toString(item.estCost));
            actCostText.setText(Double.toString(item.actCost));
        }
    }

    public String getUserName() {
        RestClient.get("/search/" + LoginActivity.userID + "/user", null, LoginActivity.token, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // If the response is JSONObject instead of expected JSONArray
                JSONObject res;
                String userName;
                try {
                    res = response;
                    userName = res.getString("friendlyName");
                    claimer = userName;

                } catch (JSONException ex) {
                    ex.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] header, Throwable throwable, JSONObject response) {
                Toast.makeText(view.getContext(), "GET USERNAME FAIL", Toast.LENGTH_LONG).show();
            }

        });
        return claimer;
    }

    public void claimItem(int id) throws JSONException{

        RestClient.post("events/" + ItemListActivity.eventID + "/claim/" + id, null, LoginActivity.token, new JsonHttpResponseHandler() {
            public void onSuccess(String response) {
                JSONObject res;
                try {
                    res = new JSONObject(response);
                    Log.d("debug", res.getString("some_key")); // this is how you get a value out
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
    }
    public void updateListItem(String change, int id) throws JSONException{
        RequestParams updatedData = new RequestParams();
        if(change == itemName){
            updatedData.put("item",itemName);
        }
        else if(change == estCost){
            updatedData.put("estCost",Double.parseDouble(estCost));
        }
        else if(change == actCost){
            updatedData.put("actCost",Double.parseDouble(actCost));
        }

        RestClient.put("events/" + ItemListActivity.eventID + "/list/" + id, updatedData, LoginActivity.token, new JsonHttpResponseHandler() {
            public void onSuccess(String response) {
                JSONObject res;
                try {
                    res = new JSONObject(response);
                    Log.d("debug", res.getString("some_key")); // this is how you get a value out
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });

    }
    public void deleteItem(int id) throws JSONException {
        RestClient.delete("events/" + ItemListActivity.eventID + "/list/" + id,null, LoginActivity.token, new JsonHttpResponseHandler() {
            public void onSuccess(String response) {
                JSONObject res;
                try {

                    res = new JSONObject(response);
                    Log.d("debug", res.getString("some_key")); // this is how you get a value out
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
    }
    public ItemListAdapter(Context context, List<Item> items)
    {
        this.context = context;
        mItems = new ArrayList<>(items);
    }
    @Override
    public int getItemCount() {
        return mItems.size();
    }
    public void setItems(List<Item> items)
    {
        mItems = new ArrayList<>(items);
    }

    @Override
    public ItemListViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item, viewGroup, false);
        ItemListViewHolder vh = new ItemListViewHolder(itemView);
        return vh;
    }

    @Override
    public void onBindViewHolder(ItemListViewHolder holder, int i) {
        final Item item = mItems.get(i);
        holder.bind(item);
    }
}
