package com.example.resqfood;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.auth.FirebaseUser;

import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class AccountFragmentt extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_account_recipient, container, false);

        TextView textViewProfile = rootView.findViewById(R.id.textViewProfile);
        textViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ProfileActivity.class));
            }
        });

        TextView textViewMyLists = rootView.findViewById(R.id.textViewMyLists);
        textViewMyLists.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser != null) {
                    String userId = currentUser.getUid();
                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                String role = dataSnapshot.child("role").getValue(String.class);
                                if ("Donor".equals(role)) {
                                    startActivity(new Intent(getActivity(), MyListsActivity.class));
                                } else {
                                    Toast.makeText(getActivity(), "You do not have access to My Lists", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(getActivity(), "Failed to fetch user data", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(getActivity(), "User not authenticated", Toast.LENGTH_SHORT).show();
                }
            }
        });


        TextView textViewAddress = rootView.findViewById(R.id.textViewAddress);

        // Set click listener
        textViewAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to MyAddressActivity
                Intent intent = new Intent(getActivity(), AddAddressActivity.class);
                startActivity(intent);
            }
        });
        TextView textViewNotification = rootView.findViewById(R.id.textViewNotification);
        textViewNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), RecipientHistoryActivity.class);
                startActivity(intent);
            }
        });




        return rootView;
    }
}
