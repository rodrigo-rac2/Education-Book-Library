package com.clubdevfire.booklibrary.book;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.clubdevfire.booklibrary.R;

import java.util.Locale;

public class Book extends RelativeLayout {

    public Book(Context context) {
        super(context);
    }

    public Book(int id, String title, String author, String editor, String url_img, String url_pdf, Context context) {
        super(context);
        setup(id, title, author, editor, url_img, url_pdf, context);
    }

    public void setup(int id, String title, String author, String editor, String url_img, String url_pdf, Context context) {
        inflate(context, R.layout.layout_book, this);

        ((TextView) findViewById(R.id.Book_Title)).setText(title);
        ((TextView) findViewById(R.id.Book_Author)).setText(author);
        ((TextView) findViewById(R.id.Book_Editor)).setText(editor);
        ((TextView) findViewById(R.id.Book_Id)).setText(String.format(Locale.getDefault(), "#%04d", id));
        findViewById(R.id.Book_DownloadButton).setOnClickListener(v -> {
            try {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url_pdf)));
            } catch (ActivityNotFoundException ex) {
                ((TextView) findViewById(R.id.Book_Title)).setError("");
            }
        });

        Glide.with(context).load(url_img).into(((ImageView) findViewById(R.id.Book_Image)));

        findViewById(R.id.Book_Image).setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);

            ImageView image = new ImageView(context);
            image.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (context.getResources().getDisplayMetrics().heightPixels / 1.5)));
            image.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            Glide.with(context).load(url_img).into(image);

            LinearLayout layout = new LinearLayout(context);
            layout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            layout.addView(image);

            builder.setView(layout);
            builder.show().getWindow().setBackgroundDrawable(null);

        });
    }
}
