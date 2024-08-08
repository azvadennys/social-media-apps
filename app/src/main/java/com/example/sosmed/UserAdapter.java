package com.example.sosmed;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserAdapter extends ArrayAdapter<UserItem> {
    private final Context context;
    private final ArrayList<UserItem> userItems;
    private final FirebaseAuth mAuth;
    private final FirebaseFirestore db;

    public UserAdapter(@NonNull Context context, @NonNull ArrayList<UserItem> userItems) {
        super(context, 0, userItems);
        this.context = context;
        this.userItems = userItems;
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        UserItem userItem = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_user, parent, false);
        }

        TextView userNameTextView = convertView.findViewById(R.id.user_name);
        Button followButton = convertView.findViewById(R.id.follow_button);

        userNameTextView.setText(userItem.getUserName());

        checkFollowStatus(userItem.getUserId(), followButton);

        followButton.setOnClickListener(v -> handleFollowUnfollow(userItem, followButton));

        return convertView;
    }

    private void checkFollowStatus(String userId, Button followButton) {
        String currentUserId = mAuth.getCurrentUser().getUid();

        DocumentReference followRef = db.collection("users").document(userId)
                .collection("followers").document(currentUserId);

        followRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().exists()) {
                    followButton.setText("Unfollow");
                } else {
                    followButton.setText("Follow");
                }
            } else {
                Toast.makeText(context, "Error checking follow status", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleFollowUnfollow(UserItem user, Button followButton) {
        String currentUserId = mAuth.getCurrentUser().getUid();
        DocumentReference followRef = db.collection("users").document(user.getUserId())
                .collection("followers").document(currentUserId);

        followRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().exists()) {
                    // Unfollow
                    followRef.delete().addOnSuccessListener(aVoid -> {
                        Toast.makeText(context, "Unfollowed " + user.getUserName(), Toast.LENGTH_SHORT).show();
                        followButton.setText("Follow");
                    }).addOnFailureListener(e -> {
                        Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                } else {
                    // Follow
                    Map<String, Object> followData = new HashMap<>();
                    followData.put("followerId", currentUserId);
                    followRef.set(followData).addOnSuccessListener(aVoid -> {
                        Toast.makeText(context, "Followed " + user.getUserName(), Toast.LENGTH_SHORT).show();
                        followButton.setText("Unfollow");
                    }).addOnFailureListener(e -> {
                        Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }
            } else {
                Toast.makeText(context, "Error checking follow status", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
