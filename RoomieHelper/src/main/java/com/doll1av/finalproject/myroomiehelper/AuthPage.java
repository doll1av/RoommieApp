package com.doll1av.finalproject.myroomiehelper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import static com.doll1av.finalproject.myroomiehelper.R.id.passwordInput;

/**
 * Authentication page, if a user does not have a roomcode click on need a code button
 *If a user already has room code fill out information in textfeilds username, password, edittextroomcode
 * if they do not have an account create an account and store their room code in their shared prefrences then login
 * else log in (11/20/2017)
 */
public class AuthPage extends AppCompatActivity implements View.OnClickListener {


    //set variable names for needed edit text feilds
    EditText userName, password, editTextRoomCode;
    TextView code;
    DatabaseReference databaseTasks;
    //set Mauth variable
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private static int SPLASH_TIME_OUT = 4000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_page);



        //find edit text and buttons corresponding item
        userName = (EditText) findViewById(R.id.userName);
        password = (EditText) findViewById((passwordInput));
        editTextRoomCode = (EditText) findViewById(R.id.enterRoomCode);
        Button submit = (Button) findViewById(R.id.Submit);

        //testing
        code = (TextView)findViewById(R.id.textViewEmail);

        //get instance
        mAuth = FirebaseAuth.getInstance();

        //set onclick listeners
        findViewById(R.id.loginCreateUser).setOnClickListener(this);
        findViewById(R.id.Submit).setOnClickListener(this);
    }

    //if they click on logincreateuser move them to signup page
    //submit, start the login activity
    public void onClick(View v) {
        Intent j = new Intent(AuthPage.this, SignUp.class);
        switch (v.getId()){
            case  R.id.loginCreateUser:

                startActivity(j);
                break;
            case R.id.Submit:
                login();
                break;
                }

            }


    //overide start method to log in users if they have logged in before
    @Override
    protected void onStart() {
        super.onStart();

        if(mAuth.getCurrentUser() != null) {
            finish();
            Intent i = new Intent(AuthPage.this,Home.class);
            startActivity(i);
        }
    }

    /**
     * login activity for the app
     * find the username and password entered in the field
     * check if they meet certain error requirements and post message if they do
     * if the login is sucessful login with their user/password and move them to home screen
     * set roomcode in userprefrences
     *
     * if user/password doesnt exist make them an account with the roomcode with registeruser() method
     *
     */
    private void login() {
        final String username = userName.getText().toString();
        String Password = password.getText().toString();

        if(username.isEmpty()) {
            userName.setError("Enter email");
            userName.requestFocus();
            return;

        }

        if(!Patterns.EMAIL_ADDRESS.matcher(username).matches()) {

            userName.setError("invalid email");
            userName.requestFocus();
            return;
        }

        if(Password.length() < 6) {
            password.setError("Password is atleast 6 chars");
            password.requestFocus();
            return;
        }

        if(Password.isEmpty()) {
            password.setError("Enter password");
            password.requestFocus();
            return;

        }



        mAuth.signInWithEmailAndPassword(username, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    Intent i = new Intent(AuthPage.this,Home.class);

                    //store prefrence of roomcode from edit text
                    String [] trimedUsername;
                    trimedUsername = username.split("@");
                    String actualUsername = trimedUsername[0];
                    String roomCodeGen = editTextRoomCode.getText().toString();
                    SharedPreferences.Editor editor = getSharedPreferences("roomateApp", MODE_PRIVATE).edit();
                    editor.putString("roomcode", roomCodeGen);
                    editor.putString("username", actualUsername);
                    editor.apply();
                    startActivity(i);
                    finish();
                }
                if(!task.isSuccessful()){

                    registerUser(username, password.toString());

                }
            }
        });

    }

    /**
     *SAME METHOD AS INSIDE SIGN UP MINUS GENERATING THE ROOMCODE
     *
     * check for invalid email then create the user with email and password
     *
     */
    public void registerUser(final String newusername, String password) {
        final String Username = userName.getText().toString();


        if(Username.isEmpty()) {
            userName.setError("Enter email");
            userName.requestFocus();
            return;

        }



        mAuth.createUserWithEmailAndPassword(newusername, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                String[] trimedUsername;

                trimedUsername = newusername.split("@");


                if(task.isSuccessful()) {
                    //add roomcode to shared prefrences

                    String roomCodeGen = editTextRoomCode.getText().toString();
                    trimedUsername = newusername.split("@");
                    String actualUsername = trimedUsername[0];
                    SharedPreferences.Editor editor = getSharedPreferences("roomateApp", MODE_PRIVATE).edit();
                    editor.putString("roomcode", roomCodeGen);
                    editor.putString("username", actualUsername);
                    editor.apply();
                    finish();
                    editor.apply();
                    Toast.makeText((getApplicationContext()), "You're registered!", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(AuthPage.this,Home.class);
                    startActivity(i);
                    finish();
                    }
                     //create directory in database: Roomcode -
                    //                                        |-> username
                   // databaseTasks.child(roomCodeGen).child(Username);




                }

            });
        };

    }









