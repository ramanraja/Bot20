package com.vz.chatbot.androidbot;

import java.util.HashMap;

import com.vz.chatbot.androidbot.helpers.G;
import com.vz.chatbot.interfaces.Bot;
import com.vz.chatbot.interfaces.BotController;
import com.vz.chatbot.interfaces.BotFactory;
import com.vz.chatbot.interfaces.UI;

public class AndroidBotController implements BotController {

    private UI ui;
    private Bot currentBot;
    BotFactory factory;
    AndroidConfig config;
    private HashMap<String, Bot> botRegistry;

    public void start () throws Exception {
    	this.config = new AndroidConfig();
        this.botRegistry = new HashMap<String, Bot>();
        this.factory = new AndroidBotFactory();
        
        // create/load all the available bots
        boolean creationResult = factory.initBots(this);
        if (creationResult) {
        	String initialBotName = config.get("INITIAL_BOT");
        	this.currentBot = factory.getBot(initialBotName);
        }
        else
        	ui.onInitError("Could not initialize one or more bots");
    }   
    
	@Override
	public void setUI(UI activityObj) {
		this.ui = activityObj;
	}   
    
    public void registerBot (String botType, Bot botObj) {
    	G.trace("Registering bot...");
    	G.trace(botObj);
    	botRegistry.put(botType, botObj);
    }
      
    public void onSpeechCue () {
    	G.trace("Controller: onSpeechCue");
        ui.prompt();
    }
 
    public void onSpeechAvailable (String speechText) {
    	G.trace("Controller: onSpeechAvailable");
        currentBot.processSpeech (speechText);
    }  

    public void loadBot (String botType) {
		AndroidBot bot = (AndroidBot) botRegistry.get(botType);
		if (bot==null)
			throw new RuntimeException("The bot is NULL !");
		this.currentBot = bot;
		G.trace("Launching bot: " +bot.botName);
    }
    
    public void onSpeechTranscriptAvailable (String transcriptText) {
    	G.trace("Controller: onSpeechTranscriptAvailable");
        ui.displayTranscript (transcriptText);
    }
    
    public void onResponseTextAvailable (String responseText) {
    	G.trace("Controller: onResponseTextAvailable");
    	G.trace(responseText);
        ui.speak (responseText);
    }
    
    public void onActionTextAvailable (String actionText) {
    	G.trace("Controller: onActionTextAvailable");
    	G.trace(actionText);
        ui.speak (actionText);
    }

    public void onHintAvailable (String hintText) {
        ui.displayHint(hintText);
    }      
    
    // called when the current bot has finished processing the input
    public void onProcessingCompleted () {  // TODO: cleanup
    	G.trace("Controller: onProcessingCompleted");
		//G.trace("(Boot strapping - not enabled)"); // TODO: enable this
		//ui.prompt();
		//enable ui button
    }
}

