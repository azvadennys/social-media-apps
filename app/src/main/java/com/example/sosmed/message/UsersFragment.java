package com.example.sosmed.message;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.sosmed.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;

public class UsersFragment extends Fragment {

    private ListView userList;
    private UserAdapter userAdapter;
    private List<User> userListItems;

    private FirebaseFirestore firestore;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userList = view.findViewById(R.id.user_list);
        firestore = FirebaseFirestore.getInstance();

        userListItems = new ArrayList<>();
        userAdapter = new UserAdapter(getContext(), userListItems);
        userList.setAdapter(userAdapter);

        userList.setOnItemClickListener((parent, v, position, id) -> {
            User selectedUser = userListItems.get(position);
            startChat(selectedUser);
        });

        loadUsers();
    }

    private void loadUsers() {
        firestore.collection("users").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                userListItems.clear();
                String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                for (QueryDocumentSnapshot document : task.getResult()) {
                    User user = document.toObject(User.class);

                    if (user != null) {
                        String userId = user.getUid();
                        if (userId != null && !userId.equals(currentUserId)) {
                            userListItems.add(user);
                        }
                    }
                }
                userAdapter.notifyDataSetChanged();
            } else {
                Log.e("UsersFragment", "Error loading users", task.getException());
            }
        });
    }

    private void startChat(User user) {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String userId = user.getUid();

        if (userId != null) {
            // Create or retrieve the chat ID
            String chatId = currentUserId.compareTo(userId) < 0 ? currentUserId + "_" + userId : userId + "_" + currentUserId;

            Intent intent = new Intent(getContext(), ChatActivity.class);
            intent.putExtra("chatId", chatId);
            intent.putExtra("userId", userId);
            startActivity(intent);
        }
    }
}
