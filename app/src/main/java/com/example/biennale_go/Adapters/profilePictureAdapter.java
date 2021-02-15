package com.example.biennale_go.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.biennale_go.R;
import com.example.biennale_go.Utility.MenuListItem;
import com.example.biennale_go.Utility.ProfilPictureItem;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;


public class profilePictureAdapter extends RecyclerView.Adapter<profilePictureAdapter.ViewHolder> {
    public List<ProfilPictureItem> items;
    private OnProfilePictureItemClick onItemClicklister;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private int i = 0;
    private ArrayList<String> scores = new ArrayList<>();

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView picture;
        public TextView userName;
        public OnProfilePictureItemClick onItemClickListener;

        public ViewHolder(final View view, OnProfilePictureItemClick onItemClickListener) {
            super(view);
            picture = view.findViewById(R.id.picutre);
            userName = view.findViewById(R.id.quizName);
            this.onItemClickListener = onItemClickListener;
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onItemClickListener.onProfilePictureItemClick(getAdapterPosition(),"");
        }
    }

    public profilePictureAdapter(List<ProfilPictureItem> myDataset, OnProfilePictureItemClick onItemClickListener) {
        items = myDataset;
        this.onItemClicklister = onItemClickListener;
    }

    @Override
    public profilePictureAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.profile_picture_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(v, onItemClicklister);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        int pos = position + 1;
        holder.picture.setImageDrawable(items.get(position).getImage());
        holder.itemView.setOnClickListener(v -> {
            for (int i = 0; i < items.size();i++) {
                if (i != position) {
                    items.get(i).getImage().setColorFilter(holder.itemView.getResources().getColor(R.color.grayColor), PorterDuff.Mode.SRC_IN);
                }
                else
                    items.get(position).getImage().setColorFilter(holder.itemView.getResources().getColor(R.color.com_facebook_blue), PorterDuff.Mode.SRC_IN);
            }
            notifyDataSetChanged();
            onItemClicklister.onProfilePictureItemClick(position, items.get(position).getImageName());
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public interface OnProfilePictureItemClick {
        void onProfilePictureItemClick(int position, String imageName);
    }
}
