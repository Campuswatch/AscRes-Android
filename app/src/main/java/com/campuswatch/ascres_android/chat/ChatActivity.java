package com.campuswatch.ascres_android.chat;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.campuswatch.ascres_android.R;
import com.campuswatch.ascres_android.root.App;
import com.firebase.ui.database.FirebaseRecyclerAdapter;

import java.io.File;
import java.util.Date;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.campuswatch.ascres_android.Constants.APP_TAG;
import static com.campuswatch.ascres_android.Constants.IMAGE_CAPTURE_CODE;
import static com.campuswatch.ascres_android.Constants.IMAGE_GALLERY_REQUEST;
import static com.campuswatch.ascres_android.map.MapsActivity.IS_EMERGENCY;

public class ChatActivity extends AppCompatActivity
        implements ChatActivityMVP.View {

    @Inject
    ChatActivityMVP.Presenter presenter;

    @BindView(R.id.chat_recycler)
    RecyclerView chatRecycler;
    @BindView(R.id.chat_send_button)
    ImageButton chatSendButton;
    @BindView(R.id.chat_message_edit)
    EditText chatMessageEdit;
    @BindView(R.id.chat_toolbar)
    Toolbar chatToolbar;

    private File imageFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        ((App) getApplication()).getComponent().inject(this);
        ButterKnife.bind(this);

        presenter.setView(this);
        presenter.initializeChat();

        initializeActionBar();
    }

    @Override
    protected void onStart() {
        super.onStart();
        chatSendButton.setOnClickListener(sendButtonListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!IS_EMERGENCY) {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!IS_EMERGENCY) {
            presenter.cleanChatAdapter();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return true;
    }

    private void photoGalleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,
                getString(R.string.select_picture_title)), IMAGE_GALLERY_REQUEST);
    }

    private void photoCameraIntent() {
        if (isExternalStorageAvailable()) {
            String imageName = DateFormat.format("yyyy-MM-dd_hhmmss", new Date()).toString() + ".jpg";
            File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) +
                    File.separator + APP_TAG + File.separator);
            if (!folder.exists()) {
                folder.mkdir();
            }
            imageFile = new File(folder, imageName);
            Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            it.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageFile));
            startActivityForResult(it, IMAGE_CAPTURE_CODE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.sendPhoto:
                photoCameraIntent();
                break;
            case R.id.sendPhotoGallery:
                photoGalleryIntent();
                break;
            case android.R.id.home:
                onBackPressed();
                break;
        } return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == IMAGE_GALLERY_REQUEST) {
            if (resultCode == RESULT_OK) {
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    presenter.sendImage(selectedImageUri);
                }
            }
        } else if (requestCode == IMAGE_CAPTURE_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                presenter.sendImage(Uri.fromFile(imageFile));
            }
        }
    }

    private void initializeActionBar() {
        setSupportActionBar(chatToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().show();
    }

    private View.OnClickListener sendButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String message = chatMessageEdit.getText().toString().trim();
            if (!message.isEmpty()) {
                presenter.sendMessage(message);
                chatMessageEdit.setText("");
            }
        }
    };

    @Override
    public void setChatAdapterAndManager(FirebaseRecyclerAdapter adapter) {
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setStackFromEnd(true);
        chatRecycler.setAdapter(adapter);
        chatRecycler.setLayoutManager(manager);
    }

    @Override
    public void smoothScroll(long position) {
        chatRecycler.smoothScrollToPosition((int)position);
    }

    @Override
    public void makeToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    private boolean isExternalStorageAvailable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }
}
