package org.example.survey; 

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;
 
public class JSONParserUser_Attributes 
{
 
    static InputStream is;
    static JSONObject jObj;
    static String json = "";    
 
    // constructor
    public JSONParserUser_Attributes() 
    {
 
    }
 
    //method to get the web survey's data from the survey MySQL table
    public static JSONObject makeHttpRequest(String url, List<NameValuePair> params, Context context)
    {      	    	
    	Log.e("In", params.toString());
    	
        // Making HTTP request
        try 
        { 
        	DefaultHttpClient httpClient = new DefaultHttpClient();
            String paramString = URLEncodedUtils.format(params, "utf-8");
        	url += "?" + paramString;        
        	HttpGet httpGet = new HttpGet(url);
            HttpResponse httpResponse = httpClient.execute(httpGet);
            HttpEntity httpEntity = httpResponse.getEntity();
            is = httpEntity.getContent();           
        } 
        catch (UnsupportedEncodingException e) 
        {
            e.printStackTrace();
        } 
        catch (ClientProtocolException e) 
        {
            e.printStackTrace();
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
        
        try
        {    
        	//reading the content of "is" from the .php survey file
        	BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"), 8);
        	
            StringBuilder sb = new StringBuilder();
            String line;                     
            
            String hold;                       
            
            while ((line = reader.readLine()) != null)            
            {            	            	            	
                sb.append(line + ", ");               
            }
            
            is.close();
            json = sb.toString();            
           
        }            	
        catch (Exception e) 
        {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }   
               
        Log.e("All Records: ", json.toString());    
        
        TableUser_Attributes tua = new TableUser_Attributes(context);
        SQLiteDatabase db = tua.getWritableDatabase();
        
        final String TABLE_NAME = "user_attributes";		
		final String[] FROM = {"_id", "GENDER", "GENDER_IMP", "AGE", "AGE_IMP", "NATIONALITY", "NATIONALITY_IMP", "GREWUP", "GREWUP_IMP", "HOME", "HOME_IMP", "SCHOOL", "SCHOOL_IMP", "MAJOR", "MAJOR_IMP", "MINOR", "MINOR_IMP", "YEAR", "YEAR_IMP", "WORK_FIELD", "WORK_FIELD_IMP", "POSITION", "POSITION_IMP", "EMPLOYER", "EMPLOYER_IMP", "SEXUAL_ORIENTATION", "SEXUAL_ORIENTATION_IMP", "RELATIONSHIP", "RELATIONSHIP_IMP", "RELIGIOUS", "RELIGIOUS_IMP", "INTEREST1", "INTEREST1_IMP", "INTEREST2", "INTEREST2_IMP", "INTEREST3", "INTEREST3_IMP", "INTEREST4", "INTEREST4_IMP", "INTEREST5", "INTEREST5_IMP"};
		final String ORDER_BY = "_id DESC";
		
		//select all records fro the web_survey table in SQLite database
        Cursor cursor = db.query(TABLE_NAME, FROM, null, null, null, null, ORDER_BY);      
        
        //moving the cursor to the first position
        cursor.moveToFirst();
        {
        	//getting count of records that exists
        	int count = cursor.getCount();
        	
        	//if there are no records in the web_survey table, create one
        	if (count == 0)
        	{	
        	
        		if(json.toString() != null)
        		{
	        		StringTokenizer tokens = new StringTokenizer(json, ",");
	                String record[] = new String[tokens.countTokens()];
	                int i = 0;        
	                String placeRecord[] = new String[50];
	                String Attribute[] = new String [50];
	                String AttributeType[] = new String[50];
	                
	                while(tokens.hasMoreTokens())
	                {                	
	                	record[i] = tokens.nextToken();
	                	record[i] = record[i].replace("\"", "");
	                	
	                	//Log.e("record[i]", record[i].toString());
	                	i++;
	                }   
	                
	                int x = 0;
	
	                for(i=3; i < 82; i=i+2)
	                {
	                	placeRecord[x] = record[i].toString();
	                	placeRecord[x] = placeRecord[x].replace("'", "");
	                	
	                	AttributeType[x] = placeRecord[x].substring(0, placeRecord[x].indexOf(":"));
	                	Log.e("AttributeType", AttributeType[x].toString());
	                	                	
	                	Attribute[x] = placeRecord[x].substring(placeRecord[x].lastIndexOf(":") + 1);
	                	//Log.e("placeRecord", placeRecord[x].toString());
	                	Log.e("Attribute[x]", Attribute[x].toString());
	                	
	                	x++;
	                }
	                
	                ContentValues values = new ContentValues();
	                
	        		String GENDER = "GENDER";
	        		values.put(GENDER, Attribute[0].toString());
	        		String GENDER_IMP = "GENDER_IMP";
	        		values.put(GENDER_IMP, Attribute[1].toString());
	        		String AGE = "AGE";
	        		values.put(AGE, Attribute[2].toString());
	        		String AGE_IMP = "AGE_IMP";
	        		values.put(AGE_IMP, Attribute[3].toString());
	        		String NATIONALITY = "NATIONALITY";
	        		values.put(NATIONALITY, Attribute[4].toString());
	        		String NATIONALITY_IMP = "NATIONALITY_IMP";
	        		values.put(NATIONALITY_IMP, Attribute[5].toString());
	        		String GREWUP = "GREWUP";
	        		values.put(GREWUP, Attribute[6].toString());
	        		String GREWUP_IMP = "GREWUP_IMP";
	        		values.put(GREWUP_IMP, Attribute[7].toString());
	        		String HOME = "HOME";
	        		values.put(HOME, Attribute[8].toLowerCase());
	        		String HOME_IMP = "HOME_IMP";
	        		values.put(HOME_IMP, Attribute[9].toString());
	        		String SCHOOL = "SCHOOL";
	        		values.put(SCHOOL, Attribute[10].toString());
	        		String SCHOOL_IMP = "SCHOOL_IMP";
	        		values.put(SCHOOL_IMP, Attribute[11].toString());
	        		String MAJOR = "MAJOR";
	        		values.put(MAJOR, Attribute[12].toString());
	        		String MAJOR_IMP = "MAJOR_IMP";
	        		values.put(MAJOR_IMP, Attribute[13].toString());
	        		String MINOR = "MINOR";
	        		values.put(MINOR, Attribute[14].toString());
	        		String MINOR_IMP = "MINOR_IMP";
	        		values.put(MINOR_IMP, Attribute[15].toString());
	        		String YEAR = "YEAR";
	        		values.put(YEAR, Attribute[16].toString());
	        		String YEAR_IMP = "YEAR_IMP";
	        		values.put(YEAR_IMP, Attribute[17].toString());
	        		String WORK_FIELD = "WORK_FIELD";
	        		values.put(WORK_FIELD, Attribute[18].toString());
	        		String WORK_FIELD_IMP = "WORK_FIELD_IMP";
	        		values.put(WORK_FIELD_IMP, Attribute[19].toString());
	        		String POSITION = "POSITION";
	        		values.put(POSITION, Attribute[20].toString());
	        		String POSITION_IMP = "POSITION_IMP";
	        		values.put(POSITION_IMP, Attribute[21].toString());
	        		String EMPLOYER = "EMPLOYER";
	        		values.put(EMPLOYER, Attribute[22].toString());
	        		String EMPLOYER_IMP = "EMPLOYER_IMP";
	        		values.put(EMPLOYER_IMP, Attribute[23].toString());
	        		String SEXUAL_ORIENTATION = "SEXUAL_ORIENTATION";
	        		values.put(SEXUAL_ORIENTATION, Attribute[24].toString());
	        		String SEXUAL_ORIENTATION_IMP = "SEXUAL_ORIENTATION_IMP";
	        		values.put(SEXUAL_ORIENTATION_IMP, Attribute[25].toString());
	        		String RELATIONSHIP = "RELATIONSHIP";
	        		values.put(RELATIONSHIP, Attribute[26].toString());
	        		String RELATIONSHIP_IMP = "RELATIONSHIP_IMP";
	        		values.put(RELATIONSHIP_IMP, Attribute[27].toString());
	        		String RELIGIOUS = "RELIGIOUS";
	        		values.put(RELIGIOUS, Attribute[28].toString());
	        		String RELIGIOUS_IMP = "RELIGIOUS_IMP";
	        		values.put(RELIGIOUS_IMP, Attribute[29].toString());
	        		String INTEREST1 = "INTEREST1";
	        		values.put(INTEREST1, Attribute[30].toString());
	        		String INTEREST1_IMP = "INTEREST1_IMP";
	        		values.put(INTEREST1_IMP, Attribute[31].toString());
	        		String INTEREST2 = "INTEREST2";
	        		values.put(INTEREST2, Attribute[32].toString());
	        		String INTEREST2_IMP = "INTEREST2_IMP";
	        		values.put(INTEREST2_IMP, Attribute[33].toString());
	        		String INTEREST3 = "INTEREST3";
	        		values.put(INTEREST3, Attribute[34].toString());
	        		String INTEREST3_IMP = "INTEREST3_IMP";
	        		values.put(INTEREST3_IMP, Attribute[35].toString());
	        		String INTEREST4 = "INTEREST4";
	        		values.put(INTEREST4, Attribute[36].toString());
	        		String INTEREST4_IMP = "INTEREST4_IMP";
	        		values.put(INTEREST4_IMP, Attribute[37].toString());
	        		String INTEREST5 = "INTEREST5";
	        		values.put(INTEREST5, Attribute[38].toString());
	        		String INTEREST5_IMP = "INTEREST5_IMP";
	        		values.put(INTEREST5_IMP, Attribute[39].toString());
	        		     		
	        		db.insertOrThrow("user_attributes", null, values);			
	        		db.close();	        		
        		}               	        	
        	}   
        }
       
        return jObj; 
    }     
}
    
    