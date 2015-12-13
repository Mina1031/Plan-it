package com.plan_it.mobile.plan_it;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.util.ArrayList;

public class Messages extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    ArrayList<MessageModel> messages = new ArrayList<>();
    ListView listView;
    EditText txt_message;
    String message = "";
    String token;
    int eventID;
    Context context = this;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getExtras();
        GetMessages();
        setContentView(R.layout.activity_messages);

        txt_message = (EditText)findViewById(R.id.txt_message);
        txt_message.setText("");
        ImageButton send = (ImageButton)findViewById(R.id.btn_send_message);
        listView = (ListView)findViewById(R.id.messages_list_view);
        listView.setAdapter(new MessageAdapter(context, R.layout.message, messages));
    }

    public void GetMessages()
    {
        messages.add(new MessageModel(LoginActivity.userID, "Kevin Murphy", "This is a message", "4:50pm"));
        messages.add(new MessageModel(1, "Steven Murphy", "This is also message", "4:54pm"));
        messages.add(new MessageModel(1, "Kevin Bacon", "This is a degree, get a little closer to me", "5:00pm"));
        messages.add(new MessageModel(1, "Kristian", "I is do's the workings", "5:10pm"));
        messages.add(new MessageModel(1, "Computer", "EXTERMINATE, KILL ALL HUMANS...... Daisy, Daisy, \nGive me your answer true.\nI'm half crazy, all for the love of you", "5:50pm"));
        messages.add(new MessageModel(1, "Server", "Yo Human, that computer is acting strange dawg", "6:50pm"));
        messages.add(new MessageModel(1, "Johnny Pneumonic", "I'm in the system, the computer will be defragged", "6:56pm"));
        messages.add(new MessageModel(LoginActivity.userID, "Kevin Murphy", "Um.......... This is weird", "7:50pm"));
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
