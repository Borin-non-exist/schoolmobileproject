package com.example.fassign.welcome.register;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fassign.R;
import com.example.fassign.User;
import com.example.fassign.homepage.homePage;
import com.example.fassign.welcome.WelcomeActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

public class register_activity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseStorage firebaseStorage;
    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        ImageView addPictureImageView = findViewById(R.id.addpicture);
        addPictureImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

        Button nextButton = findViewById(R.id.nextbutton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, 1);
    }

    private void registerUser() {
        EditText firstNameEditText = findViewById(R.id.firstnameenter);
        EditText lastNameEditText = findViewById(R.id.lastnameenter);
        EditText emailEditText = findViewById(R.id.email);
        EditText passwordEditText = findViewById(R.id.passwordfill);

        String firstName = firstNameEditText.getText().toString().trim();
        String lastName = lastNameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showToast("Information cannot be empty");
            return;
        }

        if (selectedImageUri == null) {
            showToast("Please select an image");
            return;
        }

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        showToast("Registration successful");
                        uploadUserInfo(firstName, lastName);
                    } else {
                        showToast("Registration failed. Error: " + task.getException());
                    }
                });
    }

    private void uploadUserInfo(String firstName, String lastName) {
        if (firebaseAuth.getCurrentUser() != null) {
            String userId = firebaseAuth.getCurrentUser().getUid();

            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");

            // Create a User object with only first name and last name
            User userBasicInfo = new User(userId, firstName, lastName);

            // Save the basic user information to Realtime Database
            usersRef.child(userId).setValue(userBasicInfo);

            // Create a reference to "profile_pictures" in Firebase Storage
            StorageReference storageReference = firebaseStorage.getReference().child("profile_pictures");

            // Generate a unique filename for the image
            String fileName = System.currentTimeMillis() + ".jpg";
            StorageReference imageReference = storageReference.child(fileName);

            // Upload the image to Firebase Storage
            imageReference.putFile(selectedImageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Image uploaded successfully
                        // Get the download URL of the image
                        imageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                            String imageUrl = uri.toString();

                            // Update the user information with the profile picture URL
                            userBasicInfo.setImageUrl(imageUrl);

                            // Save the complete user information to Realtime Database
                            usersRef.child(userId).setValue(userBasicInfo);

                            navigateToHomePage(firstName);
                        });
                    })
                    .addOnFailureListener(e -> {
                        // Image upload failed, display the error message
                        showToast("Image upload failed. Error: " + e.getMessage());
                    });
        } else {
            showToast("User authentication failed. Please try again.");
        }
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            ImageView selectedImageView = findViewById(R.id.selectedImageView);
            selectedImageView.setImageURI(selectedImageUri);
        }
    }

    private void navigateToHomePage(String firstName) {
        Intent intent = new Intent(register_activity.this, homePage.class);
        intent.putExtra("firstName", firstName);
        startActivity(intent);
        finish();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
