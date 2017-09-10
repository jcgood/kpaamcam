import java.io.File;
import java.nio.file.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.logging.Logger;

/**
 * Created by ning on 4/28/17.
 */
public class folderExtractor {
    public static Logger logFile=null;
    public static void main(String[] args){

        System.out.println("Please enter your name for logging");
        String username=null;
        username=System.console().readLine();
        if(username==null){
            username="defaultUser";
        }
        System.out.println("Validating system settings and program settings...");
        String currentDir=System.getProperty("user.dir");

        if(!validFileFolder()){
            System.out.println("No files folder found or no file in the folder\n Please put the 'files' folder in the current directory\n"+currentDir);
            System.exit(0);
        }
        if(!createOutputDir()){
            System.out.println("The program can't create folders on your system\n Please check if your system setting is correct \n or contact ndeng@buffalo.edu");
            System.exit(0);
        }



        System.out.println("Creating LogFile...");
        getLogFile("output", username);
        //getLogFile("output", "test");
        System.out.println("Executing Query...");

        DBReader dbr=new DBReader();
        Connection conn=null;
        while(true){
            System.out.println("--To organize files bulkily, type 'bulk'\n"+"--To organize files by fieldTrip incrementally type 'increment'\n"+
                    "--To organize files by session incrementally type 'sincrement'\n"+"--To exit this program, type 'exit'");
            String command=System.console().readLine();
            //String command="increment";
            switch (command){
                case "bulk":
                    if(!validDBFile(null)) {
                        System.out.println("No database file found\n Please put the db.sqlite3 file in the db directory under the current directory\n "+currentDir);
                        System.exit(0);
                    }
		    File oldFile=new File("db/db.sqlite3");
                    File backup=new File("oldDbBck/db.sqlite3");
                    try {
                        Files.copy(oldFile.toPath(),backup.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    }
                    catch (Exception e){
                        System.out.print("DB backup failed, please report this problem to ndeng@buffalo.edu");
                    }
                    System.out.println("Connecting Database...");
                    conn=dbr.DBConnect(null);
                    break;
                case "increment":
                    if(!validIncrementalDBFile(null,null)) {
                        System.out.println("No database file found\n Please put the the old and new db.sqlite3 file under the oldDB and newDB directories\n ");
                        System.exit(0);
                    }
                    System.out.println("Preparing databse...");
                    dbr.inCrementalMode(null, null, logFile);
                    System.out.println("Connecting Database...");
                    conn=dbr.DBConnect("diffDB/incDB.sqlite3");
                    break;
                case "sincrement":
                    if(!validIncrementalDBFile(null,null)) {
                        System.out.println("No database file found\n Please put the the old and new db.sqlite3 file under the oldDB and newDB directories\n ");
                        System.exit(0);
                    }
                    System.out.println("Preparing databse...");
                    dbr.incrementalSession(null, null, logFile);
                    System.out.println("Connecting Database...");
                    conn=dbr.DBConnect("diffDB/incSessionDB.sqlite3");
                    break;
		case "exit":
 			System.exit(0);
		    break;
                default:
                    System.out.println("undefined command");
                    break;
            }
            if(command.equals("bulk")||command.equals("increment")||command.equals("sincrement")) break;
        }


        LinkedList<SessionBasic> sessionInfo=new LinkedList<>();
        if(conn==null){
            System.out.println("Unable to connect to the database, please check if you are running this program with the dependent .jar file");
            System.exit(0);
        }

        long startTime=System.currentTimeMillis();
        ResultSet rc=dbr.fileQuery(conn);
        long endTime= System.currentTimeMillis();
        System.out.println("Query execution time:"+(endTime-startTime)+" ms");
        fileManager fm=new fileManager();
        DataAnalyzer da=new DataAnalyzer();
        try{
            System.out.println("Reorganizing files, please wait...");
            startTime=System.currentTimeMillis();
            fm.organizeFile(rc, logFile);
            endTime=System.currentTimeMillis();
            System.out.println("File reorganizing time:"+(endTime-startTime)+" ms");
            System.out.println("Reorganizing files done, the result is in the 'output' folder");

            while(true){
                System.out.println("--To get dangling file information, type 'dgfile'\n"+"--To get incomplete session information, type 'icmpsession'\n"+"--To exit this program, type 'exit'");
                String command=System.console().readLine();
                switch (command){
                    case "dgfile":
                        da.danglingFileCheck(conn,logFile);
                        break;
                    case "icmpsession":
                        da.inCompleteSession(conn,logFile,sessionInfo);
                        break;
                    case "exit":
                        break;
                    default:
                        System.out.println("undefined command");
                        break;
                }
                if(command.equals("exit")) break;
            }

            System.exit(0);
        }
        catch (Exception e){
            logFile.severe(e+"");
        }
    }
    private static boolean validDBFile(String path){
        if(path==null) path="db/db.sqlite3";
        File dbFile=new File(path);
        if(!dbFile.isFile()){
           return false;
        }
        return true;
    }
    private static boolean validFileFolder(){
        File fileFolder=new File("files/app");
        if(!fileFolder.isDirectory()){
            return false;
        }
        try{
           Path path= Paths.get("files/app");
            DirectoryStream<Path> dirStream = Files.newDirectoryStream(path);
            if(!dirStream.iterator().hasNext()) {
                dirStream.close();
                return false;
            }
            dirStream.close();
        }
        catch (Exception e){
            return false;
        }
        return true;
    }
    private static boolean createOutputDir(){
        File outPut=new File("output");
        if(!outPut.isDirectory()){
            if(!outPut.mkdir()){
                return false;
            }
        }
        return true;
    }
    private static void getLogFile(String path, String userName){
        Log lg=new Log();
        logFile=lg.createLog(path, userName);
    }
    private static boolean validIncrementalDBFile(String path1, String path2){
        if(path1==null) path1="oldDb/db.sqlite3";
        File oldDBFile=new File(path1);
        if(!oldDBFile.isFile()){
            return false;
        }
        if(path2==null) path2="newDb/db.sqlite3";
        File newDBFile=new File(path2);
        if(!newDBFile.isFile()){
            return false;
        }
        return true;
    }

}
