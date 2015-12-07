package com.plan_it.mobile.plan_it;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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

public class FriendsListActivity extends AppCompatActivity {
    public ArrayList<FriendListModel> friendsList = new ArrayList<>();
    ListView list;
    private ArrayList<FriendListModel> mFriends;
    FriendsListAdapter adapter;
    Context context = this;
    int userID = LoginActivity.userID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try
        {
            fillFriendsList();
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

    }
    private int GetImageResource(ImageView imageView)
    {
        return (Integer)imageView.getTag();
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Delete Friend");
        menu.add(0, v.getId(), 0, "Are you sure?");
    }

    public void fillFriendsList()throws JSONException {
        RequestParams jdata = new RequestParams();
        jdata.put("id", userID);

        Log.d("fillFriendsList: ", " The method has been hit");

        RestClient.get("/user/" + userID + "/friend", null, LoginActivity.token, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray friendsArray) {
                Log.d("onSuccess: ", friendsArray.toString());
                JSONObject friend = null;
                try {
                    setContentView(R.layout.activity_friends_list);

                    for (int i = 0; i < friendsArray.length(); i++)
                    {
                        friend = friendsArray.getJSONObject(i);
                        friendsList.add(new FriendListModel(friend.getInt("UserID"), friend.getString("friendlyName"), R.drawable.ic_perm_identity_black_24dp, true));
                       Log.d("Friend: ", friend.toString());
                    }

                    list = (ListView)findViewById(R.id.friends_list_view);
                    list.setAdapter(new FriendsListAdapter(context, R.layout.friends_list_item, friendsList));
                    list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            ImageView imgIsFavourite = (ImageView) view.findViewById(R.id.friends_list_favourite_star);

                            if (GetImageResource(imgIsFavourite) == R.drawable.ic_favorite_blue_48dp)
                            {
                                imgIsFavourite.setImageResource(R.drawable.ic_favorite_border_blue_48dp);
                                friendsList.get(position).IsFavourite = false;

                                Toast.makeText(context, "Removed from favourites",
                                        Toast.LENGTH_LONG).show();
                            }
                            else
                            {
                                imgIsFavourite.setImageResource(R.drawable.ic_favorite_blue_48dp);
                                friendsList.get(position).IsFavourite = true;
                                Toast.makeText(context, "Added to favourites",
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    });

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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void navAddFriend(View v)
    {
        Intent intent = new Intent(this, AddFriendActivity.class);
        startActivity(intent);
    }
}
