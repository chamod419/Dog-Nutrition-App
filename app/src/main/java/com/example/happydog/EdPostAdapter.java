package com.example.happydog;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
public class EdPostAdapter extends RecyclerView.Adapter<EdPostAdapter.EducationalPostViewHolder> {

    private List<EdPost> postList;

    public EdPostAdapter(List<EdPost> postList) {
        this.postList = postList;
    }

    @NonNull
    @Override
    public EducationalPostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_educational_post, parent, false);
        return new EducationalPostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EducationalPostViewHolder holder, int position) {
        EdPost post = postList.get(position);
        holder.titleTextView.setText(post.getTitle());
        holder.descriptionTextView.setText(post.getDescription());
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public static class EducationalPostViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView descriptionTextView;

        public EducationalPostViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.postTitleTextView);
            descriptionTextView = itemView.findViewById(R.id.postDescriptionTextView);
        }
    }
}
