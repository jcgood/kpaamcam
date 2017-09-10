/**
 * Created by ning on 5/2/17.
 */
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.logging.*;
import java.util.*;
public class Log {
    public Logger createLog(String path, String userName){
        if(userName==null || userName.length()==0) userName="defaultUser";
        Logger log = Logger.getLogger("logFile");
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = Calendar.getInstance().getTime();
        String dateInfo=dateFormat.format(date);
        String logPath=path+"/"+"log.log";
        try{
            Handler fileHandler=new FileHandler(logPath);
            SimpleFormatter formatter = new SimpleFormatter();
            fileHandler.setFormatter(formatter);
            fileHandler.setLevel(Level.ALL);
            log.addHandler(fileHandler);
            log.setLevel(Level.ALL);
            log.config("Configuration done");

        }
        catch (Exception e){
            e.printStackTrace();
            log.log(Level.SEVERE, "Error occur in FileHandler.", e);
        }
        log.info(dateInfo+ "\n User:" +userName);
        return log;
    }

}
