package com.example.resqfood;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import android.view.View;
import android.widget.Toast;
import android.util.Log;
import android.content.Intent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import java.util.ArrayList;
import java.util.List;

public class DonationDetailActivity extends AppCompatActivity {
    private static final String TAG = "DonationDetailActivity";

    private RecyclerView recyclerViewDonations;
    private FoodAdapter foodAdapter;
    private List<Donation> donationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation_detail);

        recyclerViewDonations = findViewById(R.id.recyclerViewDonationDetail);
        donationList = new ArrayList<>();
        foodAdapter = new FoodAdapter(this, donationList);

        recyclerViewDonations.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewDonations.setAdapter(foodAdapter);

        String foodName = getIntent().getStringExtra("foodName");
        queryDonations(foodName);
    }

    private void queryDonations(String foodName) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                donationList.clear();
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String userId = userSnapshot.getKey();
                    DatabaseReference userDonationsRef = usersRef.child(userId).child("donations");

                    userDonationsRef.orderByChild("foodName").equalTo(foodName)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                                    for (DataSnapshot donationSnapshot : dataSnapshot1.getChildren()) {
                                        Donation donation = donationSnapshot.getValue(Donation.class);
                                        donationList.add(donation);
                                    }
                                    foodAdapter.notifyDataSetChanged();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Toast.makeText(DonationDetailActivity.this, "Failed to retrieve donations", Toast.LENGTH_SHORT).show();
                                    Log.e(TAG, "Error retrieving donations", databaseError.toException());
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(DonationDetailActivity.this, "Failed to retrieve users", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error retrieving users", databaseError.toException());
            }
        });
    }

    public void onItemClick() {
        // Handle click on a donation item
        // Start CollectActivity with donation details
        Intent intent = new Intent(DonationDetailActivity.this, CollectActivity.class);

    }


}
    /* DatabaseReference donationRef = FirebaseDatabase.getInstance().getReference().child("users").child("donations");
        donationRef.orderByChild("foodName").equalTo(foodName)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        donationList.clear();
                        for (DataSnapshot donationSnapshot : dataSnapshot.getChildren()) {
                            Donation donation = donationSnapshot.getValue(Donation.class);
                            donationList.add(donation);
                        }
                        if (donationList.isEmpty()) {
                            findViewById(R.id.textViewNoFoodFound).setVisibility(View.VISIBLE);
                        } else {
                            findViewById(R.id.textViewNoFoodFound).setVisibility(View.GONE);
                            foodAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(DonationDetailActivity.this, "Failed to load food items", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Error fetching food items: ", databaseError.toException());
                    }
                });*/



/*
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;
import android.content.Intent;
public class DonationDetailActivity extends AppCompatActivity implements FoodAdapter.OnItemClickListener {

    private static final String TAG = "DonationDetailActivity";

    private RecyclerView recyclerView;
    private FoodAdapter adapter;
    private List<Donation> donationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation_detail);

        recyclerView = findViewById(R.id.recyclerViewDonationDetail);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        donationList = new ArrayList<>();
        String foodName = getIntent().getStringExtra("foodName");
        // Retrieve the userId from the intent
        String userId = getIntent().getStringExtra("userId");

        // Query Firebase to get details of donations for the logged-in user
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(userId).collection("donations")
                .whereEqualTo("foodName", foodName)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        Donation donation = documentSnapshot.toObject(Donation.class);
                        donationList.add(donation);
                    }
                    if (donationList.isEmpty()) {
                        findViewById(R.id.textViewNoFoodFound).setVisibility(View.VISIBLE);
                    } else {
                        findViewById(R.id.textViewNoFoodFound).setVisibility(View.GONE);
                        adapter = new FoodAdapter(donationList, DonationDetailActivity.this);
                        recyclerView.setAdapter(adapter);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(DonationDetailActivity.this, "Failed to load donations", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error fetching donations: ", e);
                });
    }
    @Override
    public void onItemClick(Donation donation) {
        // Handle click on a donation item
        // Start CollectActivity with donation details
        Intent intent = new Intent(DonationDetailActivity.this, CollectActivity.class);
        intent.putExtra("donation", donation); // Pass the clicked donation object to CollectActivity
        startActivity(intent);
    }
}


/*
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import androidx.annotation.NonNull;
import java.util.ArrayList;
import java.util.List;
import android.content.Intent;
import com.google.firebase.database.ValueEventListener;


public class DonationDetailActivity extends AppCompatActivity implements FoodAdapter.OnItemClickListener {

    private static final String TAG = "DonationDetailActivity";
    private RecyclerView recyclerView;
    private FoodAdapter adapter;
    private List<Donation> donationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation_detail);

        recyclerView = findViewById(R.id.recyclerViewDonationDetail);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        donationList = new ArrayList<>();

        // Retrieve the foodName from the intent
        String foodName = getIntent().getStringExtra("foodName");

        // Retrieve the userId from the intent
        String userId = getIntent().getStringExtra("userId");

        // Query Firebase Realtime Database to get details of food items matching foodName under the user's node
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("donations");
        userRef.orderByChild("foodName").equalTo(foodName)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        donationList.clear();
                        for (DataSnapshot donationSnapshot : dataSnapshot.getChildren()) {
                            Donation food = donationSnapshot.getValue(Donation.class);
                            donationList.add(food);
                        }
                        if (donationList.isEmpty()) {
                            findViewById(R.id.textViewNoFoodFound).setVisibility(View.VISIBLE);
                        } else {
                            findViewById(R.id.textViewNoFoodFound).setVisibility(View.GONE);
                            adapter = new FoodAdapter(donationList, DonationDetailActivity.this);
                            recyclerView.setAdapter(adapter);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(DonationDetailActivity.this, "Failed to load food items", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Error fetching food items: ", databaseError.toException());
                    }
                });
    }

    @Override
    public void onItemClick(Donation donation) {
        Intent intent = new Intent(DonationDetailActivity.this, CollectActivity.class);
        intent.putExtra("donorId", donation);
        startActivity(intent);
}*/