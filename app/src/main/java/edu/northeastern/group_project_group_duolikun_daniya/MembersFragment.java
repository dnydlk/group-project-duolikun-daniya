package edu.northeastern.group_project_group_duolikun_daniya;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

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

        mainActivity = (MainActivity) getActivity();
        Log.d("LogCat - MembersFragment", "Got mainActivity");

        // Set up the RecyclerView
        setUpRecyclerView(view);

        // Get lastInteractedGroupRef and get members list
        getGroupIdOfLastIntGroup();


        return view;
    }

    private void setUpRecyclerView(View view) {
        membersRecyclerView = view.findViewById(R.id.members_recycler_view);
        if (membersRecyclerView != null) {
            membersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            membersRecyclerView.setAdapter(membersAdapter);
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
        DatabaseReference membersRef = FirebaseDatabase.getInstance().getReference(NODE_USERS)
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

    /**
     * Helper method to make a toast.
     */
    private void makeAToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    public interface GroupIdCallback {
        void onGroupIdReceived(String groupId);
    }

}