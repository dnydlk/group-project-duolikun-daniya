package edu.northeastern.group_project_group_duolikun_daniya;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserAccount extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseUser user;
    TextView emailHeader;
    TextView userEmail;
    Button logOutBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_account);

        // Initializations
        auth = FirebaseAuth.getInstance();
        logOutBtn = findViewById(R.id.log_out_btn);
        emailHeader = findViewById(R.id.email_header);
        userEmail = findViewById(R.id.user_email);
        user = auth.getCurrentUser();

        // Check if use is null
        if (user == null) {
            // Go to log in page
            Intent intent = new Intent(getApplicationContext(), LogIn.class);
            startActivity(intent);
            finish();
        } else {
            // Show current user's email
            userEmail.setText(user.getEmail());
        }

        // Log out
        logOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Log out current user from Firebase Database
                FirebaseAuth.getInstance().signOut();
                // Go to Log in page
                Intent intent = new Intent(getApplicationContext(), LogIn.class);
                startActivity(intent);
                finish();
            }
        });
    }
}