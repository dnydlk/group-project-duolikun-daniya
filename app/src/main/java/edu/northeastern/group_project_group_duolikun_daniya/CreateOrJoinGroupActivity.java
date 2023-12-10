package edu.northeastern.group_project_group_duolikun_daniya;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Random;

import edu.northeastern.group_project_group_duolikun_daniya.data.Group;

public class CreateOrJoinGroupActivity extends AppCompatActivity {
    TextInputEditText createGroupEditText;
    TextInputEditText joinGroupEditText;
    Button nextBtn;
    FirebaseUser firebaseCurrentUser;
    String userId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_or_join_group);

        // Initializations
        createGroupEditText = findViewById(R.id.create_group);
        joinGroupEditText = findViewById(R.id.join_group);
        nextBtn = findViewById(R.id.next_create_or_join_btn);
        firebaseCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Get the user's ID
        if (userId != null) {
            userId = firebaseCurrentUser.getUid();
        }

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String groupNameToCreate = String.valueOf(createGroupEditText.getText());
                String groupIDToJoin = String.valueOf(joinGroupEditText.getText());

                if (groupNameToCreate.isEmpty() && groupIDToJoin.isEmpty()) {
                    makeAToast("Please enter a group name to create or join one via group code");
                } else if (!groupNameToCreate.isEmpty() && !groupIDToJoin.isEmpty()) {
                    makeAToast("Please choose either create or join, not both");
                } else if (!groupNameToCreate.isEmpty()) {
                    createGroupToFirebase(groupNameToCreate);
                } else {
                    joinGroupOnFirebase(groupIDToJoin, userId);
                }
            }
        });
    }

    /**
     * Exit app on back button press on this activity
     */
    @Override
    public void onBackPressed() {
        // todo give user the choice to log out
        new AlertDialog.Builder(this).setTitle("").setMessage("You need to create or join a group" +
                " to be able to use the app's features\n" +
                "Are you sure you want to " + "exit?").setPositiveButton(
                "Yes", (dialog, which) -> {
                    finishAffinity();
                    super.onBackPressed();
                }).setNegativeButton("No", null).show();

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

    /**
     * Helper method to join an existing group on Firebase
     */
    private void joinGroupOnFirebase(String groupIDToJoin, String userId) {
        // Reference to the user's 'groups' node in Firebase
        DatabaseReference userGroupsRef =
                FirebaseDatabase.getInstance().getReference("users").child(userId).child("groups");

        // Add the group to the user's list of groups
        userGroupsRef.child(groupIDToJoin).setValue(true)
                .addOnSuccessListener(aVoid -> {
                    // Successfully added the group to the user's list
                    makeAToast("Successfully joined the group");
                })
                .addOnFailureListener(e -> {
                    // Failed to add the group to the user's list
                    makeAToast("Failed to join the group, please make sure the group code is " +
                            "correct");
                });
    }

    private void createGroupToFirebase(String groupNameToCreate) {
        // Reference to the 'groups' node in Firebase
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("groups");

        // Generate a unique ID for the group (Eg. a12345)
        String groupId = generateGroupId();

        // Create a new Group object
        Group group = new Group(groupNameToCreate, groupId, new HashMap<>());

        // Save the group to Firebase
        databaseReference.child(groupId).setValue(group)
                .addOnSuccessListener(aVoid -> {

                    // Group created successfully
                    Log.d("CreateOrJoinGroup", "Group created successfully");
                    makeAToast("Group created successfully");

                    // Back to main activity
                    Intent intent = new Intent(CreateOrJoinGroupActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    // Group creation failed
                    Log.d("CreateOrJoinGroup", "Group creation failed");
                    makeAToast("Group creation failed");
                });

    }

    /**
     * Helper method to make a Toast
     */
    private void makeAToast(String message) {
        Toast.makeText(CreateOrJoinGroupActivity.this, message, Toast.LENGTH_SHORT).show();
    }


    /**
     * Helper method to generate a unique group ID
     */
    private String generateGroupId() {
        Random random = new Random();

        // Generate a random alphabet character (A-Z)
        char randomChar = (char) (random.nextInt(26) + 'A');

        // Generate a random 5-digit number
        int randomNumber = 10000 + random.nextInt(90000);

        // Return the concatenation of the random character and the random number as the groupID
        return randomChar + String.valueOf(randomNumber);
    }


}