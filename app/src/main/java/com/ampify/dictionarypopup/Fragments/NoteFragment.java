package com.ampify.dictionarypopup.Fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.ampify.dictionarypopup.Constants;
import com.ampify.dictionarypopup.R;
import com.ampify.dictionarypopup.ViewModel;

public class NoteFragment extends DialogFragment implements DialogInterface.OnDismissListener {

    public interface NoteListener {
        void noteListener(String note);
    }

    private ViewModel mViewModel;
    private String mWord;
    private String mNote;
    private EditText mEditNote;
    private static NoteListener mListener;

    public static NoteFragment newInstance(String word, String note, NoteListener listener) {
        NoteFragment fragment = new NoteFragment();
        Bundle args= new Bundle();
        args.putString(Constants.WORD_ARG, word);
        args.putString(Constants.NOTE_ARG, note);
        fragment.setArguments(args);
        mListener = listener;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mWord = getArguments().getString(Constants.WORD_ARG);
        mNote = getArguments().getString(Constants.NOTE_ARG);
        mViewModel = new ViewModelProvider(this).get(ViewModel.class);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_note, null);

        dialog.setView(view)
                .setPositiveButton(R.string.set_note_positive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        recordNote();
                    }
                })
                .setNegativeButton(R.string.set_note_negative, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteNote();
                    }
                })
                .setNeutralButton(R.string.set_note_neutral, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cancelNote();
                    }
                });

        mEditNote = view.findViewById(R.id.note_description);
        mEditNote.setText(mNote);

        return dialog.create();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        recordNote();
    }

    public void recordNote() {
        mNote = mEditNote.getText().toString();
        mViewModel.note(mWord, mNote);
        if (!mNote.isEmpty()) {
            mViewModel.favourite(mWord, true);
        }
        mListener.noteListener(mNote);
    }

    public void deleteNote() {
        mEditNote.setText("");
    }

    public void cancelNote() {
        mEditNote.setText(mNote);
        mListener.noteListener(mNote);
    }

}
