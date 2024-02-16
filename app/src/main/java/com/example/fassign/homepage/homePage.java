package com.example.fassign.homepage;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import com.example.fassign.R;
import com.example.fassign.homepage.addstory.addstory;
import com.example.fassign.profile.viewProfile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class homePage extends AppCompatActivity {

    private ImageButton homeButton;
    private ImageButton meButton;
    private ImageButton plusButton;
    private CircleImageView homeBannerProfileImageView;

    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        // Initialize buttons and image view
        homeButton = findViewById(R.id.homebutton);
        meButton = findViewById(R.id.mebutton);
        plusButton = findViewById(R.id.button_topleft);
        homeBannerProfileImageView = findViewById(R.id.homeBannerProfileImageView);

        // Initialize Firebase components
        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Set click listeners
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle click for homeButton (redirect to homePage)
                startActivity(new Intent(homePage.this, homePage.class));
            }
        });

        meButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle click for meButton (redirect to viewProfile)
                startActivity(new Intent(homePage.this, viewProfile.class));
            }
        });

        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle click for plusButton (redirect to addStory)
                startActivity(new Intent(homePage.this, addstory.class));
            }
        });

        homeBannerProfileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle click for homeBannerProfileImageView (redirect to homePage)
                startActivity(new Intent(homePage.this, viewProfile.class));
            }
        });

        // Retrieve the selected image from Firebase Storage for the current user
        retrieveProfileImage();
    }

    private void retrieveProfileImage() {
        if (currentUser != null) {
            // Get the user's document from Firestore
            db.collection("users")
                    .document(currentUser.getUid())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists() && document.contains("profileImageURL")) {
                                // Retrieve the profile image URL from Firestore
                                String profileImageURL = document.getString("profileImageURL");

                                // Load the profile image into the homeBannerProfileImageView and meButton
                                Picasso.get().load(profileImageURL).into(homeBannerProfileImageView);
                                meButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        // Handle click for meButton (redirect to viewProfile)
                                        Intent intent = new Intent(homePage.this, viewProfile.class);
                                        intent.putExtra("profileImageURL", profileImageURL);
                                        startActivity(intent);
                                    }
                                });
                            }
                        }
                    });
        }
    }
}
