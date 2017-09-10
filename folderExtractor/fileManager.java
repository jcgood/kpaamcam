/**
 * Created by ning on 4/28/17.
 */
import java.nio.file.*;
import java.io.*;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class fileManager {

    public static final String defaultOutputPath="output/";

    public void organizeFile(ResultSet res, Logger logFile){
        int ftCount=0;
        int ftFailCount=0;
        int sessionCount=0;
        int sssFailCount=0;
        int fileCount=0;
        int fileFailCount=0;
        HashMap<String, String> reNamedFiles=new HashMap<>();
        System.out.println("Reorganizing files, please wait...");
        try {
            while (res.next()) {
                char ftCreate=createFieldTripFolder(res.getString("FTid"), res.getString("FTLabel"), logFile);
                switch (ftCreate){
                    case 'c': ftCount++;
                        break;
                    case 'e': ftFailCount++;
                        break;
                }
                String ftPath=defaultOutputPath+res.getString("FTLabel")+res.getString("FTid");
                char sssCreate=createSessionFolder(ftPath,res.getString("Sid"),res.getString("Sname"),logFile);
                switch (sssCreate){
                    case 'c': sessionCount++;
                        break;
                    case 'e': sssFailCount++;
                        break;
                }
                String dirPath=ftPath+"/"+res.getString("Sname")+res.getString("Sid");
                char fileOp=moveRenameFile(dirPath,res.getString("Fid"),res.getString("Fname"), res.getString("FPath"),reNamedFiles,logFile);
                switch (fileOp){
                    case 'f':
                        if(logFile!=null){
                            logFile.log(Level.ALL,"No file format error:"+res.getString("Fname")+"  "+res.getString("FPath")+"  "+res.getString("Fid"));
                        }
                        break;
                    case 'c': fileCount++;
                        break;
                    case 'e': fileFailCount++;
                        break;
                }
            }
        }
        catch (Exception e){
            if(logFile!=null) {
                logFile.severe("File operation failure due to "+e);
            }
            System.out.println("Reorganizing files, please wait...");
        }
        logFile.info("Total "+ ftCount +" field trip directories created\n"+"Failed to create total " +ftFailCount+ " fieldTrip directories");
        logFile.info("Total "+ sessionCount +" session directories created\n"+"Failed to create total " +sssFailCount+ " session directories");
        logFile.info("Total "+ fileCount +" file records scanned and reorganized\n"+"Failed to move total " +fileFailCount+ " files\n"+ "Total "+reNamedFiles.size()+" files renamed");
    }

    private char createFieldTripFolder(String id, String label, Logger logFile){
        String folderName=defaultOutputPath+label+id;
        try{
            File newFile=new File(folderName);
            if(!newFile.exists()){
                if(newFile.mkdir()){
                    return 'c';
                }
                else{
                   return 'e';//exist
                }
            }
            else{
                return 'd';
            }
        }
        catch (Exception e){
            if(logFile!=null) {
                logFile.severe("FT folder creation failed due to "+e+" "+label);
            }
            System.out.println("Reorganizing files, please wait...");
           return 'e';//error
        }
    }

    private char createSessionFolder(String ftPath, String id, String label, Logger logFile){
        String folderName=label+id;
        String path=ftPath+"/"+folderName;

        try{
            File newFile=new File(path);
            if(!newFile.exists()){
                if(newFile.mkdir()){
                    return 'c';
                }
                else{
                    return 'e';
                }
            }
            else{
                return 'd';
            }
        }
        catch (Exception e){
            if(logFile!=null) {
                logFile.log (Level.ALL,"Error when create directory :"+e);
            }
            System.out.println("Reorganizing files, please wait...");
            return 'e';
        }
    }

    private char moveRenameFile(String dirPath, String id, String label, String path, HashMap<String, String> reNamedFiles, Logger logFile){
        String oldFileName=path;
        String fileExtension=getFileExtension(oldFileName);
        if(fileExtension==null) return 'f';

        String newFileTmpPath=dirPath+"/"+label;
        String filePath=checkDupFile(newFileTmpPath, fileExtension, logFile, reNamedFiles, id);
        try{
            File fileFolder=new File(dirPath);
            if(!fileFolder.exists()){
                if(fileFolder.mkdir()) {
                    System.out.println(dirPath + " " + "folder is created");
                }
            }
            File source=new File(oldFileName);
            File dest=new File(filePath);
            try {
                Files.move(source.toPath(), dest.toPath());
                return 'c';
            }
            catch (IOException ioe){
                if(logFile!=null) {
                    logFile.severe("File copy failed due to "+ioe+" "+filePath);
                }
                System.out.println("Reorganizing files, please wait...");
                return 'e';
            }

        }
        catch (Exception e){
            if(logFile!=null) {
                logFile.severe("File operation failure due to "+e);
            }
            System.out.println("Reorganizing files, please wait...");
            return 'e';
        }
    }
    private String getFileExtension(String filePath){
        int i=filePath.lastIndexOf(".");
        if (i>0){
            return filePath.substring(i);
        }
        return null;
    }

    private String checkDupFile(String path, String extension, Logger logFile, HashMap<String, String> renamedFile, String fileId){
        int suffix=1;
        boolean dupFile=false;
        if(new File(path+extension).isFile()){
            dupFile=true;
            //rename existing file
            File file=new File(path+extension);
            File renameFile=new File(path+"(1)"+extension);

            if(!file.renameTo(renameFile)){
                if(logFile!=null) {
                    logFile.severe("File renaming failed on"+path+extension);
                }
                System.out.println("Reorganizing files, please wait...");
            }
            else{
                renamedFile.put(path+extension, path+"(1)"+extension);
            }
        }
        else if(new File(path+"(1)"+extension).isFile()){
            dupFile=true;
        }
        if(dupFile){
            while(true){
                suffix++;
                String tmpPath=path+"("+suffix+")"+extension;
                if(!new File(tmpPath).isFile()){
                    break;
                }
            }
            renamedFile.put(fileId,path+"("+suffix+")"+extension);
            return path+"("+suffix+")"+extension;
        }
        else{
            return path+extension;
        }

    }
    private void writeRenamedFile(HashMap<String, String> renamedFile,  Logger logFile){
        File renamedFileSummary=new File("output/renamedFiles.txt");
        try {
            if(!renamedFileSummary.isFile()){
                renamedFileSummary.createNewFile();
            }
            FileOutputStream fos=new FileOutputStream(renamedFileSummary);
            BufferedWriter bw=new BufferedWriter(new OutputStreamWriter(fos));
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            java.util.Date date = Calendar.getInstance().getTime();
            String dateInfo=dateFormat.format(date);
            bw.write(dateInfo);
            bw.newLine();
            for(String key: renamedFile.keySet()){
                bw.write("key: "+renamedFile.get(key));
                bw.newLine();
            }
            bw.close();
            fos.close();
        }
        catch (Exception e){
            if(logFile!=null) {
                logFile.severe("error writing file renamedFiles.txt"+e);
            }
            System.out.println("Reorganizing files, please wait...");
        }


    }
    /*
    public static void main(String [] args){
        fileManager fm=new fileManager();
        DBReader dr=new DBReader();
        Connection conn=dr.DBConnect(null);
        Log lg=new Log();
        Logger logFile=lg.createLog("output", "default");
        ResultSet rc=dr.fileQuery(conn);
        fm.organizeFile(rc, logFile);
    }
*/
}
