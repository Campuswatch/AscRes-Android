package com.campuswatch.ascres_android.signup;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.campuswatch.ascres_android.BuildConfig;
import com.campuswatch.ascres_android.UserRepository;
import com.campuswatch.ascres_android.models.User;
import com.campuswatch.ascres_android.root.App;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ui.ResultCodes;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;

import javax.inject.Inject;

import static com.campuswatch.ascres_android.Constants.SIGN_IN;
import static com.campuswatch.ascres_android.Constants.USER_DATA;

public class SignUpActivity extends AppCompatActivity {

    @Inject
    UserRepository repo;

    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        ((App) getApplication()).getComponent().inject(this);
        userRef = FirebaseDatabase.getInstance().getReference("users");

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setTheme(R.style.CampusWatchTheme)
                        .setLogo(R.drawable.campuswatch_logo_fulltext)
                        .setIsSmartLockEnabled(!BuildConfig.DEBUG)
                        .setProviders(Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build(),
                                new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build()))
                        .build(),
                SIGN_IN);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SIGN_IN) {
            if (resultCode == RESULT_OK)
            {
                saveUser();
                return;
            }
        } if (resultCode == RESULT_CANCELED) {
            Toast.makeText(this, "Sign in cancelled", Toast.LENGTH_SHORT).show();
            finish();
            return;
        } if (resultCode == ResultCodes.RESULT_NO_NETWORK) {
            Toast.makeText(this, "No network available", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void saveUser()
    {
        FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
        SharedPreferences prefs = getSharedPreferences(USER_DATA, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        User user = new User(
                fbUser.getEmail(),
                fbUser.getDisplayName(),
                fbUser.getUid(),
                IMAGE_PLACEHOLDER,
                0, UPDATE_PHONE);
        userRef.child(user.getUid()).setValue(user);
        editor.putString(USER_DATA, user.serialize());
        editor.apply();
        repo.setUser(user);
        completeSignup();
    }

    private void completeSignup() {
        startActivity(new Intent(this, TutorialActivity.class));
        finish();
    }
}
