package com.plan_it.mobile.plan_it;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

public class Messages extends AppCompatActivity {
    ListView listView;
    EditText txt_message;
    String message = "";
    String token;
    int eventID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getExtras();

        txt_message = (EditText)findViewById(R.id.txt_message);
        ImageButton send = (ImageButton)findViewById(R.id.btn_send_message);
        listView = (ListView)findViewById(R.id.messages_list_view);

        setContentView(R.layout.activity_messages);
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
        Bundle b = intent.getExtras();
        token = b.getString("token");
        eventID = b.getInt("eventID");
    }

    public void GetMessages()
    {

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
    }
}
