package com.plan_it.mobile.plan_it;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Mshohid on 2015-11-17.
 *
 */


public class LoginActivity extends AppCompatActivity {

        public static String token;
        public static int userID;
        String user;
        String password;

        EditText etUser, etPass;
        ProgressDialog prgDialog;
        // Error Msg TextView Object
        TextView errorMsg;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        etUser=(EditText)findViewById(R.id.editText);
        etPass=(EditText)findViewById(R.id.editTextPassword);
       /* etUser.setText("nedstark");
        etPass.setText("password");*/


        // Instantiate Progress Dialog object
        prgDialog = new ProgressDialog(this);
        // Set Progress Dialog Text
        prgDialog.setMessage("Please wait...");
        // Set Cancelable as False
        prgDialog.setCancelable(false);


    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu){
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item){
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

    public void loginUser(View v) {
        user = etUser.getText().toString();
        password = etPass.getText().toString();

        login();


    }

    public void login(){
        RequestParams params = new RequestParams();
        if ((password != null) && (user != null)) {
            params.put("username", user);
            // Put Http parameter password with value of Password Edit Value control
            params.put("password", password);
        }
        RestClient.post("login", params, null, new JsonHttpResponseHandler() {
            public void onSuccess(int statusCode, Header[] header, JSONObject response) {
                JSONObject res;
                try {

                    res = response;
                    token = res.getString("token");
                    userID = res.getInt("userID");
                    Log.d("debug", res.getString("token"));
                    Intent intent = new Intent(LoginActivity.this, EventsListActivity.class);
                    startActivity(intent);
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] header,Throwable throwable, JSONObject response){

            }
        });
    }

    public void register(View v)
    {
        Intent register=new Intent(this, RegisterActivity.class );
        startActivity(register);
    }
}

