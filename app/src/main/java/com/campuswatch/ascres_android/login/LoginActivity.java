package com.campuswatch.ascres_android.login;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.campuswatch.ascres_android.R;
import com.campuswatch.ascres_android.UserRepository;
import com.campuswatch.ascres_android.models.User;
import com.campuswatch.ascres_android.root.App;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import javax.inject.Inject;

public class LoginActivity extends BaseLoginActivity implements
        LoginFragment.OnLoginListener,
        UserPhoneFragment.OnPhoneCompletedListener,
        UserImageFragment.ImageCapturedListener {

    private static final String UID = "uid";
    private static final String NAME = "name";
    private static final String EMAIL = "email";
    private static final String PHONE = "phone";
    private static final String IMAGE = "image";
    private static final String HOUSEHOLD = "household";

    @Inject
    UserRepository mRepo;

    private DatabaseReference mHouseRef;
    private DatabaseReference mUserRef;
    private StorageReference mImageRef;
    private FirebaseAuth mAuth;

    private String mUid;
    private String mName;
    private String mEmail;
    private String mPhone;
    private String mImage;
    private String mHousehold;

    private boolean onboardComplete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ((App) getApplication()).getComponent().inject(this);

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        mHouseRef = db.getReference("households");
        mUserRef = db.getReference("users");
        mImageRef = FirebaseStorage.getInstance().getReference("images");
        mAuth = FirebaseAuth.getInstance();

        onboardComplete = false;

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
        mHousehold = savedInstanceState.getString(HOUSEHOLD);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        outState.putString(UID, mUid);
        outState.putString(NAME, mName);
        outState.putString(EMAIL, mEmail);
        outState.putString(PHONE, mPhone);
        outState.putString(IMAGE, mImage);
        outState.putString(HOUSEHOLD, mHousehold);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!onboardComplete && mAuth.getCurrentUser() != null) {
            mAuth.signOut();
        }
    }

    @Override
    public void onLoginClicked(String email, String password) {
        showProgressDialog();
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            hideProgressDialog();
            if (!task.isSuccessful()) {
                makeToast("Login failed");
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
        showProgressDialog();
        mImageRef.child(mUid).child(mUid).putFile(imagePath).addOnCompleteListener(this, task -> {

            if (!task.isSuccessful()) {
                hideProgressDialog();
                makeToast("Image upload failed.");
                return;
            }

            if (task.getResult().getDownloadUrl() != null) {
                mImage = task.getResult().getDownloadUrl().toString();
            }

            getHousehold();
        });
    }

    private void getHousehold() {

        mHouseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot deepSnap : snapshot.getChildren()) {
                        if (deepSnap.getValue().equals(mUid)) {
                            mHousehold = snapshot.getKey();
                            buildUserAndFinish();
                            return;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                hideProgressDialog();
                makeToast(databaseError.getMessage());
            }
        });
    }

    private void buildUserAndFinish() {
        User user = new User(mUid, mName, mEmail, mPhone, mImage, mHousehold);
        mUserRef.child(mUid).setValue(user);
        mRepo.setUser(user);

        onboardComplete = true;

        startActivity(new Intent(LoginActivity.this, TutorialActivity.class));
        finish();
    }

    private void startTransaction(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.commit();
    }
}
