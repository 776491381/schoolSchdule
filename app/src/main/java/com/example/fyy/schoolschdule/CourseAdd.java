package com.example.fyy.schoolschdule;

import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;


public class CourseAdd implements DialogInterface.OnClickListener{

    public interface CourseAddedCallback {
        void run(Course course);
    }

    private AlertDialog.Builder builder;
    private View view;
    private CourseAddedCallback callback = null;
    private Course course;

    public CourseAdd(Context context) {
        this(context, null);
    }

    public CourseAdd(Context context, Course course) {
        this.course = course;
        view = LayoutInflater.from(context).inflate(R.layout.course_add, null);
        builder = new AlertDialog.Builder(context);
        builder.setView(view);
        builder.setTitle(R.string.course_add_title);
        builder.setPositiveButton(R.string.course_add_confirm, this);
        builder.setNegativeButton(R.string.course_add_cancel, null);
    }

    public void show() {
        builder.create().show();
    }

    public void show(CourseAddedCallback callback) {
        show();
        this.callback = callback;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        String name, classroom;
        int start, last, day;
        try {
            name = getString(R.id.course_add_name);
            classroom = getString(R.id.course_add_classroom);
            start = getInt(R.id.course_add_start, 1, 12);
            last = getInt(R.id.course_add_last, 1, 12);

            Spinner spinner = (Spinner)view.findViewById(R.id.course_add_day);
            day = (int)spinner.getSelectedItemId();
        } catch (Exception e) {
            Log.e("CourseAdd", "Input Error" + e);
            return;
        }

        Course c;
        c = new Course(name, classroom, start, last, day);
        callback.run(c);
    }

    private String getString(int resource) throws Exception {
        TextView textview = (TextView)view.findViewById(resource + 1);
        TextInputLayout inputLayout = (TextInputLayout) view.findViewById(resource);
        String text = textview.getText().toString();
        if (text.length() == 0) {
            inputLayout.setErrorEnabled(true);
            inputLayout.setError("Empty!");
            throw new Exception();
        }
        return text;
    }

    private int getInt(int resource, int min, int max) throws Exception {
        TextView textview = (TextView)view.findViewById(resource + 1);
        TextInputLayout inputLayout = (TextInputLayout) view.findViewById(resource);
        int number = Integer.parseInt(textview.getText().toString());
        if (number > max || number < min) {
            inputLayout.setErrorEnabled(true);
            inputLayout.setError("Wrong Range");
            throw new Exception();
        }
        return number;
    }
}
