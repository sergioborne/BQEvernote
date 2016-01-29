package com.sbc.bqevernote;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

/**
 * @author rwondratschek
 */
public class CreateNoteDialogFragment extends DialogFragment {

    public static final String TAG = "CreateNoteDialogFragment";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_create_note, null);
        final TextInputLayout titleView = (TextInputLayout) view.findViewById(R.id.textInputLayout_title);
        final TextInputLayout contentView = (TextInputLayout) view.findViewById(R.id.textInputLayout_content);

        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        ((NoteListActivity) getActivity()).createNewNote(titleView.getEditText().getText().toString(),
                                contentView.getEditText().getText().toString());

                        break;
                }
            }
        };

        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.create_new_note)
                .setView(view)
                .setPositiveButton(R.string.create, onClickListener)
                .setNegativeButton(android.R.string.cancel, onClickListener)
                .create();
    }

}
