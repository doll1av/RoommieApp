package com.doll1av.finalproject.myroomiehelper;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CreateTask extends Activity {

    EditText TaskName;
    Button AddTask;
    EditText Date;

    DatabaseReference databaseTasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);

        //database entry and button
        TaskName = (EditText) findViewById(R.id.TaskName);
        Date = (EditText) findViewById(R.id.Date);
        AddTask = (Button) findViewById(R.id.AddTask);

        AddTask.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            addTask();

            }
        });

        //nav buttons
        Button button = (Button) findViewById(R.id.button9);
        Button button2 = (Button) findViewById(R.id.button8);
        Button button3 = (Button) findViewById(R.id.button7);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(CreateTask.this,Home.class);
                startActivity(i);
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(CreateTask.this,MyTasks.class);
                startActivity(i);
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(CreateTask.this,OtherUsersClaimed.class);
                startActivity(i);
            }
        });

    }

    public void addTask() {
        SharedPreferences prefs = getSharedPreferences("roomateApp", MODE_PRIVATE);
        String usernamePrefrence = prefs.getString("username", "FILL");
        String code = prefs.getString("roomcode", "Error");
        FirebaseDatabase database = FirebaseDatabase.getInstance()  ;
        databaseTasks = database.getReference();
        String id = databaseTasks.push().getKey();
        String taskName = TaskName.getText().toString();
        String date = Date.getText().toString();
        if(!TextUtils.isEmpty(taskName)){

            String newTask = databaseTasks.push().getKey();
            AddTask task = new AddTask(taskName, date, newTask, usernamePrefrence);
            databaseTasks.child(code).child("Unclaimed").child(newTask).setValue(task);
            Toast.makeText(this, "Task Added", Toast.LENGTH_SHORT).show();

        }else
            Toast.makeText(this, "Enter a task name", Toast.LENGTH_SHORT).show();


    }





}
