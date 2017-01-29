/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package passman;

import java.util.Scanner;

/*
 *  @author ironcrown.crl
 */
public class passmanCLI 
{
    /*
    *   @param  None.
    *   @pre    None.
    *   @post   Main Menu is printed to the command line.
    */
    public static void printMainMenu()
    {
        System.out.print("Select an option below. Entering 'exit' at any point will "
            +"return you to the previous menu.\n'exit' at the main menu "
            +"will close the application.\n\n");
        System.out.print("1: Search for an entry\n");
        System.out.print("2: Create an entry\n");
		System.out.print("3: Display current entries\n");
		System.out.print("4: Select entry by cell\n");
        System.out.print("-->");
    }
    
    public static void printToCLI(String in)
    {
        System.out.print(in);
    }
    
    public static void printToCLI(char in)
    {
        System.out.print(in);
    }
	
	
	private static entryList fullList = new entryList(); //Class entryList variable for storing entry data.
	public static passmanCLI cout = new passmanCLI();
	
	public static void modOptions(int locTags)
	{
		boolean repeatFlag = true;
		do
		{
			Scanner sc = new Scanner(System.in);
			System.out.print("\nEntry found.\n1. View entry.\n2. Modify entry.\n3. Remove entry.\n-->");
			String viewModRemFlag = sc.nextLine();
			System.out.println("");

			/*******************************************************************
			 * Display the selected entry if user enters 1.
			 ******************************************************************/
			if(viewModRemFlag.equals("1")) {
				fullList.get(locTags).displayEntry(); 
				//System.out.println("");
			}

			/*******************************************************************
			 * Modify selected entry if user enters 2.
			 ******************************************************************/
			else if(viewModRemFlag.equals("2")) 
			{
				//Display current entry.
				System.out.print("Current Entry:\n");
				fullList.get(locTags).displayEntry();  //Display current entry.
				System.out.println("");

				/**
				 * Get user choice. Allow three options; replace only the title,
				 * replace only the entry, or modify the tags. Modify tags will open
				 * a sub-menu that will allow the choice to add a tag, or remove a tag.
				 * Iterate until user enters exit.
				 */
				String userModChoice = "";
				do{
					//Print menu, get user choice.
					System.out.print("1. Replace title.\n2. Replace entry.\n3. Modify tags.\n-->");
					userModChoice = sc.nextLine();
					System.out.println("");
					
					switch(userModChoice)
					{
					/**/case"1":	//Replace title case.
							System.out.print("Enter a title for the entry:\n-->");
							String userTitle = sc.nextLine(); //Get title of new entry.
							System.out.println("");
							
							//Display new proposed entry.
							System.out.print("The proposed new entry is as follows:\n");
							System.out.println("title: " +userTitle);
							System.out.print("entry: ");
							for(int i = 0; i < fullList.get(locTags).getSize(); i++)
							{
								char[] temp = fullList.get(locTags).getEntry();
								System.out.print(temp[i]);
							}
							System.out.println("");
							System.out.println("tags: " +fullList.get(locTags).getTags());
							
							//Confirm new proposed entry.
							System.out.print("\nConfirm entry replacement? You must still"
											+ "\ncommit the modified data to the file before"
											+ "\nexiting the program. Replace current entry? (y/n)\n-->");
									
							char confirmTitleEntryFlag = sc.next().charAt(0); sc.nextLine();
							System.out.println("");
							
							if(confirmTitleEntryFlag == 'y' || confirmTitleEntryFlag == 'Y')
							{
								//Set entry to old entry and new title.
								fullList.get(locTags).setEntry(fullList.get(locTags).getEntry(), userTitle);

								System.out.println("Title replacement successful.");
								//Return to main menu.
							}
							else
							{
								System.out.println("Modification aborted.");
								userModChoice = "exit"; //Exit to preceding menu.
							}
							break;
					/**/case"2":	//Replace entry case.
							System.out.print("Enter the new entry:-->");

							/**
							* THIS NEEDS TO BE REPLACED BY A CHAR ARRAY.
							*/
							String userEntryString = sc.nextLine(); //Get entry.
							char[] userEntry = userEntryString.toCharArray();
							System.out.println("");
							
							//Display new proposed entry.
							System.out.print("The proposed new entry is as follows:\n");
							System.out.println("title: " +fullList.get(locTags).getTitle());
							System.out.print("entry: ");
							for(int i = 0; i < userEntry.length; i++)
							{
								System.out.print(userEntry[i]);
							}
							System.out.println("");
							System.out.println("tags: " +fullList.get(locTags).getTags());
							
							//Confirm new proposed entry.
							System.out.print("\nConfirm entry replacement? You must still"
											+ "\ncommit the modified data to the file before"
											+ "\nexiting the program. Replace current entry? (y/n)\n-->");
									
							char confirmEntryFlag = sc.next().charAt(0); sc.nextLine();
							System.out.println("");
							
							if(confirmEntryFlag == 'y' || confirmEntryFlag == 'Y')
							{
								//Set entry to old entry and new title.
								fullList.get(locTags).setEntry(userEntry, fullList.get(locTags).getTitle());

								System.out.println("Title replacement successful.");
								//Return to main menu.
							}
							else
							{
								System.out.println("Modification aborted.");
								userModChoice = "exit"; //Exit to preceding menu.
							}
							
							break;
					/**/case"3":	//Modify tags case.
							System.out.print("1. Add tag.\n2. Remove tag.\n-->");
							String userSubmodChoice = sc.nextLine();
							System.out.println("");
							
							boolean subcaseThreeLoopFlag = false;
							do{
								switch(userSubmodChoice)
								{
									case"1": //Add tag case.
										System.out.print("Enter the tag you wish added:\n-->");
										String addedTag = sc.nextLine();
										
										fullList.get(locTags).addTag(addedTag);
										System.out.print("\nTag added successfully.\n");
										subcaseThreeLoopFlag = false; break;
										
									case"2": //Remove tag case.
										System.out.print("Enter the tag you wish removed:\n-->");
										String removalTag = sc.nextLine();
										
										boolean tagRemovalSuccessFlag = fullList.get(locTags).removeTag(removalTag);
										if(tagRemovalSuccessFlag)
										{
											System.out.print("\nTag removed successfully.\n");
										}
										else
										{
											/*
											 * THIS IS UNCONTROLLED
											 */
											System.out.print("\nUNCONTROLLED TAG REMOVAL FAILURE");
										}
										subcaseThreeLoopFlag = false; break;
										
									case"exit":
										subcaseThreeLoopFlag = false; break;
										
									default:
										System.out.print("Invalid entry. Re-enter.\n"); break;
								}
							}while(subcaseThreeLoopFlag);

							break;
					/**/case"exit":
							break;
					/**/default:
							System.out.print("INVALID ENTRY. RE-ENTER.\n"); break;
					}
				}while(!userModChoice.equals("exit"));
			}

			/*******************************************************************
			 * Remove an entry if user enters 3.
			 ******************************************************************/
			else if(viewModRemFlag.equals("3")) {
				System.out.print("Are you sure you wish to remove this entry? (y/n):\n-->");
				String continueToRemove = sc.nextLine();
				
				if(!(continueToRemove.equals("y") || continueToRemove.equals("Y")))
				{
					//User wishes to abort, so back up one menu.
					
				}
				else
				{
					boolean removalStatusFlag = fullList.removeEntry(fullList.get(locTags));
					if(removalStatusFlag)
					{
						System.out.print("Entry removed successfully. Returning to main menu.\n\n");
						repeatFlag = false; break;
					}
					else
					{
						/**
						 *	THIS IS AN UNCONTROLLED EXCEPTION
						 */
						System.out.print("UNCONTROLLED REMOVAL ERROR\n");
					}
				}
			}
			else if(viewModRemFlag.equals("exit"))
			{
				repeatFlag = false; break;
			}
		}while(repeatFlag);
	}

    public static void loopSearchMenuOptions() throws userPromptedBreakException
    {
		passmanCLI cli = new passmanCLI();
        Scanner sc = new Scanner(System.in);
        
        System.out.println("1: searchByTags");
        System.out.println("2: searchByTitle");
		System.out.print("-->");
        String stringSearchChoice = sc.nextLine();
		if(stringSearchChoice.equals("exit"))
		{
			//Break to main menu.
			throw new userPromptedBreakException("");
		}
		int searchChoice = Integer.parseInt(stringSearchChoice);
		System.out.println("");
        boolean repeatFlag = false;
             
        do
        {
            switch(searchChoice)
            {
                case 1:
					boolean caseOneLoopFlag = true;
					do
					{
						System.out.println("Enter a tag to search by:");
						//System.out.println("Enter tags to search by, separated by commas:");
						//sc.nextLine();  //Skip the last newline.
						String tagStr = sc.nextLine(); //Get user input of tags.

						//Process and error handle tags.
						int locTags = fullList.searchByTags(tagStr);
						if(locTags != -1)
						{
							modOptions(locTags);
						}
						else
						{
							System.out.print("Entry not found.");
						}

						System.out.print("Search again? (y/n)\n-->");
						char loopChoice = sc.next().charAt(0); //Get next char.
						System.out.println("");

						if(loopChoice == 'y' || loopChoice == 'Y')
						{
							continue;
						}
						else
						{
							caseOneLoopFlag = false; //Set flag to break from caseTwo loop.
						}
					}while(caseOneLoopFlag);
                    System.out.println("Enter a tag to search by:");
                    //System.out.println("Enter tags to search by, separated by commas:");
                    //sc.nextLine();  //Skip the last newline.
                    String tagStr = sc.nextLine(); //Get user input of tags.
                    
                    //Process and error handle tags.
                    
                    int locTags = fullList.searchByTags(tagStr);
					if(locTags != -1)
					{
						modOptions(locTags);
					}
					else
					{
						System.out.print("Entry not found.");
					}
						
					System.out.print("Search again? (y/n)\n-->");
					char loopChoice = sc.next().charAt(0); //Get next char.
					sc.nextLine(); //Eat the '\n' char.
					System.out.println("");
						
					if(loopChoice == 'y' || loopChoice == 'Y')
					{
						continue;
					}
					else
					{
						caseOneLoopFlag = false; //Set flag to break from caseTwo loop.
					}

                    break;
                case 2:
					boolean caseTwoLoopFlag = true;
					
					//caseTwo do-while loop.
					do
					{
						System.out.print("Enter a title to search by:\n-->");
						//sc.nextLine();  //Skip the last newline.
						String titleStr = sc.nextLine(); //Get user input of tags.

						//Process and error handle title.

						int locTitle = fullList.searchByTitle(titleStr);

						if(locTitle != -1)
						{
							modOptions(locTitle);
						}
						else
						{
							System.out.print("Entry not found.");
						}
						
						System.out.print("Search again? (y/n)\n-->");
						loopChoice = sc.next().charAt(0); //Get next char.
						sc.nextLine(); //Eat the '\n' char.
						System.out.println("");
						
						if(loopChoice == 'y' || loopChoice == 'Y')
						{
							continue;
						}
						else
						{
							caseTwoLoopFlag = false; //Set flag to break from caseTwo loop.
						}
					}while(caseTwoLoopFlag); //End caseTwo do-while loop.
					break; //Break switch statement.
                default:
                    System.out.println("INVALID ENTRY. RE-ENTER:\n"); 
                    repeatFlag = true; break;
            }
        }while(repeatFlag);
    }
    
    public static void loopMainMenuOptions()
    {
        passmanCLI cli = new passmanCLI();
        Scanner sc = new Scanner(System.in);
        boolean repeatFlag = true;
        do
        {
            cli.printMainMenu(); //Prints main menu.
            String choice = sc.nextLine();
			System.out.println("");
            switch(choice)
            {
                case "1":
					try{
						loopSearchMenuOptions();
					}
					catch(userPromptedBreakException e)
					{
						//Do nothing. This exception is simply used to move up the stack.
					}
                    break;
                case "2":
					//Get new data.
					System.out.print("Enter the title of the new entry:\n-->");
					String newTitle = sc.nextLine();
					System.out.print("\nEnter the new entry:\n-->");
					String newEntry = sc.nextLine();
					System.out.println("");
					
					//Add new data to fullList.
                    fullList.addEntry(newEntry.toCharArray(),newTitle);
					
					//Print success message.
					System.out.print("Entry added successfully.\n\n");
                    break;
				case "3":
					System.out.print("Current contents of database:\n");
					fullList.displayListTitles(); 
					System.out.print("\n");
					
					break;
				case "4":
					System.out.print("Enter cell number to select:\n-->");
					int cellChosen = sc.nextInt(); sc.nextLine();//Eat '\n' char.
					
					modOptions(cellChosen);
					break;
                case "exit":
                    System.out.println("Exiting!"); 
                    repeatFlag = false; break;
                default:
                    System.out.println("INVALID ENTRY. RE-ENTER:\n"); 
                    break;
            }
        }while(repeatFlag);
    }
	
}
