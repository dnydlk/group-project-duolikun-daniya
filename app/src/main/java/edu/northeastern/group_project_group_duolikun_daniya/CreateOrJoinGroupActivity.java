package edu.northeastern.group_project_group_duolikun_daniya;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

public class CreateOrJoinGroupActivity extends AppCompatActivity {
    TextInputEditText createGroupEditText;
    TextInputEditText joinGroupEditText;
    Button createGroupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_or_join_group);

        createGroupEditText = findViewById(R.id.create_group);
        joinGroupEditText = findViewById(R.id.join_group);
        createGroupButton = findViewById(R.id.next_create_or_join_btn);
    }

}
