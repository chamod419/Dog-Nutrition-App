package com.example.happydog;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.OrderViewHolder> {

    private List<Order> orders;

    public OrdersAdapter(List<Order> orders) {
        this.orders = orders;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orders.get(position);
        holder.orderIdTextView.setText("Order ID: " + order.getOrderId());
        holder.orderStatusTextView.setText("Status: " + order.getStatus());
        holder.orderTotalTextView.setText("Total: $" + order.getTotalAmount());
        holder.orderPaymentMethodTextView.setText("Payment Method: " + order.getPaymentMethod());

        // Clear previous views
        holder.orderItemsLayout.removeAllViews();

        // Add new product names
        for (CartItem item : order.getOrderItems()) {
            TextView productNameTextView = new TextView(holder.itemView.getContext());
            productNameTextView.setText(item.getProductName());
            holder.orderItemsLayout.addView(productNameTextView);
        }
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView orderIdTextView;
        TextView orderStatusTextView;
        TextView orderTotalTextView;
        TextView orderPaymentMethodTextView;
        LinearLayout orderItemsLayout;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderIdTextView = itemView.findViewById(R.id.orderIdTextView);
            orderStatusTextView = itemView.findViewById(R.id.orderStatusTextView);
            orderTotalTextView = itemView.findViewById(R.id.orderTotalTextView);
            orderPaymentMethodTextView = itemView.findViewById(R.id.orderPaymentMethodTextView);
            orderItemsLayout = itemView.findViewById(R.id.orderItemsLayout);
        }
    }
}
