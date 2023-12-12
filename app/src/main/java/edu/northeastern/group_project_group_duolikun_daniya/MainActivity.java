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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import edu.northeastern.group_project_group_duolikun_daniya.data.User;

public class MainActivity extends AppCompatActivity {

    public String getUserID() {
        return userID;
    }

    private static final String NODE_USERS = "users";
    private static final String NODE_GROUPS = "groups";
    private FirebaseAuth firebaseAuth;
    private DatabaseReference usersRef;
    private DatabaseReference curUserRef;
    private DatabaseReference userGroupsRef;
    private BottomNavigationView bottomNavigationView;
    private String firebaseAuthEmail;
    private String userID;

    public DatabaseReference getUserGroupsRef() {
        return userGroupsRef;
    }

//    private getLastInteractedGroupID() {
//        // todo
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("LogCat - MainActivity", "onCreate() called");
        setContentView(R.layout.activity_main);

        // Initializations
        replaceFragment(new HomeFragment());
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Get the logged in user's email from Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuthEmail = firebaseAuth.getCurrentUser().getEmail();
        Log.d("LogCat - MainActivity", "The current FIREBASE User is " + firebaseAuthEmail);

        // Get a reference to all users
        usersRef = FirebaseDatabase.getInstance().getReference(NODE_USERS);
        Log.d("LogCat - MainActivity", "usrRef: " + usersRef);

        // Get a reference to the current user's groups
        userID = userEmailAsID(firebaseAuthEmail);
        Log.d("LogCat - MainActivity", "userID: " + userID);
        userGroupsRef = usersRef.child(userID).child(NODE_GROUPS);
        Log.d("LogCat - MainActivity", "userGroupsRef: " + userGroupsRef);

        // Check if the current user exists in the database, if not, create a new user node
        checkCurrentUserExists();

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

    }

    /**
     * Helper method to replace a fragment.
     */
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    @NonNull
    private String userEmailAsID(String email) {
        return email.replace(".", ",");
    }

    /**
     * Checks if the current user exists in the database, if not, creates a new user node
     */
    private void checkCurrentUserExists() {
        Log.d("LogCat - MainActivity", "checkCurrentUser(): called\n" +
                "   Checking current user");

        // Check if firebaseAuthEmail exists in the database
        usersRef.child(userEmailAsID(firebaseAuthEmail))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.exists()) {
                            // User node does not exist, create a new user node
                            Log.d("LogCat - MainActivity",
                                    "       Current user is not in the database");
                            createNewUser(userID);
                            checkNumberOfGroupsForUser(userID);
                        }
                        if (dataSnapshot.exists()) {
                            Log.d("LogCat - MainActivity",
                                    "       Current user already in the database\n");
                            // todo check current user's number of groups
                            checkNumberOfGroupsForUser(userID);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d("LogCat - MainActivity",
                                "onCancelled: " + databaseError.getMessage());
                        makeAToast("Error: " + databaseError.getMessage());
                    }
                });
    }

    private void createNewUser(String email) {
        Log.d("LogCat - MainActivity", "createNewUser(): called");

        // Create a new User object
        User newUser = new User(email, new HashMap<>(), "None");

        // Add the User to the Firebase Database
        usersRef.child(userEmailAsID(email)).setValue(newUser)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("LogCat - MainActivity", "   createNewUser: User created " +
                                "successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("LogCat - MainActivity", "   createNewUser: User creation failed");
                    }
                });
        curUserRef = usersRef.child(userID);
    }

    private void checkNumberOfGroupsForUser(String userEmail) {
        Log.d("LogCat - MainActivity", "checkNumberOfGroupsForUser(): called");

        userGroupsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Get the number of groups
                    int numGroups = (int) snapshot.getChildrenCount();
                    Log.d("LogCat - MainActivity",
                            "   " + userEmail + "'s number of groups: " + numGroups);
                    if (numGroups == 1) {
                        Log.d("LogCat - MainActivity", "   " + userEmail + " has 1 group\n");
                        // todo show that group
                        //  setHomeText()
                        //  need groupName, groupID, MoneySpent
                    } else if (numGroups > 1) {
                        // todo show lastInteractedGroup
                        Log.d("LogCat - MainActivity", "   " + userEmail + " has more than 1 " +
                                "group\n");

                    }
                }
                // if the current user doesn't have any groups, create one
                else {
                    Log.d("LogCat - MainActivity", "   " + userEmail + " doesn't have any " +
                            "groups\n");
                    promptUserToJoinOrCreateGroup(userEmail);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    /**
     * Helper method to make a toast.
     */
    private void makeAToast(String message) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Helper method to generate a group.
     */
    private void promptUserToJoinOrCreateGroup(String userID) {
        Log.d("LogCat - MainActivity", "promptUserToJoinOrCreateGroup(): called\n" +
                "   Prompting user to join or create a group");
        Intent intent = new Intent(MainActivity.this, CreateOrJoinGroupActivity.class);
        intent.putExtra("userID", userID);
        startActivity(intent);
    }

}