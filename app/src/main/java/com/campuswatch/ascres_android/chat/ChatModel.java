package com.campuswatch.ascres_android.chat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Thought of by samwyz for the most part on 4/13/17.
 */

class ChatModel implements ChatActivityMVP.Model {

    private DatabaseReference chatRef;
    private StorageReference imageRef;

    ChatModel() {
        FirebaseDatabase firebase = FirebaseDatabase.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        chatRef = firebase.getReference("chats");
        imageRef = storage.getReference("images");
    }

    @Override
    public DatabaseReference getChatReference() {
        return chatRef;
    }

    @Override
    public StorageReference getImageReference() {
        return imageRef;
    }
}
