package com.campuswatch.ascres_android.login;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.campuswatch.ascres_android.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class TutorialFragment extends Fragment {

    private static final String IMAGE = "image";

    @BindView(R.id.fragment_image)
    ImageView tutorialImage;

    private int mImage;

    public TutorialFragment() {
        // Required empty public constructor
    }

    public static TutorialFragment newInstance(int image) {
        Bundle args = new Bundle();
        args.putInt(IMAGE, image);
        TutorialFragment fragment = new TutorialFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mImage = getArguments().getInt(IMAGE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tutorial, container, false);
        ButterKnife.bind(this, v);
        tutorialImage.setImageDrawable(ContextCompat.getDrawable(getActivity(), mImage));
        return v;
    }

}
