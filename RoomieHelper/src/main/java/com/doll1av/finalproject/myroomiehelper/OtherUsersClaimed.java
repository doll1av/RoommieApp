package com.doll1av.finalproject.myroomiehelper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class OtherUsersClaimed extends AppCompatActivity {

    ListView tasks;
    List<AddTask> taskList;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    FirebaseDatabase database = FirebaseDatabase.getInstance()  ;
    DatabaseReference databaseTasks;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_users_claimed);

        taskList = new ArrayList<>();

        tasks = (ListView) findViewById(R.id.listViewTasks);
        String code = PreferenceManager.getDefaultSharedPreferences(OtherUsersClaimed.this).getString("roomcode", "");
        databaseTasks = database.getReference();
        Button button = (Button) findViewById(R.id.button5);
        Button button2 = (Button) findViewById(R.id.button6);
        Button button3 = (Button) findViewById(R.id.claimButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(OtherUsersClaimed.this,Home.class);
                startActivity(i);
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent j = new Intent(OtherUsersClaimed.this, CreateTask.class);
                startActivity(j);
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent k = new Intent(OtherUsersClaimed.this, MyTasks.class);
                startActivity(k);
            }
        });
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

                for(DataSnapshot tasksnapshot: dataSnapshot.child(code).child("claimedGeneral").getChildren()){
                    AddTask task = tasksnapshot.getValue(AddTask.class);

                    taskList.add(task);
                }


                ListLayoutAllClaimed adapter = new ListLayoutAllClaimed(OtherUsersClaimed.this, taskList);
                tasks.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
