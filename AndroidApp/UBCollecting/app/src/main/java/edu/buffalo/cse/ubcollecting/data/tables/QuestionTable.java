package edu.buffalo.cse.ubcollecting.data.tables;

/**
 * Created by aamel786 on 2/17/18.
 */

import java.util.ArrayList;

import edu.buffalo.cse.ubcollecting.data.models.Question;
import edu.buffalo.cse.ubcollecting.ui.AddQuestionsActivity;
import edu.buffalo.cse.ubcollecting.data.DatabaseHelper;

public class QuestionTable extends Table<Question> {

    public static final String TABLE = "Question";

    // Question (QuestionPool) Table - column names
    public static final String KEY_ID = "id";
    public static final String KEY_TYPE = "Type";
    public static final String KEY_DISPLAY_TEXT = "DisplayText";

    public static final String KEY_VERSION ="VersionNumber";
    public static final String KEY_NOTES ="Notes";
    public static final String KEY_DELETED ="Deleted";
    public static final String KEY_MIN_LENGTH = "MinLength";
    public static final String KEY_MAX_LENGTH = "MaxLength";
    public static final String KEY_NULL_CHECK_TYPE = "NullCheckType";


    public QuestionTable() {
        super();
        activityClass = AddQuestionsActivity.class;
    }

    @Override
    public String createTable() {
//        Log.i("QuestionTable", TABLE);
        return "CREATE TABLE "
                + TABLE + "(" + KEY_ID + " TEXT PRIMARY KEY NOT NULL, "
                + KEY_TYPE + " VARCHAR, "
                + KEY_DISPLAY_TEXT + " VARCHAR,"
                + KEY_VERSION + " NUMERIC DEFAULT 1.0 NOT NULL,"
                + KEY_NOTES + " VARCHAR DEFAULT '',"
                + KEY_DELETED + " INTEGER DEFAULT 0 NOT NULL,"
                + KEY_MIN_LENGTH + " INTEGER,"
                + KEY_MAX_LENGTH + " INTEGER,"
                + KEY_NULL_CHECK_TYPE + " VARCHAR" + ")";
    }

    @Override
    public String getTableName() {
        return TABLE;
    }

    public String[] getNullCheckAndLength(String id) {

      ArrayList<Question> questionTexts = DatabaseHelper.QUESTION_TABLE.getAll();
      Question questionText = null;
      for(int i = 0; i < questionTexts.size(); i++) {
        if(questionTexts.get(i).id.equals(id)) {
          questionText = questionTexts.get(i);
          break;
        }
      }
      String[] nullCheckAndLength = new String[]{Integer.toString(questionText.getMinLength()),  Integer.toString(questionText.getMaxLength()), questionText.getNullCheckType()};
      return nullCheckAndLength;
    }
}
