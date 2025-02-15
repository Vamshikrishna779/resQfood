
package com.example.resqfood;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class MyAddressActivity extends AppCompatActivity {

    private EditText autoCompleteTextView, editTextCity, editTextState, editTextPostalCode;
    private Button buttonSaveChanges;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        autoCompleteTextView = findViewById(R.id.autoCompleteTextView);
        editTextCity = findViewById(R.id.editTextCity);
        editTextState = findViewById(R.id.editTextState);
        editTextPostalCode = findViewById(R.id.editTextPostalCode);
        buttonSaveChanges = findViewById(R.id.buttonSaveAddress); // Reusing the same button for saving changes

        mDatabase = FirebaseDatabase.getInstance().getReference();

        loadAddressDetails();

        buttonSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChangesToFirebase();
            }
        });
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

            Map<String, Object> addressMap = Map.of(
                    "address", address,
                    "city", city,
                    "state", state,
                    "postalCode", postalCode
            );

            mDatabase.child("users").child(userId).child("address").setValue(addressMap)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(MyAddressActivity.this, "Changes saved successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(MyAddressActivity.this, "Failed to save changes", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
