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

import static com.doll1av.finalproject.myroomiehelper.R.id.textViewDate;
import static com.doll1av.finalproject.myroomiehelper.R.id.textViewTask;

public class ListLayoutAllClaimed extends ArrayAdapter<AddTask> {
    private Activity context;
    private List<AddTask> taskList;




    public ListLayoutAllClaimed(Activity context, List<AddTask> TaskList)
    {

        super(context,R.layout.activity_list_layout_all_claimed);
        this.context = context;
        this.taskList = TaskList;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listviewitem = inflater.inflate(R.layout.activity_list_layout_all_claimed, null, true);
        TextView task = (TextView) listviewitem.findViewById(textViewTask);
        TextView date = (TextView) listviewitem.findViewById(textViewDate);

        AddTask tasks = taskList.get(position);
        task.setText(tasks.getTask());
        date.setText(tasks.getDate()+ "-" + tasks.getUsername());

        return listviewitem;
    }
}
