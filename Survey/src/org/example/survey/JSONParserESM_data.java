package org.example.survey; 

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;
 
public class JSONParserESM_data
{
 
    static InputStream is;
    static JSONObject jObj;
    static String json = "";
     
    // constructor
    public JSONParserESM_data() 
    {
 
    }
 
    //method posts value of params to the survey table on MySQL
    public static  JSONObject makeHttpRequest(String url, List<NameValuePair> params) 
    {    
    	Log.e("url", url.toString());
    	
        // making HTTP request
        try 
        { 
        	DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(new UrlEncodedFormEntity(params));
 
            HttpResponse httpResponse = httpClient.execute(httpPost);               
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
            String line = null;
            
            while ((line = reader.readLine()) != null) 
            {
                sb.append(line + "\n");
                Log.e("sb", sb.toString());
            }
            
            is.close();
            //json = json.replaceAll("survey.php", "");   
            Log.e("json", json);
        }            	
        catch (Exception e) 
        {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }           
                
        return jObj; 
    } 
}