package com.vz.chatbot.interfaces;

/**
    The bot is the main engine powering a chat. It takes the user input as a text
    string and does all processing. It communicates to the UI through a BotController.
*/
public interface Bot {
    
    public void initBot(String botName);
    
    public void setServiceHandler (ServiceHandler handler);
    
    public void setActionHandler (ActionHandler handler);
    
    public void onResultAvailable(String result);
    
    // Register yourself with the BotCotroller
    public void register (BotController controller);
    
    // This is the one-stop processing that a Bot performs: it takes a
    // text string as input and does all further processing.
    public void processSpeech (String speechTranscript);
}