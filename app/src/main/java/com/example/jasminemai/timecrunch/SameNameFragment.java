package com.example.jasminemai.timecrunch;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;


import org.w3c.dom.Text;

/**
 * A fragment with a Google +1 button.
 * Activities that contain this fragment must implement the
 * { PasswordDialog.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the { PasswordDialog#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SameNameFragment extends DialogFragment {
    private String pass;
    private String firstPass;

    public String getPass() {
        return pass;
    }

    public String getFirstPass() {
        return firstPass;
    }

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface DialogListener {
        void onDialogPositiveClick(DialogFragment dialog, String password);

        void onDialogNegativeClick(DialogFragment dialog);
    }

    DialogListener mListener;

    //Create Dialog
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        //   "final" is important for the closure to be created in the inner class
        final View dialog_view = inflater.inflate(R.layout.fragment_same_name, null);

        builder.setView(dialog_view)
                // Add action buttons
                .setPositiveButton(R.string.replace, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Log.d("DIALOG", "positive clicked");

                        // collect strings
                        //TextView tu = dialog_view.findViewById(R.id.confirmPassword);

                        mListener.onDialogPositiveClick(SameNameFragment.this, getPass());
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // This will dismiss the dialog without the callback:
                        // AuthDialog.this.getDialog().cancel();
                        Log.d("DIALOG", "negative clicked");
                        mListener.onDialogNegativeClick(SameNameFragment.this);
                    }
                });

        final AlertDialog nameChange = builder.create();
        nameChange.show();

        return nameChange;
    }



    //Attaches fragment to activity. Ensures that activity implements this interface
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Activity activity;

        if (context instanceof Activity){
            activity= (Activity) context;
            try {
                // Instantiate the NoticeDialogListener so we can send events to the host
                mListener = (DialogListener) activity;
            } catch (ClassCastException e) {
                // The activity doesn't implement the interface, throw exception
                throw new ClassCastException(activity.toString()
                        + " must implement NoticeDialogListener");
            }
            Log.d("DIALOG", "attached");
        }

    }

}
