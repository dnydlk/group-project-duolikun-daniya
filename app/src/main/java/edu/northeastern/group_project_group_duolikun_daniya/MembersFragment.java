package edu.northeastern.group_project_group_duolikun_daniya;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MembersFragment extends Fragment {

    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d("LogCat - MembersFragment", "onCreateView");
        view = inflater.inflate(R.layout.fragment_members, container, false);
        return view;
    }
}