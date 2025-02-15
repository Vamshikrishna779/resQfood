package com.example.resqfood;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.Calendar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.util.List;
import java.util.Locale;
import androidx.fragment.app.DialogFragment;
import android.app.ProgressDialog;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class FoodDonationActivity extends AppCompatActivity implements OnMapReadyCallback {

    EditText editTextFoodName, editTextQuantity, editTextExpirationDate, editTextPackaging, editTextCondition,
            editTextPreferredTime, editTextLocation, editTextSpecialInstructions, editTextOrganization, editTextMobileNumber;
    ImageView imageViewFood;
    Button buttonSelectImage, buttonSubmitDonation;
    FirebaseFirestore db;
    StorageReference storageRef;
    MaterialTimePicker timePicker;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private ProgressDialog progressDialog;
    private GoogleMap mMap;
    private LatLng defaultLocation = new LatLng(0, 0);
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_donation);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        editTextFoodName = findViewById(R.id.editTextFoodName);
        editTextQuantity = findViewById(R.id.editTextQuantity);
        editTextExpirationDate = findViewById(R.id.editTextExpirationDate);
        editTextPackaging = findViewById(R.id.editTextPackaging);
        editTextCondition = findViewById(R.id.editTextCondition);
        editTextMobileNumber = findViewById(R.id.editTextMobileNumber);
        editTextPreferredTime = findViewById(R.id.editTextPreferredTime);
        editTextLocation = findViewById(R.id.editTextLocation);
        editTextSpecialInstructions = findViewById(R.id.editTextSpecialInstructions);
        editTextOrganization = findViewById(R.id.editTextOrganization);
        imageViewFood = findViewById(R.id.imageViewFood);
        buttonSelectImage = findViewById(R.id.buttonSelectImage);
        buttonSubmitDonation = findViewById(R.id.buttonSubmitDonation);

        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Submitting Donation...");
        progressDialog.setCancelable(false);

        editTextExpirationDate.setOnClickListener(v -> showDatePicker());
        editTextPreferredTime.setOnClickListener(v -> showTimePicker());
        buttonSelectImage.setOnClickListener(v -> openImageChooser());
        buttonSubmitDonation.setOnClickListener(v -> {
            String userName = getIntent().getStringExtra("userName");
            buttonSubmitDonation.setEnabled(false);
            showProgressDialog();
            uploadDonation(userName);
            //showThankYouDialog();
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Enable the My Location layer and button on the map
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        // Move the map to the user's current location
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
                    }
                });

        mMap.setOnMapClickListener(latLng -> {
            // Add a marker at the clicked location
            mMap.clear(); // Clear existing markers
            mMap.addMarker(new MarkerOptions().position(latLng));

            // Perform reverse geocoding to get address from marker position
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                if (addresses != null && addresses.size() > 0) {
                    Address address = addresses.get(0);
                    String addressLine = address.getAddressLine(0);
                    // Update address EditText
                    editTextLocation.setText(addressLine);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void showProgressDialog() {
        progressDialog.show();
    }

    private void dismissProgressDialog() {
        progressDialog.dismiss();
    }

    private void showThankYouDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thank You")
                .setMessage("Thank you for making a food donation!")
                .setPositiveButton("Close", (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showTimePicker() {
        // Initialize the MaterialTimePicker
        timePicker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(12) // Set default hour
                .setMinute(0) // Set default minute
                .setTitleText("Select Preferred Time")
                .build();

        // Set listener for positive button click
        timePicker.addOnPositiveButtonClickListener(dialog -> {
            // Get selected hour and minute
            int hour = timePicker.getHour();
            int minute = timePicker.getMinute();

            // Update EditText with selected time
            String formattedTime = String.format(Locale.getDefault(), "%02d:%02d", hour, minute);
            editTextPreferredTime.setText(formattedTime);
        });

        // Show the MaterialTimePicker
        timePicker.show(getSupportFragmentManager(), "TIME_PICKER_TAG");
    }

    private void showDatePicker() {
        final Calendar currentDate = Calendar.getInstance();
        int year = currentDate.get(Calendar.YEAR);
        int month = currentDate.get(Calendar.MONTH);
        int dayOfMonth = currentDate.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, monthOfYear, dayOfMonth1) -> {
                    Calendar expirationCalendar = Calendar.getInstance();
                    expirationCalendar.set(Calendar.YEAR, year1);
                    expirationCalendar.set(Calendar.MONTH, monthOfYear);
                    expirationCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth1);

                    editTextExpirationDate.setText(dayOfMonth1 + "/" + (monthOfYear + 1) + "/" + year1);
                }, year, month, dayOfMonth);

        datePickerDialog.show();
    }

    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imageViewFood.setImageURI(imageUri);
        }
    }/*
public class FoodDonationActivity extends AppCompatActivity {

    EditText editTextFoodName, editTextQuantity, editTextExpirationDate, editTextPackaging, editTextCondition,
            editTextPreferredTime, editTextLocation, editTextSpecialInstructions, editTextOrganization;
    ImageView imageViewFood;
    EditText editTextMobileNumber;
    Button buttonSelectImage, buttonSubmitDonation;
    FirebaseFirestore db;
    StorageReference storageRef;
    MaterialTimePicker timePicker;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_donation);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        editTextFoodName = findViewById(R.id.editTextFoodName);
        editTextQuantity = findViewById(R.id.editTextQuantity);
        editTextExpirationDate = findViewById(R.id.editTextExpirationDate);
        editTextPackaging = findViewById(R.id.editTextPackaging);
        editTextCondition = findViewById(R.id.editTextCondition);
        editTextMobileNumber = findViewById(R.id.editTextMobileNumber);
        editTextPreferredTime = findViewById(R.id.editTextPreferredTime);
        editTextLocation = findViewById(R.id.editTextLocation);
        editTextSpecialInstructions = findViewById(R.id.editTextSpecialInstructions);
        editTextOrganization = findViewById(R.id.editTextOrganization);
        imageViewFood = findViewById(R.id.imageViewFood);
        buttonSelectImage = findViewById(R.id.buttonSelectImage);
        buttonSubmitDonation = findViewById(R.id.buttonSubmitDonation);

        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Submitting Donation...");
        progressDialog.setCancelable(false);

        editTextExpirationDate.setOnClickListener(v -> showDatePicker());
        editTextPreferredTime.setOnClickListener(v -> showTimePicker());
        buttonSelectImage.setOnClickListener(v -> openImageChooser());
        buttonSubmitDonation.setOnClickListener(v -> {
            String userName = getIntent().getStringExtra("userName");
            buttonSubmitDonation.setEnabled(false);
            showProgressDialog();
            uploadDonation(userName);
            //showThankYouDialog();
        });
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        mMap.moveCamera(CameraUpdateFactory.newLatLng(defaultLocation));



            // Perform reverse geocoding to get address from marker position
            Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                if (addresses != null && addresses.size() > 0) {
                    Address address = addresses.get(0);
                    String addressLine = address.getAddressLine(0);
                    // Update address EditText
                    editTextLocation.setText(addressLine);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
    private void showProgressDialog() {
        progressDialog.show();
    }

    private void dismissProgressDialog() {
        progressDialog.dismiss();
    }
    private void showThankYouDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thank You")
                .setMessage("Thank you for making a food donation!")
                .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss(); // Close the dialog
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void showTimePicker() {
        // Initialize the MaterialTimePicker
        timePicker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(12) // Set default hour
                .setMinute(0) // Set default minute
                .setTitleText("Select Preferred Time")
                .build();

        // Set listener for positive button click
        timePicker.addOnPositiveButtonClickListener(dialog -> {
            // Get selected hour and minute
            int hour = timePicker.getHour();
            int minute = timePicker.getMinute();

            // Update EditText with selected time
            String formattedTime = String.format(Locale.getDefault(), "%02d:%02d", hour, minute);
            editTextPreferredTime.setText(formattedTime);
        });

        // Show the MaterialTimePicker
        timePicker.show(getSupportFragmentManager(), "TIME_PICKER_TAG");
    }

    private void showDatePicker() {
        final Calendar currentDate = Calendar.getInstance();
        int year = currentDate.get(Calendar.YEAR);
        int month = currentDate.get(Calendar.MONTH);
        int dayOfMonth = currentDate.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, monthOfYear, dayOfMonth1) -> {
                    Calendar expirationCalendar = Calendar.getInstance();
                    expirationCalendar.set(Calendar.YEAR, year1);
                    expirationCalendar.set(Calendar.MONTH, monthOfYear);
                    expirationCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth1);

                    editTextExpirationDate.setText(dayOfMonth1 + "/" + (monthOfYear + 1) + "/" + year1);
                }, year, month, dayOfMonth);

        datePickerDialog.show();
    }

    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imageViewFood.setImageURI(imageUri);
        }
    }*/

    private void uploadDonation(String name) {
        String foodName = editTextFoodName.getText().toString().trim();
        int quantity = Integer.parseInt(editTextQuantity.getText().toString().trim());
        String expirationDate = editTextExpirationDate.getText().toString().trim();
        String packaging = editTextPackaging.getText().toString().trim();
        String condition = editTextCondition.getText().toString().trim();
        String mobileNumber = editTextMobileNumber.getText().toString().trim();
        String preferredTime = editTextPreferredTime.getText().toString().trim();
        String location = editTextLocation.getText().toString().trim();
        String specialInstructions = editTextSpecialInstructions.getText().toString().trim();
        String organization = editTextOrganization.getText().toString().trim();

        if (foodName.isEmpty() || expirationDate.isEmpty() || imageUri == null ||
                packaging.isEmpty() || condition.isEmpty() || mobileNumber.isEmpty() || preferredTime.isEmpty() || organization.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields and select an image", Toast.LENGTH_SHORT).show();
            buttonSubmitDonation.setEnabled(true);
            dismissProgressDialog();
            return;
        }

        final StorageReference imageRef = storageRef.child("images/" + System.currentTimeMillis() + ".jpg");
        UploadTask uploadTask = imageRef.putFile(imageUri);
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                String documentId = String.valueOf(System.currentTimeMillis());
                Donation donation = new Donation(
                        foodName,
                        quantity,
                        expirationDate,
                        packaging,
                        condition,
                        mobileNumber,
                        preferredTime,
                        location,
                        specialInstructions,
                        uri.toString(),
                        name,
                        organization);

                // Save donation data under the user's document in Firestore
                FirebaseFirestore.getInstance()
                        .collection("users")
                        .document(userId)
                        .collection("donations")
                        .document(documentId)
                        .set(donation)
                        .addOnSuccessListener(aVoid -> {
                            // Save donation data under the user's node in Realtime Database
                            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                            mDatabase.child("users")
                                    .child(userId)
                                    .child("donations")
                                    .child(documentId)
                                    .setValue(donation)
                                    .addOnSuccessListener(aVoid1 -> {
                                        Toast.makeText(this, "Donation submitted successfully!", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(FoodDonationActivity.this, MyListsActivity.class);

                                        intent.putExtra("documentId", foodName);
                                        startActivity(intent);
                                        clearFields();
                                        buttonSubmitDonation.setEnabled(true);
                                        dismissProgressDialog();
                                        showThankYouDialog();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(this, "Failed to submit donation (Realtime Database)", Toast.LENGTH_SHORT).show();
                                        buttonSubmitDonation.setEnabled(true);
                                        dismissProgressDialog();
                                    });
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Failed to submit donation (Firestore)", Toast.LENGTH_SHORT).show();
                            buttonSubmitDonation.setEnabled(true);
                            dismissProgressDialog();
                        });
            });
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show();
            buttonSubmitDonation.setEnabled(true);
            dismissProgressDialog();
        });
    }

    private void clearFields() {
        editTextFoodName.setText("");
        editTextQuantity.setText("");
        editTextExpirationDate.setText("");
        editTextPackaging.setText("");
        editTextCondition.setText("");
        editTextMobileNumber.setText("");
        editTextPreferredTime.setText("");
        editTextLocation.setText("");
        editTextSpecialInstructions.setText("");
        editTextOrganization.setText("");
        imageViewFood.setImageURI(null);
        imageUri = null;
    }
}

/*
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.util.Calendar;

import android.app.DatePickerDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
public class FoodDonationActivity extends AppCompatActivity {

    EditText editTextFoodName, editTextQuantity, editTextExpirationDate;
    ImageView imageViewFood;
    Button buttonSelectImage, buttonSubmitDonation;
    FirebaseFirestore db;
    StorageReference storageRef;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_donation);

        editTextFoodName = findViewById(R.id.editTextFoodName);
        editTextQuantity = findViewById(R.id.editTextQuantity);
        editTextExpirationDate = findViewById(R.id.editTextExpirationDate);
        imageViewFood = findViewById(R.id.imageViewFood);
        buttonSelectImage = findViewById(R.id.buttonSelectImage);
        buttonSubmitDonation = findViewById(R.id.buttonSubmitDonation);

        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();
        editTextExpirationDate.setOnClickListener(v -> showDatePicker());
        buttonSelectImage.setOnClickListener(v -> openImageChooser());
        buttonSubmitDonation.setOnClickListener(v -> {
            String userName = getIntent().getStringExtra("userName");
            buttonSubmitDonation.setEnabled(false);
            uploadDonation(userName);
        });
    }

    private void showDatePicker() {
        final Calendar currentDate = Calendar.getInstance();
        int year = currentDate.get(Calendar.YEAR);
        int month = currentDate.get(Calendar.MONTH);
        int dayOfMonth = currentDate.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, monthOfYear, dayOfMonth1) -> {
                    Calendar expirationCalendar = Calendar.getInstance();
                    expirationCalendar.set(Calendar.YEAR, year1);
                    expirationCalendar.set(Calendar.MONTH, monthOfYear);
                    expirationCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth1);

                    editTextExpirationDate.setText(dayOfMonth1 + "/" + (monthOfYear + 1) + "/" + year1);
                }, year, month, dayOfMonth);

        datePickerDialog.show();
    }

    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imageViewFood.setImageURI(imageUri);
        }
    }

    private void uploadDonation(String name) {
        String foodName = editTextFoodName.getText().toString().trim();
        int quantity = Integer.parseInt(editTextQuantity.getText().toString().trim());
        String expirationDate = editTextExpirationDate.getText().toString().trim();

        if (foodName.isEmpty() || expirationDate.isEmpty() || imageUri == null) {
            Toast.makeText(this, "Please fill in all fields and select an image", Toast.LENGTH_SHORT).show();
            buttonSubmitDonation.setEnabled(true);
            return;
        }

        final StorageReference imageRef = storageRef.child("images/" + System.currentTimeMillis() + ".jpg");
        UploadTask uploadTask = imageRef.putFile(imageUri);
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                String documentId = String.valueOf(System.currentTimeMillis());
                Donation donation = new Donation(foodName, quantity, expirationDate, uri.toString(), name);

                // Save donation data under the user's document in Firestore
                FirebaseFirestore.getInstance()
                        .collection("users")
                        .document(userId)
                        .collection("donations")
                        .document(documentId)
                        .set(donation)
                        .addOnSuccessListener(aVoid -> {
                            // Save donation data under the user's node in Realtime Database
                            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                            mDatabase.child("users")
                                    .child(userId)
                                    .child("donations")
                                    .child(documentId)
                                    .setValue(donation)
                                    .addOnSuccessListener(aVoid1 -> {
                                        Toast.makeText(this, "Donation submitted successfully!", Toast.LENGTH_SHORT).show();
                                        clearFields();
                                        buttonSubmitDonation.setEnabled(true);
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(this, "Failed to submit donation (Realtime Database)", Toast.LENGTH_SHORT).show();
                                        buttonSubmitDonation.setEnabled(true);
                                    });
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Failed to submit donation (Firestore)", Toast.LENGTH_SHORT).show();
                            buttonSubmitDonation.setEnabled(true);
                        });
            });
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show();
            buttonSubmitDonation.setEnabled(true);
        });
    }


    private void clearFields() {
        editTextFoodName.setText("");
        editTextQuantity.setText("");
        editTextExpirationDate.setText("");
        imageViewFood.setImageURI(null);
        imageUri = null;
    }
}*/