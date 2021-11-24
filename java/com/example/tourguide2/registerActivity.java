package com.example.tourguide2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tourguide2.utils.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class registerActivity extends AppCompatActivity{
    private static final String TAG = "registerActivity";
    private Context mContext = registerActivity.this;
    TextInputEditText fullNameEditText, emailEditText, passwordEditText;
    String fullName, email, password;
    Button registerButton;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private ProgressBar progressBar;
    private DatabaseReference DBreference;
    private TextView signInIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Log.i("invoked","on create");

        mAuth = FirebaseAuth.getInstance();
//        creating firebase instance
        database = FirebaseDatabase.getInstance();
        DBreference = database.getReference();

        fullNameEditText = findViewById(R.id.registerFullNameInput);
        emailEditText = findViewById(R.id.registerEmailInput);
        passwordEditText = findViewById(R.id.registerPasswordInput);
        registerButton = findViewById(R.id.registerButton);
        progressBar = findViewById(R.id.registrationProgressBar);
        signInIntent = findViewById(R.id.signInIntent);


        signInIntent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(registerActivity.this,"sign in", LENGTH_LONG).show();
                SignInIntent();
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
    }

    private void SignInIntent() {
        Log.i("invoked","login");
        Intent signInIntent = new Intent(mContext,loginActivity.class);
        startActivity(signInIntent);
    }

    private void register() {

        Log.i("invoked","register");
        progressBar.setVisibility(View.VISIBLE);
        fullName = fullNameEditText.getText().toString();
        email = emailEditText.getText().toString();
        password = passwordEditText.getText().toString();
        Log.i("check",fullName+ " " +email + " "+ password+"");
        if (fullName.isEmpty()) {
            fullNameEditText.requestFocus();
            fullNameEditText.setError("Enter the full name");
            progressBar.setVisibility(View.GONE);
            return;
        }
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.requestFocus();
            emailEditText.setError("Enter the valid email address");
            progressBar.setVisibility(View.GONE);
            return;
        }
        if (password.isEmpty()) {
            passwordEditText.requestFocus();
            passwordEditText.setError("Password cannot be empty");
            progressBar.setVisibility(View.GONE);
            return;
        }
        if (password.length() <= 6) {
            passwordEditText.requestFocus();
            passwordEditText.setError("The password should have more than 6 characters");
            progressBar.setVisibility(View.GONE);
            return;
        }


        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.i("registering", "successfull");
                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                    Log.i("firebase user", String.valueOf(firebaseUser));
                    firebaseUser.sendEmailVerification();
                    Log.i("is varified", String.valueOf(firebaseUser.isEmailVerified()));
                    User user = new User(fullName, email);
                    DBreference.child("User").child(firebaseUser.getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                makeText(registerActivity.this, "User registered", LENGTH_LONG).show();
                                mAuth.signOut();
                                progressBar.setVisibility(View.INVISIBLE);
                                Intent loginIntent = new Intent(mContext, loginActivity.class);
                                startActivity(loginIntent);
                                finish();
                            }
                        }
                    });

                }
                else {
                    Toast.makeText(registerActivity.this, "Authentication failed: " + task.getException().getMessage(), LENGTH_LONG).show();
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
        });
    }



}









