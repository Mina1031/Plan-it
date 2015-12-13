package com.plan_it.mobile.plan_it;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class FriendsListActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{
    public ArrayList<FriendListModel> friendsList = new ArrayList<>();
    ListView list;
    boolean isFromEditEvent = false;
    int eventID;
    int friendID;
    String friendName;
    private ArrayList<FriendListModel> mFriends;
    public Context context = this;
    int userID = LoginActivity.userID;
    String token = LoginActivity.token;

    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        setContentView(R.layout.activity_friends_list);

        if(intent.getExtras() != null) {
            isFromEditEvent = bundle.getBoolean("isFromEditEvent");
            eventID = bundle.getInt("eventID");
        }
        try
        {
            fillFriendsList();
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout_friend_list);
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        friendsList = null;
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    private int GetImageResource(ImageView imageView)
    {
        return (Integer)imageView.getTag();
    }

    public void fillFriendsList()throws JSONException {

        RestClient.get("/user/" + userID + "/friend", null, LoginActivity.token, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray friendsArray) {
                Log.d("onSuccess: ", friendsArray.toString());
                JSONObject friend = null;
                try {
                    for (int i = 0; i < friendsArray.length(); i++)
                    {
                        friend = friendsArray.getJSONObject(i);
                        friendsList.add(new FriendListModel(friend.getInt("UserID"), friend.getString("friendlyName"), R.drawable.ic_perm_identity_black_24dp, true));
                        friendID = friend.getInt("UserID");
                        friendName = friend.getString("friendlyName");
                        Log.d("Friend: ", friend.toString());
                    }

                    list = (ListView)findViewById(R.id.friends_list_view);
                    list.setAdapter(new FriendsListAdapter(context, R.layout.friends_list_item, friendsList, eventID, isFromEditEvent));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] header, Throwable throwable, JSONObject response) {
                Toast.makeText(getApplicationContext(), "FAILURE", Toast.LENGTH_LONG).show();
            }

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_friends_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        if (id == R.id.action_event_list)
        {
            Intent intent = new Intent(this, EventsListActivity.class);
            startActivity(intent);
        }
        if (id == R.id.action_add_friends)
        {
            Intent intent = new Intent(this, AddFriendActivity.class);
            startActivity(intent);
        }
        if (id == R.id.action_create_new_event)
        {
            Intent intent = new Intent(this, CreateEventActivity.class);
            startActivity(intent);
        }
        if(id == R.id.action_refresh)
        {
            Intent intent = getIntent();
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    public void navAddFriend(View v)
    {
        Intent intent = new Intent(this, AddFriendActivity.class);
        startActivity(intent);
    }
    public void Refresh()
    {
        Intent intent = getIntent();
        startActivity(intent);
    }

    public void inviteFriend(int friendId) throws JSONException{
        RestClient.post("/events/" + eventID + "/invite/" + friendId, null, token, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Toast.makeText(getApplicationContext(), "Success, Friend: " + friendName + " Was added\n" + response, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Toast.makeText(getApplicationContext(), "FAILURE, Friend: " + friendName + " Could not be added\n" + responseString, Toast.LENGTH_LONG).show();
            }
        });
    }
    public void RemoveFriend(int id, int friendId)
    {
        swipeRefreshLayout.setRefreshing(true);
        RequestParams jdata = new RequestParams();
        jdata.put("userID", friendId);
        jdata.put("id", id);
        Log.d("Removing User: ", Integer.toString(id));
        RestClient.delete("/user/" + id + "/friend/" + friendId, jdata, LoginActivity.token, new JsonHttpResponseHandler() {
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Toast.makeText(context, "Success, " + response + " has been removed from your list", Toast.LENGTH_LONG).show();
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] header, Throwable throwable, JSONObject response) {
                Toast.makeText(context, "Failure, Unable to remove: " + response, Toast.LENGTH_LONG).show();
            }
        });
    Refresh();
    }
}
