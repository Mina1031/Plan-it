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
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

public class Messages extends AppCompatActivity {

    EditText txt_message;
    String message = "";
    String token;
    int eventID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getExtras();
        setContentView(R.layout.activity_messages);
        txt_message = (EditText)findViewById(R.id.txt_message);
        ImageButton send = (ImageButton)findViewById(R.id.btn_send_message);
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void getExtras(){
        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        token = b.getString("token");
        eventID = b.getInt("eventID");
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
