package com.ildar.attainment.fragment;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.ildar.attainment.MainActivity;
import com.ildar.attainment.R;
import com.ildar.attainment.adapter.TaskAdapter;
import com.ildar.attainment.alarm.AlarmHelper;
import com.ildar.attainment.dialog.EditTaskDialogFragment;
import com.ildar.attainment.model.Item;
import com.ildar.attainment.model.ModelTask;

/**
 * Created by ildar on 20.07.2016.
 */
public abstract class TaskFragment extends Fragment {
    protected RecyclerView recyclerView;
    protected RecyclerView.LayoutManager layoutManager;

    protected TaskAdapter adapter;

    public MainActivity activity;

    public AlarmHelper alarmHelper;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getActivity() != null){
            activity =  (MainActivity) getActivity();
        }

        alarmHelper = AlarmHelper.getInstance();

        addTaskFromDB();
    }

    public void updateTask(ModelTask task){
        adapter.updateTask(task);
    }

    public abstract void addTask(ModelTask newTask, boolean saveToDB);

    public void removeFragment(final int location){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        dialogBuilder.setMessage(R.string.dialog_removing_message);

        Item item = adapter.getItem(location);

        if (item.isTask()){
            ModelTask removingTask = (ModelTask) item;
            final long timeStamp = removingTask.getTimeStamp();
            final boolean[] isRemoved = {false};

            dialogBuilder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    adapter.removeItem(location);
                    isRemoved[0] = true;

                    Snackbar snackbar = Snackbar.make(getActivity().findViewById(R.id.coordinator),
                            R.string.removed, Snackbar.LENGTH_LONG);

                    snackbar.setAction(R.string.dialog_cancel, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            addTask(activity.dbHelper.query().getTask(timeStamp), false);
                            isRemoved[0] = false;
                        }
                    });
                    snackbar.getView().addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                        @Override
                        public void onViewAttachedToWindow(View view) {

                        }

                        @Override
                        public void onViewDetachedFromWindow(View view) {
                            if (isRemoved[0]){
                                alarmHelper.removeAlarm(timeStamp);
                                activity.dbHelper.removedTask(timeStamp);
                            }
                        }
                    });

                    snackbar.show();
                    dialogInterface.dismiss();
                }
            });

            dialogBuilder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
        }

        dialogBuilder.show();
    }

    public void showTaskEditDialog(ModelTask task){
        DialogFragment editingTaskDialog = EditTaskDialogFragment.newinstance(task);
        editingTaskDialog.show(getActivity().getFragmentManager(), "EditTaskDialogFragment");
    }

    public abstract void addTaskFromDB();

    public abstract void moveTask(ModelTask task);

    public abstract void findTask(String title);
}
