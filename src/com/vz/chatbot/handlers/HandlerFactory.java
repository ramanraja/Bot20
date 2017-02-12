package com.vz.chatbot.handlers;

import com.vz.chatbot.androidbot.helpers.G;
import com.vz.chatbot.handlers.action.*;
import com.vz.chatbot.handlers.service.*;
import com.vz.chatbot.interfaces.ActionHandler;
import com.vz.chatbot.interfaces.ServiceHandler;

public class HandlerFactory {
 
	static enum botTypes {
		personal_assistant, smart_office, helpdesk_agent
	};
	
    public static ServiceHandler getServiceHandler (String botType) {
    	
    	botTypes type = botTypes.valueOf(botType.toLowerCase());
    	ServiceHandler handler = new DefaultServiceHandler();
    	switch (type) {
    		case personal_assistant :
    			handler = new PersonalServiceHandler();
    			break;
    		case smart_office :
    			handler = new ConferenceServiceHandler();
    			break;
    		case helpdesk_agent :
    			handler = new HelpDeskServiceHandler();
    			break;
    		default:
    			G.trace("Error: Unknown botType"); // will throw an IllegalArgumentException
    			break;	
    	}
    	return handler;
    }
    
    public static ActionHandler getActionHandler (String botType) {

    	botTypes type = botTypes.valueOf(botType.toLowerCase());
    	ActionHandler handler = new DefaultActionHandler();
    	switch (type) {
    		case personal_assistant :
    			handler = new DefaultActionHandler(); // TODO: if you add any actions, change this!
    			break;
    		case smart_office :
    			handler = new ConferenceActionHandler();
    			break;
    		case helpdesk_agent :
    			handler = new HelpDeskActionHandler();
    			break;
    		default:
    			G.trace("Error: Unknown botType"); // will throw an IllegalArgumentException
    			break;	
    	}
    	return handler;
    }
}    