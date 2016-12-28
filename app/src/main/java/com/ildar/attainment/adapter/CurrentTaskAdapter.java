package com.ildar.attainment.adapter;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.res.Resources;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ildar.attainment.R;
import com.ildar.attainment.Utils;
import com.ildar.attainment.fragment.CurrentTaskFragment;
import com.ildar.attainment.model.Item;
import com.ildar.attainment.model.ModelSeparator;
import com.ildar.attainment.model.ModelTask;

import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by ildar on 19.07.2016.
 */
public class CurrentTaskAdapter extends TaskAdapter{

    private static final int TYPE_TASK = 0;
    public static final int TYPE_SEPARATOR = 1;

    public CurrentTaskAdapter(CurrentTaskFragment taskFragment) {
        super(taskFragment);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        switch (viewType){
            case TYPE_TASK:
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.model_task, parent, false);
                TextView title = (TextView) v.findViewById(R.id.tvTaskTitle);
                TextView date = (TextView) v.findViewById(R.id.tvTaskData);
                CircleImageView priority = (CircleImageView) v.findViewById(R.id.cvTaskPriority);
                return new TaskViewHolder(v, title, date, priority);
            case TYPE_SEPARATOR:
                View separator = LayoutInflater.from(parent.getContext()).inflate(R.layout.model_separator, parent, false);
                TextView type = (TextView) separator.findViewById(R.id.tvSeparatorName);
                return new SeparatorViewHolder(separator, type);
            default:
                return null;
        }
    }

    //заполнение виджетов View данными из элемента списка с номером position
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Item item = items.get(position);
        final Resources resources = holder.itemView.getResources();

        if (item.isTask()){
            holder.itemView.setEnabled(true);
            final ModelTask task = (ModelTask) item;
            final TaskViewHolder taskViewHolder = (TaskViewHolder) holder;

            final View itemView = taskViewHolder.itemView;

            taskViewHolder.title.setText(task.getTitle());
            if (task.getDate() != 0){
                taskViewHolder.date.setText(Utils.getFullDate(task.getDate()));
            }else{
                taskViewHolder.date.setText(null);
            }

            itemView.setVisibility(View.VISIBLE);
            taskViewHolder.priority.setEnabled(true);

            if (task.getDate() != 0 && task.getDate() < Calendar.getInstance().getTimeInMillis()){
                itemView.setBackgroundColor(resources.getColor(R.color.gray_200));
            }else{
                itemView.setBackgroundColor(resources.getColor(R.color.gray_50));
            }

            //itemView.setBackgroundColor(resources.getColor(R.color.gray_50));

            taskViewHolder.title.setTextColor(resources.getColor(R.color.primary_text_default_material_light));
            taskViewHolder.date.setTextColor(resources.getColor(R.color.secondary_text_default_material_light));
            taskViewHolder.priority.setColorFilter(resources.getColor(task.getPriorityColor()));
            taskViewHolder.priority.setImageResource(R.drawable.ic_checkbox_blank_circle_white_48dp);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getTaskFragment().showTaskEditDialog(task);
                }
            });

            itemView.setOnLongClickListener((new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getTaskFragment().removeFragment(taskViewHolder.getLayoutPosition());
                        }
                    },1000);

                    return true;
                }
            }));

            taskViewHolder.priority.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    task.setStatus(ModelTask.STATUS_DONE);
                    taskViewHolder.priority.setEnabled(false);
                    getTaskFragment().activity.dbHelper.update().status(task.getTimeStamp(), ModelTask.STATUS_DONE);

                    //itemView.setBackgroundColor(resources.getColor(R.color.gray_200));

                    taskViewHolder.title.setTextColor(resources.getColor(R.color.primary_text_disabled_material_light));
                    taskViewHolder.date.setTextColor(resources.getColor(R.color.secondary_text_disabled_material_light));
                    taskViewHolder.priority.setColorFilter(resources.getColor(task.getPriorityColor()));

                    ObjectAnimator flipIn = ObjectAnimator.ofFloat(taskViewHolder.priority, "rotationY",-180f, 0f);

                    flipIn.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animator) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animator) {
                            if (task.getStatus() == ModelTask.STATUS_DONE){
                                taskViewHolder.priority.setImageResource(R.drawable.ic_check_circle_white_48dp);

                                ObjectAnimator translationX = ObjectAnimator.ofFloat(itemView, "translationX", 0f, itemView.getWidth());

                                ObjectAnimator translationXBack = ObjectAnimator.ofFloat(itemView, "translationX", itemView.getWidth(), 0f);

                                translationX.addListener(new Animator.AnimatorListener() {
                                    @Override
                                    public void onAnimationStart(Animator animator) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animator) {
                                        itemView.setVisibility(View.GONE);
                                        getTaskFragment().moveTask(task);
                                        removeItem(taskViewHolder.getLayoutPosition());
                                    }

                                    @Override
                                    public void onAnimationCancel(Animator animator) {

                                    }

                                    @Override
                                    public void onAnimationRepeat(Animator animator) {

                                    }
                                });

                                AnimatorSet translationSet = new AnimatorSet();
                                translationSet.play(translationX).before(translationXBack);
                                translationSet.start();
                            }
                        }

                        @Override
                        public void onAnimationCancel(Animator animator) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animator) {

                        }
                    });

                    flipIn.start();
                }
            });
        } else {
            //separator
            ModelSeparator separator = (ModelSeparator) item;
            SeparatorViewHolder separatorViewHolder = (SeparatorViewHolder) holder;

            separatorViewHolder.type.setText(resources.getString(separator.getType()));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (getItem(position).isTask()){
            return TYPE_TASK;
        }else {
            return TYPE_SEPARATOR;
        }
    }
}
