package cz.tul.android.tracker.app;

import android.support.v7.app.ActionBarActivity;

import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import android.util.Log;
import android.content.Context;


import java.util.Timer;
import java.util.TimerTask;
import java.util.Date;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import android.widget.Button;
import android.widget.TextView;
import android.widget.CheckBox;
import android.widget.Toast;

import java.io.OutputStreamWriter;
import java.io.IOException;

public class MainActivity extends ActionBarActivity {

    Timer gpsTimer;
    long gpsCheckTime = 15000;
    long gpsMinDistance = 200;
    boolean gps_recorder_running = false;
    Location lastLocation = null;
    Location loc =null;

    private TextView locViewGPS = null;

    CheckBox prefCheckBox;
    TextView prefEditTextAcc;
    TextView prefEditTextTime;
    Button logButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefCheckBox = (CheckBox)findViewById(R.id.prefCheckBox);
        prefEditTextAcc = (TextView)findViewById(R.id.prefEditTextAcc);
        prefEditTextTime = (TextView)findViewById(R.id.prefEditTextTime);
        logButton = (Button) findViewById(R.id.buttonLog);
        logButton.setOnClickListener(onClickListener);

        clearFile();

        SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences(this);

        loadPref();
        startRecording();
        // Instance field for listener
        SharedPreferences.OnSharedPreferenceChangeListener listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                if ("edittext_preference_time".equals(key))
                    startRecording();;
            }
        };
        prefs.registerOnSharedPreferenceChangeListener(listener);



        /*Toast.makeText(getApplicationContext(),getBestLocation().toString() ,
                Toast.LENGTH_LONG).show();*/
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
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


    private void loadPref(){
        SharedPreferences mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        boolean my_checkbox_preference = mySharedPreferences.getBoolean("checkbox_preference", false);
        prefCheckBox.setChecked(my_checkbox_preference);

        String my_edittext_preference_acc = mySharedPreferences.getString("edittext_preference_acc", "");
        try{
            gpsMinDistance = Long.parseLong(my_edittext_preference_acc);
            prefEditTextAcc.setText(my_edittext_preference_acc+" m");
        }catch (NumberFormatException e){
            Log.d("TAG","Wrong format in gpsMinDistance");
        }
        String my_edittext_preference_time = mySharedPreferences.getString("edittext_preference_time", "");
        try{
            gpsCheckTime = Long.parseLong(my_edittext_preference_time)*1000;
            prefEditTextTime.setText(my_edittext_preference_time+" s");
        }catch (NumberFormatException e){
            Log.d("TAG","Wrong format in gpsMinDistance");
        }

    }

    private void writeToFile(String data) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput("log.txt", Context.MODE_APPEND));
            outputStreamWriter.append(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {

        }
    }








        /**
         * try to get the 'best' location selected from all providers
         */
    private Location getBestLocation() {
        Location gpslocation = getLocationByProvider(LocationManager.GPS_PROVIDER);
        Location networkLocation =
                getLocationByProvider(LocationManager.NETWORK_PROVIDER);
        // if we have only one location available, the choice is easy
        if (gpslocation == null) {
            Log.d("TAG", "No GPS Location available.");
            return networkLocation;
        }
        if (networkLocation == null) {
            Log.d("TAG", "No Network Location available");
            return gpslocation;
        }
        // a locationupdate is considered 'old' if its older than the configured
        // update interval. this means, we didn't get a
        // update from this provider since the last check
        long old = System.currentTimeMillis() - gpsCheckTime;//getGPSCheckMilliSecsFromPrefs();
        boolean gpsIsOld = (gpslocation.getTime() < old);
        boolean networkIsOld = (networkLocation.getTime() < old);
        // gps is current and available, gps is better than network
        if (!gpsIsOld) {
            Log.d("TAG", "Returning current GPS Location");
            return gpslocation;
        }
        // gps is old, we can't trust it. use network location
        if (!networkIsOld) {
            Log.d("TAG", "GPS is old, Network is current, returning network");
            return networkLocation;
        }
        // both are old return the newer of those two
        if (gpslocation.getTime() > networkLocation.getTime()) {
            Log.d("TAG", "Both are old, returning gps(newer)");
            return gpslocation;
        } else {
            Log.d("TAG", "Both are old, returning network(newer)");
            return networkLocation;
        }
    }

    /**
     * get the last known location from a specific provider (network/gps)
     */
    private Location getLocationByProvider(String provider) {
        Location location = null;
        /*if (!isProviderSupported(provider)) {
            return null;
        }*/
        LocationManager locationManager = (LocationManager) getApplicationContext()
                .getSystemService(Context.LOCATION_SERVICE);
        try {
            if (locationManager.isProviderEnabled(provider)) {
                location = locationManager.getLastKnownLocation(provider);
            }
        } catch (IllegalArgumentException e) {
            Log.d("TAG", "Cannot acces Provider " + provider);
        }
        return location;
    }


    public void startRecording() {
       if(gpsTimer!=null) gpsTimer.cancel();
        gpsTimer = new Timer();
        long checkInterval = gpsCheckTime;//getGPSCheckMilliSecsFromPrefs();
        long minDistance = gpsMinDistance; //getMinDistanceFromPrefs();
        // receive updates
        LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        for (String s : locationManager.getAllProviders()) {
            locationManager.requestLocationUpdates(s, checkInterval,
                    minDistance, new LocationListener() {

                @Override
                public void onStatusChanged(String provider,
                                            int status, Bundle extras) {}

                @Override
                public void onProviderEnabled(String provider) {}

                @Override
                public void onProviderDisabled(String provider) {}

                @Override
                public void onLocationChanged(Location location) {
                    // if this is a gps location, we can use it
                    if (location.getProvider().equals(
                            LocationManager.GPS_PROVIDER)) {
                        doLocationUpdate(location, true);
                    }
                }
            });
            // //Toast.makeText(this, "GPS Service STARTED",
            // Toast.LENGTH_LONG).show();
            gps_recorder_running = true;
        }
        // start the gps receiver thread
        gpsTimer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                Location location = getBestLocation();
                doLocationUpdate(location, false);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        locViewGPS= (TextView)findViewById(R.id.textViewLocation);
                        Date date = new Date(loc.getTime());
                        locViewGPS.setText("Lat: "+loc.getLatitude()+"\nLong: "+loc.getLongitude()+"\nProv: "+loc.getProvider()+"\nAcc: "+loc.getAccuracy() + "\nTime:"+date.toString());
                    }
                });
            }
        }, 0, checkInterval);
    }

    public void doLocationUpdate(Location l, boolean force) {
        long minDistance = gpsMinDistance;//getMinDistanceFromPrefs();
        Log.d("TAG", "update received:" + l);
        if (l == null) {
            Log.d("TAG", "Empty location");
            if (force)
                Toast.makeText(this, "Current location not available",
                        Toast.LENGTH_SHORT).show();
            return;
        }
        if (lastLocation != null) {
            float distance = l.distanceTo(lastLocation);
            Log.d("TAG", "Distance to last: " + distance);
            if (l.distanceTo(lastLocation) < minDistance && !force) {
                Log.d("TAG", "Position didn't change");
                return;
            }
            if (l.getAccuracy() >= lastLocation.getAccuracy()
                    && l.distanceTo(lastLocation) < l.getAccuracy() && !force) {
                Log.d("TAG",
                        "Accuracy got worse and we are still "
                                + "within the accuracy range.. Not updating");
                return;
            }
            /*if (l.getTime() <= lastprovidertimestamp && !force) {
                Log.d("TAG", "Timestamp not never than last");
                return;
            }*/
        }
        // upload/store your location here
         loc =  getBestLocation();
         writeToFile(loc.getTime()+","+loc.getLatitude()+","+loc.getLongitude()+","+loc.getAccuracy()+";");
         lastLocation = loc;



    }

    private void clearFile() {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput("log.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write("");
            outputStreamWriter.close();
        }
        catch (IOException e) {

        }
    }

}
