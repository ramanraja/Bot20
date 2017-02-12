package com.vz.chatbot.androidbot;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

import android.os.Environment;

import com.vz.chatbot.androidbot.helpers.CSVReader;
import com.vz.chatbot.androidbot.helpers.G;
import com.vz.chatbot.handlers.HandlerFactory;
import com.vz.chatbot.interfaces.Bot;
import com.vz.chatbot.interfaces.BotController;
import com.vz.chatbot.interfaces.BotFactory;

/**
    BotFactory creates Bot objects and holds them in memory cache. 
    It can return the reference to any bot, when called by its name.
    Note: Bots will not be garbage collected as long as the factory is active.
*/
public class AndroidBotFactory implements BotFactory {
	  
	HashMap<String, Bot> bots;
	CSVReader reader;
	String botRoot;
	String separator = File.separator;
	
    // create, deserialize or load all available bots to memory cache
	public boolean initBots (BotController controller) {
		
		this.bots = new HashMap<String, Bot>();
		this.reader = new CSVReader();
		AndroidConfig config = new AndroidConfig();
		boolean forceRebuild = Boolean.valueOf(config.get("FORCE_REBUILD")); 
				
        // load all bots under the bots directory
		File sdcardRoot = Environment.getExternalStorageDirectory();
		this.botRoot = sdcardRoot.toString() +separator +config.get("BOTS_HOME");
		G.trace("Bots home directory: " +botRoot);
		
    	try {
    		File rootdir = new File(botRoot);
	    	File[] botdirs = rootdir.listFiles();
	    	G.trace(botdirs.length +" bots found:");
	    	for (File bd : botdirs)
	    		G.trace(bd.toString());
	    	
	    	for (File bd : botdirs) {
	    		if (!bd.isDirectory())
	    			continue;  // skip any non-directories
	    		String path = bd.toString();
	    		String botName = path.substring(path.lastIndexOf(separator)+1);;
				Bot bot = getBot(botName, forceRebuild); 	
				if (bot==null) 
					throw new RuntimeException ("The Bot "+botName +" is NULL !");
				// give the bot the reference to the controller
				bot.register (controller);
				G.trace("Bot "+botName +" is ready...");
	    	} // for
		} 
		catch (Exception e) {
			G.trace("--------------Failed to initialize bots-------------");
			G.trace("Check: Did you specify the correct bot directory on the SD card ?");
			G.trace("Check: Are all the input files present in the bot directory ?");
			G.trace("Check: Did you set 'permission.WRITE_EXTERNAL_STORAGE' in manifest ?");			
			e.printStackTrace();
			return false;
		} 
        return true;
    }
    
    // return a bot reference from cache/disk/after creation
    public Bot getBot(String botName) throws Exception {
        return getBot(botName, false);
    }
    
    // if forceRebuild, ignore cached version and overwrite with a fresh build
    private Bot getBot(String botName, boolean forceRebuild) throws Exception {
    	
		G.trace("Loading Bot: " +botName);
		// Try to load bot from memory cache
		if (bots.containsKey(botName)) {
			G.trace("Bot already in memory cache: " +botName);
			return bots.get(botName);
		}
		//  Try to load serialized bot from disk
		G.trace("Bot not in memory cache, looking for serialized object");
		AndroidBot bot = null;
		if (! forceRebuild)
			bot = load(botName);
		if (bot != null) 
			G.trace("Loaded bot from serialized object: " +botName );
		else {
			G.trace("Cannot load serialized bot - building it...");
			bot = build(botName);
			G.trace("Finished building bot: " +botName);
			// serialize it to disk
			save (botName, bot);
			G.trace("Serilized the bot: " +botName);
		}
		
		// setup the handlers
		bot.initBot(botName); // this important to setup the botType first
		bot.setServiceHandler(HandlerFactory.getServiceHandler(bot.botType));
		bot.setActionHandler(HandlerFactory.getActionHandler(bot.botType));

		// add it to memory cache
		bots.put(botName, bot);
		return bot;
    }    
    
	public void save (String botName, AndroidBot bot) //throws Exception
	{
		String configDir = this.botRoot +separator;  
		String botFile = configDir +botName +".bot";		
		G.trace("Serializing bot to file: "+ botFile);
		try {
			// TODO: use openFileOutput+MODE_WORLD_READABLE or, better, a FileProvider
		     FileOutputStream fileOut = new FileOutputStream(botFile);
		     ObjectOutputStream out = new ObjectOutputStream(fileOut);
		     out.writeObject(bot);
		     out.close();
		     fileOut.close();
		     G.trace("Serialized successfully: " +botName);
		}
		catch(IOException e) { 
			G.trace("Error serializing bot: " +botName);
			e.printStackTrace();
			//throw(e);
		}
	}
	
	public AndroidBot load(String botName) 
	{
		String configDir = this.botRoot +separator;
		String botFile = configDir +botName +".bot";
		G.trace("Loading serialized bot: " +botFile);
		AndroidBot bot = null;
	    try {
	       FileInputStream fileIn = new FileInputStream(botFile);
	       ObjectInputStream objIn = new ObjectInputStream(fileIn);
	       bot = (AndroidBot) objIn.readObject();
	       objIn.close();
	       fileIn.close();
	       G.trace("Successfully deserialized bot: "+botName);
		}
	    catch(Exception e) { 
		   G.trace("Could not deserialize bot: "+ botName);
		   G.trace(e.getMessage());
		   // This is a normal condition, so don't throw an exception now !
		}
		return bot;
	}
	
	public AndroidBot build(String botName) throws Exception
	{
		String configDir = this.botRoot +separator +botName+separator; 
		G.trace("config Dir: " +configDir);
		
		String botFile = configDir+"bot.csv";
		String userFile = configDir+"user.csv";
		String requestFile = configDir+"request.csv";
		String responseFile = configDir+"response.csv";
		String stateFile = configDir+"state.csv";
		String actionFile = configDir+"action.csv";
		String hintFile = configDir+"hint.csv";

		G.trace("Loading the following config files: ");
		G.trace(botFile);
		G.trace(userFile);
		G.trace(requestFile);
		G.trace(responseFile);
		G.trace(stateFile);
		G.trace(actionFile);
		G.trace(hintFile);
		
		this.reader = new CSVReader();
		AndroidBot bot = new AndroidBot();
		bot.personalInfo = new HashMap<String, String>();
		bot.domainDictionary = new ArrayList<Pattern>();
		bot.canonicalTerms = new ArrayList<String>();		
		bot.responseText = new HashMap<String, String>();
		bot.actionMap = new HashMap<String, String>();
		bot.hintMap = new HashMap<String, String>();
		//bot.serviceMap = new HashMap<String, Integer>(); // TODO: auto-populate this from tags
		bot.fsm = new FSM();

		loadHashMap (botFile, bot.personalInfo, true); // standardize names to lower case
		loadHashMap (userFile, bot.personalInfo);
		loadHashMap (responseFile, bot.responseText);
		loadHashMap (actionFile, bot.actionMap, true);
		loadHashMap (hintFile, bot.hintMap);
		initRequest (requestFile, bot);
		initFSM (stateFile, bot);
		return bot;
	}
	
	private void loadHashMap(String fileName, HashMap mapObj) throws Exception {
		loadHashMap(fileName, mapObj, false);
	}
	
	@SuppressWarnings("unchecked")
	private void loadHashMap(String fileName, HashMap mapObj, boolean lowerCase) throws Exception
	{
		// each line is <param>,<value>
		ArrayList<ArrayList<String>> outerlist;
		try {
			outerlist = reader.readCSV(fileName);
			G.trace (fileName +" (" +outerlist.size() +" lines)");
			G.trace (outerlist.get(0)); // header
			outerlist.remove(0);
			if (lowerCase) {
				for (ArrayList<String> innerlist : outerlist)
					mapObj.put(innerlist.get(0).trim().toLowerCase(),  innerlist.get(1).trim().toLowerCase());
			}
			else {
				for (ArrayList<String> innerlist : outerlist)
					mapObj.put(innerlist.get(0).trim(),  innerlist.get(1).trim());
			}
		}
		catch (Exception e) {throw (e);}  // TODO: handle this
	}
	
	public void initRequest (String requestFile, AndroidBot bot)  throws Exception
	{
		//each line is <canonical term>,<string of comma separated equivalents>
		ArrayList<ArrayList<String>> outerlist;
		try {
			outerlist = reader.readCSV(requestFile);
			G.trace (requestFile +" (" +outerlist.size() +" lines)");
			G.trace (outerlist.get(0)); // header
			outerlist.remove(0);
	        for (ArrayList<String> innerlist : outerlist) {
	        	String canons = innerlist.get(0).toLowerCase().trim();
	        	bot.canonicalTerms.add(canons);
	        	String equivalents = innerlist.get(1).toLowerCase().trim();
	        	bot.domainDictionary.add(parseVariants(equivalents));
	        }
		} 
		catch (Exception e)   // TODO: handle this
		{throw (e);}
	}
	
	private Pattern parseVariants(String input)
	{
		// the input is comma separated list of equivalents for a canonical term
		// split on commas and trim each word 
		String[] alternatives = input.split("\\s*,\\s*");
		// match any of the word in the list of alternatives, at word boundaries
		StringBuilder regexBuilder = new StringBuilder("\\b(");
		for (int i=0; i<alternatives.length; i++) {
			regexBuilder.append(alternatives[i]);
			if (i != alternatives.length-1)
				regexBuilder.append("|"); // A dangling '|' at the end will match *anything*
		}
		regexBuilder.append(")\\b");
		G.trace(regexBuilder.toString()); 
		Pattern pattern = Pattern.compile(regexBuilder.toString());
		return pattern;
	}
	
	public void initFSM (String stateFile, AndroidBot bot) throws Exception
	{
		//each line is <input context, canonical term, output context, responses, actions, hints>
		ArrayList<ArrayList<String>> outerlist;
		try {
			outerlist = reader.readCSV(stateFile);
			G.trace (stateFile +" (" +outerlist.size() +" lines)");
			G.trace (outerlist.get(0)); // header
			outerlist.remove(0);  // remove header
			// the first line, first column could be the initial state
			// the first line is now the zeroth line
        	if (outerlist.get(0).size()==1) {
        		String state = outerlist.get(0).get(0);
        		bot.fsm.setInitialState(state);
        		//G.trace("Initial state: " +state);
        		outerlist.remove(0);
        	}
	        for (ArrayList<String> innerlist : outerlist) {
	        	G.trace(innerlist);
	        	if (innerlist.size() != 6)
	        		G.trace("*** Every row in the state map must have 6 columns ***");
	        	String instate = innerlist.get(0).toLowerCase().trim();
	        	String message = innerlist.get(1).toLowerCase().trim();
	        	State outstate =  new State (innerlist.get(2).toLowerCase().trim(), 
	        						innerlist.get(3).toLowerCase().trim(),
	        						innerlist.get(4).toLowerCase().trim(),
	        						innerlist.get(5).toLowerCase().trim()
	        						);
	        	bot.fsm.addTransition(instate, message, outstate);
	        }
	        bot.fsm.dump();
	        G.trace(" ");
		} 
		catch (Exception e) {throw (e);}  // TODO: handle this
	}
}	
