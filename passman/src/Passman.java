/**
 *  @author		Coleman R. Lombard
 *	@file		Passman.java
 ******************************************************************************* 			
 *			Overview:
 ******************************************************************************* 
 *			This is the main class for Passman, a password management application
 *			which aims to simplify, and further secure the process of managing
 *			personal login information, or other sensitive data. This program stores
 *			user data in an SQLite database (see passmanSQLite.java) on the disk, 
 *			which is encrypted with Rijndael, the current AES Standard 
 *			(see passmanEncrypt.java).
 ******************************************************************************* 
 *			Security:
 ******************************************************************************* 
 *			This program aims to create as little of a fingerprint as possible while
 *			in memory. To this end, sensitive database records (known as entries 
 *			to this application) are accessed only upon selection of the entry 
 *			title, such that only a single entry is ever loaded in memory at a 
 *			given time. 
 * 
 *			EDIT: 12/14/2016
 *			The fingerprint of this application in memory is not as small as I had
 *			intended at the onset. Because of the fact that Java Swing's ScrollPane
 *			objects accept only a JList for display, which accepts only a string
 *			for loading into the JList, sensitive entry data is being stored in
 *			Strings at the point of display. Because of the fact that Java's Strings
 *			are immutable, and Java is a garbage collected language, there's no
 *			way to clear the sensitive data from a String. This is really not a
 *			huge issue unless a malicious user has access to your memory (your system
 *			is heavily compromised), or in the event of some highly unlikely attempt
 *			to physically extract data from RAM (your physical computer is highly
 *			compromised). Regardless, this is not a best practice, and I'm working
 *			on a workaround.
 * 
 *			Upon selection of another event, or closure of the application, the 
 *			array storing the entry is cleared. On disk, the sensitive portion 
 *			of each SQLite record, the entry, is encrypted with Rijndael, the 
 *			current AES Standard. I intend to implement use of Threefish at a
 *			later date.
 * 
 *			It should also be noted here that I am by no means an expert in
 *			cryptography. This application is largely intended as a learning experience,
 *			and as such, it would be wise to layer security protocols when using this
 *			program. I personally run this application in an encrypted partition 
 *			on a secure private machine; defense in depth and all that. I have 
 *			done my best to ensure the most secure environment possible, but 
 *			don't bet your nuclear launch codes on my limited experience.
 * 
 *			Users are should review passmanEncrypt.java for more information on
 *			the program's encryption procedure and standard.
 ******************************************************************************* 
 *			User Functionality:
 ******************************************************************************* 
 *			The impetus behind this program was to simplify and further secure
 *			my personal login information and other sensitive data that I don't
 *			trust myself to remember in its entirety. This program organizes data
 *			to contain three segments, a title, tags, and the entry itself. The 
 *			user is able to enter whatever they like for all fields, but it is highly
 *			recommended that all data should avoid use of the enter key or '\n' characters. 
 *			Tags on entry should be separated by commas. It is recommended but not
 *			required that tags be a single word.
 * 
 *			The search bar at the bottom left of the screen allows for searching
 *			for an entry by any tags it may possess.
  
 *	@date		12/02/2016
 *  @version	Beta v0.02 : KateBait 
 */

package passman;

import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.awt.*;
import javax.swing.*;
import java.io.Console;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.apache.commons.codec.binary.Base64;

public class Passman 
{
    //private static entryList fullList = new entryList();		//Class entryList variable for storing entry data.
	private static passmanSQLite SQL = new passmanSQLite();		//Class database object.
	private static passmanEncrypt crypt = new passmanEncrypt();	//Class encryption object.
	private static passmanCLI cli = new passmanCLI();			//Class GUI object.
	private static Scanner sc = new Scanner(System.in);
	private static Console cons = System.console();
    
    public static void main(String[] args) 
    {
        //Print title and current version.
        System.out.print("passmanGUI Password Management Software\nBeta v0.02 \"KateBait\": 1/4/2017\n\n");
		boolean createdEntryTable = true;
		boolean createdClosedTable = true;
		String loadedPass = "";
		String newUserPass = "";
		String userPass = "";
		String hashedUserPass = "";
		String password = "";
		byte[] salt;
		boolean failureFlag = true;
		
		//Create entryTable if it does not exist.
		try{
			SQL.createEntryTable();
			createdEntryTable = true;
		}
		catch(SQLException e)
		{
			//If reached, entryTable already exists, wasn't created.
			createdEntryTable = false;
		}
		
		//Create closedTable if it does not exist.
		try{
			SQL.createClosedTable();
			createdClosedTable = true;
		}
		catch(SQLException e)
		{
			//If reached, closedTable already exists, wasn't created.
			createdClosedTable = false;
		}
		
		if(createdEntryTable) //Set passphrase if new volume is created.
		{
			System.out.print("New volume created. Specify password for this volume: ");
			//newUserPass = sc.nextLine();
			newUserPass = new String(cons.readPassword());
			try{
				String newUserPassHash = crypt.PBKDF2Hash(newUserPass);
				SQL.insertVolumePassword(newUserPassHash, crypt.getSalt());
			}catch(Exception insertVolumePasswordExcep)
			{
				System.err.println("Error in passmanSQLite insertVolumePassword");
				insertVolumePasswordExcep.printStackTrace();
			}
		}
		
		 //Prompt for unlock if volume exists.
		try{
			//Load pass from database. This is a hash of the pass.
			loadedPass = SQL.loadPass();
			int attemptCount = 0; boolean lockFlag = true;
			salt = Base64.decodeBase64(SQL.loadSalt());
						
			while(attemptCount < 5)
			{
				//Prompt entry.
				System.out.print("Enter volume password: ");
				//userPass = sc.nextLine(); //Get user pass.
				userPass = new String(cons.readPassword()); //Get user pass.
				hashedUserPass = crypt.PBKDF2Hash(userPass, salt); //Hash user pass.

				if(hashedUserPass.equals(loadedPass)) //Real pass is set to user's entry.
				{
					password = userPass;
					lockFlag = false;
					break;
				}
				attemptCount++;
			}
			
			if(lockFlag)
			{
				System.out.println("Too many incorrect attempts. Volume locked.");
				System.exit(0);
			}
			else
			{
				failureFlag = false;
			}
				
		}catch(Exception loadPassExcep) {
			System.err.println("Error in passmanSQLite loadPass");
			loadPassExcep.printStackTrace();
		}
		
		if(failureFlag)
		{
			System.exit(0);
		}
		
		/***********************************************************************
		 * Encryption
		 **********************************************************************/
		crypt.setKey(password);
		
		String plainStr = "secrets"; String cipherText = "";
		char[] plainData = plainStr.toCharArray();
		try{
			cipherText = crypt.encrypt(plainStr);
		}catch(Exception encryptExcept)
		{
			System.err.println("Encryption failed in passmanEncrypt encrypt function.");
			encryptExcept.printStackTrace();
		}
		
		try{
			plainStr = crypt.decrypt(cipherText);
		}catch(Exception decryptExcept)
		{
			System.err.println("Decryption failed in passmanEncrypt decrypt function.");
			decryptExcept.printStackTrace();
		}
		
		/***********************************************************************
		 * Run GUI
		 **********************************************************************/
		passmanGUI testGUI = new passmanGUI();
		testGUI.setSize(600, 600);
		testGUI.setVisible(true);
    }
	
	public static entry encrypt(entry cleartextEntry)
	{
		String entryStr = ""; 
		char[] cleartextChars = cleartextEntry.getEntry();
		for(int i = 0; i < cleartextChars.length; i++)
		{
			entryStr += cleartextChars[i];
		}

		String cipherStr = crypt.encrypt(entryStr);
		char[] cipherChars = new char[cipherStr.length()];
		for(int i = 0; i < cipherStr.length(); i++)
		{
			cipherChars[i] = cipherStr.charAt(i);
		}
		
		cleartextEntry.setEntry(cipherChars, cleartextEntry.getTitle());
		
		return cleartextEntry;
	}
	
	public static entry decrypt(entry cipherEntry)
	{
		//entry cleartextEntry = cipherEntry; 
		String entryStr = ""; 
		char[] cipherChars = cipherEntry.getEntry();
		for(int i = 0; i < cipherChars.length; i++)
		{
			entryStr += cipherChars[i];
		}
		//System.out.println("entryStr size = " +entryStr.length() +"\nentryStr = " +entryStr);
		
		String decipherStr = crypt.decrypt(entryStr);
		
		//System.out.println("decipherStr size = " +decipherStr.length() +"\ndecipherStr = " +decipherStr);
		char[] cleartextChars = new char[entryStr.length()];
		for(int i = 0; i < decipherStr.length(); i++)
		{
			cleartextChars[i] = decipherStr.charAt(i);
		}
		
		cipherEntry.setEntry(cleartextChars, cipherEntry.getTitle());
		
		return cipherEntry;
	}
	
	public static void SQLInsert(entry e) throws SQLException
	{
		e = encrypt(e);
		SQL.insert(e);
	}
	
	public static int getDBSize()
	{
		int i = SQL.getDBSize();
		return i;
	}
	
	public static ArrayList<String> loadTitles()
	{
		ArrayList<String> titles = null;
		try{
			titles = SQL.loadTitles();
		}catch(SQLException e)
		{
			System.err.println("passman.SQLite loadTitles exception.");
		}
		return titles;
	}
	
	public static int searchByTitle(String title)
	{
		int index = 0;
		index = SQL.searchByTitle(title);
		return index;
	}
	
	public static entry get(int cell)
	{
		entry e = SQL.get(cell);
		e = decrypt(e);
		return e;
	}
	
	public static void replace(int index, entry e)
	{
		try{
			e = encrypt(e);
			SQL.replace(index, e);
			
		}catch(SQLException ex)
		{
			System.err.println("passman.SQLite replace exception.");
		}
	}
	
	public static void remove(int cell)
	{
		try{
			SQL.remove(cell);
			
		}catch(SQLException ex)
		{
			System.err.println(ex.getClass().getName() +" Failure in remove function passmanSQLite");
		}
	}
	
	public static ArrayList<Integer> searchByTag(String tag)
	{
		int index = 0; ArrayList<Integer> list = new ArrayList<Integer>();
		list = SQL.searchByTag(tag);
		return list;
	}
	
	public static LinkedList<String> loadTags()
	{
		LinkedList<String> list = new LinkedList<String>();
		
		//Read all tags from db.
		list = SQL.loadTags();
		
		return list;
	}
}
