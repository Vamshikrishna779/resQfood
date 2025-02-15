package com.example.resqfood;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;



public class RecipientHomeActivity extends AppCompatActivity {
    private static final int REQUEST_LOCATION_PERMISSION = 1;

    private FirebaseFirestore mFirestore;
    private FirebaseUser currentUser;
    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationClient;
    private EditText editTextSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_recipient);

        mFirestore = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        showLocationSelectionDialog();
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavViewView);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getTitle().toString()) {
                    case "Home":
                        Navigation.findNavController(RecipientHomeActivity.this, R.id.nav_host_fragment).navigate(R.id.homeFragment);
                        return true;
                    case "Account":
                        Navigation.findNavController(RecipientHomeActivity.this, R.id.nav_host_fragment).navigate(R.id.accountFragmentt);
                        return true;
                    case "Support":
                        Navigation.findNavController(RecipientHomeActivity.this, R.id.nav_host_fragment).navigate(R.id.supportFragment);
                        return true;
                    case "Settings":
                        Navigation.findNavController(RecipientHomeActivity.this, R.id.nav_host_fragment).navigate(R.id.settingsFragment);
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
                        Intent intent = new Intent(RecipientHomeActivity.this, AddAddressActivity.class);
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
