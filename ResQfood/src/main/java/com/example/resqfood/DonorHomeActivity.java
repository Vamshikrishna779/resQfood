package com.example.resqfood;
import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.tasks.OnSuccessListener;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import androidx.navigation.Navigation;

public class DonorHomeActivity extends AppCompatActivity {

    private static final int REQUEST_LOCATION_PERMISSION = 1;

    private FirebaseFirestore mFirestore;
    private FirebaseUser currentUser;
    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationClient;
    private EditText editTextSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_donor);

        mFirestore = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        showLocationSelectionDialog();
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavView);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getTitle().toString()) {
                    case "Home":
                        Navigation.findNavController(DonorHomeActivity.this, R.id.nav_host_fragment).navigate(R.id.homeFragment);
                        return true;
                    case "Account":
                        Navigation.findNavController(DonorHomeActivity.this, R.id.nav_host_fragment).navigate(R.id.accountFragment);
                        return true;
                    case "Support":
                        Navigation.findNavController(DonorHomeActivity.this, R.id.nav_host_fragment).navigate(R.id.supportFragment);
                        return true;
                    case "Settings":
                        Navigation.findNavController(DonorHomeActivity.this, R.id.nav_host_fragment).navigate(R.id.settingsFragment);
                        return true;
                }
                return false;
            }
        });
    }
    private void showLocationSelectionDialog() {
        // Check if the user already has a location
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference addressRef = FirebaseDatabase.getInstance().getReference()
                    .child("users")
                    .child(userId)
                    .child("address");

            addressRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        // User already has a location, show a message or handle accordingly
                    } else {
                        // User does not have a location, proceed to add
                        showDialogToAddLocation();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle onCancelled event
                }
            });
        }
    }

    private void showDialogToAddLocation() {
        new AlertDialog.Builder(this)
                .setTitle("Select Location")
                .setMessage("Would you like to use your current location?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Navigate to AddAddressActivity
                        Intent intent = new Intent(DonorHomeActivity.this, AddAddressActivity.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing or handle differently
                    }
                })
                .show();
    }

}
/*

        // Initialize Google Map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapView);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
    }

    private void showLocationSelectionDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Select Location")
                .setMessage("Would you like to use your current location?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Check if location permission is granted
                        if (ActivityCompat.checkSelfPermission(DonorHomeActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                                && ActivityCompat.checkSelfPermission(DonorHomeActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // Request location permission if not granted
                            ActivityCompat.requestPermissions(DonorHomeActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
                            return;
                        }
                        // Get current location
                        fusedLocationClient.getLastLocation()
                                .addOnSuccessListener(DonorHomeActivity.this, new OnSuccessListener<Location>() {
                                    @Override
                                    public void onSuccess(Location location) {
                                        if (location != null) {
                                            LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                                            googleMap.addMarker(new MarkerOptions().position(currentLatLng).title("Current Location"));
                                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
                                        }
                                    }
                                });
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Open activity to search for location
                        Intent intent = new Intent(DonorHomeActivity.this, AddAddressActivity.class);
                        startActivity(intent);
                    }
                })
                .show();
    }
}









/*
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.Navigation;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class DonorHomeActivity extends AppCompatActivity {
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private FirebaseFirestore mFirestore;
    private FirebaseUser currentUser;

    private GoogleMap googleMap;
    private MapView mapView;
    private EditText editTextSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_donor);
        showLocationSelectionDialog();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getTitle().toString()) {
                    case "Home":
                        Navigation.findNavController(DonorHomeActivity.this, R.id.nav_host_fragment).navigate(R.id.homeFragment);
                        return true;
                    case "Account":
                        Navigation.findNavController(DonorHomeActivity.this, R.id.nav_host_fragment).navigate(R.id.accountFragment);
                        return true;
                    case "Support":
                        Navigation.findNavController(DonorHomeActivity.this, R.id.nav_host_fragment).navigate(R.id.supportFragment);
                        return true;
                    case "Settings":
                        Navigation.findNavController(DonorHomeActivity.this, R.id.nav_host_fragment).navigate(R.id.settingsFragment);
                        return true;
                }
                return false;
            }
        });
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_LOCATION_PERMISSION);
    }

    private void showLocationSelectionDialog() {
        if (currentUser != null) {
            return;
        }

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_location_selection, null);
        dialogBuilder.setView(dialogView);
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapView);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                // Check for location permission
                if (ContextCompat.checkSelfPermission(DonorHomeActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Request location permission
                    requestLocationPermission(); // Corrected method call
                    return;
                }

                // If permission is granted, proceed with your map initialization
                googleMap.setMyLocationEnabled(true);
                // Other map initialization code goes here...

                // Initialize map settings and move camera to default location
                googleMap.getUiSettings().setZoomControlsEnabled(true);
                googleMap.getUiSettings().setMyLocationButtonEnabled(true);
                googleMap.getUiSettings().setMapToolbarEnabled(false);
                googleMap.getUiSettings().setZoomGesturesEnabled(true);

                // Move camera to default location
                LatLng defaultLocation = new LatLng(0, 0);
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 10));
            }
        });

        Button buttonConfirmLocation = dialogView.findViewById(R.id.buttonConfirmLocation);
        Button buttonSkip = dialogView.findViewById(R.id.buttonSkip);
        editTextSearch = dialogView.findViewById(R.id.editTextSearch); // Update for place name input

        // Set click listener for Confirm Location button
        buttonConfirmLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = getIntent().getStringExtra("userName");
                saveLocation(userName);
            }
        });

        // Set click listener for Skip button
        buttonSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle skip action
                alertDialog.dismiss(); // Close the dialog
            }
        });
    }

    private void saveLocation(String name) {
        String placeName = editTextSearch.getText().toString().trim();
        // Save location to Firestore under the logged-in user
        Map<String, Object> locationData = new HashMap<>();
        double latitude = 0; // Initialize latitude
        double longitude = 0; // Initialize longitude

        // Call your method to get latitude and longitude from place name
        LatLng locationLatLng = getLocationFromPlaceName(DonorHomeActivity.this, placeName);
        if (locationLatLng != null) {
            latitude = locationLatLng.latitude;
            longitude = locationLatLng.longitude;
        }

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String documentId = String.valueOf(System.currentTimeMillis());
        com.example.resqfood.Location location = new com.example.resqfood.Location(placeName, latitude, longitude);


        FirebaseFirestore.getInstance().collection("users")
                .document(userId)
                .collection("location")
                .document(documentId) // Specify a document ID
                .set(location)
                .addOnSuccessListener(aVoid -> {
                    // Location saved successfully
                    Toast.makeText(DonorHomeActivity.this, "Location saved successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Handle failure to save location
                    Toast.makeText(DonorHomeActivity.this, "Failed to save location: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });

        // Also save location to Realtime Database under the logged-in user
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users")
                .child(userId)
                .child("location")
                .setValue(location)
                .addOnSuccessListener(aVoid -> {
                    // Realtime Database update successful
                })
                .addOnFailureListener(e -> {
                    // Handle failure to update Realtime Database
                });
    }



    // Method to get LatLng from a place name

    private LatLng getLocationFromPlaceName(Context context, String placeName) {
        String apiKey = "AIzaSyDrx4zDvwXMPeSEhB6PMwYpLU6798mdBfY"; // Replace with your actual API key
        String apiUrl = "https://maps.googleapis.com/maps/api/place/findplacefromtext/json?" +
                "input=" + placeName +
                "&inputtype=textquery" +
                "&fields=geometry" +
                "&key=" + apiKey;

        try {
            // Make HTTP request to the Google Places API
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = connection.getInputStream();
            Scanner scanner = new Scanner(inputStream);
            scanner.useDelimiter("\\A");
            String response = scanner.hasNext() ? scanner.next() : "";

            // Parse JSON response to extract latitude and longitude
            JSONObject jsonResponse = new JSONObject(response);
            JSONArray candidates = jsonResponse.getJSONArray("candidates");
            if (candidates.length() > 0) {
                JSONObject candidate = candidates.getJSONObject(0);
                JSONObject geometry = candidate.getJSONObject("geometry");
                JSONObject location = geometry.getJSONObject("location");
                double lat = location.getDouble("lat");
                double lng = location.getDouble("lng");
                return new LatLng(lat, lng);
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return null;
    }
    }
*/