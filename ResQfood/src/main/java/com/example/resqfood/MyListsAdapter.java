package com.example.resqfood;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class MyListsAdapter extends RecyclerView.Adapter<MyListsAdapter.ViewHolder> {

    private List<Donation> donationList;
    private OnDeleteClickListener deleteClickListener;

    public interface OnDeleteClickListener {
        void onDeleteClick(int position);
    }

    public MyListsAdapter(List<Donation> donationList, OnDeleteClickListener deleteClickListener) {
        this.donationList = donationList;
        this.deleteClickListener = deleteClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mylist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Donation donation = donationList.get(position);
        holder.textViewFoodName.setText(donation.getFoodName());
        holder.textViewQuantity.setText(String.valueOf(donation.getQuantity()));
        holder.textViewExpirationDate.setText(donation.getExpirationDate());
        Glide.with(holder.itemView.getContext())
                .load(donation.getImageUrl())
                .apply(new RequestOptions().override(100, 100))
                .into(holder.imageViewFood);

        holder.btnDelete.setOnClickListener(v -> {
            deleteClickListener.onDeleteClick(position);
        });
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
        Button btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewFoodName = itemView.findViewById(R.id.textViewFoodName);
            textViewQuantity = itemView.findViewById(R.id.textViewQuantity);
            textViewExpirationDate = itemView.findViewById(R.id.textViewExpirationDate);
            imageViewFood = itemView.findViewById(R.id.imageViewFood);
            btnDelete = itemView.findViewById(R.id.deleteButton);
        }
    }
}

/*
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;
/*
public class MyListsAdapter extends RecyclerView.Adapter<MyListsAdapter.ViewHolder> {

    private List<Donation> donationList;
    private OnDeleteClickListener deleteClickListener;

    public interface OnDeleteClickListener {
        void onDeleteClick(int position);
    }

    public MyListsAdapter(List<Donation> donationList, OnDeleteClickListener deleteClickListener) {
        this.donationList = donationList;
        this.deleteClickListener = deleteClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mylist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Donation donation = donationList.get(position);
        holder.textViewFoodName.setText(donation.getFoodName());
        holder.textViewQuantity.setText(String.valueOf(donation.getQuantity()));
        holder.textViewExpirationDate.setText(donation.getExpirationDate());
        Glide.with(holder.itemView.getContext())
                .load(donation.getImageUrl())
                .apply(new RequestOptions().override(100, 100))
                .into(holder.imageViewFood);

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteClickListener.onDeleteClick(position);
            }
        });
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
        Button btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewFoodName = itemView.findViewById(R.id.textViewFoodName);
            textViewQuantity = itemView.findViewById(R.id.textViewQuantity);
            textViewExpirationDate = itemView.findViewById(R.id.textViewExpirationDate);
            imageViewFood = itemView.findViewById(R.id.imageViewFood);
            btnDelete = itemView.findViewById(R.id.deleteButton);
        }
    }
}



import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import java.util.List;
import android.content.Context;
public class MyListsAdapter extends RecyclerView.Adapter<MyListsAdapter.ViewHolder> {

    private List<Donation> donationList;

    public MyListsAdapter(List<Donation> donationList) {
        this.donationList = donationList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mylist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Donation donation = donationList.get(position);
        holder.bind(donation);
        holder.deleteButton.setOnClickListener(v -> {
            // Handle delete button click
            // You can either remove the item from the list or notify the activity to delete it from the database
        });
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
        Button deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewFoodName = itemView.findViewById(R.id.textViewFoodName);
            textViewQuantity = itemView.findViewById(R.id.textViewQuantity);
            textViewExpirationDate = itemView.findViewById(R.id.textViewExpirationDate);
            imageViewFood = itemView.findViewById(R.id.imageViewFood);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }

        public void bind(Donation donation) {
            textViewFoodName.setText(donation.getFoodName());
            textViewQuantity.setText(String.valueOf(donation.getQuantity()));
            textViewExpirationDate.setText(donation.getExpirationDate());
            // Load image using Glide or Picasso
            Glide.with(itemView).load(donation.getImageUrl()).into(imageViewFood);
            // Picasso.get().load(donation.getImageUrl()).into(imageViewFood);
        }
    }
}


/*
public class MyListsAdapter extends RecyclerView.Adapter<MyListsAdapter.ViewHolder> {

    private List<Donation> donationList;


    public MyListsAdapter(List<Donation> donationList, Context context) {
        this.donationList = donationList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mylist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Donation donation = donationList.get(position);
        holder.textViewFoodName.setText(donation.getFoodName());
        holder.textViewQuantity.setText(Integer.valueOf(donation.getQuantity()));
        holder.textViewExpirationDate.setText(donation.getExpirationDate());
        // Load image using Glide
        Glide.with(holder.itemView.getContext())
                .load(donation.getImageUrl())
                .apply(new RequestOptions().override(100, 100)) // Resize image if necessary
                .into(holder.imageViewFood);
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
*/