package cz.tul.android.tracker.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

/**
 * Created by najezenejjezek on 12.5.14.
 */
public class LocationReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        Location location = (Location) intent.getExtras().get(LocationManager.KEY_LOCATION_CHANGED);
        Log.d("MyTag","location from receiver "+location);
    }
}
