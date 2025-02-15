package com.example.resqfood;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener; // Import your DonationForm class

public class CollectActivity extends AppCompatActivity {
    private static final String TAG = "CollectActivity";
    private EditText editTextRecipientName;
    private EditText editTextRecipientContact;
    private EditText editTextPickupLocation;
    private EditText editTextPreferredDateTime;
    private EditText editTextSpecialInstructions;
    private EditText editTextStatus;
    private CheckBox checkBoxConfirmation;
    private Button buttonSubmit;
    private ProgressDialog progressDialog;
    private String foodName;
    private DatabaseReference donationsRef;
    private DatabaseReference usersRef;
    private String recipientId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect);

        // Initialize views
        editTextRecipientName = findViewById(R.id.editTextRecipientName);
        editTextRecipientContact = findViewById(R.id.editTextRecipientContact);
        editTextPickupLocation = findViewById(R.id.editTextPickupLocation);
        editTextPreferredDateTime = findViewById(R.id.editTextPreferredDateTime);
        editTextSpecialInstructions = findViewById(R.id.editTextSpecialInstructions);
        editTextStatus = findViewById(R.id.editTextStatus);
        checkBoxConfirmation = findViewById(R.id.checkBoxConfirmation);
        buttonSubmit = findViewById(R.id.buttonSubmit);

        // Retrieve foodName passed from the previous activity
        foodName = getIntent().getStringExtra("foodName");

        // Initialize Firebase Database reference
        usersRef = FirebaseDatabase.getInstance().getReference().child("users");

        // Set OnClickListener for Submit button
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateForm()) {
                    // Save form data under the selected item
                    saveFormData();
                }
            }
        });
    }

    private boolean validateForm() {
        String recipientName = editTextRecipientName.getText().toString().trim();
        String recipientContact = editTextRecipientContact.getText().toString().trim();
        String pickupLocation = editTextPickupLocation.getText().toString().trim();
        String preferredDateTime = editTextPreferredDateTime.getText().toString().trim();
        boolean confirmation = checkBoxConfirmation.isChecked();

        // Validate form fields
        if (recipientName.isEmpty() || recipientContact.isEmpty() || pickupLocation.isEmpty() || preferredDateTime.isEmpty()) {
            Toast.makeText(this, "Please fill out all required fields", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!confirmation) {
            Toast.makeText(this, "Please confirm collection terms", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void saveFormData() {
        // Show progress dialog while submitting
        progressDialog = new ProgressDialog(CollectActivity.this);
        progressDialog.setMessage("Submitting form...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String recipientName = editTextRecipientName.getText().toString().trim();
        String recipientContact = editTextRecipientContact.getText().toString().trim();
        String pickupLocation = editTextPickupLocation.getText().toString().trim();
        String preferredDateTime = editTextPreferredDateTime.getText().toString().trim();
        String specialInstructions = editTextSpecialInstructions.getText().toString().trim();
        String status = editTextStatus.getText().toString().trim();
        boolean confirmation = checkBoxConfirmation.isChecked();
        DonationForm donationForm = new DonationForm(recipientName, recipientContact, pickupLocation, preferredDateTime, specialInstructions, status);

        String foodName = getIntent().getStringExtra("foodName");

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    DatabaseReference userDonationsRef = userSnapshot.child("recipients").getRef();
                    DatabaseReference newDonationRef = userDonationsRef.push();
                    String recipientId = "Recipient_" + newDonationRef.getKey()+"_"+ foodName;

/* userDonationsRef.orderByChild("foodName").equalTo(foodName)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                                    for (DataSnapshot donationSnapshot : dataSnapshot1.getChildren()) {*/
                                        userDonationsRef.child(recipientId).setValue(donationForm)
                                                .addOnSuccessListener(aVoid -> {
                                                    progressDialog.dismiss();
                                                    showSuccessDialog();
                                                    Toast.makeText(CollectActivity.this, "Donation submitted successfully!", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(CollectActivity.this, NotificationActivity.class);

                                                    intent.putExtra("recipientId", recipientId);
                                                    intent.putExtra("recipientName", recipientName);
                                                    startActivity(intent);
                                                    finish();
                                                })
                                                .addOnFailureListener(e -> {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(CollectActivity.this, "Failed to submit (Realtime Database)", Toast.LENGTH_SHORT).show();
                                                });
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    progressDialog.dismiss();
                                    Toast.makeText(CollectActivity.this, "Failed to retrieve donations", Toast.LENGTH_SHORT).show();
                                    Log.e(TAG, "Error retrieving donations", databaseError.toException());
                                }
                            });
                }



    private void showSuccessDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Success")
                .setMessage("Thank you! Your form has been submitted successfully.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Optionally, navigate back to DonationDetailActivity or perform any other action
                        finish(); // Close the activity
                    }
                })
                .setCancelable(false)
                .show();
    }


/*
    private void saveFormData() {
        // Show progress dialog while submitting
        progressDialog = new ProgressDialog(CollectActivity.this);
        progressDialog.setMessage("Submitting form...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String recipientName = editTextRecipientName.getText().toString().trim();
        String recipientContact = editTextRecipientContact.getText().toString().trim();
        String pickupLocation = editTextPickupLocation.getText().toString().trim();
        String preferredDateTime = editTextPreferredDateTime.getText().toString().trim();
        String specialInstructions = editTextSpecialInstructions.getText().toString().trim();
        boolean confirmation = checkBoxConfirmation.isChecked();
        DonationForm donationForm = new DonationForm(recipientName, recipientContact, pickupLocation, preferredDateTime, specialInstructions);
        // Current user is the donor

        // Save the donation under the specific structure under the donor's account
        DatabaseReference userDonationsRef = usersRef.child(userId).child("donations").child(foodName).push();
        userDonationsRef.setValue(donationForm)
                .addOnSuccessListener(aVoid -> {
                    progressDialog.dismiss();
                    showSuccessDialog();
                    Toast.makeText(this, "Donation submitted successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Failed to submit (Realtime Database)", Toast.LENGTH_SHORT).show();
                });
    }


    private void showSuccessDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Success")
                .setMessage("Thank you! Your form has been submitted successfully.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Optionally, navigate back to DonationDetailActivity or perform any other action
                        finish(); // Close the activity
                    }
                })
                .setCancelable(false)
                .show();
    }
*/
    public class DonationForm {

        private String recipientName;
        private String recipientContact;
        private String pickupLocation;
        private String preferredDateTime;
        private String specialInstructions;
        private String status;
    private String recipientId;


    public DonationForm() {
            // Default constructor required for calls to DataSnapshot.getValue(DonationForm.class)
        }

        public DonationForm(String recipientName, String recipientContact, String pickupLocation, String preferredDateTime, String specialInstructions, String status) {
            this.recipientId = recipientId;
            this.recipientName = recipientName;
            this.recipientContact = recipientContact;
            this.pickupLocation = pickupLocation;
            this.preferredDateTime = preferredDateTime;
            this.specialInstructions = specialInstructions;
            this.status = status;
        }

        public String getRecipientName() {
            return recipientName;
        }

        public String getRecipientContact() {
            return recipientContact;
        }

        public String getPickupLocation() {
            return pickupLocation;
        }

        public String getPreferredDateTime() {
            return preferredDateTime;
        }

        public String getSpecialInstructions() {
            return specialInstructions;
        }

        public String getStatus(){return  status;}
    }
}

/*
    public class DonationForm {
        private String recipientName;
        private String recipientContact;
        private String pickupLocation;
        private String preferredDateTime;
        private String specialInstructions;

        public DonationForm() {
            // Default constructor required for Firebase
        }

        public DonationForm(String recipientName, String recipientContact, String pickupLocation, String preferredDateTime, String specialInstructions) {
            this.recipientName = recipientName;
            this.recipientContact = recipientContact;
            this.pickupLocation = pickupLocation;
            this.preferredDateTime = preferredDateTime;
            this.specialInstructions = specialInstructions;
        }

        // Getters and setters for all fields
        public String getRecipientName() {
            return recipientName;
        }

        public void setRecipientName(String recipientName) {
            this.recipientName = recipientName;
        }

        public String getRecipientContact() {
            return recipientContact;
        }

        public void setRecipientContact(String recipientContact) {
            this.recipientContact = recipientContact;
        }

        public String getPickupLocation() {
            return pickupLocation;
        }

        public void setPickupLocation(String pickupLocation) {
            this.pickupLocation = pickupLocation;
        }

        public String getPreferredDateTime() {
            return preferredDateTime;
        }

        public void setPreferredDateTime(String preferredDateTime) {
            this.preferredDateTime = preferredDateTime;
        }

        public String getSpecialInstructions() {
            return specialInstructions;
        }

        public void setSpecialInstructions(String specialInstructions) {
            this.specialInstructions = specialInstructions;
        }
    }
}


/*
public class CollectActivity extends AppCompatActivity {

    EditText editTextRecipientName, editTextRecipientContact, editTextPickupLocation,
            editTextPreferredDateTime, editTextSpecialInstructions;
    Button buttonSubmit;
    CheckBox checkBoxConfirmation;

    DatabaseReference mDatabase;
    ProgressDialog progressDialog;
    private String donationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Submitting...");
        progressDialog.setCancelable(false);

        editTextRecipientName = findViewById(R.id.editTextRecipientName);
        editTextRecipientContact = findViewById(R.id.editTextRecipientContact);
        editTextPickupLocation = findViewById(R.id.editTextPickupLocation);
        editTextPreferredDateTime = findViewById(R.id.editTextPreferredDateTime);
        editTextSpecialInstructions = findViewById(R.id.editTextSpecialInstructions);
        checkBoxConfirmation = findViewById(R.id.checkBoxConfirmation);

        buttonSubmit = findViewById(R.id.buttonSubmit);
        donationId = getIntent().getStringExtra("donationId");

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateForm()) {
                    // Check if the checkbox is checked
                    if (checkBoxConfirmation.isChecked()) {
                        // Disable the submit button to prevent multiple submissions
                        buttonSubmit.setEnabled(false);
                        saveRecipientInformation();
                    } else {
                        // If checkbox is not checked, display error message
                        Toast.makeText(CollectActivity.this, "Please agree to the terms", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private boolean validateForm() {
        String recipientName = editTextRecipientName.getText().toString().trim();
        String recipientContact = editTextRecipientContact.getText().toString().trim();
        String pickupLocation = editTextPickupLocation.getText().toString().trim();
        String preferredDateTime = editTextPreferredDateTime.getText().toString().trim();

        if (recipientName.isEmpty()) {
            editTextRecipientName.setError("Recipient name is required");
            return false;
        }

        if (recipientContact.isEmpty()) {
            editTextRecipientContact.setError("Contact number is required");
            return false;
        }

        if (pickupLocation.isEmpty()) {
            editTextPickupLocation.setError("Pickup location is required");
            return false;
        }

        if (preferredDateTime.isEmpty()) {
            editTextPreferredDateTime.setError("Preferred date and time are required");
            return false;
        }

        return true;
    }

    private void saveRecipientInformation() {
        progressDialog.show();

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String documentId = String.valueOf(System.currentTimeMillis());
        String recipientName = editTextRecipientName.getText().toString().trim();
        String recipientContact = editTextRecipientContact.getText().toString().trim();
        String pickupLocation = editTextPickupLocation.getText().toString().trim();
        String preferredDateTime = editTextPreferredDateTime.getText().toString().trim();
        String specialInstructions = editTextSpecialInstructions.getText().toString().trim();

        DatabaseReference recipientRef = FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(userId)
                .child("recipient")
                .child(documentId);

        Map<String, Object> recipientData = new HashMap<>();
        recipientData.put("name", recipientName);
        recipientData.put("contact", recipientContact);
        recipientData.put("pickupLocation", pickupLocation);
        recipientData.put("preferredDateTime", preferredDateTime);
        recipientData.put("specialInstructions", specialInstructions);

        // Save the recipient information under a unique document ID
        recipientRef.setValue(recipientData)
                .addOnSuccessListener(aVoid -> {
                    progressDialog.dismiss();
                    showThankYouDialog();
                    retrieveDonorInformation(documentId); // Pass the document ID to retrieveDonorInformation
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(CollectActivity.this, "Failed to save recipient information", Toast.LENGTH_SHORT).show();
                    // Re-enable the submit button
                    buttonSubmit.setEnabled(true);
                });
    }

    private void retrieveDonorInformation(String documentId) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String donorId = currentUser.getUid();
            mDatabase.child("users").child(donorId).child("recipient").child(documentId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String recipientName = dataSnapshot.child("name").getValue(String.class);
                        String recipientContact = dataSnapshot.child("contact").getValue(String.class);
                        String pickupLocation = dataSnapshot.child("pickupLocation").getValue(String.class);
                        String preferredDateTime = dataSnapshot.child("preferredDateTime").getValue(String.class);
                        String specialInstructions = dataSnapshot.child("specialInstructions").getValue(String.class);

                        Intent intent = new Intent(CollectActivity.this, NotificationActivity.class);
                        intent.putExtra("recipientName", recipientName);
                        intent.putExtra("recipientContact", recipientContact);
                        intent.putExtra("pickupLocation", pickupLocation);
                        intent.putExtra("preferredDateTime", preferredDateTime);
                        intent.putExtra("specialInstructions", specialInstructions);
                        startActivity(intent);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle onCancelled event if needed
                }
            });
        }
    }

    private void showThankYouDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thank You")
                .setMessage("Your information has been submitted successfully!")
                .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}


/*
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;
import com.google.firebase.messaging.RemoteMessage.Notification;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;
import com.google.firebase.messaging.NotificationParams;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnFailureListener;


import java.util.HashMap;
import java.util.Map;

public class CollectActivity extends AppCompatActivity {

    EditText editTextRecipientName, editTextRecipientContact, editTextPickupLocation,
            editTextPreferredDateTime, editTextSpecialInstructions;
    Button buttonSubmit;
    CheckBox checkBoxCofirmation;

    DatabaseReference mDatabase;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Submitting...");
        progressDialog.setCancelable(false);

        editTextRecipientName = findViewById(R.id.editTextRecipientName);
        editTextRecipientContact = findViewById(R.id.editTextRecipientContact);
        editTextPickupLocation = findViewById(R.id.editTextPickupLocation);
        editTextPreferredDateTime = findViewById(R.id.editTextPreferredDateTime);
        editTextSpecialInstructions = findViewById(R.id.editTextSpecialInstructions);
        CheckBox checkBoxConfirmation = findViewById(R.id.checkBoxConfirmation);

        buttonSubmit = findViewById(R.id.buttonSubmit);

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateForm()) {
                    // Check if the checkbox is checked
                    if (checkBoxConfirmation.isChecked()) {
                        // Disable the submit button to prevent multiple submissions
                        buttonSubmit.setEnabled(false);
                        saveRecipientInformation();
                    } else {
                        // If checkbox is not checked, display error message
                        Toast.makeText(CollectActivity.this, "Please agree to the terms", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    private boolean validateForm() {
        String recipientName = editTextRecipientName.getText().toString().trim();
        String recipientContact = editTextRecipientContact.getText().toString().trim();
        String pickupLocation = editTextPickupLocation.getText().toString().trim();
        String preferredDateTime = editTextPreferredDateTime.getText().toString().trim();

        if (recipientName.isEmpty()) {
            editTextRecipientName.setError("Recipient name is required");
            return false;
        }

        if (recipientContact.isEmpty()) {
            editTextRecipientContact.setError("Contact number is required");
            return false;
        }

        if (pickupLocation.isEmpty()) {
            editTextPickupLocation.setError("Pickup location is required");
            return false;
        }

        if (preferredDateTime.isEmpty()) {
            editTextPreferredDateTime.setError("Preferred date and time are required");
            return false;
        }

        return true;
    }

    private void saveRecipientInformation() {
        progressDialog.show();

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String documentId = String.valueOf(System.currentTimeMillis());
        String recipientId = String.valueOf(System.currentTimeMillis());
        String recipientName = editTextRecipientName.getText().toString().trim();
        String recipientContact = editTextRecipientContact.getText().toString().trim();
        String pickupLocation = editTextPickupLocation.getText().toString().trim();
        String preferredDateTime = editTextPreferredDateTime.getText().toString().trim();
        String specialInstructions = editTextSpecialInstructions.getText().toString().trim();

        Map<String, Object> recipientData = new HashMap<>();
        recipientData.put("name", recipientName);
        recipientData.put("contact", recipientContact);
        recipientData.put("pickupLocation", pickupLocation);
        recipientData.put("preferredDateTime", preferredDateTime);
        recipientData.put("specialInstructions", specialInstructions);

        // Save the recipient information under the donor's node
        mDatabase.child("users").child(userId).child("donations").child(documentId).child(recipientId).setValue(recipientData)
                .addOnSuccessListener(aVoid -> {
                    progressDialog.dismiss();
                    showThankYouDialog();

                    // Retrieve donor information
                    retrieveDonorInformation();
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(CollectActivity.this, "Failed to save recipient information", Toast.LENGTH_SHORT).show();
                    // Re-enable the submit button
                    buttonSubmit.setEnabled(true);
                });
    }

    private void retrieveDonorInformation() {
        String donorId = getIntent().getStringExtra("donorId");
        mDatabase.child("users").child(donorId).child("donations").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Get donor's FCM token
                    String donorFCMToken = dataSnapshot.child("fcmToken").getValue(String.class);
                    if (donorFCMToken != null) {
                        // Send notification to the donor
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle onCancelled event if needed
            }
        });
    }


    private void showThankYouDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thank You")
                .setMessage("Your information has been submitted successfully!")
                .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
*/