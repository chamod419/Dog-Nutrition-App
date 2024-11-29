package com.example.happydog;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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
public class ShoppingCartFragment extends Fragment {

    private RecyclerView cartRecyclerView;
    private TextView totalAmountTextView, emptyCartTextView;
    private Button placeOrderButton;
    private CartItemAdapter cartItemAdapter;
    private List<CartItem> cartItemList = new ArrayList<>();
    private DatabaseReference cartDatabaseReference;
    private DatabaseReference ordersDatabaseReference;
    private double totalAmount = 0.0;
    private FirebaseAuth auth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shopping_cart, container, false);

        cartRecyclerView = view.findViewById(R.id.cartRecyclerView);
        totalAmountTextView = view.findViewById(R.id.totalAmountTextView);
        emptyCartTextView = view.findViewById(R.id.emptyCartTextView);
        placeOrderButton = view.findViewById(R.id.placeOrderButton);
        auth = FirebaseAuth.getInstance();

        cartDatabaseReference = FirebaseDatabase.getInstance().getReference("carts");
        ordersDatabaseReference = FirebaseDatabase.getInstance().getReference("orders");
        cartItemAdapter = new CartItemAdapter(getContext(), cartItemList);

        cartRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        cartRecyclerView.setAdapter(cartItemAdapter);

        loadCartItems();

        placeOrderButton.setOnClickListener(v -> placeOrder());

        return view;
    }

    private void loadCartItems() {
        String userId = auth.getCurrentUser().getUid();
        cartDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                cartItemList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    CartItem cartItem = snapshot.getValue(CartItem.class);
                    if (cartItem != null && userId.equals(cartItem.getUserId())) {
                        cartItemList.add(cartItem);
                        showToast("Added item: " + cartItem.getProductName());
                    } else {
                    }
                }

                cartItemAdapter.notifyDataSetChanged();
                calculateTotalAmount();
                checkIfCartIsEmpty();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                showToast("Failed to load cart items: " + databaseError.getMessage());
            }
        });
    }

    private void checkIfCartIsEmpty() {
        if (cartItemList.isEmpty()) {
            cartRecyclerView.setVisibility(View.GONE);
            totalAmountTextView.setVisibility(View.GONE);
            emptyCartTextView.setVisibility(View.VISIBLE);
        } else {
            cartRecyclerView.setVisibility(View.VISIBLE);
            totalAmountTextView.setVisibility(View.VISIBLE);
            emptyCartTextView.setVisibility(View.GONE);
        }
    }

    private void calculateTotalAmount() {
        totalAmount = 0.0;
        for (CartItem cartItem : cartItemList) {
            double itemTotal = cartItem.getProductPrice() * cartItem.getQuantity();
            totalAmount += itemTotal;
        }
        totalAmountTextView.setText(String.format("Total: $%.2f", totalAmount));
    }

    private void placeOrder() {
        String userId = auth.getCurrentUser().getUid();

        // Retrieve the payment method for the user
        DatabaseReference userDatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId);
        userDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                petOwner petOwner = dataSnapshot.getValue(petOwner.class);
                if (petOwner != null) {
                    String paymentMethod = petOwner.getPaymentMethod();

                    if ("Online Card".equalsIgnoreCase(paymentMethod)) {
                        showCardDetailsDialog();
                    } else if ("COD - Cash On Delivery".equalsIgnoreCase(paymentMethod)) {
                        processOrder(userId, "COD - Cash On Delivery");
                    } else {
                        showToast("Unknown payment method: " + paymentMethod);
                    }
                } else {
                    showToast("User details not found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                showToast("Failed to get user payment method: " + databaseError.getMessage());
            }
        });
    }

    private void showCardDetailsDialog() {
        CardDetailsDialog dialog = new CardDetailsDialog(getContext(), this::processOrderWithCard);
        dialog.show();
    }

    private void processOrderWithCard(String cardNumber, String cardExpiry, String cardCvc) {
        // Validate card details
        if (isValidCardDetails(cardNumber, cardExpiry, cardCvc)) {
            String userId = auth.getCurrentUser().getUid();
            processOrder(userId, "Card");
        } else {
            showToast("Invalid card details");
        }
    }

    private boolean isValidCardDetails(String cardNumber, String cardExpiry, String cardCvc) {
        // Implement your card validation logic here
        return !cardNumber.isEmpty() && !cardExpiry.isEmpty() && !cardCvc.isEmpty();
    }

    private void processOrder(String userId, String paymentMethod) {
        String orderId = ordersDatabaseReference.push().getKey();

        Order order = new Order(orderId, userId, cartItemList, totalAmount, "Pending", paymentMethod);

        ordersDatabaseReference.child(orderId).setValue(order)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Order placed successfully, now clear the cart
                        clearCart(userId);
                        showToast("Order placed successfully");
                    } else {
                        showToast("Failed to place order: " + task.getException().getMessage());
                    }
                });
    }

    private void clearCart(String userId) {
        cartDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> keysToRemove = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    CartItem cartItem = snapshot.getValue(CartItem.class);
                    if (cartItem != null && userId.equals(cartItem.getUserId())) {
                        keysToRemove.add(snapshot.getKey());
                    }
                }
                for (String key : keysToRemove) {
                    cartDatabaseReference.child(key).removeValue()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    showToast("Cart item removed successfully");
                                } else {
                                    showToast("Failed to remove cart item: " + task.getException().getMessage());
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                showToast("Failed to load cart items for removal: " + databaseError.getMessage());
            }
        });
    }


    private void showToast(String message) {
        if (getActivity() != null) {
            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        } else {
            Log.e("ShoppingCartFragment", "Context or Activity is null, cannot show Toast");
        }
    }

}
