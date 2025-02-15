package com.example.resqfood;
import android.Manifest;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import androidx.core.app.ActivityCompat;


public class AddAddressActivity extends AppCompatActivity {

    private EditText autoCompleteTextView, editTextCity, editTextState, editTextPostalCode;
    private Button buttonSaveAddress;
    private Button buttonSaveChanges, buttonGetCurrentLocation;
    private DatabaseReference mDatabase;
    private LocationManager locationManager;
    private LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        autoCompleteTextView = findViewById(R.id.autoCompleteTextView);
        editTextCity = findViewById(R.id.editTextCity);
        editTextState = findViewById(R.id.editTextState);
        editTextPostalCode = findViewById(R.id.editTextPostalCode);
        buttonSaveAddress = findViewById(R.id.buttonSaveAddress);
        buttonSaveChanges = findViewById(R.id.buttonSaveChanges);
        buttonGetCurrentLocation = findViewById(R.id.buttonGetCurrentLocation);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        loadAddressDetails();

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                Toast.makeText(AddAddressActivity.this, "Latitude: " + latitude + ", Longitude: " + longitude, Toast.LENGTH_SHORT).show();
                getAddressFromLocation(latitude, longitude);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
                Toast.makeText(AddAddressActivity.this, "Location provider enabled: " + provider, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onProviderDisabled(String provider) {
                Toast.makeText(AddAddressActivity.this, "Location provider disabled: " + provider, Toast.LENGTH_SHORT).show();
            }

        };

        requestLocationUpdates();
        getCurrentLocation();

        buttonSaveAddress.setOnClickListener(v -> saveAddressToFirebase());

        buttonGetCurrentLocation.setOnClickListener(v -> getCurrentLocation());

        buttonSaveChanges.setOnClickListener(v -> saveChangesToFirebase());

        // Check if the activity was launched with an intent to edit existing address
        if (getIntent().hasExtra("edit_address")) {
            // Load the existing address details
            loadAddressDetails();
            // Change the button text
            buttonSaveAddress.setText("Save Changes");
        } else {
            // Request location updates when the activity is created for adding a new address
            requestLocationUpdates();
        }
    }

    private void requestLocationUpdates() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PERMISSION_GRANTED) {
            requestLocationPermission();
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }

    private void getCurrentLocation() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PERMISSION_GRANTED) {
            requestLocationPermission();
            return;
        }
        /*Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (lastKnownLocation != null) {
            double latitude = lastKnownLocation.getLatitude();
            double longitude = lastKnownLocation.getLongitude();
            getAddressFromLocation(latitude, longitude);
        }*/ else {
            Toast.makeText(this, "Unable to retrieve current location add manually", Toast.LENGTH_SHORT).show();
        }
    }

    private void getAddressFromLocation(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);
                String addressLine = address.getAddressLine(0);
                String city = address.getLocality();
                String state = address.getAdminArea();
                String postalCode = address.getPostalCode();

                // Check if any address component is missing and replace it with the nearest available details
                if (TextUtils.isEmpty(addressLine)) {
                    addressLine = address.getSubLocality();
                }
                if (TextUtils.isEmpty(city)) {
                    city = address.getSubAdminArea();
                }
                if (TextUtils.isEmpty(state)) {
                    state = address.getAdminArea();
                }
                if (TextUtils.isEmpty(postalCode)) {
                    postalCode = address.getPostalCode();
                }

                autoCompleteTextView.setText(addressLine);
                editTextCity.setText(city);
                editTextState.setText(state);
                editTextPostalCode.setText(postalCode);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveAddressToFirebase() {
        String address = autoCompleteTextView.getText().toString().trim();
        String city = editTextCity.getText().toString().trim();
        String state = editTextState.getText().toString().trim();
        String postalCode = editTextPostalCode.getText().toString().trim();

        if (TextUtils.isEmpty(address) || TextUtils.isEmpty(city) || TextUtils.isEmpty(state) || TextUtils.isEmpty(postalCode)) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            Map<String, Object> addressMap = new HashMap<>();
            addressMap.put("address", address);
            addressMap.put("city", city);
            addressMap.put("state", state);
            addressMap.put("postalCode", postalCode);

            mDatabase.child("users").child(userId).child("address").setValue(addressMap)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(AddAddressActivity.this, "Address saved successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(AddAddressActivity.this, "Failed to save address", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void loadAddressDetails() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference addressRef = mDatabase.child("users").child(userId).child("address");
            addressRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Map<String, Object> addressMap = (Map<String, Object>) dataSnapshot.getValue();
                        if (addressMap != null) {
                            autoCompleteTextView.setText(String.valueOf(addressMap.get("address")));
                            editTextCity.setText(String.valueOf(addressMap.get("city")));
                            editTextState.setText(String.valueOf(addressMap.get("state")));
                            editTextPostalCode.setText(String.valueOf(addressMap.get("postalCode")));
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle error
                }
            });
        }
    }

    private void saveChangesToFirebase() {
        String address = autoCompleteTextView.getText().toString().trim();
        String city = editTextCity.getText().toString().trim();
        String state = editTextState.getText().toString().trim();
        String postalCode = editTextPostalCode.getText().toString().trim();

        if (TextUtils.isEmpty(address) || TextUtils.isEmpty(city) || TextUtils.isEmpty(state) || TextUtils.isEmpty(postalCode)) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            Map<String, Object> addressMap = new HashMap<>();
            addressMap.put("address", address);
            addressMap.put("city", city);
            addressMap.put("state", state);
            addressMap.put("postalCode", postalCode);

            mDatabase.child("users").child(userId).child("address").setValue(addressMap)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(AddAddressActivity.this, "Changes saved successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(AddAddressActivity.this, "Failed to save changes", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
    }
}

/*
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import androidx.annotation.NonNull;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import java.io.IOException;
import java.util.HashMap;
import com.google.firebase.database.DataSnapshot;


public class AddAddressActivity extends AppCompatActivity {

    private EditText autoCompleteTextView, editTextCity, editTextState, editTextPostalCode;
    private Button buttonSaveAddress;
    private Button buttonSaveChanges, buttonGetCurrentLocation;
    private DatabaseReference mDatabase;
    private LocationManager locationManager;
    private LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        autoCompleteTextView = findViewById(R.id.autoCompleteTextView);
        editTextCity = findViewById(R.id.editTextCity);
        editTextState = findViewById(R.id.editTextState);
        editTextPostalCode = findViewById(R.id.editTextPostalCode);
        buttonSaveAddress = findViewById(R.id.buttonSaveAddress);
        buttonSaveChanges = findViewById(R.id.buttonSaveChanges);
        buttonGetCurrentLocation = findViewById(R.id.buttonGetCurrentLocation);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        loadAddressDetails();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                Toast.makeText(AddAddressActivity.this, "Latitude: " + latitude + ", Longitude: " + longitude, Toast.LENGTH_SHORT).show();
                getAddressFromLocation(latitude, longitude); // Call getAddressFromLocation here
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                // Called when the status of the provider changes (e.g., enabled -> disabled)
            }

            @Override
            public void onProviderEnabled(String provider) {
                Toast.makeText(AddAddressActivity.this, "Location provider enabled: " + provider, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onProviderDisabled(String provider) {
                Toast.makeText(AddAddressActivity.this, "Location provider disabled: " + provider, Toast.LENGTH_SHORT).show();
            }
        };

        buttonSaveAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAddressToFirebase();
            }
        });

        buttonGetCurrentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentLocation();
            }
        });

        buttonSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChangesToFirebase();
            }
        });

        // Check if the activity was launched with an intent to edit existing address
        if (getIntent().hasExtra("edit_address")) {
            // Load the existing address details
            loadAddressDetails();
            // Change the button text
            buttonSaveAddress.setText("Save Changes");
        } else {
            // Request location updates when the activity is created for adding a new address
            requestLocationUpdates();
        }
    }


    private void requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            return;
        }
        Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (lastKnownLocation != null) {
            double latitude = lastKnownLocation.getLatitude();
            double longitude = lastKnownLocation.getLongitude();
            getAddressFromLocation(latitude, longitude);
        } else {
            Toast.makeText(this, "Unable to retrieve current location", Toast.LENGTH_SHORT).show();
        }
    }

    // getAddressFromLocation method is moved outside of the onClick method
    private void getAddressFromLocation(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);
                String addressLine = address.getAddressLine(0);
                String city = address.getLocality();
                String state = address.getAdminArea();
                String postalCode = address.getPostalCode();

                // Check if any address component is missing and replace it with the nearest available details
                if (TextUtils.isEmpty(addressLine)) {
                    addressLine = address.getSubLocality();
                }
                if (TextUtils.isEmpty(city)) {
                    city = address.getSubAdminArea();
                }
                if (TextUtils.isEmpty(state)) {
                    state = address.getAdminArea();
                }
                if (TextUtils.isEmpty(postalCode)) {
                    postalCode = address.getPostalCode();
                }

                autoCompleteTextView.setText(addressLine);
                editTextCity.setText(city);
                editTextState.setText(state);
                editTextPostalCode.setText(postalCode);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveAddressToFirebase() {
        String address = autoCompleteTextView.getText().toString().trim();
        String city = editTextCity.getText().toString().trim();
        String state = editTextState.getText().toString().trim();
        String postalCode = editTextPostalCode.getText().toString().trim();

        if (TextUtils.isEmpty(address) || TextUtils.isEmpty(city) || TextUtils.isEmpty(state) || TextUtils.isEmpty(postalCode)) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            Map<String, Object> addressMap = new HashMap<>();
            addressMap.put("address", address);
            addressMap.put("city", city);
            addressMap.put("state", state);
            addressMap.put("postalCode", postalCode);

            mDatabase.child("users").child(userId).child("address").setValue(addressMap)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(AddAddressActivity.this, "Address saved successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(AddAddressActivity.this, "Failed to save address", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void loadAddressDetails() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference addressRef = mDatabase.child("users").child(userId).child("address");
            addressRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Map<String, Object> addressMap = (Map<String, Object>) dataSnapshot.getValue();
                        if (addressMap != null) {
                            autoCompleteTextView.setText(String.valueOf(addressMap.get("address")));
                            editTextCity.setText(String.valueOf(addressMap.get("city")));
                            editTextState.setText(String.valueOf(addressMap.get("state")));
                            editTextPostalCode.setText(String.valueOf(addressMap.get("postalCode")));
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle error
                }
            });
        }
    }

    private void saveChangesToFirebase() {
        String address = autoCompleteTextView.getText().toString().trim();
        String city = editTextCity.getText().toString().trim();
        String state = editTextState.getText().toString().trim();
        String postalCode = editTextPostalCode.getText().toString().trim();

        if (TextUtils.isEmpty(address) || TextUtils.isEmpty(city) || TextUtils.isEmpty(state) || TextUtils.isEmpty(postalCode)) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            Map<String, Object> addressMap = new HashMap<>();
            addressMap.put("address", address);
            addressMap.put("city", city);
            addressMap.put("state", state);
            addressMap.put("postalCode", postalCode);

            mDatabase.child("users").child(userId).child("address").setValue(addressMap)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(AddAddressActivity.this, "Changes saved successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(AddAddressActivity.this, "Failed to save changes", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}




/*
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AddAddressActivity extends AppCompatActivity {

    private EditText autoCompleteTextView, editTextCity, editTextState, editTextPostalCode;
    private Button buttonSaveAddress, buttonGetCurrentLocation;
    private DatabaseReference mDatabase;
    private LocationManager locationManager;
    private LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        autoCompleteTextView = findViewById(R.id.autoCompleteTextView);
        editTextCity = findViewById(R.id.editTextCity);
        editTextState = findViewById(R.id.editTextState);
        editTextPostalCode = findViewById(R.id.editTextPostalCode);
        buttonSaveAddress = findViewById(R.id.buttonSaveAddress);
        buttonGetCurrentLocation = findViewById(R.id.buttonGetCurrentLocation);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                getAddressFromLocation(latitude, longitude);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {}

            @Override
            public void onProviderEnabled(String provider) {}

            @Override
            public void onProviderDisabled(String provider) {}
        };

        buttonGetCurrentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentLocation();
            }
        });

        buttonSaveAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAddressToFirebase();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(locationListener);
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            return;
        }
        Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (lastKnownLocation != null) {
            double latitude = lastKnownLocation.getLatitude();
            double longitude = lastKnownLocation.getLongitude();
            getAddressFromLocation(latitude, longitude);
        } else {
            Toast.makeText(this, "Unable to retrieve current location", Toast.LENGTH_SHORT).show();
        }
    }

    private void getAddressFromLocation(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);
                String addressLine = address.getAddressLine(0);
                String city = address.getLocality();
                String state = address.getAdminArea();
                String postalCode = address.getPostalCode();

                // Check if any address component is missing and replace it with the nearest available details
                if (TextUtils.isEmpty(addressLine)) {
                    addressLine = address.getSubLocality();
                }
                if (TextUtils.isEmpty(city)) {
                    city = address.getSubAdminArea();
                }
                if (TextUtils.isEmpty(state)) {
                    state = address.getAdminArea();
                }
                if (TextUtils.isEmpty(postalCode)) {
                    postalCode = address.getPostalCode();
                }

                autoCompleteTextView.setText(addressLine);
                editTextCity.setText(city);
                editTextState.setText(state);
                editTextPostalCode.setText(postalCode);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveAddressToFirebase() {
        String address = autoCompleteTextView.getText().toString().trim();
        String city = editTextCity.getText().toString().trim();
        String state = editTextState.getText().toString().trim();
        String postalCode = editTextPostalCode.getText().toString().trim();

        if (TextUtils.isEmpty(address) || TextUtils.isEmpty(city) || TextUtils.isEmpty(state) || TextUtils.isEmpty(postalCode)) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            Map<String, Object> addressMap = Map.of(
                    "address", address,
                    "city", city,
                    "state", state,
                    "postalCode", postalCode
            );

            mDatabase.child("users").child(userId).child("address").setValue(addressMap)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(AddAddressActivity.this, "Address saved successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(AddAddressActivity.this, "Failed to save address", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}*/



/*package com.example.resqfood;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.android.libraries.places.api.model.Place;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
public class AddAddressActivity extends AppCompatActivity {

    private EditText autoCompleteTextView, editTextCity, editTextState, editTextPostalCode;
    private Button buttonSaveAddress;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        autoCompleteTextView = findViewById(R.id.autoCompleteTextView);
        editTextCity = findViewById(R.id.editTextCity);
        editTextState = findViewById(R.id.editTextState);
        editTextPostalCode = findViewById(R.id.editTextPostalCode);
        buttonSaveAddress = findViewById(R.id.buttonSaveAddress);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        buttonSaveAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAddressToFirebase();
            }
        });
    }

    private void saveAddressToFirebase() {
        String address = autoCompleteTextView.getText().toString().trim();
        String city = editTextCity.getText().toString().trim();
        String state = editTextState.getText().toString().trim();
        String postalCode = editTextPostalCode.getText().toString().trim();

        if (TextUtils.isEmpty(address) || TextUtils.isEmpty(city) || TextUtils.isEmpty(state) || TextUtils.isEmpty(postalCode)) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            Map<String, Object> addressMap = Map.of(
                    "address", address,
                    "city", city,
                    "state", state,
                    "postalCode", postalCode
            );

            mDatabase.child("users").child(userId).child("address").setValue(addressMap)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(AddAddressActivity.this, "Address saved successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(AddAddressActivity.this, "Failed to save address", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}*/