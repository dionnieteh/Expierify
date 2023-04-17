package com.example.expierify;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class SortFragment extends Fragment {

    private boolean isCategoryFragmentVisible = true;
    private boolean isLabelFragmentVisible = false;

    public SortFragment() {
        // Required empty public constructor
    }

    public static SortFragment newInstance(String param1, String param2) {
        SortFragment fragment = new SortFragment();
        Bundle args = new Bundle();
        // set arguments here if needed
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sort, container, false);


        // Show CategoryFragment by default
        FragmentManager fragmentManager = getChildFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView6, CategoryFragment.class, null)
                .setReorderingAllowed(true)
                .commit();

        // Hide LabelFragment container
        view.findViewById(R.id.fragmentContainerView7).setVisibility(View.GONE);

        Button labelBtn = view.findViewById(R.id.labelBtn);
        Button categBtn = view.findViewById(R.id.categBtn);
        categBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                labelBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.lightgreen)));
                categBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.darkgreen)));
                FragmentManager fragmentManager = getChildFragmentManager();
                if (!isCategoryFragmentVisible) {
                    // Hide LabelFragment container
                    getActivity().findViewById(R.id.fragmentContainerView7).setVisibility(View.GONE);
                    // Show CategoryFragment
                    fragmentManager.beginTransaction()
                            .replace(R.id.fragmentContainerView6, CategoryFragment.class, null)
                            .setReorderingAllowed(true)
                            .commit();
                    getActivity().findViewById(R.id.fragmentContainerView6).setVisibility(View.VISIBLE);
                    isCategoryFragmentVisible = true;
                    isLabelFragmentVisible = false;
                }
            }
        });

        labelBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                categBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.lightgreen)));
                labelBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.darkgreen)));
                FragmentManager fragmentManager = getChildFragmentManager();
                if (!isLabelFragmentVisible) {
                    // Hide CategoryFragment container
                    getActivity().findViewById(R.id.fragmentContainerView6).setVisibility(View.GONE);
                    isCategoryFragmentVisible = false;
                    // Show LabelFragment
                    fragmentManager.beginTransaction()
                            .replace(R.id.fragmentContainerView7, LabelFragment.class, null)
                            .setReorderingAllowed(true)
                            .commit();
                    getActivity().findViewById(R.id.fragmentContainerView7).setVisibility(View.VISIBLE);
                    isLabelFragmentVisible = true;
                }
            }
        });

        return view;
    }
}