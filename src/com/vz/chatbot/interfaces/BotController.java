package com.vz.chatbot.interfaces;

/**
    BotController is the mediator between the Bot and the UI.
    It invokes the BotFactory to create the bots.
    Then it handles all life cycle callbacks from both the UI events and the Bot's responses.
*/

public interface BotController {
   
	public void setUI (UI ui);
	public void start() throws Exception; // rev up all the bots
    public void registerBot (String botType, Bot bot); // maintain a registry of all the bots
    public void loadBot (String botType); // switch between Avatars
    public void onSpeechCue ();  // receive cue from the UI that the user wants to speak/type
    public void onSpeechAvailable (String speechTranscipt); // called after speech to text conversion 
    public void onProcessingCompleted (); // called after the bot has finished processing the input
    
    // the following can just be routed to the UI class
    public void onSpeechTranscriptAvailable (String transcriptText);
    public void onResponseTextAvailable (String responseText);
    public void onActionTextAvailable (String actionText);
    public void onHintAvailable (String hintText);
}

