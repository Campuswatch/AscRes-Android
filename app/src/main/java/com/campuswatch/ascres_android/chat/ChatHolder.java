package com.campuswatch.ascres_android.chat;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.campuswatch.ascres_android.utils.DateUtil;
import com.campuswatch.ascres_android.models.Chat;
import com.campuswatch.ascres_android.models.User;

/**
 * Thought of by samwyz for the most part on 4/13/17.
 */

public class ChatHolder extends RecyclerView.ViewHolder {

    private final Context context;
    private final TextView messageText;
    private final TextView timestampText;
    private final ImageView chatImage;
    private final LinearLayout chatLayout;

    public ChatHolder(View itemView) {
        super(itemView);
        this.context = itemView.getContext().getApplicationContext();

        messageText = (TextView) itemView.findViewById(R.id.chat_text);
        timestampText = (TextView) itemView.findViewById(R.id.timestamp_text);
        chatImage = (ImageView) itemView.findViewById(R.id.chat_image);
        chatLayout = (LinearLayout) itemView.findViewById(R.id.chat_message_layout);

        setIsRecyclable(false);
    }

    void setMessage(Chat chat, User user) {
        if (chat.getIsImage()) {
            chatImage.setVisibility(View.VISIBLE);
            Glide.with(context).load(chat.getText()).into(chatImage);
        } else {
            if (chat.getUid().equals(user.getUid())) {
                chatLayout.setBackground(ContextCompat.getDrawable(R.drawable.chat_bubble_user));
                chatLayout.setGravity(Gravity.END);
            } else {
                chatLayout.setBackground(ContextCompat.getDrawable(R.drawable.chat_bubble_dispatch));
                chatLayout.setGravity(Gravity.START);
            } messageText.setText(chat.getText());
        }
    }

    void setTimestampText(Chat chat, User user) {
        if  (chat.getUid().equals(user.getUid())){
            timestampText.setGravity(Gravity.END);
        } else timestampText.setGravity(Gravity.START);
        timestampText.setText(DateUtil.convertTimestampTime(chat.getTimestamp()));
    }
}
