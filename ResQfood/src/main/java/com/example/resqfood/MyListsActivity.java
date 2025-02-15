package com.example.resqfood;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

import java.util.ArrayList;
import java.util.List;


public class MyListsActivity extends AppCompatActivity implements MyListsAdapter.OnDeleteClickListener {

    private RecyclerView recyclerView;
    private List<Donation> donationList;
    private MyListsAdapter adapter;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private String documentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_lists);
        documentId = getIntent().getStringExtra("documentId");
        recyclerView = findViewById(R.id.recyclerView);
        donationList = new ArrayList<>();
        adapter = new MyListsAdapter(donationList, this); // Pass this as the OnDeleteClickListener
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        fetchDonations();
    }

    private void fetchDonations() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Fetch from Realtime Database
            mDatabase.child("users")
                    .child(userId)
                    .child("donations")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            donationList.clear(); // Clear the list before adding new items
                            for (DataSnapshot donationSnapshot : dataSnapshot.getChildren()) {
                                Donation donation = donationSnapshot.getValue(Donation.class);
                                donationList.add(donation);
                            }
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(MyListsActivity.this, "Failed to fetch Realtime Database donations", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "No user logged in", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDeleteClick(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to delete the donation?")
                .setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        checkIfDonated(position);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing, dialog will be dismissed
                    }
                })
                .show();
    }

    private void checkIfDonated(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Have you donated this item?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Show confirmation dialog
                        showConfirmDeleteDialog(position);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing, dialog will be dismissed
                    }
                })
                .show();
    }

    private void showConfirmDeleteDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("The item has been donated. Do you still want to delete the donation?")
                .setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ProgressDialog progressDialog = new ProgressDialog(MyListsActivity.this);
                        progressDialog.setMessage("Deleting from account...");
                        progressDialog.setCancelable(false);
                        progressDialog.show();

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.dismiss();
                                deleteDonation(position);
                            }
                        }, 2000); // 2 seconds delay before deleting
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing, dialog will be dismissed
                    }
                })
                .show();
    }

    private void deleteDonation(int position) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            Donation donation = donationList.get(position);
            String documentId = String.valueOf(donation.getDocumentId());

            // Delete from Realtime Database
            mDatabase.child("users")
                    .child(userId)
                    .child("donations")
                    .child(documentId)
                    .removeValue()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(MyListsActivity.this, "Donation deleted successfully", Toast.LENGTH_SHORT).show();
                        donationList.remove(position);
                        adapter.notifyItemRemoved(position);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(MyListsActivity.this, "Failed to delete donation", Toast.LENGTH_SHORT).show();
                    });
        }
    }

}
    /*
    @Override
    public void onDeleteClick(int position) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            Donation donation = donationList.get(position);
            String documentId = String.valueOf(donation.getDocumentId());

            // Delete from Realtime Database
            mDatabase.child("users")
                    .child(userId)
                    .child("donations")
                    .removeValue()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(MyListsActivity.this, "Donation deleted successfully", Toast.LENGTH_SHORT).show();
                        donationList.remove(position);
                        adapter.notifyItemRemoved(position);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(MyListsActivity.this, "Failed to delete donation", Toast.LENGTH_SHORT).show();
                    });
        }
    }
}


/*
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.FirebaseFirestore;


public class MyListsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<Donation> donationList;
    private MyListsAdapter adapter;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String userId;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_lists);
        adapter = new MyListsAdapter(donationList);
        adapter.setOnDeleteClickListener(new MyListsAdapter.OnDeleteClickListener() {
            @Override
            public void onDeleteClick(int position) {
                deleteDonation(position);
            }
        });
        recyclerView.setAdapter(adapter);

        // Other initialization code...

        recyclerView = findViewById(R.id.recyclerView);
        donationList = new ArrayList<>();
        adapter = new MyListsAdapter(donationList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        fetchDonations();
    }

    private void fetchDonations() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            Donation donation = donationList.get(position);
            String donationId = donation.getId();
            // Fetch from Firestore
            /*db.collection("users")
                    .document(userId)
                    .collection("donations")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                Donation donation = document.toObject(Donation.class);
                                donationList.add(donation);
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(MyListsActivity.this, "Failed to fetch Firestore donations", Toast.LENGTH_SHORT).show();
                        }
                    });*/

        /*    // Fetch from Realtime Database
            mDatabase.child("users")
                    .child(userId)
                    .child("donations")
                    .child(donationId)
                    .removeValue()
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot donationSnapshot : dataSnapshot.getChildren()) {
                                Donation donation = donationSnapshot.getValue(Donation.class);
                                donationList.add(donation);
                            }
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(MyListsActivity.this, "Failed to fetch Realtime Database donations", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "No user logged in", Toast.LENGTH_SHORT).show();
        }
    }
}

/*
public class MyListsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DonationAdapter donationAdapter;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private String userId;
    Button buttonDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_lists);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        donationAdapter = new DonationAdapter();
        recyclerView.setAdapter(donationAdapter);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
        } else {
            // Handle the case where there's no authenticated user
        }

        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Query Realtime Database to get donations made by the current user
        mDatabase.child("users").child(userId).child("donations")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<Donation> donations = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Donation donation = snapshot.getValue(Donation.class);
                            if (donation != null) {
                                donations.add(donation);
                            }
                        }
                        donationAdapter.setDonations(donations);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d("MyListsActivity", "Database error: " + databaseError.getMessage());
                    }
                });
    }

    // Adapter class to display donations
    private class DonationAdapter extends RecyclerView.Adapter<DonationAdapter.DonationViewHolder> {

        private List<Donation> donations = new ArrayList<>();

        public void setDonations(List<Donation> donations) {
            this.donations = donations;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public DonationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_donation, parent, false);
            return new DonationViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull DonationViewHolder holder, int position) {
            Donation donation = donations.get(position);
            holder.bind(donation);
        }

        @Override
        public int getItemCount() {
            return donations.size();
        }

        class DonationViewHolder extends RecyclerView.ViewHolder {

            TextView textViewFoodName, textViewQuantity, textViewExpirationDate;
            ImageView imageViewFood;
            Button deleteButton;

            public DonationViewHolder(@NonNull View itemView) {
                super(itemView);
                textViewFoodName = itemView.findViewById(R.id.textViewFoodName);
                textViewQuantity = itemView.findViewById(R.id.textViewQuantity);
                textViewExpirationDate = itemView.findViewById(R.id.textViewExpirationDate);
                imageViewFood = itemView.findViewById(R.id.imageViewFood);
                deleteButton = itemView.findViewById(R.id.deleteButton);

                deleteButton.setOnClickListener(v -> {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Donation donation = donations.get(position);
                        //deleteDonation(donation);
                    }
                });
            }

            public void bind(Donation donation) {
                textViewFoodName.setText("Food Name: " + donation.getFoodName());
                textViewQuantity.setText("Quantity: " + donation.getQuantity());
                textViewExpirationDate.setText("Expiration Date: " + donation.getExpirationDate());

                // Load image using Picasso or Glide library
                if (donation.getImageUrl() != null && !donation.getImageUrl().isEmpty()) {
                    Picasso.get().load(donation.getImageUrl()).into(imageViewFood);
                }
            }
        }
    }

   /* private void deleteDonation(Donation donation) {
        String donationId = donation.getId();
        mDatabase.child("users").child(userId).child("donations").child(donationId)
                .removeValue()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(MyListsActivity.this, "Donation deleted successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.d("MyListsActivity", "Failed to delete donation: " + e.getMessage());
                    Toast.makeText(MyListsActivity.this, "Failed to delete donation", Toast.LENGTH_SHORT).show();
                });
    }*/


/*
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MyListsActivity extends AppCompatActivity implements MyListsAdapter.OnDeleteClickListener {

    private RecyclerView recyclerViewDonations;
    private List<Donation> donationList;
    private MyListsAdapter adapter;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_lists);
        mAuth = FirebaseAuth.getInstance();

        recyclerViewDonations = findViewById(R.id.recyclerViewDonations);
        donationList = new ArrayList<>();
        adapter = new MyListsAdapter(donationList, this);
        recyclerViewDonations.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewDonations.setAdapter(adapter);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        fetchDonations();
    }

    private void fetchDonations() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            // DatabaseReference for donations node under the user
            DatabaseReference donationsRef = FirebaseDatabase.getInstance().getReference()
                    .child("users")
                    .child(userId)
                    .child("donations");

            // Fetch the document IDs under the donations node
            donationsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot donationSnapshot : dataSnapshot.getChildren()) {
                        String documentId = donationSnapshot.getKey(); // Get the document ID
                        // Now fetch the donation using the document ID
                        DatabaseReference donationRef = donationsRef.child(documentId);
                        donationRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                // Deserialize the Donation object
                                Donation donation = snapshot.getValue(Donation.class);
                                if (donation != null) {
                                    donationList.add(donation);
                                    adapter.notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(MyListsActivity.this, "Failed to fetch Realtime Database donations", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(MyListsActivity.this, "Failed to fetch Realtime Database donations", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "No user logged in", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDeleteClick(int position) {
        showDeleteConfirmationDialog(position);
    }

    private void showDeleteConfirmationDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Delete");
        builder.setMessage("Are you sure you want to delete this donation?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteDonation(position);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void deleteDonation(int position) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            String donationId = donationList.get(position).getId();
            mDatabase.child("users").child(userId).child("donations").child(donationId)
                    .removeValue()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(MyListsActivity.this, "Donation deleted successfully", Toast.LENGTH_SHORT).show();
                        fetchDonations();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(MyListsActivity.this, "Failed to delete donation", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(this, "No user logged in", Toast.LENGTH_SHORT).show();
        }
    }
}

*/
