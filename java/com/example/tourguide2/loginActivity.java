package com.example.tourguide2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

import com.example.tourguide2.utils.User;
import com.example.tourguide2.utils.emailValidationhelper;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shobhitpuri.custombuttons.GoogleSignInButton;

public class loginActivity extends AppCompatActivity {
    private static final String TAG = "loginActivity";
    private GoogleSignInClient mGoogleSignInClient;
    private Context mContext = loginActivity.this;
    private TextInputEditText emailEditText;
    private TextInputEditText passwordEditText;
    private String email, password;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private TextView registerTextView;
    private Button loginButton;
    private FirebaseUser firebaseUser;
    private GoogleSignInButton googleSignInButton;
    private int RC_SIGN_IN = 1;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("User");
        registerTextView = findViewById(R.id.registerHere);
        emailEditText = findViewById(R.id.loginEmailInput);
        passwordEditText = findViewById(R.id.loginPasswordInput);
        progressBar = findViewById(R.id.loginPorgressBar);
        loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                signIn();

            }
        });
        registerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickedRegister();
            }
        });
        configureGoogleSignIn();
        googleSignInButton = findViewById(R.id.googleSignInButton);
        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleSignIn();
            }
        });



    }
    private void configureGoogleSignIn()
    {
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }
    private void googleSignIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                Toast.makeText(loginActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();

            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            User user = new User(firebaseUser.getDisplayName(),firebaseUser.getEmail());
                            reference.child(firebaseUser.getUid()).setValue(user);

                            startActivity(new Intent(loginActivity.this,homeActivity.class));


                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(loginActivity.this, (CharSequence) task.getException(),Toast.LENGTH_LONG).show();

                        }
                    }
                });
    }

    public void clickedRegister() {
        Intent intentRegister = new Intent(this, registerActivity.class);
        startActivity(intentRegister);
    }


    public void signIn() {
        progressBar.setVisibility(View.VISIBLE);
        email = emailEditText.getText().toString();
        password = passwordEditText.getText().toString();
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
        if (password.length() < 6) {
            passwordEditText.requestFocus();
            passwordEditText.setError("The password should have more than 6 characters");
            progressBar.setVisibility(View.GONE);
            return;
        }
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(mContext, "Signing In", Toast.LENGTH_SHORT).show();
                    if (mAuth.getCurrentUser().isEmailVerified()) {
                        Intent homeIntent = new Intent(mContext, homeActivity.class);
                        startActivity(homeIntent);
                        finish();
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                    else {
                        Toast.makeText(loginActivity.this,"verify your email",Toast.LENGTH_LONG).show();
                        mAuth.signOut();
                        progressBar.setVisibility(View.INVISIBLE);
                        return;
                    }
                } else {
                    Toast.makeText(mContext, "No such User. Try again", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);

                }
            }
        });


    }

}