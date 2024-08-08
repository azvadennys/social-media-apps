package com.example.sosmed.posting;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import java.util.ArrayList;
import java.util.List;
import com.example.sosmed.R;

public class PostDetailFragment extends Fragment {

    private static final String ARG_POST_ID = "postId";

    private String postId;
    private DatabaseReference postDatabase;
    private DatabaseReference commentsDatabase;
    private FirebaseAuth mAuth;

    private TextView postContent;
    private Button likeButton;
    private Button commentButton;
    private Button shareButton;
    private RecyclerView commentRecyclerView;
    private CommentAdapter commentAdapter;
    private List<Comment> commentList;

    public static PostDetailFragment newInstance(String postId) {
        PostDetailFragment fragment = new PostDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_POST_ID, postId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_detail, container, false);

        postContent = view.findViewById(R.id.post_content);
        likeButton = view.findViewById(R.id.like_button);
        commentButton = view.findViewById(R.id.comment_button);
        shareButton = view.findViewById(R.id.share_button);
        commentRecyclerView = view.findViewById(R.id.comment_list);

        commentRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        commentList = new ArrayList<>();
        commentAdapter = new CommentAdapter(getActivity(), commentList);
        commentRecyclerView.setAdapter(commentAdapter);

        if (getArguments() != null) {
            postId = getArguments().getString(ARG_POST_ID);
        }

        postDatabase = FirebaseDatabase.getInstance().getReference().child("Posts").child(postId);
        commentsDatabase = FirebaseDatabase.getInstance().getReference().child("Comments").child(postId);
        mAuth = FirebaseAuth.getInstance();

        loadPostDetails();
        loadComments();

        likeButton.setOnClickListener(v -> likePost());
        commentButton.setOnClickListener(v -> addComment("Sample Comment"));
        shareButton.setOnClickListener(v -> sharePost());

        return view;
    }

    private void loadPostDetails() {
        postDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Post post = snapshot.getValue(Post.class);
                if (post != null) {
                    postContent.setText(post.getContent());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors
            }
        });
    }

    private void loadComments() {
        commentsDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                commentList.clear();
                for (DataSnapshot commentSnapshot : snapshot.getChildren()) {
                    Comment comment = commentSnapshot.getValue(Comment.class);
                    if (comment != null) {
                        commentList.add(comment);
                    }
                }
                commentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors
            }
        });
    }

    private void likePost() {
        postDatabase.child("likesCount").setValue(ServerValue.increment(1));
    }

    private void addComment(String commentText) {
        String userId = mAuth.getCurrentUser().getUid();
        Comment comment = new Comment(userId, commentText, System.currentTimeMillis());
        commentsDatabase.push().setValue(comment);
    }

    private void sharePost() {
        // Implement sharing logic
    }
}
