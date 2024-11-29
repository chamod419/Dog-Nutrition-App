package com.example.happydog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
public class OrdersFragment extends Fragment {

    private RecyclerView ordersRecyclerView;
    private OrdersAdapter ordersAdapter;
    private List<Order> orderList = new ArrayList<>();
    private DatabaseReference ordersDatabaseReference;
    private FirebaseAuth auth;
    private TextView noOrdersTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_orders, container, false);

        ordersRecyclerView = view.findViewById(R.id.ordersRecyclerView);
        noOrdersTextView = view.findViewById(R.id.noOrdersTextView);

        ordersAdapter = new OrdersAdapter(orderList);
        ordersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ordersRecyclerView.setAdapter(ordersAdapter);

        auth = FirebaseAuth.getInstance();
        ordersDatabaseReference = FirebaseDatabase.getInstance().getReference("orders");

        loadOrders();

        return view;
    }

    private void loadOrders() {
        String userId = auth.getCurrentUser().getUid();
        ordersDatabaseReference.orderByChild("userId").equalTo(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                orderList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Order order = snapshot.getValue(Order.class);
                    if (order != null) {
                        orderList.add(order);
                    }
                }
                ordersAdapter.notifyDataSetChanged();

                // Update visibility based on the size of the orderList
                if (ordersAdapter.getItemCount() == 0) {
                    noOrdersTextView.setVisibility(View.VISIBLE);
                    ordersRecyclerView.setVisibility(View.GONE);
                } else {
                    noOrdersTextView.setVisibility(View.GONE);
                    ordersRecyclerView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
            }
        });
    }
}
