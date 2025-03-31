package com.example.swiftmart;

import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.swiftmart.Frgments.AccountFragment;
import com.example.swiftmart.Frgments.CartFragment;
import com.example.swiftmart.Frgments.CategoryFragment;
import com.example.swiftmart.Frgments.ExploreFragment;
import com.example.swiftmart.Frgments.HomeFragment;
import com.example.swiftmart.Utils.NetworkChangeReceiver;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements NetworkChangeReceiver.NetworkChangeListener {

    String selectedLanguage;
    AlertDialog dialog;
    NetworkChangeReceiver networkChangeReceiver = new NetworkChangeReceiver(this);

    private ChipNavigationBar mainBottomNavigation;

    private FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLanguage();
        setContentView(R.layout.activity_main);

        selectedLanguage = getIntent().getStringExtra("selectedLanguage");

        frameLayout = findViewById(R.id.frameLayout);
        mainBottomNavigation = findViewById(R.id.mainBottomNavigation);

        if (selectedLanguage != null) {
            changeLanguage(selectedLanguage);
        }

        if (savedInstanceState == null) {
            replaceFragment(new HomeFragment());
            mainBottomNavigation.setItemSelected(R.id.nav_home, true);
        }

        bottomTabs();
        setStatusBarColor(R.color.home);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {

            }
        });

        AdView mAdView;
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

    }

    private void bottomTabs(){
        // Set up listener for bottom navigation bar
        mainBottomNavigation.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            if (item == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (item == R.id.nav_explore) {
                selectedFragment = new ExploreFragment();
            } else if (item == R.id.nav_category) {
                selectedFragment = new CategoryFragment();
            } else if (item == R.id.nav_person) {
                selectedFragment = new AccountFragment();
            } else if (item == R.id.nav_cart) {
                selectedFragment = new CartFragment();
            }

            if (selectedFragment != null) {
                replaceFragment(selectedFragment);
            }
        });
    }

    private void changeLanguage(String newLanguage) {
        SharedPreferences sharedPreferences = getSharedPreferences("LANGUAGE_SETTINGS", MODE_PRIVATE);
        int item = sharedPreferences.getInt("item", 0);

        if (selectedLanguage != null) {
            switch (selectedLanguage) {
                case "English":
                    selectLanguage("en", item);
                    break;
                case "हिन्दी":
                    selectLanguage("hi", item);
                    break;
                case "বাংলা":
                    selectLanguage("bn", item);
                    break;
                case "मराठी":
                    selectLanguage("mr", item);
                    break;
                case "తెలుగు":
                    selectLanguage("te", item);
                    break;
                case "தமிழ்":
                    selectLanguage("ta", item);
                    break;
                case "ગુજરાતી":
                    selectLanguage("gu", item);
                    break;
                case "اُردُو":
                    selectLanguage("ur", item);
                    break;
                case "ಕನ್ನಡ":
                    selectLanguage("kn", item);
                    break;
                case "മലയാളം":
                    selectLanguage("ml", item);
                    break;
                default:
                    Toast.makeText(this, "Unsupported language", Toast.LENGTH_SHORT).show();
                    return;
            }
        }
    }

    private void selectLanguage(String language, int item) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.setLocale(locale);
        getBaseContext().getResources().updateConfiguration(configuration, getBaseContext().getResources().getDisplayMetrics());

        SharedPreferences.Editor editor = getSharedPreferences("LANGUAGE_SETTINGS", MODE_PRIVATE).edit();
        editor.putString("language", language);
        editor.putInt("item", item);
        editor.apply();
    }

    private void loadLanguage() {
        SharedPreferences sharedPreferences = getSharedPreferences("LANGUAGE_SETTINGS", MODE_PRIVATE);
        String language = sharedPreferences.getString("language", "en");
        selectLanguage(language, sharedPreferences.getInt("item", 0));
    }

    private void ShowDialog() {

        dialog = new AlertDialog.Builder(MainActivity.this)
                .setView(R.layout.no_internet_dialog)
                .setCancelable(false)
                .create();
        dialog.show();


        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


//        TextView playButton = dialog.findViewById(R.id.playButton);
//
//        playButton.setOnClickListener(view->{
//
//            Toast.makeText(this, "Button Clicked", Toast.LENGTH_SHORT).show();
//        });


    }

    @Override
    public void onNetworkChanged(boolean isConnected) {
        if (isConnected) {

            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
                dialog = null;
            }

        } else {

            if (dialog == null || !dialog.isShowing()) {

                ShowDialog();
            }

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(networkChangeReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(networkChangeReceiver);
    }

    private void setStatusBarColor(int colorResource) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(getResources().getColor(colorResource));
        }
    }

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        R.anim.right_to_left_enter,
                        R.anim.right_to_left_exit,
                        R.anim.left_to_right_enter,
                        R.anim.left_to_right_exit
                )
                .replace(R.id.frameLayout, fragment)
                .commit();
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        Fragment currentFragment = fm.findFragmentById(R.id.frameLayout);

        if (currentFragment instanceof HomeFragment) {
            super.onBackPressed();
        } else {
            replaceFragment(new HomeFragment());
            mainBottomNavigation.setItemSelected(R.id.nav_home, true);
        }
    }


}
