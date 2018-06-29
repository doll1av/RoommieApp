package com.doll1av.finalproject.myroomiehelper;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import static com.doll1av.finalproject.myroomiehelper.R.id.textViewTask;
import static com.doll1av.finalproject.myroomiehelper.R.id.textViewDate;

/**
 * adapter for task lists to show data
 * create AddTasks out of the data
 * set the texts in layout file with corresponding data then return
 */
public class TaskList extends ArrayAdapter<AddTask> {

    private Activity context;
    private List<AddTask> taskList;

    public TaskList(Activity context, List<AddTask> taskList)
    {
        super(context,R.layout.list_layout, taskList);
        this.context = context;
        this.taskList = taskList;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listviewitem = inflater.inflate(R.layout.list_layout, null, true);
        TextView task = (TextView) listviewitem.findViewById(textViewTask);
        TextView date = (TextView) listviewitem.findViewById(textViewDate);

        AddTask tasks = taskList.get(position);
        task.setText(tasks.getTask());
        date.setText(tasks.getDate());

        return listviewitem;
    }
}
