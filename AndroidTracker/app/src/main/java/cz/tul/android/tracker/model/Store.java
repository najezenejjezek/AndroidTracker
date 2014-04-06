package cz.tul.android.tracker.model;

import android.location.Location;

/**
 * Created by najezenejjezek on 4.4.14.
 */
public class Store {
    private static Store instance = null;
    private Location uploadedLoc = null;
    private Location actualLoc = null;

    public static Store getInstance() {
        if (instance == null) {
            instance = new Store();
        }
        return instance;
    }
    public Location getUploadedLoc(){
        return uploadedLoc;
    }
    public Location getActualLoc(){
        return  actualLoc;
    }
    public void setUploadedLoc(Location location){
        this.uploadedLoc = location;
    }
    public void setActualLoc(Location location){
        this.actualLoc = location;
    }

}
