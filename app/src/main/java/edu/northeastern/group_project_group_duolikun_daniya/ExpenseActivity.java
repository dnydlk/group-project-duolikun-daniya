package edu.northeastern.group_project_group_duolikun_daniya;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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

import edu.northeastern.group_project_group_duolikun_daniya.expense_recycle_view.ExpenseItem;
import edu.northeastern.group_project_group_duolikun_daniya.expense_recycle_view.ExpensesAdapter;
import edu.northeastern.group_project_group_duolikun_daniya.member_recycle_view.MemberItem;

public class ExpenseActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ExpensesAdapter adapter;
    List<ExpenseItem> expenseList;
    private String curGroupID, userEmail, selectedSpinnerString, newSelectedSpinnerString;
    private DatabaseReference expensesRef;
    private FloatingActionButton addExpenseBtn;
    private MemberItem lastAddedMemberItem;
    private int lastAddedPosition = -1;
    private TextView addExpenseDialogTextView;
    private BottomNavigationView bottomNavigationView;
    private List<String> memberList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense);
        Log.d("LogCat - ExpenseActivity", "onCreate() called");

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
        fetchExpenses(curGroupID);

    }

    private void setUpRecyclerViewAndAdapter() {
        recyclerView = findViewById(R.id.expense_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        expenseList = new ArrayList<>();
        adapter = new ExpensesAdapter(expenseList);
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
                            String memberNameToDelete = expenseList.get(position).getWhat();
                            deleteMemberFromDB(memberNameToDelete);
                        } else if (direction == ItemTouchHelper.RIGHT) {
                            // Swipe right to edit
                            editMemberDialog(position);
                        }
                    }
                });
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void editMemberDialog(int memberPositionINDatabase) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_expense_input);
        View dialogView = dialog.getWindow().getDecorView();

        adjustDiaLogSize(dialog);

        addExpenseDialogTextView = dialogView.findViewById(R.id.add_expense_text_view);
        addExpenseDialogTextView.setText("Edit expense");
        EditText whatEditText = dialogView.findViewById(R.id.add_expense_what_edit_text);
        EditText houMuchEditText = dialogView.findViewById(R.id.add_expense_how_much_edit_text);


        // Fetch the current member name from the database and set it in the EditText
        expensesRef.child(String.valueOf(memberPositionINDatabase)).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    // Set the fetched member name to EditText if successful
                    ExpenseItem expenseItemInDatabase = task.getResult().getValue(ExpenseItem.class);
                    whatEditText.setText(expenseItemInDatabase.getWhat());
                    houMuchEditText.setText(expenseItemInDatabase.getHowMuch());
                } else {
                    Log.d("LogCat - MembersFragment", "Failed to fetch member data.");
                    makeAToast("Failed to fetch member data.");
                    dialog.dismiss();
                }
            }
        });

        Spinner whoSpinner = dialogView.findViewById(R.id.who_planets_spinner);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, memberList);
        whoSpinner.setAdapter(spinnerAdapter);
        whoSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Your code here
                newSelectedSpinnerString = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another callback
            }
        });

        Button saveLinkButton = dialogView.findViewById(R.id.save_expense_button);
        saveLinkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("LogCat - MembersFragment", "saveLinkButton clicked");
                String newWhat = whatEditText.getText().toString();
                String newHowMuch = houMuchEditText.getText().toString();
                newSelectedSpinnerString = whoSpinner.getSelectedItem().toString();

                if (!newWhat.isEmpty() && !newHowMuch.isEmpty() && !newSelectedSpinnerString.isEmpty()) {
                    // Update the expenses list in the database
                    ExpenseItem newExpenseItem = new ExpenseItem(newWhat, newHowMuch,
                            newSelectedSpinnerString);
                    expensesRef.child(String.valueOf(memberPositionINDatabase)).setValue(newExpenseItem).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Update expense list and notify the adapter
                            expenseList.set(memberPositionINDatabase, newExpenseItem);
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
        addExpenseBtn = findViewById(R.id.floating_action_add_expense_btn);
        bottomNavigationView = findViewById(R.id.expense_bottom_navigation);
    }

    private void setUpListeners() {
        addExpenseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("LogCat - MemberActivity", "addMemberBtn clicked");
                addExpenseDialog();

            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.home) {
                Log.d("LogCat - MainActivity", "Menu Clicked");
                Intent intent = new Intent(ExpenseActivity.this, MainActivity.class);
                intent.putExtra("curGroupID", curGroupID);
                intent.putExtra("userEmail", userEmail);
                startActivity(intent);
            } else if (itemId == R.id.members) {
                Log.d("LogCat - MainActivity", "Members Clicked");
                Intent intent = new Intent(ExpenseActivity.this, MemberActivity.class);
                intent.putExtra("curGroupID", curGroupID);
                intent.putExtra("userEmail", userEmail);
                startActivity(intent);
            } else if (itemId == R.id.expenses) {
                Log.d("LogCat - MainActivity", "Expenses Clicked");
            } else if (itemId == R.id.user) {
                Log.d("LogCat - MainActivity", "Transactions Clicked");
                Intent intent = new Intent(ExpenseActivity.this, UserAccountActivity.class);
                intent.putExtra("curGroupID", curGroupID);
                intent.putExtra("userEmail", userEmail);
                startActivity(intent);
            }
            return false;
        });
    }

    private void addExpenseDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_expense_input);
        View dialogView = dialog.getWindow().getDecorView();

        adjustDiaLogSize(dialog);

        addExpenseDialogTextView = dialogView.findViewById(R.id.add_expense_text_view);
        addExpenseDialogTextView.setText("Add a new expense");
        EditText whatEditText = dialogView.findViewById(R.id.add_expense_what_edit_text);
        EditText houMuchEditText = dialogView.findViewById(R.id.add_expense_how_much_edit_text);
        Button saveLinkButton = dialogView.findViewById(R.id.save_expense_button);
        Spinner whoSpinner = dialogView.findViewById(R.id.who_planets_spinner);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, memberList);
        whoSpinner.setAdapter(spinnerAdapter);
        whoSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedSpinnerString = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        saveLinkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("LogCat - MemberActivity", "saveLinkButton clicked");

                String what = whatEditText.getText().toString();

                String howMuch = houMuchEditText.getText().toString();

                if (!what.isEmpty() && !howMuch.isEmpty() && !selectedSpinnerString.isEmpty()) {
                    ExpenseItem newExpenseItem = new ExpenseItem(what, howMuch,
                            selectedSpinnerString);
                    expenseList.add(newExpenseItem);
                    lastAddedMemberItem = new MemberItem(what);
                    lastAddedPosition = expenseList.size() - 1;
                    adapter.notifyItemInserted(lastAddedPosition);
                    // Add the new member to the database
                    AddNewExpenseToDB(newExpenseItem);
                    dialog.dismiss();
                    showSnackbar("New Expense Added");
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
                    String whatToDelete = expenseList.get(lastAddedPosition).getWhat();
                    // Delete from memberList
                    expenseList.remove(lastAddedPosition);
                    // Delete from database
                    deleteMemberFromDB(whatToDelete);
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

    private void deleteMemberFromDB(String what) {
        Log.d("LogCat - MemberActivity", "deleteMemberFromDB() called");

        expensesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot expenseSnapshot : snapshot.getChildren()) {
                    // Check if the expense's name matches the one we want to delete
                    //todo
                    if (expenseSnapshot.getValue(ExpenseItem.class).getWhat().equals(what)) {
                        // Get the key of the node that has the matching name
                        String keyToDelete = expenseSnapshot.getKey();
                        // Remove the node using the key
                        expensesRef.child(keyToDelete).removeValue().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Log.d("LogCat - MemberActivity", "Member deleted successfully");
                                // Perform any additional actions needed after successful deletion
                                makeAToast(what + " deleted");
                            } else {
                                Log.e("LogCat - MemberActivity", "Failed to delete member",
                                        task.getException());
                                makeAToast("Failed to delete " + what);
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

    private void AddNewExpenseToDB(ExpenseItem newExpenseItem) {
        Log.d("LogCat - ExpenseActivity", "AddNewExpenseToDB() called");
        ExpenseItem expenseItem = new ExpenseItem(newExpenseItem.getWhat(),
                newExpenseItem.getHowMuch(), newExpenseItem.getWho());
        expensesRef.child(String.valueOf(lastAddedPosition)).setValue(newExpenseItem).addOnCompleteListener(new OnCompleteListener<Void>() {
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

    private void fetchExpenses(String groupToShowID) {
        Log.d("LogCat - ExpensesActivity", "fetchExpenses() called");

        // get the memberList
        DatabaseReference membersRef = FirebaseDatabase.getInstance().getReference("users")
                .child(userEmail).child("groups").child(groupToShowID).child(
                        "members");
        membersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    memberList = new ArrayList<>();
                    for (DataSnapshot memberSnapshot : snapshot.getChildren()) {
                        String member = memberSnapshot.getValue(String.class);
                        memberList.add(member);
                    }

                    Log.d("LogCat - ExpensesActivity", "iterating through memberList");
                    for (String member : memberList) {
                        Log.d("LogCat - ExpensesActivity", "member: " + member);
                    }
                } else {
                    Log.d("LogCat - ExpensesActivity", "snapshot does not exist");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("LogCat - ExpensesActivity", "onCancelled() called");
            }
        });

        // get the expenseList
        expensesRef = FirebaseDatabase.getInstance().getReference("users")
                .child(userEmail).child("groups").child(groupToShowID).child(
                        "expenses");

        expensesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    expenseList = new ArrayList<>();
                    for (DataSnapshot expenseSnapshot : snapshot.getChildren()) {
                        //String expense = expenseSnapshot.getValue(String.class);
                        ExpenseItem expenseItem = expenseSnapshot.getValue(ExpenseItem.class);
                        expenseList.add(expenseItem);
                    }

                    Log.d("LogCat - MembersActivity", "iterating through memberList");
                    for (ExpenseItem item : expenseList) {
                        Log.d("LogCat - MembersActivity", "member: " + item.toString());
                    }
                    adapter.updateData(expenseList);
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