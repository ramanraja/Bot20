package com.vz.chatbot.handlers.service;

import org.json.JSONObject;

import com.vz.chatbot.interfaces.Bot;
import com.vz.chatbot.interfaces.ServiceHandler;

public class DBServiceHandler implements ServiceHandler{

	private Bot bot;

	@Override
	public JSONObject handle(JSONObject jobject) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String handle(String serviceRequest) {
		// TODO Auto-generated method stub
		return null;
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