package com.example.swiftmart.Account;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.swiftmart.Adapter.LanguageAdapter;
import com.example.swiftmart.Adapter.LanguageModel;
import com.example.swiftmart.MainActivity;
import com.example.swiftmart.R;

import java.util.ArrayList;

public class Language_Activity extends AppCompatActivity {

    RelativeLayout done;
    RecyclerView recycle;
    LanguageAdapter languageAdapter;

    RecyclerView.LayoutManager layoutManager;
    ArrayList<LanguageModel> arrlanguage=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);

        setStatusBarColor(R.color.home);

        done = findViewById(R.id.right);
        recycle = findViewById(R.id.recycle);
        layoutManager = new GridLayoutManager(this, 2);

        recycle.setLayoutManager(new GridLayoutManager(Language_Activity.this, 2));

        arrlanguage.add(new LanguageModel(R.drawable.img, "Afrikaans"));
        arrlanguage.add(new LanguageModel(R.drawable.img_15, "Arabic"));
        arrlanguage.add(new LanguageModel(R.drawable.img_2, "English"));
        arrlanguage.add(new LanguageModel(R.drawable.img_3, "French"));
        arrlanguage.add(new LanguageModel(R.drawable.img_4, "Hindi"));
        arrlanguage.add(new LanguageModel(R.drawable.img_5, "German"));
        arrlanguage.add(new LanguageModel(R.drawable.img_6, "Chinese"));
        arrlanguage.add(new LanguageModel(R.drawable.img_7, "Russians"));
        arrlanguage.add(new LanguageModel(R.drawable.img_8, "Spanish"));
        arrlanguage.add(new LanguageModel(R.drawable.img_9, "Japanese"));
        arrlanguage.add(new LanguageModel(R.drawable.img_10, "Indonesian"));
        arrlanguage.add(new LanguageModel(R.drawable.img_11, "Italian"));
        arrlanguage.add(new LanguageModel(R.drawable.img_12, "Portuguese"));
        arrlanguage.add(new LanguageModel(R.drawable.img_13, "Vietnamese"));

        languageAdapter = new LanguageAdapter(Language_Activity.this, arrlanguage);
        recycle.setAdapter(languageAdapter);

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Language_Activity.this, MainActivity.class);
                startActivity(i);
                LanguageModel selectedLanguage = languageAdapter.getSelectedLanguage();
                if (selectedLanguage != null){
                    String selectedLanguageName = selectedLanguage.getName();
                    Intent intent = new Intent(Language_Activity.this, MainActivity.class);
                    intent.putExtra("selectedLanguage", selectedLanguageName);
                    startActivity(intent);
                    finish();
                }
            }
        });

    }

    private void setStatusBarColor(int colorResource) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(getResources().getColor(colorResource));
        }
    }
}