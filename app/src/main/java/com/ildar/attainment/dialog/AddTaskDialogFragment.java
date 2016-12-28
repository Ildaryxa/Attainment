package com.ildar.attainment.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.ildar.attainment.R;
import com.ildar.attainment.Utils;
import com.ildar.attainment.alarm.AlarmHelper;
import com.ildar.attainment.model.ModelTask;

import java.util.Calendar;

/**
 * Created by ildar on 18.07.2016.
 */
public class AddTaskDialogFragment extends DialogFragment{

    private AddingTaskListener addingTaskListener;

    //используем паттерн наблюдатель
    public interface AddingTaskListener{
        void onTaskAdded(ModelTask newTask);
        void onTaskAddingCancel();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            addingTaskListener = (AddingTaskListener) context;
        } catch (ClassCastException ex){
            throw new ClassCastException(context.toString() + "must implement AddingTaskListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.dialog_title);
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View container = inflater.inflate(R.layout.dialog_task, null);
        final TextInputLayout tilTitle = (TextInputLayout) container.findViewById(R.id.tilDialogTaskTitle);
        final EditText etTitle = tilTitle.getEditText();

        TextInputLayout tilDate = (TextInputLayout) container.findViewById(R.id.tilDialogTaskDate);
        final EditText etDate = tilDate.getEditText();

        TextInputLayout tilTime = (TextInputLayout) container.findViewById(R.id.tilDialogTaskTime);
        final EditText etTime = tilTime.getEditText();

        Spinner spPriority = (Spinner) container.findViewById(R.id.spDialogTaskPriority);

        tilTitle.setHint(getResources().getString(R.string.task_title));
        tilDate.setHint(getResources().getString(R.string.task_date));
        tilTime.setHint(getResources().getString(R.string.task_time));

        builder.setView(container);

        final ModelTask task = new ModelTask();

        ArrayAdapter<String> priorityAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, ModelTask.PRIORITY_LEVELS);
        spPriority.setAdapter(priorityAdapter);
        spPriority.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                task.setPriority(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        final Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY)+1);

        if (etDate != null) {
            etDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (etDate.length() == 0){
                        etDate.setText(" ");
                    }

                    DialogFragment datePickerFragment = new DatePickedFragment(){
                        @Override
                        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                            calendar.set(Calendar.YEAR, i);
                            calendar.set(Calendar.MONTH, i1);
                            calendar.set(Calendar.DAY_OF_MONTH, i2);
                            etDate.setText(Utils.getDate(calendar.getTimeInMillis()));
                        }

                        @Override
                        public void onCancel(DialogInterface dialog) {
                            etDate.setText(null);
                        }
                    };

                    datePickerFragment.show(getFragmentManager(), "DatePickerFragment");
                }
            });

            if (etTime != null) {
                etTime.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (etTime.length() == 0){
                            etTime.setText(" ");
                        }

                        DialogFragment timePickerFragment = new TimePickerFragment(){
                            @Override
                            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                                calendar.set(Calendar.HOUR_OF_DAY, i);
                                calendar.set(Calendar.MINUTE, i1);
                                calendar.set(Calendar.SECOND, 0);
                                etTime.setText(Utils.getTime(calendar.getTimeInMillis()));
                            }

                            @Override
                            public void onCancel(DialogInterface dialog) {
                                etTime.setText(null);
                            }
                        };

                        timePickerFragment.show(getFragmentManager(), "TimePickerFragment");
                    }
                });
            }
        }

        builder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                task.setTitle(etTitle.getText().toString());
                task.setStatus(ModelTask.STATUS_CURRENT);
                if(etDate.length()!=0 | etTime.length()!=0){
                    task.setDate(calendar.getTimeInMillis());

                    AlarmHelper alarmHelper = AlarmHelper.getInstance();
                    alarmHelper.setAlarm(task);
                }
                task.setStatus(ModelTask.STATUS_CURRENT);
                addingTaskListener.onTaskAdded(task);
                dialogInterface.dismiss();
            }
        });
        builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                addingTaskListener.onTaskAddingCancel();
                dialogInterface.dismiss();
            }
        });


        AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                final Button positiveButton = ((AlertDialog) dialogInterface).getButton(AlertDialog.BUTTON_POSITIVE);
                if (etTitle.length() == 0){
                    positiveButton.setEnabled(false);
                    tilTitle.setError(getResources().getString(R.string.dialog_error_empty_title));
                }

                etTitle.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        if (charSequence.length() == 0){
                            positiveButton.setEnabled(false);
                            tilTitle.setError(getResources().getString(R.string.dialog_error_empty_title));
                        }else{
                            positiveButton.setEnabled(true);
                            tilTitle.setErrorEnabled(false);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
            }
        });
        return alertDialog;
    }
}
