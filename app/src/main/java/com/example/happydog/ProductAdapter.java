package com.example.happydog;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.UUID;
public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private Context context;
    private List<SaleItem> saleItemList;
    private DatabaseReference cartDatabaseReference;

    public ProductAdapter(Context context, List<SaleItem> saleItemList) {
        this.context = context;
        this.saleItemList = saleItemList;
        this.cartDatabaseReference = FirebaseDatabase.getInstance().getReference("carts");
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        SaleItem saleItem = saleItemList.get(position);

        holder.nameTextView.setText(saleItem.getName());
        holder.descriptionTextView.setText(saleItem.getDescription());
        holder.priceTextView.setText(String.format("$%.2f", saleItem.getPrice()));

        Picasso.get()
                .load(saleItem.getImageUrl())
                .placeholder(R.drawable.new_logo_reduced)
                .error(R.drawable.new_logo_reduced)
                .into(holder.imageView);

        holder.addToCartButton.setOnClickListener(v -> {
            addToCart(saleItem);
        });

        holder.reviewButton.setOnClickListener(v-> {
            addReview(saleItem.getId());
        });
    }

    @Override
    public int getItemCount() {
        return saleItemList.size();
    }


    private void addToCart(SaleItem saleItem) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String productId = saleItem.getId();

        DatabaseReference userCartRef = FirebaseDatabase.getInstance().getReference("carts");

        Query query = userCartRef.orderByChild("userId").equalTo(userId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean productExists = false;
                String cartItemId = null;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    CartItem cartItem = snapshot.getValue(CartItem.class);
                    if (cartItem != null && cartItem.getProductId().equals(productId)) {
                        productExists = true;
                        cartItemId = cartItem.getId();
                        int newQuantity = cartItem.getQuantity() + 1;

                        // Update quantity if product is already in the cart
                        userCartRef.child(cartItemId).child("quantity").setValue(newQuantity)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(context, "Product quantity updated in cart.", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(context, "Failed to update product quantity in cart.", Toast.LENGTH_SHORT).show();
                                });
                        break;
                    }
                }

                if (!productExists) {
                    cartItemId = UUID.randomUUID().toString();
                    CartItem cartItem = new CartItem(cartItemId, productId, userId, 1,
                            saleItem.getName(), saleItem.getDescription(), saleItem.getPrice(), saleItem.getImageUrl());

                    userCartRef.child(cartItemId).setValue(cartItem)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(context, "Product added to cart.", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(context, "Failed to add product to cart.", Toast.LENGTH_SHORT).show();
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, "Failed to check cart.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void addReview(String productId) {
        DatabaseReference reviewDatabaseReference = FirebaseDatabase.getInstance().getReference("reviews");
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Add a Review");
        View view = LayoutInflater.from(context).inflate(R.layout.popup_add_review, null);
        builder.setView(view);

        final EditText reviewEditText = view.findViewById(R.id.reviewEditText);
        Button submitReviewButton = view.findViewById(R.id.submitReviewButton);

        AlertDialog dialog = builder.create();

        submitReviewButton.setOnClickListener(v -> {
            String reviewText = reviewEditText.getText().toString().trim();
            if (reviewText.isEmpty()) {
                reviewEditText.setError("Review cannot be empty");
                return;
            }

            String reviewId = UUID.randomUUID().toString();
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

            Review review = new Review(reviewId, productId, userId, reviewText);

            reviewDatabaseReference.child(reviewId).setValue(review)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(context, "Review added successfully.", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(context, "Failed to add review.", Toast.LENGTH_SHORT).show();
                    });
        });

        dialog.show();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView nameTextView;
        TextView descriptionTextView;
        TextView priceTextView;
        Button addToCartButton;

        Button reviewButton;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.productImageView);
            nameTextView = itemView.findViewById(R.id.productNameTextView);
            descriptionTextView = itemView.findViewById(R.id.productDescriptionTextView);
            priceTextView = itemView.findViewById(R.id.productPriceTextView);
            addToCartButton = itemView.findViewById(R.id.addToCartButton);
            reviewButton = itemView.findViewById(R.id.addreviewbutton);
        }
    }
}
