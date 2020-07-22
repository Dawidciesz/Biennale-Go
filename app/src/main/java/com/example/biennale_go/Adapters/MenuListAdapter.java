package com.example.biennale_go.Adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.biennale_go.R;
import com.example.biennale_go.Utility.CurrentUser;
import com.example.biennale_go.Utility.MenuListItem;
import com.example.biennale_go.Utility.RankingItem;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;


public class MenuListAdapter extends RecyclerView.Adapter<MenuListAdapter.ViewHolder> {
    public List<MenuListItem> items;
    private OnMenuItemClick onItemClicklister;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private int i = 0;
    private ArrayList<String> scores = new ArrayList<>();

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView picture;
        public TextView userName;
        public OnMenuItemClick onItemClickListener;

        public ViewHolder(final View view, OnMenuItemClick onItemClickListener) {
            super(view);
            picture = view.findViewById(R.id.picutre);
            userName = view.findViewById(R.id.quizName);
            this.onItemClickListener = onItemClickListener;
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            onItemClickListener.onMenuItemClick(getAdapterPosition());
        }
    }

    public MenuListAdapter(List<MenuListItem> myDataset, OnMenuItemClick onItemClickListener) {
        items = myDataset;
        this.onItemClicklister = onItemClickListener;
    }

    @Override
    public MenuListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.menu_listg, parent, false);

        ViewHolder viewHolder = new ViewHolder(v, onItemClicklister);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        int pos = position + 1;
        holder.picture.setImageDrawable(items.get(position).getImage());
        holder.userName.setText(items.get(position).getName());
        holder.userName.setAnimation(AnimationUtils.loadAnimation(holder.userName.getContext(),R.anim.item_anim));
        holder.picture.setAnimation(AnimationUtils.loadAnimation(holder.picture.getContext(),R.anim.item_anim));

    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public interface OnMenuItemClick {
        void onMenuItemClick(int position);
    }
}
