package com.campuswatch.ascres_android.chat;

import android.net.Uri;

import com.campuswatch.ascres_android.R;
import com.campuswatch.ascres_android.root.UserRepository;
import com.campuswatch.ascres_android.models.Chat;
import com.campuswatch.ascres_android.utils.DateUtil;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import static com.campuswatch.ascres_android.map.MapsActivity.ALERT_ID;

/**
 * Thought of by samwyz for the most part on 4/13/17.
 */

class ChatPresenter implements ChatActivityMVP.Presenter {

    private FirebaseRecyclerAdapter adapter;
    private ChatActivityMVP.Model model;
    private ChatActivityMVP.View view;
    private UserRepository repo;

    ChatPresenter(ChatActivityMVP.Model model, UserRepository repo) {
        this.model = model;
        this.repo = repo;
    }

    @Override
    public void setView(ChatActivityMVP.View view) {
        this.view = view;
    }

    @Override
    public void initializeChat() {
        adapter = getAdapter();
        view.setChatAdapterAndManager(adapter);
        model.getChatReference().child(ALERT_ID)
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                view.smoothScroll(dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    @Override
    public void sendMessage(String message) {
        if (ALERT_ID != null) {
            Chat chat = new Chat(repo.getUser().getUid(), message,
                    DateUtil.getTimeInMillis(), ALERT_ID, false);
            model.getChatReference().child(ALERT_ID)
                    .push().setValue(chat);

        } else {
            view.makeToast("Your alert has been resolved");
        }
    }

    @SuppressWarnings("VisibleForTests")
    @Override
    public void sendImage(Uri image) {
        model.getImageReference().child(repo.getUser().getUid())
                .child(ALERT_ID)
                .child(image.getLastPathSegment())
                .putFile(image).addOnSuccessListener(taskSnapshot -> {

            Chat chat = new Chat(repo.getUser().getUid(), taskSnapshot.getDownloadUrl().toString(),
                    DateUtil.getTimeInMillis(), ALERT_ID, true);

            model.getChatReference().child(ALERT_ID)
                    .push().setValue(chat);

            view.smoothScroll(adapter.getItemCount());
        });
    }

    @Override
    public void cleanChatAdapter() {
        adapter.cleanup();
    }

    private FirebaseRecyclerAdapter getAdapter() {
        return new FirebaseRecyclerAdapter<Chat, ChatHolder>
                (Chat.class, R.layout.chat_message, ChatHolder.class,
                        model.getChatReference()
                                .child(ALERT_ID)) {
            @Override
            protected void populateViewHolder(ChatHolder viewHolder, Chat chat, int position) {
                viewHolder.setMessage(chat, repo.getUser());
                viewHolder.setTimestampText(chat, repo.getUser());
            }
        };
    }
}
