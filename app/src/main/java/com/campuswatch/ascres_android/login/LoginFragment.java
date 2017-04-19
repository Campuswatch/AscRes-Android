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


public class LoginFragment extends Fragment implements View.OnClickListener {

    @BindView(R.id.email_et) EditText emailField;

    @BindView(R.id.password_et) EditText passwordField;

    @BindView(R.id.login_button) Button loginButton;

    private OnLoginListener mListener;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        loginButton.setOnClickListener(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnLoginListener) {
            mListener = (OnLoginListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnLoginListener");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.login_button) {
            if (mListener != null) {
                login(emailField.getText().toString(), passwordField.getText().toString());
            }
        }
    }

    private void login(String email, String password) {
        if(!validateForm()) {
            return;
        }

        emailField.setText(null);
        passwordField.setText(null);

        mListener.onLoginClicked(email, password);

    }

    private boolean validateForm() {
        boolean valid = true;

        String email = emailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            emailField.setError("Required.");
            valid = false;
        } else {
            emailField.setError(null);
        }

        String password = passwordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            passwordField.setError("Required.");
            valid = false;
        } else {
            passwordField.setError(null);
        }

        return valid;
    }

    interface OnLoginListener {
        void onLoginClicked(String email, String password);
    }
}
