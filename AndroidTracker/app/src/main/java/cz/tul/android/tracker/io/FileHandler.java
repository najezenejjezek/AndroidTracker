package cz.tul.android.tracker.io;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;


/**
 * Created by najezenejjezek on 28.2.14.
 */
public class FileHandler implements Cloneable{
    private static FileHandler fileHandler ;
    private static Context mContext;

    private FileHandler(){
        // Empty
    }

    public static synchronized FileHandler getInstance(Context context){
        if(fileHandler == null){
            fileHandler = new FileHandler();
        }
        mContext = context.getApplicationContext();
        return fileHandler;
    }

    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException("I'm a singleton!");
    }

    public String readFromFile() {

        String ret = "";

        try {
            InputStream inputStream = mContext.openFileInput("log.txt");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }
    public void writeToFile(String data) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(mContext.openFileOutput("log.txt", Context.MODE_APPEND));
            outputStreamWriter.append(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {

        }
    }
    public void clearFile() {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(mContext.openFileOutput("log.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write("");
            outputStreamWriter.close();
        }
        catch (IOException e) {

        }
    }
}
