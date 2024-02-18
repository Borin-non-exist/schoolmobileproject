package com.example.fassign.profile;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fassign.R;
import com.example.fassign.profile.changepassword.changepassword;
import com.example.fassign.profile.editProfile.editprofile;
import com.example.fassign.welcome.WelcomeActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class viewProfile extends AppCompatActivity {

    private ImageButton backButton;
    private Button editButton;
    private Button changePasswordButton;
    private Button logoutButton;
    private Button deleteAccountButton;
    private CircleImageView profileImageView;
    private TextView usernameTextView; // Add this line

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        // Initialize Firebase components
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        // Initialize buttons and image view
        backButton = findViewById(R.id.backbutton);
        editButton = findViewById(R.id.editbutton);
        changePasswordButton = findViewById(R.id.changepwbutton);
        logoutButton = findViewById(R.id.logoutbutton);
        deleteAccountButton = findViewById(R.id.deleteaccount);
        profileImageView = findViewById(R.id.addpicture);
        usernameTextView = findViewById(R.id.usernameTextView); // Add this line
        // Set click listeners
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle click for backButton (redirect to welcome activity)
                startActivity(new Intent(viewProfile.this, WelcomeActivity.class));
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle click for editButton (redirect to editProfile activity)
                startActivity(new Intent(viewProfile.this, editprofile.class));
            }
        });

        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle click for changePasswordButton (redirect to changePassword activity)
                startActivity(new Intent(viewProfile.this, changepassword.class));
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle click for logoutButton (logout and redirect to welcome activity)
                mAuth.signOut();
                startActivity(new Intent(viewProfile.this, WelcomeActivity.class));
                finish();
            }
        });

        deleteAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle click for deleteAccountButton
                showDeleteAccountDialog();
            }
        });

        // Retrieve the profile image from Firebase
        retrieveProfileImage();
    }

    private void retrieveProfileImage() {
        if (currentUser != null) {
            db.collection("users")
                    .document(currentUser.getUid())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists() && document.contains("profileImageURL")) {
                                String profileImageURL = document.getString("profileImageURL");
                                Picasso.get().load(profileImageURL).into(profileImageView);

                                // Retrieve and display the user's first name and last name
                                String firstName = document.getString("firstName");
                                String lastName = document.getString("lastName");
                                String fullName = firstName + " " + lastName;
                                usernameTextView.setText(fullName);
                            }
                        }
                    });
        }
    }


    private void showDeleteAccountDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Account");
        builder.setMessage("Are you sure you want to delete your account? This action cannot be undone.");

        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Delete the account from Firebase
                deleteUserAccount();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Cancel the dialog
                dialog.dismiss();
            }
        });

        builder.show();
    }

    private void deleteUserAccount() {
        if (currentUser != null) {
            // Delete the user from Firebase Authentication
            currentUser.delete()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                // Delete the user data from Firestore
                                db.collection("users")
                                        .document(currentUser.getUid())
                                        .delete()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(viewProfile.this, "Account deleted successfully", Toast.LENGTH_SHORT).show();
                                                    startActivity(new Intent(viewProfile.this, WelcomeActivity.class));
                                                    finish();
                                                } else {
                                                    Toast.makeText(viewProfile.this, "Failed to delete account", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            } else {
                                Toast.makeText(viewProfile.this, "Failed to delete account", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}
