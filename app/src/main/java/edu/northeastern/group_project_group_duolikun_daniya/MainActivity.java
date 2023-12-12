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
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("LogCat - MainActivity", "onCreate() called");
        setContentView(R.layout.activity_main);
        replaceFragment(new HomeFragment());

        // Initializations
        firebaseAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference("users");
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

        // Check if the current user exists in the database, if not, create a new user node
        checkCurrentUserExists();

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

    /**
     * Checks if the current user exists in the database, if not, creates a new user node
     */
    private void checkCurrentUserExists() {
        Log.d("LogCat - MainActivity", "checkCurrentUser(): called\n" +
                "   Checking current user");
        FirebaseUser currentFirebaseUser = firebaseAuth.getCurrentUser();
        Log.d("LogCat - MainActivity",
                "        The current FIREBASE User is " + currentFirebaseUser.getEmail());
        if (currentFirebaseUser != null) {
            // Get user's email
            String userEmail = currentFirebaseUser.getEmail();
            if (userEmail != null) {
                // Check if user node exists in the database
                usersRef.child(userEmail.replace(".", ","))
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (!dataSnapshot.exists()) {
                                    // User node does not exist, create a new user node
                                    Log.d("LogCat - MainActivity",
                                            "            And this user is not in the Database");
                                    createNewUser(userEmail);
                                    checkNumberOfGroupsForUser(userEmail);
                                }
                                if (dataSnapshot.exists()) {
                                    Log.d("LogCat - MainActivity",
                                            "            This user is already in users node\n");
                                    // todo check current user's number of groups
                                    checkNumberOfGroupsForUser(userEmail);
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
        Log.d("LogCat - MainActivity", "createNewUser(): called\n" +
                "   Creating new user");
        // Create a new User object
        User newUser = new User(email, new HashMap<>(), null);

        // Write the User to the Firebase Database
        // Replaced '.' in email with ',' to use as Firebase key
        usersRef.child(email.replace(".", ",")).setValue(newUser)
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
    }

    private void checkNumberOfGroupsForUser(String userEmail) {
        Log.d("LogCat - MainActivity", "checkNumberOfGroupsForUser(): called\n" +
                "   Checking number of groups for user for user: " + userEmail);
        // Get a reference to the current user's groups node
        DatabaseReference userGroupsRef = usersRef.child(userEmail.replace(".", ",")).child(
                "groups");
        userGroupsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Get the number of groups
                    int numGroups = (int) snapshot.getChildrenCount();
                    Log.d("LogCat - MainActivity",
                            "   " + userEmail + "'s number of groups: " + numGroups);
                    // todo
                    //  =0 create new group node
                    //  =1 show that group
                    //  >1 show lastInteractedGroup
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
     * Helper method to generate a group.
     */
    private void promptUserToJoinOrCreateGroup(String userEmail) {
        Log.d("LogCat - MainActivity", "promptUserToJoinOrCreateGroup(): called\n" +
                "   Prompting user to join or create a group");
        Intent intent = new Intent(MainActivity.this, CreateOrJoinGroupActivity.class);
        intent.putExtra("userEmail", userEmail);
        startActivity(intent);
    }

    /**
     * Helper method to make a toast.
     */
    private void makeAToast(String message) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
    }

}