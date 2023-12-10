package edu.northeastern.group_project_group_duolikun_daniya;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LogIn extends AppCompatActivity {
    TextInputEditText editTextEmail;
    TextInputEditText editTextPassword;
    Button logInBtn;
    FirebaseAuth firebaseAuth;
    ProgressBar progressBar;
    TextView signUpTextView;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and open MainActivity accordingly.
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if(currentUser != null){
            // todo Only testing now, so the new intent is directed to the User Account Page
            //  But in the end it should be the Home page
            makeAToast("Welcome Back!");
//            Intent intent = new Intent(getApplicationContext(), UserAccount.class);
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        // Initializations
        firebaseAuth = FirebaseAuth.getInstance();
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        progressBar = findViewById(R.id.progress_bar);
        logInBtn = findViewById(R.id.log_in_btn);
        signUpTextView = findViewById(R.id.sign_up_now);



        // Log in button
        logInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Hide the keyboard onClick
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);

                progressBar.setVisibility(View.VISIBLE);
                String email = String.valueOf(editTextEmail.getText());
                String password = String.valueOf(editTextPassword.getText());

                // Check if email or password is empty
                if (email.isEmpty()) {
                    Toast.makeText(LogIn.this, "Enter email", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.isEmpty()) {
                    Toast.makeText(LogIn.this, "Enter password", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Sign in a user with email address and password
                firebaseAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    // Open main activity if succeed
                                    // todo Only testing now, so the new intent is directed to the User Account Page
                                    //  But in the end it should be the Home page
                                    makeAToast("Welcome Back!");
//                                    Intent intent = new Intent(getApplicationContext(), UserAccount.class);
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
//                                    // If sign in fails, display a message to the user.
//                                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                                    Toast.makeText(LogIn.this, "Log-In Failed. Please Verify Your" +
                                                    " Email and Password and Try Again.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        // Click to sign up
        signUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SignUp.class);
                startActivity(intent);
                finish();
            }
        });
    }

    // Helper method to make a Toast
    private void makeAToast(String message) {
        Toast.makeText(LogIn.this, message, Toast.LENGTH_SHORT).show();
    }
}