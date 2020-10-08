package com.example.biennale_go.Adapters;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.biennale_go.MapsActivity;
import com.example.biennale_go.R;
import com.example.biennale_go.Utility.RankingItem;
import com.example.biennale_go.Utility.RoadListItem;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;


public class RoadListAdapter extends RecyclerView.Adapter<RoadListAdapter.ViewHolder> {
    public List<RoadListItem> items;
    private OnRoadItemClick onItemClicklister;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private int i = 0;
    private ArrayList<String> scores = new ArrayList<>();

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView name;
        public ImageView picture;
        public TextView description;
        public Button mapButton;
        public OnRoadItemClick onItemClickListener;
        public RelativeLayout mainLayout;
        public RelativeLayout subItem;

        public ViewHolder(final View view, OnRoadItemClick onItemClickListener) {
            super(view);
            picture = view.findViewById(R.id.roadImage);
            name = view.findViewById(R.id.road_name);
            description = view.findViewById(R.id.road_description);
            mapButton = view.findViewById(R.id.open_road_map);
            subItem = view.findViewById(R.id.main_layout);
            subItem = view.findViewById(R.id.sub_item);

            this.onItemClickListener = onItemClickListener;
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            onItemClickListener.onRoadItemClick(getAdapterPosition());
        }

        private void bind(RoadListItem item) {
            // Get the state
            boolean expanded = item.isExpanded();
            // Set the visibility based on state
            subItem.setVisibility(expanded ? View.VISIBLE : View.GONE);


        }
    }

    public RoadListAdapter(List<RoadListItem> myDataset, OnRoadItemClick onItemClickListener) {
        items = myDataset;
        this.onItemClicklister = onItemClickListener;
    }

    @Override
    public RoadListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.roads_item, parent, false);

        ViewHolder viewHolder = new ViewHolder(v, onItemClicklister);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.picture.setImageBitmap(items.get(position).getImage());

        holder.name.setText(items.get(position).getName());
        holder.description.setText(items.get(position).getDescription());
        holder.mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.name.getContext(), MapsActivity.class);
                Bundle b = new Bundle();
                b.putSerializable("polyline", items.get(position).getPolyline());
                b.putString("polylineColor", items.get(position).getColor());
                intent.putExtras(b);
                holder.name.getContext().startActivity(intent);
            }
        });
//        holder.name.setAnimation(AnimationUtils.loadAnimation(holder.name.getContext(),R.anim.item_anim));
//        holder.picture.setAnimation(AnimationUtils.loadAnimation(holder.picture.getContext(),R.anim.item_anim));


        RoadListItem movie = items.get(position);
        // Set movie data
        holder.bind(movie);

        // Set movie data
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the current state of the item
                boolean expanded = movie.isExpanded();
                // Change the state
//                if (movie.isExpanded()){
//                    holder.mainLayout.height
//                }
                movie.setExpanded(!expanded);
                // Notify the adapter that item has changed
                RoadListAdapter.this.notifyItemChanged(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public interface OnRoadItemClick {
        void onRoadItemClick(int position);
    }
}
