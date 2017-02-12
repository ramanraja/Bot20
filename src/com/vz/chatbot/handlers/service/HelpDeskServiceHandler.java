package com.vz.chatbot.handlers.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONObject;

import com.vz.chatbot.androidbot.helpers.G;
import com.vz.chatbot.handlers.service.PersonalServiceHandler.Services;
import com.vz.chatbot.interfaces.Bot;
import com.vz.chatbot.interfaces.ServiceHandler;

public class HelpDeskServiceHandler implements ServiceHandler {

	private static String UNKNOWN_ENTITY = " unknown ";
	private Bot bot;
	
	static enum Services {
		ticketnumber1,ticketnumber2,ticketnumber3, 
		eta1,eta2,eta3
	}
	
	@Override
	public JSONObject handle(JSONObject jobject) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String handle(String serviceName) {
		Services serviceNumber = Services.valueOf(serviceName.toLowerCase());
		
		String returnVal = UNKNOWN_ENTITY;
		switch (serviceNumber) {
		case ticketnumber1:
			returnVal = "23 68";
			break;			
		case ticketnumber2: 
			returnVal = "29 45";
			break;
		case ticketnumber3:
			returnVal = "26 21";
			break;	
		case eta1:
			returnVal = "48";
			break;	
		case eta2:
			returnVal = "24";
			break;				
		case eta3:
			returnVal = "12";
			break;				
		default :
			throw new IllegalArgumentException("Handler not yet implemented: " +serviceName);
		}
		return (returnVal);
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