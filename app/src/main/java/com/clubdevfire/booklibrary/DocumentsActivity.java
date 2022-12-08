package com.clubdevfire.booklibrary;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.clubdevfire.booklibrary.document.Document;
import com.clubdevfire.booklibrary.document.DocumentAdapter;
import com.clubdevfire.booklibrary.document.DocumentUtils;
import com.clubdevfire.booklibrary.sql.Sql;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Objects;

public class DocumentsActivity extends AppCompatActivity {
    
    private TabLayout mTabsDisciplines;
    private RecyclerView mRecyclerDocumentList;
    private FrameLayout mListLayout;
    private LinearLayout mEmptyLayout;
    private LinearLayout mLoadingLayout;
    private String mTurma;
    private String mAno;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_documents);
        
        mTabsDisciplines = findViewById(R.id.Documents_TabLayout_Tabs);
        mRecyclerDocumentList = findViewById(R.id.Documents_RecyclerView_List);
        mListLayout = findViewById(R.id.Documents_FrameLayout_ListLayout);
        mEmptyLayout = findViewById(R.id.Documents_LinearLayout_Empty);
        mLoadingLayout = findViewById(R.id.Documents_LinearLayout_Loading);
        
        mTurma = getIntent().getExtras().getString("turma");
        mAno = getIntent().getExtras().getString("ano");
        
        try {
            File file = new File(getFilesDir(), DocumentUtils.FILE_NAME);
            if (file.createNewFile()) {
                Snackbar.make(mListLayout, "Agora você pode marcar documentos.", Snackbar.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
        }
        
        String anoText = (!mAno.equals("EJA") ? "º Ano" : "");
        String turmaText = (!mAno.equals("EJA") ? "Turma" : "Módulo");
        String anoTurmaText = String.format(new Locale("pt", "BR"), "%s%s - %s %s", mAno, anoText, turmaText, mTurma);
        ((TextView) findViewById(R.id.Documents_TextView_Turma)).setText(anoTurmaText);
        
        findViewById(R.id.Documents_Button_Back).setOnClickListener(v -> {
            startActivity(new Intent(DocumentsActivity.this, SplashActivity.class));
            finish();
        });
        
        mTabsDisciplines.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                loadTab(String.valueOf(tab.getText()));
            }
            
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        
        loadTab(String.valueOf(Objects.requireNonNull(mTabsDisciplines.getTabAt(mTabsDisciplines.getSelectedTabPosition())).getText()));
    }
    
    private void loadTab(String discipline) {
        setLoadingMode(true);
        mRecyclerDocumentList.setAdapter(new DocumentAdapter());
        mEmptyLayout.setVisibility(View.GONE);
        String query = "SELECT * FROM documentos WHERE disciplina='" + discipline + "' AND ano='" + mAno + "' AND turma='" + mTurma + "' ORDER BY data DESC;";
        new Sql(this, query).setOnQueryCompleteListener(result -> {
            try {
                DocumentUtils.checkLoad(DocumentsActivity.this);
            } catch (IOException ioException) {
                // erro
            }
            
            try {
                boolean isEmpty = true;
                mRecyclerDocumentList.setAdapter(new DocumentAdapter());
                while (result.next()) {
                    isEmpty = false;
                    ((DocumentAdapter) Objects.requireNonNull(mRecyclerDocumentList.getAdapter())).addDocument(new Document(
                            result.getInt("id"),
                            result.getString("nome"),
                            result.getString("autor"),
                            result.getString("data"),
                            result.getString("link"),
                            DocumentsActivity.this)
                    );
                }
                if (isEmpty) mEmptyLayout.setVisibility(View.VISIBLE);
            } catch (SQLException | NullPointerException | IndexOutOfBoundsException exception) {
                // erro
                exception.printStackTrace();
            } finally {
                setLoadingMode(false);
            }
        }).start();
    }
    
    private void setLoadingMode(boolean enabled) {
        for (int index = 0; index < mTabsDisciplines.getTabCount(); index++) {
            Objects.requireNonNull(mTabsDisciplines.getTabAt(index)).view.setEnabled(!enabled);
        }
        mLoadingLayout.setVisibility(enabled ? View.VISIBLE : View.GONE);
    }
    

    
}