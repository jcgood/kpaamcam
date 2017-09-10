import java.io.*;
import java.sql.*;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.text.Normalizer.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Created by ning on 7/13/17.
 */
public class DBReader {

    HashMap<String, InfoTuple> sessionInfoList=new HashMap<>();
    HashMap<String, String> ftFiles=new HashMap<>();
    HashMap<String, String> quesnirFiles=new HashMap<>();
    public Connection DBConnect(String path){
        try{
            Class.forName("org.sqlite.JDBC");// register the driver
            String connMainString="jdbc:sqlite:";
            String connString=connMainString;
            if(path==null || path.length()==0) connString+="db/db.sqlite3";
            else connString+=path;
            Connection conn= DriverManager.getConnection(connString);
            if(conn==null) System.out.println("Failed to connect to DB");
            return conn;
        }
        catch (Exception e){
            System.out.println(e);
            return null;
        }
    }
    private File createCSVFile(){
        File csvFile= new File("files/KPAAMCAM.csv");
        try {
            if(!csvFile.isFile()){
                csvFile.createNewFile();
                return csvFile;
            }
            return csvFile;
        }
        catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }
    public void cleanFiles(Connection conn){
        String getAllFilesQuery="SELECT uuid as fid,measure as flabel FROM latestNonDeletedArchEntIdentifiers " +
                "WHERE AttributeID "+
                "= (SELECT AttributeID FROM AttributeKey WHERE AttributeName='FileID') "+
                "GROUP BY uuid ORDER BY ValueTimestamp asc;";
        HashMap<String, ArrayList<String>> map=new HashMap<>();
        try{
            Statement fs=conn.createStatement();
            ResultSet rfile=fs.executeQuery(getAllFilesQuery);
            while(rfile.next()){
                String nonSuffixLabel=getOriginLabel(rfile.getString("flabel"));
                if(!map.containsKey(nonSuffixLabel)){
                    map.put(nonSuffixLabel, new ArrayList<String>());
                }
                ArrayList<String> tmp=map.get(nonSuffixLabel);
                tmp.add(rfile.getString("fid"));
                map.put(nonSuffixLabel, tmp);
            }

            String attributeIDQuery="SELECT AttributeID as aid FROM AttributeKey WHERE AttributeName='FileID'";
            String attributeID="";

            Statement s1=conn.createStatement();
            ResultSet ra=s1.executeQuery(attributeIDQuery);
            while(ra.next()){
                attributeID=ra.getString("aid");
            }
            for(String key: map.keySet()){
                ArrayList<String> temp=map.get(key);
                int size=temp.size();
                if(size==1){
                    String updateQuery="UPDATE AentValue SET Measure='"+ key+ "' WHERE AttributeID='"
                            +attributeID+"' AND UUID='"+temp.get(0)+"'";

                    Statement u1=conn.createStatement();
                    u1.executeUpdate(updateQuery);
                    u1.close();
                    continue;
                }
                else{
                    for(int i=0;i<size;i++){
                        int suffix=i+1;
                        String withSuffix=key+"("+suffix+")";

                        String updateQuery="UPDATE AEntValue SET Measure='"+ withSuffix+ "' WHERE AttributeID='"
                                +attributeID+"' AND UUID='"+temp.get(i)+"'";

                        Statement u1=conn.createStatement();
                        u1.executeUpdate(updateQuery);
                        u1.close();
                    }
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
//one time function
    public void cleanSessions(Connection connection){
        String getAllSessionInfo="SELECT uuid as Sid,measure as Sname, ValueTimeStamp as ts FROM latestNonDeletedArchEntIdentifiers WHERE AttributeID = (SELECT AttributeID FROM AttributeKey WHERE AttributeName='SessionID') AND DELETED is NULL";
        HashSet<String> uselessSss=new HashSet<>();
        try{
            Statement s1=connection.createStatement();
            ResultSet rAllSession=s1.executeQuery(getAllSessionInfo);
            while(rAllSession.next()){
                String uuid=rAllSession.getString("Sid");
                String loadSssIntvRelnQuery="select measure from latestNonDeletedAentValue where attributeID=(select attributeID from attributekey where attributename='PersonName') and uuid in"+
                        " (select uuid from latestNonDeletedAentReln where uuid <>'"+uuid+"' "+
                        "and RelationshipID in (select RelationshipID from latestNonDeletedAentReln where uuid='"+uuid+
                        "' and RelationshipID in (select RelationshipID from latestNonDeletedRelationship "+
                        "where RelnTypeID=(select RelnTypeID from RelnType where RelnTypeName='Session and Interviewer') "+
                        "and latestNonDeletedRelationship.Deleted IS NULL)))";
                Statement s2=connection.createStatement();
                ResultSet rintv=s2.executeQuery(loadSssIntvRelnQuery);
                while(rintv.next()){
                    String intv=rintv.getString("measure");
                    if(!intv.equals("Marius Kum Kebei") && !intv.equals("Angiachi Demetris")){
                        uselessSss.add(uuid);
                    }
                }
            }
            for(String id: uselessSss){
                String delSessionEnt="DELETE FROM ArchEntity WHERE uuid='"+id+"'";
                String delSessionValue="DELETE FROM AentValue WHERE uuid='"+id+"'";
                Statement u1=connection.createStatement();
                u1.executeUpdate(delSessionEnt);
                Statement u2=connection.createStatement();
                u2.executeUpdate(delSessionValue);
            }


        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private String getOriginLabel(String label){
        String nonSuffixLabel=label;
        Pattern numberPat = Pattern.compile("\\(\\d+\\)$");
        Matcher numMatcher = numberPat.matcher(label);
        if(numMatcher.find()){
            String [] patternSplit=label.split("\\(");
            StringBuilder sb=new StringBuilder();
            for(int i=0;i<patternSplit.length-1;i++){
                sb.append(patternSplit[i]);
            }
            nonSuffixLabel=sb.toString();

        }
        return nonSuffixLabel;
    }
    public void ssessionInfo(Connection connection){
        HashSet<String> files=new HashSet<>();
        String getAllSessionInfo="SELECT uuid as Sid,measure as Sname, ValueTimeStamp as ts FROM latestNonDeletedArchEntIdentifiers WHERE AttributeID = (SELECT AttributeID FROM AttributeKey WHERE AttributeName='SessionID') AND DELETED is NULL";
        ArrayList<String> photoFile=new ArrayList<>();
        try{
            Statement s1=connection.createStatement();
            ResultSet rAllSession=s1.executeQuery(getAllSessionInfo);
            while(rAllSession.next()){
                //session basic info
                JSONObject obj=new JSONObject();
                String uuid=rAllSession.getString("Sid");
                InfoTuple it=new InfoTuple();
                it.sid=uuid;
                String sessionName=rAllSession.getString("Sname");
                obj.put("DBSessionLabel", sessionName);
                sessionName=cleanFileName(sessionName);
                it.sname=sessionName;
                String ts=rAllSession.getString("ts");
                it.date=ts.substring(0, ts.indexOf(" "));
                obj.put("SessionUuid",uuid);
                obj.put("SessionLabel", it.sname);
                String getSessionBasicInfo="SELECT uuid as id, attributename as attr, measure as val, freetext as annotation, attributetype, attributeisfile " +
                        "FROM latestNonDeletedArchent JOIN latestNonDeletedAentvalue AS av using (uuid) JOIN attributekey using (attributeid) " +
                        "WHERE uuid = '"+uuid+"'";
                Statement sbasic=connection.createStatement();
                ResultSet sBasicInfo= sbasic.executeQuery(getSessionBasicInfo);
                while (sBasicInfo.next()){
                    String attr=sBasicInfo.getString("attr");
                    if(!attr.equals("SessionID")){
                        obj.put(sBasicInfo.getString("attr"), sBasicInfo.getString("val"));
                    }
                    if(sBasicInfo.getString("annotation")!=null){
                        obj.put(sBasicInfo.getString("attr")+"Annotation", sBasicInfo.getString("annotation"));
                    }
                    switch (attr){
                        case "SessionLocation":
                            it.loc=sBasicInfo.getString("val");
                            break;
                        case "SessionDescription":
                            it.desc=sBasicInfo.getString("val");
                            break;
                    }
                }
                //session person info
                JSONArray jPerson=new JSONArray();
                String sessionPersonRoleQuery="select t5.personUuid as pid, t6.personRoleName as psRoleName, t5.persName as pName "+
                        "from (select t1.personId as personUuid, t1.personName as persName, t2.psssId as relnId from (select pId.uuid as personId, pId.measure as personName "+
                        "from latestNonDeletedAentValue as pId where pId.AttributeID=(SELECT AttributeID from AttributeKey where AttributeName='PersonName') "+
                        "and pId.uuid in (select psName.measure from latestNonDeletedAentValue as psName, latestNonDeletedAentValue as psReln "+
                        "where psName.AttributeID=(select AttributeID from AttributeKey where AttributeName='SessionPersonName') "+
                        "and psReln.AttributeID=(select AttributeID from AttributeKey where AttributeName='SessionIDforPerson') and psReln.measure='"+uuid+"' "+
                        "and psName.uuid=psReln.uuid and psName.uuid in (select uuid from latestNonDeletedArchEntIdentifiers where AttributeID=(select AttributeID from AttributeKey "+
                        "where AttributeName='SessionIDforPerson')))) t1 inner join "+
                        "(select psName.uuid as psssId, psName.measure as psId from latestNonDeletedAentValue as psName, latestNonDeletedAentValue as psReln "+
                        "where psName.AttributeID=(select AttributeID from AttributeKey where AttributeName='SessionPersonName') "+
                        "and psReln.AttributeID=(select AttributeID from AttributeKey where AttributeName='SessionIDforPerson') and psReln.measure='"+uuid+"' "+
                        "and psName.uuid=psReln.uuid) t2 on t1.personId=t2.psId) t5 "+
                        "inner join "+
                        "(select t3.roleId as personRoleUuid, t3.roleName as personRoleName, t4.rsssId as relnId from (select rId.uuid as roleId, rId.measure as roleName "+
                        "from latestNonDeletedAentValue as rId where rId.AttributeID=(select AttributeID from AttributeKey where AttributeName='PersonRoleName') "+
                        "and rId.uuid in (select psRole.measure from latestNonDeletedAentValue as psRole, latestNonDeletedAentValue as psReln "+
                        "where psRole.AttributeID=(select AttributeID from AttributeKey where AttributeName='SessionPersonRole') and psReln.AttributeID=(select AttributeID "+
                        "from AttributeKey where AttributeName='SessionIDforPerson') and psReln.measure='"+uuid+"' and psRole.uuid=psReln.uuid))t3 "+
                        "inner join (select psRole.uuid as rsssId, psRole.measure as tempPsRoleID from latestNonDeletedAentValue as psRole, latestNonDeletedAentValue as psReln "+
                        "where psRole.AttributeID=(select AttributeID from AttributeKey where AttributeName='SessionPersonRole') and psReln.AttributeID=(select AttributeID "+
                        "from AttributeKey where AttributeName='SessionIDforPerson') and psReln.measure='"+uuid+"' "+
                        "and psRole.uuid=psReln.uuid)t4 on t3.roleId=t4.tempPsRoleID) t6 on t5.relnId =t6.relnId group by pid, psRoleName";

                Statement sPs=connection.createStatement();
                ResultSet rPs=sPs.executeQuery(sessionPersonRoleQuery);
                while(rPs.next()){
                    JSONObject psObj=new JSONObject();
                    String puuid=rPs.getString("pid");
                    String roleName=rPs.getString("psRoleName");
                    String psName=rPs.getString("pName");
                    psObj.put("PersonUuid",puuid);
                    if(roleName.equals("consultant")){
                        it.consultant.add(psName);
                    }
                    else if(roleName.equals("interviewer")){
                        it.interviewer.add(psName);
                    }
                    String getPersonInfo="SELECT uuid as id, attributename as attr, measure as val, freetext as annotation, attributetype, attributeisfile as isfile, ValueTimestamp as ts, AttributeID as attrid " +
                            "FROM latestNonDeletedArchent JOIN latestNonDeletedAentvalue AS av using (uuid) JOIN attributekey using (attributeid) " +
                            "WHERE uuid = '"+puuid+"'";
                    Statement spsInfo=connection.createStatement();
                    ResultSet spsDetail=spsInfo.executeQuery(getPersonInfo);

                    while(spsDetail.next()){
                        String psAttr=spsDetail.getString("attr");
                        psObj.put(psAttr, spsDetail.getString("val"));
                        if(psAttr.equals("PersonPhoto") && spsDetail.getString("val")!=null){
                            String oldFilename=spsDetail.getString("val");
                            String newPhotoFileName=generatePhotoName(psName,oldFilename, spsDetail.getString("ts"));
                            photoFile.add(newPhotoFileName);

                            String oldFile="files/"+oldFilename.substring(oldFilename.lastIndexOf("/")+1);
                            File oFile=new File(oldFile);
                            File newFile=new File("files/"+newPhotoFileName);
                            oFile.renameTo(newFile);
                            psObj.put("PersonPhotoNewName", newPhotoFileName);
                            files.add(newPhotoFileName);
                            it.filename.add(newPhotoFileName);
                        }
                    }
                    psObj.put("PersonRoleInSession",roleName);
                    jPerson.add(psObj);
                }
                obj.put("PersonList",jPerson);
                //session answer and file info
                JSONArray ansList=new JSONArray();
                String getSessionAnswerQuery="select uuid as aid from latestNonDeletedAentValue "+
                        "where latestNonDeletedAentValue.AttributeID=(select AttributeID from AttributeKey where AttributeName='AnswerLabel') "+
                        "and uuid in "+
                        "(select uuid from AentReln where RelationshipID in "+
                        "(select RelationshipID from AEntReln where AEntReln.uuid='"+uuid+"' "+
                        "AND RelationshipID in "+
                        "(select RelationshipID from latestNonDeletedRelationship where RelnTypeID="+
                        "(select RelnTypeID from RelnType where RelnTypeName='Answer and Session') "+
                        "and latestNonDeletedRelationship.Deleted IS NULL)))";
                Statement sAns=connection.createStatement();
                ResultSet sAnsInfo=sAns.executeQuery(getSessionAnswerQuery);
                while(sAnsInfo.next()){
                    String auuid=sAnsInfo.getString("aid");
                    JSONObject ansInfo=new JSONObject();
                    ansInfo.put("AnswerUuid",auuid);
                    String getAnswerBasicInfo="SELECT uuid as id, attributename as attr, measure as val, freetext as annotation, attributetype, attributeisfile " +
                            "FROM latestNonDeletedArchent JOIN latestNonDeletedAentvalue AS av using (uuid) JOIN attributekey using (attributeid) " +
                            "WHERE uuid = '"+auuid+"'";
                    String getAnswerFileInfo="select uuid as fid ,measure as flabel from latestNonDeletedAentValue "+
                            "where latestNonDeletedAentValue.AttributeID=(select AttributeID from AttributeKey where AttributeName='FileID') "+
                            "and uuid in "+
                            "(select uuid from AentReln where RelationshipID in "+
                            "(select RelationshipID from AEntReln where AEntReln.uuid="+auuid+" "+
                            "AND RelationshipID in "+
                            "(select RelationshipID from latestNonDeletedRelationship where RelnTypeID="+
                            "(select RelnTypeID from RelnType where RelnTypeName='Answer and File') "+
                            "and latestNonDeletedRelationship.Deleted IS NULL)))";

                    Statement sAnsBasic=connection.createStatement();
                    ResultSet sAnsBasicInfo=sAnsBasic.executeQuery(getAnswerBasicInfo);
                    Statement sAnsFile=connection.createStatement();
                    ResultSet sAnsFileLs=sAnsFile.executeQuery(getAnswerFileInfo);
                    while(sAnsBasicInfo.next()){
                        ansInfo.put(sAnsBasicInfo.getString("attr"), sAnsBasicInfo.getString("val"));
                    }
                    JSONArray filelist=new JSONArray();
                    while(sAnsFileLs.next()){
                        JSONObject fObj=new JSONObject();
                        String fid=sAnsFileLs.getString("fid");
                        String flabel=sAnsFileLs.getString("flabel");

                        fObj.put("FileUuid",fid);
                        String getFileBasicInfo="SELECT uuid as id, attributename as attr, measure as val, freetext as annotation, attributetype, attributeisfile " +
                                "FROM latestNonDeletedArchent JOIN latestNonDeletedAentvalue AS av using (uuid) JOIN attributekey using (attributeid) " +
                                "WHERE uuid = '"+fid+"'";
                        Statement fBasic=connection.createStatement();
                        ResultSet fInfo=fBasic.executeQuery(getFileBasicInfo);
                        while(fInfo.next()){
                            String attrName=fInfo.getString("attr");
                            fObj.put(attrName, fInfo.getString("val"));
                            if(attrName.equals("FileContent")){
                                String curLabel=flabel;
                                curLabel=generateAnsFileName(curLabel, fInfo.getString("val"));
                                String oldFilePath=fInfo.getString("val");
                                oldFilePath="files/"+oldFilePath.substring(oldFilePath.lastIndexOf("/")+1);
                                File oldFile=new File(oldFilePath);
                                File newFile=new File("files/"+curLabel);
                                oldFile.renameTo(newFile);
                                fObj.put("FileNewLabel",curLabel);
                                files.add(curLabel);
                                it.filename.add(curLabel);
                            }
                        }
                        filelist.add(fObj);
                    }
                    ansInfo.put("FileList",filelist);
                    ansList.add(ansInfo);
                }
                obj.put("AnswerList",ansList);
                //session fieldtrip info
                String fieldTripQuery="select uuid,measure as FTLabel from latestNonDeletedAentValue where latestNonDeletedAentValue.AttributeID=(select AttributeID from AttributeKey where AttributeName='FieldTripID') and uuid in (select uuid from AentReln where RelationshipID in (select RelationshipID from AEntReln where AEntReln.uuid='"+uuid+
                        "' AND RelationshipID in (select RelationshipID from latestNonDeletedRelationship where RelnTypeID=(select RelnTypeID from RelnType where RelnTypeName='Session and FieldTrip')and latestNonDeletedRelationship.Deleted IS NULL)))";
                Statement sFt=connection.createStatement();
                ResultSet rFt=sFt.executeQuery(fieldTripQuery);
                while(rFt.next()){
                    it.fieldTrip=rFt.getString("FTLabel");
                    String key=rFt.getString("uuid");
                    if(ftFiles.containsKey(key)){
                        it.filename.add(ftFiles.get(key));
                    }
                }

                obj.put("FieldTripName",it.fieldTrip);
                //session questionnaire info
                String questionniareQuery="SELECT uuid,measure as quesnir FROM latestNonDeletedAentValue WHERE latestNonDeletedAentValue.AttributeID = (SELECT AttributeID FROM AttributeKey WHERE AttributeName='QuestionnaireName') AND latestNonDeletedAentValue.uuid IN "+
                        "(select measure from latestNonDeletedAentValue where latestNonDeletedAentValue.AttributeID=(select AttributeID from AttributeKey where AttributeName='AnswerQuestionnaireID') and uuid in (select uuid from AentReln where RelationshipID in (select RelationshipID from AEntReln where AEntReln.uuid='"+uuid +
                        "' AND RelationshipID in (select RelationshipID from latestNonDeletedRelationship where RelnTypeID=(select RelnTypeID from RelnType where RelnTypeName='Answer and Session') and latestNonDeletedRelationship.Deleted IS NULL))) group by measure)";
                Statement sQuesnir=connection.createStatement();
                ResultSet rQuesnir=sQuesnir.executeQuery(questionniareQuery);
                while(rQuesnir.next()){
                    it.questionnaire.add(rQuesnir.getString("quesnir"));
                    String key=rQuesnir.getString("uuid");
                    if(quesnirFiles.containsKey(key)){
                        it.filename.add(quesnirFiles.get(key));
                    }
                }
                JSONArray quesnir=new JSONArray();
                for(String q: it.questionnaire){
                    JSONObject qe=new JSONObject();
                    qe.put("QuestionnaireUsed", q);
                    quesnir.add(qe);
                }
                if(it.desc==null || it.desc.equals("")){
                    //using questionnaire to generate the session description
                    StringBuilder sb=new StringBuilder();
                    for(int i=0; i<it.questionnaire.size();i++){
                        if(i!=it.questionnaire.size()-1){
                            sb.append(it.questionnaire.get(i)+"||");
                        }
                        else{
                            sb.append(it.questionnaire.get(i));
                        }
                    }
                    it.desc=sb.toString();
                }
                obj.put("QuestionnaireList", quesnir);
                sessionInfoList.put(uuid, it);
                generateSessionMetaFile(obj, it.sname);
                it.sJson=it.sname+".json";
            }
            deleteUselessFile(files);
            System.out.println("Writing csv file...");
            writeSessionInfoToCSV();

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    private void writeSessionInfoToCSV(){
        File file=createCSVFile();
        try {
            if(file!=null){
                FileOutputStream fos=new FileOutputStream(file);
                BufferedWriter bw=new BufferedWriter(new OutputStreamWriter(fos));
                String firstRow="dc.title,dc.date.created,dc.coverage.spatial,dc.description,dc.contributor.researcher,dc.contributor.speaker,filename,dc.relation.ispartof,dc.relation.references,dc.description.sponsorship";
                bw.write(firstRow);
                bw.newLine();
                for(String key: sessionInfoList.keySet()){
                    StringBuilder sb=new StringBuilder();
                    InfoTuple t=sessionInfoList.get(key);
                    sb.append(t.sname+",");
                    sb.append(t.date+",");
                    sb.append(t.loc+",");
                    sb.append(t.desc+",");

                    for(int i=0;i<t.interviewer.size(); i++) {
                        if(i!=t.interviewer.size()-1){
                            sb.append(t.interviewer.get(i)+"||");
                        }
                        else{
                            sb.append(t.interviewer.get(i)+",");
                        }
                    }

                    for(int i=0;i< t.consultant.size(); i++){
                        if(i!=t.consultant.size()-1) {
                            sb.append(t.consultant.get(i) + "||");
                        }
                        else{
                            sb.append(t.consultant.get(i)+",");
                        }
                    }

                    for(int i=0; i<t.filename.size();i++){
                        sb.append(t.filename.get(i)+"||");
                    }
                    sb.append(t.sJson+",");
                    sb.append(t.fieldTrip+",");
                    for(int i=0; i<t.questionnaire.size();i++){
                        if(i!=t.questionnaire.size()-1){
                            sb.append(t.questionnaire.get(i)+"||");
                        }
                        else{
                            sb.append(t.questionnaire.get(i)+",");
                        }
                    }
                    sb.append("The collection of this data was supported by funding from the U.S. National Science Foundation under Award No. BCS-1360763");
                    String s=sb.toString();
                    bw.write(s);
                    bw.newLine();
                }
                bw.close();
                fos.close();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    private void deleteUselessFile(HashSet<String> files){
        File folder=new File("files/");
        ArrayList<String> deleteFile=new ArrayList<>();
        for(File fileEntry: folder.listFiles()){
            String curFile=fileEntry.getName();
            String fileType=curFile.substring(curFile.lastIndexOf("."));
            if(fileType.equals(".json") || fileType.equals(".csv")) continue;
            else if(!files.contains(curFile)){
                deleteFile.add(curFile);
            }
        }
        for(String fileName: deleteFile){
            File tmpFile=new File("files/"+fileName);
            if(tmpFile.isFile()){
                tmpFile.delete();
            }
        }
    }
    private void generateSessionMetaFile(JSONObject object, String filename){
        try {
            FileWriter fw=new FileWriter("files/"+filename+".json");
            fw.write(object.toJSONString());
            fw.flush();
            fw.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    private String generatePhotoName(String name, String oldFileName, String timestamp){
        if(name=="") name="nameUnknown";
        String[] time=timestamp.split(" ");
        String extension=oldFileName.substring(oldFileName.lastIndexOf("."));
        String newFileName=name+time[0]+extension;
        newFileName=cleanFileName(newFileName);
        return newFileName;
    }
    private String generateAnsFileName(String name, String oldName){
        String fileExtension=oldName.substring(oldName.lastIndexOf("."));
        name=cleanFileName(name);
        name+=fileExtension;
        return name;
    }
    private String cleanFileName(String fileName){
        fileName= fileName.replaceAll("\\(","_");
        fileName=fileName.replaceAll("\\)","");
        fileName=fileName.replaceAll(" ","");
        fileName=fileName.replaceAll("\\+","plus");
        fileName=fileName.replaceAll("'","");
        fileName= Normalizer.normalize(fileName, Form.NFD).replaceAll("[^\\p{ASCII}]","").replaceAll("\\p{M}","");
        return fileName;
    }
    public void generateMetadata(Connection conn){
        //generate fieldTrip metadata file for each fieldTrip
        System.out.println("Generating metadata files...");
        generateFTMetaFile(conn);
        System.out.println("Done creating metadata file for fieldtrip");
        System.out.println("Generating metadata files...");
        generateQuestionnaire(conn);
        System.out.println("Done creating metadata file for questionnaire");
        System.out.println("Done generating metadata files.");
    }
    private void generateFTMetaFile(Connection conn){
        String loadAllFTQuery="SELECT uuid as ftid, measure as ftlabel from latestAllArchEntIdentifiers where AttributeID=(SELECT AttributeID FROM AttributeKey WHERE AttributeName='FieldTripID')";
        String uuid="";
        try {
            Statement s1=conn.createStatement();
            ResultSet rAllFT=s1.executeQuery(loadAllFTQuery);
            while(rAllFT.next()){
                JSONObject ftObject=new JSONObject();
                uuid=rAllFT.getString("ftid");
                String ftLabel=rAllFT.getString("ftlabel");

                ftObject.put("FieldTripUuid", uuid);
                ftObject.put("DBFieldTripLabel", ftLabel);
                ftLabel=cleanFileName(ftLabel);
                ftObject.put("FieldTripLabel", ftLabel);

                String getEntityValue="SELECT uuid as ftid, attributename as ftattr, measure as ftval, freetext as ftannotation, attributetype, attributeisfile " +
                        "FROM latestNonDeletedArchent JOIN latestNonDeletedAentvalue AS av using (uuid) JOIN attributekey using (attributeid) " +
                        "WHERE uuid = '"+uuid+"'";
                Statement s2=conn.createStatement();
                ResultSet rFT=s2.executeQuery(getEntityValue);
                if(!rFT.next()){continue;}

                while(rFT.next()){
                    if(!rFT.getString("ftattr").equals("FieldTripID")){
                        ftObject.put(rFT.getString("ftattr"), rFT.getString("ftval"));
                    }
                    if(rFT.getString("ftannotation")!=null){
                        ftObject.put(rFT.getString("ftattr")+"Annotation",rFT.getString("ftannotation"));
                    }
                }
                String getAllSessionFortheFT="select uuid as sid, measure as slabel from latestNonDeletedAentValue "+
                        "where latestNonDeletedAentValue.AttributeID=(select AttributeID from AttributeKey where AttributeName='SessionID') "+
                        "and uuid in "+
                        "(select uuid from AentReln where RelationshipID in "+
                        "(select RelationshipID from AEntReln where AEntReln.uuid='"+uuid+"' "+
                        "AND RelationshipID in "+
                        "(select RelationshipID from latestNonDeletedRelationship where RelnTypeID="+
                        "(select RelnTypeID from RelnType where RelnTypeName='Session and FieldTrip') "+
                        "and latestNonDeletedRelationship.Deleted IS NULL)))";
                Statement s3=conn.createStatement();
                ResultSet rFTSession=s3.executeQuery(getAllSessionFortheFT);
                JSONArray sessionLs=new JSONArray();
                while(rFTSession.next()){
                    JSONObject objSss=new JSONObject();
                    objSss.put("SessionUuid",rFTSession.getString("sid"));
                    objSss.put("DBSessionLabel",rFTSession.getString("slabel"));
                    sessionLs.add(objSss);
                }
                ftObject.put("SessionList",sessionLs);
                String newFileName=ftLabel+".json";
                File newFile=new File("files/"+newFileName);
                try {
                    FileWriter fw=new FileWriter(newFile);
                    fw.write(ftObject.toJSONString());
                    fw.flush();
                    fw.close();
                    ftFiles.put(uuid, newFileName);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    private void generateQuestionnaire(Connection conn){
        String allQuesnirQuery="select uuid as qid, measure as qlabel from latestNonDeletedArchEntIdentifiers "+
                "where AttributeID = (select AttributeID from AttributeKey where AttributeName='QuestionnaireID')";
        String uuid="";
        try {
            Statement s1=conn.createStatement();
            ResultSet rAllRes=s1.executeQuery(allQuesnirQuery);
            while(rAllRes.next()){
                JSONObject obj=new JSONObject();
                uuid=rAllRes.getString("qid");
                String qlabel=rAllRes.getString("qlabel");
                obj.put("QuestionnaireUuid",uuid);
                obj.put("DBQuesnirLabel", qlabel);
                qlabel=cleanFileName(qlabel);

                obj.put("QuesnirLabel", qlabel);
                String getEntityValue="SELECT uuid as id, attributename as attr, measure as val, freetext as annotation, attributetype, attributeisfile " +
                        "FROM latestNonDeletedArchent JOIN latestNonDeletedAentvalue AS av using (uuid) JOIN attributekey using (attributeid) " +
                        "WHERE uuid = '"+uuid+"'";
                Statement s2=conn.createStatement();
                ResultSet rRes=s2.executeQuery(getEntityValue);
                if(!rRes.next()){continue;}

                while(rRes.next()){
                    if(!rRes.getString("attr").equals("QuestionnaireID")){
                        obj.put(rRes.getString("attr"), rRes.getString("val"));
                    }

                    if(rRes.getString("annotation")!=null){
                        obj.put(rRes.getString("attr")+"Annotation",rRes.getString("annotation"));
                    }
                }

                String loadQuesContentandOrderQuery="select t1.quesId as qid, t1.quesOrder as qorder, t2.quesContent as qContent from "+
                        "(select quId.measure as quesId, qOrder.measure as quesOrder "+
                        "from latestNonDeletedAentValue as quId, latestNonDeletedAentValue as qOrder "+
                        "where qOrder.AttributeID=(SELECT AttributeID from AttributeKey where AttributeName='QuesOrderLocal') "+
                        "and quId.AttributeID=(select AttributeID from AttributeKey where AttributeName='QuesID') "+
                        "and quId.uuid=qOrder.uuid "+
                        "and quId.measure in "+
                        "(select quesInQuesnir.measure from latestNonDeletedAentValue as quesInQuesnir, latestNonDeletedAentValue as quesnir "+
                        "where quesInQuesnir.AttributeID=(select AttributeID from AttributeKey where AttributeName='QuesID') "+
                        "and quesnir.AttributeID=(select AttributeID from AttributeKey where AttributeName='QuesnirID') "+
                        "and quesInQuesnir.uuid=quesnir.uuid and quesnir.measure='"+uuid+"') "+
                        "and qOrder.uuid in "+
                        "(select uuid from latestNonDeletedArchEntIdentifiers where AttributeID = (select AttributeID from AttributeKey where AttributeName='QuesnirID') "+
                        "and measure='"+uuid+"')) t1 "+
                        "inner join "+
                        "(select qId.measure as quesId, qContent.measure as quesContent "+
                        "from latestNonDeletedAentValue as qId, latestNonDeletedAentValue as qContent, latestNonDeletedAentValue as qLanguage "+
                        "where qContent.AttributeID=(SELECT AttributeID from AttributeKey where AttributeName='QuesContent') "+
                        "and qId.AttributeID=(select AttributeID from AttributeKey where AttributeName='QuestionUuid') "+
                        "and qLanguage.AttributeID=(select AttributeID from AttributeKey where AttributeName='QuesLangUuid') "+
                        "and qId.uuid=qContent.uuid "+
                        "and qId.uuid=qLanguage.uuid "+
                        "and qLanguage.measure IN (select eng.uuid from latestNonDeletedAentValue as eng where eng.AttributeID=(select AttributeID from AttributeKey where AttributeName='LanguageName') "+
                        "and eng.measure='English' and eng.uuid in (select uuid from latestNonDeletedArchEntIdentifiers where AttributeID=(select AttributeID from AttributeKey where AttributeName='LanguageID'))) "+
                        "and qId.measure in "+
                        "(select quesInQuesnir.measure from latestNonDeletedAentValue as quesInQuesnir, latestNonDeletedAentValue as quesnir "+
                        "where quesInQuesnir.AttributeID=(select AttributeID from AttributeKey where AttributeName='QuesID') "+
                        "and quesnir.AttributeID=(select AttributeID from AttributeKey where AttributeName='QuesnirID') "+
                        "and quesInQuesnir.uuid=quesnir.uuid and quesnir.measure='"+uuid+"') "+
                        "and qId.uuid in "+
                        "(select uuid from latestNonDeletedArchEntIdentifiers where AttributeID=(select AttributeID from AttributeKey where AttributeName='QuestionUuid'))) t2 "+
                        "on t1.quesId=t2.quesId group by t1.quesId";
                Statement s3=conn.createStatement();
                ResultSet rQuesLs=s3.executeQuery(loadQuesContentandOrderQuery);
                JSONArray quesLs=new JSONArray();

                while(rQuesLs.next()){
                    JSONObject ques=new JSONObject();
                    ques.put("QuestionUuid", rQuesLs.getString("qid"));
                    ques.put("QuestionOrder",rQuesLs.getString("qorder"));
                    ques.put("Question",rQuesLs.getString("qContent"));
                    quesLs.add(ques);
                }
                obj.put("QuestionList", quesLs);
                String newFileName=qlabel+"-Questionnaire"+".json";
                FileWriter fw=new FileWriter("files/"+newFileName);
                fw.write(obj.toJSONString());
                fw.flush();
                fw.close();
                quesnirFiles.put(uuid, newFileName);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

}