package edu.buffalo.cse.ubcollecting.data.models;

import android.util.Log;

/**
 * Created by aamel786 on 2/17/18.
 */

public class QuestionnaireContent extends Model implements Comparable<QuestionnaireContent> {

    private static final String TAG = QuestionnaireContent.class.getSimpleName().toString();

    public double version;
    public String notes;
    public int deleted;
    public String questionnaireId;
    public String questionId;
    public int questionOrder;
    public String ParentQuestionnaireContent;
    public int  isParent;



    public char wf;

    public double getVersion(){
        return version;
    }

    public void setVersion(double version){
        this.version=version;
    }

    public int getDeleted(){
        return deleted;
    }

    public void setDeleted(int deleted){
        this.deleted=deleted;
    }

    public void setNotes(String note){
        this.notes=note;
    }

    public String getNotes(){
        return  notes;
    }

    public String getIdentifier() {
        return "Questionnaire: " + questionnaireId + "  Question: " + questionId + "  Order: " + Integer.toString(questionOrder);
    }

    public String getQuestionnaireId() {
        return questionnaireId;
    }

    public void setQuestionnaireId(String questionnaireId) {
        this.questionnaireId = questionnaireId;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public int getQuestionOrder() {
        return questionOrder;
    }

    public void setQuestionOrder(int questionOrder) {
        this.questionOrder = questionOrder;
    }

    public String getParentQuestionnaireContent(){
        return this.ParentQuestionnaireContent;
    }

    public void setParentQuestionnaireContent(String qcId){
        this.ParentQuestionnaireContent = qcId;
    }

    public int getIsParent(){

        return this.isParent;
    }
    public void setIsParent(int isParent){
        this.isParent = isParent;
    }


    @Override
    public int compareTo(QuestionnaireContent other) {
        return Integer.compare(questionOrder, other.getQuestionOrder());
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof QuestionnaireContent)) {
            return false;
        }
        QuestionnaireContent content = (QuestionnaireContent) other;
        return content.getQuestionId().equals(questionId) &&
                content.getQuestionnaireId().equals(questionnaireId);
    }

}
