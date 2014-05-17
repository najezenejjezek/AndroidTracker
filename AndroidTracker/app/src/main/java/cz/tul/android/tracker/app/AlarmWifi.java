package cz.tul.android.tracker.app;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.tul.android.tracker.io.FileHandler;
import cz.tul.android.tracker.net.Connection;


public class AlarmWifi extends BroadcastReceiver
    {
        boolean wifiChangeFromApp;
        Context context = null;
        @Override
        public void onReceive(final Context context, Intent intent)
        {
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
            wl.acquire();
            Log.d("MyTag", "ALARMwifi");
            this.context = context;
            final WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
            boolean wifiState = wifiManager.isWifiEnabled();
            if(!wifiState){
                wifiManager.setWifiEnabled(true);
                wifiChangeFromApp = true;
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        JSONArray jsonArray;
                        if((jsonArray = loadJsonToArray()) != null){
                            if(Connection.testConnection()){
                                Log.d("MyTag", "jsonArray-  "+jsonArray.toString());
                                if(jsonArray.length()>0){
                                    boolean result = Connection.sendJson(jsonArray);
                                    if(result) FileHandler.getInstance(context.getApplicationContext()).clearFile();
                                }
                            }
                        }

                        wifiManager.setWifiEnabled(false);
                        wifiChangeFromApp = false;
                    }
                }, 15000);

            }
            wl.release();
        }

        public void SetAlarm(Context context,long delay)
        {
            AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            Intent i = new Intent(context, AlarmWifi.class);
            PendingIntent pi = PendingIntent.getBroadcast(context, 500, i, 0);
            am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), delay, pi); // Millisec * Second * Minute
        }

        public void CancelAlarm(Context context)
        {
            Intent intent = new Intent(context, AlarmWifi.class);
            PendingIntent sender = PendingIntent.getBroadcast(context, 500, intent, 0);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(sender);
        }
        private JSONArray loadJsonToArray(){
            JSONArray jsonArray= new JSONArray();
            String jsonFile = FileHandler.getInstance(context.getApplicationContext()).readFromFile();
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
                return jsonArray;
            }else{
                return null;
            }

        }
    }
