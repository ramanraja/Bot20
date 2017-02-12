package com.vz.chatbot.androidbot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
 
/***
 * @author Raja
 */
public class State implements Serializable {
    public String state;
    public String text;
    public String action;
    public String hint;
    
    public State(String initialState)
    {
    	this(initialState, null,null,null);
    }
    
    public State(String initialState, String text, String action, String hint)
    {
    	this.state = initialState;
    	this.text = text;
    	this.action = action;
    	this.hint = hint;
    }

    @Override
    public String toString()
    {
        return ("[" +state +"/" +text +"/" +action +"/" +hint +"]");
    }
}
