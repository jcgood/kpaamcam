package edu.buffalo.cse.ubcollecting.ui;

import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import edu.buffalo.cse.ubcollecting.data.models.Model;

/**
 * Created by kevinrathbun on 4/3/18.
 */

public class EntryOnItemSelectedListener<E extends Model> implements AdapterView.OnItemSelectedListener {
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        E type = (E) parent.getItemAtPosition(position);
        TextView listView = view.findViewById(android.R.id.text1);
        listView.setText(type.getIdentifier());
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
