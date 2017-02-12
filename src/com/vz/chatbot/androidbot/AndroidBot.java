package com.vz.chatbot.androidbot;

import java.io.Serializable;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.vz.chatbot.androidbot.helpers.G;
import com.vz.chatbot.interfaces.ActionHandler;
import com.vz.chatbot.interfaces.Bot;
import com.vz.chatbot.interfaces.BotController;
import com.vz.chatbot.interfaces.ServiceHandler;

public class AndroidBot implements Bot, Serializable {

	private static final long serialVersionUID = 1L;
	private static String PARAM_SEPARATOR = "@";
	private static String VARIABLE_SEPARATOR = "#";
	private static String FUNCTION_SEPARATOR = "%";
	private static String UNKNOWN_ENTITY = " unknown ";
	private static String DEFAULT_KEYWORD = " none ";
	private static String keyWord = DEFAULT_KEYWORD;	
	private static String PROMPT = "prompt";
	private static String LAUNCH = "launch";
	private static String PAUSE = "pause";
	private static int NO_MATCH_FOUND = -1;  // must be negative (used as an array index)
	
	//transient private JSONObject jsonParam;
	transient public String botID;
	transient public String botName;
	transient public String botType;
	transient public String avatarName;
	
	transient private BotController controller;
	transient private ServiceHandler serviceHandler;
	transient private ActionHandler actionHandler;
	transient private Random rand;
	
	// Note: canonical terms and dictionary phrases are matched by positional index only ***
	ArrayList<Pattern> domainDictionary; // order is important
	ArrayList<String> canonicalTerms; // order is important
	HashMap<String, String> personalInfo;
	HashMap<String, String> responseText;
	HashMap<String, String> actionMap;
	HashMap<String, String> hintMap;
	FSM fsm;
	
    @Override
    public void initBot (String botName){
    	this.rand = new Random();
        //this.jsonParam = new JSONObject();
        this.botName = botName;
        this.botType = personalInfo.get("bottype");
        this.botID = personalInfo.get("botid");
        this.avatarName = personalInfo.get("botavatar");  
    }
	
	public String getName() {
		return botName;
	}
	
	public String getType() {
		return botType;
	}
	
    @Override
	public void setServiceHandler(ServiceHandler handler) {
		this.serviceHandler = handler;
		serviceHandler.register(this);
	}

	@Override
	public void setActionHandler(ActionHandler handler) {
		this.actionHandler = handler;
		actionHandler.register(this);
	}
	
    @Override
    public void register (BotController controller) {
    	this.controller = controller;
    	controller.registerBot(this.botType, this);
    }

    @Override
    public void processSpeech (String rawTranscript) {
    	G.trace("Bot: processSpeech");
    	G.trace(rawTranscript);
    	String speechStr = preProcess (rawTranscript);
        int index = findMatch (speechStr);
        String speechTranscipt = prepareSpeechTranscript(speechStr, index);  
        controller.onSpeechTranscriptAvailable(speechTranscipt);
        
        boolean transitResult = makeTransition (index);
        if (!transitResult)  {// unintelligible speech
        	 String invalidResponse = fsm.getUnintelligibleSpeechResponse().text;
        	 try {
        		 invalidResponse = prepareResponseText(invalidResponse);
        	 } catch (Exception e) 
        	 {e.printStackTrace();}
        	controller.onResponseTextAvailable(invalidResponse);
        }
        else {	
	        // Now the new state details are available inside FSM
	        try {
		        String responseString = prepareResponseText();  
		        controller.onResponseTextAvailable(responseString);
		        String actionText = takeAction(); 
		        if (actionText != null)
		        	controller.onActionTextAvailable(actionText);
	        } 
	        catch (Exception e)
	        { e.printStackTrace(); }
	        String hint = prepareHint();  
	        controller.onHintAvailable(hint);    
        }
    }
    
	protected String preProcess (String input)
	{
		return (input.replaceAll("\\p{Punct}", " ").toLowerCase().trim());
	}
    
	// canonical terms and dictionary phrase list are matched by positional index only ***
	private int findMatch(String input)
	{
		Matcher matcher;
		for (int i=0; i<domainDictionary.size(); i++)
		{
			matcher = domainDictionary.get(i).matcher(input);
			if (matcher.find()) return (i);
		}
		return (NO_MATCH_FOUND);
	}
	
	private String prepareSpeechTranscript(String rawTranscript, int canonicalIndex) {
		String formattedTranscript = rawTranscript;
		if (canonicalIndex != NO_MATCH_FOUND) {
			// TODO: add HTML <b> tags depending on the index(keyword)
		}
		return formattedTranscript;
	}
    
    private boolean makeTransition (int index) {
		String message;
          boolean result = false;
		StringBuffer debugbuf = new StringBuffer(fsm.getState().state);			
		
		if (index==NO_MATCH_FOUND) {
		     result = false; 
			//do not make the transition; keep the current state
			message = "(no match)";
			this.keyWord = DEFAULT_KEYWORD;
			debugbuf.append("->");
			debugbuf.append(message);				
		}  else {
		    result = true;
			message = canonicalTerms.get(index);
			debugbuf.append("->");
			debugbuf.append(message);
			this.keyWord = message;
			
			// Make the transition
			fsm.transition(message); // this stores the new state inside FSM
			
			debugbuf.append("->");
			debugbuf.append(fsm.getState().state);
		}
		G.trace(debugbuf.toString());
		return result;
    }
    
    private String prepareResponseText () throws Exception {
    	return (prepareResponseText(null));
    }
    
	// The input is a comma separated list of numbers; each number is the ID of a response text.
	// All the phrases/sentences in a list are equivalent; randomly choose one of them
    private String prepareResponseText (String overridingResponse) throws Exception {
    	String textChoices = " Error"; 
    	if (overridingResponse != null)
    		textChoices = overridingResponse;
    	else
    		textChoices = fsm.getState().text;
    	// After transition, FSM has the new state

		//G.trace("Text choices: " +textChoices);
		String[] alternatives = textChoices.split("\\s*,\\s*");
		int choice = 0;
		if (alternatives.length > 1)  // choose one of the responses 
			choice = rand.nextInt(alternatives.length);
		// response string ID to the actual string:
		String response = responseText.get(alternatives[choice]); 
		
		// You now have a single response string. Process the # and @ and % tags in it:
		if (response.indexOf(PARAM_SEPARATOR) >= 0 || response.indexOf(VARIABLE_SEPARATOR) >= 0 || 
				response.indexOf(FUNCTION_SEPARATOR) >= 0)
		{
			StringBuffer buffer = new StringBuffer();
			String[] fragments = response.split("\\s*" +PARAM_SEPARATOR +"\\s*");
			for (String frag : fragments) 
			{
				int varindex = frag.indexOf(VARIABLE_SEPARATOR);
				int funindex = frag.indexOf(FUNCTION_SEPARATOR);
				if (varindex >= 0) {
					buffer.append(" ");
					buffer.append(getInternal(frag.substring(varindex+1).trim()));
				}  else 
					if (funindex >= 0) {
						buffer.append(" ");
						buffer.append(getExternal(frag.substring(funindex+1).trim()));
					} else {
						buffer.append(" ");
						buffer.append(frag);
					}
			}
			response = buffer.toString();
		}
		G.trace("Bot's response: " +response);
		// send it back to generate text to speech
		return(response);
    }
    
	protected String getInternal (String paramName)  // throws Exception
	{
		String paramValue;
		if (personalInfo.containsKey(paramName)) 
			paramValue = personalInfo.get(paramName);
		else {
			// throw new IllegalArgumentException("Unrecognized parameter: "+paramName);
			paramValue = UNKNOWN_ENTITY;
		}
		G.trace("Param: " + paramName +" Value: " +paramValue);
		return (paramValue);
	}
	
	public String getExternal (String serviceName) throws Exception
	{
		G.trace("Calling service: " +serviceName);
		return serviceHandler.handle(serviceName);
	}

	private String takeAction() throws Exception
	{
		String actionResponse = "";
		String actionList = fsm.getState().action;
		G.trace("Action ID List: " +actionList);  
		String[] actionIDs = actionList.split("\\s*,\\s*");
		
		for (String action : actionIDs) {
			String actionName = actionMap.get(action);
			//* TODO Launch must be the last command, so make it an ordered list *
			if (actionName.contains(LAUNCH)) {
				String[] splitstr = actionName.split(" ");
				// it is of the form 'launch bot_type'
				String botType = splitstr[1].trim();
				controller.loadBot(botType);	
				break;  // do not process any other actions after this !
			} 
			else {
				if (actionName.contains(PROMPT)) {
					G.trace("(Boot strapping - not enabled)"); // TODO: enable this
					//controller.onProcessingCompleted();
				}
				else
					actionResponse = actionHandler.handle(actionName);
			}  
		} // for
		return actionResponse;
	}
	
    private String prepareHint() {
    	String hintID = fsm.getState().hint;
    	return hintMap.get(hintID);
    }
    /*
    private void initJsonParam () {
        this.jsonParam = new JSONObject();
    }
    
    private void addJsonParam (String nameValueString) {
        // parse the string and add quotes
    }    
    
    public JSONObject getJsonParam () {
        return jsonParam;
    } 
    */
    public String toString() {
    	StringBuffer buffer = new StringBuffer("Android Bot: ");
    	buffer.append("\nID = ");
    	buffer.append(botID);
    	buffer.append("\nName = ");
    	buffer.append(botName);
    	buffer.append("\nBot Type = ");
    	buffer.append(botType);
    	buffer.append("\nAvatar = ");
    	buffer.append(avatarName);
    	return buffer.toString();
    }

	public void dump()
	{
		G.trace("------------------------");
		G.trace("Bot name: " +botName);
		G.trace("Bot type: " +botType);
		G.trace("User info :"); G.dump(personalInfo);
		G.trace("Canonical terms :"); G.trace(canonicalTerms);
		G.trace("Response Texts :"); G.dump(responseText);
		G.trace("Action Map :"); G.dump(actionMap);
		G.trace("Hints :"); G.dump(hintMap);
		G.trace("------------------------");
	}

	@Override
	public void onResultAvailable(String result) {
		// TODO Auto-generated method stub
		controller.onResponseTextAvailable(result);
	}
}
