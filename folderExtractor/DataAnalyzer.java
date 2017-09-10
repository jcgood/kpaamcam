/**
 * Created by ning on 5/3/17.
 */
import java.io.*;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
public class DataAnalyzer {
    Logger logFile=null;
    public void danglingFileCheck(Connection conn, Logger log){
        logFile=log;
        if(conn==null) return;
        ResultSet res=null;
        LinkedList<String> danglingFile=new LinkedList<>();
        getDanglingFile(danglingFile);
        String queryFileList=buildQuerySetCondition(danglingFile,"'files/app/");
        if(queryFileList.equals("()")){
            System.out.println("No dangling file according to the file organization result");
            return;
        }
        String query="SELECT FileNewInfo.FileID AS FileID, FileName, FilePath FROM " +
                "(SELECT uuid AS FileID, measure AS FilePath FROM latestNonDeletedAentValue " +
                "WHERE FilePath IN " + queryFileList +
                " AND AttributeID =(SELECT AttributeID FROM AttributeKey WHERE AttributeName='FileContent')) FileExtendedInfo " +
                "LEFT JOIN " +
                "(SELECT uuid AS FileID, measure AS FileName FROM latestNonDeletedArchEntIdentifiers WHERE AttributeID=(SELECT AttributeID FROM AttributeKey WHERE AttributeName='FileID')) FileNewInfo " +
                "ON FileNewInfo.FileID=FileExtendedInfo.FileID";
        try{
            Statement statement=conn.createStatement();
            res=statement.executeQuery(query);
            File sumDangleFile=new File("output/danglingFile.txt");
            if(!sumDangleFile.isFile()){
                sumDangleFile.createNewFile();
            }

            FileOutputStream fo=new FileOutputStream(sumDangleFile);
            BufferedWriter bw=new BufferedWriter(new OutputStreamWriter(fo));
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = Calendar.getInstance().getTime();
            String dateInfo=dateFormat.format(date);
            bw.write(dateInfo);
            bw.newLine();

            while(res.next()){
                bw.write("File: "+ res.getString("FileName")+"    FilePath: "+res.getString("FilePath") +" FileID: "+res.getString("FileID"));
                bw.newLine();
            }
            bw.write("==============================================");
            bw.close();
            fo.close();
            System.out.println("The result is written in output/danglingFile.txt");
        }
        catch(Exception e){
            if(logFile!=null) logFile.severe(e+"");
        }
    }

    private void getDanglingFile(LinkedList<String> fileNames){
        String path="files/app/";
        File dir=new File(path);
        File[] ls=dir.listFiles();
        if(ls==null){return;}
        for(File child: ls){
            fileNames.add(child.getName());
        }
    }
    private String buildQuerySetCondition(LinkedList<String> itemList, String prefix){
        if(itemList==null || itemList.size()==0) return "";
        StringBuilder sb=new StringBuilder();
        sb.append("(");
        for (String s: itemList){
            String str=prefix+s+"'";
            if(sb.length()>1){
                sb.append(",");
            }
            sb.append(str);
        }
        sb.append(")");
        return sb.toString();
    }

    public void inCompleteSession(Connection conn, Logger log, LinkedList<SessionBasic> sssInfo){
        logFile=log;

        //System.out.print(sessionList);
        String sessionLocquery="SELECT SessionID, SessionLabel, SessionLoc From " +
                "((SELECT uuid AS SessionID, measure AS SessionLabel FROM latestNonDeletedArchEntIdentifiers WHERE AttributeID= (SELECT AttributeID FROM AttributeKey WHERE AttributeName='SessionID'))SessionBasic " +
                "INNER JOIN " +
                "(SELECT uuid, measure AS SessionLoc FROM latestNonDeletedAentValue WHERE AttributeID= (SELECT AttributeID FROM AttributeKey WHERE AttributeName='SessionLocation')) SessionMeta " +
                "ON SessionBasic.SessionID =SessionMeta.uuid)";

        ResultSet rsLoc=null;
        ResultSet rsAns=null;
        ResultSet rsIntv=null;
        ResultSet rsConl=null;
        HashSet<String> ans=new HashSet<>();
        HashSet<String> itv=new HashSet<>();
        HashSet<String> cols=new HashSet<>();
        try{
            Statement sssStmt=conn.createStatement();
            rsLoc=sssStmt.executeQuery(sessionLocquery);

            sssInfo.clear();
            while(rsLoc.next()){
                SessionBasic sb=new SessionBasic(rsLoc.getString("SessionID"), rsLoc.getString("SessionLabel"), rsLoc.getString("SessionLoc"));
                sssInfo.add(sb);
            }
            String sessionList=buildSessionSetCondition(sssInfo);
            String checkAnsRelnQuery="SELECT DISTINCT uuid FROM latestNonDeletedAentReln where RelationshipID in( " +
                    "select RelationshipID from latestNonDeletedRelationship where RelnTypeID=(select RelnTypeID from RelnType where RelnTypeName='Answer and Session') and latestNonDeletedRelationship.Deleted IS NULL) " +
                    "and uuid in"+sessionList;

            String checkIntvQuery="SELECT DISTINCT uuid FROM latestNonDeletedAentReln where RelationshipID in(" +
                    "select RelationshipID from latestNonDeletedRelationship where RelnTypeID=(select RelnTypeID from RelnType where RelnTypeName='Session and Interviewer') and latestNonDeletedRelationship.Deleted IS NULL)" +
                    "and uuid in"+sessionList;

            String checkConsultantQuery="SELECT DISTINCT uuid FROM latestNonDeletedAentReln where RelationshipID in(" +
                    "select RelationshipID from latestNonDeletedRelationship where RelnTypeID=(select RelnTypeID from RelnType where RelnTypeName='Session and Consultant') and latestNonDeletedRelationship.Deleted IS NULL)" +
                    "and uuid in "+sessionList;
            Statement statement=conn.createStatement();

            rsAns=statement.executeQuery(checkAnsRelnQuery);
            while(rsAns!=null && rsAns.next()){
                ans.add(rsAns.getString("uuid"));
            }
            rsIntv=statement.executeQuery(checkIntvQuery);
            while(rsIntv!=null&&rsIntv.next()){
                itv.add(rsIntv.getString("uuid"));
            }
            rsConl=statement.executeQuery(checkConsultantQuery);
            while(rsConl!=null && rsConl.next()){
                cols.add(rsIntv.getString("uuid"));
            }

        }
        catch(Exception e){
            if(logFile!=null) logFile.severe(e+"");
            return;
        }
        final String nogps="GPS not available";
        HashSet<String> sss=new HashSet<>();
        HashSet<String> noGPS=new HashSet<>();
        for (SessionBasic s: sssInfo){
            sss.add(s.id);
            if(s.loc.equals(nogps)) noGPS.add(s.id);
        }
        HashSet<String> noAnsSession=new HashSet<>(sss);
        noAnsSession.removeAll(ans);
        HashSet<String> noItvSession=new HashSet<>(sss);
        noItvSession.removeAll(itv);
        HashSet<String> noColSession=new HashSet<>(sss);
        noColSession.removeAll(cols);

        HashMap<String, LinkedList<String>> res=new HashMap<>();
        res.put("noGPS", new LinkedList<>());
        res.put("noAnswer", new LinkedList<>());
        res.put("noInterviewer", new LinkedList<>());
        res.put("noConsultant", new LinkedList<>());
        for (SessionBasic s: sssInfo){
            if(noAnsSession.contains(s.id)) {
                LinkedList<String> tmp=res.get("noAnswer");
                tmp.add(s.label);
                res.put("noAnswer",tmp);
            }
            if(noGPS.contains(s.id)) {
                LinkedList<String> tmp=res.get("noGPS");
                tmp.add(s.label);
                res.put("noGPS",tmp);
            }
            if(noItvSession.contains(s.id)) {
                LinkedList<String> tmp=res.get("noInterviewer");
                tmp.add(s.label);
                res.put("noInterviewer",tmp);
            }
            if(noColSession.contains(s.id)) {
                LinkedList<String> tmp=res.get("noConsultant");
                tmp.add(s.label);
                res.put("noConsultant",tmp);
            }
        }
        try{
            String path="output/inCompleteMetaData.txt";
            File sumFile=new File(path);
            if(!sumFile.isFile()){
                sumFile.createNewFile();
            }
            FileOutputStream fos=new FileOutputStream(sumFile);
            BufferedWriter bw=new BufferedWriter(new OutputStreamWriter(fos));
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = Calendar.getInstance().getTime();
            String dateInfo=dateFormat.format(date);
            bw.write(dateInfo);
            bw.newLine();
            for(String key: res.keySet()){
                bw.write(key+": ");
                LinkedList<String> tmp=res.get(key);
                for(String s: tmp){
                    bw.write(s+"; ");
                }
                bw.newLine();
                bw.write("=================================================");
                bw.newLine();
                bw.newLine();
            }
            bw.close();
            fos.close();
            System.out.println("The result is written in output/inCompleteMetaData.txt");
        }
        catch (Exception e){
            if(logFile!=null) {
                logFile.log (Level.ALL,"Error when summarizing incomplete data :"+e);
            }
            return;
        }

    }

    private String buildSessionSetCondition(LinkedList<SessionBasic> itemList){
        if(itemList==null || itemList.size()==0) return "";
        StringBuilder sb=new StringBuilder();
        sb.append("(");
        for (SessionBasic s: itemList){
            String str="'"+s.id+"'";
            if(sb.length()>1){
                sb.append(",");
            }
            sb.append(str);
        }
        sb.append(")");
        return sb.toString();
    }
    /*
    public static void main(String [] args){
        DataAnalyzer da=new DataAnalyzer();
        DBReader dr=new DBReader();
        Connection conn=dr.DBConnect(null);
        Log lg=new Log();
        Logger logFile=lg.createLog("output", "default");
        da.danglingFileCheck(conn,logFile);
        da.inCompleteSession(conn,logFile,new LinkedList<SessionBasic>());
    }
*/
}
