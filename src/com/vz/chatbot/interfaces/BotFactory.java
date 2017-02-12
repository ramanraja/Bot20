package com.vz.chatbot.interfaces;

/**
    BotFactory creates bot objects and (possibly) holds them in memory cache. 
    It can return the reference to any bot, specified by its name.
*/

public interface BotFactory {

    public boolean initBots(BotController controller) throws Exception;  // create or load all the bots
    public Bot getBot(String botName) throws Exception;  // return a specific bot
}