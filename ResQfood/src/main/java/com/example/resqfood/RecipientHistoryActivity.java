package com.example.resqfood;


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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class RecipientHistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DatabaseReference mDatabase;
    private RecipientDetailsAdapter adapter;
    private FirebaseAuth mAuth;
    private List<RecipientDetails> recipientDetailsList;
    private String recipientId; // Declare recipientId as a class variable

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipient_history);

        recipientId = getIntent().getStringExtra("recipientId"); // Assign value to recipientId from intent

        recyclerView = findViewById(R.id.recyclerViewRecipientDetails);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        fetchRecipientDetails();
    }

    private void fetchRecipientDetails() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = mAuth.getCurrentUser().getUid();

            mDatabase.child("users")
                    .child(userId)
                    .child("recipients")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            recipientDetailsList = new ArrayList<>();
                            for (DataSnapshot recipientSnapshot : dataSnapshot.getChildren()) {
                                String recipientName = recipientSnapshot.child("recipientName").getValue(String.class);
                                String recipientContact = recipientSnapshot.child("recipientContact").getValue(String.class);
                                String pickupLocation = recipientSnapshot.child("pickupLocation").getValue(String.class);
                                String preferredDateTime = recipientSnapshot.child("preferredDateTime").getValue(String.class);
                                String specialInstructions = recipientSnapshot.child("specialInstructions").getValue(String.class);
                                String status = recipientSnapshot.child("status").getValue(String.class);

                                RecipientDetails recipientDetails = new RecipientDetails(recipientName, recipientContact, pickupLocation, preferredDateTime, specialInstructions, status);
                                recipientDetailsList.add(recipientDetails);
                            }

                            adapter = new RecipientDetailsAdapter(recipientDetailsList);
                            recyclerView.setAdapter(adapter);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(RecipientHistoryActivity.this, "Failed to fetch recipient details", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "No user logged in", Toast.LENGTH_SHORT).show();
        }
    }

    public static class RecipientDetailsAdapter extends RecyclerView.Adapter<RecipientDetailsAdapter.ViewHolder> {

        private List<RecipientDetails> recipientDetailsList;

        public RecipientDetailsAdapter(List<RecipientDetails> recipientDetailsList) {
            this.recipientDetailsList = recipientDetailsList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipient_history, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            RecipientDetails recipientDetails = recipientDetailsList.get(position);
            holder.bind(recipientDetails);
        }

        @Override
        public int getItemCount() {
            return recipientDetailsList.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            private TextView textViewRecipientName;
            private TextView textViewRecipientContact;
            private TextView textViewPickupLocation;
            private TextView textViewPreferredDateTime;
            private TextView textViewSpecialInstructions;
            private TextView textViewStatus;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                textViewRecipientName = itemView.findViewById(R.id.textViewRecipientName);
                textViewRecipientContact = itemView.findViewById(R.id.textViewRecipientContact);
                textViewPickupLocation = itemView.findViewById(R.id.textViewPickupLocation);
                textViewPreferredDateTime = itemView.findViewById(R.id.textViewPreferredDateTime);
                textViewSpecialInstructions = itemView.findViewById(R.id.textViewSpecialInstructions);
                textViewStatus = itemView.findViewById(R.id.textViewStatus);
            }

            public void bind(RecipientDetails recipientDetails) {
                textViewRecipientName.setText("Recipient Name: " + recipientDetails.getRecipientName());
                textViewRecipientContact.setText("Recipient Contact: " + recipientDetails.getRecipientContact());
                textViewPickupLocation.setText("Pickup Location: " + recipientDetails.getPickupLocation());
                textViewPreferredDateTime.setText("Preferred Date and Time: " + recipientDetails.getPreferredDateTime());
                textViewSpecialInstructions.setText("Special Instructions: " + recipientDetails.getSpecialInstructions());
                textViewStatus.setText("Status: " + recipientDetails.getStatus());
            }
        }
    }
}

/*


import android.os.Bundle;
import android.widget.Toast;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.resqfood.RecipientDetails;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;
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

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;



import java.util.List;
public class RecipientHistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DatabaseReference mDatabase;
    private RecipientDetailsAdapter adapter;
    private FirebaseAuth mAuth;
    private List<RecipientDetails> recipientDetailsList;
    private String recipientId; // Declare recipientId as a class variable

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipient_history);

        recipientId = getIntent().getStringExtra("recipientId"); // Assign value to recipientId from intent

        recyclerView = findViewById(R.id.recyclerViewRecipientDetails);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        fetchRecipientDetails();
    }

    private void fetchRecipientDetails() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = mAuth.getCurrentUser().getUid();

            mDatabase.child("users")
                    .child(userId)
                    .child("recipients")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            recipientDetailsList = new ArrayList<>();
                            for (DataSnapshot recipientSnapshot : dataSnapshot.getChildren()) {
                                RecipientDetails recipientDetails = recipientSnapshot.getValue(RecipientDetails.class);
                                if (recipientDetails != null) {
                                    recipientDetailsList.add(recipientDetails);
                                }
                            }

                            recyclerView.setAdapter(adapter);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(RecipientHistoryActivity.this, "Failed to fetch recipient details", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "No user logged in", Toast.LENGTH_SHORT).show();
        }
    }



    public static class RecipientDetailsAdapter extends RecyclerView.Adapter<RecipientHistoryActivity.RecipientDetailsAdapter.ViewHolder> {

        private List<RecipientDetails> recipientDetailsList;
        //private NotificationActivity.RecipientDetailsAdapter.onAcceptButtonClickListner onAcceptButtonClickListener;

        public RecipientDetailsAdapter(List<RecipientDetails> recipientDetailsList) {
            this.recipientDetailsList = recipientDetailsList;
        }

        @NonNull
        @Override
        public RecipientHistoryActivity.RecipientDetailsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipient_history, parent, false);
            return new RecipientHistoryActivity.RecipientDetailsAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecipientHistoryActivity.RecipientDetailsAdapter.ViewHolder holder, int position) {
            RecipientDetails recipientDetails = recipientDetailsList.get(position);
            holder.bind(recipientDetails);

        }

        @Override
        public int getItemCount() {
            return recipientDetailsList.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            private TextView textViewRecipientName;
            private TextView textViewRecipientContact;
            private TextView textViewPickupLocation;
            private TextView textViewPreferredDateTime;
            private TextView textViewSpecialInstructions;
            private TextView textViewStatus;


            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                textViewRecipientName = itemView.findViewById(R.id.textViewRecipientName);
                textViewRecipientContact = itemView.findViewById(R.id.textViewRecipientContact);
                textViewPickupLocation = itemView.findViewById(R.id.textViewPickupLocation);
                textViewPreferredDateTime = itemView.findViewById(R.id.textViewPreferredDateTime);
                textViewSpecialInstructions = itemView.findViewById(R.id.textViewSpecialInstructions);
                textViewStatus = itemView.findViewById(R.id.textViewStatus);
            }

            public void bind(RecipientDetails recipientDetails) {
                textViewRecipientName.setText("Recipient Name: " + recipientDetails.getRecipientName());
                textViewRecipientContact.setText("Recipient Contact: " + recipientDetails.getRecipientContact());
                textViewPickupLocation.setText("Pickup Location: " + recipientDetails.getPickupLocation());
                textViewPreferredDateTime.setText("Preferred Date and Time: " + recipientDetails.getPreferredDateTime());
                textViewSpecialInstructions.setText("Special Instructions: " + recipientDetails.getSpecialInstructions());
                textViewStatus.setText("Status: " + recipientDetails.getStatus());
            }

        }


    }
}*/