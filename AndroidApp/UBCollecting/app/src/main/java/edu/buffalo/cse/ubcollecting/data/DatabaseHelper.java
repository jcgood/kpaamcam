package edu.buffalo.cse.ubcollecting.data;

/**
 * Created by Aamel Unia on 2/17/18.
 */

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

import edu.buffalo.cse.ubcollecting.app.App;
import edu.buffalo.cse.ubcollecting.data.models.Language;
import edu.buffalo.cse.ubcollecting.data.models.LanguageType;
import edu.buffalo.cse.ubcollecting.data.models.QuestionPropertyDef;
import edu.buffalo.cse.ubcollecting.data.models.QuestionnaireType;
import edu.buffalo.cse.ubcollecting.data.models.Role;
import edu.buffalo.cse.ubcollecting.data.tables.AnswerTable;
import edu.buffalo.cse.ubcollecting.data.tables.FieldTripTable;
import edu.buffalo.cse.ubcollecting.data.tables.FileTable;
import edu.buffalo.cse.ubcollecting.data.tables.LanguageTable;
import edu.buffalo.cse.ubcollecting.data.tables.LanguageTypeTable;
import edu.buffalo.cse.ubcollecting.data.tables.PersonTable;
import edu.buffalo.cse.ubcollecting.data.tables.QuestionLangVersionTable;
import edu.buffalo.cse.ubcollecting.data.tables.QuestionOptionTable;
import edu.buffalo.cse.ubcollecting.data.tables.QuestionPropertyDefTable;
import edu.buffalo.cse.ubcollecting.data.tables.QuestionPropertyTable;
import edu.buffalo.cse.ubcollecting.data.tables.QuestionTable;
import edu.buffalo.cse.ubcollecting.data.tables.QuestionnaireContentTable;
import edu.buffalo.cse.ubcollecting.data.tables.QuestionnaireTable;
import edu.buffalo.cse.ubcollecting.data.tables.QuestionnaireTypeTable;
import edu.buffalo.cse.ubcollecting.data.tables.RoleTable;
import edu.buffalo.cse.ubcollecting.data.tables.SessionAnswerTable;
import edu.buffalo.cse.ubcollecting.data.tables.SessionPersonTable;
import edu.buffalo.cse.ubcollecting.data.tables.SessionQuestionTable;
import edu.buffalo.cse.ubcollecting.data.tables.SessionQuestionnaireTable;
import edu.buffalo.cse.ubcollecting.data.tables.SessionTable;
import edu.buffalo.cse.ubcollecting.data.tables.Table;
import edu.buffalo.cse.ubcollecting.utils.Constants;

import static edu.buffalo.cse.ubcollecting.data.tables.LanguageTable.ENGLISH_LANG_NAME;


public class DatabaseHelper extends SQLiteOpenHelper {

    public static final AnswerTable ANSWER_TABLE = new AnswerTable();
    public static final FieldTripTable FIELD_TRIP_TABLE = new FieldTripTable();
    public static final FileTable FILE_TABLE = new FileTable();
    public static final LanguageTable LANGUAGE_TABLE = new LanguageTable();
    public static final LanguageTypeTable LANGUAGE_TYPE_TABLE = new LanguageTypeTable();
    public static final PersonTable PERSON_TABLE = new PersonTable();
    public static final QuestionTable QUESTION_TABLE = new QuestionTable();
    public static final QuestionLangVersionTable QUESTION_LANG_VERSION_TABLE = new QuestionLangVersionTable();
    public static final QuestionnaireTable QUESTIONNAIRE_TABLE = new QuestionnaireTable();
    public static final QuestionnaireContentTable QUESTIONNAIRE_CONTENT_TABLE = new QuestionnaireContentTable();
    public static final QuestionnaireTypeTable QUESTIONNAIRE_TYPE_TABLE = new QuestionnaireTypeTable();
    public static final QuestionOptionTable QUESTION_OPTION_TABLE = new QuestionOptionTable();
    public static final QuestionPropertyTable QUESTION_PROPERTY_TABLE = new QuestionPropertyTable();
    public static final RoleTable ROLE_TABLE = new RoleTable();
    public static final SessionTable SESSION_TABLE = new SessionTable();
    public static final SessionAnswerTable SESSION_ANSWER_TABLE = new SessionAnswerTable();
    public static final SessionPersonTable SESSION_PERSON_TABLE = new SessionPersonTable();
    public static final QuestionPropertyDefTable QUESTION_PROPERTY_DEF_TABLE = new QuestionPropertyDefTable();
    public static final SessionQuestionnaireTable SESSION_QUESTIONNAIRE_TABLE = new SessionQuestionnaireTable();
    public static final SessionQuestionTable SESSION_QUESTION_TABLE = new SessionQuestionTable();
    public static final List<Table<?>> TABLES = Arrays.asList(
            ANSWER_TABLE,
            FIELD_TRIP_TABLE,
            FILE_TABLE,
            LANGUAGE_TABLE,
            LANGUAGE_TYPE_TABLE,
            PERSON_TABLE,
            QUESTION_TABLE,
            QUESTION_LANG_VERSION_TABLE,
            QUESTIONNAIRE_TABLE,
            QUESTIONNAIRE_CONTENT_TABLE,
            QUESTIONNAIRE_TYPE_TABLE,
            QUESTION_OPTION_TABLE,
            QUESTION_PROPERTY_TABLE,
            ROLE_TABLE,
            SESSION_TABLE,
            SESSION_ANSWER_TABLE,
            SESSION_PERSON_TABLE,
            QUESTION_PROPERTY_DEF_TABLE,
            SESSION_QUESTIONNAIRE_TABLE,
            SESSION_QUESTION_TABLE
    );

    private static final String TAG = DatabaseHelper.class.getSimpleName().toString();
    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "Collection";


    public DatabaseHelper() {
        super(App.getContext(), DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Method that pre-populates local SQlite database on phone with necessary content
     */

    public static void populateData() {
        FireBaseCloudHelper fireBaseCloudHelper = new FireBaseCloudHelper(App.getContext());
        Role admin = new Role();
        admin.setName(Constants.ROLE_ADMIN);
        admin.setIntroRequired(0);
        admin.setPhotoRequired(0);
        admin.setOnClient(0);
        /* INSERT */
        try {
            fireBaseCloudHelper.insert(ROLE_TABLE, admin);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            Log.i(TAG, "Could not access server database (Firebase)");
            e.printStackTrace();
        }
        ROLE_TABLE.insert(admin);

        Role consultant = new Role();
        consultant.setName(Constants.ROLE_CONSULTANT);
        consultant.setIntroRequired(1);
        consultant.setPhotoRequired(1);
        consultant.setOnClient(1);
        /* INSERT */
        try {
            fireBaseCloudHelper.insert(ROLE_TABLE, consultant);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            Log.i(TAG, "Could not access server database (Firebase)");
            e.printStackTrace();
        }
        ROLE_TABLE.insert(consultant);

        Role interviewer = new Role();
        interviewer.setName(Constants.ROLE_INTERVIEWER);
        interviewer.setIntroRequired(0);
        interviewer.setPhotoRequired(0);
        interviewer.setOnClient(1);
        /* INSERT */
        try {
            fireBaseCloudHelper.insert(ROLE_TABLE, interviewer);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            Log.i(TAG, "Could not access server database (Firebase)");
            e.printStackTrace();
        }
        ROLE_TABLE.insert(interviewer);

        QuestionnaireType regType = new QuestionnaireType();
        regType.setName(Constants.REGULAR);
        /* INSERT */
        try {
            fireBaseCloudHelper.insert(QUESTIONNAIRE_TYPE_TABLE, regType);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            Log.i(TAG, "Could not access server database (Firebase)");
            e.printStackTrace();
        }
        QUESTIONNAIRE_TYPE_TABLE.insert(regType);

        QuestionnaireType introType = new QuestionnaireType();
        introType.setName(Constants.INTRODUCTORY);
        /* INSERT */
        try {
            fireBaseCloudHelper.insert(QUESTIONNAIRE_TYPE_TABLE, introType);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            Log.i(TAG, "Could not access server database (Firebase)");
            e.printStackTrace();
        }
        QUESTIONNAIRE_TYPE_TABLE.insert(introType);

        QuestionnaireType sclType = new QuestionnaireType();
        sclType.setName(Constants.SOCIOLINGUISTIC);
        /* INSERT */
        try {
            fireBaseCloudHelper.insert(QUESTIONNAIRE_TYPE_TABLE, sclType);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            Log.i(TAG, "Could not access server database (Firebase)");
            e.printStackTrace();
        }
        QUESTIONNAIRE_TYPE_TABLE.insert(sclType);

        QuestionnaireType emrType = new QuestionnaireType();
        emrType.setName(Constants.EMERGENCY);
        /* INSERT */
        try {
            fireBaseCloudHelper.insert(QUESTIONNAIRE_TYPE_TABLE, emrType);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            Log.i(TAG, "Could not access server database (Firebase)");
            e.printStackTrace();
        }
        QUESTIONNAIRE_TYPE_TABLE.insert(emrType);

        QuestionnaireType testType = new QuestionnaireType();
        testType.setName(Constants.TEST);
        /* INSERT */
        try {
            fireBaseCloudHelper.insert(QUESTIONNAIRE_TYPE_TABLE, testType);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            Log.i(TAG, "Could not access server database (Firebase)");
            e.printStackTrace();
        }
        QUESTIONNAIRE_TYPE_TABLE.insert(testType);

        QuestionPropertyDef audProperty = new QuestionPropertyDef();
        audProperty.setName(Constants.AUDIO);
        /* INESRT */
        try {
            fireBaseCloudHelper.insert(QUESTION_PROPERTY_DEF_TABLE, audProperty);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            Log.i(TAG, "Could not access server database (Firebase)");
            e.printStackTrace();
        }
        QUESTION_PROPERTY_DEF_TABLE.insert(audProperty);

        QuestionPropertyDef vidProperty = new QuestionPropertyDef();
        vidProperty.setName(Constants.VIDEO);
        /* INSERT */
        try {
            fireBaseCloudHelper.insert(QUESTION_PROPERTY_DEF_TABLE, vidProperty);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            Log.i(TAG, "Could not access server database (Firebase)");
            e.printStackTrace();
        }
        QUESTION_PROPERTY_DEF_TABLE.insert(vidProperty);

        QuestionPropertyDef phtProperty = new QuestionPropertyDef();
        phtProperty.setName(Constants.PHOTO);
        /* INSERT */
        try {
            fireBaseCloudHelper.insert(QUESTION_PROPERTY_DEF_TABLE, phtProperty);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            Log.i(TAG, "Could not access server database (Firebase)");
            e.printStackTrace();
        }
        QUESTION_PROPERTY_DEF_TABLE.insert(phtProperty);

        QuestionPropertyDef freeProperty = new QuestionPropertyDef();
        freeProperty.setName(Constants.TEXT);
        /* INSERT */
        try {
            fireBaseCloudHelper.insert(QUESTION_PROPERTY_DEF_TABLE, freeProperty);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            Log.i(TAG, "Could not access server database (Firebase)");
            e.printStackTrace();
        }
        QUESTION_PROPERTY_DEF_TABLE.insert(freeProperty);

        //Roopa
        QuestionPropertyDef listProperty = new QuestionPropertyDef();
        listProperty.setName(Constants.LIST);
        /* INSERT */
        try {
            fireBaseCloudHelper.insert(QUESTION_PROPERTY_DEF_TABLE, listProperty);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            Log.i(TAG, "Could not access server database (Firebase)");
            e.printStackTrace();
        }
        QUESTION_PROPERTY_DEF_TABLE.insert(listProperty);

        QuestionPropertyDef loopProperty = new QuestionPropertyDef();
        loopProperty.setName(Constants.LOOP);
        /* INSERT */
        try {
            fireBaseCloudHelper.insert(QUESTION_PROPERTY_DEF_TABLE, loopProperty);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            Log.i(TAG, "Could not access server database (Firebase)");
            e.printStackTrace();
        }
        QUESTION_PROPERTY_DEF_TABLE.insert(loopProperty);

        //Roopa
//
//        QuestionPropertyDef fileProperty = new QuestionPropertyDef();
//        fileProperty.setName("File");
//        QUESTION_PROPERTY_DEF_TABLE.insert(fileProperty);

//        QuestionPropertyDef llProperty = new QuestionPropertyDef();
//        llProperty.setName("LangList");
//        QUESTION_PROPERTY_DEF_TABLE.insert(llProperty);
//
//        QuestionPropertyDef lleProperty = new QuestionPropertyDef();
//        lleProperty.setName("LangListEnd");
//        QUESTION_PROPERTY_DEF_TABLE.insert(lleProperty);
//
//        QuestionPropertyDef oneProperty = new QuestionPropertyDef();
//        oneProperty.setName("ChooseOne");
//        QUESTION_PROPERTY_DEF_TABLE.insert(oneProperty);
//
//        QuestionPropertyDef multProperty = new QuestionPropertyDef();
//        multProperty.setName("SelectMultiple");
//        QUESTION_PROPERTY_DEF_TABLE.insert(multProperty);
//
//        QuestionPropertyDef emgProperty = new QuestionPropertyDef();
//        emgProperty.setName("Emergency");
//        QUESTION_PROPERTY_DEF_TABLE.insert(emgProperty);
//
//        QuestionPropertyDef checkProperty = new QuestionPropertyDef();
//        checkProperty.setName("CheckList");
//        QUESTION_PROPERTY_DEF_TABLE.insert(checkProperty);
//
//        QuestionPropertyDef dateProperty = new QuestionPropertyDef();
//        dateProperty.setName("Date");
//        QUESTION_PROPERTY_DEF_TABLE.insert(dateProperty);

        LanguageType lwc = new LanguageType();
        lwc.setName("LWC");
        /* INSERT */
        try {
            fireBaseCloudHelper.insert(LANGUAGE_TYPE_TABLE, lwc);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            Log.i(TAG, "Could not access server database (Firebase)");
            e.printStackTrace();
        }
        LANGUAGE_TYPE_TABLE.insert(lwc);

        LanguageType researchLang = new LanguageType();
        researchLang.setName("Research Language");
        /* INSERT */
        try {
            fireBaseCloudHelper.insert(LANGUAGE_TYPE_TABLE, researchLang);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            Log.i(TAG, "Could not access server database (Firebase)");
            e.printStackTrace();
        }
        LANGUAGE_TYPE_TABLE.insert(researchLang);

        LanguageType reg = new LanguageType();
        reg.setName("Regional");
        /* INSERT */
        try {
            fireBaseCloudHelper.insert(LANGUAGE_TYPE_TABLE, reg);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            Log.i(TAG, "Could not access server database (Firebase)");
            e.printStackTrace();
        }
        LANGUAGE_TYPE_TABLE.insert(reg);

        Language english = new Language();
        english.setName(ENGLISH_LANG_NAME);
        english.setTypeId(researchLang.getId());
        /* INSERT */
        try {
            fireBaseCloudHelper.insert(LANGUAGE_TYPE_TABLE, english);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            Log.i(TAG, "Could not access server database (Firebase)");
            e.printStackTrace();
        }
        LANGUAGE_TABLE.insert(english);

        Language french = new Language();
        french.setName("French");
        french.setTypeId(researchLang.getId());
        /* INSERT */
        try {
            fireBaseCloudHelper.insert(LANGUAGE_TYPE_TABLE, french);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            Log.i(TAG, "Could not access server database (Firebase)");
            e.printStackTrace();
        }
        LANGUAGE_TABLE.insert(french);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("PRAGMA foreign_keys=ON");
        //Creating The Tables
        db.execSQL(PERSON_TABLE.createTable());
        db.execSQL(QUESTION_TABLE.createTable());
        db.execSQL(QUESTION_OPTION_TABLE.createTable());
        db.execSQL(QUESTION_PROPERTY_TABLE.createTable());
        db.execSQL(QUESTION_PROPERTY_DEF_TABLE.createTable());
        db.execSQL(QUESTION_LANG_VERSION_TABLE.createTable());
        db.execSQL(QUESTIONNAIRE_TABLE.createTable());
        db.execSQL(QUESTIONNAIRE_TYPE_TABLE.createTable());
        db.execSQL(QUESTIONNAIRE_CONTENT_TABLE.createTable());
        db.execSQL(LANGUAGE_TABLE.createTable());
        db.execSQL(LANGUAGE_TYPE_TABLE.createTable());
        db.execSQL(ROLE_TABLE.createTable());
        db.execSQL(FIELD_TRIP_TABLE.createTable());
        db.execSQL(FILE_TABLE.createTable());
        db.execSQL(SESSION_TABLE.createTable());
        db.execSQL(SESSION_ANSWER_TABLE.createTable());
        db.execSQL(SESSION_PERSON_TABLE.createTable());
        db.execSQL(SESSION_QUESTIONNAIRE_TABLE.createTable());
        db.execSQL((SESSION_QUESTION_TABLE.createTable()));
        db.execSQL(ANSWER_TABLE.createTable());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.d(TAG, String.format("SQLiteDatabase.onUpgrade(%d -> %d)", oldVersion, newVersion));

        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + PersonTable.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + QuestionTable.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + QuestionOptionTable.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + QuestionPropertyTable.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + QuestionPropertyDefTable.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + QuestionLangVersionTable.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + QuestionnaireTable.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + QuestionnaireTypeTable.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + QuestionnaireContentTable.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + LanguageTypeTable.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + LanguageTable.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + RoleTable.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + FieldTripTable.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + FileTable.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + SessionTable.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + SessionAnswerTable.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + SessionPersonTable.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + SessionQuestionnaireTable.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + SessionQuestionTable.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + AnswerTable.TABLE);

        // create new tables
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
