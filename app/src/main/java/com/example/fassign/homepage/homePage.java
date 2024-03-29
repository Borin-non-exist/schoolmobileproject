package com.example.fassign.homepage;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import com.example.fassign.R;
import com.example.fassign.homepage.addstory.addstory;
import com.example.fassign.profile.viewProfile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import de.hdodenhof.circleimageview.CircleImageView;

public class homePage extends AppCompatActivity {

    private ImageButton homeButton;
    private ImageButton meButton;
    private ImageButton plusButton;
    private CircleImageView homeBannerProfileImageView;
    private TextView usernameTextView;

    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    private static final String DEFAULT_PROFILE_URL = "your_default_profile_image_url_here";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        // Initialize buttons, views, and Firebase components
        homeButton = findViewById(R.id.homebutton);
        meButton = findViewById(R.id.mebutton);
        plusButton = findViewById(R.id.button_topleft);
        homeBannerProfileImageView = findViewById(R.id.homeBannerProfileImageView);
        usernameTextView = findViewById(R.id.usernameTextView);

        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference().child("profile_pictures");

        // Set click listeners
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(homePage.this, homePage.class));
            }
        });

        meButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(homePage.this, viewProfile.class);
                startActivity(intent);
            }
        });

        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(homePage.this, addstory.class));
            }
        });

        homeBannerProfileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(homePage.this, viewProfile.class));
            }
        });

        // Retrieve the selected image from Firebase Storage for the current user
        retrieveProfileImage();

        // Retrieve and display the username
        retrieveUserData();
    }

    private void retrieveProfileImage() {
        if (currentUser != null) {
            storageReference.child(currentUser.getUid() + ".jpg")
                    .getDownloadUrl()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            String profileImageURL = task.getResult().toString();
                            Picasso.get().load(profileImageURL).into(homeBannerProfileImageView);
                            meButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(homePage.this, viewProfile.class);
                                    intent.putExtra("profileImageURL", profileImageURL);
                                    startActivity(intent);
                                }
                            });
                        } else {
                            // If the image retrieval fails, set a default profile image
                            Picasso.get().load(DEFAULT_PROFILE_URL).into(homeBannerProfileImageView);
                            meButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // Handle click for default image
                                }
                            });
                        }
                    });
        }
    }

    private void retrieveUserData() {
        if (currentUser != null) {
            db.collection("users")
                    .document(currentUser.getUid())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                String username = document.getString("username");
                                if (username != null) {
                                    usernameTextView.setText(username);
                                }
                            }
                        }
                    });
        }
    }
}
