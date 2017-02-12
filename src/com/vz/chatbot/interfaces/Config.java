package com.vz.chatbot.interfaces;

/**
    This is a Centralized configuration manager.
*/
public interface Config {

    public void initConfig(); 	// read config settings from a file/ DB /content provider
    public void set (String param, String value);
    public String get (String param);
}