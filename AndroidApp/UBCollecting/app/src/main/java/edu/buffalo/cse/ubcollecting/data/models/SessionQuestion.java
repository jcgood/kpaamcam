package edu.buffalo.cse.ubcollecting.data.models;

public class SessionQuestion extends Model {


    private static final String TAG = SessionQuestion.class.getSimpleName().toString();

    private String label;
    private String sessionQuestionnaireId;
    private String questionCompleted;



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
}
