package edu.northeastern.group_project_group_duolikun_daniya;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import edu.northeastern.group_project_group_duolikun_daniya.member_recycle_view.MemberItem;
import edu.northeastern.group_project_group_duolikun_daniya.member_recycle_view.MembersAdapter;

public class MemberActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    MembersAdapter adapter;
    List<String> memberList;
    private String curGroupID, userEmail;
    private DatabaseReference membersRef;
    private FloatingActionButton addMemberBtn;
    private MemberItem lastAddedMemberItem;
    private int lastAddedPosition = -1;
    private TextView addMemberDialogTextView;
    private BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member);
        Log.d("LogCat - MemberActivity", "onCreate() called");

        // Initialize RecyclerView and Adapter
        setUpRecyclerViewAndAdapter();

        // Initialize UI
        initializeUI();
        
        // Set up listeners
        setUpListeners();

        // Get curGroupID, userEmail from intent
        curGroupID = getIntent().getStringExtra("curGroupID");
        Log.d("LogCat - MemberActivity", "curGroupID: " + curGroupID);
        userEmail = getIntent().getStringExtra("userEmail");
        Log.d("LogCat - MemberActivity", "userEmail: " + userEmail);

        // Fetch members from Firebase
        fetchMembers(curGroupID);
    }

    private void setUpRecyclerViewAndAdapter() {
        recyclerView = findViewById(R.id.members_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        memberList = new ArrayList<>();
        adapter = new MembersAdapter(memberList);
        recyclerView.setAdapter(adapter);

        // Swipe to delete and edit
        ItemTouchHelper itemTouchHelper =
                new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                        ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                    @Override
                    public boolean onMove(@NonNull RecyclerView recyclerView,
                                          @NonNull RecyclerView.ViewHolder viewHolder,
                                          @NonNull RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder,
                                         int direction) {
                        int position = viewHolder.getAdapterPosition();
                        if (direction == ItemTouchHelper.LEFT) {
                            // Swipe left to delete
                            String memberNameToDelete = memberList.get(position);
                            deleteMemberFromDB(memberNameToDelete);
                        } else if (direction == ItemTouchHelper.RIGHT) {
                            // Swipe right to edit
                            showEditMemberDialog(position);
                        }
                    }
                });
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }
    private void showEditMemberDialog(int memberPositionINDatabase) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_member_input);
        View dialogView = dialog.getWindow().getDecorView();

        adjustDiaLogSize(dialog);

        TextView addMemberDialogTextView = dialogView.findViewById(R.id.add_member_text_view);
        addMemberDialogTextView.setText("Edit member name");
        EditText memberNameEditText = dialogView.findViewById(R.id.add_member_edit_text);

        // Fetch the current member name from the database and set it in the EditText
        membersRef.child(String.valueOf(memberPositionINDatabase)).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    // Set the fetched member name to EditText if successful
                    String memberNameInDatabase = String.valueOf(task.getResult().getValue());
                    memberNameEditText.setText(memberNameInDatabase);
                } else {
                    Log.d("LogCat - MembersFragment", "Failed to fetch member data.");
                    makeAToast("Failed to fetch member data.");
                    dialog.dismiss();
                }
            }
        });

        Button saveLinkButton = dialogView.findViewById(R.id.save_member_button);
        saveLinkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("LogCat - MembersFragment", "saveLinkButton clicked");
                String memberName = memberNameEditText.getText().toString();

                if (!memberName.isEmpty()) {
                    // Update the member's name in the database
                    membersRef.child(String.valueOf(memberPositionINDatabase)).setValue(memberName).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Update members list and notify the adapter
                            memberList.set(memberPositionINDatabase, memberName);
                            adapter.notifyItemChanged(memberPositionINDatabase);
                            dialog.dismiss();
                            showSnackbar("Member updated");
                        } else {
                            makeAToast("Failed to update member");
                        }
                    });
                } else {
                    makeAToast("Please enter member name");
                }
            }
        });

        dialog.show();
    }

    private void initializeUI() {
        addMemberBtn = findViewById(R.id.floating_action_add_member_btn);
        bottomNavigationView = findViewById(R.id.member_bottom_navigation);
    }
    
    private void setUpListeners() {
        addMemberBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("LogCat - MemberActivity", "addMemberBtn clicked");
                showAddMemberDialog();

            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.home) {
                Log.d("LogCat - MainActivity", "Menu Clicked");
                Intent intent = new Intent(MemberActivity.this, MainActivity.class);
                intent.putExtra("curGroupID", curGroupID);
                intent.putExtra("userEmail", userEmail);
                startActivity(intent);
            } else if (itemId == R.id.members) {
                Log.d("LogCat - MainActivity", "Members Clicked");
            } else if (itemId == R.id.expenses) {
                Log.d("LogCat - MainActivity", "Expenses Clicked");
                Intent intent = new Intent(MemberActivity.this, ExpenseActivity.class);
                intent.putExtra("curGroupID", curGroupID);
                intent.putExtra("userEmail", userEmail);
                startActivity(intent);
            } else if (itemId == R.id.user) {
                Log.d("LogCat - MainActivity", "Transactions Clicked");
                Intent intent = new Intent(MemberActivity.this, TransactionActivity.class);
                intent.putExtra("curGroupID", curGroupID);
                intent.putExtra("userEmail", userEmail);
                startActivity(intent);
            }
            return false;
        });
    }

    private void showAddMemberDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_member_input);
        View dialogView = dialog.getWindow().getDecorView();

        adjustDiaLogSize(dialog);

        addMemberDialogTextView = dialogView.findViewById(R.id.add_member_text_view);
        addMemberDialogTextView.setText("Add a new member to your group");
        EditText memberNameEditText = dialogView.findViewById(R.id.add_member_edit_text);
        Button saveLinkButton = dialogView.findViewById(R.id.save_member_button);

        saveLinkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("LogCat - MemberActivity", "saveLinkButton clicked");
                String memberName = memberNameEditText.getText().toString();

                if (!memberName.isEmpty()) {
                    MemberItem newMemberItem = new MemberItem(memberName);
                    memberList.add(newMemberItem.getName());
                    lastAddedMemberItem = newMemberItem;
                    lastAddedPosition = memberList.size() - 1;
                    adapter.notifyItemInserted(lastAddedPosition);
                    // Add the new member to the database
                    AddNewMemberToDB(newMemberItem);
                    dialog.dismiss();
                    showSnackbar("New Member Added");
                } else {
                    makeAToast("Please enter member name");
                }
            }
        });
        dialog.show();
    }

    private void showSnackbar(String message) {
        Snackbar snackbar = Snackbar.make(
                findViewById(android.R.id.content),
                message,
                Snackbar.LENGTH_SHORT
        );

        // Undo
        snackbar.setAction("UNDO", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (lastAddedMemberItem != null && lastAddedPosition != -1) {
                    String memberNameToDelete = memberList.get(lastAddedPosition);
                    // Delete from memberList
                    memberList.remove(lastAddedPosition);
                    // Delete from database
                    deleteMemberFromDB(memberNameToDelete);
                    adapter.notifyItemRemoved(lastAddedPosition);
                    lastAddedMemberItem = null;
                    lastAddedPosition = -1;
                } else {
                    Log.d("LogCat - MemberActivity", "lastAddedMember is null");
                }
            }
        });
        snackbar.show();
    }

    private void deleteMemberFromDB(String memberNameToDelete) {
        Log.d("LogCat - MemberActivity", "deleteMemberFromDB() called");

        membersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot memberSnapshot : snapshot.getChildren()) {
                    // Check if the member's name matches the one we want to delete
                    if (memberSnapshot.getValue(String.class).equals(memberNameToDelete)) {
                        // Get the key of the node that has the matching name
                        String keyToDelete = memberSnapshot.getKey();
                        // Remove the node using the key
                        membersRef.child(keyToDelete).removeValue().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Log.d("LogCat - MemberActivity", "Member deleted successfully");
                                // Perform any additional actions needed after successful deletion
                                makeAToast(memberNameToDelete + " deleted");
                            } else {
                                Log.e("LogCat - MemberActivity", "Failed to delete member",
                                        task.getException());
                                makeAToast("Failed to delete " + memberNameToDelete);
                            }
                        });
                        break; // Break the loop after finding the match
                    }
                }
            }



            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("LogCat - MemberActivity", "Failed to read members", error.toException());
            }
        });
    }

    /**
     * Helper method to make a toast.
     */
    private void makeAToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void AddNewMemberToDB(MemberItem newMemberItem) {
        membersRef.child(String.valueOf(lastAddedPosition)).setValue(newMemberItem.getName()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d("LogCat - MemberActivity", "saveLinkButton clicked:  Member added to " +
                            "database");
                } else {
                    Log.d("LogCat - MemberActivity", "saveLinkButton clicked:  Member failed to " +
                            "add to database");
                }

            }
        });
    }

    private void adjustDiaLogSize(Dialog dialog) {
        // Adjusting the size of the dialog
        Window dialogWindow = dialog.getWindow();
        if (dialogWindow != null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(dialogWindow.getAttributes());

            // Set the width and height of the dialog
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int displayWidth = displayMetrics.widthPixels;
            layoutParams.width = (int) (displayWidth * 0.95); // 80% of screen width
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

            dialogWindow.setAttributes(layoutParams);
        }
    }

    private void fetchMembers(String groupToShowID) {
        Log.d("LogCat - MembersActivity", "getMemberList() called");
        membersRef = FirebaseDatabase.getInstance().getReference("users")
                .child(userEmail).child("groups").child(groupToShowID).child(
                        "members");
        Log.d("LogCat - MembersActivity", "membersRef: " + membersRef.getPath().toString());

        membersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    memberList = new ArrayList<>();
                    for (DataSnapshot memberSnapshot : snapshot.getChildren()) {
                        String member = memberSnapshot.getValue(String.class);
                        memberList.add(member);
                    }

                    Log.d("LogCat - MembersActivity", "iterating through memberList");
                    for (String member : memberList) {
                        Log.d("LogCat - MembersActivity", "member: " + member);
                    }
                    adapter.updateData(memberList);
                } else {
                    Log.d("LogCat - MembersActivity", "snapshot does not exist");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("LogCat - MembersActivity", "onCancelled() called");
            }
        });
    }


}