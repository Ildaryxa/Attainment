package com.ildar.attainment.model;

import com.ildar.attainment.R;

/**
 * Created by ildar on 02.08.2016.
 */
public class ModelSeparator implements Item{
    public static final int TYPE_OVERDUE = R.string.separator_Overdue;
    public static final int TYPE_TODAY = R.string.separator_Today;
    public static final int TYPE_TOMORROW = R.string.separator_Tomorrow;
    public static final int TYPE_FUTURE = R.string.separator_Future;

    private int type;

    public ModelSeparator(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public boolean isTask() {
        return false;
    }
}
