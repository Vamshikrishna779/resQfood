
package com.example.resqfood;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class ProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView profileImageView;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
    private StorageReference storageRef;
    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference().child("users");
        storageRef = FirebaseStorage.getInstance().getReference();

        profileImageView = findViewById(R.id.profileImageView);

        Button buttonEditPhoto = findViewById(R.id.buttonEditPhoto);
        buttonEditPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagePicker();
            }
        });

        Button saveButton = findViewById(R.id.Change);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChanges();
            }
        });

        loadUserData();
    }

    private void loadUserData() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            userRef.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String name = snapshot.child("name").getValue(String.class);
                        String email = snapshot.child("email").getValue(String.class);
                        String role = snapshot.child("role").getValue(String.class);
                        String organization = snapshot.child("organization").getValue(String.class);
                        String contact = snapshot.child("contact").getValue(String.class);
                        String profileImage = snapshot.child("profileImage").getValue(String.class);

                        EditText editTextName = findViewById(R.id.editTextName);
                        EditText editTextEmail = findViewById(R.id.editTextEmail);
                        EditText editTextOrganization = findViewById(R.id.editTextOrganization);
                        EditText editTextContact = findViewById(R.id.editTextContact);
                        editTextName.setText(name);
                        editTextEmail.setText(email);
                        editTextOrganization.setText(organization);
                        editTextContact.setText(contact);
                        // Set the email field to be non-editable
                        editTextEmail.setEnabled(false);
                        TextView textViewRole = findViewById(R.id.textViewRole);
                        ImageView profileImageView = findViewById(R.id.profileImageView);

                        textViewRole.setText(role);


                        if (profileImage != null) {
                            // Load the profile image using Glide or any other image loading library
                            Glide.with(ProfileActivity.this)
                                    .load(profileImage)
                                    .into(profileImageView);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle database error
                }
            });
        }
    }

    private void openImagePicker() {
        // Create an intent to either pick an image from the gallery or capture a new image using the camera
        Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Check if there is a camera activity available
        PackageManager packageManager = getPackageManager();
        boolean hasCamera = cameraIntent.resolveActivity(packageManager) != null;

        // Create a chooser intent to let the user pick between gallery and camera
        Intent chooserIntent = Intent.createChooser(pickIntent, "Select Image");
        if (hasCamera) {
            // Add the camera intent as an extra option
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{cameraIntent});
        }

        // Start the chooser activity
        startActivityForResult(chooserIntent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PICK_IMAGE_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImagePicker();
            } else {
                Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PICK_IMAGE_REQUEST) {
                if (data != null && data.getData() != null) {
                    selectedImageUri = data.getData();
                    profileImageView.setImageURI(selectedImageUri);
                }
            }
        }
    }


    private void saveChanges() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            EditText editTextName = findViewById(R.id.editTextName);
            EditText editTextOrg = findViewById(R.id.editTextOrganization);
            EditText editTextContact = findViewById(R.id.editTextContact);

            String newName = editTextName.getText().toString().trim();
            String newOrg = editTextOrg.getText().toString().trim();
            String newContact = editTextContact.getText().toString().trim();


            userRef.child(currentUser.getUid()).child("name").setValue(newName);
            userRef.child(currentUser.getUid()).child("organization").setValue(newOrg);
            userRef.child(currentUser.getUid()).child("contact").setValue(newContact);

            if (selectedImageUri != null) {
                saveImageToDatabase(selectedImageUri);
            } else {
                Toast.makeText(this, "Changes saved successfully!", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void updateEmailInDatabase(String newEmail) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            userRef.child(currentUser.getUid()).child("email").setValue(newEmail)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Email updated successfully in Firebase Realtime Database
                            Toast.makeText(ProfileActivity.this, "Email updated successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            // Email update failed, handle the error
                            Toast.makeText(ProfileActivity.this, "Failed to update email in Database", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
    private void saveImageToDatabase(Uri imagePath) {
        final StorageReference imageRef = storageRef.child("profileImages/" + mAuth.getCurrentUser().getUid() + ".jpg");
        UploadTask uploadTask = imageRef.putFile(imagePath);
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                userRef.child(mAuth.getCurrentUser().getUid()).child("profileImage").setValue(uri.toString())
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(this, "Changes saved successfully!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(this, "Error saving image to database!", Toast.LENGTH_SHORT).show();
                            }
                        });
            });
        });
    }
}

/*package com.example.resqfood;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.storage.FirebaseStorage;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class ProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView profileImageView;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference().child("users");

        profileImageView = findViewById(R.id.profileImageView);

        Button buttonEditPhoto = findViewById(R.id.buttonEditPhoto);
        buttonEditPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagePicker();
            }
        });

        loadUserData();
    }

    private void loadUserData() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            userRef.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String name = snapshot.child("name").getValue(String.class);
                        String email = snapshot.child("email").getValue(String.class);
                        String role = snapshot.child("role").getValue(String.class);
                        String profileImage = snapshot.child("profileImage").getValue(String.class);

                        EditText editTextName = findViewById(R.id.editTextName);
                        EditText editTextEmail = findViewById(R.id.editTextEmail);
                        TextView textViewRole = findViewById(R.id.textViewRole);
                        ImageView profileImageView = findViewById(R.id.profileImageView);

                        editTextName.setText(name);
                        editTextEmail.setText(email);
                        textViewRole.setText(role);

                        if (profileImage != null) {
                            // Load the profile image using Glide or any other image loading library
                            Glide.with(ProfileActivity.this)
                                    .load(profileImage)
                                    .into(profileImageView);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle database error
                }
            });
        }
    }


    private void openImagePicker() {
        // Create an intent to either pick an image from the gallery or capture a new image using the camera
        Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

// Check if there is a camera activity available
        PackageManager packageManager = getPackageManager();
        boolean hasCamera = cameraIntent.resolveActivity(packageManager) != null;

// Create a chooser intent to let the user pick between gallery and camera
        Intent chooserIntent = Intent.createChooser(pickIntent, "Select Image");
        if (hasCamera) {
            // Add the camera intent as an extra option
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{cameraIntent});
        }

// Start the chooser activity
        startActivityForResult(chooserIntent, PICK_IMAGE_REQUEST);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PICK_IMAGE_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImagePicker();
            } else {
                Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PICK_IMAGE_REQUEST) {
                if (data != null && data.getData() != null) {
                    Uri selectedImageUri = data.getData();
                    profileImageView.setImageURI(selectedImageUri);
                    saveImageToDatabase(selectedImageUri.toString());
                }
            }
        }
    }

    private void saveImageToDatabase(String imagePath) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();

            // Upload image to Firebase Storage
            StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("profile_images").child(uid);
            storageRef.putFile(Uri.parse(imagePath))
                    .addOnSuccessListener(taskSnapshot -> {
                        // Get the uploaded image URL
                        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            // Save the image URL to Realtime Database
                            String imageUrl = uri.toString();
                            userRef.child(uid).child("profileImage").setValue(imageUrl)
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(this, "Image saved to database!", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(this, "Error saving image to database!", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error uploading image to storage!", Toast.LENGTH_SHORT).show();
                    });
        }
    }

}*/
