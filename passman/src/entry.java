/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package passman;
import java.util.*;

/**
 * @author ironcrown.crl
 */
public class entry 
{
    /***************************************************************************
     * private entry class variables
     **************************************************************************/
    
    //Tags associated with the entry. Allow for searches based on category.
    private LinkedList<String> tags = new LinkedList<String>();
    
    //The size of the entry.
    private int n; 
    
    //The title of the entry.
    private String title;
    
    //The entry itself. Stored as a c_string to avoid caching at runtime.
    private char[] entry = new char[n];
    
    /***************************************************************************
     *  constructors
     **************************************************************************/
    
    entry(){}   //Default constructor.
    
    entry(char[] newEntry, String aTitle)  //Construct with existing entry.
    {
        entry = newEntry;
        title = aTitle;
        n = newEntry.length;
    }
            
    /***************************************************************************
     *  public functions
     **************************************************************************/
    
    public LinkedList<String> getTags()
    {
        return tags;
    }
    
    public char[] getEntry()
    {
        return entry;
    }
    
    public String getTitle()
    {
        return title;
    }
    
    public void displayEntry()
    {
        passmanCLI ostream = new passmanCLI();  //Define display object.
        
		//Print title.
        ostream.printToCLI("title: " +title +"\n");
		
		//Print entry.
        ostream.printToCLI("entry: ");
        for(int i = 0; i < n; i++)
        {
			ostream.printToCLI(entry[i]);   //Invoke display object for each value of entry.
        }
		ostream.printToCLI("\n");
		
		//Print tags.
		ostream.printToCLI("tags: ");
		for(int i = 0; i < tags.size(); i++)
		{
			ostream.printToCLI(tags.get(i) +" ");
		}
		ostream.printToCLI("\n");
        
    }
    
    public int getSize()
    {
        return n;
    }
    
    public void setEntry(char charArray[], String aTitle)
    {
        n = charArray.length;   //Get size of new entry.
        entry = new char[n];    //Resize current entry.
        title = aTitle;
        entry = charArray;
    }
	
	public void addTag(String newTag)
	{
		tags.add(newTag);
	}
	
	public boolean removeTag(String badTag)
	{
		boolean flag = tags.remove(badTag);
		return flag;
	}
    
    public int searchTags(String str)
    {
        int index = 0;
        
        index = tags.indexOf(str);
        
        if(index == -1)
        {
            return -1;
        }
        else
        {
            return index;
        }
    }
}
