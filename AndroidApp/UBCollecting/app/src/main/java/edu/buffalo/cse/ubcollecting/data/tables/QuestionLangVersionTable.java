package edu.buffalo.cse.ubcollecting.data.tables;

/**
 * Created by aamel786 on 2/17/18.
 */

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

import edu.buffalo.cse.ubcollecting.QuestionLangVersionActivity;
import edu.buffalo.cse.ubcollecting.data.DatabaseHelper;
import edu.buffalo.cse.ubcollecting.data.models.Language;
import edu.buffalo.cse.ubcollecting.data.models.QuestionLangVersion;

import static edu.buffalo.cse.ubcollecting.data.tables.LanguageTable.ENGLISH_LANG_NAME;

public class QuestionLangVersionTable extends Table<QuestionLangVersion> {

    public static final String TABLE = "QuestionLangVersion";

    // QuestionLangVersion Table - column names
    public static final String KEY_ID = "id";
    public static final String KEY_QUESTION_ID = "QuestionId";
    public static final String KEY_QUESTION_LANG_ID = "QuestionLanguageId";
    public static final String KEY_QUESTION_TEXT = "QuestionText";


    public QuestionLangVersionTable() {
        super();
        activityClass = QuestionLangVersionActivity.class;
    }

    @Override
    public String createTable() {
        return "CREATE TABLE "
                + TABLE + "(" + KEY_ID + " TEXT, " + KEY_QUESTION_ID + " TEXT, "
                + KEY_QUESTION_LANG_ID + " TEXT," + KEY_QUESTION_TEXT + " VARCHAR,"
                + "PRIMARY KEY(" + KEY_QUESTION_ID + ", " + KEY_QUESTION_LANG_ID + "),"
                + " FOREIGN KEY(" + KEY_QUESTION_LANG_ID + ") REFERENCES " + LanguageTable.TABLE
                + " (" + LanguageTable.KEY_ID + ")," + " FOREIGN KEY(" + KEY_QUESTION_ID + ") REFERENCES " + QuestionTable.TABLE
                + " (" + QuestionTable.KEY_ID + ")" + ")";
    }

    @Override
    public String getTableName() {
        return TABLE;
    }


    /**
     * Function that returns a HashMap based on the passed in questionId where  keys are
     * of type {@link Language} and the values are of type {@link QuestionLangVersion}. Essentially
     * a way to get all the different languages and their corresponding question text for the passed
     * in questionId
     * @param quesId The questionId you want to search the SQlite table for all matching entries
     * @return {@link HashMap} where keys are {@link Language} and values are {@link QuestionLangVersion}
     */
    public HashMap<Language,QuestionLangVersion> getQuestionTexts(String quesId){

        String selection = KEY_QUESTION_ID + " = ?";

        String[] selectionArgs = {quesId};

        ArrayList<QuestionLangVersion> questionTexts = DatabaseHelper.QUESTION_LANG_VERSION_TABLE.getAll(selection, selectionArgs,null);

        HashMap<Language,QuestionLangVersion > questions = new HashMap<>();

        for (QuestionLangVersion question: questionTexts){

            Language lang = DatabaseHelper.LANGUAGE_TABLE.findById(question.getQuestionLanguageId());

            questions.put(lang,question);
        }

        return questions;
    }

    /**
     * Returns the text of a question in English
     * @param quesId The questionId you want to search the SQlite table for all matching entries
     * @return a {@link QuestionLangVersion}
     */
    public QuestionLangVersion getQuestionTextInEnglish(String quesId){

        String selection = KEY_QUESTION_ID + " = ?";

        String[] selectionArgs = {quesId};

        ArrayList<QuestionLangVersion> questionTexts = DatabaseHelper.QUESTION_LANG_VERSION_TABLE.getAll(selection, selectionArgs,null);

        for (QuestionLangVersion question: questionTexts){

            Language lang = DatabaseHelper.LANGUAGE_TABLE.findById(question.getQuestionLanguageId());

            if (lang.getIdentifier().equals(ENGLISH_LANG_NAME)) {
                return question;
            }
        }
        Log.e(TAG, "No English version of question " + quesId + " found");
        return null;
    }

}
