package edu.buffalo.cse.ubcollecting.ui;

import android.content.Context;
import android.os.Bundle;
//import android.support.annotation.Nullable;
//import androidx.core.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.List;

import edu.buffalo.cse.ubcollecting.R;
import edu.buffalo.cse.ubcollecting.data.models.Questionnaire;
import edu.buffalo.cse.ubcollecting.data.models.QuestionnaireType;

import static edu.buffalo.cse.ubcollecting.data.DatabaseHelper.QUESTIONNAIRE_TYPE_TABLE;

public class CreateQuestionnaireFragment extends Fragment {


    private EditText nameField;
    private EditText labelField;
    private EditText descriptionField;
    private QuestionnaireManager questionnaireManager;
    private Spinner typeSpinner;
    private ArrayAdapter<QuestionnaireType> typeAdapter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        questionnaireManager = (QuestionnaireManager) context;

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_questionnaire, container, false);
        nameField = view.findViewById(R.id.questionnaire_name_field);
        labelField = view.findViewById(R.id.questionnaire_label_field);
        descriptionField = view.findViewById(R.id.questionnaire_description_field);
        typeSpinner =  view.findViewById(R.id.questionnaire_type_spinner);

        List<QuestionnaireType> types = QUESTIONNAIRE_TYPE_TABLE.getAll();
        typeAdapter = new ArrayAdapter<QuestionnaireType>(getContext(),
                android.R.layout.simple_spinner_item,
                types);
        typeSpinner.setAdapter(typeAdapter);
        typeSpinner.setSelected(false);
        typeSpinner.setOnItemSelectedListener(new EntryOnItemSelectedListener<QuestionnaireType>());
        if(getArguments().getBoolean("Update")){
            setUI();
        }
        return view;
    }

    public void setUI(){
        Questionnaire entry = questionnaireManager.getQuestionnaireEntry();
        nameField.setText(entry.getName());
        labelField.setText(entry.getIdentifier());
        descriptionField.setText(entry.getDescription());

        for (int i = 0; i < typeAdapter.getCount(); i++) {
            QuestionnaireType type = typeAdapter.getItem(i);
            if (type.getId().equals(entry.getTypeId())) {
                break;
            }
        }
        typeSpinner.setSelection(0);
    }


    public String getName(){
        return nameField.getText().toString();
    }

    public String getDescription(){
        return descriptionField.getText().toString();
    }

    public String getType(){
        QuestionnaireType type = (QuestionnaireType) typeSpinner.getSelectedItem();
        return type.getId();
    }



}
