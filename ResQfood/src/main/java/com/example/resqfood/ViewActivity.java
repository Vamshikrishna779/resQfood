package com.example.resqfood;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.firebase.database.ValueEventListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
public class ViewActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private RecyclerView recyclerView;
    private DonationAdapter adapter;
    private List<Donation> donationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        donationList = new ArrayList<>();
        adapter = new DonationAdapter(donationList);
        recyclerView.setAdapter(adapter);

        searchDonations();
    }

    private void searchDonations() {
        DatabaseReference usersRef = mDatabase.child("users");
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                donationList.clear();
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    DatabaseReference userDonationsRef = userSnapshot.child("donations").getRef();
                    userDonationsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot donationSnapshot : dataSnapshot.getChildren()) {
                                Donation donation = donationSnapshot.getValue(Donation.class);
                                if (donation != null) {
                                    donationList.add(donation);
                                }
                            }
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle database error
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
            }
        });
    }

    public static class DonationAdapter extends RecyclerView.Adapter<DonationAdapter.ViewHolder> {

        private List<Donation> donationList;

        public DonationAdapter(List<Donation> donationList) {
            this.donationList = donationList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Donation donation = donationList.get(position);
            holder.textViewFoodName.setText(donation.getFoodName());
            holder.textViewQuantity.setText(String.valueOf(donation.getLocation()));
            holder.textViewExpirationDate.setText(donation.getExpirationDate());

            Picasso.get().load(donation.getImageUrl()).into(holder.imageViewFood);
        }

        @Override
        public int getItemCount() {
            return donationList.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            TextView textViewFoodName;
            TextView textViewQuantity;
            TextView textViewExpirationDate;
            ImageView imageViewFood;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                textViewFoodName = itemView.findViewById(R.id.textViewFoodName);
                textViewQuantity = itemView.findViewById(R.id.textViewQuantity);
                textViewExpirationDate = itemView.findViewById(R.id.textViewExpirationDate);
                imageViewFood = itemView.findViewById(R.id.imageViewFood);
            }
        }
    }
}
