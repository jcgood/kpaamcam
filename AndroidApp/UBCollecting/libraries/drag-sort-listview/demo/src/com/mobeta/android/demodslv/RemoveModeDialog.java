package com.mobeta.android.demodslv;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.core.app.DialogFragment;

import com.mobeta.android.dslv.DragSortController;

/**
 * Simply passes remove mode back to OnOkListener
 */
public class RemoveModeDialog extends DialogFragment {

    private static final String EXTRA_REMOVE_MODE = "remove_mode";

    private int mRemoveMode;

    private RemoveOkListener mListener;

    public static RemoveModeDialog newInstance(int removeMode) {
        Bundle args = new Bundle();
        args.putInt(EXTRA_REMOVE_MODE, removeMode);

        RemoveModeDialog frag = new RemoveModeDialog();
        frag.setArguments(args);
        return frag;
    }

    public RemoveModeDialog() {
        super();
    }

    public interface RemoveOkListener {
        void onRemoveOkClick(int removeMode);
    }

    public void setRemoveOkListener(RemoveOkListener l) {
        mListener = l;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mRemoveMode = getArguments().getInt(EXTRA_REMOVE_MODE, DragSortController.FLING_REMOVE);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Set the dialog title
        builder.setTitle(R.string.select_remove_mode)
                .setSingleChoiceItems(R.array.remove_mode_labels, mRemoveMode,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mRemoveMode = which;
                            }
                        })
                // Set the action buttons
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if (mListener != null) {
                            mListener.onRemoveOkClick(mRemoveMode);
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });

        return builder.create();
    }
}
