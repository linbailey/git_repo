package org.example.survey;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

public class TriggerAlarm extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {   
    	//wakes up the phone
    	PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
        wl.acquire();
        
        //calling the runAlarm method
        runAlarm(context);
        
        String provider;
		LocationManager locationManager;
		Location location;
		
		locationManager =(LocationManager) context.getSystemService(context.LOCATION_SERVICE);
	    Criteria c = new Criteria(); 
	    
	    provider=locationManager.getBestProvider(c, false);
	    location=locationManager.getLastKnownLocation(provider);
	    if(location!=null)
	    {
	        double lng = location.getLongitude();
	        double lat = location.getLatitude();
	        	        
	        String cityName = null;
	        
	        Geocoder gcd = new Geocoder(context, Locale.getDefault());
	        
	        List<Address> addresses;
	        
	        try
	        {
	            addresses = gcd.getFromLocation(lat, lng, 1);
	            
	            if (addresses.size() > 0)
	            {
	            	cityName = addresses.get(0).getLocality();
	            }	            
	            
	        } 
	        catch (IOException e) 
	        {
	            e.printStackTrace();
	        }
	        
	        String s = lng + "\n" + lat + "\n\nMy Current City is: " + cityName;
	        
	        Toast.makeText(context, "You current city is " + cityName, Toast.LENGTH_LONG).show();
	    }
	    else
	    {
	    	Toast.makeText(context, "no provider", Toast.LENGTH_LONG).show();
	    }	
	    
        
		wl.release();
    }              	
	
	public void runAlarm(Context context) 
	{
		//String url_update_database = "http://192.168.1.147/android_connect/android_survey.php";
		//String url_update_database = "http://128.235.41.93/android_survey.php";
		String url_update_database = "http://173.255.233.194/ESM_data.php";
		
		final String TABLE_NAME = "ESM_data";		
		final String[] FROM = {"_id", "USER_ID", "TIMESTAMP","GEOLOCATION", "GENERAL_INTEREST", "ATTRIBUTE", "ATTRIBUTE_TYPE", "INTERESTED", "REASON_SIMILARITY", "REASON_PLACE", "REASON_ACTIVITY", "REASON_ENTOURAGE", "PLACE_TYPE", "SOCIABILITY", "RARITY", "FAMILIARITY_PPL", "CROWDEDNESS", "BUSYNESS", "FAMILIARITY_PLACE", "SYNC"};
		final String ORDER_BY = "_id DESC";
				
		TableESM_data ts = new TableESM_data(context);
		
		Log.e("created", "just created events");
		
		SQLiteDatabase db = ts.getReadableDatabase();
		
		Log.e("created", "just created db");
				
		String select = "SYNC = '0'";
		
		//instantiating cursor	
		Cursor cursorRunAlarm = null;
		
		//selects all records that are flagged with a 0 in "SYNC"'s column	
		cursorRunAlarm = db.query(TABLE_NAME, FROM, select, null, null, null, null);
		
		//moving cursor to first position
		cursorRunAlarm.moveToFirst();
		
		while (cursorRunAlarm.isAfterLast() == false)
		{
			Log.e("row", cursorRunAlarm.getString(0).toString());
			//Log.e("cursorRunAlarm", cursorRunAlarm.getString(14).toString());
			
			String USER_ID = cursorRunAlarm.getString(1);
			String TIMESTAMP = cursorRunAlarm.getString(2);
			String GEOLOCATION = cursorRunAlarm.getString(3);
			String GENERAL_INTEREST = cursorRunAlarm.getString(4);
			String ATTRIBUTE = cursorRunAlarm.getString(5);
			String ATTRIBUTE_TYPE = cursorRunAlarm.getString(6);
			String INTERESTED = cursorRunAlarm.getString(7);
			String REASON_SIMILARITY = cursorRunAlarm.getString(8);
			String REASON_PLACE = cursorRunAlarm.getString(9);
			String REASON_ACTIVITY = cursorRunAlarm.getString(10);
			String REASON_ENTOURAGE = cursorRunAlarm.getString(11);
			String PLACE_TYPE = cursorRunAlarm.getString(12);
			String SOCIABILITY = cursorRunAlarm.getString(13);
			String RARITY = cursorRunAlarm.getString(14);
			String FAMILIARITY_PPL = cursorRunAlarm.getString(15);
			String CROWDEDNESS = cursorRunAlarm.getString(16);
			String BUSYNESS = cursorRunAlarm.getString(17);
			String FAMILIARITY_PLACE = cursorRunAlarm.getString(18);		
			
			// Building Parameters 
	    	List<NameValuePair> params = new ArrayList<NameValuePair>();
	    		    	
	    	params.add(new BasicNameValuePair("USER_ID", USER_ID));
	    	params.add(new BasicNameValuePair("TIMESTAMP", TIMESTAMP));
	    	params.add(new BasicNameValuePair("GEOLOCATION", GEOLOCATION));
	    	params.add(new BasicNameValuePair("GENERAL_INTEREST", GENERAL_INTEREST));
	    	params.add(new BasicNameValuePair("ATTRIBUTE", ATTRIBUTE));
	    	params.add(new BasicNameValuePair("ATTRIBUTE_TYPE", ATTRIBUTE_TYPE));
	    	params.add(new BasicNameValuePair("INTERESTED", INTERESTED));
	    	params.add(new BasicNameValuePair("REASON_SIMILARITY", REASON_SIMILARITY));	 
	    	params.add(new BasicNameValuePair("REASON_PLACE", REASON_PLACE));
	    	params.add(new BasicNameValuePair("REASON_ACTIVITY", REASON_ACTIVITY));
	    	params.add(new BasicNameValuePair("REASON_ENTOURAGE", REASON_ENTOURAGE));
	    	params.add(new BasicNameValuePair("PLACE_TYPE", PLACE_TYPE));
	    	params.add(new BasicNameValuePair("SOCIABILITY", SOCIABILITY));
	    	params.add(new BasicNameValuePair("RARITY", RARITY));
	    	params.add(new BasicNameValuePair("FAMILIARITY_PPL", FAMILIARITY_PPL));
	    	params.add(new BasicNameValuePair("CROWDEDNESS", CROWDEDNESS));
	    	params.add(new BasicNameValuePair("BUSYNESS", BUSYNESS));
	    	params.add(new BasicNameValuePair("FAMILIARITY_PLACE", FAMILIARITY_PLACE));
	    	
	    	//calling a method that starts another thread
	     	Threading(url_update_database, params);
			 			
	     	cursorRunAlarm.moveToNext();
		}
				
		cursorRunAlarm.close();
		db.close();		
		
		//calling SyncCheck2 to see if the phone is online
		SyncCheck2(context);	
	}		

	public void Threading(final String url_update_database, final List<NameValuePair> params)
	{
		new Thread (new Runnable()
		{
			@Override
			public void run() 
			{	
				JSONObject json = JSONParserESM_data.makeHttpRequest(url_update_database, params);
			}
			
		}).start();
		
	}
	
	//checking to see if the phone is online
	public String SyncCheck2(Context context)
	{				
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    if (netInfo != null && netInfo.isConnected()) 
	    {
	        Toast.makeText(context, "Syncing Survey to online database.", Toast.LENGTH_LONG).show();
	        
	        TableESM_data events = new TableESM_data(context);
			SQLiteDatabase dw = events.getWritableDatabase();
			
			ContentValues cv = new ContentValues();
			//the value 1 is a flag to verify that the records have been uploaded to MySQL
			cv.put("SYNC", "1");
			
			////replaces all 0's with 1's
			dw.update("ESM_data", cv, "SYNC = 0", null);
			dw.close();
			
			//Log.e("netInfo", netInfo.toString());			
	    }
	    else
	    {
	    	 Toast.makeText(context, "You are offline. I cannot sync Survey to the online database.", Toast.LENGTH_LONG).show();
	    }	    
	   
		return null;	    	
	}	
}
