package com.example.resqfood;
import android.os.Bundle;
import android.widget.Toast;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
public class NotificationActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DatabaseReference mDatabase;
    private RecipientDetailsAdapter adapter;
    private FirebaseAuth mAuth;
    private List<RecipientDetails> recipientDetailsList;
    private String recipientId; // Declare recipientId as a class variable

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

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

                            adapter = new RecipientDetailsAdapter(recipientDetailsList, new RecipientDetailsAdapter.onAcceptButtonClickListner() {
                                @Override
                                public void onAcceptButtonClick(int position) {
                                    NotificationActivity.this.onAcceptButtonClick(position);
                                }
                            });
                            recyclerView.setAdapter(adapter);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(NotificationActivity.this, "Failed to fetch recipient details", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "No user logged in", Toast.LENGTH_SHORT).show();
        }
    }
    public void onAcceptButtonClick(int position) {
        // Update the status in Firebase
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            RecipientDetails recipientDetails = recipientDetailsList.get(position);
            String recipientName = recipientDetails.getRecipientName();

            // Query all users for the recipient name
            mDatabase.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        DatabaseReference userDonationsRef = userSnapshot.child("recipients").getRef();
                        userDonationsRef.orderByChild("recipientName").equalTo(recipientName).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot recipientSnapshot : snapshot.getChildren()) {
                                    String recipientId = recipientSnapshot.getKey();
                                    userDonationsRef.child(recipientId).child("status").setValue("Confirmed")
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(NotificationActivity.this, "Recipient status confirmed", Toast.LENGTH_SHORT).show();
                                                    // Update your UI or perform any other actions here
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(NotificationActivity.this, "Failed to update recipient status", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(NotificationActivity.this, "Failed to fetch recipients", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(NotificationActivity.this, "Failed to fetch users", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

  /*  public void onAcceptButtonClick(int position) {
        // Update the status in Firebase
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            RecipientDetails recipientDetails = recipientDetailsList.get(position);
            DatabaseReference userDonationsRef = mDatabase.child("users").child(userId).child("recipients");
            DatabaseReference newDonationRef = userDonationsRef.push();
            if (newDonationRef != null) {
                String recipientId = newDonationRef.getKey(); // Remove foodName from recipientId
                userDonationsRef.child(recipientId)
                        .child("status")
                        .setValue("Confirmed")
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(NotificationActivity.this, "Recipient status confirmed", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(NotificationActivity.this, "Failed to update recipient status", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }
    }
*/

    public static class RecipientDetailsAdapter extends RecyclerView.Adapter<RecipientDetailsAdapter.ViewHolder> {

        private List<RecipientDetails> recipientDetailsList;
        private onAcceptButtonClickListner onAcceptButtonClickListener;

        public RecipientDetailsAdapter(List<RecipientDetails> recipientDetailsList, onAcceptButtonClickListner listener) {
            this.recipientDetailsList = recipientDetailsList;
            this.onAcceptButtonClickListener = listener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification_details, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            RecipientDetails recipientDetails = recipientDetailsList.get(position);
            holder.bind(recipientDetails);
            holder.buttonAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Call the onAcceptButtonClick method here
                    onAcceptButtonClickListener.onAcceptButtonClick(position);
                }
            });
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
            private Button buttonAccept;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                textViewRecipientName = itemView.findViewById(R.id.textViewRecipientName);
                textViewRecipientContact = itemView.findViewById(R.id.textViewRecipientContact);
                textViewPickupLocation = itemView.findViewById(R.id.textViewPickupLocation);
                textViewPreferredDateTime = itemView.findViewById(R.id.textViewPreferredDateTime);
                textViewSpecialInstructions = itemView.findViewById(R.id.textViewSpecialInstructions);
                buttonAccept = itemView.findViewById(R.id.buttonAccept);
            }

            public void bind(RecipientDetails recipientDetails) {
                textViewRecipientName.setText("Recipient Name: " + recipientDetails.getRecipientName());
                textViewRecipientContact.setText("Recipient Contact: " + recipientDetails.getRecipientContact());
                textViewPickupLocation.setText("Pickup Location: " + recipientDetails.getPickupLocation());
                textViewPreferredDateTime.setText("Preferred Date and Time: " + recipientDetails.getPreferredDateTime());
                textViewSpecialInstructions.setText("Special Instructions: " + recipientDetails.getSpecialInstructions());
            }
        }

        public interface onAcceptButtonClickListner {
            void onAcceptButtonClick(int position);
        }
    }
}
/*
public class NotificationActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DatabaseReference mDatabase;
    private RecipientDetailsAdapter adapter;
    private FirebaseAuth mAuth;
    private List<RecipientDetails> recipientDetailsList;

    String recipientId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        recipientId = getIntent().getStringExtra("recipientId");

        recyclerView = findViewById(R.id.recyclerViewRecipientDetails);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mDatabase = FirebaseDatabase.getInstance().getReference();

        fetchRecipientDetails();
    }

    private void fetchRecipientDetails() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            mDatabase.child("users")
                    .child(userId)
                    .child("recipients")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            List<RecipientDetails> recipientDetailsList = new ArrayList<>();
                            for (DataSnapshot recipientSnapshot : dataSnapshot.getChildren()) {
                                RecipientDetails recipientDetails = recipientSnapshot.getValue(RecipientDetails.class);
                                if (recipientDetails != null) {
                                    recipientDetailsList.add(recipientDetails);
                                }
                            }
                            adapter = new RecipientDetailsAdapter(recipientDetailsList);
                            recyclerView.setAdapter(adapter);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(NotificationActivity.this, "Failed to fetch recipient details", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "No user logged in", Toast.LENGTH_SHORT).show();
        }
    }
    public void onAcceptButtonClick(int position) {
        // Get the position of the item

        // Update the status in Firebase
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            RecipientDetails recipientDetails = recipientDetailsList.get(position);
            recipientId = String.valueOf(recipientDetails.getRecipientId());

            mDatabase.child("users")
                    .child(userId)
                    .child("recipients")
                    .child(recipientId)
                    // Assuming recipientDetails has an ID field
                    .child("status")
                    .setValue("Confirmed")
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(view.getContext(), "Recipient status confirmed", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(view.getContext(), "Failed to update recipient status", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    public static class RecipientDetailsAdapter extends RecyclerView.Adapter<RecipientDetailsAdapter.ViewHolder> {

        private List<RecipientDetails> recipientDetailsList;

        private onAcceptButtonClickListner onAcceptButtonClickListenr;


        public RecipientDetailsAdapter(List<RecipientDetails> recipientDetailsList) {
            this.recipientDetailsList = recipientDetailsList;
            this.onAcceptButtonClickListenr = onAcceptButtonClickListner;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification_details, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            RecipientDetails recipientDetails = recipientDetailsList.get(position);
            holder.bind(recipientDetails);
            holder.buttonAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Call the onAcceptButtonClick method here
                    onAcceptButtonClickListner.onAcceptButtonClick(position);
                }
            });
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
            private Button buttonAccept;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                textViewRecipientName = itemView.findViewById(R.id.textViewRecipientName);
                textViewRecipientContact = itemView.findViewById(R.id.textViewRecipientContact);
                textViewPickupLocation = itemView.findViewById(R.id.textViewPickupLocation);
                textViewPreferredDateTime = itemView.findViewById(R.id.textViewPreferredDateTime);
                textViewSpecialInstructions = itemView.findViewById(R.id.textViewSpecialInstructions);
                buttonAccept = itemView.findViewById(R.id.buttonAccept);
            }

            public void bind(RecipientDetails recipientDetails) {
                textViewRecipientName.setText("Recipient Name: " + recipientDetails.getRecipientName());
                textViewRecipientContact.setText("Recipient Contact: " + recipientDetails.getRecipientContact());
                textViewPickupLocation.setText("Pickup Location: " + recipientDetails.getPickupLocation());
                textViewPreferredDateTime.setText("Preferred Date and Time: " + recipientDetails.getPreferredDateTime());
                textViewSpecialInstructions.setText("Special Instructions: " + recipientDetails.getSpecialInstructions());
            }
        }
    }
}


/*package com.example.resqfood;
import android.os.Bundle;
import android.widget.TextView;
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

public class NotificationActivity extends AppCompatActivity {

    private TextView textViewRecipientName;
    private TextView textViewRecipientContact;
    private TextView textViewPickupLocation;
    private TextView textViewPreferredDateTime;
    private TextView textViewSpecialInstructions;

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        textViewRecipientName = findViewById(R.id.textViewRecipientName);
        textViewRecipientContact = findViewById(R.id.textViewRecipientContact);
        textViewPickupLocation = findViewById(R.id.textViewPickupLocation);
        textViewPreferredDateTime = findViewById(R.id.textViewPreferredDateTime);
        textViewSpecialInstructions = findViewById(R.id.textViewSpecialInstructions);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        fetchRecipientDetails();
    }

    private void fetchRecipientDetails() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            mDatabase.child("users")
                    .child(userId)
                    .child("recipients")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                String recipientName = dataSnapshot.child("recipientName").getValue(String.class);
                                String recipientContact = dataSnapshot.child("recipientContact").getValue(String.class);
                                String pickupLocation = dataSnapshot.child("pickupLocation").getValue(String.class);
                                String preferredDateTime = dataSnapshot.child("preferredDateTime").getValue(String.class);
                                String specialInstructions = dataSnapshot.child("specialInstructions").getValue(String.class);

                                // Update the TextViews with the retrieved data
                                textViewRecipientName.setText("Recipient Name: " + recipientName);
                                textViewRecipientContact.setText("Recipient Contact: " + recipientContact);
                                textViewPickupLocation.setText("Pickup Location: " + pickupLocation);
                                textViewPreferredDateTime.setText("Preferred Date and Time: " + preferredDateTime);
                                textViewSpecialInstructions.setText("Special Instructions: " + specialInstructions);
                            } else {
                                Toast.makeText(NotificationActivity.this, "Recipient details not found", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(NotificationActivity.this, "Failed to fetch recipient details", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "No user logged in", Toast.LENGTH_SHORT).show();
        }
    }
}
*/