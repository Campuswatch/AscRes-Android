package com.campuswatch.ascres_android.chat;

import android.net.Uri;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;

/**
 * Thought of by samwyz for the most part on 4/13/17.
 */

public class ChatActivityMVP {

    interface View
    {
        void setChatAdapterAndManager(FirebaseRecyclerAdapter adapter);
        void smoothScroll(long position);
        void makeToast(String msg);
    }

    interface Presenter
    {
        void setView (ChatActivityMVP.View view);
        void sendMessage(String message);
        void sendImage(Uri image);
        void cleanChatAdapter();
        void initializeChat();
    }

    interface Model
    {
        DatabaseReference getChatReference();
        StorageReference getImageReference();
    }
}
