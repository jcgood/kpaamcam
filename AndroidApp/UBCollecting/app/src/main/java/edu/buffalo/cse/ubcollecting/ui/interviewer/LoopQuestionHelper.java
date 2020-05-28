package edu.buffalo.cse.ubcollecting.ui.interviewer;

import java.util.HashMap;

import edu.buffalo.cse.ubcollecting.data.DatabaseHelper;
import edu.buffalo.cse.ubcollecting.data.models.Language;
import edu.buffalo.cse.ubcollecting.data.models.QuestionLangVersion;

class LoopQuestionHelper {

    // Parses the Loop Question format into question Ids and question texts
    static HashMap<String, String> createQuestionHashMap(String questionString) {
        HashMap<String, String> questionHashMap = new HashMap<>();

        int index = 0;
        for (int i = 0; i < questionString.length(); i++) {
            if (questionString.charAt(i) == '#' || questionString.charAt(i) == '|') {
                String questionId = questionString.substring(index, i);
                index = i + 1;
                HashMap<Language, QuestionLangVersion> langVersionHashMap =
                        DatabaseHelper.QUESTION_LANG_VERSION_TABLE.getQuestionTexts(questionId);
                questionHashMap.put(questionId, langVersionHashMap.get(langVersionHashMap.keySet().iterator().next()).questionText);
            }
            if (i == (questionString.length() - 1)) {
                String questionId = questionString.substring(index);
                HashMap<Language, QuestionLangVersion> langVersionHashMap =
                        DatabaseHelper.QUESTION_LANG_VERSION_TABLE.getQuestionTexts(questionId);
                questionHashMap.put(questionId, langVersionHashMap.get(langVersionHashMap.keySet().iterator().next()).questionText);
            }
        }

        return questionHashMap;
    }
}
