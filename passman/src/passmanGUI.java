/**
 * @author	Coleman R. Lombard
 * @file	passmanGUI.java
 *			This file contains the passmanGUI class, used to display all components
 *			of the passman application.
 * 
 *			All member functions are documented in the javadoc style, each comment
 *			block is preceded by a tag "(#)" for ease of searching. To view documentation,
 *			simply search by this tag sequence.
 * 
 * @date	11/4/2016
 */
package passman;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Scanner;
import javax.swing.border.*;
import javax.swing.text.JTextComponent;

public class passmanGUI extends JFrame{
	//private static entryList fullList = new entryList(); //Class entryList variable for storing entry data.
	private static passmanSQLite SQL = new passmanSQLite();
	Passman main = new Passman();
	
	//GUI objects and variables.
	private static int pref_w = 500;
	private static int pref_h = 300;
	private JFrame removeGUI, modGUI;
	private JLabel titleLabel, entryLabel, tagsLabel;
	private JButton createButton; 
	private JButton modifyButton = new JButton("Modify"); 
	private JButton removeButton = new JButton("Remove");
	private JPanel buttons, labels, gui;
	private DefaultListModel<String> model;
	private JList<String> list;
	private JScrollPane scrollPane;
	private JTextArea entryText, tagsText, titleText;
	private JTextArea newEntryText, newTagsText, newTitleText;
	//private JTextField searchField;
	private LinkedList<String> existingTags;
	//private JComboBox tagAutocompleteSearchBox;
	private AutocompleteJComboBox tagAutocompleteBox;
	private int indexInList;
	private boolean modMenuCreated = false;
	private boolean removeMenuCreated = false;
	private static String OS = System.getProperty("os.name").toLowerCase();
	
	public passmanGUI() {
		super("passmanGUI");
		
		gui = new JPanel(new BorderLayout(2, 2));
		gui.setBorder(new TitledBorder("Title"));
		
		labels = new JPanel(new GridLayout(0, 1, 1, 1));
		labels.setBorder(new TitledBorder("Data"));
		
		/***********************************************************************
		 * Populate buttons.
		 **********************************************************************/
		
		//Create Buttons Panel
		buttons = new JPanel(new GridLayout(1, 0, 1, 1));
				
		//Set modify and remove buttons invisible until selection.
		modifyButton.setVisible(false);
		removeButton.setVisible(false);
		
		//Generate createButton. Calls a function to add an entry to the fullList.
		createButton = new JButton("Create Entry");
		generateCreateFunction();

		/*
		//Create searchField and add listener.
		searchField = new JTextField();
		searchField.setBackground(Color.DARK_GRAY);
		searchField.setForeground(Color.WHITE);
		searchField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		//searchField.addActionListener(action);
		*/
		
		//Stick searchfield inside JComboBox.
		existingTags = main.loadTags();
		StringSearchable searchable = new StringSearchable(existingTags);
		tagAutocompleteBox = new AutocompleteJComboBox(searchable);
		tagAutocompleteBox.setBackground(Color.GRAY);
		tagAutocompleteBox.setForeground(Color.WHITE);
		tagAutocompleteBox.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		
		/***********************************************************************
		 * Create JList to add to scrollpane.
		 **********************************************************************/
		
		//Load titles from SQLite database passman.db table entryTable into ArrayList SQLTitles.
		ArrayList<String> SQLTitles = main.loadTitles();

		//Load all database titles into model, then into list to be displayed.
		model = new DefaultListModel<String>();
		for(int i = 0; i < SQLTitles.size(); i++)
		{
			model.addElement(SQLTitles.get(i));
		}

		list = new JList<String>(model);

		//Create listener for the JList.
		list.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent ev) {
				if(!ev.getValueIsAdjusting() && list.getSelectedIndex()>-1) {
					
					//Delete old modify and remove frames if relevant.
					if(modMenuCreated)
					{
						for(ActionListener act : modifyButton.getActionListeners())
						{
							modifyButton.removeActionListener(act);
						}

						modMenuCreated = false;
					}
					if(removeMenuCreated)
					{
						//removeGUI.dispose();
						for(ActionListener act : removeButton.getActionListeners())
						{
							removeButton.removeActionListener(act);
						}
						
						removeMenuCreated = false;
					}
					
					modifyButton.setVisible(true);
					removeButton.setVisible(true);
					
					//Get the selected title from the scrollpane. Retrieve full entry from database.
					String selectedTitle = "";
					selectedTitle = list.getSelectedValue().toString();
					indexInList = searchListByTitle(selectedTitle);
					int indexInDatabase = main.searchByTitle(selectedTitle);
					entry temp = main.get(indexInDatabase);
					
					//Convert entry to char array.
					char[] c = temp.getEntry();
					
					/*
					 * THIS NEEDS TO BE FIXED TO AVOID ENTRY STORAGE AS STRING IF POSSIBLE.
					 */
					String str = new String(c); //Convert entry to string.
					
					LinkedList<String> strArray = temp.getTags();
					
					//Populate relevent GUI fields with retrieved entry data.
					titleText.setText(selectedTitle);
					entryText.setText(str);
					tagsText.setText(strArray.toString());
					
					//Generate modify button and functionality.
					generateModifyFunction(indexInList);
					modMenuCreated = true;
					
					//Generate remove button and functionality.
					generateRemoveFunction(indexInList);
					removeMenuCreated = true;
				}
			}
		});
		
		/***********************************************************************
		 * Populate labels.
		 **********************************************************************/
		
		/*
		 * Title JLabel. Upon selection of an entry in the scrollpane, this field
		 * will repopulate with the title data of the selected entry object.
		 */
		titleLabel = new JLabel("Title: ");
		titleLabel.setBackground(Color.DARK_GRAY);
		titleLabel.setForeground(Color.WHITE);
		
		titleText = new JTextArea();
		titleText.setBackground(Color.DARK_GRAY);
		titleText.setForeground(Color.WHITE);
		JPanel titleLabelPanel = new JPanel(new BorderLayout()); //Transfer to new JPanel for beautification.
		
		//Place beautified JPanel in scrollpane for scroll functionality with large size entry.
		JScrollPane titlePane = new JScrollPane(titleLabelPanel);
		
		//Add label
		titlePane.setBorder(new TitledBorder("Title: "));
		titlePane.setBackground(Color.GRAY);
		
		//Add title to Panel
		titleLabelPanel.add(titleText);
		titleText.setEditable(false);
		
		/*
		 * Entry JLabel. Upon selection of an entry in the scrollpane, this field
		 * will repopulate with the entry data of the selected entry object.
		 */
		entryLabel = new JLabel("Entry: ");
		entryText = new JTextArea();
		entryText.setBackground(Color.DARK_GRAY);
		entryText.setForeground(Color.WHITE);
		JPanel entryLabelPanel = new JPanel(new BorderLayout(2,2)); //Transfer to new JPanel for beautification.
		
		//Place beautified JPanel in scrollpane for scroll functionality with large size entry.
		JScrollPane entryPane = new JScrollPane(entryLabelPanel);
		
		//Add label
		entryPane.setBorder(new TitledBorder("Entry: "));
		entryPane.setBackground(Color.GRAY);
		
		//Add entry to Panel
		entryLabelPanel.add(entryText);
		entryText.setEditable(false);
		entryText.setWrapStyleWord(true);
		entryText.setLineWrap(true);
		
		/*
		 * Tags JLabel. Upon selection of an entry in the scrollpane, this field
		 * will repopulate with the tag data of the selected entry object.
		 */
		tagsLabel = new JLabel("Tags: ");
		tagsText = new JTextArea();
		tagsText.setBackground(Color.DARK_GRAY);
		tagsText.setForeground(Color.WHITE);
		JPanel tagsLabelPanel = new JPanel(new BorderLayout(2,2)); //Transfer to new JPanel for beautification.
		
		//Place beautified JPanel in scrollpane for scroll functionality with large size entry.
		JScrollPane tagsPane = new JScrollPane(tagsLabelPanel);
		
		//Add label
		tagsPane.setBorder(new TitledBorder("Tags: "));
		tagsPane.setBackground(Color.GRAY);
		
		//Add tags to Panel
		tagsLabelPanel.add(tagsText);
		tagsText.setEditable(false);
		
		//Place all components into JPanel.
		JPanel lower = new JPanel(new BorderLayout(2,2));
		lower.add(entryPane, BorderLayout.CENTER);
		lower.add(tagsPane, BorderLayout.SOUTH);
		lower.add(titlePane, BorderLayout.NORTH);
		labels.add(lower, BorderLayout.CENTER);
		
		//Allow selection of list entries.
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		/***********************************************************************
		 * Construct final GUI.
		 **********************************************************************/
		
		//Color all buttons.
		createButton.setBackground(Color.LIGHT_GRAY);
		createButton.setBorder(new LineBorder(Color.DARK_GRAY));
		createButton.setOpaque(true);
		
		modifyButton.setBackground(Color.LIGHT_GRAY);
		modifyButton.setBorder(new LineBorder(Color.DARK_GRAY));
		modifyButton.setOpaque(true);
		
		removeButton.setBackground(Color.LIGHT_GRAY);
		removeButton.setBorder(new LineBorder(Color.DARK_GRAY));
		removeButton.setOpaque(true);
		
		//Add all buttons to button panel 'buttons'.
		//buttons.add(searchField);
		buttons.add(tagAutocompleteBox);
		buttons.add(createButton);
		buttons.add(modifyButton);
		buttons.add(removeButton);
		
		//Create scrollPane and populate with list.
		scrollPane = new JScrollPane(list);
		Dimension titleScrollPaneDimension = new Dimension(120,20);
		scrollPane.setPreferredSize(titleScrollPaneDimension);
		
		//Color major components.
		gui.setBackground(Color.GRAY);
		list.setBackground(Color.DARK_GRAY);
		list.setForeground(Color.WHITE);
		labels.setBackground(Color.GRAY);
		buttons.setBackground(Color.GRAY);
		
		//Add major objects to gui frame; labels, scrollPane, and buttons.
		gui.add(labels, BorderLayout.CENTER);
		gui.add(scrollPane, BorderLayout.WEST);
		gui.add(buttons, BorderLayout.SOUTH);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		add(gui);
		setVisible(true);
		
		//Run empty search by tags to alphabetize initial list.
		searchByTag("");
	}
	
	/*(#)	searchListByTitle()
	 *		This function returns the index of the title parameter in the JList
	 *		list if it exists in the list.
	 ***************************************************************************
	 * @param	Title as String.
	 * @pre		None.
	 * @post	Index of title in GUI JList list if it exists in the list.
	 * @return	int index of the title.
	 * @throws	None.
	 */
	public int searchListByTitle(String title)
	{
		int titleIndex = 0;
		
		for(int i = 0; i < list.getModel().getSize(); i++)
		{
			if(list.getModel().getElementAt(i).equals(title))
			{
				titleIndex = i;
				break;
			}
		}
		return titleIndex;
	}
	
	/*(#)	searchByTag()
	 *		This function updates the GUI display to include all entries 
	 *		containing the tag provided to the GUI search bar.
	 ***************************************************************************
	 * @param	String str to search by.
	 * @pre		None.
	 * @post	Index of all items in GUI JList list containing the tag.
	 * @return	None.
	 * @throws	None.
	 */
	public void searchByTag(String str)
	{
		ArrayList<Integer> tagIndexList = new ArrayList<Integer>();
		try{
			tagIndexList = main.searchByTag(str);

			//Load titles from SQLite database passman.db table entryTable into ArrayList SQLTitles.
			ArrayList<String> SQLTitles = main.loadTitles();

			model.removeAllElements();
			clearDataSelection();

			for(int j = 0; j < tagIndexList.size(); j++)
			{
				//Update the JList to contain only the titles at these indices.
				model.addElement(SQLTitles.get(tagIndexList.get(j)-1));
			}
		}catch(Exception p){
			System.err.println(p.getClass().getName() +"Exception in searchfield.");
		}
	}
	
	/*(#)	generateCreateFunction()
	 *		This generates the GUI tool needed to handle user addition of a new
	 *		entry from the user. It will also call needed functionality from the
	 *		main function to add data to the database, and handle other necessary
	 *		functionality associated with entry creation.
	 ***************************************************************************
	 * @param	None.
	 * @pre		None.
	 * @post	JFrame is generated to handle user addition of new entry.
	 * @return	None.
	 * @throws	None.
	 */
	public void generateCreateFunction()
	{
		createButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//Display GUI to read entry from user.
				final JFrame searchGUI = new JFrame("passmanGUI - NewEntry");
				JPanel newEntryPanel = new JPanel(new BorderLayout(2,2));	
				newEntryPanel.setBackground(Color.GRAY);
				
				/*
				 * Title text area. Will allow for entry of title data.
				 */
				newTitleText = new JTextArea();
				newTitleText.setBackground(Color.DARK_GRAY);
				newTitleText.setForeground(Color.WHITE);
				newTitleText.setCaretColor(Color.WHITE);
				JPanel newTitleLabelPanel = new JPanel(new BorderLayout(2,2)); //Transfer to new JPanel for beautification.

				//Place beautified JPanel in scrollpane for scroll functionality with large size entry.
				JScrollPane newTitlePane = new JScrollPane(newTitleLabelPanel);

				//Add label
				newTitlePane.setBorder(new TitledBorder("Title: "));
				newTitlePane.setBackground(Color.GRAY);

				//Add entry to Panel
				newTitleLabelPanel.add(newTitleText);

				/*
				 * newEntry text area. Will allow for entry of entry data.
				 */
				newEntryText = new JTextArea();
				newEntryText.setBackground(Color.DARK_GRAY);
				newEntryText.setForeground(Color.WHITE);
				newEntryText.setCaretColor(Color.WHITE);
				newEntryText.setWrapStyleWord(true);
				newEntryText.setLineWrap(true);
				JPanel newEntryLabelPanel = new JPanel(new BorderLayout(2,2)); //Transfer to new JPanel for beautification.

				//Place beautified JPanel in scrollpane for scroll functionality with large size entry.
				JScrollPane newEntryPane = new JScrollPane(newEntryLabelPanel);

				//Add label
				newEntryPane.setBorder(new TitledBorder("Entry: "));
				newEntryPane.setBackground(Color.GRAY);

				//Add entry to Panel
				newEntryLabelPanel.add(newEntryText);

				/*
				 * Tags text area. Will allow for entry of tags data.
				 */
				newTagsText = new JTextArea();
				newTagsText.setBackground(Color.DARK_GRAY);
				newTagsText.setForeground(Color.WHITE);
				newTagsText.setCaretColor(Color.WHITE);
				JPanel newTagsLabelPanel = new JPanel(new BorderLayout(2,2)); //Transfer to new JPanel for beautification.

				//Place beautified JPanel in scrollpane for scroll functionality with large size entry.
				JScrollPane newTagsPane = new JScrollPane(newTagsLabelPanel);

				//Add label
				newTagsPane.setBorder(new TitledBorder("Tags: (Separate with commas)"));
				newTagsPane.setBackground(Color.GRAY);

				//Add tags to Panel
				newTagsLabelPanel.add(newTagsText);

				//Add title, entry, and tag fields to newEntryPanel.
				newEntryPanel.add(newTitlePane, BorderLayout.NORTH);
				newEntryPanel.add(newEntryPane, BorderLayout.CENTER);
				newEntryPanel.add(newTagsPane, BorderLayout.SOUTH);
		
				
				/**
				 * Create submit button and listener to push data onto fullList
				 * once user is finished with their new entry.
				 */
				JButton submitNewEntryButton = new JButton("Submit");
				submitNewEntryButton.setBackground(Color.LIGHT_GRAY);
				submitNewEntryButton.setBorder(new LineBorder(Color.DARK_GRAY));
				submitNewEntryButton.setOpaque(true);
				submitNewEntryButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						//Read data from text fields.
						String title = newTitleText.getText();		//Get title.
						String tags = newTagsText.getText();		//Get tags.
						String str = newEntryText.getText();		//Get entry as string.
						
						//If title has no entry, close window.
						if(title.isEmpty()){
							searchGUI.dispose();
						}
						else{
							/*
							 * Else add new entry to database via passman.SQLInsert()
							 * and add title to model.
							 */
							char[] entry = str.toCharArray();			//Cast entry to char array.
							entry newEntry = new entry(entry, title);	//Create new entry in fullList.

							//Parse tags to fullList tags.
							char[] t = tags.toCharArray(); String s = "";
							for(int i = 0; i < tags.length(); i++)
							{
								//All tag entries are separated by commas. Add tags, omit commas.
								if(t[i] == ',')
								{
									newEntry.addTag(s);
									s = "";
								}
								else
								{
									s += t[i];
								}
							}
							newEntry.addTag(s);

							//Populate JList list with new data loaded into model.
							model.addElement(title);

							//Push new data onto database.
							try{
								main.SQLInsert(newEntry);
							}catch(SQLException f)
							{
								System.err.println("SQLInsert Exception thrown after submission of new entry.");
							}
							
							//Maintain alphabetical title sort by running blank tag search.
							searchByTag("");
							
							//Clear displayed data.
							clearDataSelection();
							
							//Close window.
							searchGUI.dispose();
						}
					}
				});	
				searchGUI.getPreferredSize();
				
				searchGUI.add(newEntryPanel, BorderLayout.CENTER);
				searchGUI.add(submitNewEntryButton, BorderLayout.SOUTH);
				searchGUI.setSize(600, 600);
				searchGUI.setVisible(true);
			}
		});
	}
	
	/*(#)	generateRemoveFunction()
	 *		This generates the GUI tool needed to handle user removal of an 
	 *		entry. It will also call needed functionality from the main function 
	 *		to remove data from the database, andhandle other necessary 
	 *		functionality associated with entry removal.
	 ***************************************************************************
	 * @param	None.
	 * @pre		None.
	 * @post	JFrame is generated to handle user removal of the entry selected
	 *			in the GUI.
	 * @return	None.
	 * @throws	None.
	 */
	public void generateRemoveFunction(final int indexInList)
	{
		//Create remove button and add listener.
		removeButton.setVisible(true);
		removeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent removeButtonAction) {
				//Display GUI to read entry from user.
				//final JFrame removeGUI = new JFrame("passmanGUI - Confirm Removal");
				removeGUI = new JFrame("passmanGUI - Confirm Removal");
				JPanel newEntryPanel = new JPanel(new BorderLayout(2,2));	
				newEntryPanel.setBackground(Color.GRAY);
				
				/*
				 * Title text area. Will allow for entry of title data.
				 */
				newTitleText = new JTextArea();
				newTitleText.setBackground(Color.DARK_GRAY);
				newTitleText.setForeground(Color.WHITE);
				newTitleText.setCaretColor(Color.WHITE);
				JPanel newTitleLabelPanel = new JPanel(new BorderLayout(2,2)); //Transfer to new JPanel for beautification.
				
				/**
				 * Create submit button and listener to push data onto fullList
				 * once user is finished with their new entry.
				 */
				JButton confirmButton = new JButton("Confirm Removal?");
				confirmButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent confirmRemoval) {
						//Deselect entry
						//int tempNumLabel = indexInList;
						list.clearSelection();
						
						//Get title of entry to remove.
						String removedItemTitle = list.getModel().getElementAt(indexInList);
						
						//Get index in database.
						int indexInDatabase = main.searchByTitle(removedItemTitle);
								
						//Remove entry from database.
						main.remove(indexInDatabase);
						
						//Refresh model by removing title.
						int removedItemIndex = searchListByTitle(removedItemTitle);
						model.removeElementAt(removedItemIndex);
						
						clearDataSelection();
						removeGUI.dispose();
					}
				});	
				removeGUI.getPreferredSize();
				
				//removeGUI.add(newEntryPanel, BorderLayout.CENTER);
				removeGUI.add(confirmButton, BorderLayout.SOUTH);
				if(OS.contains("win"))
				{
					removeGUI.setSize(400, 60);
				}
				else
				{
					removeGUI.setSize(400, 55);
				}
				removeGUI.setLocationRelativeTo(gui);
				removeGUI.setVisible(true);
			}
		});
	}
	
	/*(#)	generateModifyFunction()
	 *		This generates the GUI tool needed to handle user modification of an
	 *		entry. It will also call needed functionality from the main function 
	 *		to modify data in the database, and handle other necessary 
	 *		functionality associated with entry modification.
	 ***************************************************************************
	 * @param	None.
	 * @pre		None.
	 * @post	JFrame is generated to handle user modification of the entry
	 *			selected in the GUI.
	 * @return	None.
	 * @throws	None.
	 */
	public void generateModifyFunction(final int indexInList)
	{
		//Create modify button and add listener.
		//modifyButton = new JButton("Modify");
		modifyButton.setVisible(true);
		modifyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent modButton) {
				//Display GUI to read entry from user.
				modGUI = new JFrame("passmanGUI - ModifyEntry");
				JPanel newEntryPanel = new JPanel(new BorderLayout(2,2));	
				newEntryPanel.setBackground(Color.GRAY);
				final int indexInModel = indexInList;
				final int indexInDatabase = main.searchByTitle(list.getModel().getElementAt(indexInModel));
				
				/*
				 * Title text area. Will allow for entry of title data.
				 */
				newTitleText = new JTextArea();
				newTitleText.setBackground(Color.DARK_GRAY);
				newTitleText.setForeground(Color.WHITE);
				newTitleText.setCaretColor(Color.WHITE);
				JPanel newTitleLabelPanel = new JPanel(new BorderLayout(2,2)); //Transfer to new JPanel for beautification.

				//Place beautified JPanel in scrollpane for scroll functionality with large size entry.
				JScrollPane newTitlePane = new JScrollPane(newTitleLabelPanel);

				//Add label
				newTitlePane.setBorder(new TitledBorder("Title: "));
				newTitlePane.setBackground(Color.GRAY);

				//Add entry to Panel
				newTitleLabelPanel.add(newTitleText);

				/*
				 * newEntry text area. Will allow for entry of entry data.
				 */
				newEntryText = new JTextArea();
				newEntryText.setBackground(Color.DARK_GRAY);
				newEntryText.setForeground(Color.WHITE);
				newEntryText.setCaretColor(Color.WHITE);
				newEntryText.setWrapStyleWord(true);
				newEntryText.setLineWrap(true);
				JPanel newEntryLabelPanel = new JPanel(new BorderLayout(2,2)); //Transfer to new JPanel for beautification.

				//Place beautified JPanel in scrollpane for scroll functionality with large size entry.
				JScrollPane newEntryPane = new JScrollPane(newEntryLabelPanel);

				//Add label
				newEntryPane.setBorder(new TitledBorder("Entry: "));
				newEntryPane.setBackground(Color.GRAY);

				//Add entry to Panel
				newEntryLabelPanel.add(newEntryText);

				/*
				 * Tags text area. Will allow for entry of tags data.
				 */
				newTagsText = new JTextArea();
				newTagsText.setBackground(Color.DARK_GRAY);
				newTagsText.setForeground(Color.WHITE);
				newTagsText.setCaretColor(Color.WHITE);
				JPanel newTagsLabelPanel = new JPanel(new BorderLayout(2,2)); //Transfer to new JPanel for beautification.

				//Place beautified JPanel in scrollpane for scroll functionality with large size entry.
				JScrollPane newTagsPane = new JScrollPane(newTagsLabelPanel);

				//Add label
				newTagsPane.setBorder(new TitledBorder("Tags: (Separate with commas)"));
				newTagsPane.setBackground(Color.GRAY);

				//Add tags to Panel
				newTagsLabelPanel.add(newTagsText);
				
				//Get selected entry and populate fields with existing data.
				entry entryToMod = main.get(indexInDatabase);
				char[] c = entryToMod.getEntry(); //Get entry as char array.
					
					/*
					 * THIS NEEDS TO BE FIXED TO AVOID ENTRY STORAGE AS STRING IF POSSIBLE.
					 */
					String str = new String(c); //Convert to string.
					
					LinkedList<String> strArray = entryToMod.getTags();
					
					newTitleText.setText(list.getSelectedValue().toString());
					newEntryText.setText(str);
					
					//Process brackets out of tags text.
					String strModify = strArray.toString();
					String strModify2 = "";
					for(int i = 0; i < strModify.length(); i++)
					{
						if((strModify.charAt(i) == '[') || (strModify.charAt(i) == ']'))
						{
							//Do nothing
						}
						else
						{
							strModify2 += strModify.charAt(i);
						}
					}			
					newTagsText.setText(strModify2);
					
				//Add title, entry, and tag fields to newEntryPanel.
				newEntryPanel.add(newTitlePane, BorderLayout.NORTH);
				newEntryPanel.add(newEntryPane, BorderLayout.CENTER);
				newEntryPanel.add(newTagsPane, BorderLayout.SOUTH);
				
				/**
				 * Create submit button and listener to push data onto fullList
				 * once user is finished with their new entry.
				 */
				JButton submitNewEntryButton = new JButton("Submit");
				submitNewEntryButton.setBackground(Color.LIGHT_GRAY);
				submitNewEntryButton.setBorder(new LineBorder(Color.DARK_GRAY));
				submitNewEntryButton.setOpaque(true);
				submitNewEntryButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent newEntryButtonAction) {
						//Read data from text fields.
						String title = newTitleText.getText();		//Get title.
						String tags = newTagsText.getText();		//Get tags.
						String str = newEntryText.getText();		//Get entry as string.
						
						//If title has no entry, close window.
						if(title.isEmpty()){
							modGUI.dispose();
						}
						else{
							/*
							 * Else add new entry to database via passman.SQLInsert()
							 * and add title to model.
							 */
							char[] entry = str.toCharArray();			//Cast entry to char array.
							entry newEntry = new entry(entry, title);	//Create new entry in fullList.

							//Parse tags to fullList tags.
							char[] t = tags.toCharArray(); String s = "";
							for(int i = 0; i < tags.length(); i++)
							{
								//All tag entries are separated by commas. Add tags, omit commas.
								if(t[i] == ',')
								{
									newEntry.addTag(s);
									s = "";
								}
								else
								{
									s += t[i];
								}
							}
							newEntry.addTag(s);

							//Push new data onto database.
							main.replace(indexInDatabase, newEntry);
							
							//Refresh the sort with a blank tag sort.
							searchByTag("");
							
							//Refresh the selected entry's displayed data.
							list.setSelectedIndex(indexInModel); //Reset selected entry, it was cleared from the list.
							titleText.setText(newTitleText.getText());
							entryText.setText(newEntryText.getText());
							tagsText.setText(newTagsText.getText());
							
							//Clear displayed data.
							//clearDataSelection();
							
							//Close window.
							modGUI.dispose();
						}
					}
				});	
				modGUI.getPreferredSize();
				
				modGUI.add(newEntryPanel, BorderLayout.CENTER);
				modGUI.add(submitNewEntryButton, BorderLayout.SOUTH);
				modGUI.setSize(600, 600);
				modGUI.setVisible(true);
			} 
		});
	}
	
	/*(#)	clearDataSelection()
	 *		This function removes all data from the GUI displays.
	 ***************************************************************************
	 * @param	None.
	 * @pre		None.
	 * @post	GUI is cleared of all entry data.
	 * @return	None.
	 * @throws	None.
	 */
	private void clearDataSelection()
	{
		list.clearSelection();
		titleText.setText("");
		entryText.setText("");
		tagsText.setText("");
		modifyButton.setVisible(false);
		removeButton.setVisible(false);
	}
	
	private interface Searchable<E, V>
	{
		public Collection<E> search(V value);
	}
	
	private class StringSearchable implements Searchable<String,String>
	{
		private LinkedList<String> terms = new LinkedList<String>();

		public StringSearchable(LinkedList<String> newTerms){
			terms.addAll(newTerms);
		}

		public Collection<String> search(String value) {
			LinkedList<String> founds = new LinkedList<String>();
			
			for (String s : terms)
			{
				if (s.indexOf(value) == 0)
				{
					founds.add(s);
				}
			}
			return founds;
		}
	}
	
	private class AutocompleteJComboBox extends JComboBox
	{
		private final Searchable<String,String> searchable;
		public final JTextField tc = null;
		
		public AutocompleteJComboBox(Searchable<String,String> s){
			super();
			this.searchable = s;
			setEditable(true);
			Component c = getEditor().getEditorComponent();
			if (c instanceof JTextComponent)
			{
				final JTextField tc = (JTextField)c;
				tc.setBackground(Color.DARK_GRAY);
				tc.setForeground(Color.WHITE);
				
				Action action = new AbstractAction()
				{
					public void actionPerformed(ActionEvent e)
					{
						searchByTag(tc.getText());
					}
				};
				tc.addActionListener(action);

				tc.getDocument().addDocumentListener(new DocumentListener(){

					@Override
					public void changedUpdate(DocumentEvent de) {}

					@Override
					public void insertUpdate(DocumentEvent de) {
						update();
					}

					@Override
					public void removeUpdate(DocumentEvent de) {
						update();
					}

					public void update(){ 
						SwingUtilities.invokeLater(new Runnable(){

							@Override
							public void run() {
								LinkedList<String> founds = new LinkedList<String>(searchable.search(tc.getText()));
								
								HashSet<String> foundSet = new HashSet<String>();
								
								for (String s : founds){
									foundSet.add(s.toLowerCase());
								}
								


								Collections.sort(founds);//sort alphabetically
								setEditable(false);
								removeAllItems();

								//if founds contains the search text, then only add once.
								
								if (!foundSet.contains(tc.getText().toLowerCase()))
								{
									addItem(tc.getText());
								}
								
								for (String s : founds) 
								{
									addItem(s);
								}
								
								setEditable(true);
								setPopupVisible(true);
								tc.requestFocus();
							}
						});
					}
				});

				tc.addFocusListener(new FocusListener(){
					@Override
					public void focusGained(FocusEvent arg0) {
						if ( tc.getText().length() > 0 ){
							setPopupVisible(true);
						}
					}

					@Override
					public void focusLost(FocusEvent arg0) {						

					}
				});
			}else{
				throw new IllegalStateException("Editing component is not a JTextComponent!");
			}
		}
	}
}
