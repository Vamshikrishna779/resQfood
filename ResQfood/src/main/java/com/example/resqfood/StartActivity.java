package com.example.resqfood;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class StartActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            // User is already logged in, check their role
            checkUserRole(currentUser.getUid());
        } else {
            // User is not logged in, show the StartActivity
            setContentView(R.layout.activity_start);
            findViewById(R.id.buttonSignup).setOnClickListener(v -> {
                Intent intent = new Intent(StartActivity.this, SignupActivity.class);
                startActivity(intent);
                finish();
            });

            findViewById(R.id.buttonLogin).setOnClickListener(v -> {
                Intent intent = new Intent(StartActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            });
        }
    }

    private void checkUserRole(String userId) {
        mFirestore.collection("users").document(userId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            String role = document.getString("role");
                            if ("Donor".equals(role)) {
                                startDonorActivity();
                            } else if ("Recipient".equals(role)) {
                                startRecipientActivity();
                            }
                        }
                    } else {
                        // Error getting user role, go to login
                        startLoginActivity();
                    }
                });
    }

    private void startDonorActivity() {
        Intent intent = new Intent(StartActivity.this, DonorHomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void startRecipientActivity() {
        Intent intent = new Intent(StartActivity.this, RecipientHomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void startLoginActivity() {
        Intent intent = new Intent(StartActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}


/*
public class StartActivity extends AppCompatActivity {

    private static final int REQUEST_SIGNUP = 1;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            redirectToHomeActivity(currentUser.getUid());
            finish(); // Finish the StartActivity so the user can't go back to it without signing out
        }

        Button buttonSignup = findViewById(R.id.buttonSignup);
        Button buttonLogin = findViewById(R.id.buttonLogin);

        buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(StartActivity.this, SignupActivity.class), REQUEST_SIGNUP);
            }
        });

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StartActivity.this, LoginActivity.class));
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SIGNUP && resultCode == RESULT_OK) {
            // Signup successful, navigate back to StartActivity
            // No need to do anything here, as we're already in StartActivity
        }
    }

    private void redirectToHomeActivity(String userId) {
        db.collection("users").document(userId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            String role = document.getString("role");
                            if (role != null) {
                                Intent intent;
                                switch (role) {
                                    case "Donor":
                                        intent = new Intent(StartActivity.this, DonorHomeActivity.class);
                                        break;
                                    case "Recipient":
                                        intent = new Intent(StartActivity.this, RecipientHomeActivity.class);
                                        break;
                                    default:
                                        intent = new Intent(StartActivity.this, LoginActivity.class);
                                }
                                startActivity(intent);
                                finish(); // Close StartActivity to prevent going back
                            }
                        } else {
                            // Handle case where user document doesn't exist
                            // Redirect to login activity
                            startActivity(new Intent(StartActivity.this, LoginActivity.class));
                            finish(); // Close StartActivity to prevent going back
                        }
                    } else {
                        // Handle failed document retrieval
                        // Redirect to login activity
                        startActivity(new Intent(StartActivity.this, LoginActivity.class));
                        finish(); // Close StartActivity to prevent going back
                    }
                });
    }
}
*/