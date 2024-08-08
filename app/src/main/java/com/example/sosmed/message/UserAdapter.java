package com.example.sosmed.message;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.widget.ArrayAdapter;
import com.example.sosmed.R;
import java.util.List;

public class UserAdapter extends ArrayAdapter<User> {

    public UserAdapter(@NonNull Context context, @NonNull List<User> users) {
        super(context, 0, users);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_user, parent, false);
        }

        User user = getItem(position);

        TextView userNameTextView = convertView.findViewById(R.id.user_name);
        TextView userEmailTextView = convertView.findViewById(R.id.user_email);

        if (user != null) {
            userNameTextView.setText(user.getName());
            userEmailTextView.setText(user.getEmail());
        }

        return convertView;
    }
}
