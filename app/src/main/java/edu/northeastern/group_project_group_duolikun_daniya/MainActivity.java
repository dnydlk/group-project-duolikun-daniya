package edu.northeastern.group_project_group_duolikun_daniya;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import edu.northeastern.group_project_group_duolikun_daniya.data.User;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            // Redirect to login activity if no user is logged in
            Intent intent = new Intent(MainActivity.this, LogIn.class);
            startActivity(intent);
            finish();
        } else {
            // Check if user has groups
            checkUserGroups(currentUser.getUid());
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("message");
        myRef.setValue("Hello, World!");

    }

    private void checkUserGroups(String userId) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null && user.getGroups() != null) {
                    int numberOfGroups = user.getGroups().size();
                    handleGroupCount(numberOfGroups);
                } else {
                    // todo Handle case where user has no groups
                    promptUserToJoinOrCreateGroup();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("GroupCount", "Failed to read value.", databaseError.toException());
            }
        });
    }

    private void handleGroupCount(int numberOfGroups) {
        if (numberOfGroups == 1) {
            // User is part of one group, present with that group
            // todo Show the group here
        } else if (numberOfGroups > 0) {
            // User is part of groups, proceed with the last interacted group
            // todo Show the last interacted group here
        } else {
            // User has no groups, prompt to create or join
            promptUserToJoinOrCreateGroup();
        }
    }

    private void promptUserToJoinOrCreateGroup() {
        // todo Show a dialog, a new activity, or a fragment asking the user to join or create a group
        //  A dialog like link collector is good
    }


}