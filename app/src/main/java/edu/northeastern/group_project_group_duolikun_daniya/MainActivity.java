package edu.northeastern.group_project_group_duolikun_daniya;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import edu.northeastern.group_project_group_duolikun_daniya.data.User;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private DatabaseReference usersRef;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // todo remove this line
        // Log.d("LogCat - MainActivity", "onCreate() called");
        setContentView(R.layout.activity_main);
        replaceFragment(new HomeFragment());

        // Initializations
        firebaseAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference("users");
        usersRef.child("testNode").setValue("testValue");
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Bottom Navigation View
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.home) {
                    replaceFragment(new HomeFragment());
                } else if (itemId == R.id.members) {
                    replaceFragment(new MembersFragment());
                } else if (itemId == R.id.expenses) {
                    replaceFragment(new ExpensesFragment());
                } else if (itemId == R.id.transactions) {
                    replaceFragment(new TransactionsFragment());
                }

                item.setChecked(true);

                return false;
            }
        });

        //
        checkCurrentUserExists();

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

    /**
     * Checks if the current user exists in the database, if not, creates a new user node
     */
    private void checkCurrentUserExists() {
        // todo remove this line
        Log.d("LogCat - MainActivity", "checkCurrentUser(): Checking current user");
        FirebaseUser currentFirebaseUser = firebaseAuth.getCurrentUser();
        Log.d("LogCat - MainActivity",
                "checkCurrentUser(): currentFirebaseUser: " + currentFirebaseUser.getEmail());
        if (currentFirebaseUser != null) {
            // Get user's email
            String userEmail = currentFirebaseUser.getEmail();
            Log.d("LogCat - MainActivity", "checkCurrentUser(): userEmail: " + userEmail);
            if (userEmail != null) {
                // Check if user node exists in the database
                usersRef.child(userEmail.replace(".", ",")).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Log.d("LogCat - MainActivity", "onDataChange(DataSnapshot dataSnapshot): " +
                                    "userEmail already exists in users node");
                            // todo check current user's number of groups
                            checkNumberOfGroupsForUser(userEmail);
                        } else {
                            // User node does not exist, create a new user node
                            Log.d("LogCat - MainActivity", "onDataChange(DataSnapshot dataSnapshot): " +
                                    "dataSnapshot.exists() does not exists");
                            createNewUser(userEmail);
                            Log.d("LogCat - MainActivity", "onDataChange(DataSnapshot dataSnapshot): " +
                                    "createNewUser() called");

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

    private void checkNumberOfGroupsForUser(String userEmail) {
        Log.d("LogCat - MainActivity", "checkNumberOfGroupsForUser(): Checking number of groups for " +
                "user: " + userEmail);
        // Get a reference to the current user's groups node
        DatabaseReference userGroupsRef = usersRef.child(userEmail.replace(".", ",")).child("groups");
        Log.d("LogCat - MainActivity", "checkNumberOfGroupsForUser(): userGroupsRef: " + userGroupsRef.toString());

        userGroupsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Get the number of groups
                    int numGroups = (int) snapshot.getChildrenCount();
                    Log.d("LogCat - MainActivity", "checkNumberOfGroupsForUser(): numGroups: " + numGroups);
                    // todo
                    //  =0 create new group node
                    //  =1 show that group
                    //  >1 show lastInteractedGroup
                }
                // if the current user doesn't have any groups, create one
                else {
                    Log.d("LogCat - MainActivity", "checkNumberOfGroupsForUser(): snapshot.exists() is " +
                            "false, user doesn't have any groups");
                    promptUserToJoinOrCreateGroup(userEmail);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    private void createNewUser(String email) {
        // Create a new User object
        User newUser = new User(email, new HashMap<>(), null);

        // Write the User to the Firebase Database
        // Replaced '.' in email with ',' to use as Firebase key
        usersRef.child(email.replace(".", ",")).setValue(newUser)
                .addOnSuccessListener(aVoid -> {
                    Log.d("LogCat - MainActivity", "createNewUser: User created successfully");
                })
                .addOnFailureListener(e -> {
                    Log.w("MainActivity", "createNewUser: Failed to create user", e);
                });
    }

    // todo
    private void createNewGroup(String groupName, String groupID, Map<String, Boolean> members) {

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
//                                Log.d("LogCat - GroupCount", "User is part of one group");
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
//                            promptUserToJoinOrCreateGroup(userEmail);
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
//            Log.d("LogCat - GroupCount", "User is part of one group");
//        } else if (numberOfGroups > 0) {
//            // User is part of groups, proceed with the last interacted group
//            // todo Show the last interacted group here
//        } else {
//            // User has no groups, prompt to create or join
//            makeAToast("No groups found, please create or join one");
//            promptUserToJoinOrCreateGroup();
//        }
//    }


    private void promptUserToJoinOrCreateGroup(String userEmail) {
        Log.d("LogCat - MainActivity", "promptUserToJoinOrCreateGroup(): Prompting user to join or " +
                "create a group");
        Intent intent = new Intent(MainActivity.this, CreateOrJoinGroupActivity.class);
        intent.putExtra("userEmail", userEmail);
        startActivity(intent);
    }

    /**
     * Helper method to make a toast
     */
    private void makeAToast(String message) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

}