package com.plan_it.mobile.plan_it;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class ItemListActivity extends AppCompatActivity {
    int split = 1;
    private List<Item> mItems;
    private ItemListAdapter adapter;
    private RecyclerView items_recycler_view;
    String token;
    public static int eventID;
    String totalActual;
    String totalEstimate;
    String splitActual;
    String splitEstimate;
    TextView totAct;
    TextView totEst;
    TextView splitAct;
    TextView splitEst;
    TextView splitNumber;
    ImageView minusSplit;
    ImageView addSplit;

    Double totalActualCost;
    Double totalEstimatedCost;

    void initializeData()
    {
        try {
            getItemList();
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getExtras();
        setContentView(R.layout.activity_item_list);
        totAct = (TextView)findViewById(R.id.item_list_text_actual_cost);
        totEst = (TextView)findViewById(R.id.item_list_text_estimated_cost);
        splitAct = (TextView)findViewById(R.id.item_list_text_actual_cost_split);
        splitEst = (TextView)findViewById(R.id.item_list_text_estimated_cost_split);
        splitNumber = (TextView)findViewById(R.id.number_split_text);
        minusSplit = (ImageView)findViewById(R.id.remove_split_button);
        addSplit = (ImageView)findViewById(R.id.add_split_button);

        initializeData();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_item_list, menu);
        return true;
    }

    public void addItem(View v)
    {
        final Dialog dialog=new Dialog(this);
        dialog.setContentView(R.layout.additem_dialog);
        dialog.setTitle("Add Item...");
        final EditText dialog_item=(EditText)dialog.findViewById(R.id.dialog_itemName);
        final EditText dialog_estCost=(EditText)dialog.findViewById(R.id.dialog_estCost);
        final EditText dialog_actCost=(EditText)dialog.findViewById(R.id.dialog_actCost);

        Button dialogButton = (Button) dialog.findViewById(R.id.dialog_add);
        // if button is clicked, close the custom dialog
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String value_item = dialog_item.getText().toString();
                Double value_estCost=Double.parseDouble(dialog_estCost.getText().toString());
                Double value_actCost =Double.parseDouble(dialog_actCost.getText().toString());

                try {
                    createListItem(value_item, value_estCost, value_actCost);
                    Intent i = getIntent();
                    startActivity(i);
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
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

    public void splitChanged(View v)
    {

    }

    public void getExtras(){
        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        token = b.getString("token");
        eventID = b.getInt("eventID");
    }

    public void createListItem(String itName, double estCost, double actCost) throws JSONException{
        RequestParams data = new RequestParams();
        data.put("item", itName);
        data.put("estCost",estCost);
        data.put("whoseBringing",0);
        data.put("actCost", actCost);

        RestClient.post("events/" + eventID + "/list", data, token, new JsonHttpResponseHandler() {
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

    public void getItemList() throws JSONException {
        RestClient.get("events/" + eventID + "/list", null, LoginActivity.token, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray itemsList) {
                // Pull out the first event on the public timeline
                JSONObject items = null;
                mItems = new ArrayList<>();
                String whoseBringing;
                Double totalAct = 0.0;
                Double totalEst = 0.0;
                try {
                    for (int i = 0; i < itemsList.length(); i++) {
                        items = itemsList.getJSONObject(i);
                        int itemId = items.getInt("ListID");
                        String itemName = items.getString("item");
                        Double estimatedCost = items.getDouble("estCost");
                        Double actualCost = items.getDouble("actCost");
                        totalAct = totalAct + actualCost;
                        totalEst = totalEst + estimatedCost;

                        if (items.has("whoseBringing")) {
                            whoseBringing = items.getString("whoseBringing");
                        } else {
                            whoseBringing = "tap to claim";
                        }

                        mItems.add(new Item(itemId, itemName, whoseBringing, estimatedCost, actualCost));
                    }

                    totalActualCost = totalAct;
                    totalEstimatedCost = totalEst;

                    splitNumber.setText(String.valueOf(split));

                    Double spActual = totalActualCost / split;
                    Double spEstimate = totalEstimatedCost / split;
                    splitActual = String.valueOf(String.format("%.2f",spActual));
                    splitEstimate = String.valueOf(String.format("%.2f",spEstimate));
                    totalActual = String.valueOf(String.format("%.2f",totalActualCost));
                    totalEstimate = String.valueOf(String.format("%.2f",totalEstimatedCost));

                    totAct.setText("$" + totalActual);
                    totEst.setText("$" + totalEstimate);

                    splitAct.setText("$" + splitActual);
                    splitEst.setText("$" + splitEstimate);

                    minusSplit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            split--;
                            if(split <= 0){
                                split = 1;
                            }
                            splitNumber.setText(String.valueOf(split));
                            Double spActual = totalActualCost / split;
                            Double spEstimate = totalEstimatedCost / split;
                            splitActual = String.valueOf(String.format("%.2f", spActual));
                            splitEstimate = String.valueOf(String.format("%.2f", spEstimate));
                            totalActual = String.valueOf(String.format("%.2f", totalActualCost));
                            totalEstimate = String.valueOf(String.format("%.2f", totalEstimatedCost));

                            totAct.setText("$" + totalActual);
                            totEst.setText("$" + totalEstimate);

                            splitAct.setText("$" + splitActual);
                            splitEst.setText("$" + splitEstimate);
                        }
                    });
                    addSplit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            split++;
                            splitNumber.setText(String.valueOf(split));
                            Double spActual = totalActualCost / split;
                            Double spEstimate = totalEstimatedCost / split;
                            splitActual = String.valueOf(String.format("%.2f", spActual));
                            splitEstimate = String.valueOf(String.format("%.2f", spEstimate));
                            totalActual = String.valueOf(String.format("%.2f", totalActualCost));
                            totalEstimate = String.valueOf(String.format("%.2f", totalEstimatedCost));

                            totAct.setText("$" + totalActual);
                            totEst.setText("$" + totalEstimate);

                            splitAct.setText("$" + splitActual);
                            splitEst.setText("$" + splitEstimate);
                        }
                    });





                    adapter = new ItemListAdapter(getApplicationContext(), mItems);
                    items_recycler_view = (RecyclerView) findViewById(R.id.item_list_recycler_view);
                    LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
                    items_recycler_view.setLayoutManager(llm);
                    items_recycler_view.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] header, Throwable throwable, JSONObject response) {
            }

        });
    }
}
