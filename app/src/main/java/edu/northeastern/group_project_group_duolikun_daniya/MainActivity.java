package edu.northeastern.group_project_group_duolikun_daniya;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

    private String userEmail, curGroupID;
    private TextView groupNameTextView;
    private TextView totalSpentAmountTextView;
    private ImageView shareGroupNumBtn, addGroupBtn;
    private BottomNavigationView bottomNavigationView;

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
        shareGroupNumBtn = findViewById(R.id.share_group_num_btn);
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
        shareGroupNumBtn.setOnClickListener(view -> copyGroupIdAndShowToast());

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
                Intent intent = new Intent(MainActivity.this, UserAccountActivity.class);
                intent.putExtra("curGroupID", curGroupID);
                intent.putExtra("userEmail", userEmail);
                startActivity(intent);
            }
            return false;
        });
    }

    public void setCurGroupID(String curGroupID) {
        this.curGroupID = curGroupID;
    }

    private void copyGroupIdAndShowToast() {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Group ID", curGroupID);
        clipboard.setPrimaryClip(clip);
        makeAToast("Group ID copied to clipboard, share it with freinds!");
    }

    /**
     * Helper method to make a toast.
     */
    private void makeAToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}