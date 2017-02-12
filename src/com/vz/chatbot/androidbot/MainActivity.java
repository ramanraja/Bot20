package com.vz.chatbot.androidbot;

/**
 * @author Raja
 * The main entry point for Android Bots.
 */

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.Locale;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.vz.chatbot.androidbot.helpers.G;
import com.vz.chatbot.interfaces.*;
import com.vz.chatbot.handlers.helpers.*;

//Required permissions:
//<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
//<uses-permission android:name="android.permission.INTERNET" />

public class MainActivity extends Activity implements 
	UI, OnClickListener, TextToSpeech.OnInitListener {

	private static final int SPEECH_REQUEST = 100;
	boolean parrotMode = false;  // TODO: toggle from menu
	private TextView label1;
	private Button button1;
	private TextToSpeech tts;
	BotController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        label1 = (TextView)findViewById (R.id.textView1);
        button1 = (Button)findViewById (R.id.button1);
        label1.setText("You said:");
        button1.setText("Try saying 'Hello'");
        button1.setOnClickListener(this);       
        
        this.tts = new TextToSpeech(this, this);
        tts.setPitch((float) 1.2);     
        
        this.controller = new AndroidBotController();
        controller.setUI (this);
        try {
        	controller.start();
        } catch (Exception e)
        {
        	G.trace("----Controller failed to start !----");
        	e.printStackTrace();
        	button1.setEnabled(false);
        }
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
        	//G.trace("----Menu selected----");  // TODO
        	parrotMode = !parrotMode;
        	G.trace("Parrot Mode: "+parrotMode);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Text to Speech method
	@Override
	public void onInit(int status) {
	   G.trace("TTS OnInit: Status= ["+status+"]");
	   if (status == TextToSpeech.SUCCESS) {
	     G.trace("TTS engine initialized");
	     tts.setLanguage(Locale.UK);		
	   }
	   else
		 G.trace("TTS engine could not be initialized");
	}

	@Override
	public void onClick(View v) {
		G.trace("Button clicked");
		controller.onSpeechCue();
	}

	@Override
	public void setController(BotController controller) {
		// Do NOTHING; the controller is already set in this implementation
	}

	@Override
	public void prompt() {
		G.trace("UI: prompt");
	    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
	    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
	            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
	    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
	    intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say Hello");
	    try {
	        startActivityForResult(intent, SPEECH_REQUEST);
	    } catch (ActivityNotFoundException a) {
	        Toast.makeText(getApplicationContext(),
	            "Sorry, this device cannot speak", Toast.LENGTH_SHORT).show();
	    } 
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		G.trace("UI: onActivityResult");
	    super.onActivityResult(requestCode, resultCode, data);
	    switch (requestCode) 
	    {
	    	case SPEECH_REQUEST: 
	    		if (resultCode == RESULT_OK && null != data) {
	    			ArrayList<String> result = data.getStringArrayListExtra
	    					(RecognizerIntent.EXTRA_RESULTS);
	    			this.onSpeechAvailable(result.get(0));
	    		}
	    		else {
	    			label1.setText("Speech Error");
	    			G.trace("ERROR: Could not capture speech input");
	    		}
	    	break;
	    }
	}
	
	public void onInitError(String errorMsg) {
		label1.setText("Fatal Error: "+errorMsg);
		G.trace("Initialization error: "+errorMsg);
		button1.setEnabled(false);
	}
	
	public void onSpeechError(String errorMsg) {
		label1.setText("Error: "+errorMsg);
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
		tts.speak(textInput, TextToSpeech.QUEUE_ADD, null);  // TODO: flush or add ?
		//tts.speak(textInput, TextToSpeech.QUEUE_FLUSH, null);
	}

	@Override
	public void displayTranscript(String transcript) {
		label1.setText(transcript); // TODO: HTML formatted
	}

	@Override
	public void displayHint(String hint) {
		button1.setText(Html.fromHtml(hint));
	}
	
	@Override
	public void onDestroy()
	{
		G.trace("onDestroy...");
	    if (tts != null) {
	    	tts.stop();
	        tts.shutdown();
	    }
	    // TODO: close database connections etc
	    // dbHelper.close();
	    super.onDestroy();
	}		
	
	// For testing only (when voice input is not available)
	public void quickTest() throws Exception
	{
		String[] testStrings = {
			"Good morning !",  "Hello, there !", 
			"Please tell me the current time", 
			"What is my meeting schedule for the day ?", 
			"What day is today ?", "Yes, please",  
			"Thanks a ton", "Here is some gibberish" };
		for (String str : testStrings) 
			controller.onSpeechAvailable(str);
		G.trace("............BYE !........");
	}
}
