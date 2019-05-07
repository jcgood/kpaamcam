package edu.buffalo.cse.ubcollecting.ui.interviewer;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Map;

import edu.buffalo.cse.ubcollecting.R;
import edu.buffalo.cse.ubcollecting.data.DatabaseHelper;
import edu.buffalo.cse.ubcollecting.data.models.Answer;
import edu.buffalo.cse.ubcollecting.data.models.QuestionnaireContent;
import edu.buffalo.cse.ubcollecting.data.tables.AnswerTable;

import static edu.buffalo.cse.ubcollecting.SessionActivity.getSession;
import static edu.buffalo.cse.ubcollecting.ui.interviewer.TakeQuestionnaireActivity.getQuestionnaire;
import static edu.buffalo.cse.ubcollecting.ui.interviewer.UserSelectQuestionnaireActivity.SELECTED_QUESTIONNAIRE;
import static edu.buffalo.cse.ubcollecting.ui.interviewer.UserSelectSessionActivity.SELECTED_SESSION;

public class ListFragment extends Fragment {
    RecyclerView answerList;
    EntryAdapter entryAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        answerList = (RecyclerView) view.findViewById(R.id.answer_list);
        ArrayList<String> list = new ArrayList<String>();
        list.add("hello");
        list.add("what's good");
        entryAdapter = new EntryAdapter(list);
        answerList.setAdapter(entryAdapter);

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private class EntryHolder extends RecyclerView.ViewHolder {


        private Button deleteButton;
        private EditText answerEntry;


        public EntryHolder(View view) {
            super(view);
            deleteButton = view.findViewById(R.id.entry_list_delete_button);
            answerEntry = view.findViewById(R.id.answer_entry_item);

        }

        public void bindEntry(String entry, final int position) {
            answerEntry.setText(entry);
        }
    }

    private class EntryAdapter extends  RecyclerView.Adapter<EntryHolder> {
        ArrayList<String> list;
        public EntryAdapter(ArrayList<String> list){
            this.list = list;
        }

        @Override
        public EntryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater
                    .inflate(R.layout.entry_list_item_view2, parent, false);
            return null;
        }

        @Override
        public void onBindViewHolder(EntryHolder holder, int position) {
            String entry = list.get(position);
            holder.bindEntry(entry, position);


        }

        @Override
        public int getItemCount() {
            return 0;
        }
    }

}
