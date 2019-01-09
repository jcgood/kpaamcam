package edu.buffalo.cse.ubcollecting.data.models;

/**
 * Created by aamel786 on 2/17/18.
 */

public class QuestionOption extends Model {

    private static final String TAG = QuestionOption.class.getSimpleName().toString();


    public String questionId;
    public String questionLanguageId;
    public String optionText;


    public String getIdentifier() {
        return optionText;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getQuestionLanguageId() {
        return questionLanguageId;
    }

    public void setQuestionLanguageId(String questionLanguageId) {
        this.questionLanguageId = questionLanguageId;
    }

    public String getOptionText() {
        return optionText;
    }

    public void setOptionText(String optionText) {
        this.optionText = optionText;
    }


}
