package com.campuswatch.ascres_android.login;

import android.content.Intent;
import android.net.Uri;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import javax.inject.Inject;

public class LoginActivity extends AppCompatActivity implements
        LoginFragment.OnLoginListener,
        UserPhoneFragment.OnPhoneCompletedListener,
        UserImageFragment.ImageCapturedListener {

    private static final String UID = "uid";
    private static final String NAME = "name";
    private static final String EMAIL = "email";
    private static final String PHONE = "phone";
    private static final String IMAGE = "image";

    @Inject
    UserRepository mRepo;

    private DatabaseReference mUserRef;
    private StorageReference mImageRef;
    private FirebaseAuth mAuth;

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

        mUserRef = FirebaseDatabase.getInstance().getReference("users");
        mImageRef = FirebaseStorage.getInstance().getReference("images");
        mAuth = FirebaseAuth.getInstance();

        startTransaction(new LoginFragment());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mUid = savedInstanceState.getString(UID);
        mName = savedInstanceState.getString(NAME);
        mEmail = savedInstanceState.getString(EMAIL);
        mPhone = savedInstanceState.getString(PHONE);
        mImage = savedInstanceState.getString(IMAGE);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        outState.putString(UID, mUid);
        outState.putString(NAME, mName);
        outState.putString(EMAIL, mEmail);
        outState.putString(PHONE, mPhone);
        outState.putString(IMAGE, mImage);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onLoginClicked(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            if (!task.isSuccessful()) {
                Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show();
                return;
            }

            if (mAuth.getCurrentUser() != null) {
                mUid = mAuth.getCurrentUser().getUid();
                mName = mAuth.getCurrentUser().getDisplayName();
                mEmail = mAuth.getCurrentUser().getEmail();
            }

            startTransaction(new UserPhoneFragment());

        });
    }

    @Override
    public void onPhoneUpdated(String phone) {
        mPhone = phone;
        startTransaction(new UserImageFragment());
    }

    @SuppressWarnings("VisibleForTests")
    @Override
    public void onImageCaptured(Uri imagePath) {
        mImageRef.child(mUid).child(mUid).putFile(imagePath).addOnCompleteListener(this, task -> {
            if (!task.isSuccessful()) {
                Toast.makeText(LoginActivity.this, "Image upload failed.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (task.getResult().getDownloadUrl() != null) {
                mImage = task.getResult().getDownloadUrl().toString();
            }

            User user = new User(mUid, mName, mEmail, mPhone, mImage);
            mUserRef.child(mUid).setValue(user);
            mRepo.setUser(user);

            startActivity(new Intent(LoginActivity.this, TutorialActivity.class));
            finish();

        });
    }

    private void startTransaction(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.commit();
    }
}
