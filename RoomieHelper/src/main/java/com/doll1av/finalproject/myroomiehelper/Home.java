package com.doll1av.finalproject.myroomiehelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
 * grab the shared preference for their roomcode and grab all child nodes in the unclaimed directory
 * (11/20/2017)
 *      Currently all claimed tasks are shown not just the users claimed
 * grab snapshots of the data in the directory create AddTask objects with the data
 * load them into task list and display them in the Listview
 * (11/20/2017)
 *      Currently the date category is not showing and is being deleted when its moved from unlcaimed to claimed
 * when user holds a item open an update dialog showing the task name and a button to finish task, remove it if they finish
 *
 */
public class Home extends Activity {

    ListView tasks;
    List<AddTask> taskList;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    FirebaseDatabase database = FirebaseDatabase.getInstance()  ;
    DatabaseReference databaseTasks;
    SharedPreferences sharedPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        //initilize stuff create vars for feilds in layout
        mAuth = FirebaseAuth.getInstance();
        taskList = new ArrayList<>();

        tasks = (ListView) findViewById(R.id.ListViewTasksHome);
        databaseTasks = database.getReference();

        Button button = (Button) findViewById(R.id.button);
        Button button2 = (Button) findViewById(R.id.button2);
        Button button3 = (Button) findViewById(R.id.button3);

        //move them to create task and the general task(MyTasks.java) pool
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent j = new Intent(Home.this, CreateTask.class);
                startActivity(j);
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(Home.this,MyTasks.class);
                startActivity(i);
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent k = new Intent(Home.this,OtherUsersClaimed.class);
                startActivity(k);
            }
        });


    }

    //store the roomcode in 'code' then loop through the claimed directory for all children
    //after all children are added to taskList load data into adapter and display
    @Override
    protected void onStart() {
        super.onStart();

        databaseTasks.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               //String code = PreferenceManager.getDefaultSharedPreferences(Home.this).getString("roomcode", "");
                SharedPreferences prefs = getSharedPreferences("roomateApp", MODE_PRIVATE);
                String roomcodePrefrence = prefs.getString("roomcode", "Error");
                String usernamePrefrence = prefs.getString("username", "Error");
                taskList.clear();

                for(DataSnapshot tasksnapshot: dataSnapshot.child(roomcodePrefrence).child("Claimed").child(usernamePrefrence).getChildren()){
                   AddTask task = tasksnapshot.getValue(AddTask.class);
                    taskList.add(task);

                }

                TaskList adapter = new TaskList(Home.this, taskList);

                tasks.setAdapter(adapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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

    //build the alert box if they click start finishTask()
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
                finishTask(taskName, date, id);


            }
        });

        textViewTask.setText(taskName);
        dialogBuilder.setTitle("complete task");
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    //create a task with data then find it and terminate it
    private boolean finishTask(String task, String date, String id) {
        SharedPreferences prefs = getSharedPreferences("roomateApp", MODE_PRIVATE);
        String roomcodePrefrence = prefs.getString("roomcode", "Error");
        String usernamePrefrence = prefs.getString("username", "Error");

        AddTask myTask = new AddTask(task, date, id, usernamePrefrence);

        DatabaseReference removeTask = FirebaseDatabase.getInstance().getReference().child(roomcodePrefrence).child("Claimed").child(usernamePrefrence).child(myTask.getId());
        removeTask.removeValue();
        DatabaseReference removeFromGeneralPool = FirebaseDatabase.getInstance().getReference().child(roomcodePrefrence).child("claimedGeneral").child(myTask.getId());
        removeFromGeneralPool.removeValue();

        return true;
    }
    }


