
/**
 * @author Raja
 * The main entry point for Console Bot.
 */

import org.json.simple.JSONObject;
import java.io.Console;
import java.util.ArrayList;
import java.util.Locale;
import com.vz.chatbot.androidbot.helpers.G;
import com.vz.chatbot.androidbot.*;
import com.vz.chatbot.interfaces.*;

public class ConsoleBot  implements UI {

	private static final int SPEECH_REQUEST = 100;
	boolean parrotMode = false;  // TODO: toggle from menu
	BotController controller;

    public static void main(String... args)  throws Exception{
        ConsoleBot bot = new ConsoleBot();
        bot.init();
        bot.go();
        //bot.quickTest();
    }
    
    public void init() {
        this.controller = new AndroidBotController();
        controller.setUI (this);
        try {
        	controller.start();
        } catch (Exception e)
        {
        	G.trace2("----Controller failed to start !----");
        	e.printStackTrace();
        }
    }

    public void go() {
        Console console = System.console();
        G.trace2("Try saying 'Hello' to the bot. Type 'quit' to exit.");
        while(true) {
            prompt();
            String speechTxt = console.readLine();
            if (speechTxt.equals("quit") || speechTxt.equals("exit"))
                break;
            onSpeechAvailable(speechTxt);
        }
    }
    

	@Override
	public void setController(BotController controller) {
		// Do NOTHING; the controller is already set in this implementation
	}

	@Override
	public void prompt() {
        System.out.print("Your input>> ");
	}
	
	public void onInitError(String errorMsg) {
		G.trace2("Initialization error: "+errorMsg);
	}
	
	public void onSpeechError(String errorMsg) {
		G.trace("Speech processing error: "+errorMsg);
	}
	
	@Override
	public void onSpeechAvailable(String speechTranscript) {
		G.trace("UI: onSpeechAvailable");
		if (parrotMode) {
			displayTranscript(speechTranscript);
			speak(speechTranscript);
		}
		else
			controller.onSpeechAvailable(speechTranscript);
	}

	@Override
	public void speak(String textInput) {
		G.trace("UI: speak");
		G.trace2(textInput);
	}

	@Override
	public void displayTranscript(String transcript) {
		G.trace2(transcript);  
	}

	@Override
	public void displayHint(String hint) {
	     G.trace2(" ");
		G.trace2(hint);
		G.trace2(" ");
	}
	
	// For testing only (when voice input is not available)
	public void quickTest() throws Exception
	{
		String[] testStrings = {
			"Good morning !",  "Hello, there !", 
			"Please tell me the current time", 
			"What is my meeting schedule ?", 
			"What day is today ?", "Yes, please",  
			"Thanks a ton", "The sun rises in the east" };
		for (String str : testStrings) 
			controller.onSpeechAvailable(str);
		G.trace("BYE !");
	}
}
