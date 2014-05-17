package cz.tul.android.tracker.app;

import android.app.AlertDialog;
import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.support.v7.app.ActionBarActivity;

import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


import android.location.Location;


import android.util.Log;
import android.content.Context;

import java.util.Date;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.CheckBox;
import android.widget.Toast;

import cz.tul.android.tracker.model.Store;

public class MainActivity extends ActionBarActivity {

    long gpsCheckTime = 60000;
    long gpsMinDistance = 100;
    SharedPreferences mySharedPreferences;

    private TextView locViewGPS = null;
    private TextView uploadedRecord = null;

    CheckBox prefCheckBoxGps;
    CheckBox prefCheckBoxWifi;

    TextView prefEditTextAcc;
    TextView prefEditTextTime;
    Button logButton;

    Store store = null;
    boolean userVerified = false;

    SharedPreferences.OnSharedPreferenceChangeListener sharedPrefListener = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefCheckBoxGps = (CheckBox)findViewById(R.id.prefCheckBoxGps);
        prefCheckBoxWifi = (CheckBox)findViewById(R.id.prefCheckBoxWifi);

        prefEditTextAcc = (TextView)findViewById(R.id.prefEditTextAcc);
        prefEditTextTime = (TextView)findViewById(R.id.prefEditTextTime);

        uploadedRecord = (TextView)findViewById(R.id.textViewUploaded);
        locViewGPS= (TextView)findViewById(R.id.textViewLocation);

        logButton = (Button) findViewById(R.id.buttonLog);
        logButton.setOnClickListener(onClickListener);

        mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        store = Store.getInstance();


        //FileHandler.getInstance(getApplicationContext()).clearFile();
        loadPref();

        if (userVerified){
            startService(new Intent(getApplicationContext(), LocationService.class));
        }else{
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Log in");
            alert.setMessage("UserName");
            final EditText input = new EditText(this);
            alert.setView(input);

            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    String value = input.getText().toString();
                    Log.d("MyTag",value);
                    if(!value.equals("")){
                        SharedPreferences.Editor editor = mySharedPreferences.edit();
                        editor.putString("edittext_preference_username",value);
                        editor.commit();
                        startService(new Intent(getApplicationContext(), LocationService.class));

                    }else {
                        Toast.makeText(getApplicationContext(), "Username is required to use an app.",Toast.LENGTH_SHORT);
                        MainActivity.this.finish();
                    }
                }
            });

            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    Toast.makeText(getApplicationContext(), "Username is required to use an app.",Toast.LENGTH_SHORT);
                    MainActivity.this.finish();
                }
            });

            alert.show();

        }

        SharedPreferences.OnSharedPreferenceChangeListener sharedPrefListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                if ("checkbox_preference_wifi".equals(key) || "checkbox_preference_gps".equals(key)){
                    loadPref();
                    Log.d("MyTag","Change pref "+key);
                    stopService(new Intent(getApplicationContext(), LocationService.class));
                    startService(new Intent(getApplicationContext(), LocationService.class));

                }
            }
        };
        mySharedPreferences.registerOnSharedPreferenceChangeListener(sharedPrefListener);



        Location uploadedLoc = store.getUploadedLoc();
        if(uploadedLoc!=null){
            Date date = new Date(uploadedLoc.getTime());
            uploadedRecord.setText("Uploaded location:\nLat: " + uploadedLoc.getLatitude() + "\nLong: " + uploadedLoc.getLongitude() + "\nProv: " + uploadedLoc.getProvider() + "\nAcc: " + uploadedLoc.getAccuracy() + "\nTime:" + date.toString());
        }

        Location actualLoc = store.getActualLoc();
        if(actualLoc != null){
            Date date = new Date(actualLoc.getTime());
            locViewGPS.setText("Actual location:\nLat: "+actualLoc.getLatitude()+"\nLong: "+actualLoc.getLongitude()+"\nProv: "+actualLoc.getProvider()+"\nAcc: "+actualLoc.getAccuracy() + "\nTime:"+date.toString());
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onResume() {
        registerReceiver(receiver, new IntentFilter(LocationService.NOTIFICATION));
        Location uploadedLoc = store.getUploadedLoc();
        if(uploadedLoc!=null){
            Date date = new Date(uploadedLoc.getTime());
            uploadedRecord.setText("Uploaded location:\nLat: " + uploadedLoc.getLatitude() + "\nLong: " + uploadedLoc.getLongitude() + "\nProv: " + uploadedLoc.getProvider() + "\nAcc: " + uploadedLoc.getAccuracy() + "\nTime:" + date.toString());
        }

        Location actualLoc = store.getActualLoc();
        if(actualLoc != null){
            Date date = new Date(actualLoc.getTime());
            locViewGPS.setText("Actual location:\nLat: "+actualLoc.getLatitude()+"\nLong: "+actualLoc.getLongitude()+"\nProv: "+actualLoc.getProvider()+"\nAcc: "+actualLoc.getAccuracy() + "\nTime:"+date.toString());
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        unregisterReceiver(receiver);
        super.onPause();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        /*int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }*/
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, SetPreferenceActivity.class);
        startActivityForResult(intent, 0);

        return true;
        //return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        //super.onActivityResult(requestCode, resultCode, data);

  /*
   * To make it simple, always re-load Preference setting.
   */

        loadPref();
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            switch(v.getId()){
                case R.id.buttonLog:
                    Intent myIntent = new Intent(MainActivity.this, LogActivity.class);
                    MainActivity.this.startActivity(myIntent);
                    break;
            }
        }
    };

    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Location location = (Location)bundle.get(LocationService.LOCATION);
                int resultCode = bundle.getInt(LocationService.RESULT);
                boolean uploaded = bundle.getBoolean(LocationService.UPLOADED);
                if (resultCode == RESULT_OK) {
                    if (uploaded){
                        //store.setUploadedLoc(location);
                        Date date = new Date(location.getTime());
                        uploadedRecord.setText("Uploaded location:\nLat: " + location.getLatitude() + "\nLong: " + location.getLongitude() + "\nProv: " + location.getProvider() + "\nAcc: " + location.getAccuracy() + "\nTime:" + date.toString());
                    }else{
                        //store.setActualLoc(location);
                        Date date = new Date(location.getTime());
                        locViewGPS.setText("Actual location:\nLat: "+location.getLatitude()+"\nLong: "+location.getLongitude()+"\nProv: "+location.getProvider()+"\nAcc: "+location.getAccuracy() + "\nTime:"+date.toString());
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Service failed",
                            Toast.LENGTH_LONG).show();
                }
            }
        }
    };



    private void loadPref(){
        SharedPreferences mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        boolean my_checkbox_preference_gps = mySharedPreferences.getBoolean("checkbox_preference_gps", false);
        prefCheckBoxGps.setChecked(my_checkbox_preference_gps);
        prefCheckBoxGps.setEnabled(false);

        boolean my_checkbox_preference_wifi = mySharedPreferences.getBoolean("checkbox_preference_wifi", false);
        prefCheckBoxWifi.setChecked(my_checkbox_preference_wifi);
        prefCheckBoxWifi.setEnabled(false);

        userVerified = !mySharedPreferences.getString("edittext_preference_username","").equals("");

        String my_edittext_preference_acc = mySharedPreferences.getString("edittext_preference_acc", "100");
        try{
            gpsMinDistance = Long.parseLong(my_edittext_preference_acc);
            prefEditTextAcc.setText("Min distance:"+my_edittext_preference_acc+" m");
        }catch (NumberFormatException e){
            Log.d("TAG","Wrong format in gpsMinDistance in main");
        }
        String my_edittext_preference_time = mySharedPreferences.getString("edittext_preference_time", "60");
        try{
            gpsCheckTime = Long.parseLong(my_edittext_preference_time)*1000;
            prefEditTextTime.setText("Check time:" + my_edittext_preference_time+" s");
        }catch (NumberFormatException e){
            Log.d("TAG","Wrong format in gpsChecktime in main");
        }

    }







}
