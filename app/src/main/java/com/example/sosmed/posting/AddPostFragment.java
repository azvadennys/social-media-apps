package com.example.sosmed.posting;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.example.sosmed.R;

public class AddPostFragment extends Fragment {

    private EditText postContentEditText;
    private Button postButton;
    private DatabaseReference postsDatabase;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_post, container, false);

        postContentEditText = view.findViewById(R.id.post_content);
        postButton = view.findViewById(R.id.post_button);

        mAuth = FirebaseAuth.getInstance();
        postsDatabase = FirebaseDatabase.getInstance().getReference().child("Posts");

        postButton.setOnClickListener(v -> submitPost());

        return view;
    }

    private void submitPost() {
        String postContent = postContentEditText.getText().toString().trim();

        if (TextUtils.isEmpty(postContent)) {
            Toast.makeText(getActivity(), "Please enter some content", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();
        String postId = postsDatabase.push().getKey();
        Post newPost = new Post(postId, userId, postContent, System.currentTimeMillis());

        postsDatabase.child(postId).setValue(newPost)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getActivity(), "Post added successfully", Toast.LENGTH_SHORT).show();
                        // Optionally, navigate back to the previous fragment or activity
                        getFragmentManager().popBackStack();
                    } else {
                        Toast.makeText(getActivity(), "Failed to add post", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
