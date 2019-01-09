package edu.buffalo.cse.ubcollecting.ui.interviewer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import edu.buffalo.cse.ubcollecting.R;
import edu.buffalo.cse.ubcollecting.data.DatabaseHelper;
import edu.buffalo.cse.ubcollecting.data.models.Person;
import edu.buffalo.cse.ubcollecting.data.models.Role;
import edu.buffalo.cse.ubcollecting.data.models.Session;
import edu.buffalo.cse.ubcollecting.data.models.SessionPerson;

import static edu.buffalo.cse.ubcollecting.ui.interviewer.UserLandingActivity.REQUEST_CODE_ADD_ENTRY;
import static edu.buffalo.cse.ubcollecting.ui.interviewer.UserLandingActivity.FLAG_INTERVIEWER_EDIT;
import static edu.buffalo.cse.ubcollecting.SessionActivity.getSession;
import static edu.buffalo.cse.ubcollecting.ui.interviewer.UserSelectSessionActivity.SELECTED_SESSION;



/**
 * Activity for assigning roles to a session.
 */

public class AddSessionRolesActivity extends AppCompatActivity {

    private Button createPerson;
    private TextView selectPerson;
    private ListView personListView;
    private ArrayList<Person> people;
    private ArrayAdapter<Person> personAdapter;
    private SearchView personSearch;
    private Spinner roleSpinner;
    private ArrayAdapter<Role> roleAdapter;
    private Button assignButton;
    private ListView assignedRolesListView;
    private Button continueButton;

    private int personSelectedIndex = -1;
    private HashSet<SessionPerson> assignedRoles;
    private ArrayList<String> allRolesAssigned;
    private ArrayList<String> assignedRolesUI;
    private HashMap<String, SessionPerson> uiMapping;
    private AssignedRolesAdapter assignedRolesAdapter;


    private HashMap<SessionPerson,ArrayList<Role>> rolesAlreadyAssigned;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_session_roles);

        allRolesAssigned = new ArrayList<>();
        assignedRolesUI = new ArrayList<>();
        assignedRoles = new HashSet<>();
        uiMapping = new HashMap<>();


        assignedRolesListView = findViewById(R.id.assigned_roles_list_view);
        assignedRolesAdapter = new AssignedRolesAdapter(this,assignedRolesUI);
        assignedRolesListView.setAdapter(assignedRolesAdapter);

        selectPerson = findViewById(R.id.select_person);

        createPerson = findViewById(R.id.create_new_person);
        createPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = DatabaseHelper.PERSON_TABLE.insertActivityIntent(AddSessionRolesActivity.this);
                i.setFlags(FLAG_INTERVIEWER_EDIT);
                startActivityForResult(i,REQUEST_CODE_ADD_ENTRY);
            }
        });

        personListView = findViewById(R.id.person_list_view);
        people = DatabaseHelper.PERSON_TABLE.getAll();
        personAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_single_choice, people);
        personListView.setAdapter(personAdapter);
        personListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        setListViewHeights();

        personListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                personSelectedIndex = i;
            }
        });


        roleSpinner = this.findViewById(R.id.choose_role_spinner);
        ArrayList<Role> roles = DatabaseHelper.ROLE_TABLE.getOnClientRoles();
        roleAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, roles);
        roleSpinner.setAdapter(roleAdapter);
        roleSpinner.setSelected(false);


        rolesAlreadyAssigned = DatabaseHelper.SESSION_PERSON_TABLE.getSessionPersonRoles(getSession(getIntent()).getId());

        if (!rolesAlreadyAssigned.isEmpty()){
            setActivityState();
        }

        assignButton = findViewById(R.id.assign_role);
        assignButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(validateAssignRole()){
                    Person checkedPerson = (Person) personListView.getItemAtPosition(personSelectedIndex);
                    personListView.setItemChecked(personSelectedIndex,false);
                    personSelectedIndex = -1;
                    selectPerson.setError(null);

                    Role role = (Role) roleSpinner.getSelectedItem();
                    roleSpinner.setSelected(false);

                    SessionPerson sp = new SessionPerson();
                    sp.setPersonId(checkedPerson.getId());
                    sp.setRoleId(role.getId());
                    sp.setSessionId(getSession(getIntent()).getId());


                    if (!assignedRoles.contains(sp)){
                        assignedRoles.add(sp);
                        allRolesAssigned.add(role.getName().toLowerCase());

                        StringBuilder sb = new StringBuilder();
                        sb.append(checkedPerson.getName());
                        sb.append(" : ");
                        sb.append(role.getName());
                        assignedRolesUI.add(sb.toString());

                        uiMapping.put(sb.toString(),sp);
                        assignedRolesAdapter.notifyDataSetChanged();
                    }
                    else {
                        Toast.makeText(AddSessionRolesActivity.this, "You have already assigned this person to that role", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

        continueButton = findViewById(R.id.assign_roles_done_button);

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateContinue()){
                    Log.i(assignedRoles.toString(), "INSERTING INTO DATABASE");

                    for (SessionPerson sp :rolesAlreadyAssigned.keySet()){
                        if(!assignedRoles.contains(sp)){
                            DatabaseHelper.SESSION_PERSON_TABLE.delete(sp.getId());
                            Log.i(sp.getPersonId(),"DELETING PERSON");
                        }
                    }

                    for (SessionPerson sp: assignedRoles){
                        if(!rolesAlreadyAssigned.containsKey(sp)){
                            DatabaseHelper.SESSION_PERSON_TABLE.insert(sp);
                            Log.i(sp.getPersonId(),"ADDING PERSON");

                        }
                        else {
                            DatabaseHelper.SESSION_PERSON_TABLE.update(sp);
                            Log.i(sp.getPersonId(),"UPDATING PERSON");

                        }
                    }

                    Intent i = UserSelectQuestionnaireActivity.newIntent(AddSessionRolesActivity.this);
                    i.putExtra(SELECTED_SESSION, getSession(getIntent()));
                    startActivity(i);
                    finish();
                }
            }
        });

        personSearch = findViewById(R.id.search_people);

        personSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String text) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String text) {
                personAdapter.getFilter().filter(text);
                return false;
            }
        });

    }

    private class AssignedRolesAdapter extends ArrayAdapter<String> {

        public AssignedRolesAdapter(Context context, ArrayList<String> chosenRoles) {
            super(context, 0, chosenRoles);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final String personAndRole = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.entry_list_item_view2, parent, false);
            }

            TextView text = (TextView) convertView.findViewById(R.id.entry_list_text_view);
            text.setText(personAndRole);

            ImageButton deleteButton = convertView.findViewById(R.id.entry_list_delete_button);

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    assignedRolesUI.remove(personAndRole);
                    SessionPerson sp = uiMapping.get(personAndRole);
                    assignedRoles.remove(sp);
                    uiMapping.remove(personAndRole);
                    String [] splitString = personAndRole.split(":");
                    allRolesAssigned.remove(splitString[1].trim().toLowerCase());
                    assignedRolesAdapter.notifyDataSetChanged();
                }
            });


            return convertView;

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_CODE_ADD_ENTRY) {
            people = DatabaseHelper.PERSON_TABLE.getAll();
            personAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_single_choice, people);
            personAdapter.notifyDataSetChanged();
            personListView.setAdapter(personAdapter);
        }

    }



    public static Intent newIntent(Context packageContext) {
        Intent i = new Intent(packageContext, AddSessionRolesActivity.class);
        return i;
    }


    private boolean validateAssignRole(){
        boolean valid = true;

        if(personSelectedIndex == -1){
            selectPerson.setError("You must select a person");
            valid = false;
        }

        if (!valid) {
            Toast.makeText(this, "Please Select a Person and a Role", Toast.LENGTH_SHORT).show();
        }

        return valid;
    }


    private boolean validateContinue(){
        boolean valid = true;

        if (allRolesAssigned.size() < 2){
            valid = false;
        }

        if (!allRolesAssigned.contains("interviewer") || !allRolesAssigned.contains("consultant")){
            valid = false;
        }

        if (!valid) {
            Toast.makeText(this, "Please Assign an Interviewer and a Consultant", Toast.LENGTH_SHORT).show();
        }

        return valid;
    }

    private void setListViewHeights(){

        ViewGroup.LayoutParams assignedRolesLayoutParams = assignedRolesListView.getLayoutParams();
        assignedRolesLayoutParams.height = 180;
        assignedRolesListView.setLayoutParams(assignedRolesLayoutParams);
        assignedRolesListView.requestLayout();

        ViewGroup.LayoutParams personLayoutParams = personListView.getLayoutParams();
        personLayoutParams.height = 400;
        personListView.setLayoutParams(personLayoutParams);
        personListView.requestLayout();
    }

    private void setActivityState(){
        for (SessionPerson sp: rolesAlreadyAssigned.keySet()){
            assignedRoles.add(sp);

            for (int i = 0; i<rolesAlreadyAssigned.get(sp).size(); i++){
                allRolesAssigned.add(rolesAlreadyAssigned.get(sp).get(i).getName().toLowerCase());
                Person person = DatabaseHelper.PERSON_TABLE.findById(sp.getPersonId());
                StringBuilder sb = new StringBuilder();
                sb.append(person.getName());
                sb.append(" : ");
                sb.append(rolesAlreadyAssigned.get(sp).get(i).getName());
                assignedRolesUI.add(sb.toString());
                uiMapping.put(sb.toString(),sp);
            }

        }
        assignedRolesAdapter.notifyDataSetChanged();
    }

}
