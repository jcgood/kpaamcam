package edu.buffalo.cse.ubcollecting.data.models;

public class SessionQuestionnaire extends Model {

    private static final String TAG = SessionQuestionnaire.class.getSimpleName().toString();

    private String questionnaire_id;
    private String sessionId;
    private String lastQuestionAnswered;
    private String label;


    public String getIdentifier(){
        return label;
    }
    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getQuestionnaire_id() {
        return questionnaire_id;
    }

    public void setQuestionnaire_id(String questionnaire_id) {
        this.questionnaire_id = questionnaire_id;
    }

    public String getLastQuestionAnswered() {
        return lastQuestionAnswered;
    }

    public void setLastQuestionAnswered(String lastQuestionAnswered) {
        this.lastQuestionAnswered = lastQuestionAnswered;
    }
}
