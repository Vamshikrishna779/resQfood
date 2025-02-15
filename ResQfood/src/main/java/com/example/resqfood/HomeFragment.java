package com.example.resqfood;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import android.widget.Button;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.firestore.DocumentSnapshot;
import java.util.Timer;
import java.util.TimerTask;
import android.widget.ImageView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import java.util.List;
import android.widget.LinearLayout;


import android.content.Intent;
public class HomeFragment extends Fragment {
    private int[] slideImages = {
            R.drawable.slide_image_1,
            R.drawable.slide_image_2,
            R.drawable.slide_image_3
    };

    private String[] slideTitles = {
            "o",
            "o",
            "o"
    };

    private ViewPager2 viewPager;
    private SlidePagerAdapter adapter;
    private Timer timer;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
    private int currentPage = 0;
    private DatabaseReference mDatabase;
    private FirebaseFirestore db;
    private ImageView imageViewProfile;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        viewPager = view.findViewById(R.id.viewPager);
        adapter = new SlidePagerAdapter(getContext(), slideImages);
        viewPager.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mAuth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference().child("users");

        imageViewProfile = view.findViewById(R.id.imageViewProfile);

        loadProfileImage();





        LinearLayout addressLinearLayout = view.findViewById(R.id.addresslinear);

        addressLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the AddAddressActivity
                Intent intent = new Intent(getActivity(), AddAddressActivity.class);
                startActivity(intent);
            }
        });

        TabLayout tabLayout = view.findViewById(R.id.tabLayout);
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(slideTitles[position])
        ).attach();

        // Start auto-sliding
        startAutoSlide();

        SearchView searchView = view.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                String userId = getActivity().getIntent().getStringExtra("userId");
                Intent intent = new Intent(getActivity(), DonationDetailActivity.class);
                intent.putExtra("userId", userId);
                intent.putExtra("foodName", query);
                startActivity(intent);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        Button buttonDonate = view.findViewById(R.id.buttonDonate);
        // Set OnClickListener to handle button click
        buttonDonate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the FoodDonationActivity
                startActivity(new Intent(getActivity(), FoodDonationActivity.class));
            }
        });
        Button buttonBlog = view.findViewById(R.id.buttonBlog);
        // Set OnClickListener to handle button click
        buttonBlog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the FoodDonationActivity
                startActivity(new Intent(getActivity(), BlogActivity.class));
            }
        });
        Button buttonShare = view.findViewById(R.id.buttonShare);
        // Set OnClickListener to handle button click
        buttonShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the FoodDonationActivity
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                String driveLink = "https://drive.google.com/file/d/1ZHLxeCl2XK2qaYMkrkFhGvZzg43hEaTF/view?usp=sharing";
                String shareMessage = "Download our app from the following link: " + driveLink;
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                startActivity(Intent.createChooser(shareIntent, "Share app via"));

            }

        });
        Button buttonContact = view.findViewById(R.id.buttonContact);
        buttonContact.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // Start the FoodDonationActivity
                startActivity(new Intent(getActivity(), ViewActivity.class));
            }
        });

        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        retrieveAddressName();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Stop auto-sliding when the fragment is destroyed
        stopAutoSlide();
    }

    private void startAutoSlide() {
        if (timer != null) {
            return; // Timer is already running
        }

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                currentPage = (currentPage + 1) % slideImages.length;
                viewPager.post(() -> viewPager.setCurrentItem(currentPage));
            }
        }, 3000, 3000); // Change the second argument (3000) to set the delay between each slide in milliseconds
    }

    private void stopAutoSlide() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        retrieveAddressName();
    }

    private void retrieveAddressName() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference addressRef = FirebaseDatabase.getInstance().getReference()
                    .child("users")
                    .child(userId)
                    .child("address")
                    .child("address"); // Assuming "address" is the key for the address name

            addressRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String addressName = snapshot.getValue(String.class);
                        // Display the address name in your UI
                        View view = getView();
                        if (view != null) {
                            TextView addressTextView = view.findViewById(R.id.addressTextView);
                            addressTextView.setText(addressName);
                        }
                    } else {
                        // Address not found, handle accordingly
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle onCancelled event
                }
            });
        }
    }

    private void loadProfileImage() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            userRef.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String profileImage = snapshot.child("profileImage").getValue(String.class);

                        View rootView = getView(); // Get the root view of the fragment layout

                        if (rootView != null) {
                            // Find the imageViewProfile within the fragment layout
                            ImageView imageViewProfile = rootView.findViewById(R.id.imageViewProfile);
                            if (imageViewProfile != null) {
                                Glide.with(requireContext())
                                        .load(profileImage)
                                        .placeholder(R.drawable.profile_image)
                                        .error(R.drawable.profile_image)
                                        .into(imageViewProfile);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle database error
                }
            });
        }
    }


}
