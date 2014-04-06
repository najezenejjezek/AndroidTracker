package cz.tul.android.tracker.app;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import cz.tul.android.tracker.io.FileHandler;

public class LogActivity extends ActionBarActivity {

    private TextView logTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
        logTextView = (TextView) findViewById(R.id.textViewLog);
        String jsonFile = FileHandler.getInstance(getApplicationContext()).readFromFile();
        String [] jsonStringArray = jsonFile.split(";");
        String log = "";
        for(String s :jsonStringArray){
            try {
                JSONObject json = new JSONObject(s);
                log += json.getString("latitude")+"  "+json.getString("longitude")+ "  "+json.getString("accuracy")+"m\n";

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        logTextView.setText(log);



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.log, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }




}
