package com.vz.chatbot.androidbot;

import java.io.Serializable;
import java.util.HashMap;

import com.vz.chatbot.androidbot.helpers.G;

/***
 * @author Raja
 * The Finite State Machine keeping the bot's context
 */
public class FSM implements Serializable {

	private static String DEFAULT_STATE = "any";
	private static String GENERIC_MESSAGE = "any";
	protected String initialState = DEFAULT_STATE;
	protected String THANKS_MSG = "1,2,3,4";  
	protected String UNINTELLIGIBLE_SPEECH = "7,8,9,10";
	protected String DEFAULT_MESSAGE = "0";
	protected String INVALID_MESSAGE = "20,21,22,23";
	protected String NO_ACTION = "0";
	protected String DEFAULT_HINT = "0";
	protected String DEFAULT_RESPONSE = "0";

	private State currentState;
	private State invalidMessage;
	private State unitelligibleSpeech;
	private HashMap<String, State> transitionMap;
	
	public FSM()
	{
		this(DEFAULT_STATE);
	}
	
	public FSM(String initialState)
	{
		this.initialState = initialState;
		this.currentState = new State(initialState, DEFAULT_MESSAGE,NO_ACTION,DEFAULT_HINT);
		this.invalidMessage = new State(initialState, INVALID_MESSAGE,NO_ACTION,DEFAULT_HINT);
		this.unitelligibleSpeech = new State(initialState, UNINTELLIGIBLE_SPEECH,NO_ACTION,DEFAULT_HINT);
		this.transitionMap = new HashMap<String, State>();
		G.trace("FSM created with Initial State: " +initialState);
	}
	
	public void setInitialState(String initialState)
	{
		this.initialState = initialState; // save it for future use
		this.currentState = new State (initialState, DEFAULT_RESPONSE, NO_ACTION, DEFAULT_HINT);
		G.trace("FSM Initial State set to: " +initialState);
	}
	
	public State getState()
	{
		return currentState; //.state;
	}
	
	public int getSize()
	{
		return (transitionMap.size());
	}
	
	public void addTransition(String message, String inState, State outState)
	{
		transitionMap.put(message+inState, outState);
	}
	
	public void setTransitionTable(HashMap<String, State> table)
	{
		this.transitionMap = table;
	}
	
	public State peek (String message)
	{
		String key = currentState.state + message;
		if (!transitionMap.containsKey(key)) 
			return (buildInvalidMessage());
		return (transitionMap.get(key));
	}
	/*
	public State transition (String message)
	{
		String key = currentState.state + message;
		G.trace("Looking for key: " +key);
		if (!transitionMap.containsKey(key)) {
			key = currentState.state + GENERIC_MESSAGE;  //"any"
			G.trace("Now Looking for key: " +key);
			if (!transitionMap.containsKey(key))
				return (buildInvalidMessage());
		}
		currentState = transitionMap.get(key); // this is the actual transition
		return (currentState); // Caution: caller must not modify this object !
		// TODO: return a clone? read only object ?
	}	*/
	
	// TODO: it is enough if you return boolean from this method (see Bot)
	public State transition (String message)
	{
		String key = currentState.state + message;
		G.trace("Looking for key: " +key);
		if (!transitionMap.containsKey(key)) {
			key = currentState.state + GENERIC_MESSAGE;  //"any"
			G.trace("Now Looking for key: " +key);
			if (!transitionMap.containsKey(key))
				currentState = buildInvalidMessage(); // this is an error transition
		}
		else
			currentState = transitionMap.get(key); // this is the actual transition
		
		return (currentState); // Caution: caller must not modify this object !
		// TODO: return a clone? read only object ?
	}	
	
	private State buildInvalidMessage()
	{
		// preserve the current state and hint; only set the text to INVALID_MESSAGE
		// return object: (currentState.state, INVALID_MESSAGE, NO_ACTION, currentState.hint);
		this.invalidMessage.state =  currentState.state;
		this.invalidMessage.hint = currentState.hint;
		return (invalidMessage);
	}
	
	public void reset()
	{
		currentState = new State(initialState, "none","none","none");
	}
	
	// Unintelligible speech is never processed by FSM, but this method is here for uniformity
	public State getUnintelligibleSpeechResponse()
	{
		return (this.unitelligibleSpeech);
	}
	
	public void dump()
	{
		G.dump(transitionMap);
	}
}






