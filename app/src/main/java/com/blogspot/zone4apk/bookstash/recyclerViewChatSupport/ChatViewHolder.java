package com.blogspot.zone4apk.bookstash.recyclerViewChatSupport;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.blogspot.zone4apk.bookstash.R;

public class ChatViewHolder extends RecyclerView.ViewHolder {

    ImageView imageViewChatAdmin;
    ImageView imageViewChatOther;
    TextView chatMessage;
    TextView chatTimeStamp;

    public ChatViewHolder(View itemView) {
        super(itemView);
        imageViewChatAdmin = (ImageView) itemView.findViewById(R.id.imageview_chat_admin);
        imageViewChatOther = (ImageView) itemView.findViewById(R.id.imageview_chat_user);
        chatMessage = (TextView) itemView.findViewById(R.id.text_chat);
        chatTimeStamp = (TextView) itemView.findViewById(R.id.text_chat_time_stamp);

    }

    public void setChatSender(String sender, Context context) {
        if (sender.equals("admin")) {
            chatMessage.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAdminCard));
            chatMessage.setTextColor(ContextCompat.getColor(context, R.color.colorAdminText));
            imageViewChatAdmin.setVisibility(View.VISIBLE);
            imageViewChatOther.setVisibility(View.GONE);
        } else {
            chatMessage.setBackgroundColor(ContextCompat.getColor(context, R.color.colorUserCard));
            chatMessage.setTextColor(ContextCompat.getColor(context, R.color.colorUserText));
            imageViewChatAdmin.setVisibility(View.GONE);
            imageViewChatOther.setVisibility(View.VISIBLE);
        }
    }

    public void setChatMessage(String chatMessageText) {
        chatMessage.setText(chatMessageText);
    }

    public void setChatTimeStamp(String chatTimeStampText) {
        chatTimeStamp.setText(chatTimeStampText);
    }
}
