package com.vz.chatbot.handlers.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONObject;

import com.vz.chatbot.interfaces.Bot;
import com.vz.chatbot.interfaces.ServiceHandler;

public class PersonalServiceHandler implements ServiceHandler {

	private static String UNKNOWN_ENTITY = " unknown ";
	static enum Services {
		date, time, reminder, calendar, holidays
	}

	private Bot bot;;
	
	@Override
	public JSONObject handle(JSONObject jobject) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String handle(String serviceName) {
		Services serviceNumber = Services.valueOf(serviceName.toLowerCase());
		
		String returnVal = UNKNOWN_ENTITY;
		SimpleDateFormat sdf;		
		switch (serviceNumber) {
		case date:
			sdf = new SimpleDateFormat("EEEE MMMM yyyy", Locale.US);
			returnVal = sdf.format(new Date());
			break;			
		case time:
			sdf = new SimpleDateFormat("h m a", Locale.US);
			String[] frags = sdf.format(new Date()).split("\\s+");
			returnVal = frags[0] +" hours and " +frags[1] +" minutes, " +frags[2];
			break;
		case reminder:
			returnVal = new String("Just a reminder. You have an O R R at 11 AM today.");
			break;	
		case calendar:
			returnVal = new String("O R R at 11 AM. Team meeting at 3 PM. Chat Bot demo at 9 PM");
			break;	
		case holidays:
			returnVal = new String("February 20. Presidents Day... May 29. Memorial Day");
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