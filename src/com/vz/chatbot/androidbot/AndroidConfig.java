package com.vz.chatbot.androidbot;

import java.util.HashMap;
import com.vz.chatbot.interfaces.Config;

/**
    This is a Centralized configuration manager.
    This simple implementation keeps config items in a hash map.
    Subclass this to implemented more sophisticated DB based configuration, and/or
    invoke external configuration services etc.
*/
public class AndroidConfig implements Config {

    private static HashMap<String, String> configObject = new HashMap<String, String>();
    
    public AndroidConfig () {
    	initConfig(); // TODO: remove this, and call initConfig from menu
    	//load hashmap from a csv file
    }
    
    public void initConfig() {
    	// TODO: read config settings from a file/content provider/ DB
    	set("INITIAL_BOT", "bot1");
    	set("BOTS_HOME", "MyBots");
    	set("FORCE_REBUILD", "true");
    }
    
    // TODO: user interface to set config parameters
    public void set (String param, String value) {
        configObject.put(param, value);
    }
    
    public String get (String param) {
        String value = configObject.get(param);
        if (value==null)
            throw new IllegalArgumentException("Config param " +param + " is missing !");
        return value;
    }     
}