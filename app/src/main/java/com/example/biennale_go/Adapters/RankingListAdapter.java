package com.example.biennale_go.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.biennale_go.R;
import com.example.biennale_go.Utility.RankingItem;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class RankingListAdapter extends RecyclerView.Adapter<RankingListAdapter.ViewHolder> {
    public List<RankingItem> items;
    private OnItemClick monItemClicklister;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView userName;
        public TextView userScore;
        public TextView userDistanceTraveled;
        public OnItemClick onItemClickListener;

        public ViewHolder(final View view, OnItemClick onItemClick) {
            super(view);
            userName = view.findViewById(R.id.quizName);
            userScore = view.findViewById(R.id.userScore);
            userDistanceTraveled = view.findViewById(R.id.userDistanceTraveled);
            this.onItemClickListener = onItemClick;
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onItemClickListener.onItemClick(getAdapterPosition());
        }
    }

    public RankingListAdapter(List<RankingItem> myDataset, OnItemClick onItemClick) {
        items = myDataset;
        this.monItemClicklister = onItemClick;
    }

    @Override
    public RankingListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ranking_item, parent, false);

        ViewHolder viewHolder = new ViewHolder(v, monItemClicklister);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.userName.setText(items.get(position).getName());
        holder.userScore.setText(String.valueOf(items.get(position).getScore()));
        holder.userDistanceTraveled.setText(String.valueOf(items.get(position).getDistanceTraveled()) + "km");
//        holder.userName.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//            }
//        });

//        holder.removeButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                db.collection("quizes").document(items.get(position)).delete();
//                removeItem(position);
//            }
//        });
    }

//    public void addItem(String item) {
//        items.add(item);
//    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void removeItem(int position) {
        items.remove(position);
        notifyDataSetChanged();
    }

    public interface OnItemClick {
        void onItemClick(int posision);
    }
}
