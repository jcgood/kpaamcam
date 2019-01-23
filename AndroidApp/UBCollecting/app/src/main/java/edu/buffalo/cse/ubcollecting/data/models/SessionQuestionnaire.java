package edu.buffalo.cse.ubcollecting.data.models;

public class SessionQuestionnaire extends Model {

    private static final String TAG = SessionQuestionnaire.class.getSimpleName().toString();

    private String questionnaire_id;
    private String sessionId;
    private String lastQuestionAnswered;
    private String label;
    private String version;
    private String notes;
    private String date;



    public String getIdentifier(){
        return label;
    }

    public String getQuestionnaire_id() {
        return questionnaire_id;
    }

    public void setQuestionnaire_id(String questionnaire_id) {
        this.questionnaire_id = questionnaire_id;
    }
    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }


    public String getLastQuestionAnswered() {
        return lastQuestionAnswered;
    }

    public void setLastQuestionAnswered(String lastQuestionAnswered) {
        this.lastQuestionAnswered = lastQuestionAnswered;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
