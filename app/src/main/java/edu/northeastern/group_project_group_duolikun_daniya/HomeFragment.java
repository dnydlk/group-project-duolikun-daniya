package edu.northeastern.group_project_group_duolikun_daniya;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

public class HomeFragment extends Fragment {
    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.i("HomeFragment", "onCreateView");
        view = inflater.inflate(R.layout.fragment_home, container, false);
        return view;
    }
}