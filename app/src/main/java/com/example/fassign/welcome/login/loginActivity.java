package com.example.fassign.welcome.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fassign.R;
import com.example.fassign.homepage.homePage;
import com.example.fassign.welcome.WelcomeActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class loginActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();

        ImageButton buttonTopLeft = findViewById(R.id.button_topleft);
        buttonTopLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle the click event for button-topleft
                navigateToWelcomeActivity();
            }
        });

        Button backButton = findViewById(R.id.backbutton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle the click event for backbutton
                navigateToWelcomeActivity();
            }
        });

        Button loginButton = findViewById(R.id.loginbutton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle the click event for login button
                loginUser();
            }
        });

        // Your existing code...
    }

    private void loginUser() {
        // Retrieve values from EditText fields
        EditText emailEditText = findViewById(R.id.emailEditText);
        EditText passwordEditText = findViewById(R.id.passwordEditText);

        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Check if any field is empty
        if (email.isEmpty() || password.isEmpty()) {
            showToast("Email and password cannot be empty");
            return;
        }

        // Firebase authentication
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Login successful
                            showToast("Login successful");
                            navigateToHomePage();
                        } else {
                            // If sign in fails, display a message to the user.
                            showToast("Incorrect username or password. Please try again.");
                        }
                    }
                });
    }

    private void navigateToHomePage() {
        Intent intent = new Intent(loginActivity.this, homePage.class);
        startActivity(intent);
        finish(); // Optional: Close the current activity if needed
    }

    private void navigateToWelcomeActivity() {
        Intent intent = new Intent(loginActivity.this, WelcomeActivity.class);
        startActivity(intent);
        finish(); // Optional: Close the current activity if needed
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
