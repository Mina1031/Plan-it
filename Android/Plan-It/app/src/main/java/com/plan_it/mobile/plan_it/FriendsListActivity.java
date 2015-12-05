package com.plan_it.mobile.plan_it;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class FriendsListActivity extends AppCompatActivity {
    public ArrayList<FriendListModel> friendsList = new ArrayList<>();
    ListView list;
    FriendsListAdapter adapter;
    Context context = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);
        fillFriendsList();
        list = (ListView)findViewById(R.id.friends_list_view);
        list.setAdapter(new FriendsListAdapter(this, R.layout.friends_list_item, friendsList));
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
    }
    private int GetImageResource(ImageView imageView)
    {
        return (Integer)imageView.getTag();
    }
    public void fillFriendsList()
    {
        friendsList.add(new FriendListModel("KevinMurphy", R.drawable.mickey_mouse_icon, true));
        friendsList.add(new FriendListModel("JoanneTanson", R.drawable.cottage_26_waterside_248, false));
        friendsList.add(new FriendListModel("KristianCastaneda", R.drawable.riot_fest_325, false));
        friendsList.add(new FriendListModel("LukeFarnell", R.drawable.mickey_mouse_icon, true));
        friendsList.add(new FriendListModel("KamranSyed", R.drawable.canot_camp_000, true));
        friendsList.add(new FriendListModel("MohammedSumon", R.drawable.ic_perm_identity_black_24dp, true));
        friendsList.add(new FriendListModel("AminaAbbasi", R.drawable.victoria_snowboard_mount_washington_small, true));
        friendsList.add(new FriendListModel("JeffTyber", R.drawable.ic_account_circle_blue_24dp, false));
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
}
