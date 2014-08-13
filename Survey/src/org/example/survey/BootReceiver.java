package org.example.survey;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class BootReceiver extends BroadcastReceiver
{
	// Boot intent action name
    private static final String BOOT_ACTION_NAME = "android.intent.action.BOOT_COMPLETED";
 
    @Override
    public void onReceive(Context context, Intent intent)
    {
        if (BOOT_ACTION_NAME.equals(intent.getAction()))
        {
        	//creating the alarm that will wake the phone's cpu up and uploads records to MySQL in the background
    	    Intent intent1 = new Intent(context, TriggerAlarm.class);
    	    Calendar calendar = Calendar.getInstance();
    	    
    	    //These times will change in the final product
    	    calendar.set(Calendar.HOUR_OF_DAY, 24);
    	    calendar.set(Calendar.MINUTE, 00);
    	    
    	    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent1, 0);

    	    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);	    
    	    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_HALF_DAY, pendingIntent);    	    
        }
    }   
}
