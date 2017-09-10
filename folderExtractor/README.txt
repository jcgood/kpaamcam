For using this program:
1. Launch your terminal tool and navigate using cd command to the current directory, e.g. cd ~/Desktop/folderExtractor 
2. Compile the program using following javac command:
	javac *.java
3. Run the program copy and paste the following command to your terminal and hit Enter:
        java -classpath ".:libs/sqliteDB.jar" folderExtractor

(
Prerequisites: 
	1. Download the module from the FAIMS server, unzip it.
	2. To run bulky file organization, copy the db.sqlite in the unzipped folder of the module to the 'db' directory.
	3. To run incremental file organization, copy the  db.sqlite in the unzipped folder of the module to the 'newDb' directory, and copy the old db file to oldDb, if you don't have a backup of the old database file, you may want to check if there's a auto backup in oldDbBck folder.
	4. Copy the files folder in the unzipped folder of the module to the current directory.
	5. You need to have jdk1.8 to run this program. 
)
