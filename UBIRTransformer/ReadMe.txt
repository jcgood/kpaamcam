or using this program:
1. Launch your terminal tool and navigate using cd command to the current directory, e.g. cd ~/Desktop/folderExtractor 
2. Compile the program using following javac command:
	javac -Xlint:unchecked -classpath ".:libs/json-simple-1.1.jar" *.java
3. Run the program copy and paste the following command to your terminal and hit Enter:
        java -classpath ".:libs/json-simple-1.1.jar;.:libs/sqlite-jdbc-3.18.0.jar" DataTransformer 

(
Prerequisites: 
	1. Download the module from the FAIMS server, unzip it.
	2. Put the db.sqlite3 to the /db/ folder
	3. Copy all the files under files/app folder in the unzipped folder to the /files/ directory.
	4. You need to have jdk1.8 to run this program. 
)
 



