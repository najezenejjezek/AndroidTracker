package cz.tul.android.tracker.app;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;

public class AlarmGPS extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent)
        {
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
            wl.acquire();
            Log.d("MyTag", "ALARMGPS");
            shortGpsUpdate(context);
            wl.release();
        }

    public void SetAlarm(Context context,long delay)
    {
        AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, AlarmGPS.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 200, i, 0);
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), delay, pi); // Millisec * Second * Minute
    }

    public void CancelAlarm(Context context)
    {
        Intent intent = new Intent(context, AlarmGPS.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 200, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }
    private void shortGpsUpdate(final Context context){
        Intent activeIntent = new Intent(context, LocationReceiver.class);
        final PendingIntent locationListenerPendingIntent =
                PendingIntent.getBroadcast(context, 0, activeIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        final LocationManager locationManager = (LocationManager) context.getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,8000,50,locationListenerPendingIntent);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                locationManager.removeUpdates(locationListenerPendingIntent);
                context.startService(new Intent(context,LocationService.class));
            }
        }, 25000);
    }
}
