package com.example.fassign.welcome.register;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fassign.R;
import com.example.fassign.User;
import com.example.fassign.homepage.homePage;
import com.example.fassign.welcome.WelcomeActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


import android.net.Uri;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.Objects;

public class register_activity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseStorage firebaseStorage;

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
                // Handle the click event for addPictureButton
                openGallery();
            }
        });

        Button nextButton = findViewById(R.id.nextbutton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle the click event for nextButton
                registerUser();
            }
        });

        // Your existing code...
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, 1);
    }

    private void registerUser() {
        // Retrieve values from EditText fields
        EditText firstNameEditText = findViewById(R.id.firstnameenter);
        EditText lastNameEditText = findViewById(R.id.lastnameenter);
        EditText emailEditText = findViewById(R.id.email);
        EditText passwordEditText = findViewById(R.id.passwordfill);

        String firstName = firstNameEditText.getText().toString().trim();
        String lastName = lastNameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Check if any field is empty
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showToast("Information cannot be empty");
            return;
        }

        // Firebase authentication
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Registration successful
                        // Upload user information to Firebase
                        uploadUserInfo(firstName, lastName);
                    } else {
                        showToast("Registration failed. Please try again.");
                    }
                });
    }

    private void uploadUserInfo(String firstName, String lastName) {
        // Retrieve values from EditText fields
        EditText emailEditText = findViewById(R.id.email);
        EditText passwordEditText = findViewById(R.id.passwordfill);

        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Firebase authentication
        // Note: Do not create a new user again, use the existing authenticated user
        if (firebaseAuth.getCurrentUser() != null) {
            // Create a unique user ID
            String userId = firebaseAuth.getCurrentUser().getUid();

            // Create a User object
            User user = new User(userId, firstName, lastName, null); // You can update imageUrl as needed

            // Save the user information to Realtime Database
            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");
            usersRef.child(userId).setValue(user);

            // Navigate to the homepage
            navigateToHomePage(firstName);
        } else {
            showToast("User authentication failed. Please try again.");
        }
    }




    private void uploadImage(Uri imageUri, String firstName, String lastName) {
        // Create a reference to "profile_pictures" in Firebase Storage
        StorageReference storageReference = firebaseStorage.getReference().child("profile_pictures");

        // Generate a unique filename for the image
        String fileName = System.currentTimeMillis() + ".jpg";
        StorageReference imageReference = storageReference.child(fileName);

        // Upload the image to Firebase Storage
        imageReference.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Image uploaded successfully
                    // Get the download URL of the image
                    imageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();

                        // Save user information and image URL to Firebase Database or Firestore
                        // Update the following code according to your specific Firebase setup
                        // For example, you can use Realtime Database or Firestore
                        // You may also need to create a User model class to store this information

                        // For demonstration purposes, let's assume you have a "users" node in Realtime Database
                        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");

                        // Create a unique user ID
                        String userId = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();

                        // Create a User object
                        User user = new User(userId, firstName, lastName, imageUrl);

                        // Save the user information to Realtime Database
                        usersRef.child(userId).setValue(user);

                        // Navigate to the homepage
                        navigateToHomePage(firstName);
                    });
                })
                .addOnFailureListener(e -> showToast("Image upload failed. Please try again."));
    }

    private void navigateToHomePage(String firstName) {
        Intent intent = new Intent(register_activity.this, homePage.class);
        intent.putExtra("firstName", firstName);
        startActivity(intent);
        finish();
    }

    // Add onActivityResult method if needed for handling the gallery result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            // Handle the selected image from the gallery
            // The image URI will be in data.getData()
            Uri selectedImageUri = data.getData();
            // You can display the selected image in an ImageView if needed
            // For example, assuming you have an ImageView with id "selectedImageView"
            ImageView selectedImageView = findViewById(R.id.selectedImageView);
            selectedImageView.setImageURI(selectedImageUri);
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
