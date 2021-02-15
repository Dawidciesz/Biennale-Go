package com.example.biennale_go.Adapters;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.biennale_go.R;
import com.example.biennale_go.Utility.CurrentUser;
import com.example.biennale_go.Utility.RankingItem;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;


public class RankingListAdapter extends RecyclerView.Adapter<RankingListAdapter.ViewHolder> {
    public List<RankingItem> items;
    private OnItemClick monItemClicklister;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private int i = 0;
    private ArrayList<String> scores = new ArrayList<>();

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public LinearLayout item;
        public LinearLayout subItem;
        public TextView number;
        public TextView userName;
        public TextView userScore;
        public TextView userDistanceTraveled;
        public AppCompatImageView pointsImage, userImage;
        public AppCompatImageView kmImage;
        public TextView title;
        public TextView genre;
        public TextView year;



        public OnItemClick onItemClickListener;

        public ViewHolder(final View view, OnItemClick onItemClick) {
            super(view);
            item = view.findViewById(R.id.item);
            number = view.findViewById(R.id.number);
            userName = view.findViewById(R.id.quizName);
            userImage = view.findViewById(R.id.picutre);
            userScore = view.findViewById(R.id.points);
            pointsImage = view.findViewById(R.id.picutre_points);
            kmImage = view.findViewById(R.id.picutre_km);
            subItem = view.findViewById(R.id.sub_item);
            genre = view.findViewById(R.id.points);
            year = view.findViewById(R.id.km);
            userDistanceTraveled = view.findViewById(R.id.km);
            this.onItemClickListener = onItemClick;
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onItemClickListener.onItemClick(getAdapterPosition());
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(getAdapterPosition());
            }
        }

        private void bind(RankingItem item) {
            boolean expanded = item.isExpanded();
            subItem.setVisibility(expanded ? View.VISIBLE : View.GONE);
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
        int pos = position + 1;
        holder.userName.setText(items.get(position).getName());
        holder.userScore.setAnimation(AnimationUtils.loadAnimation(holder.userName.getContext(),R.anim.item_anim));
        if (CurrentUser.name.equals((holder.userName.getText().toString()))) {
            holder.item.setBackgroundColor(Color.rgb(153, 153, 153));
            holder.userName.setTextColor(Color.BLACK);
        }
        else {
            holder.item.setBackgroundColor(Color.rgb(255,255,255));
            holder.userName.setTextColor(Color.BLACK);
        }
        holder.number.setText(pos + "");
        holder.userScore.setAnimation(AnimationUtils.loadAnimation(holder.userName.getContext(),R.anim.item_anim));
        holder.userScore.setText(String.valueOf(items.get(position).getScore()));
        holder.userDistanceTraveled.setAnimation(AnimationUtils.loadAnimation(holder.userName.getContext(),R.anim.item_anim));
        holder.pointsImage.setAnimation(AnimationUtils.loadAnimation(holder.userName.getContext(),R.anim.item_anim));
        holder.kmImage.setAnimation(AnimationUtils.loadAnimation(holder.userName.getContext(),R.anim.item_anim));
        Resources res = holder.item.getResources();
        holder.userImage.setImageDrawable(res.getDrawable(Integer.parseInt(items.get(position).getProfilPictureId())));
        holder.userImage.setColorFilter(Color.parseColor(items.get(position).getProfilPictureColor()), PorterDuff.Mode.SRC_IN);
        DecimalFormat df = new DecimalFormat("###.###");
        df.setMinimumFractionDigits(2);
        holder.userDistanceTraveled.setText(String.valueOf(df.format(items.get(position).getDistanceTraveled()/1000) + "km"));
        RankingItem movie = items.get(position);
        holder.bind(movie);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean expanded = movie.isExpanded();
                movie.setExpanded(!expanded);
                RankingListAdapter.this.notifyItemChanged(position);
            }
        });
    }

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
