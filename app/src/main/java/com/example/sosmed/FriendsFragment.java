package com.example.sosmed;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FriendsFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ListView userListView;
    private ArrayList<UserItem> userList;
    private ArrayAdapter<UserItem> adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_friends, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        userListView = view.findViewById(R.id.user_list);
        userList = new ArrayList<>();
        adapter = new UserAdapter(getContext(), userList);
        userListView.setAdapter(adapter);

        loadUserList();

        userListView.setOnItemClickListener((parent, view1, position, id) -> {
            UserItem selectedUser = userList.get(position);
            handleFollowUnfollow(selectedUser);
        });
    }

    private void loadUserList() {
        db.collection("users").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                userList.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String userId = document.getId();
                    String userName = document.getString("name");
                    userList.add(new UserItem(userId, userName));
                }
                adapter.notifyDataSetChanged();
            } else {
                Toast.makeText(getContext(), "Error loading user list", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleFollowUnfollow(UserItem user) {
        String currentUserId = mAuth.getCurrentUser().getUid();

        DocumentReference followRef = db.collection("users").document(user.getUserId())
                .collection("followers").document(currentUserId);

        followRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                boolean isFollowing = task.getResult().exists();
                if (isFollowing) {
                    // Unfollow
                    followRef.delete().addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "Unfollowed " + user.getUserName(), Toast.LENGTH_SHORT).show();
                        loadUserList();
                    }).addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                } else {
                    // Follow
                    Map<String, Object> followData = new HashMap<>();
                    followData.put("followerId", currentUserId);
                    followRef.set(followData).addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "Followed " + user.getUserName(), Toast.LENGTH_SHORT).show();
                        loadUserList();
                    }).addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }
            } else {
                Toast.makeText(getContext(), "Error checking follow status", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
