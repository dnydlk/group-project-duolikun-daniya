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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Random;

import edu.northeastern.group_project_group_duolikun_daniya.data.Group;

public class CreateOrJoinGroupActivity extends AppCompatActivity {
    private final String groupID = generateGroupId();
    private TextInputEditText createGroupEditText;
    private TextInputEditText joinGroupEditText;
    private Button nextBtn;
    private String userID;
    private String groupNameToCreate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("LogCat - CreateOrJoinGroupActivity", "onCreate() called");
        setContentView(R.layout.activity_create_or_join_group);

        // Initializations
        createGroupEditText = findViewById(R.id.create_group);
        joinGroupEditText = findViewById(R.id.join_group);
        nextBtn = findViewById(R.id.next_create_or_join_btn);

        // Get the user's ID
        userID = getIntent().getStringExtra("userEmail");
        Log.d("LogCat - CreateOrJoinGroupActivity", "userID: " + userID);

//         I forget what i want to do here
//         but this is getting current user's groups node reference
        assert userID != null;
        DatabaseReference userGroupsRef =
                FirebaseDatabase.getInstance().getReference("users").child(userID.replace(".",
                        ",")).child("groups");

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                groupNameToCreate = String.valueOf(createGroupEditText.getText());
                String groupIDToJoin = String.valueOf(joinGroupEditText.getText());

                // Both fields are empty
                if (groupNameToCreate.isEmpty() && groupIDToJoin.isEmpty()) {
                    makeAToast("Please enter a group name to create or join one via group code");
                }
                // Both fields are NOT empty
                else if (!groupNameToCreate.isEmpty() && !groupIDToJoin.isEmpty()) {
                    makeAToast("Please choose either create or join, not both");
                }
                // Join group field is empty, CREATE A NEW GROUP
                else if (!groupNameToCreate.isEmpty()) {
                    // Check if the group name already exists, if not, create a new group
                    createNewGroup(groupNameToCreate, groupID, userGroupsRef);
                    toEditMembersPage();
                    return;
                }
                // Create group field is empty, JOIN AN EXISTING GROUP
                else {
                    // todo rewrite this joinGroupOnFirebase(groupIDToJoin, userId);
                }
            }
        });
    }

    /**
     * Helper method to make a Toast
     */
    private void makeAToast(String message) {
        Toast.makeText(CreateOrJoinGroupActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Helper method to create a new group on Firebase
     * todo redo this
     */
    private void createNewGroup(String groupNameToCreate, String groupID,
                                DatabaseReference userGroupsRef) {
        Log.d("LogCat - CreateOrJoinGroupActivity", "createNewGroup(): called\n" +
                "   Creating a new group with name: " + groupNameToCreate);
        // Create a new Group object
        Group group = new Group(groupNameToCreate, groupID, new HashMap<>());

        // Save the group to Firebase
        userGroupsRef.child(groupID).setValue(group)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Group created successfully
                        Log.d("LogCat - CreateOrJoinGroupActivity",
                                "      New group: " + groupNameToCreate + " created " +
                                        "successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Group creation failed
                        Log.d("LogCat - LogCat", "      " + groupNameToCreate + " creation failed");
                        makeAToast("Group creation failed");
                    }
                });

        // Set the user's lastInteractedGroup to the newly created group
        FirebaseDatabase.getInstance().getReference("users").child(userID).child(
                "lastInteractedGroup").setValue(groupID);
        Log.d("LogCat - CreateOrJoinGroupActivity",
                userID + "'s lastInteractedGroup set to " + groupID);
    }

    private void toEditMembersPage() {
        Log.d("LogCat - CreateOrJoinGroupActivity", "toEditMembersPage(): called");
        Intent intent = new Intent(CreateOrJoinGroupActivity.this, EditMembersActivity.class);
        intent.putExtra("groupID", groupID);
        intent.putExtra("userEmail", userID);
        intent.putExtra("groupName", groupNameToCreate);
        startActivity(intent);
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
     * todo redo this
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

    /**
     * Helper method to generate a unique group ID
     */
    private String generateGroupId() {
        Random random = new Random();

        // Generate a random alphabet character (A-Z)
//        char randomChar = (char) (random.nextInt(26) + 'A');
        char randomChar = (char) (new Random().nextInt(26) + 'A');

        // Generate a random 5-digit number
        int randomNumber = 10000 + random.nextInt(90000);

        // Return the concatenation of the random character and the random number as the groupID
        return randomChar + String.valueOf(randomNumber);
    }

    // todo add a cancel button, when clicked, delete the group from firebase

}
