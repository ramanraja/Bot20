package com.vz.chatbot.androidbot.helpers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * @author Raja
 * Utility class to read a CSV file containing Strings,and return it as a list of lists.
 * Each row is a list, which in turn contains the cells as a list of Strings.
*/
public class CSVReader {

    private static final char DEFAULT_SEPARATOR = ',';
    private static final char DEFAULT_QUOTE = '"';
    private char separator;
    private char quote;
    
    public ArrayList<ArrayList<String>> readCSV (String csvFileName)  throws Exception 
    {
        ArrayList<ArrayList<String>> table = new ArrayList<ArrayList<String>>();
        Scanner scanner = new Scanner(new File(csvFileName));
        while (scanner.hasNext()) {
            ArrayList<String> row = parseLine(scanner.nextLine().trim());
            if (row != null && row.size() > 0)  // skip empty lines
                table.add(row);
        }
        scanner.close();     
        return (table);   
    }
    
    public CSVReader()
    {
        this.separator = DEFAULT_SEPARATOR;
        this.quote = DEFAULT_QUOTE;        
    }

    public CSVReader(char separatorChar)
    {
        this.separator = separatorChar;
        this.quote = DEFAULT_QUOTE;    
    }
    
    public CSVReader(char separatorChar, char quoteChar)
    {
        this.separator = separatorChar;
        this.quote = quoteChar;
    }
        
    protected ArrayList<String> parseLine(String line) throws Exception
    {
    	if (line == null || line.isEmpty()) 
    		return (null);
    	
    	ArrayList<String> result = new ArrayList<String>();
    	
    	StringBuffer buffer = new StringBuffer();
        char[] chars = line.toCharArray();
        
        int i=0;
        while (i<line.length()) 
        {  
        	if (chars[i] == quote)
        	{	
        	     flush(buffer, result);
        		i++;  // skip the opening quote 
        		while(chars[i] != quote)
        		{
        			buffer.append(chars[i]);
        			i++;
        			if(i >= line.length())
        				throw new IllegalArgumentException("Reached EOL before quote was closed");
        		}
        		i++; // skip the closing quote
        		flush(buffer, result);
        	}
        	else if (chars[i] == separator)
        	{
        		i++; // skip the separator
        		flush(buffer, result);
        	}
        	else
        	{
    			buffer.append(chars[i]);
    			i++;
        	}
       }
	flush(buffer, result);       
    	return result;
    }

    private void flush (StringBuffer buffer, ArrayList<String> list)
    {
        String str = buffer.toString().trim();
        if(str.length() > 0)
            list.add(str);
        buffer.setLength(0);  // clear it    
    }
    
    public void dump(ArrayList<ArrayList<String>> arrlist)
    {
        System.out.println("Size of the ArrayList: " +arrlist.size());
        for (ArrayList<String> innerlist : arrlist) {
        	 System.out.print(innerlist.size());
           System.out.println(innerlist);
        }
    }

}