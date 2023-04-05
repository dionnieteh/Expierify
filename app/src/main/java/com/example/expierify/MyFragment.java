package com.example.expierify;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class MyFragment extends Fragment {

    private String fragmentTagToShow;

    public MyFragment() {
        // Required empty public constructor
    }

    public static MyFragment newInstance() {
        MyFragment fragment = new MyFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // Get the fragment tag to show from the arguments
            fragmentTagToShow = getArguments().getString("fragmentTagToShow");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sort, container, false);
    }

    public void switchFragments(String fragmentTagToShow) {
        FragmentManager fragmentManager = getParentFragmentManager();
        Fragment fragmentToShow = fragmentManager.findFragmentByTag(fragmentTagToShow);

        for (Fragment fragment : fragmentManager.getFragments()) {
            if (fragment != null && fragment.isVisible()) {
                fragmentManager.beginTransaction()
                        .hide(fragment)
                        .commit();
            }
        }

        if (fragmentToShow != null) {
            fragmentManager.beginTransaction()
                    .show(fragmentToShow)
                    .commit();
        }
    }


}
