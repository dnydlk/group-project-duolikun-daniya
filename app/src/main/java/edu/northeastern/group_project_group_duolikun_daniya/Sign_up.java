package edu.northeastern.group_project_group_duolikun_daniya;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

public class Sign_up extends AppCompatActivity {

    TextInputEditText editTextEmail;
    TextInputEditText editTextPassword;
    Button signUpBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        //
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        signUpBtn = findViewById(R.id.sign_up_btn);

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = String.valueOf(editTextEmail.getText());
                String password = String.valueOf(editTextPassword.getText());

                // Check if email or password is empty
                if (email.isEmpty()) {
                    Toast.makeText(Sign_up.this, "Enter email", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.isEmpty()) {
                    Toast.makeText(Sign_up.this, "Enter password", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Create user in Firebase Database

            }
        });
    }
}