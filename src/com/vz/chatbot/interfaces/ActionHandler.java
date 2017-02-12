package com.vz.chatbot.interfaces;
import org.json.JSONObject;

public interface ActionHandler  {
	
	public JSONObject handle (JSONObject jobject) throws Exception;
	public String handle (String actionRequest) throws Exception;
	public void register (Bot bot);
	public void onResultAvailable (Object resultObj);
}