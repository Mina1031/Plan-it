package com.plan_it.mobile.plan_it;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

public class ViewEventActvity extends Activity{

    private static final int CAMERA_REQUEST = 1888;

    String base64ImageUpdate;
    Bitmap bmp;
    byte[] imageByte;
    int eventID;
    boolean isFromEditEvent;
    String eTitle;
    String eDesc;
    String eLocation;
    String eFromDate;
    String eToDate;
    String eFromTime;
    String eToTime;
    IsAttending status;
    byte[] byteArray;
    Bitmap eImage;

    Button addMore;
    EditText etTitle;
    EditText etDesc;
    EditText etLocation;
    EditText etFromDate;
    EditText etToDate;
    EditText etFromTime;
    EditText etToTime;
    TextView tvWhoIsComing;
    Button itemList;
    Button messageBoard;
    Button btnGoing;
    Button btnNotGoing;
    Button btnLoadImg;
    ImageView eventImage;
    Button deleteEvent;

    Calendar myCalendar = Calendar.getInstance();

    public ArrayList<Members> mList;
    ListView attendeeList;
    Context context = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event_actvity);

        getBundleValues();
        addMore = (Button)findViewById(R.id.btn_invite_more);
        tvWhoIsComing = (TextView)findViewById(R.id.tvWhoIsComing);
        etTitle = (EditText)findViewById(R.id.etViewEventTitle);
        etDesc = (EditText)findViewById(R.id.etViewEventDescription);
        etLocation = (EditText)findViewById(R.id.etViewEventLocation);
        etFromDate = (EditText)findViewById(R.id.etViewEventFromDate);
        etToDate = (EditText)findViewById(R.id.etViewEventEndDate);
        etFromTime = (EditText)findViewById(R.id.etFromTime);
        etToTime = (EditText)findViewById(R.id.etToTime);
        itemList = (Button)findViewById(R.id.btnViewItemList);
        messageBoard = (Button)findViewById(R.id.btnViewMsgBoard);
        btnGoing = (Button)findViewById(R.id.btnAccept);
        btnNotGoing = (Button)findViewById(R.id.btnDecline);
        deleteEvent = (Button)findViewById(R.id.btnViewDeleteEvent);

        attendeeList = (ListView)findViewById(R.id.attendee_list);

        populateAttendee();

        btnLoadImg = (Button)findViewById(R.id.btnChngPic);
        eventImage = (ImageView)findViewById(R.id.ivViewEventImage);

        btnLoadImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                imageOption();
            }
        });
        onEdit();
        initializeData();

        if(status == IsAttending.ATTENDING){isAttending();}
        else if(status == IsAttending.INVITED){isInvited();}
        else if(status == IsAttending.OWNER){isOwner();}
        else if(status == IsAttending.DECLINED){isDeclined();}
    }

    public void imageOption(){
        final CharSequence[] items = {"Take Photo", "Choose from Library", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(ViewEventActvity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                } else if (items[item].equals("Choose from Library")) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(
                            Intent.createChooser(intent, "Select File"),
                            1);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_REQUEST) {
                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                Bitmap scaled = Bitmap.createScaledBitmap(thumbnail, 256, 256, true);
                imageByte = bytes.toByteArray();
                scaled.compress(Bitmap.CompressFormat.JPEG, 10, bytes);
                base64ImageUpdate = Base64.encodeToString(imageByte, Base64.NO_WRAP);
                eventImage.setImageBitmap(scaled);

                try {
                    updateEvent(base64ImageUpdate);
                    Toast.makeText(getApplicationContext(), "Image Successfully Updated", Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            } else if (requestCode == 1) {
                Uri selectedImageUri = data.getData();
                String[] projection = {MediaStore.MediaColumns.DATA};
                Cursor cursor = managedQuery(selectedImageUri, projection, null, null,
                        null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                cursor.moveToFirst();
                String selectedImagePath = cursor.getString(column_index);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(selectedImagePath, options);

                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                Bitmap d = new BitmapDrawable(getApplicationContext().getResources() , selectedImagePath).getBitmap();
                Bitmap scaled = Bitmap.createScaledBitmap(d,140, 150, true);
                scaled.compress(Bitmap.CompressFormat.JPEG, 10, bytes);

                imageByte = bytes.toByteArray();
                base64ImageUpdate = Base64.encodeToString(imageByte, Base64.NO_WRAP);
                eventImage.setImageBitmap(scaled);

                try {
                    updateEvent(base64ImageUpdate);
                    Toast.makeText(getApplicationContext(), "Image Successfully Updated", Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void populateAttendee(){
        try{
            getMembers();
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    public void getBundleValues(){
        Intent intent = getIntent();
        Bundle eventBundle = intent.getExtras();
        eventID = eventBundle.getInt("eventID");

        eTitle = eventBundle.getString("eventName");
        eDesc = eventBundle.getString("eventDescription");
        eLocation = eventBundle.getString("eventLocation");
        eFromDate = eventBundle.getString("eventFromDate");
        eToDate = eventBundle.getString("eventToDate");
        eFromTime = eventBundle.getString("eventFromTime");
        eToTime = eventBundle.getString("eventToTime");
        status = (IsAttending) eventBundle.get("isAttending");
        byteArray = eventBundle.getByteArray("eventPhoto");
        boolean itemListAccess = eventBundle.getBoolean("itemList");
        boolean messageBoardAccess = eventBundle.getBoolean("messageBoard");
    }
    public void initializeData(){
        etTitle.setText(eTitle);
        etDesc.setText(eDesc);
        etLocation.setText(eLocation);
        etFromDate.setText(eFromDate);
        etToDate.setText(eToDate);
        etFromTime.setText(eFromTime);
        etToTime.setText(eToTime);
        bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        eventImage.setImageBitmap(bmp);
    }

    public void onEdit(){
       etTitle.setOnEditorActionListener(new TextView.OnEditorActionListener() {
           @Override
           public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
               if (actionId == EditorInfo.IME_ACTION_DONE) {
                   try {
                       eTitle = etTitle.getText().toString();
                       updateEvent(eTitle);
                       Toast.makeText(getApplicationContext(), "Successfully Updated", Toast.LENGTH_LONG).show();
                   } catch (JSONException e) {
                       e.printStackTrace();
                   }

                   return true;
               }
               return false;
           }
       });

        etDesc.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {

                    try {
                        eDesc = etDesc.getText().toString();
                        updateEvent(eDesc);
                        Toast.makeText(getApplicationContext(), "Successfully Updated", Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    return true;
                }
                return false;
            }
        });

        etLocation.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {

                    try {
                        eLocation = etLocation.getText().toString();
                        updateEvent(eLocation);
                        Toast.makeText(getApplicationContext(), "Successfully Updated", Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    return true;
                }
                return false;
            }
        });

        etFromDate.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                //Your query to fetch Data
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    eFromDate = etFromDate.getText().toString();
                    updateEvent(eFromDate);
                    Toast.makeText(getApplicationContext(), "Successfully Updated", Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        etToDate.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                //Your query to fetch Data
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    eToDate = etToDate.getText().toString();
                    updateEvent(eToDate);
                    Toast.makeText(getApplicationContext(), "Successfully Updated", Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        etFromTime.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {

                    try {
                        eFromTime = etFromTime.getText().toString();
                        updateEvent(eFromTime);
                        Toast.makeText(getApplicationContext(), "Successfully Updated", Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    return true;
                }
                return false;
            }
        });
        etToTime.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {

                    try {
                        eToTime = etToTime.getText().toString();
                        updateEvent(eToTime);
                        Toast.makeText(getApplicationContext(), "Successfully Updated", Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    return true;
                }
                return false;
            }
        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_event_actvity, menu);
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

    public void isAttending(){
        etTitle.setEnabled(false);
        etTitle.setFocusable(false);
        etDesc.setEnabled(false);
        etDesc.setFocusable(false);
        etLocation.setEnabled(false);
        etLocation.setFocusable(false);
        etFromDate.setEnabled(false);
        etFromDate.setFocusable(false);
        etToDate.setEnabled(false);
        etToDate.setFocusable(false);
        etFromTime.setEnabled(false);
        etFromTime.setFocusable(false);
        etToTime.setEnabled(false);
        etToTime.setFocusable(false);

        btnLoadImg.setVisibility(View.INVISIBLE);
        deleteEvent.setVisibility(View.VISIBLE);
        deleteEvent.setText("Leave Event");
        deleteEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    leaveEvent();
                    Intent i = new Intent(ViewEventActvity.this, EventsListActivity.class);
                    startActivity(i);
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void isDeclined(){
        etTitle.setEnabled(false);
        etTitle.setFocusable(false);
        etDesc.setEnabled(false);
        etDesc.setFocusable(false);
        etLocation.setEnabled(false);
        etLocation.setFocusable(false);
        etFromDate.setEnabled(false);
        etFromDate.setFocusable(false);
        etToDate.setEnabled(false);
        etToDate.setFocusable(false);
        etFromTime.setEnabled(false);
        etFromTime.setFocusable(false);
        etToTime.setEnabled(false);
        etToTime.setFocusable(false);


        deleteEvent.setVisibility(View.GONE);
        itemList.setVisibility(View.GONE);
        messageBoard.setVisibility(View.GONE);
        btnLoadImg.setVisibility(View.GONE);

        btnGoing.setVisibility(View.VISIBLE);
        deleteEvent.setVisibility(View.VISIBLE);
        deleteEvent.setText("Leave Event");
        deleteEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    leaveEvent();
                    Intent i = new Intent(ViewEventActvity.this, EventsListActivity.class);
                    startActivity(i);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        btnGoing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    answerInvite("Attending");
                    Intent intent = new Intent(ViewEventActvity.this, EventsListActivity.class);
                    startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void isInvited(){

        etTitle.setEnabled(false);
        etTitle.setFocusable(false);
        etDesc.setEnabled(false);
        etDesc.setFocusable(false);
        etLocation.setEnabled(false);
        etLocation.setFocusable(false);
        etFromDate.setEnabled(false);
        etFromDate.setFocusable(false);
        etToDate.setEnabled(false);
        etToDate.setFocusable(false);
        etFromTime.setEnabled(false);
        etFromTime.setFocusable(false);
        etToTime.setEnabled(false);
        etToTime.setFocusable(false);


        deleteEvent.setVisibility(View.GONE);
        itemList.setVisibility(View.GONE);
        messageBoard.setVisibility(View.GONE);
        btnLoadImg.setVisibility(View.GONE);

        btnGoing.setVisibility(View.VISIBLE);
        btnNotGoing.setVisibility(View.VISIBLE);

        btnGoing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    answerInvite("Attending");
                    Intent intent = new Intent(ViewEventActvity.this, EventsListActivity.class);
                    startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        btnNotGoing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    answerInvite("Declined");
                    Intent intent = new Intent(ViewEventActvity.this, EventsListActivity.class);
                    startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void isOwner() {
        tvWhoIsComing.setText("Invite People");

        addMore.setVisibility(View.VISIBLE);
        deleteEvent.setVisibility(View.VISIBLE);

        eTitle = etTitle.getText().toString();
        eDesc = etDesc.getText().toString();
        eLocation = etLocation.getText().toString();
        eFromDate = etFromDate.getText().toString();
        eToDate = etToDate.getText().toString();

        etFromDate.setInputType(InputType.TYPE_NULL);
        etFromDate.setOnTouchListener(listener);

        etToDate.setInputType(InputType.TYPE_NULL);
        etToDate.setOnTouchListener(listener);

        addMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Intent i = new Intent(ViewEventActvity.this, FriendsListActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("isFromEditEvent", true);
                    bundle.putInt("eventID", eventID);
                    i.putExtras(bundle);
                    startActivity(i);
            }
        });

        deleteEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    deleteEvent();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Intent i = new Intent(ViewEventActvity.this, EventsListActivity.class);
                startActivity(i);
            }
        });

    }
    View.OnTouchListener listener = new View.OnTouchListener(){
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if(event.getAction() == MotionEvent.ACTION_DOWN) {
                new DatePickerDialog(ViewEventActvity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
            return false;
        }
    };

    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            if(etFromDate.isFocused()){
                updateLabel(0);
            }
            else if(etToDate.isFocused()){
                updateLabel(1);
            }
        }
    };

    private void updateLabel(int id) {
        String myFormat = "MMM/dd/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.CANADA );
        switch(id) {
            case 0:
                etFromDate.setText(sdf.format(myCalendar.getTime()));
                break;
            case 1:
                etToDate.setText(sdf.format(myCalendar.getTime()));
                break;
        }
    }


    public void updateEvent(String change) throws JSONException {
        RequestParams jdata = new RequestParams();
        if(change == eTitle) {
            jdata.put("what", eTitle);
        }
        else if(change == eDesc) {
            jdata.put("why", eDesc);
        }
        else if(change == eLocation) {
            jdata.put("where", eLocation);
        }
        else if(change == eFromDate) {
            jdata.put("when", eFromDate);
        }
        else if(change == eToTime) {
            jdata.put("toTime", eToTime);
        }
        else if(change == eFromTime) {
            jdata.put("fromTime", eFromTime);
        }
        else if(change == eToDate) {
            jdata.put("endDate", eToDate);
        }
        else if(change == base64ImageUpdate){
            jdata.put("picture", base64ImageUpdate);
        }
        RestClient.put("events/" + eventID, jdata, LoginActivity.token, new JsonHttpResponseHandler() {
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

    public void getMembers()throws JSONException{
        RestClient.get("events/" + eventID + "/members", null, LoginActivity.token, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray memberArray) {
                Log.d("onSuccess: ", memberArray.toString());
                JSONObject member = null;
                try {
                    mList = new ArrayList<>();
                    for (int i = 0; i < memberArray.length(); i++) {
                        member = memberArray.getJSONObject(i);
                        int userId = member.getInt("UserId");
                        String friendlyName = member.getString("friendlyName");
                        String status = member.getString("isAttending");
                        MemberStatus memberStatus = MemberStatus.valueOf(status.trim().toUpperCase());
                        mList.add(new Members(userId, friendlyName, memberStatus, true, true));
                        Log.d("Member: ", member.toString());
                    }

                    attendeeList.setAdapter(new AttendeeListAdapter(context, R.layout.attendee_list, mList));

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

   /* public void getEvent() throws JSONException {
        RestClient.get("events/" + eventID, null, LoginActivity.token, new JsonHttpResponseHandler() {
            public void onSuccess(String response) {
                JSONObject res;
                String statusString;
                try {
                    res = new JSONObject(response);
                    eTitle = res.getString("what");
                    eDesc = res.getString("why");
                    eLocation = res.getString("where");
                    eFromDate = res.getString("when");
                    eToDate = res.getString("endDate");
                    eFromTime = res.getString("fromTime");
                    eToTime = res.getString("toTime");
                    statusString = res.getString("isAttending");
                    status = IsAttending.valueOf(statusString.trim().toUpperCase());
                    String base64String = res.getString("picture");
                    Bitmap eventImg = base64ToBitmap(base64String);
                    bmp = Bitmap.createScaledBitmap(eventImg, 140, 150, true);

                    Log.d("debug", res.getString("some_key")); // this is how you get a value out
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });

    }*/

    private Bitmap base64ToBitmap(String b64){
        byte[] imageAsBytes = Base64.decode(b64.getBytes(),Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
    }

    public void deleteEvent() throws JSONException {
        RestClient.delete("events/" + eventID, null, LoginActivity.token, new JsonHttpResponseHandler() {
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


    public void navItemList(View v)
    {
        Intent intent = new Intent(this, ItemListActivity.class);
        intent.putExtra("token", LoginActivity.token);
        intent.putExtra("eventID", eventID);
        startActivity(intent);
    }

    public void NavMessages(View v)
    {
        Intent intent = new Intent(this, Messages.class);
        intent.putExtra("token", LoginActivity.token);
        intent.putExtra("eventID", eventID);
        startActivity(intent);
    }

    public void answerInvite(String answer)throws JSONException{
        RestClient.post("events/" + eventID + "/invite/" + answer, null, LoginActivity.token, new JsonHttpResponseHandler() {
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

    public void leaveEvent() throws JSONException{
        RestClient.post("events/" + eventID + "/leave", null, LoginActivity.token, new JsonHttpResponseHandler() {
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



}
