package com.example.sosmed.message;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.widget.ArrayAdapter;
import com.example.sosmed.R;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MessageAdapter extends ArrayAdapter<Message> {

    private String currentUserId;

    public MessageAdapter(Context context, List<Message> messages, String currentUserId) {
        super(context, 0, messages);
        this.currentUserId = currentUserId;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_message, parent, false);
        }

        Message message = getItem(position);

        TextView messageText = convertView.findViewById(R.id.message_text);
        TextView messageTime = convertView.findViewById(R.id.message_time);
        View messageContainer = convertView.findViewById(R.id.message_container);

        if (message != null) {
            messageText.setText(message.getMessage());
            messageTime.setText(formatTimestamp(message.getTimestamp()));

            if (message.getUserId().equals(currentUserId)) {
                // Apply styles for the current user's messages
                messageContainer.setBackgroundResource(R.drawable.sent_message_background);
                messageText.setTextColor(getContext().getResources().getColor(R.color.white));
                ((LinearLayout) convertView).setGravity(Gravity.END); // Align sender messages to the right
            } else {
                // Apply styles for other users' messages
                messageContainer.setBackgroundResource(R.drawable.received_message_background);
                messageText.setTextColor(getContext().getResources().getColor(R.color.black));
                ((LinearLayout) convertView).setGravity(Gravity.START); // Align receiver messages to the left
            }
        }

        return convertView;
    }

    private String formatTimestamp(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }
}
