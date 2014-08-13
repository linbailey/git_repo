package org.example.survey;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.example.survey.R;
import org.example.survey.JSONParserESM_data;

import com.arellomobile.android.push.BasePushMessageReceiver;
import com.arellomobile.android.push.PushManager;
import com.arellomobile.android.push.utils.RegisterBroadcastReceiver;

import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.text.Html;
import android.text.method.TextKeyListener;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class Survey extends Activity 
{			
	Context context = this;
	
	//variables that hold the radio buttons' value
	String gender = "";
	int age = 0;
	int general_interest = 0;
	int attribute_interest = 0;
	String attribute_type = "";
	String attribute = "";
	int reason_similarity = 0;
	int reason_place = 0;
	int reason_activity = 0;
	int reason_entourage = 0;
	int place_type = 0;
	int sociability = 0;
	int rarity = 0;
	int familiarity_people = 0;
	int crowdedness = 0;
	int busyness = 0;
	int familiarity_place = 0;
	//int interest_strength = 0;
	int SYNC = 0;
	String AttType = "";
	String Att = "";
	//String cityName = "";
			
	//holds the user id that is stored in a text file
	String UserID;
	
	int yes = 0;
	int no = 0;
	
	int b = 0;
	//variables that are used for push whoosh push notifications
	String APP_ID = "EC484-EB431";
	String SENDER_ID = "1006496853897";
	
	//instantiating this class to send data to the MySQL database
	JSONParserESM_data jsonParser = new JSONParserESM_data();
	
	//variable that holds the ip address for the survey table in MySQL
	String url_update_database = "http://173.255.233.194/ESM_data.php";
	
	int randomattributeType = 0;
	Random random = new Random();
	
		
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{				
		String userIdFile = "UserID_File.txt";
		String path = (getFilesDir()+File.separator+userIdFile);
		File filepath = new File(path);
				
		super.onCreate(savedInstanceState);
	     
		//checking to see if the file path exists
		if(filepath.exists() == false)
		{				
			setContentView(R.layout.welcomedefault);
			
			ProgressBar progressbarSearch = (ProgressBar) findViewById(R.id.progressBar1);
			progressbarSearch.setVisibility(View.INVISIBLE);
			
			Button btncontinue = (Button) findViewById(R.id.btncontinue);
			btncontinue.setVisibility(View.INVISIBLE);			
		}
		else
		{			
			setContentView(R.layout.page1);
		}	
		
		GetCity();
	    		
		//creating the alarm that will wake the phone's cpu up and uploads records to MySQL in the background
	    Intent intent = new Intent(this, TriggerAlarm.class);
	    Calendar calendar = Calendar.getInstance();
	    
	    //These times will change in the final product
	    calendar.set(Calendar.HOUR_OF_DAY, 24);
	    calendar.set(Calendar.MINUTE, 00);
	    
	    PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

	    AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);	    
	    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_HALF_DAY, pendingIntent);   
	    	    
		//Register receivers for push notifications
		registerReceivers();
	
		//Create and start push manager	    
	 	PushManager pushManager = new PushManager(this, APP_ID, SENDER_ID);
	 	pushManager.onStartup(this);
	 	
	 	checkMessage(getIntent());	
	}		
	
	//Registration receiver
	RegisterBroadcastReceiver mBroadcastReceiver = new RegisterBroadcastReceiver()
	{
	    @Override
	    public void onRegisterActionReceive(Context context, Intent intent)
	    {
	        checkMessage(intent);
	    }	
	};
	 
	//Push message receiver
	private BasePushMessageReceiver mReceiver = new BasePushMessageReceiver()
	{
	    @Override
	    protected void onMessageReceive(Intent intent)
	    {
	        //JSON_DATA_KEY contains JSON payload of push notification.
	        //showMessage("push message is " + intent.getExtras().getString(JSON_DATA_KEY));
	    }
	};
	 
	//Registration of the receivers
	public void registerReceivers()
	{
	    IntentFilter intentFilter = new IntentFilter(getPackageName() + ".action.PUSH_MESSAGE_RECEIVE");
	 
	    registerReceiver(mReceiver, intentFilter);
	     
	    registerReceiver(mBroadcastReceiver, new IntentFilter(getPackageName() + "." + PushManager.REGISTER_BROAD_CAST_ACTION));       
	}
	
	//Unregistering receivers
	public void unregisterReceivers()
	{
	    //Unregister receivers on pause
	    try
	    {
	        unregisterReceiver(mReceiver);
	    }
	    catch (Exception e)
	    {
	        
	    }
	     
	    try
	    {
	        unregisterReceiver(mBroadcastReceiver);
	    }
	    catch (Exception e)
	    {
	        
	    }
	}
	
	@Override
	public void onResume()
	{
	    super.onResume();
	     
	    //Re-register receivers on resume
	    registerReceivers();
	}
	 
	@Override
	public void onPause()
	{
	    super.onPause();
	 
	    //Unregister receivers on pause
	    unregisterReceivers();
	}
	
	//checks the push notification that the phone receives
	private void checkMessage(Intent intent)
	{
	    if (null != intent)
	    {
	        if (intent.hasExtra(PushManager.PUSH_RECEIVE_EVENT))
	        {
	            //showMessage("push message is " + intent.getExtras().getString(PushManager.PUSH_RECEIVE_EVENT));
	        }
	        else if (intent.hasExtra(PushManager.REGISTER_EVENT))
	        {
	            showMessage("This app is registered for push notifications.");
	        }
	        else if (intent.hasExtra(PushManager.UNREGISTER_EVENT))
	        {
	            showMessage("unregister");
	        }
	        else if (intent.hasExtra(PushManager.REGISTER_ERROR_EVENT))
	        {
	            showMessage("register error");
	        }
	        else if (intent.hasExtra(PushManager.UNREGISTER_ERROR_EVENT))
	        {
	            showMessage("unregister error");
	        }
	 
	        resetIntentValues();
	    }
	}	 
	
	//Will check main Activity intent and if it contains any PushWoosh data, will clear it	 
	private void resetIntentValues()
	{
	    Intent mainAppIntent = getIntent();
	 
	    if (mainAppIntent.hasExtra(PushManager.PUSH_RECEIVE_EVENT))
	    {
	        mainAppIntent.removeExtra(PushManager.PUSH_RECEIVE_EVENT);
	    }
	    else if (mainAppIntent.hasExtra(PushManager.REGISTER_EVENT))
	    {
	        mainAppIntent.removeExtra(PushManager.REGISTER_EVENT);
	    }
	    else if (mainAppIntent.hasExtra(PushManager.UNREGISTER_EVENT))
	    {
	        mainAppIntent.removeExtra(PushManager.UNREGISTER_EVENT);
	    }
	    else if (mainAppIntent.hasExtra(PushManager.REGISTER_ERROR_EVENT))
	    {
	        mainAppIntent.removeExtra(PushManager.REGISTER_ERROR_EVENT);
	    }
	    else if (mainAppIntent.hasExtra(PushManager.UNREGISTER_ERROR_EVENT))
	    {
	        mainAppIntent.removeExtra(PushManager.UNREGISTER_ERROR_EVENT);
	    }
	 
	    setIntent(mainAppIntent);
	}
	 
	private void showMessage(String message)
	{
	    Toast.makeText(this, message, Toast.LENGTH_LONG).show();
	}
	
	@Override
	protected void onNewIntent(Intent intent)
	{
	    super.onNewIntent(intent);
	    setIntent(intent);
	 
	    checkMessage(intent);
	 
	    setIntent(new Intent());
	}	
	
	public String GetCity()
	{		
		String provider;
		LocationManager locationManager;
		Location location;
		String cityName = "";
		
		//getting the location from the network tower
		locationManager =(LocationManager) this.getSystemService(this.LOCATION_SERVICE);
	    Criteria c = new Criteria(); 
	    
	    provider=locationManager.getBestProvider(c, false);
	    location=locationManager.getLastKnownLocation(provider);
	    
	    if(location!=null)
	    {
	        double lng = location.getLongitude();
	        double lat = location.getLatitude();
	        	        
	        Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
	        
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
	        
	        Toast.makeText(this, "Your current city is " + cityName, Toast.LENGTH_LONG).show();
	    }
	   
	    Log.e("cityName", cityName.toString());
	    
	    return(cityName);
	}
	
	//creates the user id file if it doesn't exist, retrieves user id if file exists
		public int UserIDTextFile(View view) throws IOException 
		{						
			String userIdFile = "UserID_File.txt";
			String path = (getFilesDir()+File.separator+userIdFile);			
			File filepath = new File(path);
			String read;
			StringBuilder builder = new StringBuilder("");
			String IDtext = "";
			int ID = 0;
			
			if (!filepath.exists()) 
			{					
				EditText userIDText = (EditText) findViewById(R.id.userIdText);
														
				IDtext = userIDText.getText().toString();
				
				try
				{
					ID = Integer.parseInt(IDtext.toString());
				}
				catch(Exception e)
				{
					Toast.makeText(this, "Please enter an integer.", Toast.LENGTH_LONG).show();						
				}
				
				if(ID != 0)
				{					
					//Writing to a file...	
					ID = Integer.parseInt(IDtext.toString());
					
					BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new File(path)));
					bufferedWriter.write(String.valueOf(ID));
					bufferedWriter.close();			
					
					Log.e("ID", String.valueOf(ID));
					
					Toast.makeText(this, "Your User ID has been recorded.", Toast.LENGTH_LONG).show();
										
					Log.e("this is user ID", String.valueOf(ID));;									
					Log.e("This is the path", filepath.toString());
					
					EditText userIdText = (EditText) findViewById(R.id.userIdText);
					Button btnEnter = (Button) findViewById(R.id.btnEnter);
					
					userIdText.setVisibility(View.INVISIBLE);
					btnEnter.setVisibility(View.INVISIBLE);
				}
				
				//call a method that starts another thread that gets data from the User_Attributes table in MySQL
				ThreadAtt(String.valueOf(ID));				
				
				InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
			    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
			    
			    
				//ecessive toast messages to appear when loading data for the first time
				Toast.makeText(context, "Loading data for the first time...", Toast.LENGTH_LONG).show();
				Toast.makeText(context, "Loading data for the first time...", Toast.LENGTH_LONG).show();
				Toast.makeText(context, "Loading data for the first time...", Toast.LENGTH_LONG).show();
				Toast.makeText(context, "Loading data for the first time...", Toast.LENGTH_LONG).show();
				Toast.makeText(context, "Loading data for the first time...", Toast.LENGTH_LONG).show();
				Toast.makeText(context, "Loading data for the first time...", Toast.LENGTH_LONG).show();
				Toast.makeText(context, "Loading data for the first time...", Toast.LENGTH_LONG).show();
				Toast.makeText(context, "Loading data for the first time...", Toast.LENGTH_LONG).show();
				Toast.makeText(context, "Loading data for the first time...", Toast.LENGTH_LONG).show();
				
				//showing progress bar
				ProgressBar progressbarSearch = (ProgressBar) findViewById(R.id.progressBar1);
				progressbarSearch.setVisibility(view.VISIBLE);
				
				Handler myHandler = new Handler();
				
				//loads the progress bar for 40 seconds
				myHandler.postDelayed(stopSearching, 40000);											
			}
			else
			{
				//Reading the user id file
				 BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(path)));
				 
				 while((read = bufferedReader.readLine()) != null)
				 {
					 builder.append(read);
				 }
				 
				 bufferedReader.close();
				 
				 ID = Integer.parseInt(builder.toString());
			 
				 Log.e("this is the user ID", String.valueOf(ID));	
				 Log.e("This is the path", filepath.toString());						 
			}	   		
					
			return(ID);
		}
		
	//hides the progress bar and shows the start button
	private Runnable stopSearching = new Runnable()
	{
	    @Override
	    public void run()
	    {
	        ProgressBar progressbarSearch = (ProgressBar) findViewById(R.id.progressBar1);
	        progressbarSearch.setVisibility(View.INVISIBLE);
	        
	        Button btncontinue = (Button) findViewById(R.id.btncontinue);
	        btncontinue.setVisibility(View.VISIBLE);
	    }
	};
	
	//handles for first survey question page
	public void Begin(View view) throws IOException, InterruptedException
	{		
		CreateAtt();
		
		setContentView(R.layout.page1);
	}	
	
	//creating zero values for table AttType
  	public void CreateAtt()
  	{
  		String TABLE_NAME = "AttType";
  		
  		TableAttType tat = new TableAttType(this);
  		SQLiteDatabase sd = tat.getWritableDatabase();
  		
  		ContentValues values = new ContentValues();

  		String GENDER = "gender";
  		values.put(GENDER, "0");
  		String AGE = "age";
  		values.put(AGE, "0");
  		String NATIONALITY = "nationality";
  		values.put(NATIONALITY, "0");
  		String GREWUP = "grewup";
  		values.put(GREWUP, "0");
  		String HOME = "home";
  		values.put(HOME, "0");
  		String SCHOOL = "school";
  		values.put(SCHOOL, "0");
  		String MAJOR = "major";
  		values.put(MAJOR, "0");
  		String MINOR = "minor";
  		values.put(MINOR, "0");
  		String YEAR = "year";
  		values.put(YEAR, "0");
  		String WORK_FIELD = "work_field";
  		values.put(WORK_FIELD, "0");
  		String POSITION = "position";
  		values.put(POSITION, "0");
  		String EMPLOYER = "employer";
  		values.put(EMPLOYER, "0");
  		String SEXUAL_ORIENTATION = "sexual_orientation";
  		values.put(SEXUAL_ORIENTATION, "0");
  		String RELATIONSHIP = "relationship";
  		values.put(RELATIONSHIP, "0");
  		String RELIGIOUS = "religious";
  		values.put(RELIGIOUS, "0");
  		String INTEREST1 = "interest1";
  		values.put(INTEREST1, "0");
  		String INTEREST2 = "interest2";
  		values.put(INTEREST2, "0");
  		String INTEREST3 = "interest3";
  		values.put(INTEREST3, "0");
  		String INTEREST4 = "interest4";
  		values.put(INTEREST4, "0");
  		String INTEREST5 = "interest5";
  		values.put(INTEREST5, "0");
  				
  		sd.insertOrThrow(TABLE_NAME, null, values);			
  		sd.close();		      	
  	}
	
  	//page1 method
  	public void Page1(View view)
  	{  		
  		RadioButton rdbtngeneral_interestyes = (RadioButton) findViewById(R.id.rdbtngeneral_interestyes);
  		RadioButton rdbtngeneral_interestno = (RadioButton) findViewById(R.id.rdbtngeneral_interestno);
  		
  		if(rdbtngeneral_interestyes.isChecked())
  		{
  			general_interest = 1;
  		}
  		else if(rdbtngeneral_interestno.isChecked())
  		{
  			general_interest = 2;
  		}
  		
  		if(general_interest == 0)
  		{
  			Check();
  		}
  		else
  		{
  			/*String holdAtt[] = new String[30];
			String holdAttType[] = new String[30];
			
			FileInputStream fisType = null;
			FileInputStream fisAtt = null;
			
			String AttributeFile = "AttributeFile.txt";
			String AttributeTypeFile = "AttributeTypeFile.txt";
			
			String attpath = (getFilesDir()+File.separator+AttributeFile);
			String attTypepath = (getFilesDir()+File.separator+AttributeTypeFile);
			
			File attfilepath = new File(attpath);
			File attTypefilepath = new File(attTypepath);*/
			
			try
			{
				randomattributeType = this.pullAttribute();
			} 
			catch (IOException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
						
			try 
			{
				randomattributeType = ReturnAtt();
			}
			catch (IOException e1) 
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
						
			/*try 
			{
				fisAtt = new FileInputStream(attpath);
				fisType = new FileInputStream(attTypepath);
			} 
			catch (FileNotFoundException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			BufferedReader bfrAtt = new BufferedReader(new InputStreamReader(fisAtt));
			BufferedReader bfr = new BufferedReader(new InputStreamReader(fisType));
					
			int x = 0;
						
			for(x=0; x < randomattributeType +1; x++)
			{				
				try 
				{
					holdAttType[x] = bfr.readLine();
					holdAtt[x] = bfrAtt.readLine();
				} 
				catch (IOException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				Att = holdAtt[x].toString();
				AttType = holdAttType[x].toString();				
			}		
			
			Log.e("AttType", AttType.toString());
			Log.e("Att", Att.toString());
			
			try 
			{
				bfrAtt.close();
				bfr.close();
			} 
			catch (IOException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				
  			setContentView(R.layout.page2);
  			
			TextView txtAttType = (TextView) findViewById(R.id.txtAttType);
			txtAttType.setText(AttType.toString());			
			
			TextView txtviewAttribute = (TextView) findViewById(R.id.txtviewAttribute);
			txtviewAttribute.setText(Att.toString());*/
			
			AttLoad(view);
  		}
  		
  	}
  	

	//page 2 method
	public void Page2(View view) throws IOException
	{
		RadioButton rdbtnattyes = (RadioButton) findViewById(R.id.rdbtnattyes);
		RadioButton rdbtnattno = (RadioButton) findViewById(R.id.rdbtnattno);
		
		if(rdbtnattyes.isChecked())
		{
			attribute_interest = 1;
		}
		else if(rdbtnattno.isChecked())
		{
			attribute_interest = 2;
		}
				
		//error checking to see if attribute_interest is still "0"
		if(attribute_interest == 0)
		{
			Check();
		}
		else if(rdbtnattyes.isChecked())
		{	
			//displays third page
			setContentView(R.layout.page3);		
		}			
		else if(rdbtnattno.isChecked())
		{
			//displays fourth page
			setContentView(R.layout.page4);
		}		
	}
	
	//getting the user's attributes from the mysql table
	public void ThreadAtt(final String getUserID)
	{
		new Thread (new Runnable()
		{
			@Override
			public void run() 
			{	
				String web_survey_url = "http://173.255.233.194/User_Attributes.php";
				
				//instantiating the second JSONParserSurvey class that handles the survey table from MySQL
				JSONParserUser_Attributes  jparser2 = new JSONParserUser_Attributes ();
				
				List<NameValuePair> params = new ArrayList<NameValuePair>();
								
				params.add(new BasicNameValuePair("USERID", getUserID.toString()));				
				
				Log.e("this is getUserID in threadatt", getUserID.toString());
				Log.e("this is params", params.toString());
			
				JSONObject json = JSONParserUser_Attributes .makeHttpRequest(web_survey_url, params, context);	
			}
		}).start();			
	}
	
	//page 3 method
	public void Page3(View view) throws IOException
	{			
		yes = 1;
		
		CheckBox ckboxreason_similarity = (CheckBox) findViewById(R.id.ckboxreason_similarity);
		CheckBox ckboxreason_place = (CheckBox) findViewById(R.id.ckboxreason_place);
		CheckBox ckboxreason_activity = (CheckBox) findViewById(R.id.ckboxreason_activity);
		CheckBox ckboxreason_entourage = (CheckBox) findViewById(R.id.ckboxreason_entourage);
		
		if (ckboxreason_similarity.isChecked())
		{
			reason_similarity = 1;
		}
		if (ckboxreason_place.isChecked())
		{
			reason_place = 1;
		}
		if (ckboxreason_activity.isChecked())
		{
			reason_activity = 1;
		}
		if (ckboxreason_entourage.isChecked())
		{
			reason_entourage = 1;
		}
		
		if(reason_similarity == 0 && reason_place == 0 && reason_activity == 0 && reason_entourage == 0)
		{
			Check();
		}
		else
		{			
			setContentView(R.layout.page5);
		}
	}
	
	//page 4 method
	public void Page4(View view) throws IOException
	{
		no = 1;
		
		CheckBox ckboxreason_similarity = (CheckBox) findViewById(R.id.ckboxreason_similarity2);
		CheckBox ckboxreason_place = (CheckBox) findViewById(R.id.ckboxreason_place2);
		CheckBox ckboxreason_activity = (CheckBox) findViewById(R.id.ckboxreason_activity2);
		CheckBox ckboxreason_entourage = (CheckBox) findViewById(R.id.ckboxreason_entourage2);
		
		if (ckboxreason_similarity.isChecked())
		{
			reason_similarity = 1;
		}
		if (ckboxreason_place.isChecked())
		{
			reason_place = 1;
		}
		if (ckboxreason_activity.isChecked())
		{
			reason_activity = 1;
		}
		if (ckboxreason_entourage.isChecked())
		{
			reason_entourage = 1;
		}
		
		if(reason_similarity == 0 && reason_place == 0 && reason_activity == 0 && reason_entourage == 0)
		{
			Check();
		}
		else
		{			
			setContentView(R.layout.page5);
		}
	}	
		
	//page 5 method
	public void Page5(View view)
	{	
		RadioButton rdbtnhome = (RadioButton) findViewById(R.id.rdbtnhome);
		RadioButton rdbtneducational = (RadioButton) findViewById(R.id.rdbtneducational);
		RadioButton rdbtnvocational = (RadioButton) findViewById(R.id.rdbtnvocational);
		RadioButton rdbtnrecreational = (RadioButton) findViewById(R.id.rdbtnrecreational);
		RadioButton rdbtntransit = (RadioButton) findViewById(R.id.rdbtntransit);
		
		RadioButton rdbtnsociability1 = (RadioButton) findViewById(R.id.rdbtnsociability1);
		RadioButton rdbtnsociability2 = (RadioButton) findViewById(R.id.rdbtnsociability2);
		RadioButton rdbtnsociability3 = (RadioButton) findViewById(R.id.rdbtnsociability3);
		RadioButton rdbtnsociability4 = (RadioButton) findViewById(R.id.rdbtnsociability4);
		RadioButton rdbtnsociability5 = (RadioButton) findViewById(R.id.rdbtnsociability5);
		
					
		if(rdbtnhome.isChecked())
		{
			place_type = 1;
		}
		else if(rdbtneducational.isChecked())
		{
			place_type = 2;
		}
		else if(rdbtnvocational.isChecked())
		{
			place_type = 3;
		}
		else if(rdbtnrecreational.isChecked())
		{
			place_type = 4;
		}
		else if(rdbtntransit.isChecked())
		{
			place_type = 5;
		}
		
		if(rdbtnsociability1.isChecked())
		{
			sociability = 1;
		}
		else if(rdbtnsociability2.isChecked())
		{
			sociability = 2;
		}
		else if(rdbtnsociability3.isChecked())
		{
			sociability = 3;
		}
		else if(rdbtnsociability4.isChecked())
		{
			sociability = 4;
		}
		else if(rdbtnsociability5.isChecked())
		{
			sociability = 5;
		}
		
		if(place_type == 0 || sociability == 0)
		{
			Check();
		}
		else
		{			
			AttLoad(view);
			
		}
	}
	
	//page 6 method
	public void Page6(View view) throws IOException 
	{			
		RadioButton rdbtnrarity1 = (RadioButton) findViewById(R.id.rdbtnrarity1);
		RadioButton rdbtnrarity2 = (RadioButton) findViewById(R.id.rdbtnrarity2);
		RadioButton rdbtnrarity3 = (RadioButton) findViewById(R.id.rdbtnrarity3);
		RadioButton rdbtnrarity4 = (RadioButton) findViewById(R.id.rdbtnrarity4);
		RadioButton rdbtnrarity5 = (RadioButton) findViewById(R.id.rdbtnrarity5);
		
		RadioButton rdbtnfamiliarity_people1 = (RadioButton) findViewById(R.id.rdbtnfamiliarity_people1);
		RadioButton rdbtnfamiliarity_people2 = (RadioButton) findViewById(R.id.rdbtnfamiliarity_people2);
		RadioButton rdbtnfamiliarity_people3 = (RadioButton) findViewById(R.id.rdbtnfamiliarity_people3);
		RadioButton rdbtnfamiliarity_people4 = (RadioButton) findViewById(R.id.rdbtnfamiliarity_people4);
		RadioButton rdbtnfamiliarity_people5 = (RadioButton) findViewById(R.id.rdbtnfamiliarity_people5);
		
		if(rdbtnrarity1.isChecked())
		{
			rarity = 1;
		}
		else if(rdbtnrarity2.isChecked())
		{
			rarity = 2;
		}
		else if(rdbtnrarity3.isChecked())
		{
			rarity = 3;
		}
		else if(rdbtnrarity4.isChecked())
		{
			rarity = 4;
		}
		else if(rdbtnrarity5.isChecked())
		{
			rarity = 5;
		}
		
		if(rdbtnfamiliarity_people1.isChecked())
		{
			familiarity_people = 1;
		}
		else if(rdbtnfamiliarity_people2.isChecked())
		{
			familiarity_people = 2;
		}
		else if(rdbtnfamiliarity_people3.isChecked())
		{
			familiarity_people = 3;
		}
		else if(rdbtnfamiliarity_people4.isChecked())
		{
			familiarity_people = 4;
		}
		else if(rdbtnfamiliarity_people5.isChecked())
		{
			familiarity_people = 5;
		}
		
		if(rarity == 0 || familiarity_people == 0)
		{
			Check();
		}
		else
		{			
			setContentView(R.layout.page7);
		}
	}
	
	//page 7 method
	public void Page7(View view) throws IOException
	{	
		RadioButton rdbtncrowdedness1 = (RadioButton) findViewById(R.id.rdbtncrowdedness1);
		RadioButton rdbtncrowdedness2 = (RadioButton) findViewById(R.id.rdbtncrowdedness2);
		RadioButton rdbtncrowdedness3 = (RadioButton) findViewById(R.id.rdbtncrowdedness3);
		RadioButton rdbtncrowdedness4 = (RadioButton) findViewById(R.id.rdbtncrowdedness4);
		RadioButton rdbtncrowdedness5 = (RadioButton) findViewById(R.id.rdbtncrowdedness5);
		
		RadioButton rdbtnbusyness1 = (RadioButton) findViewById(R.id.rdbtnbusyness1);
		RadioButton rdbtnbusyness2 = (RadioButton) findViewById(R.id.rdbtnbusyness2);
		RadioButton rdbtnbusyness3 = (RadioButton) findViewById(R.id.rdbtnbusyness3);
		RadioButton rdbtnbusyness4 = (RadioButton) findViewById(R.id.rdbtnbusyness4);
		RadioButton rdbtnbusyness5 = (RadioButton) findViewById(R.id.rdbtnbusyness5);
		
		if(rdbtncrowdedness1.isChecked())
		{
			crowdedness = 1;
		}
		else if(rdbtncrowdedness2.isChecked())
		{
			crowdedness = 2;
		}
		else if(rdbtncrowdedness3.isChecked())
		{
			crowdedness = 3;
		}
		else if(rdbtncrowdedness4.isChecked())
		{
			crowdedness = 4;
		}
		else if(rdbtncrowdedness5.isChecked())
		{
			crowdedness = 5;
		}
		
		if(rdbtnbusyness1.isChecked())
		{
			busyness = 1;
		}
		else if(rdbtnbusyness2.isChecked())
		{
			busyness = 2;
		}
		else if(rdbtnbusyness3.isChecked())
		{
			busyness = 3;
		}
		else if(rdbtnbusyness4.isChecked())
		{
			busyness = 4;
		}
		else if(rdbtnbusyness5.isChecked())
		{
			busyness = 5;
		}
		
		if(crowdedness == 0 || busyness == 0)
		{
			Check();
		}
		else
		{
			setContentView(R.layout.page8);
		}
	}
	
	//page 8 method
	public void Page8(View view)
	{
		String TABLE_NAME = "ESM_data";
		
		RadioButton rdbtnfamiliarity_place1 = (RadioButton) findViewById(R.id.rdbtnfamiliarity_place1);
		RadioButton rdbtnfamiliarity_place2 = (RadioButton) findViewById(R.id.rdbtnfamiliarity_place2);
		RadioButton rdbtnfamiliarity_place3 = (RadioButton) findViewById(R.id.rdbtnfamiliarity_place3);
		RadioButton rdbtnfamiliarity_place4 = (RadioButton) findViewById(R.id.rdbtnfamiliarity_place4);
		RadioButton rdbtnfamiliarity_place5 = (RadioButton) findViewById(R.id.rdbtnfamiliarity_place5);
		
		if(rdbtnfamiliarity_place1.isChecked())
		{
			familiarity_place = 1;
		}
		else if(rdbtnfamiliarity_place2.isChecked())
		{
			familiarity_place = 2;
		}
		else if(rdbtnfamiliarity_place3.isChecked())
		{
			familiarity_place = 3;
		}
		else if(rdbtnfamiliarity_place4.isChecked())
		{
			familiarity_place = 4;
		}
		else if(rdbtnfamiliarity_place5.isChecked())
		{
			familiarity_place = 5;
		}
		
		if(familiarity_place == 0)
		{
			Check();
		}
		else
		{
			//creating the timestamp
			Calendar cldr = Calendar.getInstance();
			SimpleDateFormat SDformat = new SimpleDateFormat("MM-dd-yyyy H:mm:ss a");
			String currentTime = SDformat.format(cldr.getTime());
									
			Log.e("this is the date", currentTime.toString());			
			
			//insert a new record into the ESM_data database in SQLite
			TableESM_data ts = new TableESM_data(this);
			SQLiteDatabase dw = ts.getWritableDatabase();
			
			ContentValues values = new ContentValues();
			String USER_ID = "user_ID";
			try 
			{
				values.put(USER_ID, String.valueOf(this.UserIDTextFile(view)));
			} 
			catch (IOException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			String TIMESTAMP = "timestamp";
			values.put(TIMESTAMP, currentTime.toString());
			String GEOLOCATION = "geolocation";
			values.put(GEOLOCATION, this.GetCity());
			String GENERAL_INTEREST = "general_interest";
			values.put(GENERAL_INTEREST, general_interest);
			String ATTRIBUTE = "attribute";
			values.put(ATTRIBUTE, Att);
			String ATTRIBUTE_TYPE = "attribute_type";
			values.put(ATTRIBUTE_TYPE, AttType);
			String INTERESTED = "interested";
			values.put(INTERESTED, attribute_interest);
			String REASON_SIMILARITY = "reason_similarity";
			values.put(REASON_SIMILARITY, reason_similarity);
			String REASON_PLACE = "reason_place";
			values.put(REASON_PLACE, reason_place);
			String REASON_ACTIVITY = "reason_activity";
			values.put(REASON_ACTIVITY, reason_activity);
			String REASON_ENTOURAGE = "reason_entourage";
			values.put(REASON_ENTOURAGE, reason_entourage);
			String PLACE_TYPE = "place_type";
			values.put(PLACE_TYPE, place_type);
			String SOCIABILITY = "sociability";
			values.put(SOCIABILITY, sociability);
			String RARITY = "rarity";
			values.put(RARITY, rarity);
			String FAMILIARITY_PPL = "familiarity_ppl";
			values.put(FAMILIARITY_PPL, familiarity_people);
			String CROWDEDNESS = "crowdedness";
			values.put(CROWDEDNESS, crowdedness);
			String BUSYNESS = "busyness";
			values.put(BUSYNESS, busyness);
			String FAMILIARITY_PLACE = "familiarity_place";
			values.put(FAMILIARITY_PLACE, familiarity_place);
			//String INTEREST_STRENGTH = "interest_strength";
			//values.put(INTEREST_STRENGTH, interest_strength);
			
			Log.e("Att", Att.toString());
			Log.e("AttType", AttType.toString());
			
			//flag to indicate that the record has not been uploaded to MySQL
			String SYNC = "SYNC";
			values.put(SYNC, "0");
							
			dw.insertOrThrow(TABLE_NAME, null, values);			
			dw.close();				
        	
			// updating table ESM_data on MySQL in background thread	       
			UpdateDatabase();
	        
	        //checks that the records in SQLite's ESM_data table have been uploaded to MySQL
			SyncCheck();
			
			//displaying the final page
			setContentView(R.layout.confirmation);
		}
	}
	
	//updating the ESM_data database on SQLite on a different thread
	public void UpdateDatabase()
	{
		new Thread (new Runnable()
		{
			@Override
			public void run() 
			{	
				//instantiating cursor
				Cursor cursorGetSurvey = null;
				try
				{
					//calling getSurvey
					getSurvey(cursorGetSurvey);
				}
				catch (ClientProtocolException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
				catch (JSONException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
				catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		  //starts the thread
		}).start();
	}
	
	//selecting all records from the ESM_data table in SQLite database
	public void getSurvey(Cursor cursorGetSurvey) throws JSONException, ClientProtocolException, IOException 
	{
		final String TABLE_NAME = "ESM_data";		
		final String[] FROM = {"_id", "USER_ID", "TIMESTAMP","GEOLOCATION", "GENERAL_INTEREST", "ATTRIBUTE", "ATTRIBUTE_TYPE", "INTERESTED", "REASON_SIMILARITY", "REASON_PLACE", "REASON_ACTIVITY", "REASON_ENTOURAGE", "PLACE_TYPE", "SOCIABILITY", "RARITY", "FAMILIARITY_PPL", "CROWDEDNESS", "BUSYNESS", "FAMILIARITY_PLACE", "SYNC"};
		final String ORDER_BY = "_id DESC";
							
		//perform a managed query. the activity will handle closing and requerying the cursor when needed
		TableESM_data ts = new TableESM_data(this);
		SQLiteDatabase db = ts.getReadableDatabase();
		
		//cursor now has all records from ESM_data table in SQLite database		
		cursorGetSurvey = db.query(TABLE_NAME, FROM, null, null, null, null, ORDER_BY);
		startManagingCursor(cursorGetSurvey);
		
		//calling upload survey table method
		uploadSurvey(cursorGetSurvey);		
		cursorGetSurvey.close();
		db.close();
	}
	
	//uploads the records in the ESM_data table in SQLite to the ESM_data table in MySQL
	public void uploadSurvey(Cursor cursorUploadSurvey) throws JSONException, ClientProtocolException, IOException 
	{
		TableESM_data ts = new TableESM_data(this);
		SQLiteDatabase dr = ts.getReadableDatabase();
	
		cursorUploadSurvey.moveToFirst();
		{				
			//String ESM_ID = cursorUploadSurvey.getString(0);
			String USER_ID = cursorUploadSurvey.getString(1);
			String TIMESTAMP = cursorUploadSurvey.getString(2);
			String GEOLOCATION = cursorUploadSurvey.getString(3);
			String GENERAL_INTEREST = cursorUploadSurvey.getString(4);
			String ATTRIBUTE = cursorUploadSurvey.getString(5);
			String ATTRIBUTE_TYPE = cursorUploadSurvey.getString(6);
			String INTERESTED = cursorUploadSurvey.getString(7);
			String REASON_SIMILARITY = cursorUploadSurvey.getString(8);
			String REASON_PLACE = cursorUploadSurvey.getString(9);
			String REASON_ACTIVITY = cursorUploadSurvey.getString(10);
			String REASON_ENTOURAGE = cursorUploadSurvey.getString(11);
			String PLACE_TYPE = cursorUploadSurvey.getString(12);
			String SOCIABILITY = cursorUploadSurvey.getString(13);
			String RARITY = cursorUploadSurvey.getString(14);
			String FAMILIARITY_PPL = cursorUploadSurvey.getString(15);
			String CROWDEDNESS = cursorUploadSurvey.getString(16);
			String BUSYNESS = cursorUploadSurvey.getString(17);
			String FAMILIARITY_PLACE = cursorUploadSurvey.getString(18);
			
			
			// Building Parameters 
	    	List<NameValuePair> params = new ArrayList<NameValuePair>();
	    	
	    	//params.add(new BasicNameValuePair("ESM_ID", ESM_ID));	    	
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

	    	Log.e("user ID", USER_ID.toString());
	    	Log.e("time", TIMESTAMP.toString());
	    	Log.e("geolocation", GEOLOCATION.toString());
	    	Log.e("general_interest", GENERAL_INTEREST.toString());
	    	Log.e("ATTRIBUTE", ATTRIBUTE.toString());
	    	Log.e("ATTRIBUTE_TYPE", ATTRIBUTE_TYPE.toString());
	    	Log.e("INTERESTED", INTERESTED.toString());
	    	Log.e("REASON_SIMILARITY", REASON_SIMILARITY.toString());
	    	Log.e("REASON_PLACE", REASON_PLACE.toString());
	    	Log.e("REASON_ACTIVITY", REASON_ACTIVITY.toString());
	    	Log.e("REASON_ENTOURAGE", REASON_ENTOURAGE.toString());
	    	Log.e("PLACE_TYPE", PLACE_TYPE.toString());
	    	Log.e("SOCIABILITY", SOCIABILITY.toString());
	    	Log.e("RARITY", RARITY.toString());
	    	Log.e("FAMILIARITY_PPL", FAMILIARITY_PPL.toString());
	    	Log.e("CROWDEDNESS", CROWDEDNESS.toString());
	    	Log.e("BUSYNESS", BUSYNESS.toString());
	    	Log.e("FAMILIARITY_PLACE", FAMILIARITY_PLACE.toString());
		    	
	    	//sends data in params to JSONParserESM_data
	    	JSONObject json = JSONParserESM_data.makeHttpRequest(url_update_database, params);  
	    }
		
		cursorUploadSurvey.close();
		dr.close(); 		
	}
	
	//method to place attribute in text file from user_attributes table
	@SuppressLint("NewApi")
	public int pullAttribute() throws IOException, InterruptedException
	{				
		FileInputStream fisType = null;
		FileInputStream fisAtt = null;
		FileWriter fWriter = null;
		FileWriter fWriter2 = null;
		
		String AttributeTypeFile = "AttributeTypeFile.txt";
		String AttributeFile = "AttributeFile.txt";
		
		String attTypepath = (getFilesDir()+File.separator+AttributeTypeFile);
		String attpath = (getFilesDir()+File.separator+AttributeFile);
		
		File attTypefilepath = new File(attTypepath);
		File attfilepath = new File(attpath);
		
		String read;
		StringBuilder builder = new StringBuilder("");
		
		String readType = "";
		String readatt = "";
		
		String holdColName[] = new String[38];
		String holdColValue[] = new String[38];
		
		int y = 0;
		int z = 0;
		int colcounter = 0;
		
		String holdType[] = new String[38];
		String holdAtt[] = new String[38];
						
		TableUser_Attributes tua = new TableUser_Attributes(this);
		SQLiteDatabase sdb = tua.getReadableDatabase();
		
		Cursor cursor = sdb.rawQuery("SELECT * FROM user_attributes", null);
		
		cursor.moveToFirst();
		
		if(attTypefilepath.exists() == false)
		{				
			for(y=0; y < cursor.getColumnCount() -1; y++)
			{
				String tempName = cursor.getColumnName(y).trim();
				
				//if(cursor.getColumnName(y).trim() == "age")
				if(tempName.contains("age") && !tempName.contains("age_imp"))
				{
					//holdColName[z] = cursor.getColumnName(y);
					holdColName[z] = tempName.toString();
					
					fWriter = new FileWriter(attTypefilepath, true);
					//fWriter.write(holdColName[z].toString());
					fWriter.write(tempName.toString());
					fWriter.write("\r\n");
					fWriter.close();
				
					Log.e("holdcolname", holdColName[z].toString());
					
					holdColValue[z] = String.valueOf(cursor.getInt(y));
					
					fWriter2 = new FileWriter(attpath, true);
					fWriter2.write(holdColValue[z].toString());
					fWriter2.write("\r\n");
					fWriter2.close();
					
					Log.e("holdcolValue", holdColValue[z].toString());
					
					z++;
				}				
				else if(cursor.getType(y) == Cursor.FIELD_TYPE_STRING)
				{	
					holdColName[z] = cursor.getString(y).toString();
					
					if(holdColName[z].toString().trim().equals("NULL") || holdColName[z].toString().trim().equals("") | holdColName[z].toString().trim().equals(null))
					{
						z++;
					}
					else
					{
						holdColName[z] = cursor.getColumnName(y);
						
						fWriter = new FileWriter(attTypefilepath, true);
						fWriter.write(holdColName[z].toString());
						fWriter.write("\r\n");
						fWriter.close();
					
						Log.e("holdcolname", holdColName[z].toString());
						
						holdColValue[z] = cursor.getString(y);
						
						fWriter2 = new FileWriter(attpath, true);
						fWriter2.write(holdColValue[z].toString());
						fWriter2.write("\r\n");
						fWriter2.close();
						
						Log.e("holdcolValue", holdColValue[z].toString());
						
						z++;	
					}
				}
			}
			
			randomattributeType = random.nextInt(z);
		}	
		else
		{
			int x = 0;
			
			fisType = new FileInputStream(attTypepath);
			BufferedReader bfr = new BufferedReader(new InputStreamReader(fisType));
			
			fisAtt = new FileInputStream(attpath);
			BufferedReader bfr2 = new BufferedReader(new InputStreamReader(fisAtt));
					
			int c = 0;
			String tempType = "";
			String tempAtt = "";
			
			for(x=0; x < 20; x++)
			{
				bfr.readLine();
				
				if(bfr.readLine() == null)
				{
					break;
				}
			}
			
			fisType = new FileInputStream(attTypepath);
			BufferedReader bfrType = new BufferedReader(new InputStreamReader(fisType));
			
			fisAtt = new FileInputStream(attpath);
			BufferedReader bfr2Att = new BufferedReader(new InputStreamReader(fisAtt));
			
			Log.e("x", String.valueOf(x));
			
			randomattributeType = random.nextInt(x);
						
			for(c=0; c < randomattributeType +1; c++)
			{
				holdType[c] = bfrType.readLine();
				holdAtt[c] = bfr2Att.readLine();
				
				tempType = holdType[c].toString();
				tempAtt = holdAtt[c].toString();
			}		
			
			Log.e("holdType", tempType.toString());
			Log.e("holdAtt", tempAtt.toString());
			
			bfr.close();
			bfr2.close();
		
			Log.e("randomizer", String.valueOf(randomattributeType));
		}
		
		return randomattributeType;		
	}
	
	//method that pulls the attribute
	public int ReturnAtt() throws IOException
	{
		String TABLE_NAME = "AttType";
		String colname = "";
		String colvalue = "";
		
		Random randomnum = new Random();
				
		TableAttType tat = new TableAttType(this);
		SQLiteDatabase sd = tat.getWritableDatabase();		

		//select * from table
		Cursor cursorAtt = sd.rawQuery("SELECT * FROM AttType", null);
		
		//select the column name that matches the number of randomattributeType
		cursorAtt = sd.rawQuery("SELECT " + cursorAtt.getColumnName(randomattributeType +1) + " FROM AttType", null);;
		
		Log.e("randomattributeType", String.valueOf(randomattributeType));
		
		cursorAtt.moveToFirst();
	
		colvalue = cursorAtt.getString(0).trim();
		colname = cursorAtt.getColumnName(0).trim();
		
		Log.e("colname", colname.toString());
		Log.e("colvalue", colvalue.toString());
		
		ContentValues values = new ContentValues();
		
		//checking the value of the column
		if(colvalue.equals("0"))
		{			
			values.put(colname, "1");
			Log.e("colname", colname.toString());
			
			sd.update(TABLE_NAME, values, colname + "=" + colname, null);			
		}
		if(colvalue.equals("1"))
		{			
			values.put(colname, "2");
			Log.e("colname", colname.toString());
			
			sd.update(TABLE_NAME, values, colname + "=" + colname, null);				
		}
		
		//if the column's value equals 2 then pick another random number
		if(colvalue.equals("2"))
		{	
			b++;
			
			if(b < 20)
			{			
				FileInputStream fisType = null;
										
				String AttributeTypeFile = "AttributeTypeFile.txt";
				
				String attTypepath = (getFilesDir()+File.separator+AttributeTypeFile);
				
				fisType = new FileInputStream(attTypepath);
				
				BufferedReader bufferedreadertype = new BufferedReader(new InputStreamReader(fisType));
						
				int x = 0;
							
				while(bufferedreadertype.readLine()!= null)
				{										
					Log.e("x", String.valueOf(x));
					
					x++;
				}
							
				randomattributeType = randomnum.nextInt(x);
				
				Log.e("randomattributeType", String.valueOf(randomattributeType));
				
				ReturnAtt();
			}
			else
			{
				Toast.makeText(this, "No new attributes to choose from.", Toast.LENGTH_LONG).show();
			}
					
		}
		
		Log.e("values", values.toString());
				
		sd.close();	
				
		return(randomattributeType);
		
	}
	
	public void BackButton(View viewID)
	{	
		//currentid is the string of the tag name
		String currentid = viewID.getTag().toString();
		
		Log.e("currentid", currentid.toString());
		
		if(currentid.toString().contains("page2"))
		{
			general_interest = 0;
			
			setContentView(R.layout.page1);
		}
		else if(currentid.toString().contains("page3"))
		{
			attribute_interest = 0;
			
			AttLoad(viewID);
			
			setContentView(R.layout.page2);
			
			TextView txtAttType = (TextView) findViewById(R.id.txtAttType);
			txtAttType.setText(AttType.toString());			
			
			TextView txtviewAttribute = (TextView) findViewById(R.id.txtviewAttribute);
			txtviewAttribute.setText(Att.toString());
		}
		else if(currentid.toString().contains("page4"))
		{
			attribute_interest = 0;
			
			setContentView(R.layout.page2);
			
			TextView txtAttType2 = (TextView) findViewById(R.id.txtAttType);
			txtAttType2.setText(AttType.toString());			
			
			TextView txtviewAttribute2 = (TextView) findViewById(R.id.txtviewAttribute);
			txtviewAttribute2.setText(Att.toString());
		}
		else if(currentid.toString().contains("page5"))
		{
			reason_similarity = 0;
			reason_place = 0;
			reason_activity = 0;
			reason_entourage = 0;
		
			if(no == 1)
			{
				no = 0;
				setContentView(R.layout.page4);
			}
			else
			{
				yes = 0;
				setContentView(R.layout.page3);
			}
			
		}
		else if(currentid.toString().contains("page6"))
		{
			place_type = 0;
			sociability = 0;
			
			setContentView(R.layout.page5);
		}
		else if(currentid.toString().contains("page7"))
		{
			rarity = 0;
			familiarity_people = 0;
			
			AttLoad(viewID);
			
			setContentView(R.layout.page6);
			
			TextView txtAtt3 = (TextView) findViewById(R.id.txtAtt3);
			txtAtt3.setText(Html.fromHtml("3. How common do you think having the attribute " + "<b> <i>" + Att + "</b> </i>" + " is right now?"));		
		}
		else if(currentid.toString().contains("page8"))
		{
			crowdedness = 0;
			busyness = 0;
			
			setContentView(R.layout.page7);
		}		
	}
	
	//pulls the attributes from the text files
	public void AttLoad(View view)
	{
		//String currentviewtag = view.getTag().toString();
		
		String holdAtt[] = new String[30];
		String holdAttType[] = new String[30];
		
		FileInputStream fisType = null;
		FileInputStream fisAtt = null;
		
		String AttributeFile = "AttributeFile.txt";
		String AttributeTypeFile = "AttributeTypeFile.txt";
		
		String attpath = (getFilesDir()+File.separator+AttributeFile);
		String attTypepath = (getFilesDir()+File.separator+AttributeTypeFile);
		
		//File attfilepath = new File(attpath);
		//File attTypefilepath = new File(attTypepath);
		
		try 
		{
			fisAtt = new FileInputStream(attpath);
			fisType = new FileInputStream(attTypepath);
		} 
		catch (FileNotFoundException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		BufferedReader bfrAtt = new BufferedReader(new InputStreamReader(fisAtt));
		BufferedReader bfr = new BufferedReader(new InputStreamReader(fisType));
				
		int x = 0;
					
		for(x=0; x < randomattributeType +1; x++)
		{				
			try 
			{
				holdAttType[x] = bfr.readLine();
				holdAtt[x] = bfrAtt.readLine();
			} 
			catch (IOException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			AttType = holdAttType[x].toString();
			Att = holdAtt[x].toString();							
		}		
		
		Log.e("AttType", AttType.toString());
		Log.e("Att", Att.toString());
		
		Log.e("view's tag", view.getTag().toString());
		
		//if(currentviewtag.toString().contains("page1"))
		if(view.getTag().toString().contains("page1"))
		{
			setContentView(R.layout.page2);
  			
			TextView txtAttType = (TextView) findViewById(R.id.txtAttType);
			txtAttType.setText(AttType.toString());			
			
			TextView txtviewAttribute = (TextView) findViewById(R.id.txtviewAttribute);
			txtviewAttribute.setText(Att.toString());
  		}
		else if(view.getTag().toString().contains("page5"))
		{
			setContentView(R.layout.page6);
			
			TextView txtAtt3 = (TextView) findViewById(R.id.txtAtt3);
			txtAtt3.setText(Html.fromHtml("3. How common do you think having the attribute " + "<b> <i>" + Att.toString() + "</b> </i>" + " is right now?"));		
		}
						
		try 
		{
			bfrAtt.close();
			bfr.close();
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	//method to display a toast message if variable answers are = "0"
	public void Check()
	{
		Toast.makeText(this, "Please choose an answer.", Toast.LENGTH_LONG).show();
	}
	
	//method to check if the phone has an online connection
	public String SyncCheck()
	{				
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    if (netInfo != null && netInfo.isConnected()) 
	    {
	    	//This toast message will be removed in the final product
	        Toast.makeText(this, "Syncing Survey to online database.", Toast.LENGTH_LONG).show();
	        
	        TableESM_data ts = new TableESM_data(this);
			SQLiteDatabase dw = ts.getWritableDatabase();
			
			//instantiating a content value variable
			ContentValues cv = new ContentValues();
			//the value 1 is a flag to verify that the records have been uploaded to MySQL
			cv.put("SYNC", "1");
			
			//replaces all 0's with 1's
			dw.update("ESM_data", cv, "SYNC = 0", null);
						
			Log.e("SQLupdate", cv.toString());
			
			dw.close();
	    }
	    else
	    {
	    	 //This toast message will be removed in the final product	
	    	 Toast.makeText(context, "You are offline. I cannot sync Survey to the online database.", Toast.LENGTH_LONG).show();
	    }	 
		return null;	    	
	}	
		
	//closes the program
	public void Exit(View view)
	{
		finish();
	}
	
	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.survey, menu);
		return true;
	}*/	
}
