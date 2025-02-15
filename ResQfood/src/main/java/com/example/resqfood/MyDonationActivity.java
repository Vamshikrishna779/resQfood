package com.example.resqfood;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class MyDonationActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MyDonationAdapter donationAdapter;
    private List<Donation> donationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_lists);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        donationList = new ArrayList<>();
        donationAdapter = new MyDonationAdapter(donationList);
        recyclerView.setAdapter(donationAdapter);

        // Retrieve donations for the current user from Realtime Database
        retrieveDonations();
    }

    private void retrieveDonations() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child("users")
                .child(userId)
                .child("donations")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        donationList.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Donation donation = snapshot.getValue(Donation.class);
                            if (donation != null) {
                                donationList.add(donation);
                            }
                        }
                        donationAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(MyDonationActivity.this, "Failed to retrieve donations", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public class MyDonationAdapter extends RecyclerView.Adapter<MyDonationAdapter.ViewHolder> {

        private List<Donation> donationList;

        public MyDonationAdapter(List<Donation> donationList) {
            this.donationList = donationList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_donation, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Donation donation = donationList.get(position);
            holder.bind(donation);
        }

        @Override
        public int getItemCount() {
            return donationList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private TextView textViewFoodName;
            private TextView textViewQuantity;
            private TextView textViewExpirationDate;
            private ImageView imageViewFood;
            private Button buttonContactDonor;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                textViewFoodName = itemView.findViewById(R.id.textViewFoodName);
                textViewQuantity = itemView.findViewById(R.id.textViewQuantity);
                textViewExpirationDate = itemView.findViewById(R.id.textViewExpirationDate);
                imageViewFood = itemView.findViewById(R.id.imageViewFood);
                buttonContactDonor = itemView.findViewById(R.id.buttonContactDonor);
            }

            public void bind(Donation donation) {
                textViewFoodName.setText(donation.getFoodName());
                textViewQuantity.setText(String.valueOf(donation.getQuantity()));
                textViewExpirationDate.setText(donation.getExpirationDate());
                // Load image into imageViewFood using Picasso or Glide
                Glide.with(itemView.getContext()).load(donation.getImageUrl()).into(imageViewFood);

                buttonContactDonor.setOnClickListener(v -> {
                    // Implement button click action (e.g., open email or phone number)
                });
            }
        }
    }
}
