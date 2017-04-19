package com.campuswatch.ascres_android.map;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.campuswatch.ascres_android.R;
import com.campuswatch.ascres_android.models.User;
import com.campuswatch.ascres_android.views.ImageTransform;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;
import static com.campuswatch.ascres_android.Constants.IMAGE_GALLERY_REQUEST;

/**
 * Thought of by samwyz for the most part on 4/13/17.
 */

public class UserUpdateFragment extends DialogFragment {

    private static final String USER_DATA = "user";

    @BindView(R.id.user_update_image)
    ImageView imageEdit;
    @BindView(R.id.name_update_edit)
    EditText nameEdit;
    @BindView(R.id.phone_update_edit)
    EditText phoneEdit;
    @BindView(R.id.email_update_edit)
    EditText emailEdit;
    @BindView(R.id.update_fragment_button)
    Button updateButton;
    @BindView(R.id.update_profile_text)
    TextView headerText;

    UserUpdateListener listener;
    Uri imageUri;
    User user;

    public UserUpdateFragment() {
        //empty public constructor
    }

    public static UserUpdateFragment newInstance(String user) {
        UserUpdateFragment fragment = new UserUpdateFragment();
        Bundle args = new Bundle();
        args.putString(USER_DATA, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = User.create(getArguments().getString(USER_DATA));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_update, container, false);

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00FFFFFF")));
        WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
        params.gravity = Gravity.TOP;
        getDialog().getWindow().setAttributes(params);

        ButterKnife.bind(this, view);

        imageUri = Uri.parse(user.getImage());
        imageEdit.setOnClickListener(updateImageListener);
        updateButton.setOnClickListener(updateButtonListener);

        nameEdit.setText(user.getName());
        emailEdit.setText(user.getEmail());
        Glide.with(this).load(imageUri)
                .placeholder(R.drawable.logo_full_resize)
                .bitmapTransform(new ImageTransform(this.getContext()))
                .into(imageEdit);

        if (!user.getPhone().equals("")) {
            phoneEdit.setText(user.getPhone());
        }

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getDialog().getWindow().setAllowEnterTransitionOverlap(true);
        } getDialog().getWindow().setWindowAnimations(R.style.update_slide_animation);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof UserUpdateListener) {
            listener = (UserUpdateListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnLoginListener");
        }
    }

    private View.OnClickListener updateButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            boolean validated = true;

            if (nameEdit.getText().toString().isEmpty()) {
                nameEdit.setError("Fill out");
                validated = false;
            } if (!isValidMail(emailEdit.getText().toString().trim())) {
                emailEdit.setError("Invalid email");
                validated = false;
            } if (!isValidMobile(phoneEdit.getText().toString().trim())) {
                phoneEdit.setError("Invalid phone");
                validated = false;
            }

            if (validated) {
                nameEdit.setError(""); emailEdit.setError(""); phoneEdit.setError("");
                listener.OnUserUpdated(nameEdit.getText().toString().trim(),
                        phoneEdit.getText().toString().trim(),
                        emailEdit.getText().toString().trim(),
                        imageUri);
                dismiss();
            }
        }
    };

    private View.OnClickListener updateImageListener = v -> photoGalleryIntent();

    private void photoGalleryIntent() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent,
                getString(R.string.select_picture_title)), IMAGE_GALLERY_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //handle camera or gallery activity result
        if (requestCode == IMAGE_GALLERY_REQUEST) {
            if (resultCode == RESULT_OK) {
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    imageUri = selectedImageUri;
                    Glide.with(this).load(imageUri)
                            .placeholder(R.drawable.logo_full_resize)
                            .bitmapTransform(new ImageTransform(this.getContext()))
                            .into(imageEdit);
                }
            }
        }
    }

    public interface UserUpdateListener {
        void OnUserUpdated(String name, String phone, String email, Uri image);
    }

    private boolean isValidMobile(String phone) {
        return android.util.Patterns.PHONE.matcher(phone).matches();
    }

    private boolean isValidMail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
