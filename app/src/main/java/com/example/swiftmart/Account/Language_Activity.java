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
import androidx.recyclerview.widget.LinearLayoutManager;
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

        recycle.setLayoutManager(new LinearLayoutManager(Language_Activity.this));

        arrlanguage.add(new LanguageModel("English","English"));
        arrlanguage.add(new LanguageModel("हिन्दी", "Hindi"));
        arrlanguage.add(new LanguageModel("বাংলা", "Bengali"));
        arrlanguage.add(new LanguageModel("मराठी", "Marathi"));
        arrlanguage.add(new LanguageModel("తెలుగు", "Telugu"));
        arrlanguage.add(new LanguageModel("தமிழ்","Tamil"));
        arrlanguage.add(new LanguageModel("ગુજરાતી","Gujarati"));
        arrlanguage.add(new LanguageModel("اُردُو","Urdu"));
        arrlanguage.add(new LanguageModel("ಕನ್ನಡ","Kannada"));
        arrlanguage.add(new LanguageModel("മലയാളം","Malayalam"));

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