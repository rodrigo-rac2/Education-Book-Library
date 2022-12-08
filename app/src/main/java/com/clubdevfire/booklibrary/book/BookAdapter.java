package com.clubdevfire.booklibrary.book;

import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {

    final List<Book> bookList;

    public BookAdapter() {
        this.bookList = new ArrayList<>();
    }

    public static class BookViewHolder extends RecyclerView.ViewHolder {
        public final LinearLayout layout;

        public BookViewHolder(@NonNull LinearLayout itemView) {
            super(itemView);
            layout = itemView;
            layout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }

        public void setBook(Book book) {
            if (book.getParent() != null)
                ((LinearLayout) book.getParent()).removeAllViews();
            layout.removeAllViews();
            layout.addView(book);
        }
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BookViewHolder(new LinearLayout(parent.getContext()));
    }

    @Override
    public void onBindViewHolder(BookViewHolder holder, int position) {
        holder.setBook(bookList.get(position));
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    public void addBook(Book book) {
        book.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        bookList.add(book);
    }

}
