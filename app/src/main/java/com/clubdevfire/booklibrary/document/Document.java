package com.clubdevfire.booklibrary.document;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.clubdevfire.booklibrary.R;

import java.io.IOException;
import java.util.Locale;

public class Document extends RelativeLayout {

    public Document(Context context) {
        super(context);
    }

    public Document(int id, String title, String author, String date, String uri, Context context) {
        super(context);
        setup(id, title, author, date, uri, context);
    }

    public void setup(int id, String title, String author, String date, String uri, Context context) {
        inflate(context, R.layout.layout_document, this);

        TextView mTitle = findViewById(R.id.Document_Title);
        TextView mAuthor = findViewById(R.id.Document_Author);
        TextView mDate = findViewById(R.id.Document_Date);
        TextView mId = findViewById(R.id.Document_Id);
        CheckBox mCheck = findViewById(R.id.Document_Check);

        if (DocumentUtils.CheckIsChecked(id)) mCheck.setChecked(true);

        mTitle.setText(title);
        mAuthor.setText(author);
        mDate.setText(date);
        mId.setText(String.format(Locale.getDefault(), "#%04d", id));
        mCheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
            try {
                if (isChecked) DocumentUtils.checkAdd(id, context);
                else DocumentUtils.checkRemove(id, context);
            } catch (IOException ioException) {
                mTitle.setError("Não foi possivel marcar este documento.");
            }
        });

        findViewById(R.id.Document_DownloadButton).setOnClickListener(v -> {
            try {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(uri)));
            } catch (ActivityNotFoundException ex) {
                mTitle.setError("");
            }
        });
    }

}
