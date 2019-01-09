package edu.buffalo.cse.ubcollecting.data.models;

/**
 * Created by aamel786 on 2/17/18.
 */

public class QuestionLangVersion extends Model {

    private static final String TAG = QuestionLangVersion.class.getSimpleName().toString();

    public String questionId;
    public String questionLanguageId;
    public String questionText;

    public String getIdentifier() {
        String identifier = questionText;
        if (identifier.length() <= 15) {
            return identifier;
        } else {
            return identifier.substring(0,15) + "...";
        }
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

    public String getQuestionText() {
        return questionText;
    }

    public String getTextSummary() {
        String summary;
        if (getQuestionText().length() < 20) {
            summary = getQuestionText();
        } else {
            summary = getQuestionText().substring(0, 20);
        }
        return summary;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof QuestionLangVersion) {
            QuestionLangVersion questionLang = (QuestionLangVersion) obj;
            return questionLang.getQuestionId().equals(getQuestionId()) &&
                    questionLang.getQuestionLanguageId().equals(getQuestionLanguageId());
        }

        return false;
    }
}
