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
import com.google.firebase.database.FirebaseDatabase;

import java.util.Random;

/**
 * User creates account with email and password in the given edit text fields
 * generate a roomcode by generating a random number 1-36 (A-Z,0-9) all capital letters
 * load the 4 digits into a char array, convert them to string, set their shared prefrence roomcode
 * to whatever generated
 *
 * (11/20/17)
 * User has to go back to main menu to actually log in as of now
 *
 *
 */
public class SignUp extends AppCompatActivity implements View.OnClickListener {

    DatabaseReference databaseTasks;
    EditText userName, password;
    TextView roomCodeInput;

    //auth
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    //shared prefrences
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
     //   getWindow().getDecorView().setBackgroundColor(Color.BLACK);

        //get all the stuff
        userName = (EditText) findViewById(R.id.userName);
        password = (EditText) findViewById((R.id.passwordInput));
        roomCodeInput = (TextView) findViewById(R.id.roomCode);
        Button submit = (Button) findViewById(R.id.Submit);


        //auth
        mAuth = FirebaseAuth.getInstance();

        findViewById(R.id.loginCreateUser).setOnClickListener(this);
        findViewById(R.id.Submit).setOnClickListener(this);


    }
    //send em back to homepage if they click to go back
    //if not registeruser()
    public void onClick(View v) {
        Intent i = new Intent(SignUp.this,AuthPage.class);
        //Intent j = new Intent(SignUp.this, .class);
        switch (v.getId()){
            case  R.id.Submit:
                registerUser();
                break;
            case R.id.loginCreateUser:
                startActivity(i);
                break;
        }

    }


    /**
     *check to see if the password and email are valid
     * Generate room code
     * create directory in database for the roomcode
     *
     */


    public void registerUser() {
        final String username = userName.getText().toString();
        String Password = password.getText().toString();
        final String random = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        final char[] c = new char[4];

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
        mAuth.createUserWithEmailAndPassword(username, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                String[] trimedUsername;
                if(task.isSuccessful()) {

                    //create char array for roomcode
                    for (int x = 0; x < 4; x++) {
                        Random r = new Random();
                        int Result = r.nextInt(36 - 1) + 0;
                        c[x] = random.charAt(Result);

                    }
                    String roomCodeGen = String.copyValueOf(c);
                    roomCodeInput.setText("Your room code is: " + roomCodeGen + " give this code to your roommates so they can join! go back to the main screen to log in!");
                    FirebaseDatabase database = FirebaseDatabase.getInstance()  ;
                    databaseTasks = database.getReference();


                    //create directory in database: Roomcode -
                    //                                       |-> username
                    //                                       |->unclaimed
                    databaseTasks.child(roomCodeGen).child("Claimed").setValue("null");
                    databaseTasks.child(roomCodeGen).child("Unclaimed").setValue("null");

                    trimedUsername = userName.getText().toString().split("@");
                    String actualUsername = trimedUsername[0];
                    SharedPreferences.Editor editor = getSharedPreferences("roomateApp", MODE_PRIVATE).edit();
                    editor.putString("roomcode", roomCodeGen);
                    editor.putString("username", actualUsername);
                    editor.apply();

                    Toast.makeText((getApplicationContext()), "You're in!", Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText((getApplicationContext()), "Some error, try again!", Toast.LENGTH_SHORT).show();
            }
        });

    }

}
