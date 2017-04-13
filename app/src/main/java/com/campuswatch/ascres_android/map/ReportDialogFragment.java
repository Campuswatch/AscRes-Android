package com.campuswatch.ascres_android.map;

import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.campuswatch.ascres_android.R;
import com.google.android.gms.maps.model.LatLng;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Thought of by samwyz for the most part on 4/13/17.
 */

public class ReportDialogFragment extends DialogFragment {
    //popped when make report button is pressed to indicate what type of incident is being reported

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
    @BindView(R.id.report_text6)
    TextView text6;

    NoticeDialogListener mListener;
    LatLng latlng;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        latlng = getArguments().getParcelable("location");
        View view = inflater.inflate(R.layout.fragment_report, container, false);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00FFFFFF")));
        WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
        params.gravity = Gravity.TOP;
        getDialog().getWindow().setAttributes(params);

        ButterKnife.bind(this, view);

        text1.setOnClickListener(reportListener);
        text2.setOnClickListener(reportListener);
        text3.setOnClickListener(reportListener);
        text4.setOnClickListener(reportListener);
        text5.setOnClickListener(reportListener);

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
        mListener = (NoticeDialogListener) context;
    }

    View.OnClickListener reportListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mListener.onDialogClick(ReportDialogFragment.this, Integer.valueOf(String.valueOf(v.getTag())), latlng);
            dismiss();
        }
    };

    public interface NoticeDialogListener {
        void onDialogClick(DialogFragment dialog, int category, LatLng latlng);
    }
}
