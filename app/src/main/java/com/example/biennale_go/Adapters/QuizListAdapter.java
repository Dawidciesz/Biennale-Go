package com.example.biennale_go.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.biennale_go.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class QuizListAdapter extends RecyclerView.Adapter<QuizListAdapter.ViewHolder> {
    public List<String> items;
    private OnItemClick monItemClicklister;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView textView;
        public OnItemClick onItemClickListener;
        public Button removeButton;

        public ViewHolder(final View view, OnItemClick onItemClick) {
            super(view);
            textView = view.findViewById(R.id.quizItem);
            this.onItemClickListener = onItemClick;
            removeButton = (Button) view.findViewById(R.id.deleteButton);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onItemClickListener.onItemClick(getAdapterPosition());
        }
    }

    public QuizListAdapter(List<String> myDataset, OnItemClick onItemClick) {
        items = myDataset;
        this.monItemClicklister = onItemClick;
    }

    @Override
    public QuizListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_quiz_item, parent, false);

        ViewHolder viewHolder = new ViewHolder(v, monItemClicklister);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.textView.setText(items.get(position));
        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        holder.removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("quizes").document(items.get(position)).delete();
                removeItem(position);
            }
        });
    }

    public void addItem(String item) {
        items.add(item);
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
