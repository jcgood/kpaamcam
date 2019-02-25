package edu.buffalo.cse.ubcollecting.data.models;

public class Loop extends Model {



    private static final String TAG = Loop.class.getSimpleName().toString();

    public String questionnaireId;
    public String iterations;
    public String startIndex;
    public String endIndex;
    public String version;
    public String notes;
    public String deleted;
    @Override
    public String getIdentifier() {
        return null;
    }


    public String getQuestionnaireContentId() {
        return questionnaireId;
    }

    public void setQuestionnaireId(String questionnaireId){
        this.questionnaireId = questionnaireId;
    }

    public String getIterations(){
        return iterations;
    }

    public void setIterations(String iterations){
        this.iterations = iterations;
    }

    public String getStartIndex(){
        return this.startIndex;
    }

    public void setStartIndex(String startIndex){
        this.startIndex = startIndex;
    }

    public String getEndIndex() {
        return this.endIndex;
    }
    public void setEndIndex(String endIndex){
        this.endIndex = endIndex;
    }
    public String getVersion() {
        return version;
    }

    public void setVersion(String Version){
        this.version = version;
    }


    public String getNotes() {
        return notes;
    }

    public void  setNotes(String notes){
        this.notes = notes;
    }

    public String getDeleted() {
        return deleted;
    }

    public void setDeleted(String deleted){
       this.deleted = deleted;
    }
}
