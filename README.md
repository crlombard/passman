# passman
Password Management Software

Beta v0.04 : 11/05/2017


Introduction:
passman is a GUI based password management system. 

It will run on any system that is supported by the Java Runtime
Environment(JRE).



Use:
With the JRE installed, you have two options to launch the software. For Windows
users, simply double click on the "passman.bat" script, and the program should
launch automagically. The program may be launched on any OS by executing
    "java -jar passman.jar"
from a command line interface opened to the directory containing the program
files.


Misc:
Each time the application is run with no file entitled "passman.db" in its
directory, a new SQLite database is created. If you want to make a new database,
remove the "passman.db" file from the directory.
