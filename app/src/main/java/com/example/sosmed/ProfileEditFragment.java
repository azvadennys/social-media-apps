package com.example.sosmed;

import android.os.Bundle;
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
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileEditFragment extends Fragment {

    private EditText editProfileName, editProfileEmail;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile_edit, container, false); // Ensure the layout file name is correct
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        editProfileName = view.findViewById(R.id.edit_profile_name);
        editProfileEmail = view.findViewById(R.id.edit_profile_email);
        Button buttonSave = view.findViewById(R.id.button_save);

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Check if user is logged in
        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        // Set onClick listener for the save button
        buttonSave.setOnClickListener(v -> saveProfile());

        // Load existing profile data
        loadProfileData();
    }

    private void saveProfile() {
        // Get input values
        String name = editProfileName.getText().toString().trim();
        String email = editProfileEmail.getText().toString().trim();

        // Validate input
        if (name.isEmpty() || email.isEmpty()) {
            Toast.makeText(getContext(), "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get current user ID
        String userId = mAuth.getCurrentUser().getUid();

        // Update Firestore document
        db.collection("users").document(userId)
                .update("name", name, "email", email)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Profile updated", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void loadProfileData() {
        // Check if user is logged in
        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get current user ID
        String userId = mAuth.getCurrentUser().getUid();

        // Retrieve user data from Firestore
        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("name");
                        String email = documentSnapshot.getString("email");

                        // Set retrieved data to views
                        editProfileName.setText(name);
                        editProfileEmail.setText(email);
                    } else {
                        Toast.makeText(getContext(), "No profile data found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
