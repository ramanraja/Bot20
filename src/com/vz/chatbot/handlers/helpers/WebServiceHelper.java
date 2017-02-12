package com.vz.chatbot.handlers.helpers;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;

import com.vz.chatbot.interfaces.ActionHandler;
import com.vz.chatbot.interfaces.ServiceHandler;

import android.os.AsyncTask;
import android.os.Handler;

/**
 * Provides basic web service calls
 * @author Raja
 *
 */
public class WebServiceHelper {
	  //JSONParser parser = new JSONParser();
	private ServiceHandler svchandler = null;
	private ActionHandler acthandler =null;
	
	public void register(ServiceHandler handler) {
		this.svchandler = handler;
	}
	
	public void register(ActionHandler handler) {
		this.acthandler = handler;
	}
	
	public void doGet (String serviceURL) {
		new StringDemon().execute(serviceURL);
	}
	
	public JSONObject post (String serviceURL, JSONObject data) throws Exception {
		  String response =  post(serviceURL, data.toString());
		  JSONObject jobj = new JSONObject(response);
		  return jobj;      
	}
	    
	public String post (String serviceURL, String data) throws Exception {
	    System.out.println(data);
	    BufferedReader reader = null;
	    try
	    {
	      URL url = new URL(serviceURL);
	      
		 HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		 connection.setRequestMethod("POST");
		 connection.setDoOutput(true);

		 connection.setRequestProperty("User-Agent", "Mozilla/5.0");
		 
		 //connection.setRequestProperty("Accept", "application/json");
		 //connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
		 //connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		 //Cookie cookie=new Cookie("user", "Raja");
		 //cookie.setValue("store");
		 //connection.setRequestProperty("Cookie", cookie.getValue);
	      	     
		DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
		wr.writeBytes(data);
		wr.flush();
		wr.close();      
		
		 int responseCode = connection.getResponseCode();
		 System.out.println("Response Code : " + responseCode);
		 
		 reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		  StringBuilder builder = new StringBuilder();
		  String line = null;
		  while ((line = reader.readLine()) != null) {
		     builder.append(line + "\n");
		  }
		  return builder.toString();
	  }
	  catch (Exception e){
	     e.printStackTrace();
	     throw(e);  
	  }
	  finally {
	       if (reader != null)
	       { try{ reader.close(); }
	         catch (IOException e2)
	         { e2.printStackTrace(); }
	       }
	  }
   }
	  
	  public String get(String serviceURL) throws Exception
	  {
	    // if your url can contain weird characters :
	    //serviceURL = URLEncoder.encode(serviceURL, "UTF-8");
	    BufferedReader reader = null;
	    try
	    {
	      URL url = new URL(serviceURL);
	      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	      connection.setRequestMethod("GET");
	      connection.setReadTimeout(10*1000);
	      connection.connect();
	      reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	
	      StringBuilder builder = new StringBuilder();
	      String line = null;
	      while ((line = reader.readLine()) != null) {
	         builder.append(line + "\n");
	      }
	      return(builder.toString());
	  }
	  catch (Exception e){
		     e.printStackTrace();
		     throw(e);  
		  }
		  finally {
		       if (reader != null)
		       { try{ reader.close(); }
		         catch (IOException e2)
		         { e2.printStackTrace(); }
		   }
		}
	}
	  
	public void test() throws Exception
	{
		WebServiceHelper client = new WebServiceHelper();
	    JSONObject  jobj = new JSONObject();
	    jobj.put("Name", "Nikki");
		jobj.put("Age", "23");    
		jobj.put("City", "Bangalore");    
		System.out.println(jobj.toString());
		System.out.println(jobj.get("Name"));
		System.out.println(jobj.get("Age"));
		String url = "http://validate.jsontest.com/?json=";
	    JSONObject response = client.post(url, jobj);
	    System.out.println(response.toString());
	}  
	
	// inner class in a separate thread
    private class StringDemon  extends AsyncTask<String, String, String> {
    	
    	@Override
    	protected void onPreExecute() {
    		System.out.println("In Pre-execute..");
    	}
    	
		@Override
		protected String doInBackground(String... params) {
			System.out.println("In do-in-background...");
			String str = "";
			try {
				str =  WebServiceHelper.this.get(params[0]);
			} catch (Exception e)
			{ e.printStackTrace(); }
			return str;
		}
		
		@Override
        protected void onPostExecute(String result) {
        	System.out.println("In Post-execute..");
        	if (acthandler != null)
        		((ActionHandler) acthandler).onResultAvailable(result);
        	if (svchandler != null)
        		((ServiceHandler) svchandler).onResultAvailable(result);        	
        }		
    }
}
