package edu.buffalo.cse.ubcollecting.data.models;

public class SessionQuestion extends Model {


    private static final String TAG = SessionQuestion.class.getSimpleName().toString();

    private String label;
    private String sessionQuestionnaireId;
    private String questionCompleted;
    public String  version;
    private String notes;
    private String date;


    @Override
    public String getIdentifier() {
        return null;
    }

    public String getSessionQuestionnaireId() {
        return sessionQuestionnaireId;
    }

    public void setSessionQuestionnaireId(String sessionQuestionaireId) {
        this.sessionQuestionnaireId = sessionQuestionaireId;
    }

    public String getQuestionCompleted() {
        return questionCompleted;
    }

    public void setQuestionCompleted(String questionCompleted) {
        this.questionCompleted = questionCompleted;
    }
    public String getVersion(){return version;}

    public void setVersion(String version){
        this.version = version;
    }

    public String getNotes(){return notes;}

    public void setNotes(String notes){
        this.notes = notes;
    }
    public String getDate(){
        return date;
    }
    public void setDate(String date){
        this.date = date;
    }
}
