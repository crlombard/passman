# passman
Password Management Software

passmanGUI Beta v0.03 : 2/05/2017
readme.txt user instruction file.

Distribution:

This package should include three files; "readme.txt", "passman.bat", and "passman.jar". Upon execution of the program the 
first time, a fourth file will be created, entitled "passman.db". The .jar and .db files must remain together to work properly. If you remove the .db from the directory containing the .jar, and run the .jar, the program will generate a new .db. Note that "passman.bat" is just a simple batch script which runs, verbatim, "java -jar passman.jar". You can easily modify this to suit your liking. For example, I have my .jar and .db buried in my file structure, but have a shell script on my desktop for ease of access.

Introduction:

passmanGUI is a GUI based password management system designed to create a simple, secure GUI environment for the management 
of passwords. Use of this software comes at your own risk, I make no guarantees as to its security or continued support, though I have done my best to ensure security as much as possible as of the time of this distribution (2/05/2017). The reader should be advised that I am still very much a student, and that this software is my first project of any scope to date, so don't store your nuclear launch codes in here. I wouldn't use it in enterprise yet.

passmanGUI will run on any system that is supported by the Java Runtime Environment(JRE), which is required for the use of this software. passmanGUI also makes use of SQLite to store data, AES 128 bit CBC encryption, and PBKDF2 with SHA1 for hashing. I'm looking at potentially using Serpent or Threefish encryption at some point in the future, I'm not crazy about AES, especially 128 bit AES.

Use:

With the JRE installed, you have two options to launch the software. For Windows users, simply double click on the "passman.bat" script, and the program should launch automagically. The program may be launched on any OS by executing "java -jar passman.jar" from a command line interface opened to the directory containing the program files.

Misc:

Each time the application is run with no file entitled "passman.db" in its directory, a new SQLite database is created. If you 
want to make a new database, remove the "passman.db" file from the directory.

Source code is also available for review and repackaging at your discretion. Do what you will with the software, it's free,
intended as a largely academic exercise. Any input regarding the source code is welcome.

For added security confidence, nest the whole program in a Veracrypt volume. At that point, the program could be saving data cleartext and you'd still be safe. Defence in depth and all that.
