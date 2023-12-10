package edu.northeastern.group_project_group_duolikun_daniya;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

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
            makeAToast("No groups found, please create or join one");
            promptUserToJoinOrCreateGroup();
        }
    }


    private void promptUserToJoinOrCreateGroup() {
        Intent intent = new Intent(MainActivity.this, CreateOrJoinGroupActivity.class);
        startActivity(intent);
//        // todo rename dialog to dialogCreateOrJoinGroup
//        Dialog dialog = new Dialog(this);
//        dialog.setContentView(R.layout.dialog_create_or_join_group);
//
//        // Adjust dialog size
//        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
//        layoutParams.copyFrom(dialog.getWindow().getAttributes());
//        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
//        layoutParams.height = (int) (getResources().getDisplayMetrics().heightPixels * 0.65);
//        dialog.show();
//        dialog.getWindow().setAttributes(layoutParams);
//
//        dialog.findViewById(R.id.next_on_dialog_btn).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String groupNameToCreate = ((EditText) dialog.findViewById(R.id.create_group)).getText().toString();
//                String groupNameToJoin = ((EditText) dialog.findViewById(R.id.join_group)).getText().toString();
//
//                if (groupNameToCreate.isEmpty() && groupNameToJoin.isEmpty()) {
//                    makeAToast("Please enter a group name to create or join one via group code");
//                } else if (!groupNameToCreate.isEmpty() && !groupNameToJoin.isEmpty()) {
//                    makeAToast("Please choose either create or join, not both");
//                } else if (!groupNameToCreate.isEmpty()) {
//                    // todo Create group
//                    makeAToast("Create group");
//                } else {
//                    // todo Join group
//                    makeAToast("Join group");
//                }
//            }
//        });
    }

    private void makeAToast(String message) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
    }

}