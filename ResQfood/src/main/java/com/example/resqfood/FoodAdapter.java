package com.example.resqfood;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import android.content.Context;
import android.content.Intent;

import com.squareup.picasso.Picasso;
import java.util.List;
public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodViewHolder> {

    private Context context;
    private List<Donation> donationList;

    public FoodAdapter(Context context, List<Donation> donationList) {
        this.context = context;
        this.donationList = donationList;
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_donation, parent, false);
        return new FoodViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        Donation donation = donationList.get(position);
        holder.bind(donation);
        holder.buttonContactDonor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start CollectActivity
                Intent intent = new Intent(context, CollectActivity.class);
                intent.putExtra("foodName", donation.getFoodName());
                // Pass any data needed to CollectActivity// Example: Passing donation ID
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return donationList.size();
    }
   /* @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        Donation donation = donationList.get(position);
        holder.textViewFoodName.setText(donation.getFoodName());
        holder.textViewQuantity.setText(String.valueOf(donation.getQuantity()));
        holder.textViewExpirationDate.setText(donation.getExpirationDate());
        // You can set other donation information here if needed
        holder.imageViewFood.setImageURI(Uri.parse(donation.getImageUrl()));
        // Or use a library like Picasso or Glide to load images asynchronously
    holder.buttonContactDonor.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // Start CollectActivity
            Intent intent = new Intent(context, CollectActivity.class);
            // Pass any data needed to CollectActivity
            intent.putExtra("donationId", donation.getId()); // Example: Passing donation ID
            context.startActivity(intent);
        }
    });
}
*/


    public class FoodViewHolder extends RecyclerView.ViewHolder {
        TextView textViewFoodName;
        TextView textViewQuantity;
        TextView textViewExpirationDate;
        ImageView imageViewFood;
        Button buttonContactDonor;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewFoodName = itemView.findViewById(R.id.textViewFoodName);
            textViewQuantity = itemView.findViewById(R.id.textViewQuantity);
            textViewExpirationDate = itemView.findViewById(R.id.textViewExpirationDate);
            imageViewFood = itemView.findViewById(R.id.imageViewFood);
            buttonContactDonor = itemView.findViewById(R.id.buttonContactDonor);
            // Initialize other views here if needed
        }
        public void bind(Donation donation) {
            textViewFoodName.setText(donation.getFoodName());
            textViewQuantity.setText("Location: " + donation.getLocation());
            textViewExpirationDate.setText("Expires: " + donation.getExpirationDate());

            Picasso.get().load(donation.getImageUrl()).into(imageViewFood);
        }
    }
}

/*
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;
public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodViewHolder> {

    private List<Donation> donationList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Donation donation);
    }

    public FoodAdapter(List<Donation> donationList, OnItemClickListener listener) {
        this.donationList = donationList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_donation, parent, false);
        return new FoodViewHolder(view, listener, donationList);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        Donation donation = donationList.get(position);
        holder.bind(donation);
    }

    @Override
    public int getItemCount() {
        return donationList.size();
    }

    public static class FoodViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewFoodName;
        private TextView textViewQuantity;
        private TextView textViewExpirationDate;
        private ImageView imageViewFood;
        private OnItemClickListener listener;
        private List<Donation> donationList;

        public FoodViewHolder(@NonNull View itemView, OnItemClickListener listener, List<Donation> donationList) {
            super(itemView);
            this.listener = listener;
            this.donationList = donationList;
            textViewFoodName = itemView.findViewById(R.id.textViewFoodName);
            textViewQuantity = itemView.findViewById(R.id.textViewQuantity);
            textViewExpirationDate = itemView.findViewById(R.id.textViewExpirationDate);
            imageViewFood = itemView.findViewById(R.id.imageViewFood);
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(donationList.get(position));
                }
            });
        }

        public void bind(Donation donation) {
            textViewFoodName.setText(donation.getFoodName());
            textViewQuantity.setText("Quantity: " + donation.getQuantity());
            textViewExpirationDate.setText("Expiration Date: " + donation.getExpirationDate());

            Glide.with(itemView.getContext()).load(donation.getImageUrl()).into(imageViewFood);
        }
    }
}
*/