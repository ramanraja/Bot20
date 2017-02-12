package com.vz.chatbot.handlers.action;

import org.json.JSONObject;

import com.vz.chatbot.androidbot.helpers.G;
import com.vz.chatbot.handlers.helpers.WebServiceHelper;
import com.vz.chatbot.interfaces.ActionHandler;
import com.vz.chatbot.interfaces.Bot;

public class ConferenceActionHandler implements ActionHandler {

	Bot bot;
	JSONObject jObject;
	WebServiceHelper wsHelper;
	
	public ConferenceActionHandler() {
		jObject = new JSONObject();
		wsHelper = new WebServiceHelper();
		wsHelper.register(this);
	}
	
	@Override
	public JSONObject handle(JSONObject jobject) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String handle(String actionRequest) {
		G.trace("Taking action for: " +actionRequest);
		if(actionRequest.equals("go")) {
			G.trace(jObject);
			wsHelper.doGet("http://ip.jsontest.com/");
		}
		else {
			String[] splitstr = actionRequest.split(":");
			try { jObject.put(splitstr[0].trim(), splitstr[1].trim()); } catch(Exception e){}
		}
		return null; // don't send any dummy strings to UI; it will speak it !
	}

	@Override
	public void onResultAvailable(Object resultObj) {
		G.trace("-------------Conference Handler: Result available-----------");
		G.trace(resultObj);
		bot.onResultAvailable((String) resultObj);
	}

	@Override
	public void register(Bot bot) {
		this.bot = bot;
	}

}