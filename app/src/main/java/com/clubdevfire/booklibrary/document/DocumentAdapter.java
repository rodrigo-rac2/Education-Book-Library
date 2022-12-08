package com.clubdevfire.booklibrary.document;

import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class DocumentAdapter extends RecyclerView.Adapter<DocumentAdapter.DocumentViewHolder> {

    final List<Document> documentList;

    public DocumentAdapter() {
        this.documentList = new ArrayList<>();
    }

    public static class DocumentViewHolder extends RecyclerView.ViewHolder {
        public final LinearLayout layout;

        public DocumentViewHolder(@NonNull LinearLayout itemView) {
            super(itemView);
            layout = itemView;
            layout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }

        public void setDocument(Document doc) {
            if (doc.getParent() != null)
                ((LinearLayout) doc.getParent()).removeAllViews();
            layout.removeAllViews();
            layout.addView(doc);
        }
    }

    @NonNull
    @Override
    public DocumentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DocumentViewHolder(new LinearLayout(parent.getContext()));
    }

    @Override
    public void onBindViewHolder(DocumentViewHolder holder, int position) {
        holder.setDocument(documentList.get(position));
    }

    @Override
    public int getItemCount() {
        return documentList.size();

    }

    public void addDocument(Document doc) {
        doc.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        documentList.add(doc);
    }
}