package com.example.fyy.schoolschdule;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.BaseViewHolder> {
    private static final int DIVIDE_TYPE = 0, DATA_TYPE = 1;
    public static int dayClasses;
    private CourseList courseList;
    private static List<Pair<String,String >> times;
    public static Course _course;

    public static abstract class BaseViewHolder extends RecyclerView.ViewHolder {

        protected Context context;

        public BaseViewHolder(View itemView, Context context) {
            super(itemView);
            this.context = context;
            itemView.setTag(this);
        }

        abstract public void setData(Course course);
    }

    private static class CourseViewHolder extends BaseViewHolder {
        public TextView name, info;
        private Course course;
        private RecyclerView.Adapter adapter;
        private TextView startTime, endTime, processBar;
        private LinearLayout linearLayout;
        public CourseViewHolder(View itemView, final Context context, final CourseAdapter adapter) {
            super(itemView, context);
            name = (TextView) itemView.findViewById(R.id.course_name);
            info = (TextView) itemView.findViewById(R.id.course_info);
            startTime = (TextView) itemView.findViewById(R.id.startTime);
            endTime = (TextView) itemView.findViewById(R.id.endTime);
            processBar = (TextView) itemView.findViewById(R.id.processBar);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.course_item_body);
            this.adapter = adapter;

            itemView.findViewById(R.id.course_item_body).setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                    dialog.setTitle(R.string.course_long_del);
                    dialog.setMessage(R.string.course_del_confirm);
                    dialog.setPositiveButton(R.string.list_delete, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            int position = CourseList.getInstance().remove(course);

                            adapter.notifyItemRemoved(position);

                        }
                    });

                    dialog.setNegativeButton(R.string.course_add_cancel,null);
                    dialog.create().show();

                    return true;
                }
            });
            itemView.findViewById(R.id.course_item_body).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent transitionIntent = new Intent(context, CourseDetail.class);
                    transitionIntent.putExtra("courseIndex", CourseList.getInstance().indexOf(course));

                    LinearLayout placeNameHolder = (LinearLayout) v.findViewById(R.id.course_item_body);
                    TextView name = (TextView) v.findViewById(R.id.course_name);
                    TextView info = (TextView) v.findViewById(R.id.course_info);

                    Pair<View, String> holderPair = Pair.create((View) placeNameHolder, "course_detail");
                    Pair<View, String> namePair = Pair.create((View) name, "course_name");
                    Pair<View, String> infoPair = Pair.create((View) info, "course_info");

                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((com.example.fyy.schoolschdule.MainActivity)context, namePair);
//                    ActivityOptionsCompat options = ActivityOptionsCompat.makeScaleUpAnimation(v, v.getLeft(), v.getTop(), v.getWidth(), v.getHeight());
                    ActivityCompat.startActivityForResult((com.example.fyy.schoolschdule.MainActivity)context, transitionIntent, 100, options.toBundle());
                }
            });
        }

        @Override
        public void setData(Course course) {
            this.course = course;
            name.setText(course.getName());
            info.setText(course.getClassroom() + " （" + course.getStart() + "-" + course.getEnd() + "）");
            if(course.getStart()>5){

                startTime.setText(MainActivity.resources.getString(R.string.course_time_pm)+" "+times.get(course.getStart()-1).first);
                endTime.setText(MainActivity.resources.getString(R.string.course_time_pm)+" "+times.get(course.getEnd()-1).second);
            }else{
                startTime.setText(MainActivity.resources.getString(R.string.course_time_am)+" "+times.get(course.getStart()-1).first);
                endTime.setText(MainActivity.resources.getString(R.string.course_time_am)+" "+times.get(course.getEnd()-1).second);
            }

            if(course.getWeek()) {
                _course = course;
                dayClasses = CourseList.getInstance().indexOf(course);
                if(timeProcess(course)){
                    processBar.setBackgroundColor(Color.parseColor("#3F51B5"));
                    linearLayout.setBackgroundColor(Color.parseColor("#00BFFF"));
                    startTime.setTextColor(Color.parseColor("#FFFFFF"));
                    name.setTextColor(Color.parseColor("#FFFFFF"));
                    endTime.setTextColor(Color.parseColor("#FFFFFF"));
                    info.setTextColor(Color.parseColor("#FFFFFF"));
                }else {
                    processBar.setBackgroundColor(Color.parseColor("#3F51B5"));
                    linearLayout.setBackgroundColor(Color.parseColor("#F3F3F3"));
                    startTime.setTextColor(Color.parseColor("#A9A9A9"));
                    name.setTextColor(Color.parseColor("#3F51B5"));
                    endTime.setTextColor(Color.parseColor("#A9A9A9"));
                    info.setTextColor(Color.parseColor("#3F51B5"));
                }

            }
            else {
                processBar.setBackgroundColor(Color.parseColor("#D3D3D3"));
                linearLayout.setBackgroundColor(Color.parseColor("#F3F3F3"));
                startTime.setTextColor(Color.parseColor("#A9A9A9"));
                name.setTextColor(Color.parseColor("#A9A9A9"));
                endTime.setTextColor(Color.parseColor("#A9A9A9"));
                info.setTextColor(Color.parseColor("#A9A9A9"));

            }
        }


        private boolean timeProcess(Course course){

            Calendar now = Calendar.getInstance();
            Calendar setEnd = Calendar.getInstance();
            Calendar setStart = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");
            try {
                String fdate = now.get(Calendar.YEAR)+"-"+(now.get(Calendar.MONTH)+1)+"-"+now.get(Calendar.DAY_OF_MONTH)+" ";
                Date edate = dateFormat.parse(fdate+times.get(course.getEnd()-1).second);
                Date sdate = dateFormat.parse(fdate+times.get(course.getStart()-1).first);
                setEnd.setTime(edate);
                setStart.setTime(sdate);
                if(course.getStart()>5){
                    setEnd.add(Calendar.HOUR_OF_DAY,12);
                    setStart.add(Calendar.HOUR_OF_DAY,12);
                }
                if((setStart.getTimeInMillis()-300000)<now.getTimeInMillis()&&setEnd.getTimeInMillis()>now.getTimeInMillis()){
                    System.out.println(dateFormat.format(edate)+"---------------");
                    return true;
                }


            } catch (ParseException e) {
                e.printStackTrace();
            }



        return false;

        }


        static{

            times = new ArrayList<>();
            times.add(new Pair<>("8:05","8:50"));
            times.add(new Pair<>("8:55","9:40"));
            times.add(new Pair<>("10:00","10:45"));
            times.add(new Pair<>("10:50","11:35"));
            times.add(new Pair<>("11:40","12:25"));

            times.add(new Pair<>("1:35","2:20"));
            times.add(new Pair<>("2:25","3:10"));
            times.add(new Pair<>("3:15","4:00"));
            times.add(new Pair<>("4:05","4:50"));

            times.add(new Pair<>("6:30","7:15"));
            times.add(new Pair<>("7:20","8:05"));
            times.add(new Pair<>("8:10","8:55"));
        }



    }



    private static class DivideViewHolder extends BaseViewHolder {

        public TextView header;

        public DivideViewHolder(View itemView, Context context) {
            super(itemView, context);
            header = (TextView) itemView.findViewById(R.id.list_subheader);
            header.setTextSize(20);
        }

        @Override
        public void setData(Course course) {
            String[] res = context.getResources().getStringArray(R.array.week_name);
            header.setText(res[course.getDay()]);
        }
    }

    public CourseAdapter(CourseList courseList) {
        this.courseList = courseList;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        BaseViewHolder vh = null;
        View v;

        switch (viewType) {
            case DATA_TYPE:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.course_item, parent, false);
                vh = new CourseViewHolder(v, parent.getContext(), this);
                break;
            case DIVIDE_TYPE:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_subheader, parent, false);
                vh = new DivideViewHolder(v, parent.getContext());
        }

        return vh;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        holder.setData(courseList.get(position));
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return courseList.isDivide(position) ? DIVIDE_TYPE : DATA_TYPE;
    }
}
