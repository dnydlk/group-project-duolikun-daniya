package edu.northeastern.group_project_group_duolikun_daniya;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;
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
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

public class SignUp extends AppCompatActivity {
    TextInputEditText editTextEmail;
    TextInputEditText editTextPassword;
    Button signUpBtn;
    FirebaseAuth firebaseAuth;
    ProgressBar progressBar;
    TextView logInTextView;

    @Override
    public void onStart() {
        super.onStart();
        Log.d("SignUpActivity", "onStart() called");
        // Check if user is signed in (non-null) and open MainActivity accordingly.
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("SignUpActivity", "onCreate() called");
        setContentView(R.layout.activity_sign_up);

        // Initializations
        firebaseAuth = FirebaseAuth.getInstance();
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        progressBar = findViewById(R.id.progress_bar);
        signUpBtn = findViewById(R.id.sign_up_btn);
        logInTextView = findViewById(R.id.log_in_now);

        // Sign up Button
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("SignUpActivity", "Sign up button clicked");
                // Hide the keyboard onClick
                InputMethodManager inputMethodManager =
                        (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);

                progressBar.setVisibility(View.VISIBLE);
                String email = String.valueOf(editTextEmail.getText());
                String password = String.valueOf(editTextPassword.getText());

                // Check if email or password is empty
                if (email.isEmpty()) {
                    Toast.makeText(SignUp.this, "Enter email", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.isEmpty()) {
                    Toast.makeText(SignUp.this, "Enter password", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Create user with email and password in Firebase Database
                firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {

                                    // Sign in success, update UI with the signed-in user'
                                    // information
                                    Log.d("SignUpActivity", "createUserWithEmail:success");
                                    Toast.makeText(SignUp.this, "Account created.",
                                            Toast.LENGTH_SHORT).show();

                                    // Using a Handler to post a delayed Runnable: wait 2 seconds
                                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            // Code to be executed after 2 seconds
                                            Intent intent = new Intent(getApplicationContext(),
                                                    LogIn.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }, 1500);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w("SignUpActivity", "createUserWithEmail:failure",
                                            task.getException());
                                    try {
                                        throw task.getException();
                                    } catch (FirebaseAuthWeakPasswordException e) {
                                        makeAToast("Password too short. Please enter at least 6 " +
                                                "characters.");
                                    } catch (FirebaseAuthInvalidCredentialsException e) {
                                        makeAToast("Invalid email format. Please enter a valid " +
                                                "email address.");
                                    } catch (FirebaseAuthUserCollisionException e) {
                                        resetPassword("This email has already been registered. " +
                                                "Please use another email.", email);
                                    } catch (Exception e) {
                                        makeAToast("Authentication failed.");
                                    }
                                }
                            }
                        });
            }
        });

        // Click to log in
        logInTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LogIn.class);
                startActivity(intent);
                finish();
            }
        });
    }

    /**
     * Helper method to make a Toast
     */
    private void makeAToast(String message) {
        Toast.makeText(SignUp.this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Helper method to reset password
     */
    private void resetPassword(String messageToShow, String emailToResetPassword) {

        // Show a snackbar with an action to reset password
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), messageToShow,
                Snackbar.LENGTH_LONG);

        // Send password reset email
        snackbar.setAction("Send Email", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth = FirebaseAuth.getInstance();
                firebaseAuth.sendPasswordResetEmail(emailToResetPassword)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d("LogCat - ResetPassword", "Email sent.");
                                    makeAToast("Email sent");
                                }
                            }
                        });
            }
        });
        snackbar.show();
    }

    /**
     * Hide the keyboard when user clicks outside of the EditText
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof TextInputEditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm =
                            (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

}