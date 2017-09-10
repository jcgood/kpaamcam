/**
 * Created by ning on 4/28/17.
 */
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.*;
import java.util.logging.Logger;


public class DBReader {
    public Connection DBConnect(String path){
        try{
            Class.forName("org.sqlite.JDBC");// register the driver
            String connMainString="jdbc:sqlite:";
            String connString=connMainString;
            if(path==null || path.length()==0) connString+="db/db.sqlite3";
            else connString+=path;
            Connection conn=DriverManager.getConnection(connString);
            if(conn==null) System.out.println("Failed to connect to DB");
            return conn;
        }
        catch (Exception e){
            System.out.println(e);
            return null;
        }
    }

    public ResultSet fileQuery(Connection conn){

        String query="SELECT FTid, FTLabel, SSS.Sid as Sid, Sname, Alabel, Fid, FName,FPath, FTime FROM "+
                "(SELECT FTid, FTLabel, Sid FROM "+
                "(SELECT FTid, FTLabel, relnid FROM "+
                "(SELECT uuid AS FTid, measure AS FTLabel FROM latestNonDeletedArchEntIdentifiers WHERE AttributeID =(SELECT AttributeID FROM AttributeKey WHERE AttributeName='FieldTripID')) FT "+
                "INNER JOIN "+
                "(SELECT uuid AS rlid, RelationshipID as relnid from AEntReln "+
                "WHERE RelationshipID IN "+
                "(SELECT RelationshipID FROM latestNonDeletedRelationship WHERE RelnTypeID=(SELECT RelnTypeID FROM RelnType WHERE RelnTypeName='Session and FieldTrip'))) rnSession "+
                "ON rnSession.rlid=FT.FTid) FTsessionReln "+
                "INNER JOIN "+
                "(SELECT relnid, Sid From "+
                "((SELECT uuid AS Sid FROM latestNonDeletedArchEntIdentifiers WHERE AttributeID =(SELECT AttributeID FROM AttributeKey WHERE AttributeName='SessionID')) sss "+
                "INNER JOIN "+
        "(SELECT uuid AS rlid, RelationshipID as relnid from AEntReln WHERE RelationshipID IN (SELECT RelationshipID FROM latestNonDeletedRelationship WHERE RelnTypeID=(SELECT RelnTypeID FROM RelnType WHERE RelnTypeName='Session and FieldTrip'))) rlnSss "+
        "ON sss.Sid=rlnSss.rlid)) session "+
        "ON FTsessionReln.relnid=session.relnid) FTSSS "+
        "INNER JOIN "+
        "(SELECT Sid, Sname, Alabel, Fid, FName,FPath, FTime FROM "+
                "(SELECT Sid, Sname, Aid, Alabel FROM "+
                "(SELECT Sid, Sname, relnid FROM "+
                "(SELECT uuid AS rlid, RelationshipID as relnid from AEntReln WHERE RelationshipID "+
                "IN (SELECT RelationshipID FROM latestNonDeletedRelationship WHERE RelnTypeID=(SELECT RelnTypeID FROM RelnType WHERE RelnTypeName='Answer and Session'))) rnSession "+
                "INNER JOIN  "+
                "(SELECT uuid AS Sid, measure AS Sname FROM latestNonDeletedArchEntIdentifiers WHERE AttributeID =(SELECT AttributeID FROM AttributeKey WHERE AttributeName='SessionID')) session "+
                "ON rnSession.rlid=session.Sid) sessionReln "+
                "INNER JOIN "+
                "(SELECT relnid, Aid, Alabel From (SELECT uuid AS rlid, RelationshipID as relnid  from AEntReln  WHERE RelationshipID IN "+
                "(SELECT RelationshipID FROM latestNonDeletedRelationship   WHERE RelnTypeID=(SELECT RelnTypeID FROM RelnType WHERE RelnTypeName='Answer and Session'))) rnAnswer "+
                "INNER JOIN "+
                "(SELECT uuid AS Aid, measure AS Alabel FROM latestAllArchEntIdentifiers "+
                    "WHERE latestAllArchEntIdentifiers.AttributeID=(select AttributeID from AttributeKey where AttributeName='AnswerLabel')) answer "+
                "ON rnAnswer.rlid=answer.Aid) AnsReln "+
                "ON sessionReln.relnid=AnsReln.relnid) SessionAns "+
                "INNER JOIN "+
                "(SELECT Aid, Fid, FName, FPath, FTime FROM "+
                "(SELECT FileInfo.Fid, FName, FPath, FTime, frelnid from (SELECT uuid AS frlid, RelationshipID as frelnid from AEntReln WHERE RelationshipID IN "+
                "(SELECT RelationshipID FROM latestNonDeletedRelationship   WHERE RelnTypeID=(SELECT RelnTypeID FROM RelnType WHERE RelnTypeName='Answer and File'))) freln "+
                "INNER JOIN "+
                "(Select fBasic.Fid, FName, FPath, FTime FROM "+
                "(SELECT uuid as Fid, measure As FName,  ValueTimeStamp as FTime FROM latestNonDeletedArchEntIdentifiers "+
                "WHERE AttributeID =(SELECT AttributeID FROM AttributeKey WHERE AttributeName='FileID')) fBasic "+
                "INNER JOIN "+
                "(SELECT uuid as Fid, measure As FPath FROM latestNonDeletedAentValue WHERE AttributeID =(SELECT AttributeID FROM AttributeKey WHERE AttributeName='FileContent')) fPath "+
                "ON fBasic.Fid=fPath.Fid) FileInfo ON FileInfo.Fid=freln.frlid) FileReln "+
                "INNER JOIN (SELECT relnid , Aid From (SELECT uuid AS rlid, RelationshipID as relnid  from AEntReln  WHERE RelationshipID IN "+
                "(SELECT RelationshipID FROM latestNonDeletedRelationship   WHERE RelnTypeID=(SELECT RelnTypeID FROM RelnType WHERE RelnTypeName='Answer and File'))) rnAnswer "+
                "INNER JOIN "+
                "(SELECT uuid AS Aid FROM latestAllArchEntIdentifiers  WHERE  latestAllArchEntIdentifiers.AttributeID=(select AttributeID from AttributeKey where AttributeName='AnswerLabel')) answer "+
                "ON rnAnswer.rlid=answer.Aid) ansFile ON FileReln.frelnid =ansFile.relnid) AnswerFile "+
                "ON SessionAns.Aid=AnswerFile.Aid) SSS "+
                "ON FTSSS.Sid=SSS.Sid ";
        try{
            PreparedStatement statement=conn.prepareStatement(query);
            statement.setFetchSize(200);
            try {
                // todo: extremely long time scanning here
                ResultSet rcFile = statement.executeQuery();
                rcFile.setFetchSize(200);
                return rcFile;
            }
            catch(Exception e){
                System.out.print(e+"");
            }
        }
        catch(Exception e){
            System.out.print(e+"");
        }
        return null;
    }
    /**
     * ** For incremental query
     * @param oldFile: old sqlite3 file
     * @param newFile: new sqlite3 file
     */
    public void inCrementalMode(String oldFile, String newFile, Logger logFile){
        createDiffDB(logFile,"diffDB/incDB");
        try{
            Class.forName("org.sqlite.JDBC");// register the driver
            String connMainString="jdbc:sqlite:";
            String connStringOld=oldFile==null?"oldDb/db.sqlite3": oldFile;
            String connStringNew=connMainString+(newFile==null?"diffDB/incDB.sqlite3": newFile);
            Connection connNew=DriverManager.getConnection(connStringNew);
            connNew.prepareStatement("ATTACH DATABASE \""+connStringOld+"\" AS oldDB;").execute();

            String deleteOldAentEntityDataQuery="DELETE FROM main.ArchEntity WHERE main.ArchEntity.uuid IN (SELECT uuid FROM oldDB.ArchEntity)";

            String deleteOldAentDataQuery="DELETE FROM main.AentValue WHERE main.AentValue.UUID IN (SELECT UUID FROM oldDB.AentValue)";
            String deleteOldAentRelnDataQuery="DELETE FROM main.AEntReln WHERE main.AEntReln.uuid IN (SELECT uuid FROM oldDB.AEntReln)";
            String deleteOldRelnDataQuery="DELETE FROM main.Relationship WHERE main.Relationship.RelationshipID IN (SELECT RelationshipID FROM oldDB.Relationship)";
            String deleteOldFileDataQuery="DELETE FROM main.File WHERE main.File.filename IN (SELECT filename FROM oldDB.File)";

            PreparedStatement ps1=null, ps2=null, ps3=null, ps4=null, ps5=null;
            connNew.setAutoCommit(false);
            ps1=connNew.prepareStatement(deleteOldAentEntityDataQuery);
            ps2=connNew.prepareStatement(deleteOldAentDataQuery);
            ps3=connNew.prepareStatement(deleteOldAentRelnDataQuery);
            ps4=connNew.prepareStatement(deleteOldRelnDataQuery);
            ps5=connNew.prepareStatement(deleteOldFileDataQuery);

            ps1.executeUpdate();
            ps2.executeUpdate();
            ps3.executeUpdate();
            ps4.executeUpdate();
            ps5.executeUpdate();

            connNew.commit();
            rmFiles(connNew, null, logFile);
            connNew.close();
        }
        catch (Exception e){
            if(logFile!=null) logFile.severe(e+"");
            return;
        }
    }
    public void incrementalSession(String oldFile, String newFile, Logger logFile){
        createDiffDB(logFile, "diffDB/incSessionDB.sqlite3");
        try{
            Class.forName("org.sqlite.JDBC");// register the driver
            String connMainString="jdbc:sqlite:";
            String connStringOld=oldFile==null?"oldDb/db.sqlite3": oldFile;
            String connStringNew=connMainString+(newFile==null?"diffDB/incSessionDB.sqlite3": newFile);
            Connection connNew=DriverManager.getConnection(connStringNew);
            connNew.prepareStatement("ATTACH DATABASE \""+connStringOld+"\" AS oldDB;").execute();

            String deleteOldAentEntityDataQuery="DELETE FROM main.ArchEntity WHERE main.ArchEntity.uuid IN "+
                    "(SELECT oldDB.ArchEntity.uuid FROM oldDB.ArchEntity WHERE oldDB.ArchEntity.AEntTypeID<>"+
                        "(SELECT oldDB.AEntType.AEntTypeID FROM oldDB.AEntType WHERE oldDB.AEntType.AEntTypeName='FieldTrip'))";

            String deleteOldAentDataQuery="DELETE FROM main.AentValue WHERE main.AentValue.UUID IN "+
                    "(SELECT UUID FROM oldDB.AentValue WHERE oldDB.AentValue.UUID NOT IN "+
                    "(SELECT UUID FROM oldDB.latestNonDeletedArchEntIdentifiers WHERE "+
                    "oldDB.latestNonDeletedArchEntIdentifiers.AttributeID=(SELECT oldDB.AttributeKey.AttributeID FROM oldDB.AttributeKey WHERE oldDB.AttributeKey.AttributeName='FieldTripID')))";
            String deleteOldAentRelnDataQuery="DELETE FROM main.AEntReln WHERE main.AEntReln.RelationshipID IN (SELECT oldDB.Relationship.RelationshipID FROM oldDB.Relationship)";
            String deleteOldRelnDataQuery="DELETE FROM main.Relationship WHERE main.Relationship.RelationshipID IN (SELECT RelationshipID FROM oldDB.Relationship)";
            String deleteOldFileDataQuery="DELETE FROM main.File WHERE main.File.filename IN (SELECT filename FROM oldDB.File)";

            PreparedStatement ps1=null, ps2=null, ps3=null, ps4=null, ps5=null;
            connNew.setAutoCommit(false);
            ps1=connNew.prepareStatement(deleteOldAentEntityDataQuery);
            ps2=connNew.prepareStatement(deleteOldAentDataQuery);
            ps3=connNew.prepareStatement(deleteOldAentRelnDataQuery);
            ps4=connNew.prepareStatement(deleteOldRelnDataQuery);
            ps5=connNew.prepareStatement(deleteOldFileDataQuery);

            ps1.executeUpdate();
            ps2.executeUpdate();
            ps3.executeUpdate();
            ps4.executeUpdate();
            ps5.executeUpdate();

            connNew.commit();
            rmFiles(connNew, null, logFile);
            connNew.close();

        }
        catch (Exception e){
            if(logFile!=null) logFile.severe(e+"");
            return;
        }
    }
    private void createDiffDB(Logger logFile, String targetPath){
        File source=new File("newDb/db.sqlite3");
        File target=new File(targetPath);
        //File target=new File("diffDB/incdb.sqlite3");
        try {
            if(target.isFile())
                target.delete();
            Files.copy(source.toPath(), target.toPath());
        }
        catch (Exception e) {
            if(logFile!=null) logFile.severe("Unable to copy DB file"+e);
        }
    }
    private void rmFiles(Connection conn, String folderPath, Logger logFile){
        if(folderPath==null) folderPath="files/app";
        String oldFileQuery="SELECT oldDB.File.Filename AS filename FROM oldDB.File";
        try{
            PreparedStatement ps1=conn.prepareStatement(oldFileQuery);
            ResultSet res= ps1.executeQuery();
            while(res.next()){
                File file=new File(res.getString("filename"));
                if(file.isFile()){
                    file.delete();
                }
            }
        }
        catch (Exception e){
            if(logFile!=null) logFile.severe(e+"");
            return;
        }
    }
    /*
    public static void main(String[] args){
        DBReader dr=new DBReader();
        //Connection conn=dr.DBConnect(null);
        Log lg=new Log();
        Logger logFile=lg.createLog("output", "default");
        dr.incrementalSession(null, null, logFile);
        Connection conn=dr.DBConnect("diffDB/incSessionDB.sqlite3");
        dr.fileQuery(conn);
    }
    */
}
