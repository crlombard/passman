/**
 * @author	Coleman R. Lombard
 * @file	passmanSQLite.java
 *			This file contains the passmanSQLite class, used to store all the data
 *			for the passman application in an SQLite database. If another database
 *			is desired, simply implement all existing member functions with functions
 *			implementing the desired database. 
 * 
 *			All member functions are documented in the javadoc style, each comment
 *			block is preceded by a tag "(#)" for ease of searching. To view documentation,
 *			simply search by this tag sequence.
 *			
 * @date	11/17/2016
 */
package passman;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedList;
import org.apache.commons.codec.binary.Base64;

public class passmanSQLite 
{
	private static Connection sqliteDB;
	String workingDir = System.getProperty("user.dir");
	private static String sqliteDBPath;
	private static int SQLiteDBSize = 0;

	passmanSQLite() 
	{
		sqliteDBPath = ("jdbc:sqlite:" +"passman.db");
	} //Default constructor.
	
	passmanSQLite(String path) 
	{	
		sqliteDBPath = path;
	} //Construct with specified sqliteDBPath
	

	/*(#) createEntryTable()
	 *	  This function creates a table for entries in the database if one does not exist.
	 ***************************************************************************
	 * @param	None.
	 * @pre		None required. One of two states will be true; either the sqliteDB
	 *			will contain a table entitled entryTable or it won't.
	 * @post	A table named entryTable will exist. If the function creates the
	 *			table, a message detailing table creation success is printed, 
	 *			currently to the standard output. Else an SQLException is thrown.
	 * @return	None.
	 * @throws	SQLException if entryTable exists.
	 */
	public static void createEntryTable() throws SQLException
	{
		sqliteDB = null;
		Statement stmt = null;
		try 
		{
			//Class.forName("org.sqlite.JDBC");
			sqliteDB = DriverManager.getConnection(sqliteDBPath);
			//System.out.println("Opened database successfully");

			stmt = sqliteDB.createStatement();
			String sql = "CREATE TABLE entryTable " +
						 "(ID INT PRIMARY KEY     NOT NULL," +
						 " TITLE TEXT NOT NULL, " + 
						 " ENTRY TEXT NOT NULL, " + 
						 " TAGS	TEXT)"; 
			stmt.executeUpdate(sql);
			stmt.close();
		} catch ( Exception e ) 
		{
			//System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			throw new SQLException(e);
		}
		//System.out.println("Entry table created successfully");	
	}
	
		/*(#) createClosedTable()
	 *	  This function creates a table in the database for keys if one does not exist.
	 ***************************************************************************
	 * @param	None.
	 * @pre		None required. One of two states will be true; either the sqliteDB
	 *			will contain a table entitled closedTable or it won't.
	 * @post	A table named closedTable will exist. If the function creates the
	 *			table, a message detailing table creation success is printed, 
	 *			currently to the standard output. Else an SQLException is thrown.
	 * @return	None.
	 * @throws	SQLException if closedTable exists.
	 */
	public static void createClosedTable() throws SQLException
	{
		sqliteDB = null;
		Statement stmt = null;
		try 
		{
			//Class.forName("org.sqlite.JDBC");
			sqliteDB = DriverManager.getConnection(sqliteDBPath);
			//System.out.println("Opened database successfully");

			stmt = sqliteDB.createStatement();
			String sql = "CREATE TABLE closedTable " +
						 "(ID INT PRIMARY KEY     NOT NULL," +
						 " SALT TEXT NOT NULL, " + 
						 " KEY TEXT NOT NULL)"; 
			stmt.executeUpdate(sql);
			stmt.close();
		} catch ( Exception e ) 
		{
			//System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			throw new SQLException(e);
		}
	}
	
	/*(#) insertVolumePassword()
	 *	  This function inserts a password and salt into a separate table
	 *	  designated for passwords. This password should be a hash, not a clear
	 *	  text password. The salt should be 32 bytes, and is used to salt the
	 *	  hash which makes the password hash using the PBKDF2 algorithm.
	 ***************************************************************************
	 * @param	None.
	 * @pre		Database must exist, and closedTable must exist.
	 * @post	password and salt are added to closedTable with ID = 1.
	 * @return	None.
	 * @throws	SQLException if SQL insertion fails.
	 */
	public static void insertVolumePassword(String password, byte[] salt) throws SQLException
	{
		String insertSQL = "INSERT INTO closedTable (ID, SALT, KEY) VALUES "
				+"(?,?,?)";              
		PreparedStatement pstmt = null;
		
		try 
		{
			pstmt = sqliteDB.prepareStatement(insertSQL);
		} catch(SQLException e) 
		{
			System.out.println("Exception");
		}

		pstmt.setInt(1, 1);
		pstmt.setString(2, (new String(Base64.encodeBase64(salt))));
		pstmt.setString(3, password);
		pstmt.execute();
		
		/*
		 * Autocommit. In the future, this will be tied to a user prompt? 
		 * Or include prompt in GUI class, and don't return until true there?.
		 */
		//sqliteDB.commit();
	}
	
	/*(#) loadSalt()
	 *	  This function returns the salt stored in closedTable.
	 ***************************************************************************
	 * @param	None.
	 * @pre		Database must exist, and closedTable must exist, must be an entry
	 *			in closedTable.
	 * @post	None.
	 * @return	byte array salt from closedTable.
	 * @throws	SQLException if SQL selection fails.
	 */
	public static byte[] loadSalt() throws SQLException
	{
		String key = ""; byte[] bytes;
		String selectSQL = "SELECT SALT FROM closedTable";
		Statement pstmt = sqliteDB.createStatement();
		ResultSet rs = pstmt.executeQuery(selectSQL);
		key = rs.getString(1);
		bytes = key.getBytes();
		return bytes;
	}
	
	/*(#) loadPass()
	 *	  This function returns the password stored in closedTable.
	 ***************************************************************************
	 * @param	None.
	 * @pre		Database must exist, and closedTable must exist, must be an entry
	 *			in closedTable.
	 * @post	None.
	 * @return	String password from closedTable is returned.
	 * @throws	SQLException if SQL selection fails.
	 */
	public static String loadPass() throws SQLException
	{
		String key = "";
		String selectSQL = "SELECT KEY FROM closedTable";
		Statement pstmt = sqliteDB.createStatement();
		ResultSet rs = pstmt.executeQuery(selectSQL);
		key = rs.getString(1);
		return key;
	}
	
	/*(#)	insert(entry newEntry)
	 *		This function adds an entry to the database.
	 ***************************************************************************
	 * @param	Object of type entry named newEntry.
	 * @pre		None required. Database can be in any state.
	 * @post	newEntry is added to entryTable, the table in the provided SQLite
	 *			database.
	 * @return	None.
	 * @throws	SQLException if SQLite insert function throws an SQLException.
	 */
	public static void insert(entry newEntry) throws SQLException
	{
		String insertSQL = "INSERT INTO entryTable (ID, TITLE, ENTRY, TAGS) VALUES "
				+"(?,?,?,?)";              
		PreparedStatement pstmt = null;
		
		try 
		{
			pstmt = sqliteDB.prepareStatement(insertSQL);
		} catch(SQLException e) 
		{
			System.out.println("Exception");
		}

		String str = "";
		for(int i = 0; i < newEntry.getEntry().length; i++)
		{
			char[] c = newEntry.getEntry();
			str = str + c[i];	
		}
		
		//Remove bracketry from tags.
		LinkedList<String> tagList = newEntry.getTags(); String cleanTag = "";
		for(int i = 0; i < tagList.size(); i++)
		{
			//System.out.println("Processing tag #" +i +" of " +tagList.size() +" tag is " +tagList.get(i));
			for(int j = 0; j < tagList.get(i).length(); j++)
			{
				//System.out.println("Processing char #" +j +" of " +tagList.get(i).length() +" char is " +tagList.get(i).charAt(j));
				if((tagList.get(i).charAt(j) == '[') || (tagList.get(i).charAt(j) == ']'))
				{
					//System.out.println("char " +tagList.get(i).charAt(j) +" was removed from the tag.");
				}
				else
				{
					cleanTag += tagList.get(i).charAt(j);
				}
			}
		}
		
		//Save all data from tags LinkedList into a String.
		String tagString = "";
		for(int i = 0; i < tagList.size(); i++)
		{
			if(i == 0)
			{
				tagString += tagList.get(i);
			}
			else
			{
				tagString += ", ";
				tagString += tagList.get(i);
			}
		}
		
		//Commit data to database.
		pstmt.setInt(1, getDBSize()+1);
		pstmt.setString(2,newEntry.getTitle());
		pstmt.setObject(3, str);
		//pstmt.setObject(4, tagList);
		pstmt.setString(4, tagString);
		pstmt.execute();
		
		/*
		 * Autocommit. In the future, this will be tied to a user prompt? 
		 * Or include prompt in GUI class, and don't return until true there?.
		 */
		//sqliteDB.commit();
	}
	
	/*(#)	displayTableContents()
	 *		This function displays the contents of the database to the shell.
	 ***************************************************************************
	 * @param	None.
	 * @pre		Database must exist.
	 * @post	Contents of entryTable are displayed to standard output.
	 * @return	None.
	 * @throws	SQLException if SQLite insert function throws an SQLException.
	 */
	public static void displayTableContents() throws SQLException
	{
		String selectSQL = "SELECT ID, TITLE, ENTRY, TAGS FROM entryTable ORDER BY TITLE COLLATE NOCASE";
		Statement pstmt = sqliteDB.createStatement();
		
		ResultSet rs = pstmt.executeQuery(selectSQL);
		
		String ID = "";
		String TITLE = "";
		String ENTRY = "";
		String TAGS = "";
				
		while (rs.next()) {
			ID = rs.getString("ID");
			TITLE = rs.getString("TITLE");
			ENTRY = rs.getString("ENTRY");
			TAGS = rs.getString("TAGS");
			System.out.print(ID +" " +TITLE +" " +ENTRY +" " +TAGS +"\n");
		}
	}
	
	/*(#)	loadTitles()
	 *		This function loads all the titles from the database into an ArrayList
	 *		of Strings, and returns.
	 ***************************************************************************
	 * @param	None.
	 * @pre		Database must exist.
	 * @post	All titles of database are loaded into ArrayList titles.
	 * @return	ArrayList titles containing Strings of all the titles in the database.
	 * @throws	SQLException if SQLite select function throws an SQLException.
	 */
	public static ArrayList<String> loadTitles() throws SQLException
	{
		String selectSQL = "SELECT TITLE FROM entryTable";
		Statement pstmt = sqliteDB.createStatement();
		
		ResultSet rs = pstmt.executeQuery(selectSQL);

		ArrayList<String> titles = new ArrayList<String>();
		while(rs.next()) {
			titles.add(rs.getString(1));
		}
		return titles;
	}
	
	/*(#)	loadFullList()
	 *		This function is used to load the full contents of the database into
	 *		the entryList fullList. fullList should be passed by reference (a
	 *		class level variable).
	 ***************************************************************************
	 * @param	None.
	 * @pre		Database must exist.
	 * @post	All titles of database are loaded into entryList fullList.
	 * @return	None.
	 * @throws	SQLException if SQLite select function throws an SQLException.
	 */
	public static void loadFullList(entryList fullList) throws SQLException
	{
		String selectSQL = "SELECT ID, TITLE, ENTRY, TAGS FROM entryTable";
		Statement pstmt = sqliteDB.createStatement();
		
		ResultSet rs = pstmt.executeQuery(selectSQL);
		
		String ID = "";
		String TITLE = "";
		String ENTRY = "";
		String TAGS = "";
				
		while (rs.next()) {
			ID = rs.getString("ID");
			TITLE = rs.getString("TITLE");
			ENTRY = rs.getString("ENTRY");
			TAGS = rs.getString("TAGS");
			
			entry e = new entry();
			e.setEntry(ENTRY.toCharArray(), TITLE);
			
			String aTag = ""; 
			
			//Set flag if TAGS is empty (contains []).
			boolean emptyTAGSFlag = (TAGS.equals("[]"));
			
			for(int i = 0; i < TAGS.length(); i++)
			{
				if(!TAGS.equals(null))
				{
					char c = TAGS.charAt(i);
					
					if((c == ']') || (c == '['))
					{
						continue;
					}
					else if(c == ',')
					{
						e.addTag(aTag);
						aTag = "";
						continue;
					}
					else if(c == ' ')
					{
						continue;
					}
					else
					{
						aTag += c;
					}
				}
			}
			
			//Add last tag if relevant.
			if(!emptyTAGSFlag)
			{
				e.addTag(aTag);
			}
			
			fullList.addEntry(e);
		}
	}
	
	/*(#)	getDBSize()
	 *		This function returns the int count of the number of records in the
	 *		database.
	 ***************************************************************************
	 * @param	None.
	 * @pre		Database must exist.
	 * @post	Database is unchanged.
	 * @return	int count of the number of records in the database.
	 * @throws	SQLException if SQLite select function throws an SQLException.
	 */
	public static int getDBSize()
	{
		String s = "SELECT Count(*) FROM entryTable"; int i = 0;
		try{
			Statement pstmt = sqliteDB.createStatement();
			ResultSet rs = pstmt.executeQuery(s);
			i = rs.getInt(1);
		}catch(Exception e)
		{
			System.err.println("Error in passmanSQLite getDBSize().");
		}

		return i;
	}
		
	/*(#)	searchByTitle(String title)
	 *		This function returns the int location of the searched title if that
	 *		title exists in the database. Note this only returns the cell of the
	 *		first instance of that title, and as such, the database should
	 *		only contain one of each title.
	 ***************************************************************************
	 * @param	String title to search by.
	 * @pre		Database must exist.
	 * @post	Database is unchanged.
	 * @return	int cell number of the record in the database.
	 * @throws	SQLException if SQLite select function throws an SQLException.
	 */
	public static int searchByTitle(String title)
	{
		String s = "SELECT ID FROM entryTable WHERE TITLE = \"" +title +"\"" +" ORDER BY TITLE COLLATE NOCASE"; int i = 0;
		try{
			Statement pstmt = sqliteDB.createStatement();
			ResultSet rs = pstmt.executeQuery(s);
			i = rs.getInt(1);
		}catch(Exception e)
		{
			System.err.println("Error in passmanSQLite searchByTitle().");
		}
		return i;
	}
	
	/*(#)	get()
	 *		This function returns the entry in the specified int cell of the 
	 *		database.
	 ***************************************************************************
	 * @param	Cell to retrieve data from.
	 * @pre		Database must exist.
	 * @post	Database is unchanged.
	 * @return	entry in index cell.
	 * @throws	SQLException if SQLite select function throws an SQLException.
	 */
	public static entry get(int cell)
	{
		String s = "SELECT * FROM entryTable WHERE ID = " +cell;
		entry e = null;
		try{
			Statement pstmt = sqliteDB.createStatement();
			ResultSet rs = pstmt.executeQuery(s);
			e = new entry(rs.getString("ENTRY").toCharArray(), rs.getString("TITLE"));
			
			String allTags = ""; String aTag = "";
			allTags = rs.getString("TAGS");
			
			//Parse tags.
			int tagCount = 0;
			//Set flag if TAGS is empty (contains []).
			boolean emptyTAGSFlag = (allTags.equals("[]"));
			
			for(int i = 0; i < allTags.length(); i++)
			{
				if(!allTags.equals(null))
				{
					char c = allTags.charAt(i);
					
					if((c == ']') || (c == '['))
					{
						continue;
					}
					else if(c == ',')
					{
						e.addTag(aTag);
						aTag = "";
						continue;
					}
					else if(c == ' ')
					{
						continue;
					}
					else
					{
						aTag += c;
					}
				}
			}
			
			//Add last tag if relevant.
			if(!emptyTAGSFlag)
			{
				e.addTag(aTag);
			}
			
		}catch(Exception f)
		{
			System.err.println("Error in passmanSQLite get().");
		}
		return e;
	}

	/*(#)	replace(int index, entry e)
	 *		This function replaces the data at the parameter index with the data 
	 *		in the parameter entry e.
	 ***************************************************************************
	 * @param	int index to replace to, entry e to replace with.
	 * @pre		Index must exist in database table.
	 * @post	Data at index is now e.
	 * @return	None.
	 * @throws	SQLException if SQLite update function throws an SQLException.
	 *			This can happen if index doesn't exist, if the database doesn't
	 *			exist, or if the table doesn't exist.
	 */
	public static void replace(int index, entry e) throws SQLException
	{
		String str = "UPDATE entryTable SET TITLE = ?, ENTRY = ?, TAGS = ? WHERE ID = ?";
		PreparedStatement pstmt = sqliteDB.prepareStatement(str);
		pstmt.setString(1, e.getTitle());
		
		String entry = "";
		for(int i = 0; i < e.getEntry().length; i++)
		{
			entry += e.getEntry()[i];
		}
		pstmt.setString(2, entry);
		
		String tags = "";
		for(int i = 0; i < e.getTags().size(); i++)
		{
			tags += e.getTags().get(i);
			if(i < e.getTags().size()-1)
			{
				tags += ",";
			}

		}
		pstmt.setString(3, tags);
		
		pstmt.setInt(4, index);

		pstmt.executeUpdate();
	}
	
	/*(#)	remove(int cell)
	 *		This function removes the data at the parameter index.
	 ***************************************************************************
	 * @param	int index to remove.
	 * @pre		Index must exist in database table.
	 * @post	Data at index is now gone. Following indices are adjusted
	 *			as required to maintain index count.
	 * @return	None.
	 * @throws	SQLException if SQLite update function throws an SQLException.
	 *			This can happen if index doesn't exist, if the database doesn't
	 *			exist, or if the table doesn't exist.
	 */
	public static void remove(int cell) throws SQLException
	{
		//Get initial row count for database.
		int initialSize = getDBSize();
		
		//Remove the statement.
		String selectSQL = "DELETE FROM entryTable WHERE ID = ?";
		PreparedStatement pstmt = sqliteDB.prepareStatement(selectSQL);
		pstmt.setInt(1, cell);
		pstmt.executeUpdate();
		
		//Increment the cell count of all entries following the removed item.
		for(int i = cell+1; i <= initialSize; i++)
		{
			//Get ID of entry at i.
			String incrementStr = "UPDATE entryTable SET ID = ? WHERE ID = ?";
			PreparedStatement incrementSTMT = sqliteDB.prepareStatement(incrementStr);
			incrementSTMT.setInt(1, i-1);	//Set ID = i-1
			incrementSTMT.setInt(2, i);		//Where ID = i
			incrementSTMT.executeUpdate();
		}
	}	
	
	/*(#)	searchByTag(String tag)
	 *		This function returns the int location of the searched title if that
	 *		title exists in the database. Note this only returns the cell of the
	 *		first instance of that title, and as such, the database should
	 *		only contain one of each title.
	 ***************************************************************************
	 * @param	ArrayList of Strings "tags" to search by.
	 * @pre		Database must exist.
	 * @post	Database is unchanged.
	 * @return	int cell number of the record in the database.
	 * @throws	SQLException if SQLite select function throws an SQLException.
	 */
	public static ArrayList<Integer> searchByTag(String tag)
	{
		int i = 0;
		ArrayList<Integer> list = new ArrayList<Integer>();
		
		//for(i = 1; i <= getDBSize(); i++)
		for(i = 1; i <= 1; i++)
		{
			//String s = "SELECT TAGS FROM entryTable WHERE ID = " +i;
			String s = "SELECT ID FROM entryTable WHERE TAGS LIKE \'%" +tag +"%\'" +" ORDER BY TITLE COLLATE NOCASE";
			try{
				Statement pstmt = sqliteDB.createStatement();
				ResultSet rs = pstmt.executeQuery(s);			

				while(rs.next())
				{
					list.add(rs.getInt(1));
				}
				
			}catch(Exception e)
			{
				System.err.println(e.getClass().getName() +"Error in passmanSQLite searchByTag().");

			}
		}
		return list;
	}
	
	public static LinkedList<String> loadTags()
	{
		LinkedList<String> rawList = new LinkedList<String>();
		LinkedList<String> list = new LinkedList<String>();
		
		//Read all tags from db.
		String s = "SELECT DISTINCT TAGS FROM entryTable ORDER BY TAGS COLLATE NOCASE"; 
		boolean multipleTags = false;
		try{
			Statement pstmt = sqliteDB.createStatement();
			ResultSet rs = pstmt.executeQuery(s);
			//list.add(rs.getString(1));
			String str = "";
			String strToAdd = "";
			while(rs.next())
			{
				str = rs.getString(1);
				for(int i = 0; i < str.length(); i++)
				{
					if((str.charAt(i) == '[') || (str.charAt(i) == ']') || (str.charAt(i) == ' '))
					{
						//Iterate.
					}

					else if(str.charAt(i) == ',')
					{
						//Commit previous tag if it's not in the list, there's more than 1 tag in here.
						if(!list.contains(strToAdd))
						{
							list.add(strToAdd);
						}
						strToAdd = "";
					}
					
					else
					{
						strToAdd += str.charAt(i);
					}
				}
				//Add last tag.
				if(!list.contains(strToAdd))
				{
					list.add(strToAdd);
				}
				strToAdd = "";
			}
			
		}catch(Exception e)
		{
			System.err.println("Error in passmanSQLite searchByTitle().");
		}
		
		return list;
	}
}
