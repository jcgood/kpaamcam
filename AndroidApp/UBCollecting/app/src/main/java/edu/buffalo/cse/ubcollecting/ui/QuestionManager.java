package edu.buffalo.cse.ubcollecting.ui;

import edu.buffalo.cse.ubcollecting.data.models.Questionnaire;

/**
 * Created by aamel786 on 6/18/18.
 */

/**
 * Interface used simply to access methods of TakeQuestionnaireActivity from QuestionFragment in decoupled manner.
 */
public interface QuestionManager {

    public void getNextQuestion();

    public boolean isLastQuestion();

}
