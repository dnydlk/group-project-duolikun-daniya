package edu.northeastern.group_project_group_duolikun_daniya;

import android.app.Dialog;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import edu.northeastern.group_project_group_duolikun_daniya.RecycleView.Member;
import edu.northeastern.group_project_group_duolikun_daniya.RecycleView.MembersAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MembersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MembersFragment extends Fragment {
    private static final String NODE_USERS = "users";
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private View view;
    private MainActivity mainActivity;
    private String groupToShowID;
    private List<String> membersList;
    private String mParam1;
    private String mParam2;
    private MembersAdapter membersAdapter;
    private RecyclerView membersRecyclerView;
    private FloatingActionButton addMemberBtn;
    private Member lastAddedMember;
    private int lastAddedPosition = -1;
    private DatabaseReference membersRef;
    private TextView addMemberDialogTextView;

    public MembersFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MemberFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MembersFragment newInstance(String param1, String param2) {
        MembersFragment fragment = new MembersFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        Log.d("LogCat - MembersFragment", "onCreate() called");

        // Initialize members list and adapter
        membersList = new ArrayList<>();
        membersAdapter = new MembersAdapter(membersList);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("LogCat - MembersFragment", "onCreateView() called");

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_members, container, false);
        addMemberBtn = view.findViewById(R.id.floating_action_btn);
        addMemberBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("LogCat - MembersFragment", "addMemberBtn clicked");
                showAddMemberDialog();

            }
        });

        mainActivity = (MainActivity) getActivity();
        Log.d("LogCat - MembersFragment", "Got mainActivity");

        // Set up the RecyclerView
        setUpRecyclerView(view);

        // Get lastInteractedGroupRef and get members list
        getGroupIdOfLastIntGroup();


        return view;
    }

    private void showAddMemberDialog() {
        final Dialog dialog = new Dialog(getContext());
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
                Log.d("LogCat - MembersFragment", "saveLinkButton clicked");
                String memberName = memberNameEditText.getText().toString();

                if (!memberName.isEmpty()) {
                    Member newMember = new Member(memberName);
                    membersList.add(newMember.getName());
                    lastAddedMember = newMember;
                    lastAddedPosition = membersList.size() - 1;
                    membersAdapter.notifyItemInserted(lastAddedPosition);
                    // Add the new member to the database
                    AddNewMemberToDB(newMember);
                    dialog.dismiss();
                    showSnackbar("New Member Added");
                } else {
                    makeAToast("Please enter member name");
                }
            }
        });
        dialog.show();
    }

    private void showEditMemberDialog(int memberPositionINDatabase) {
        final Dialog dialog = new Dialog(getContext());
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
                    MembersFragment.this.makeAToast("Failed to fetch member data.");
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
                            membersList.set(memberPositionINDatabase, memberName);
                            membersAdapter.notifyItemChanged(memberPositionINDatabase);
                            dialog.dismiss();
                            MembersFragment.this.showSnackbar("Member updated");
                        } else {
                            MembersFragment.this.makeAToast("Failed to update member");
                        }
                    });
                } else {
                    MembersFragment.this.makeAToast("Please enter member name");
                }
            }
        });

        dialog.show();
    }


    private void setUpRecyclerView(View view) {
        membersRecyclerView = view.findViewById(R.id.members_recycler_view);
        if (membersRecyclerView != null) {
            membersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            membersRecyclerView.setAdapter(membersAdapter);

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
                                String memberNameToDelete = membersList.get(position);
                                deleteMemberFromDB(memberNameToDelete);
                            } else if (direction == ItemTouchHelper.RIGHT) {
                                // Swipe right to edit
                                showEditMemberDialog(position);
                            }
                        }
                    });
            itemTouchHelper.attachToRecyclerView(membersRecyclerView);

        } else {
            Log.d("LogCat - MembersFragment", "RecyclerView not found. Check the layout file.");
        }
    }

    private void getGroupIdOfLastIntGroup() {
        getGroupIdOfLastIntGroup(new GroupIdCallback() {
            @Override
            public void onGroupIdReceived(String groupId) {
                if (groupId != null) {
                    Log.d("LogCat - MembersFragment", "groupId: " + groupId);
                    getMembersList(groupId);
                } else {
                    Log.d("LogCat - MembersFragment", "groupId is null");
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
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int displayWidth = displayMetrics.widthPixels;
            layoutParams.width = (int) (displayWidth * 0.95); // 80% of screen width
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

            dialogWindow.setAttributes(layoutParams);
        }
    }

    private void AddNewMemberToDB(Member newMember) {
        membersRef.child(String.valueOf(lastAddedPosition)).setValue(newMember.getName()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d("LogCat - MembersFragment", "saveLinkButton clicked:  Member added to " +
                            "database");
                } else {
                    Log.d("LogCat - MembersFragment", "saveLinkButton clicked:  Member failed to " +
                            "add to database");
                }

            }
        });
    }

    private void showSnackbar(String message) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);

        // Undo
        snackbar.setAction("UNDO", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (lastAddedMember != null && lastAddedPosition != -1) {
                    String memberNameToDelete = membersList.get(lastAddedPosition);
                    // Delete from memberList
                    membersList.remove(lastAddedPosition);
                    // Delete from database
                    deleteMemberFromDB(memberNameToDelete);
                    membersAdapter.notifyItemRemoved(lastAddedPosition);
                    lastAddedMember = null;
                    lastAddedPosition = -1;
                } else {
                    Log.d("LogCat - MembersFragment", "lastAddedMember is null");
                }
            }
        });
        snackbar.show();
    }

    /**
     * Helper method to make a toast.
     */
    private void makeAToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    private void getGroupIdOfLastIntGroup(GroupIdCallback callback) {
        String curUserId = mainActivity.getUserID();
        Log.d("LogCat - MembersFragment", "curUserId: " + curUserId);
        DatabaseReference lastInteractedGroupRef = FirebaseDatabase.getInstance().getReference(
                "users").child(curUserId).child("lastInteractedGroup");
        Log.d("LogCat - MembersFragment", "getGroupIdOfLastIntGroup() called");
        lastInteractedGroupRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Log.d("LogCat - MembersFragment", "snapshot exists");
                    groupToShowID = snapshot.getValue(String.class);
                    callback.onGroupIdReceived(groupToShowID);
                    Log.d("LogCat - MembersFragment", "groupToShowID: " + groupToShowID);
                } else {
                    Log.d("LogCat - MembersFragment", "snapshot does not exist");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("LogCat - MembersFragment", "getGroupIdOfLastIntGroup() cancelled");
                callback.onGroupIdReceived(null);
                makeAToast("Failed to get group ID" + error);
            }
        });
    }

    private void getMembersList(String groupToShowID) {
        Log.d("LogCat - MembersFragment", "getMembersList() called");
        membersRef = FirebaseDatabase.getInstance().getReference(NODE_USERS)
                .child(mainActivity.getUserID()).child("groups").child(groupToShowID).child(
                        "members");
        Log.d("LogCat - MembersFragment", "membersRef: " + membersRef);

        membersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    membersList = new ArrayList<>();
                    for (DataSnapshot memberSnapshot : snapshot.getChildren()) {
                        String member = memberSnapshot.getValue(String.class);
                        membersList.add(member);
                    }

                    Log.d("LogCat - MembersFragment", "iterating through membersList");
                    for (String member : membersList) {
                        Log.d("LogCat - MembersFragment", "member: " + member);
                    }

                    // Update RecyclerView
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                membersAdapter.updateData(membersList);
                            }
                        });
                    }


                } else {
                    Log.d("LogCat - MembersFragment", "snapshot does not exist");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle cancelled event
            }
        });
    }

    private void deleteMemberFromDB(String memberNameToDelete) {
        Log.d("LogCat - MembersFragment", "deleteMemberFromDB() called");

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
                                Log.d("LogCat - MembersFragment", "Member deleted successfully");
                                // Perform any additional actions needed after successful deletion
                                makeAToast(memberNameToDelete + " deleted");
                            } else {
                                Log.e("LogCat - MembersFragment", "Failed to delete member", task.getException());
                                makeAToast("Failed to delete " + memberNameToDelete);
                            }
                        });
                        break; // Break the loop after finding the match
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("LogCat - MembersFragment", "Failed to read members", error.toException());
            }
        });
    }


    public interface GroupIdCallback {
        void onGroupIdReceived(String groupId);
    }

}