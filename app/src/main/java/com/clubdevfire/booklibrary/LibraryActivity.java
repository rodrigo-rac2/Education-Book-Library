package com.clubdevfire.booklibrary;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.clubdevfire.booklibrary.book.Book;
import com.clubdevfire.booklibrary.book.BookAdapter;
import com.clubdevfire.booklibrary.sql.Sql;
import com.google.android.material.snackbar.Snackbar;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Objects;

public class LibraryActivity extends AppCompatActivity {

    private FrameLayout mListLayout;
    private RecyclerView mRecyclerDocumentList;
    private LinearLayout mEmptyLayout;
    private LinearLayout mLoadingLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);

        mListLayout = findViewById(R.id.Library_FrameLayout_ListLayout);
        mRecyclerDocumentList = findViewById(R.id.Library_RecyclerView_List);
        mListLayout = findViewById(R.id.Library_FrameLayout_ListLayout);
        mEmptyLayout = findViewById(R.id.Library_LinearLayout_Empty);
        mLoadingLayout = findViewById(R.id.Library_LinearLayout_Loading);


        findViewById(R.id.Library_Button_Back).setOnClickListener(v -> {
            startActivity(new Intent(LibraryActivity.this, SplashActivity.class));
            finish();
        });
        setLoadingMode(true);
        mRecyclerDocumentList.setAdapter(new BookAdapter());
        mEmptyLayout.setVisibility(View.GONE);
        String query = "SELECT * FROM livros ORDER BY id DESC;";
        new Sql(this, query).setOnQueryCompleteListener(result -> {
            try {
                boolean isEmpty = true;
                mRecyclerDocumentList.setAdapter(new BookAdapter());
                while (result.next()) {
                    isEmpty = false;
                    ((BookAdapter) Objects.requireNonNull(mRecyclerDocumentList.getAdapter())).addBook(new Book(
                            result.getInt("id"),
                            result.getString("titulo"),
                            result.getString("autor"),
                            result.getString("editora"),
                            result.getString("url_img"),
                            result.getString("url_pdf"),
                            LibraryActivity.this)
                    );
                }
                if (isEmpty) mEmptyLayout.setVisibility(View.VISIBLE);
            } catch (SQLException | NullPointerException | IndexOutOfBoundsException exception) {
                setErro(exception);
                exception.printStackTrace();
            } finally {
                setLoadingMode(false);
            }
        }).start();
    }

    private void setLoadingMode(boolean enabled) {
        mLoadingLayout.setVisibility(enabled ? View.VISIBLE : View.GONE);
    }

    private void setErro(Exception exception) {
        ((Vibrator) getSystemService(Context.VIBRATOR_SERVICE)).vibrate(50);
        Snackbar.make(mListLayout, R.string.erro_carregar_livros, Snackbar.LENGTH_LONG).setAction("Relatório", v -> {
            View alertContent = getLayoutInflater().inflate(R.layout.dialog_message_alert, findViewById(R.id.Message_Root));
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setView(alertContent);
            Dialog dialog = alert.show();
            alertContent.findViewById(R.id.Message_Button).setOnClickListener(bt -> {
                dialog.setCancelable(false);
                ((Button) bt).setText(R.string.wait);
                bt.setEnabled(false);
                StringBuilder log = new StringBuilder(exception.toString() + " ");
                for(StackTraceElement stack : exception.getStackTrace()){
                    log.append(stack.toString()).append(" ");
                }
                if(log.length()>4999) log = new StringBuilder(log.substring(0, 4999));
                new Sql(LibraryActivity.this, "INSERT INTO ERRORS VALUES('" + log.toString().replace("'", "*") + "', '" + BuildConfig.VERSION_NAME + "', '" + Calendar.getInstance().getTime() + "');").setOnQueryCompleteListener(result -> {
                    ((Button) bt).setText(R.string.enviado);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        bt.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                    }
                    new Handler().postDelayed(dialog::dismiss, 2000);
                }).start();
            });
        }).show();
    }
}