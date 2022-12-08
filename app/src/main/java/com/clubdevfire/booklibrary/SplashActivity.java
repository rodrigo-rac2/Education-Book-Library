package com.clubdevfire.booklibrary;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.goodiebag.horizontalpicker.HorizontalPicker;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

public class SplashActivity extends AppCompatActivity {

    private HorizontalPicker mPickerAno;
    private HorizontalPicker mPickerTurma;
    private Button mButtonDocuments;
    private TextView mTextTurma;
    private Button mButtonLibrary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mTextTurma = findViewById(R.id.Splash_Text_Turma);
        mPickerAno = findViewById(R.id.Splash_Ano);
        mPickerTurma = findViewById(R.id.Splash_Turma);
        mButtonDocuments = findViewById(R.id.Splash_Button);
        mButtonLibrary = findViewById(R.id.Splash_Library);

        ((TextView) findViewById(R.id.Splash_Version)).setText(String.format("Versão %s", BuildConfig.VERSION_NAME));

        List<HorizontalPicker.PickerItem> mAnoItems = new ArrayList<>();
        List<HorizontalPicker.PickerItem> mTurmaItems = new ArrayList<>();
        mAnoItems.add(new HorizontalPicker.TextItem("4"));
        mAnoItems.add(new HorizontalPicker.TextItem("5"));
        mAnoItems.add(new HorizontalPicker.TextItem("6"));
        mAnoItems.add(new HorizontalPicker.TextItem("7"));
        mAnoItems.add(new HorizontalPicker.TextItem("8"));
        mAnoItems.add(new HorizontalPicker.TextItem("9"));
        mAnoItems.add(new HorizontalPicker.TextItem("EJA"));
        mPickerAno.setChangeListener((horizontalPicker, anoIndex) -> {
            mTurmaItems.clear();
            String[] turmas = new String[]{"A", "B", "C", "D", "E"};
            if (anoIndex == 4 || anoIndex == 5) turmas = new String[]{"A", "B", "C", "D"};
            if (anoIndex == 6) turmas = new String[]{"VII", "VIII"};
            for (String turma : turmas) mTurmaItems.add(new HorizontalPicker.TextItem(turma));
            mPickerTurma.setItems(mTurmaItems, 0);
        });
        mPickerAno.setItems(mAnoItems, getSharedPreferences("savedStates", MODE_PRIVATE).getInt("lastAnoIndex", 0));
        mPickerTurma.setChangeListener((horizontalPicker, i) -> {
            if (i >= 0) {
                ((Vibrator) getSystemService(Context.VIBRATOR_SERVICE)).vibrate(50);
                refreshTurmaTextView();
            }
        });

        mButtonDocuments.setOnClickListener(v -> {
            getSharedPreferences("savedStates", MODE_PRIVATE).edit().putInt("lastAnoIndex", mPickerAno.getSelectedIndex()).apply();
            getSharedPreferences("savedStates", MODE_PRIVATE).edit().putInt("lastTurmaIndex", mPickerTurma.getSelectedIndex()).apply();
            mPickerAno.setEnabled(false);
            mPickerTurma.setEnabled(false);
            mButtonDocuments.setEnabled(false);
            mButtonLibrary.setEnabled(false);
            startRippleAnimation();
            new Handler().postDelayed(() -> {
                Intent documentsActivityIntent = new Intent(SplashActivity.this, DocumentsActivity.class);
                documentsActivityIntent.putExtra("ano", mPickerAno.getSelectedItem().getText());
                documentsActivityIntent.putExtra("turma", mPickerTurma.getSelectedItem().getText());
                startActivity(documentsActivityIntent);
                finish();
            }, 2000);
        });

        mButtonLibrary.setOnClickListener(v -> {
            getSharedPreferences("savedStates", MODE_PRIVATE).edit().putInt("lastAnoIndex", mPickerAno.getSelectedIndex()).apply();
            getSharedPreferences("savedStates", MODE_PRIVATE).edit().putInt("lastTurmaIndex", mPickerTurma.getSelectedIndex()).apply();
            mPickerAno.setEnabled(false);
            mPickerTurma.setEnabled(false);
            mButtonDocuments.setEnabled(false);
            mButtonLibrary.setEnabled(false);
            startRippleAnimation();
            new Handler().postDelayed(() -> {
                startActivity(new Intent(SplashActivity.this, LibraryActivity.class));
                finish();
            }, 2000);
        });

        mPickerTurma.setSelectedIndex(getSharedPreferences("savedStates", MODE_PRIVATE).getInt("lastTurmaIndex", 0));

        refreshTurmaTextView();
        new Handler().postDelayed(this::startInitAnimations, 2000);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        if (newBase.getResources().getConfiguration().fontScale > 0.85f) {
            final Configuration override = new Configuration(newBase.getResources().getConfiguration());
            override.fontScale = 0.85f;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                applyOverrideConfiguration(override);
            }
        }
    }

    private void startInitAnimations() {
        findViewById(R.id.Splash_ProgressBarInit).setVisibility(View.INVISIBLE);
        LinearLayout layContent = findViewById(R.id.Splash_Layout);
        int layMainHeight = layContent.getPaddingTop() + layContent.getPaddingBottom();
        for (int childIndex = 0; childIndex < layContent.getChildCount(); childIndex++) {
            int wrapSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            layContent.getChildAt(childIndex).measure(wrapSpec, wrapSpec);
            layMainHeight += layContent.getChildAt(childIndex).getMeasuredHeight();
            layMainHeight += ((ViewGroup.MarginLayoutParams) layContent.getChildAt(childIndex).getLayoutParams()).topMargin;
            layMainHeight += ((ViewGroup.MarginLayoutParams) layContent.getChildAt(childIndex).getLayoutParams()).bottomMargin;
        }
        ValueAnimator animLayCentral = ValueAnimator.ofInt(layContent.getHeight(), layMainHeight);
        animLayCentral.addUpdateListener(animation -> {
            ViewGroup.LayoutParams newLay = layContent.getLayoutParams();
            newLay.height = (int) animation.getAnimatedValue();
            layContent.setLayoutParams(newLay);
        });
        animLayCentral.setDuration(500);
        animLayCentral.start();

        LinearLayout layMain = findViewById(R.id.Splash_Lay_Logo);
        ValueAnimator animLayMain = ValueAnimator.ofInt(layMain.getHeight(), getResources().getDisplayMetrics().heightPixels / 3);
        animLayMain.addUpdateListener(animation -> {
            ViewGroup.LayoutParams newLay = layMain.getLayoutParams();
            newLay.height = (int) animation.getAnimatedValue();
            layMain.setLayoutParams(newLay);
        });
        animLayMain.setDuration(500);
        animLayMain.start();
    }

    private void startRippleAnimation() {
        ImageView ripple = findViewById(R.id.Splash_Lay_Ripple);
        AtomicBoolean statusPaint = new AtomicBoolean(false);
        ripple.setVisibility(View.VISIBLE);
        ValueAnimator anim = ValueAnimator.ofInt(0, getResources().getDisplayMetrics().heightPixels * 2);
        anim.addUpdateListener(animation -> {
            if (!statusPaint.get() && (int) animation.getAnimatedValue() > getResources().getDisplayMetrics().heightPixels) {
                statusPaint.set(true);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    getWindow().setStatusBarColor(Color.parseColor("#66BB6A"));
                }
            }
            ViewGroup.LayoutParams params = ripple.getLayoutParams();
            params.height = (int) animation.getAnimatedValue();
            params.width = (int) animation.getAnimatedValue();
            ripple.setLayoutParams(params);
        });
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                findViewById(R.id.Splash_Lay_Loading).setVisibility(View.VISIBLE);
                ValueAnimator alphaAnim = ValueAnimator.ofFloat(0, 1.0f);
                alphaAnim.addUpdateListener(value -> findViewById(R.id.Splash_Lay_Loading).setAlpha((Float) value.getAnimatedValue()));
                alphaAnim.setDuration(500);
                alphaAnim.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        anim.setDuration(1000);
        anim.start();
    }

    public void refreshTurmaTextView() {
        String mAno = mPickerAno.getSelectedItem().getText();
        String mTurma = mPickerTurma.getSelectedItem().getText();
        mTextTurma.setText(String.format(Locale.getDefault(), "%s%s - %s %s", mAno, (!mAno.equals("EJA") ? "º Ano" : ""), (!mAno.equals("EJA") ? "Turma" : "Módulo"), mTurma));
    }



}