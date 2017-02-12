package com.vz.chatbot.handlers.service;
import org.json.JSONObject;

import com.vz.chatbot.androidbot.helpers.G;
import com.vz.chatbot.interfaces.Bot;
import com.vz.chatbot.interfaces.ServiceHandler;

public class ConferenceServiceHandler implements ServiceHandler {
	
	private static String UNKNOWN_ENTITY = " unknown ";
	private Bot bot;
	@Override
	public JSONObject handle(JSONObject jobject) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String handle(String serviceRequest) {
		G.trace("Conferenec service for: " +serviceRequest);
		return UNKNOWN_ENTITY;
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
