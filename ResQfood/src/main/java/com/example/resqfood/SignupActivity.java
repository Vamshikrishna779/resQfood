package com.example.resqfood;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
public class SignupActivity extends AppCompatActivity {

    private EditText editTextName, editTextEmail, editTextPassword, editTextOrganization, editTextContact;
    private Spinner spinnerRole;
    private Button buttonSignup;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        TextView textViewLogin1 = findViewById(R.id.textViewLogin1);
        textViewLogin1.setOnClickListener(v -> {
            // Navigate to the login activity
            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        editTextName = findViewById(R.id.editTextName1);
        editTextEmail = findViewById(R.id.editTextEmail1);
        editTextPassword = findViewById(R.id.editTextPassword1);
        spinnerRole = findViewById(R.id.spinnerRole);
        editTextOrganization = findViewById(R.id.editTextOrganization);
        TextView textViewOrganization = findViewById(R.id.textViewOrganization);

        spinnerRole.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedRole = parentView.getItemAtPosition(position).toString();
                if (selectedRole.equals("Donor")) {
                    // Show organization name field
                    editTextOrganization.setVisibility(View.VISIBLE);
                    textViewOrganization.setVisibility(View.VISIBLE);
                } else {
                    // Hide organization name field
                    editTextOrganization.setVisibility(View.GONE);
                    textViewOrganization.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing
            }
        });

        List<String> mList = Arrays.asList("Recipient", "Donor" );
        ArrayAdapter<String> mArrayAdapter = new ArrayAdapter<>(this, R.layout.spinner_list, mList);
        mArrayAdapter.setDropDownViewResource(R.layout.spinner_list);

        spinnerRole.setAdapter(mArrayAdapter);

        buttonSignup = findViewById(R.id.buttonSignup);

        buttonSignup.setOnClickListener(v -> {
            String email = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();
            String name = editTextName.getText().toString().trim();
            String role = spinnerRole.getSelectedItem().toString().trim();
            String organization = editTextOrganization.getText().toString().trim();

            if (validateForm(email, password, name, role, organization)) {
                signupUser(email, password, name, role, organization);
            }
        });
    }

    // Validate form fields
    private boolean validateForm(String email, String password, String name, String role, String organization) {
        if (TextUtils.isEmpty(email)) {
            editTextEmail.setError("Email is required");
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError("Password is required");
            return false;
        }

        if (TextUtils.isEmpty(name)) {
            editTextName.setError("Name is required");
            return false;
        }

        if (role.equals("Donor") && TextUtils.isEmpty(organization)) {
            editTextOrganization.setError("Organization name is required for donors");
            return false;
        }

        return true;
    }
    private void signupUser(String email, String password, final String name, final String role,final String organization) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String userId = mAuth.getCurrentUser().getUid();
                            Map<String, Object> user = new HashMap<>();
                            user.put("name", name);
                            user.put("email", email);
                            user.put("role", role);
                            user.put("contact",null);
                            user.put("isLoggedIn", false);

                            if (role.equals("Donor")) {
                                user.put("organization", organization);
                            }

                            // Save user data to Firestore
                            mFirestore.collection("users").document(userId).set(user)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> firestoreTask) {
                                            if (firestoreTask.isSuccessful()) {
                                                // Save user data to Realtime Database
                                                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                                                mDatabase.child("users").child(userId).setValue(user)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> realtimeTask) {
                                                                if (realtimeTask.isSuccessful()) {
                                                                    Toast.makeText(SignupActivity.this, "User signed up successfully", Toast.LENGTH_SHORT).show();
                                                                    startLoginActivity(name);
                                                                } else {
                                                                    Toast.makeText(SignupActivity.this, "Failed to sign up user (Realtime Database)", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                            } else {
                                                Toast.makeText(SignupActivity.this, "Failed to sign up user (Firestore)", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        } else {
                            Toast.makeText(SignupActivity.this, "Sign up failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void startLoginActivity(String name) {
        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
        intent.putExtra("userName", name);
        startActivity(intent);
        finish();
    }


    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
  /*  private void signupUser(String email, String password, final String name, final String role, final String organization) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            String userId = firebaseUser.getUid();
                            Map<String, Object> user = new HashMap<>();
                            user.put("name", name);
                            user.put("email", email);
                            user.put("role", role);
                            user.put("isLoggedIn", false);

                            if (role.equals("Donor")) {
                                user.put("organization", organization);
                            }

                            // Save user data to Firestore
                            mFirestore.collection("users").document(userId).set(user)
                                    .addOnCompleteListener(firestoreTask -> {
                                        if (firestoreTask.isSuccessful()) {
                                            // Save user data to Realtime Database
                                            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                                            mDatabase.child("users").child(userId).setValue(user)
                                                    .addOnCompleteListener(realtimeTask -> {
                                                        if (realtimeTask.isSuccessful()) {
                                                            Toast.makeText(SignupActivity.this, "User signed up successfully", Toast.LENGTH_SHORT).show();
                                                            if (role.equals("Donor")) {
                                                                startDonationDetailActivity(userId);
                                                            } else {
                                                                startLoginActivity(name);
                                                            }
                                                        } else {
                                                            showError("Failed to sign up user (Realtime Database)");
                                                        }
                                                    });
                                        } else {
                                            showError("Failed to sign up user (Firestore)");
                                        }
                                    });
                        } else {
                            showError("Failed to sign up user");
                        }
                    } else {
                        showError("Sign up failed: " + task.getException().getMessage());
                    }
                });
    }
/*
    private void startLoginActivity(String name) {
        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
        intent.putExtra("userName", name);
        startActivity(intent);
        finish();
    }


    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}





/*
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    private EditText editTextName, editTextEmail, editTextPassword;
    private Spinner spinnerRole;
    private Button buttonSignup;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        TextView textViewLogin1 = findViewById(R.id.textViewLogin1);
        textViewLogin1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the login activity
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        editTextName = findViewById(R.id.editTextName1);
        editTextEmail = findViewById(R.id.editTextEmail1);
        editTextPassword = findViewById(R.id.editTextPassword1);
        spinnerRole = findViewById(R.id.spinnerRole);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.roles_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRole.setAdapter(adapter);

        buttonSignup = findViewById(R.id.buttonSignup);

        buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextEmail.getText().toString();
                String password = editTextPassword.getText().toString();
                String name = editTextName.getText().toString();
                String role = spinnerRole.getSelectedItem().toString();
                signupUser(email, password, name, role);
            }
        });
    }


    private void signupUser(String email, String password, final String name, final String role) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String userId = mAuth.getCurrentUser().getUid();
                            Map<String, Object> user = new HashMap<>();
                            user.put("name", name);
                            user.put("email", email);
                            user.put("role", role);
                            user.put("isLoggedIn", false);

                            // Save user data to Firestore
                            mFirestore.collection("users").document(userId).set(user)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> firestoreTask) {
                                            if (firestoreTask.isSuccessful()) {
                                                // Save user data to Realtime Database
                                                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                                                mDatabase.child("users").child(userId).setValue(user)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> realtimeTask) {
                                                                if (realtimeTask.isSuccessful()) {
                                                                    Toast.makeText(SignupActivity.this, "User signed up successfully", Toast.LENGTH_SHORT).show();
                                                                    startLoginActivity(name);
                                                                } else {
                                                                    Toast.makeText(SignupActivity.this, "Failed to sign up user (Realtime Database)", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                            } else {
                                                Toast.makeText(SignupActivity.this, "Failed to sign up user (Firestore)", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        } else {
                            Toast.makeText(SignupActivity.this, "Sign up failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void startLoginActivity(String name) {
        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
        intent.putExtra("userName", name);
        startActivity(intent);
        finish();
    }
}
*/