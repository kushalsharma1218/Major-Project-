package com.example.expressblog.Adapters;

import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expressblog.R;
import com.example.expressblog.entities.Draft;

import java.util.List;

public class DraftAdapter extends RecyclerView.Adapter<DraftAdapter.DraftViewHolder>{

    private List<Draft> drafts;

    public DraftAdapter(List<Draft> drafts) {
        this.drafts = drafts;
    }

    @NonNull
    @Override
    public DraftViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new DraftViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_container_draft,
                parent,
                false
        ));
    }

    @Override
    public void onBindViewHolder(@NonNull DraftViewHolder holder, int position) {
        holder.setDraft(drafts.get(position));
    }

    @Override
    public int getItemCount() {
        return drafts.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    static class DraftViewHolder extends RecyclerView.ViewHolder{

        TextView textTitle, textDesc, textDateTime, imagePath;
        public DraftViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.draft_title);
            textDateTime = itemView.findViewById(R.id.draft_time);
            textDesc = itemView.findViewById(R.id.draft_desc);
//            imagePath = itemView.findViewById(R.id.draft_image_path);
        }

        void setDraft(Draft draft)
        {
            textTitle.setText(draft.getTitle());
            textDateTime.setText(draft.getDateTime());
            textDesc.setText(draft.getDesc());
//            imagePath.setText(draft.getImagePath());
        }
    }
}
