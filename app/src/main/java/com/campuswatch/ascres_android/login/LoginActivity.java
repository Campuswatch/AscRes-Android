package com.campuswatch.ascres_android.login;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.campuswatch.ascres_android.R;
import com.campuswatch.ascres_android.UserRepository;
import com.campuswatch.ascres_android.models.User;
import com.campuswatch.ascres_android.root.App;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import javax.inject.Inject;

import static com.campuswatch.ascres_android.Constants.USER_DATA;

public class LoginActivity extends AppCompatActivity implements
        LoginFragment.OnLoginListener,
        UserPhoneFragment.OnPhoneCompletedListener {

    @Inject
    UserRepository mRepo;

    private DatabaseReference userRef;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private String mUid;
    private String mName;
    private String mEmail;
    private String mPhone;
    private String mImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ((App) getApplication()).getComponent().inject(this);
        userRef = FirebaseDatabase.getInstance().getReference("users");
        mAuth = FirebaseAuth.getInstance();

        startTransaction(new LoginFragment());
    }

    private void saveUser() {
        FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
        SharedPreferences prefs = getSharedPreferences(USER_DATA, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        User user = new User(
                fbUser.getEmail(),
                fbUser.getDisplayName(),
                fbUser.getUid(),
                // TODO// FIXME: 4/17/17
                null, null);
        userRef.child(user.getUid()).setValue(user);
        editor.putString(USER_DATA, user.serialize());
        editor.apply();
        mRepo.setUser(user);
    }

    @Override
    public void onLoginClicked(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            if (!task.isSuccessful()) {
                Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show();
                return;
            }

            mUser = mAuth.getCurrentUser();

            if (mUser != null) {
                mUid = mUser.getUid();
                mName = mUser.getDisplayName();
                mEmail = mUser.getEmail();
            }

            startTransaction(new UserPhoneFragment());

        });
    }

    @Override
    public void onPhoneUpdated(String phone) {
        mPhone = phone;
    }

    private void startTransaction(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.commit();
    }
}
