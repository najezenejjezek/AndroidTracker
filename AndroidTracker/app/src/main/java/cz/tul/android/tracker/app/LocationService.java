package cz.tul.android.tracker.app;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;


import cz.tul.android.tracker.model.Store;
import cz.tul.android.tracker.net.Connection;
import cz.tul.android.tracker.io.FileHandler;

/**
 * Created by najezenejjezek on 23.3.14.
 */
public class LocationService extends Service {
    private final IBinder mBinder = new MyBinder();
    public static final String NOTIFICATION = "cz.tul.android.tracker.app";
    public static final String RESULT = "result";
    public static final String UPLOADED = "uploaded";
    public static final String LOCATION = "location";
    private int result = Activity.RESULT_CANCELED;
    private boolean gpsMax = false;
    private boolean canChangeWifi = false;

    long gpsCheckTime = 60000;
    long gpsMinDistance = 100;
    boolean gps_recorder_running = false;
    Location lastLocation = null;
    long lastprovidertimestamp = 0;
    LocationManager locationManager;
    LocationListener locationListener = null;

    SharedPreferences mySharedPreferences;
    Store store ;

    //runs without a timer by reposting this handler at the end of the runnable
    Handler timerWifiHandler = new Handler();
    Handler timerGpsHandler = new Handler();
    Handler checkLocationHandler = new Handler();

    Runnable timerGpsRunnable = new Runnable() {

        @Override
        public void run() {
            shortGpsUpdate();
            timerGpsHandler.postDelayed(this, 5*60*1000);
        }
    };



    Runnable timerWifiRunnable = new Runnable() {

        @Override
        public void run() {
            final WifiManager wifiManager = (WifiManager)getSystemService(Context.WIFI_SERVICE);
            boolean wifiState = wifiManager.isWifiEnabled();
            if(!wifiState){
                wifiManager.setWifiEnabled(true);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        if(Connection.testConnection()){
                            JSONArray jsonArray = loadJsonToArray();
                            Log.d("MyTag", "jsonArray-  "+jsonArray.toString());
                            if(jsonArray.length()>0){
                                boolean result = Connection.sendJson(jsonArray);
                                if(result)FileHandler.getInstance(getApplicationContext()).clearFile();
                            }

                        }
                        wifiManager.setWifiEnabled(false);
                    }
                }, 15000);

            }
            timerWifiHandler.postDelayed(this, 2*60*1000);
        }
    };

    @Override
    public void onCreate() {
        loadPref();
        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        store = Store.getInstance();
        lastLocation = store.getActualLoc();
        locationListener = new LocationListener() {

            @Override
            public void onStatusChanged(String provider,
                                        int status, Bundle extras) {}

            @Override
            public void onProviderEnabled(String provider) {}

            @Override
            public void onProviderDisabled(String provider) {}

            @Override
            public void onLocationChanged(Location location) {
                doLocationUpdate(location, false);
            }
        };
        startRecording();


        Log.d("MyTag", "gpsMax"+gpsMax);
        if (!gpsMax ) {
            timerGpsHandler.postDelayed(timerGpsRunnable,60*1000);
        }
        if(canChangeWifi){
            timerWifiHandler.postDelayed(timerWifiRunnable,0);
        }


        SharedPreferences.OnSharedPreferenceChangeListener listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                if ("edittext_preference_time".equals(key) || "edittext_preference_acc".equals(key) ){
                    loadPref();
                    Log.d("MyTag","changePref acc or time");
                    locationManager.removeUpdates(locationListener);
                    startRecording();

                }
            }
        };
        mySharedPreferences.registerOnSharedPreferenceChangeListener(listener);
        ;
    }

    @Override
    public void onDestroy() {
        timerGpsHandler.removeCallbacksAndMessages(null);
        timerWifiHandler.removeCallbacksAndMessages(null);
        checkLocationHandler.removeCallbacksAndMessages(null);
        locationManager.removeUpdates(locationListener);

        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        loadPref();
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private void publishResults(Location location, boolean uploaded,int result) {
        if (uploaded){
            store.setUploadedLoc(location);
        }else {
            store.setActualLoc(location);
        }
        Intent intent = new Intent(NOTIFICATION);
        intent.putExtra(RESULT, result);
        intent.putExtra(UPLOADED,uploaded);
        intent.putExtra(LOCATION,location);
        Log.d("MyTag", "Publish."+location.toString());
        sendBroadcast(intent);
    }

    private void loadPref(){
        mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        gpsMax = mySharedPreferences.getBoolean("checkbox_preference_gps", false);
        canChangeWifi = mySharedPreferences.getBoolean("checkbox_preference_wifi", false);


        String my_edittext_preference_acc = mySharedPreferences.getString("edittext_preference_acc", "100");
        try{
            gpsMinDistance = Long.parseLong(my_edittext_preference_acc);
        }catch (NumberFormatException e){
            Log.d("MyTag","Wrong format in gpsMinDistance");
        }
        String my_edittext_preference_time = mySharedPreferences.getString("edittext_preference_time", "60");
        try{
            gpsCheckTime = Long.parseLong(my_edittext_preference_time)*1000;
        }catch (NumberFormatException e){
            Log.d("MyTag","Wrong format in gpsCheckTime");
        }

    }


    private Location getBestLocation() {
        Location gpslocation = getLocationByProvider(LocationManager.GPS_PROVIDER);
        Location networkLocation =
                getLocationByProvider(LocationManager.NETWORK_PROVIDER);
        // if we have only one location available, the choice is easy
        if (gpslocation == null) {
            Log.d("MyTag", "No GPS Location available.");
            return networkLocation;
        }
        if (networkLocation == null) {
            Log.d("MyTag", "No Network Location available");
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
            Log.d("MyTag", "Returning current GPS Location");
            return gpslocation;
        }
        // gps is old, we can't trust it. use network location
        if (!networkIsOld) {
            Log.d("MyTag", "GPS is old, Network is current, returning network");
            return networkLocation;
        }
        // both are old return the newer of those two
        if (gpslocation.getTime() > networkLocation.getTime()) {
            Log.d("MyTag", "Both are old, returning gps(newer)");
            return gpslocation;
        } else {
            Log.d("MyTag", "Both are old, returning network(newer)");
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
            Log.d("MyTag", "Cannot acces Provider " + provider);
        }
        return location;
    }


    private void startRecording() {

        long checkInterval = gpsCheckTime;//getGPSCheckMilliSecsFromPrefs();
        long minDistance = gpsMinDistance; //getMinDistanceFromPrefs();

        Runnable locationRefreshRunnable = new Runnable() {

            @Override
            public void run() {
                Log.d("MyTag", "update from runnable");
                Location location = getBestLocation();
                doLocationUpdate(location, false);
                checkLocationHandler.postDelayed(this, gpsCheckTime);
            }
        };

        locationManager.removeUpdates(locationListener);

        for (String s : locationManager.getAllProviders()) {
            if(!s.equals(LocationManager.GPS_PROVIDER) || gpsMax){
                locationManager.requestLocationUpdates(s, checkInterval,
                        minDistance, locationListener);
            }

        }
        synchronized (checkLocationHandler){
            checkLocationHandler.removeCallbacksAndMessages(null);
            Log.d("MyTag", "removeCallbacks");
            checkLocationHandler.postDelayed(locationRefreshRunnable,0);
            Log.d("MyTag", "PostDelayed");
        }

    }

    private void shortGpsUpdate(){
            final LocationListener locationListener = new LocationListener() {

                @Override
                public void onStatusChanged(String provider,
                                            int status, Bundle extras) {}

                @Override
                public void onProviderEnabled(String provider) {}

                @Override
                public void onProviderDisabled(String provider) {}

                @Override
                public void onLocationChanged(Location location) {
                    Log.d("MyTag", "GPS Quick refresh");
                }
            };
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 8000,
                    50, locationListener);

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    locationManager.removeUpdates(locationListener);
                    doLocationUpdate(getBestLocation(), false);
                    result = Activity.RESULT_OK;
                }
            }, 24000);

    }


    private void doLocationUpdate(Location location, boolean force) {
        result = Activity.RESULT_OK;
        long minDistance = gpsMinDistance;//getMinDistanceFromPrefs();
        Log.d("MyTag", "update received:" + location);
        publishResults(location,false,result);
        synchronized (store){
            Log.d("MyTag", "Start sync");
            if (location == null) {
                Log.d("MyTag", "Empty location");
                if (force)
                    Toast.makeText(this, "Current location not available",
                            Toast.LENGTH_SHORT).show();
                return;
            }
            if (store.getUploadedLoc() != null) {
                float distance = location.distanceTo(store.getUploadedLoc());
                Log.d("MyTag", "Distance to last: " + distance);
                if (location.distanceTo(store.getUploadedLoc()) < minDistance && !force) {
                    Log.d("MyTag", "Position didn't change");
                    return;
                }
                if (location.getAccuracy() >= store.getUploadedLoc().getAccuracy()
                        && location.distanceTo(store.getUploadedLoc()) < location.getAccuracy() && !force) {
                    Log.d("MyTag",
                            "Accuracy got worse and we are still "
                                    + "within the accuracy range.. Not updating");
                    return;
                }
                if (location.getTime() <= (lastprovidertimestamp+1000) && !force) {
                    Log.d("MyTag", "Timestamp not never than last");
                    return;
                }
            }

            // upload/store your location here

            lastprovidertimestamp = location.getTime();
            publishResults(location, true, Activity.RESULT_OK);
            Log.d("MyTag", "End sync");
        }
        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put("time",location.getTime());
            jsonObject.put("latitude",((int)(location.getLatitude()*1000000))/(1000000.0));
            jsonObject.put("longitude",((int)(location.getLongitude()*1000000))/(1000000.0));
            jsonObject.put("accuracy",((int)(location.getAccuracy()*100))/100.0);
            jsonObject.put("provider",location.getProvider());
            jsonObject.put("username",mySharedPreferences.getString("edittext_preference_username","john"));

        }catch (JSONException ex){
            Log.d("MyTag",ex.toString());
        }

        if(Connection.testConnection()){
            JSONArray jsonArray = loadJsonToArray();
            jsonArray.put(jsonObject);
            Log.d("MyTag", "jsonArray-  "+jsonArray.toString());
            boolean result = Connection.sendJson(jsonArray);
            if (result) {
                if(jsonArray.length()>1)FileHandler.getInstance(getApplicationContext()).clearFile();
            }else {
                FileHandler.getInstance(getApplicationContext()).writeToFile(jsonObject.toString()+";");
            }

        }else{
            FileHandler.getInstance(getApplicationContext()).writeToFile(jsonObject.toString()+";");
        }

    }
    private JSONArray loadJsonToArray(){
        JSONArray jsonArray= new JSONArray();
        String jsonFile = FileHandler.getInstance(getApplicationContext()).readFromFile();
        Log.d("MyTag", "jsonFile-  "+jsonFile.toString());
        if(!jsonFile.equals("")){
            String [] jsonStringArray = jsonFile.split(";");
            for(String s :jsonStringArray){
                try {
                    JSONObject json = new JSONObject(s);
                    jsonArray.put(json);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return jsonArray;
    }



    public class MyBinder extends Binder {
        LocationService getService() {
            return LocationService.this;
        }
    }

}

 class LocUpdate{
    private static LocUpdate instance = null;
    public static LocUpdate getInstance() {
        if (instance == null) {
            instance = new LocUpdate();
        }
        return instance;
    }


}
