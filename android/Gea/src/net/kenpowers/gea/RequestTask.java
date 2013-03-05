package net.kenpowers.gea;

import java.io.*;
import android.util.*;
import java.net.*;

import android.os.AsyncTask;



public class RequestTask extends AsyncTask<String, String, String> {
	private RequestTaskCompleteListener callback;
	
	public RequestTask(RequestTaskCompleteListener callback) {
		this.callback = callback;
	}
	
	protected String doInBackground(String... uri) {
        int statusCode = 0;
        
        try {
        	URL url = new URL(uri[0]);
        	HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        	connection.setRequestMethod("GET");
        	connection.setRequestProperty("Content-length", "0");
        	connection.connect();
        	statusCode = connection.getResponseCode();
        	if (statusCode == 200) {
        		BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line+"\n");
                }
                br.close();
                return sb.toString();
        	}
        	
        } catch (MalformedURLException e) {
        	Log.d(MainActivity.LOG_TAG, "malformed URL");
        } catch (IOException e) {
        	e.printStackTrace();
        }        
        
        return "" + statusCode;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        callback.onTaskComplete(result);
    }
}
