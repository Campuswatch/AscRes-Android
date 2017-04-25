package com.campuswatch.ascres_android.login;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.campuswatch.ascres_android.R;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.campuswatch.ascres_android.Constants.IMAGE_CAPTURE_CODE;
import static com.campuswatch.ascres_android.Constants.STORAGE_PERMISSION_REQUEST;

public class UserImageFragment extends Fragment implements View.OnClickListener {

    @BindView(R.id.image_button)
    FloatingActionButton captureButton;

    private ImageCapturedListener mListener;
    private Uri mCurrentPhotoPath;

    public UserImageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_user_image, container, false);
        ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        captureButton.setOnClickListener(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ImageCapturedListener) {
            mListener = (ImageCapturedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ImageCapturedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.image_button) {
            checkStoragePermission();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_CAPTURE_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                mListener.onImageCaptured(mCurrentPhotoPath);
            } else Toast.makeText(getActivity(), "Image not captured.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == STORAGE_PERMISSION_REQUEST) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchImageCaptureIntent();
            } else showSnackbarRationale();
        }
    }

    private void checkStoragePermission() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    STORAGE_PERMISSION_REQUEST);

        } else dispatchImageCaptureIntent();
    }

    private void showSnackbarRationale() {
        Snackbar.make(getActivity().findViewById(android.R.id.content),
                "Storage access required to upload images to dispatch and user profile",
                Snackbar.LENGTH_INDEFINITE)
                .setAction("OK", view -> requestPermissions(
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        STORAGE_PERMISSION_REQUEST)).show();
    }

    private void dispatchImageCaptureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Toast.makeText(getActivity(), "Error creating image file.", Toast.LENGTH_SHORT).show();
                ex.printStackTrace();
            }

            if (photoFile != null) {
                mCurrentPhotoPath = FileProvider.getUriForFile(getActivity(),
                        "com.campuswatch.ascres_android",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCurrentPhotoPath);
                startActivityForResult(takePictureIntent, IMAGE_CAPTURE_CODE);
            }
        }
    }

    private File createImageFile() throws IOException {
        String imageFileName = "JPEG_" + DateFormat.format("yyyy-MM-dd_hhmmss", new Date()).toString();
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    interface ImageCapturedListener {
        void onImageCaptured(Uri imagePath);
    }
}
