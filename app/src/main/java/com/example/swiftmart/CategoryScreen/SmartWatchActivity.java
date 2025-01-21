package com.example.swiftmart.CategoryScreen;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.swiftmart.Adapter.MobileSliderAdapter;
import com.example.swiftmart.Adapter.CategoryProductAdapter;
import com.example.swiftmart.Model.ProductModel;
import com.example.swiftmart.R;
import com.example.swiftmart.Utils.CustomToast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SmartWatchActivity extends AppCompatActivity {
    private ImageView backBtn;
    private TextView toolBarTitle;
    
    private LinearLayout applelogo, samsunglogo, noiselogo, fireboltlogo;
    private NestedScrollView smartwatchScrollView;
    private HorizontalScrollView smartwatchHorizontalScrollView;

    private MobileSliderAdapter smartwatchSliderAdapter;
    private ViewPager2 smartWatchViewPager;
    private Handler sliderHandler = new Handler();

    private FirebaseFirestore db;
    private DatabaseReference databaseReference;
    private List<String> imageUrls;

    private ArrayList<ProductModel> datalist = new ArrayList<>();
    private CategoryProductAdapter adapter;
    private RecyclerView smartwatchRecyclerView;
    private ProgressBar smartwatchActivityProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_watch);

        initialization();
        getLaptops();
        getSmartWatchCompany();
        getImageUrls();
        setStatusBarColor(R.color.home);

        toolBarTitle.setText("Smart Watch");
        backBtn.setOnClickListener(v -> onBackPressed());
        
    }

    private void initialization(){

        db = FirebaseFirestore.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("root");
        imageUrls = new ArrayList<>();

        smartwatchScrollView = findViewById(R.id.smartwatchScrollView);
        smartwatchHorizontalScrollView = findViewById(R.id.smartwatchHorizontalScrollView);

        smartWatchViewPager = findViewById(R.id.smartwatchViewPager);

        smartwatchRecyclerView = findViewById(R.id.smartwatchRecyclerView);
        smartwatchActivityProgressBar = findViewById(R.id.smartwatchActivityProgressBar);

        backBtn = findViewById(R.id.backBtn);
        toolBarTitle = findViewById(R.id.toolBarTitle);

        applelogo = findViewById(R.id.applelogo);
        samsunglogo = findViewById(R.id.samsunglogo);
        noiselogo = findViewById(R.id.noiselogo);
        fireboltlogo = findViewById(R.id.fireboltlogo);

        smartwatchScrollView.setVerticalScrollBarEnabled(false);
        smartwatchHorizontalScrollView.setHorizontalScrollBarEnabled(false);
    }

    // Get all the laptops
    private void getLaptops(){
        smartwatchRecyclerView.setLayoutManager(new LinearLayoutManager(SmartWatchActivity.this));
        smartwatchActivityProgressBar.setVisibility(View.VISIBLE);
        datalist.clear();

        db.collection("Products")
                .whereEqualTo("category", "SmartWatch")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null){
                            CustomToast.showToast(SmartWatchActivity.this,  "Error in data fetching");
                            smartwatchActivityProgressBar.setVisibility(View.GONE);
                            return;
                        }


                        if (value != null && !value.isEmpty()){
                            smartwatchActivityProgressBar.setVisibility(View.GONE);
                            for (QueryDocumentSnapshot documentSnapshot : value){
                                ProductModel productModel = documentSnapshot.toObject(ProductModel.class);
                                datalist.add(productModel);

                                GridLayoutManager layoutManager = new GridLayoutManager(SmartWatchActivity.this, 2);
                                smartwatchRecyclerView.setLayoutManager(layoutManager);
                                adapter = new CategoryProductAdapter(SmartWatchActivity.this, datalist);
                                smartwatchRecyclerView.setHasFixedSize(true);
                                smartwatchRecyclerView.setAdapter(adapter);
                                smartwatchRecyclerView.setItemAnimator(new DefaultItemAnimator());
                            }
                        }
                    }
                });


    }

    // Get single mobile company data
    private void getCompany(String company){
        smartwatchRecyclerView.setLayoutManager(new LinearLayoutManager(SmartWatchActivity.this));
        smartwatchActivityProgressBar.setVisibility(View.VISIBLE);
        datalist.clear();

        db.collection("Products")
                .whereEqualTo("category", "SmartWatch")
                .whereEqualTo("company", company)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null){
                            CustomToast.showToast(SmartWatchActivity.this, "Error in data fetching");
                            smartwatchActivityProgressBar.setVisibility(View.GONE);
                            return;
                        }

                        if (value != null && !value.isEmpty()){
                            smartwatchActivityProgressBar.setVisibility(View.GONE);
                            for (QueryDocumentSnapshot documentSnapshot : value){
                                ProductModel productModel = documentSnapshot.toObject(ProductModel.class);
                                datalist.add(productModel);

                                GridLayoutManager layoutManager = new GridLayoutManager(SmartWatchActivity.this, 2);
                                smartwatchRecyclerView.setLayoutManager(layoutManager);
                                adapter = new CategoryProductAdapter(SmartWatchActivity.this, datalist);
                                smartwatchRecyclerView.setHasFixedSize(true);
                                smartwatchRecyclerView.setAdapter(adapter);
                                smartwatchRecyclerView.setItemAnimator(new DefaultItemAnimator());
                            }
                        }
                    }
                });
    }

    // Get company wise mobile data
    private void getSmartWatchCompany(){
        applelogo.setOnClickListener(v -> getCompany("Apple"));
        samsunglogo.setOnClickListener(v -> getCompany("Samsung"));
        noiselogo.setOnClickListener(v -> getCompany("Noise"));
        fireboltlogo.setOnClickListener(v -> getCompany("Firebolt"));
    }


    private void getImageUrls() {
        databaseReference.child("SmartWatch").child("imgurls").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    imageUrls.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String imageUrl = dataSnapshot.getValue(String.class);
                        if (imageUrl != null) {
                            imageUrls.add(imageUrl);
                        }
                    }

                    smartwatchSliderAdapter = new MobileSliderAdapter(SmartWatchActivity.this, imageUrls);
                    smartWatchViewPager.setAdapter(smartwatchSliderAdapter);

                    sliderHandler.postDelayed(slideRunnable, 3000);

                    // Add listener to reset the auto-slide when the page is changed
                    smartWatchViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                        @Override
                        public void onPageSelected(int position) {
                            sliderHandler.removeCallbacks(slideRunnable);
                            sliderHandler.postDelayed(slideRunnable, 3000);
                        }
                    });

                    // Handle touch events to reset auto-slide interval
                    smartWatchViewPager.setOnTouchListener((v, event) -> {
                        sliderHandler.removeCallbacks(slideRunnable);
                        sliderHandler.postDelayed(slideRunnable, 3000);
                        return false;
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Database error: " + error.getMessage());
            }
        });
    }

    private final Runnable slideRunnable = new Runnable() {
        @Override
        public void run() {
            if (smartWatchViewPager != null && smartwatchSliderAdapter != null) {
                int nextItem = (smartWatchViewPager.getCurrentItem() + 1) % smartwatchSliderAdapter.getItemCount();
                smartWatchViewPager.setCurrentItem(nextItem);
                sliderHandler.postDelayed(this, 3000);
            }
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        sliderHandler.removeCallbacks(slideRunnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sliderHandler.postDelayed(slideRunnable, 3000);
    }

    private void setStatusBarColor(int colorResource) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(getResources().getColor(colorResource));
        }
    }
    
}