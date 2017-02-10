passmanGUI Beta v0.01 : 1/4/2017
readme.txt user instruction file.

Distribution:
This package should include three files; "readme.txt", "passman.bat", and "passman.jar". Upon execution of the program the first time, a fourth file will be created, entitled "passman.db". ALL THESE FILES MUST REMAIN IN THE SAME DIRECTORY. They may be placed anywhere in the file structure, but must remain together. I recommend placing all files in a folder entitled "passman" and placing this folder in the desired location in the file structure.

Introduction:
passmanGUI is a GUI based password management system designed to create a simple, secure GUI environment for the management of passwords. Use of this software comes at your own risk, I make no guarantees as to its security or continued support, though I have done my best to ensure security as much as possible as of the time of this distribution (1/4/2017).

passmanGUI will run on any system that is supported by the Java Runtime Environment(JRE), which is required for the use of this software. If you are unsure whether you have the JRE or not, follow these steps:

1. Open a command terminal.
2. Run the following command: "java -version". If "java version x.xx" is returned (where x is some number), then you're good to go, the JRE is installed. Skip #3. Otherwise...
3. If the prompt returns something to the effect of "program not found", the JRE is either not installed or not configured properly in your classpath. See Oracle's documentation of their product for the repair of this issue. NOTE: I recommend just reinstalling the JRE, inexperienced users should not make adjustments to the classpath.

Launching:
With the JRE installed, you have two options to launch the software. For Windows users, simply double click on the "passman.bat" script, and the program should launch automagically. The program may be launched on any operating system (OS) by executing "java -jar passman.jar" from a command line interface (CLI) opened to the directory containing the program files.
