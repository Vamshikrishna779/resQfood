package com.example.resqfood;

import android.content.Intent;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

import com.google.android.gms.tasks.OnCompleteListener;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextEmail, editTextPassword;
    private Button buttonLogin;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);

        TextView textViewSign = findViewById(R.id.textViewSign);
        textViewSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the signup activity
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });
        buttonLogin.setOnClickListener(v -> {
            String email = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            if (validateForm(email, password)) {
                loginUser(email, password);
            }
        });
    }

    // Validate form fields
    private boolean validateForm(String email, String password) {
        if (TextUtils.isEmpty(email)) {
            editTextEmail.setError("Email is required");
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError("Password is required");
            return false;
        }

        return true;
    }

    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            checkUserRole(user.getUid());
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
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
                        Toast.makeText(LoginActivity.this, "Failed to get user role.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void startDonorActivity() {
        Intent intent = new Intent(LoginActivity.this, DonorHomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void startRecipientActivity() {
        Intent intent = new Intent(LoginActivity.this, RecipientHomeActivity.class);
        startActivity(intent);
        finish();
    }
}

/*


public class LoginActivity extends AppCompatActivity {

    private EditText editTextEmail, editTextPassword;
    private Button buttonLogin;
    private TextView textViewStatus;
    private FirebaseAuth mAuth;

    private FirebaseFirestore mFirestore;
    private FirebaseUser currentUser;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        textViewStatus = findViewById(R.id.textViewStatus);


        TextView textViewSign = findViewById(R.id.textViewSign);
        textViewSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the signup activity
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle login button click
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();
                loginUser(email, password);
            }
        });

    }

    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                redirectToHome(user.getUid());
                                finish();
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void redirectToHome(String userId) {
        FirebaseFirestore.getInstance().collection("users")
                .document(userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            String role = document.getString("role");
                            Class<?> homeActivityClass;
                            switch (role) {
                                case "Donor":
                                    homeActivityClass = DonorHomeActivity.class;
                                    break;
                                case "Recipient":
                                    homeActivityClass = RecipientHomeActivity.class;
                                    break;
                                default:
                                    homeActivityClass = StartActivity.class;
                                    break;
                            }
                            startActivity(new Intent(LoginActivity.this, homeActivityClass));
                            finish();

                        } else {
                            Toast.makeText(LoginActivity.this, "User data not found",
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Failed to fetch user data",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
*/