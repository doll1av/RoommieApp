package com.doll1av.finalproject.myroomiehelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 *Should be named alltasks or claimtasks, something along those lines
 * shows all chores/tasks for the given roomcode
 *
 *
 */
public class MyTasks extends Activity {

    ListView tasks;
    List<AddTask> taskList;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    FirebaseDatabase database = FirebaseDatabase.getInstance()  ;
    DatabaseReference databaseTasks;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_tasks);

        taskList = new ArrayList<>();

        tasks = (ListView) findViewById(R.id.listViewTasks);
        String code = PreferenceManager.getDefaultSharedPreferences(MyTasks.this).getString("roomcode", "");
        databaseTasks = database.getReference();
        Button button = (Button) findViewById(R.id.button5);
        Button button2 = (Button) findViewById(R.id.button4);
        Button button3 = (Button) findViewById(R.id.button6);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(MyTasks.this,Home.class);
                startActivity(i);
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent j = new Intent(MyTasks.this, CreateTask.class);
                startActivity(j);
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent k = new Intent(MyTasks.this, OtherUsersClaimed.class);
                startActivity(k);
            }
        });


        tasks.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                AddTask task = taskList.get(i);
                showUpdateDialog(task.getTask(), task.getDate(), task.getId());
                return false;
            }


        });
    }

    private void showUpdateDialog(final String taskName, final String date, final String id) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogview = inflater.inflate(R.layout.claimtask, null);
        dialogBuilder.setView(dialogview);
        final TextView textViewTask  = (TextView) dialogview.findViewById(R.id.taskName);
        final Button deleteButton = (Button) dialogview.findViewById(R.id.claim);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String name = textViewTask.getText().toString();
                claimTask(taskName, date, id);


            }
        });

        textViewTask.setText(taskName);
        dialogBuilder.setTitle("claim this task");
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    private boolean claimTask(String task, String date, String id) {
        //shared prefrence for room code
        SharedPreferences prefs = getSharedPreferences("roomateApp", MODE_PRIVATE);
        String roomcodePrefrence = prefs.getString("roomcode", "Error");
        String usernamePrefrence = prefs.getString("username", "Error");
        DatabaseReference databaseReference =  database.getReference();
        AddTask myTask = new AddTask(task, date, id, usernamePrefrence);
        //remove from general pool add it to user tasklist

        databaseReference.child(roomcodePrefrence).child("Claimed").child(usernamePrefrence).child(myTask.getId()).setValue(myTask);
        databaseReference.child(roomcodePrefrence).child("claimedGeneral").child(myTask.getId()).setValue(myTask);

        DatabaseReference removeTask = FirebaseDatabase.getInstance().getReference().child(roomcodePrefrence).child("Unclaimed").child(myTask.getId());
        removeTask.removeValue();


        return true;
    }


    @Override
    protected void onStart() {
        super.onStart();

        databaseTasks.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                SharedPreferences prefs = getSharedPreferences("roomateApp", MODE_PRIVATE);
                String code = prefs.getString("roomcode", "Error");
                taskList.clear();

                for(DataSnapshot tasksnapshot: dataSnapshot.child(code).child("Unclaimed").getChildren()){
                    AddTask task = tasksnapshot.getValue(AddTask.class);

                    taskList.add(task);
                }


                TaskList adapter = new TaskList(MyTasks.this, taskList);
                tasks.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
