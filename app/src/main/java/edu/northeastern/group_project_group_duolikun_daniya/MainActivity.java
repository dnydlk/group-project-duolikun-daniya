package edu.northeastern.group_project_group_duolikun_daniya;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private static final String USERS = "users";
    private static final String GROUPS = "groups";
    private DatabaseReference allUsersRef, userAllGroupsRef, lastInteractedGroupRef;
    private String userEmail, curGroupID;
    // UI
    private TextView groupNameTextView;
    private TextView totalSpentAmountTextView;
    private ImageView userAccountBtn, shareGroupNumBtn, switchGroupBtn, addGroupBtn;
    private BottomNavigationView bottomNavigationView;

    public void setCurGroupID(String curGroupID) {
        this.curGroupID = curGroupID;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("LogCat - MainActivity", "onCreate() called");
        setContentView(R.layout.activity_main);

        // Initialize UI components
        initializeUI();

        // Retrieve and display group name and total spent amount
        updateGroupNameAndTotalSpent();

        // Set listeners
        setListeners();

    }

    private void initializeUI() {
        Log.d("LogCat - MainActivity", "initializeUI(): called");
        groupNameTextView = findViewById(R.id.group_name_text_view);
        totalSpentAmountTextView = findViewById(R.id.total_spent_amount);
        userAccountBtn = findViewById(R.id.user_account_btn);
        shareGroupNumBtn = findViewById(R.id.share_group_num_btn);
        switchGroupBtn = findViewById(R.id.switch_group_btn);
        addGroupBtn = findViewById(R.id.add_group_btn);
        bottomNavigationView = findViewById(R.id.home_bottom_navigation);
    }


    private void updateGroupNameAndTotalSpent() {
        Log.d("LogCat - MainActivity", "updateGroupNameAndTotalSpent(): called");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".", ",");
        Log.d("LogCat - MainActivity", "updateGroupNameAndTotalSpent(): userEmail: " + userEmail);

        // Reference to the user's data
        DatabaseReference userRef = database.getReference("users").child(userEmail);
        Log.d("LogCat - MainActivity", "updateGroupNameAndTotalSpent(): userRef: " + userRef
                .getPath().toString());

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String currentGroupId = dataSnapshot.child("currentGroup").getValue(String.class);
                Log.d("LogCat - MainActivity", "updateGroupNameAndTotalSpent(): currentGroupId: " +
                        currentGroupId);

                // Fetch group name and update UI
                DatabaseReference groupRef = database.getReference("groups").child(currentGroupId);
                Log.d("LogCat - MainActivity", "updateGroupNameAndTotalSpent(): groupRef: " +
                        groupRef.getPath().toString());
                groupRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot groupSnapshot) {
                        //String groupName = groupSnapshot.child("groupName").getValue(String
                        // .class);
                        String groupName = groupSnapshot.getKey();
                        Log.d("LogCat - MainActivity", "updateGroupNameAndTotalSpent(): " +
                                "groupName: " +
                                groupName);
                        groupNameTextView.setText(groupName);
                        setCurGroupID(groupName);

                        double totalSpent = 0;
                        for (DataSnapshot expenseSnapshot :
                                groupSnapshot.child("expenses").getChildren()) {
                            double amount = expenseSnapshot.child("amount").getValue(Double.class);
                            totalSpent += amount;
                        }
                        totalSpentAmountTextView.setText("$" + String.valueOf(totalSpent));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d("LogCat - MainActivity", "updateGroupNameAndTotalSpent(): " +
                                "groupRef: onCancelled");
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("LogCat - MainActivity", "updateGroupNameAndTotalSpent(): userRef: " +
                        "onCancelled");
            }
        });
    }


    private void setListeners() {
        userAccountBtn.setOnClickListener(view -> startActivity(new Intent(MainActivity.this,
                UserAccountActivity.class)));

        shareGroupNumBtn.setOnClickListener(view -> copyGroupIdAndShowToast());

        //todo switchGroupBtn.setOnClickListener(view -> startActivity(new Intent(MainActivity
        // .this, GroupListActivity.class)));

        addGroupBtn.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, CreateOrJoinGroupActivity.class);
            intent.putExtra("userEmail", userEmail);
            startActivity(intent);
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.home) {
                Log.d("LogCat - MainActivity", "Menu Clicked");
            } else if (itemId == R.id.members) {
                Log.d("LogCat - MainActivity", "Members Clicked");
                Intent intent = new Intent(MainActivity.this, MemberActivity.class);
                intent.putExtra("curGroupID", curGroupID);
                intent.putExtra("userEmail", userEmail);
                startActivity(intent);
            } else if (itemId == R.id.expenses) {
                Log.d("LogCat - MainActivity", "Expenses Clicked");
                Intent intent = new Intent(MainActivity.this, ExpenseActivity.class);
                intent.putExtra("curGroupID", curGroupID);
                intent.putExtra("userEmail", userEmail);
                startActivity(intent);
            } else if (itemId == R.id.user) {
                Log.d("LogCat - MainActivity", "Transactions Clicked");
                Intent intent = new Intent(MainActivity.this, TransactionActivity.class);
                intent.putExtra("curGroupID", curGroupID);
                intent.putExtra("userEmail", userEmail);
                startActivity(intent);
            }
            return false;
        });
    }

    private void copyGroupIdAndShowToast() {
        // Logic to copy current group ID and show toast
    }

//    private void getLoggedInFirebaseUserEmail() {
//        Log.d("LogCat - MainActivity", "getLoggedInFirebaseUserEmail(): called");
//        // Get the logged in user's email from Firebase Auth
//        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
//        firebaseAuthEmail = firebaseAuth.getCurrentUser().getEmail();
//        setUserID(userEmailAsID(firebaseAuthEmail));
//        Log.d("LogCat - MainActivity", "    The current FIREBASE User is " + firebaseAuthEmail);
//    }
//
//    /**
//     * Helper method to generate a group.
//     */
//    private void promptUserToJoinOrCreateGroup(String userID) {
//        Log.d("LogCat - MainActivity", "promptUserToJoinOrCreateGroup(): called\n" +
//                "   Prompting user to join or create a group");
//        Intent intent = new Intent(MainActivity.this, CreateOrJoinGroupActivity.class);
//        intent.putExtra("userID", userID);
//        startActivity(intent);
//    }
//
//    public void setCurGroupID(String curGroupID) {
//        this.curGroupID = curGroupID;
//        Log.d("LogCat - MainActivity", "setCurGroupID(): setted as " + curGroupID + "\n");
//    }
//
//    private void fetchAndUpdateGroupName(String groupID) {
//        String userIDFormatted = userEmailAsID(firebaseAuthEmail);
//        Log.d("LogCat - MainActivity", "fetchAndUpdateGroupName is called:" + groupID);
//        DatabaseReference userGroupRef =
//                FirebaseDatabase.getInstance().getReference("users").child(userIDFormatted)
//                .child("groups").child(groupID);
//
//        userGroupRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()) {
//                    String groupName = dataSnapshot.child("groupName").getValue(String.class);
//                    updateGroupNameTextView(groupName);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }
//
//    /**
//     * Checks if the current user exists in the database, if not, creates a new user node
//     */
//    private void checkCurrentUserExists() {
//        Log.d("LogCat - MainActivity", "checkCurrentUser(): called\n" +
//                "   Checking current user");
//
//        // Check if firebaseAuthEmail exists in the database
//        allUsersRef.child(userEmailAsID(firebaseAuthEmail))
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        if (!dataSnapshot.exists()) {
//                            // User node does not exist, create a new user node
//                            Log.d("LogCat - MainActivity",
//                                    "       Current user is not in the database");
//                            createNewUserInDatabase(userID);
//                            checkNumberOfGroupsForUser(userID);
//                        }
//                        if (dataSnapshot.exists()) {
//                            Log.d("LogCat - MainActivity",
//                                    "       Current user already in the database\n");
//                            // todo check current user's number of groups
//                            checkNumberOfGroupsForUser(userID);
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//                        Log.d("LogCat - MainActivity",
//                                "onCancelled: " + databaseError.getMessage());
//                        makeAToast("Error: " + databaseError.getMessage());
//                    }
//                });
//    }
//
//    public void setUserID(String userID) {
//        this.userID = userID;
//        Log.d("LogCat - MainActivity", "setUserID(): setted as " + userID + "\n");
//    }
//
//    @NonNull
//    private String userEmailAsID(String email) {
//        return email.replace(".", ",");
//    }
//
//    private void updateGroupNameTextView(String groupName) {
//        runOnUiThread(() -> groupNameTextView.setText(groupName));
//    }
//
//    private void createNewUserInDatabase(String email) {
//        Log.d("LogCat - MainActivity", "createNewUser(): called");
//
//        // Create a new User object
//        User newUser = new User(email, new HashMap<>(), "None");
//
//        // Add the User to the Firebase Database
//        allUsersRef.child(userEmailAsID(email)).setValue(newUser).addOnSuccessListener(
//                        aVoid -> Log.d("LogCat - MainActivity", "   createNewUser: User created
//                        " +
//                                "successfully"))
//                .addOnFailureListener(e -> Log.d("LogCat - MainActivity", "   createNewUser:
//                User" +
//                        " creation failed"));
//    }
//
//    private void checkNumberOfGroupsForUser(String userEmail) {
//        Log.d("LogCat - MainActivity", "checkNumberOfGroupsForUser(): called");
//
//        userAllGroupsRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.exists()) {
//                    // Get the number of groups
//                    int numGroups = (int) snapshot.getChildrenCount();
//                    Log.d("LogCat - MainActivity",
//                            "   " + userEmail + "'s number of groups: " + numGroups);
//                    if (numGroups == 1) {
//                        Log.d("LogCat - MainActivity", "   " + userEmail + " has 1 group\n");
//                        // todo show that group
//                        //  setHomeText()
//                        //  need groupName, groupID, MoneySpent
//                    } else if (numGroups > 1) {
//                        // todo show lastInteractedGroup
//                        Log.d("LogCat - MainActivity", "   " + userEmail + " has more than 1 " +
//                                "group\n");
//
//                    }
//                }
//                // if the current user doesn't have any groups, create one
//                else {
//                    Log.d("LogCat - MainActivity", "   " + userEmail + " doesn't have any " +
//                            "groups\n");
//                    promptUserToJoinOrCreateGroup(userEmail);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }
//
//    /**
//     * Helper method to make a toast.
//     */
//    private void makeAToast(String message) {
//        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        fetchAndUpdateGroupName(curGroupID);
//    }

}