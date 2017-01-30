# passman
Password Management Software

passmanGUI Beta v0.02 : 1/29/2017
readme.txt user instruction file.

Distribution:
This package should include three files; "readme.txt", "passman.bat", and "passman.jar". Upon execution of the program the 
first time, a fourth file will be created, entitled "passman.db". ALL THESE FILES MUST REMAIN IN THE SAME DIRECTORY. 
They may be placed anywhere in the file structure, but must remain together. I recommend placing all files in a folder 
entitled "passman" and placing this folder in the desired location in the file structure.

Introduction:
passmanGUI is a GUI based password management system designed to create a simple, secure GUI environment for the management 
of passwords. Use of this software comes at your own risk, I make no guarantees as to its security or continued support, though 
I have done my best to ensure security as much as possible as of the time of this distribution (1/29/2017). The reader should
be advised that I am still very much a student, and that this software is my first project of any scope to date.

passmanGUI will run on any system that is supported by the Java Runtime Environment(JRE), which is required for the use of this 
software. passmanGUI also makes use of SQLite to store data, AES 128 bit ECB encryption, and PBKDF2 with SHA1 for hashing. 
I am currently working on changing the encryption used to AES 256 bit CBC encryption, and looking at potentially using Threefish
at some point in the future. Do some reading on why ECB encryption is bad before you use this software in production, and consider
the implications its implementation has on my cryptographic expertise.

Use:
With the JRE installed, you have two options to launch the software. For Windows users, simply double click on the "passman.bat" 
script, and the program should launch automagically. The program may be launched on any operating system (OS) by executing 
"java -jar passman.jar" from a command line interface (CLI) opened to the directory containing the program files.

Each time the application is run with no file entitled "passman.db" in its directory, a new SQLite database is created. If you 
want to make a new database, remove the "passman.db" file from the directory.

Source code is also available for review and repackaging at your discretion. Do what you will with the software, it's free,
intended as a largely academic exercise. Any input regarding the source code is welcome.
