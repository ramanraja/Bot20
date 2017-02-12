package com.vz.chatbot.interfaces;

/**
    This is the human user interface. It may encapsulate one or more of the following:
    - a speech to text engine 
    - a text to speech engine
    - a text based UI (Eg: chat window/web application)
    - a GUI (Eg: a mobile GUI with buttons and menus)
    - hardware interfaces (Eg: LEDs, push buttons, alert sounds)
*/

public interface UI {

    public void setController (BotController controller); // The UI will communicate through the BotController
    public void prompt ();   // indicate to the user that the bot is reday and listening...
    public void onSpeechAvailable (String speechTranscript);  // inform the BotController that a chunk of speech/text has been received
    public void speak (String textInput);  // speak or type a response to the user
    public void displayTranscript (String transcript);  // display a textual transcript of user's speech after conversion
    public void displayHint (String hint);    // display a helpful hint to the user
    public void onInitError(String errorMsg);   // bot creation failed (fatal error)
    public void onSpeechError(String errorMsg);  // the current transaction failed
}
