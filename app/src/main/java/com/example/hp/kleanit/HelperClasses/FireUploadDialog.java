package com.example.hp.kleanit.HelperClasses;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.hp.kleanit.R;

import org.apache.http.entity.mime.content.StringBody;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by hp on 30-03-2016.
 */
public class FireUploadDialog extends DialogFragment {

    public Context sContext;
    public String mName;
    public String mdate;
    public String mtime;

    EditText Description;
    EditText Date;
    EditText Time;
    EditText VoluteerReq;

    Calendar myCalendar = Calendar.getInstance();

    public interface NoticeDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);

    }

    // Use this instance of the interface to deliver action events
    NoticeDialogListener mListener;


    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (NoticeDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),R.style.AppCompatAlertDialogStyle);
        LayoutInflater inflater = getActivity().getLayoutInflater();


        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        final View view = inflater.inflate(R.layout.layout, null);
        TextView Loc = (TextView) view.findViewById(R.id.Location);
        SharedPreferences prefs = FireUploadDialog.this.getActivity().getSharedPreferences("Complaint", Context.MODE_PRIVATE);
        String location = prefs.getString("fullAddress", "Location Not Found.");

        Loc.setText(location);
        builder.setView(view)
            .setTitle("Complaint Details")
                .setPositiveButton("Upload", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // FIRE ZE MISSILES!
                       // Toast.makeText(getActivity().getBaseContext(), "esadsf", Toast.LENGTH_LONG).show();
                        TextView Name = (TextView) view.findViewById(R.id.Location);
                        Description = (EditText) view.findViewById(R.id.Details);
                        Date = (EditText) view.findViewById(R.id.Date);
                        Time = (EditText) view.findViewById(R.id.Time);
                        VoluteerReq = (EditText) view.findViewById(R.id.NoOfVolunteers);
                        Log.d("VOLUNTEER",VoluteerReq.toString());



                        Log.i("tagg", String.valueOf(Name.getText()));

                        // Putting the Content in SharedPrefs
                        final SharedPreferences prefs = FireUploadDialog.this.getActivity().getSharedPreferences("Complaint", Context.MODE_PRIVATE);
                        Log.i("TAG", "Saving shared preferes, id :");
                        SharedPreferences.Editor editor = prefs.edit();
                        //editor.putBoolean("is_logged_in", true);
                        editor.putString("Description", Description.getText().toString());
                        editor.putString("Date", mdate);
                        editor.putString("Time", mtime);
                        editor.putString("Voluteer_required", VoluteerReq.getText().toString());
                        Log.d("SharedPRef", "Mein Saved");
                        editor.commit();

                        mListener.onDialogPositiveClick(FireUploadDialog.this);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                       // Toast.makeText(FireUploadDialog.this.sContext, "Complaint Cancelled", Toast.LENGTH_SHORT).show();
                        mListener.onDialogNegativeClick(FireUploadDialog.this);
                    }
                });


        // Date Picker


        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };


        Date = (EditText) view.findViewById(R.id.Date);
        Date.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(getDialog().getContext(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        // TimePicker
        Time = (EditText) view.findViewById(R.id.Time);
        Time.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(getDialog().getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        Time.setText(selectedHour + ":" + selectedMinute);
                        mtime = selectedHour + ":" + selectedMinute;
                    }
                }, hour, minute, true);//Yes 24 hour time

                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });


        // Create the AlertDialog object and return it
        return builder.create();
    }


    private void updateLabel(){

        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        mdate = sdf.format(myCalendar.getTime());
        Date.setText(sdf.format(myCalendar.getTime()));
    }
}