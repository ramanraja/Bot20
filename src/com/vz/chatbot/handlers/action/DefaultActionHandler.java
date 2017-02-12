package com.vz.chatbot.handlers.action;

import org.json.JSONObject;

import com.vz.chatbot.androidbot.helpers.G;
import com.vz.chatbot.interfaces.ActionHandler;
import com.vz.chatbot.interfaces.Bot;

public class DefaultActionHandler implements ActionHandler {

	private Bot bot;

	@Override
	public JSONObject handle(JSONObject jobject) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String handle(String actionRequest) {
		G.trace("Default action for: " +actionRequest);
		return null; // don't send any dummy strings to UI
	}

	@Override
	public void onResultAvailable(Object resultObj) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void register(Bot bot) {
		// TODO Auto-generated method stub
		this.bot = bot;
	}
	
}