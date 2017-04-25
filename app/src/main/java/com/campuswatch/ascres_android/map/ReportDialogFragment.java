package com.campuswatch.ascres_android.map;

import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.campuswatch.ascres_android.R;
import com.google.android.gms.maps.model.LatLng;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Thought of by samwyz for the most part on 4/13/17.
 */

public class ReportDialogFragment extends DialogFragment implements View.OnClickListener {
    //popped when make report button is pressed to indicate what type of incident is being reported

    private static final String LAT_LNG = "latlng";

    @BindView(R.id.report_text1)
    TextView text1;
    @BindView(R.id.report_text2)
    TextView text2;
    @BindView(R.id.report_text3)
    TextView text3;
    @BindView(R.id.report_text4)
    TextView text4;
    @BindView(R.id.report_text5)
    TextView text5;

    NoticeDialogListener mListener;
    LatLng latlng;

    public ReportDialogFragment() {

    }

    public static ReportDialogFragment newInstance(LatLng latLng) {
        ReportDialogFragment fragment = new ReportDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable(LAT_LNG, latLng);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            latlng = getArguments().getParcelable(LAT_LNG);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_report, container, false);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00FFFFFF")));

        ButterKnife.bind(this, view);

        text1.setOnClickListener(this);
        text2.setOnClickListener(this);
        text3.setOnClickListener(this);
        text4.setOnClickListener(this);
        text5.setOnClickListener(this);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setWindowAnimations(R.style.dialog_slide_animation);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof NoticeDialogListener) {
            mListener = (NoticeDialogListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnLoginListener");
        }
    }

    @Override
    public void onClick(View v) {
        mListener.onDialogClick(ReportDialogFragment.this,
                Integer.valueOf(String.valueOf(v.getTag())), latlng);
        dismiss();
    }

    interface NoticeDialogListener {
        void onDialogClick(DialogFragment dialog, int category, LatLng latlng);
    }
}
