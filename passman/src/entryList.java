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
public class entryList 
{
    /**
     * private entryList class variables
     */
    
    //A list of type entry.
    private ArrayList<entry> entryList = new ArrayList<entry>();
    
    //A count of the number of items.
    private int itemCount;
    
    /**
     *  constructors
     */
    
    entryList(){}   //Default constructor.
    
    /**
     *  public functions
     */
    
    public int searchByTags(String tag)
    {
        int cell = -1;
        
        for(int i = 0; i < itemCount; i++)
        {
            if(entryList.get(i).searchTags(tag) != -1)
            {
                cell = i;
                break;
            }
        }
        
        return cell;
    }//End searchByTags.
    
    //Search by an entry of type entry.
    public int searchByEntry(entry anEntry)
    {
        int cell = -1;
        
        for(int i = 0; i < itemCount; i++)
        {
            if(entryList.get(i) == anEntry)
            {
                cell = i;
                break;
            }
        }
        
        return cell;
    }//End searchByEntry
    
    //Search by an entry's title.
    public int searchByTitle(String title)
    {
        int cell = -1; int i = 0;
        
        for(i = 0; i < itemCount; i++)
        {
            if((get(i).getTitle()).equals(title))
            {
                cell = i;
                break;
            }
        }
        
        return cell;
    }//End searchByTitle
    
    //Add data by primitive types to the list.
    public void addEntry(char[] aCharArrayEntry, String aTitle)
    {
        entry newEntry = new entry(aCharArrayEntry,aTitle);
        entryList.add(newEntry);
        itemCount++;
    }//End addEntry.
    
    //Add data by type entry to the list.
    public void addEntry(entry newEntry)
    {
        entryList.add(newEntry);
        itemCount++;
    }//End addEntry.
    
    //Returns true if removal succeeds. Remove data by title.
    public boolean removeEntry(String title)
    {
        boolean successFlag = false;
        int location = 0;
        
        //Search for title. If exists, remove, else return false.
        location = searchByTitle(title);
        
        if(location != -1)
        {
            entry entryToRemove = get(location);
            entryList.remove(entryToRemove);
            itemCount--; 
        }
        
        return successFlag;
    }//End removeEntry.
    
        //Returns true if removal succeeds. Remove data by type entry.
    public boolean removeEntry(entry anEntry)
    {
        boolean successFlag = true;
        int location = 0;
        
        //Search for anEntry. If exists, remove, else return false.
        location = searchByEntry(anEntry);

        if(location != -1)
        {
            entryList.remove(anEntry);
            itemCount--; 
        }
		
        return successFlag;
    }//End removeEntry.
    
    public int getSize()
    {
        return itemCount;
    }//End getSize.
    
    public entry get(int i)
    {
        return entryList.get(i);
    }//End get.
	
	public void displayListTitles()
	{
		passmanCLI ostream = new passmanCLI();
		
		for(int i = 0; i < itemCount; i++)
		{
			String tempTitle = get(i).getTitle();
			ostream.printToCLI(i +". " +tempTitle +"\n");
		}
	}
	
	public void displayList()
	{
		passmanCLI ostream = new passmanCLI();
		//Add method to alphabetize?
		
		for(int i = 0; i < itemCount; i++)
		{
			ostream.printToCLI(i +".\n");
			entryList.get(i).displayEntry();
			ostream.printToCLI("\n");
		}
	}
}
