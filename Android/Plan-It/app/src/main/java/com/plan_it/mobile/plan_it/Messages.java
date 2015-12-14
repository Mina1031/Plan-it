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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class Messages extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    ArrayList<MessageModel> messages = new ArrayList<>();
    int messageId;
    ListView listView;
    EditText txt_message;
    String message = "";
    String token;
    int eventID;
    int userId = LoginActivity.userID;
    Context context = this;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getExtras();
        setContentView(R.layout.activity_messages);

        txt_message = (EditText)findViewById(R.id.txt_message);
        txt_message.setText("");
        ImageButton send = (ImageButton)findViewById(R.id.btn_send_message);
        GetMessages(messageId);

    }


    public void GetMessages(int id)
    {
        RestClient.get("/message/" + eventID + "/id/" + id, null, LoginActivity.token, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray messageArray) {
               // Log.d("onSuccess: ", messageArray.toString());
                JSONObject list_message = null;
                try {
                    list_message = messageArray.getJSONObject(0);
                    JSONArray messageList = list_message.getJSONArray("messages");
                    JSONObject messageObject = null;
                    for (int i = 0; i < messageList.length(); i++) {
                        messageObject = messageList.getJSONObject(i);
                        messages.add(new MessageModel(messageObject.getInt("userID"), messageObject.getString("friendlyName"), messageObject.getString("Message"), messageObject.getString("time")));

                    Log.d("Message: ", list_message.toString());
                    }
                    listView = (ListView)findViewById(R.id.messages_list_view);
                    listView.setAdapter(new MessageAdapter(context, R.layout.message, messages));

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
        getMenuInflater().inflate(R.menu.menu_messages, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == R.id.action_refresh)
        {
            Intent intent = getIntent();
            startActivity(intent);
        }
        if (id == R.id.action_event_list)
        {
            Intent intent = new Intent(this, EventsListActivity.class);
            startActivity(intent);
        }
        if (id == R.id.action_friends_list)
        {
            Intent intent = new Intent(this, FriendsListActivity.class);
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
        if (id == R.id.action_logout)
        {
            LoginActivity.token = null;
            LoginActivity.userID = 0;
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    public void getExtras(){
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        token = bundle.getString("token");
        eventID = bundle.getInt("eventID");
    }

    public void SendMessage(View v)
    {
        message = txt_message.getText().toString();
        RequestParams jdata = new RequestParams();
        jdata.put("message", message);
        if(message != "" && message != null)
        {
            RestClient.post("/message/" + eventID, jdata, token, new JsonHttpResponseHandler() {

            });
            Toast.makeText(this, "sent", Toast.LENGTH_LONG);
        }
        else
        {
            Toast.makeText(this, "Please Type a message \nbefore hitting send", Toast.LENGTH_LONG);
        }
        txt_message.setText("");
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        //messages = null;
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }
}
