package com.example.happydog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
// ProductCatalogFragment.java
public class ProductCatalogFragment extends Fragment {

    private RecyclerView productRecyclerView;
    private ProductAdapter productAdapter;
    private List<SaleItem> saleItemList;
    private DatabaseReference mDatabase;
    private Spinner sortSpinner;

    private Spinner typeSpinner;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_product_catalog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize Firebase Database reference
        mDatabase = FirebaseDatabase.getInstance().getReference("products");

        // Fetch products from Firebase
        fetchProducts();

        // Setup RecyclerView
        productRecyclerView = view.findViewById(R.id.productRecyclerView);
        productRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        saleItemList = new ArrayList<>();
        productAdapter = new ProductAdapter(getContext(), saleItemList);
        productRecyclerView.setAdapter(productAdapter);

        // Setup sort spinner
        sortSpinner = view.findViewById(R.id.sortSpinner);
        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sortProducts(position);
            }



            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        typeSpinner =  view.findViewById(R.id.spinner2);
        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterByType(position);
            }



            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        // Set up filter button
        view.findViewById(R.id.filterButton).setOnClickListener(v -> {
            // Implement filter functionality here
        });

        // Set the review button click listener
//        productAdapter.setOnReviewButtonClickListener(this::showAddReviewPopup);
    }

    private void fetchProducts() {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                saleItemList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    SaleItem saleItem = snapshot.getValue(SaleItem.class);
                    if (saleItem != null) {
                        saleItemList.add(saleItem);
                    }
                }
                productAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sortProducts(int position) {
        switch (position) {
            case 0: // Price: Low to High
                Collections.sort(saleItemList, Comparator.comparingDouble(SaleItem::getPrice));
                break;
            case 1: // Price: High to Low
                Collections.sort(saleItemList, (p1, p2) -> Double.compare(p2.getPrice(), p1.getPrice()));
                break;
        }
        productAdapter.notifyDataSetChanged();
    }

    private void filterByType(int position) {
        String[] types = getResources().getStringArray(R.array.sort_options2); // Ensure you have an array in your resources
        String selectedType = types[position];

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                saleItemList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    SaleItem saleItem = snapshot.getValue(SaleItem.class);
                    if (saleItem != null && saleItem.getType().equals(selectedType)) {
                        saleItemList.add(saleItem);
                    }
                }
                productAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

//    private void showAddReviewPopup(String productId) {
//        // Create a dialog
//        Dialog dialog = new Dialog(getContext());
//
//        // Inflate the custom layout
//        View view = LayoutInflater.from(getContext()).inflate(R.layout.popup_add_review, null);
//
//        // Set the custom layout to the dialog
//        dialog.setContentView(view);
//
//        // Get references to the views in the custom layout
//        EditText reviewEditText = view.findViewById(R.id.reviewEditText);
//        Button submitReviewButton = view.findViewById(R.id.submitReviewButton);
//
//        // Set the button click listener
//        submitReviewButton.setOnClickListener(v -> {
//            String reviewText = reviewEditText.getText().toString().trim();
//            if (!reviewText.isEmpty()) {
//                // Handle the review submission (e.g., save to Firebase)
//                saveReviewToFirebase(productId, reviewText);
//                dialog.dismiss();
//            } else {
//                reviewEditText.setError("Review cannot be empty");
//            }
//        });
//
//        // Show the dialog
//        dialog.show();
//    }

    /*private void saveReviewToFirebase(String productId, String reviewText) {
        DatabaseReference reviewsRef = FirebaseDatabase.getInstance().getReference("reviews").child(productId);
        String reviewId = reviewsRef.push().getKey();
        Review review = new Review(reviewId, reviewText);
        if (reviewId != null) {
            reviewsRef.child(reviewId).setValue(review);
        }
    }*/
}
