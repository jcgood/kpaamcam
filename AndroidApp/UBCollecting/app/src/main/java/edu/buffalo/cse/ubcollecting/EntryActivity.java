package edu.buffalo.cse.ubcollecting;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import java.io.Serializable;

import edu.buffalo.cse.ubcollecting.data.models.Model;
import edu.buffalo.cse.ubcollecting.data.tables.Table;

import static edu.buffalo.cse.ubcollecting.data.tables.Table.EXTRA_MODEL;


/**
 * Activity that displays {@link Model} entries in the database and allows for insertion and updating
 * database entries
 *
 * @param <E> The {@link Model} this Activity corresponds to
 */
public abstract class EntryActivity<E extends Model> extends AppCompatActivity {

    public final static int REQUEST_CODE_EDIT_ENTRY = 0;
    protected E entry;
    private Button updateButton;
    private Button submitButton;

    /**
     * Function that updates the view's fields/UI based on the entry from the SQlite Table.
     * Used for updating entries in the database.
     * @param entry The entry from the database by which to update the view's fields
     */
    abstract void setUI(E entry);

    /**
     * Function that sets the entry/model based on user submission in a view so that database
     * can be populated appropriately
     */
    abstract void setEntryByUI();

    /**
     * Helper function that validates user submission
     * @return {@link Boolean}
     */
    abstract boolean isValidEntry();

    /**
     * Sets a {@link Model} as result to return to parent activity
     *
     * @param entry {@link Model} to be set as result
     */
    public void setEntryResult(E entry) {
        Intent data = new Intent();
        data.putExtra(EXTRA_MODEL, entry);
        setResult(RESULT_OK, data);
    }

    /**
     * Helper function to extract a {@link Model} extra from and {@link Intent}
     * @param data {@link Intent} holding the extra
     * @return {@link Model} extra from {@link Intent}
     */
    public E getEntry(Intent data) {
        Serializable serializableObject = data.getSerializableExtra(EXTRA_MODEL);

        return (E) serializableObject;
    }

    /**
     * {@link View.OnClickListener} for button to update a table entry
     */
    class UpdateButtonOnClickListener implements View.OnClickListener {

        Table<E> table;

        public UpdateButtonOnClickListener(Table<E> table) {
            this.table = table;
        }

        @Override
        public void onClick(View view) {
            setEntryByUI();
            if (isValidEntry()) {
                table.update(entry);
                setEntryResult(entry);
                finish();
            }
        }
    }

    /**
     * {@link View.OnClickListener} for button to insert a table entry
     */
    class SubmitButtonOnClickListener implements View.OnClickListener {

        Table<E> table;

        public SubmitButtonOnClickListener(Table<E> table) {
            this.table = table;
        }

        @Override
        public void onClick(View view) {
            setEntryByUI();
            if (isValidEntry()) {
                table.insert(entry);
                setEntryResult(entry);
                finish();
            }
        }
    }
}
