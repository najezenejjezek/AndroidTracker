package cz.tul.android.tracker.net;

/**
 * Created by najezenejjezek on 27.2.14.
 */

import android.util.Log;

import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;


import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;


import org.json.JSONArray;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;


public class Connection {

    final static private String serverURL = "http://grailsapp-lunak.rhcloud.com";
    //final static private String serverURL = "http://147.230.178.143:8080/AndroidTrackerWeb";
    public static boolean testConnection (){
        RunnableFuture f = new FutureTask<Boolean>(new Callable<Boolean>() {
            @Override
            public Boolean call(){
                try {
                    URL myUrl = new URL(serverURL);

                    URLConnection connection = myUrl.openConnection();
                    connection.setConnectTimeout(10000);
                    connection.connect();
                    Log.d("MyTag", "server responding");
                }catch (Exception e) {
                    Log.d("Info", e.toString());
                    return  false;
                }
                return true;
            }
        });
        new Thread(f).start();
        Object res= null;
        try {
            res = f.get();
        } catch (InterruptedException e) {
            Log.d("MyTag", "interupted");
        } catch (ExecutionException e) {
            Log.d("MyTag", "execution ex");
        }
        return (Boolean)res;

    }
    public static boolean sendJson(final JSONArray jsonArray) {
        RunnableFuture f = new FutureTask<Boolean>(new Callable<Boolean>() {
            @Override
            public Boolean call(){
                HttpClient client = new DefaultHttpClient();
                HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); //Timeout Limit
                HttpResponse response;
                Log.d("MyTag", "sending data");

                try {
                    HttpPost post = new HttpPost(serverURL+"/addLocation");
                    StringEntity se = new StringEntity( jsonArray.toString());;
                    se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                    post.setEntity(se);
                    response = client.execute(post);

                    //Checking response
                    if(response!=null){
                        InputStream in = response.getEntity().getContent(); //Get the data in the entity
                    }

                } catch(Exception e) {
                    Log.d("MyTag", e.toString());
                    e.printStackTrace();
                    return false;
                }
                return true;
            }
        });
        new Thread(f).start();
        Object res= null;
        try {
            res = f.get();
        } catch (InterruptedException e) {
            Log.d("MyTag", "interupted");
            return false;
        } catch (ExecutionException e) {
            Log.d("MyTag", "execution ex");
            return false;
        }
        return (Boolean)res;




        /*Thread t = new Thread() {

            public void run() {
                Looper.prepare(); //For Preparing Message Pool for the child Thread
                HttpClient client = new DefaultHttpClient();
                HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); //Timeout Limit
                HttpResponse response;
                Log.d("MyTag", "sending data");

                try {
                    HttpPost post = new HttpPost(serverURL+"/addLocation");
                    StringEntity se = new StringEntity( jsonArray.toString());;
                    se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                    post.setEntity(se);
                    response = client.execute(post);

                    //Checking response
                    if(response!=null){
                        InputStream in = response.getEntity().getContent(); //Get the data in the entity
                    }

                } catch(Exception e) {
                    Log.d("MyTag", e.toString());
                    e.printStackTrace();
                }

                Looper.loop(); //Loop in the message queue
            }
        };

        t.start();*/
    }

}