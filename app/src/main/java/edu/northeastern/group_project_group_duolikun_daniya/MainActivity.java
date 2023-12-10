package edu.northeastern.group_project_group_duolikun_daniya;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import edu.northeastern.group_project_group_duolikun_daniya.data.User;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // todo remove this line
        // Log.d("MainActivity", "onCreate() called");
        setContentView(R.layout.activity_main);

        // Initializations
        firebaseAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference("users");

        checkCurrentUser();

//        testTextView = findViewById(R.id.testTextView1);
//
//
//        // Get the user's email from Firebase Authentication
//        firebaseCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
//        userEmail = firebaseCurrentUser.getEmail();
//        // todo Remove this line
//        System.out.println("User email is: " + userEmail);
//
//        // Check the the number of groups the user has via userEmail
//        checkUserGroups(userEmail);
//
//        // Write a message to the database
//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference myRef = database.getReference("message");
//        myRef.setValue("Hello, World!");

    }

    private void checkCurrentUser() {
        // todo remove this line
        Log.d("MainActivity", "checkCurrentUser(): Checking current user");
        FirebaseUser currentFirebaseUser = firebaseAuth.getCurrentUser();
        Log.d("MainActivity", "checkCurrentUser(): currentFirebaseUser: " + currentFirebaseUser.getEmail());
        if (currentFirebaseUser != null) {
            // Get user's email
            String userEmail = currentFirebaseUser.getEmail();
            Log.d("MainActivity", "checkCurrentUser(): userEmail: " + userEmail);
            if (userEmail != null) {
                // Check if user node exists in the database
                usersRef.child(userEmail.replace(".", ",")).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Log.d("MainActivity", "onDataChange(DataSnapshot dataSnapshot): dataSnapshot.exists()");
                        } else {
                            // User node does not exist, create a new user node
                            Log.d("MainActivity", "onDataChange(DataSnapshot dataSnapshot): dataSnapshot.exists() does not exists");
                            createNewUser(userEmail);

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Handle possible errors
                    }
                });
            }
        }
    }

    private void createNewUser(String email) {
        // Create a new User object
        User newUser = new User(email, new HashMap<>(), null);

        // Write the User to the Firebase Database
        // Replaced '.' in email with ',' to use as Firebase key
        usersRef.child(email.replace(".", ",")).setValue(newUser)
                .addOnSuccessListener(aVoid -> {
                    Log.d("MainActivity", "createNewUser: User created successfully");
                })
                .addOnFailureListener(e -> {
                    Log.w("MainActivity", "createNewUser: Failed to create user", e);
                });
    }


//    private void checkUserGroups(String userEmail) {
//        // Get a reference to the users node
//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference usersRef = database.getReference("users");
//
//        // Query the users node for the user with the given email
//        usersRef.orderByChild("email").equalTo(userEmail).addListenerForSingleValueEvent(new
//        ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()) {
//                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                        User user = snapshot.getValue(User.class);
//                        if (user != null && user.getGroups() != null && !user.getGroups()
//                        .isEmpty()) {
//
//                            // User is part of one or more groups
//                            if (user.getGroups().size() == 1) {
//
//                                // User is part of one group, present with that group
//                                // todo Show the group here
//                                testTextView.setText("User is part of one group, present with
//                                that group");
//                                Log.d("GroupCount", "User is part of one group");
//                            } else {
//                                // User is part of more than one groups, proceed with the last
//                                interacted group
//                                // todo Show the last interacted group here
//                                testTextView.setText("User is part of more than one groups,
//                                proceed with the last interacted group");
//                            }
//                            handleGroupCount(user.getGroups().size());
//                        } else {
//                            // User is not part of any group
//                            createThisNewUserToFirebase(userEmail);
//                            promptUserToJoinOrCreateGroup();
//                        }
//                    }
//                } else {
//                    // No user found with this email
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                // Handle the error
//            }
//        });
//
//    }

//    private void createThisNewUserToFirebase(String userEmail) {
//        currentUser = new User(userEmail, new HashMap<>(), null);
//    }

//    private void handleGroupCount(int numberOfGroups) {
//        if (numberOfGroups == 1) {
//            // User is part of one group, present with that group
//            // todo Show the group here
//            Log.d("GroupCount", "User is part of one group");
//        } else if (numberOfGroups > 0) {
//            // User is part of groups, proceed with the last interacted group
//            // todo Show the last interacted group here
//        } else {
//            // User has no groups, prompt to create or join
//            makeAToast("No groups found, please create or join one");
//            promptUserToJoinOrCreateGroup();
//        }
//    }


//    private void promptUserToJoinOrCreateGroup() {
//        Intent intent = new Intent(MainActivity.this, CreateOrJoinGroupActivity.class);
//        startActivity(intent);
//    }

    /**
     * Helper method to make a toast
     */
    private void makeAToast(String message) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
    }

}