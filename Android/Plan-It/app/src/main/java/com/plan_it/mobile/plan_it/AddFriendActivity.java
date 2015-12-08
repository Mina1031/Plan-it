package com.plan_it.mobile.plan_it;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class AddFriendActivity extends AppCompatActivity {
    int friendId;
    int userID;
    TextView searchResult;
    EditText friendName;
    String friendNameString = "";
    Button addFriend;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        searchResult = (TextView)findViewById(R.id.txt_search_for_friend_result);
        userID = LoginActivity.userID;
        context = this;
    }

    public void SearchForFriend(View v)
    {
        searchResult.setText("");
        addFriend = (Button)findViewById(R.id.btn_add_friend);
        friendName = (EditText)findViewById(R.id.search_for_friend_input);
        addFriend.setVisibility(View.INVISIBLE);

        friendNameString = friendName.getText().toString();

        Toast.makeText(getApplicationContext(),"Searching for: " + friendNameString, Toast.LENGTH_LONG).show();

        if(friendNameString != null && friendNameString != "") {
            RestClient.get("/search/" + friendNameString + "/friend", null, LoginActivity.token, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                    try {
                        JSONObject friend = response;

                        searchResult.setText("User Name: " + friend.getString("friendlyName") +
                                "\nEmail: " + friend.getString("email") +
                                "\nFirst Name:" + friend.getString("firstName") +
                                "\nLast Name:" + friend.getString("lastName"));
                        addFriend.setVisibility(View.VISIBLE);
                        friendId = friend.getInt("UserID");

                        Toast.makeText(getApplicationContext(), "Success, " + friendNameString + " has been found", Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] header, Throwable throwable, JSONObject response) {
                    Toast.makeText(getApplicationContext(), "Could Not Find User: " + friendNameString, Toast.LENGTH_LONG).show();
                    addFriend.setVisibility(View.INVISIBLE);
                    searchResult.setText("No User Found");
                }
            });
        }
        else if(friendNameString == null || friendNameString == "")
        {
            Toast.makeText(getApplicationContext(), "Enter a valid User Name or Email: " + friendNameString, Toast.LENGTH_LONG).show();
            searchResult.setText("Please Enter a Valid Email or User Name");
        }
        searchResult.setVisibility(View.VISIBLE);
    }

    public void AddFriend(View v)
    {
        RequestParams jdata = new RequestParams();
        jdata.put("userID", friendId);
        jdata.put("id", userID);
        Toast.makeText(getApplicationContext(),"Adding, " + friendNameString + " to your friends list", Toast.LENGTH_LONG).show();
        RestClient.post("/user/" + userID + "/friend", jdata, LoginActivity.token, new JsonHttpResponseHandler() {
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Toast.makeText(getApplicationContext(), "Success, " + response + " has been Added to your list", Toast.LENGTH_LONG).show();
                NavToFriendList();
            }

            @Override
            public void onFailure(int statusCode, Header[] header, Throwable throwable, JSONObject response) {
                Toast.makeText(getApplicationContext(), "Failure, Unable to add: " + response, Toast.LENGTH_LONG).show();
            }
        });
        NavToFriendList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_friend, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_friends_list)
        {
            NavToFriendList();
        }
        if (id == R.id.action_create_new_event)
        {
            Intent intent = new Intent(this, CreateEventActivity.class);
            startActivity(intent);
        }
        if (id == R.id.action_event_list)
        {
            Intent intent = new Intent(this, EventsListActivity.class);
            startActivity(intent);
        }
        if(id == R.id.action_refresh)
        {
            Intent intent = getIntent();
            startActivity(intent);
        }
        if (id == R.id.action_logout)
        {
            LoginActivity.token = null;
            LoginActivity.userID = 0;
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
    public void NavToFriendList()
    {
        Intent intent = new Intent(context, FriendsListActivity.class);
        startActivity(intent);
    }
}
