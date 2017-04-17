package com.campuswatch.ascres_android.login;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.campuswatch.ascres_android.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserPhoneFragment extends Fragment implements View.OnClickListener {

    @BindView(R.id.phone_et) EditText phoneField;

    @BindView(R.id.phone_button) Button phoneButton;

    private OnPhoneCompletedListener mListener;

    public UserPhoneFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_user_phone, container, false);
        ButterKnife.bind(v);
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnPhoneCompletedListener) {
            mListener = (OnPhoneCompletedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnPhoneCompletedListener");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        phoneButton.setOnClickListener(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.phone_button) {
            setPhone(phoneField.getText().toString());
        }
    }

    private void setPhone(String phone) {
        if (!validateForm()) {
            return;
        }

        phoneField.setText(null);

        mListener.onPhoneUpdated(phone);
    }

    private boolean validateForm() {
        boolean valid = true;

        String phone = phoneField.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            phoneField.setError("Required");
            valid = false;
        } else if (!android.util.Patterns.PHONE.matcher(phone).matches()) {
            phoneField.setError("Invalid phone number");
            valid = false;
        } else phoneField.setError(null);

        return valid;
    }

    interface OnPhoneCompletedListener {
        void onPhoneUpdated(String phone);
    }
}
