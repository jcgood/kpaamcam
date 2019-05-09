package edu.buffalo.cse.ubcollecting.ui;

import java.util.ArrayList;

import edu.buffalo.cse.ubcollecting.data.models.Answer;
import edu.buffalo.cse.ubcollecting.data.models.Questionnaire;
import edu.buffalo.cse.ubcollecting.data.models.QuestionnaireContent;

/**
 * Created by aamel786 on 6/18/18.
 */

/**
 * Interface used simply to access methods of TakeQuestionnaireActivity from QuestionFragment in decoupled manner.
 */
public interface QuestionManager {

    public void getNextQuestion();

    public void startLoop(ArrayList<Answer> answers);

    public boolean isLastQuestion();

    public void saveAndQuitQuestionnaire(QuestionnaireContent questionnaireContent);

}
