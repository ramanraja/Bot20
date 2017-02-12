package com.vz.chatbot.androidbot.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.util.Log;
 
/**
 * @author Raja
 * Global utilities to print debug strings in a generic way
 */
public class G {

    private static String TAG = "Chatbot";

    public static void trace (Object obj)
    {
        Log.d(TAG, obj.toString());
    }
    
    @SuppressWarnings("rawtypes")
	public static void dump (HashMap hmap)
    {
    	Log.d(TAG, "Hashmap size: " +hmap.size());
        Iterator iter = hmap.entrySet().iterator();
        while (iter.hasNext()){
        	Map.Entry entry = (Map.Entry)iter.next();
        	Log.d(TAG,entry.getKey() +"->" +entry.getValue());
    	}
    }    
}
