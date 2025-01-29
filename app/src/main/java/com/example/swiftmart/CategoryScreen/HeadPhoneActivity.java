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
import androidx.core.content.ContextCompat;
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

public class HeadPhoneActivity extends AppCompatActivity {
    private ImageView backBtn;
    private TextView toolBarTitle;
    
    private LinearLayout boatlogo, jbllogo, sonylogo, cosmicbytelogo, sennheiserlogo, zebronicslogo;
    private NestedScrollView headphoneScrollView;
    private HorizontalScrollView headphoneHorizontalScrollView;

    private MobileSliderAdapter headphoneSliderAdapter;
    private ViewPager2 headphoneViewPager;
    private Handler sliderHandler = new Handler();

    private FirebaseFirestore db;
    private DatabaseReference databaseReference;
    private List<String> imageUrls;

    private ArrayList<ProductModel> datalist = new ArrayList<>();
    private CategoryProductAdapter adapter;
    private RecyclerView headphoneRecyclerView;
    private ProgressBar headphoneActivityProgressBar;

    private LinearLayout sliderIndicator;
    private int dotCount;
    private ImageView[] dots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_head_phone);

        initialization();
        getLaptops();
        getLaptopCompany();
        getImageUrls();
        setStatusBarColor(R.color.home);

        toolBarTitle.setText("Headphone");
        backBtn.setOnClickListener(v -> onBackPressed());
    }

    private void initialization(){

        db = FirebaseFirestore.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("root");
        imageUrls = new ArrayList<>();

        headphoneScrollView = findViewById(R.id.headphoneScrollView);
        headphoneHorizontalScrollView = findViewById(R.id.headphoneHorizontalScrollView);

        sliderIndicator = findViewById(R.id.sliderIndicator);
        headphoneViewPager = findViewById(R.id.headphoneViewPager);

        headphoneRecyclerView = findViewById(R.id.headphoneRecyclerView);
        headphoneActivityProgressBar = findViewById(R.id.headphoneActivityProgressBar);

        backBtn = findViewById(R.id.backBtn);
        toolBarTitle = findViewById(R.id.toolBarTitle);

        boatlogo = findViewById(R.id.boatlogo);
        jbllogo = findViewById(R.id.jbllogo);
        sonylogo = findViewById(R.id.sonylogo);
        cosmicbytelogo = findViewById(R.id.cosmicbytelogo);
        sennheiserlogo = findViewById(R.id.sennheiserlogo);
        zebronicslogo = findViewById(R.id.zebronicslogo);

        headphoneScrollView.setVerticalScrollBarEnabled(false);
        headphoneHorizontalScrollView.setHorizontalScrollBarEnabled(false);
    }

    // Get all the laptops
    private void getLaptops(){
        headphoneRecyclerView.setLayoutManager(new LinearLayoutManager(HeadPhoneActivity.this));
        headphoneActivityProgressBar.setVisibility(View.VISIBLE);
        datalist.clear();

        db.collection("Products")
                .whereEqualTo("category", "Headphone")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null){
                            CustomToast.showToast(HeadPhoneActivity.this,  "Error in data fetching");
                            headphoneActivityProgressBar.setVisibility(View.GONE);
                            return;
                        }


                        if (value != null && !value.isEmpty()){
                            headphoneActivityProgressBar.setVisibility(View.GONE);
                            for (QueryDocumentSnapshot documentSnapshot : value){
                                ProductModel productModel = documentSnapshot.toObject(ProductModel.class);
                                datalist.add(productModel);

                                GridLayoutManager layoutManager = new GridLayoutManager(HeadPhoneActivity.this, 2);
                                headphoneRecyclerView.setLayoutManager(layoutManager);
                                adapter = new CategoryProductAdapter(HeadPhoneActivity.this, datalist);
                                headphoneRecyclerView.setHasFixedSize(true);
                                headphoneRecyclerView.setAdapter(adapter);
                                headphoneRecyclerView.setItemAnimator(new DefaultItemAnimator());
                            }
                        }
                    }
                });


    }

    // Get single mobile company data
    private void getCompany(String company){
        headphoneRecyclerView.setLayoutManager(new LinearLayoutManager(HeadPhoneActivity.this));
        headphoneActivityProgressBar.setVisibility(View.VISIBLE);
        datalist.clear();

        db.collection("Products")
                .whereEqualTo("category", "Headphone")
                .whereEqualTo("company", company)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null){
                            CustomToast.showToast(HeadPhoneActivity.this, "Error in data fetching");
                            headphoneActivityProgressBar.setVisibility(View.GONE);
                            return;
                        }

                        if (value != null && !value.isEmpty()){
                            headphoneActivityProgressBar.setVisibility(View.GONE);
                            for (QueryDocumentSnapshot documentSnapshot : value){
                                ProductModel productModel = documentSnapshot.toObject(ProductModel.class);
                                datalist.add(productModel);

                                GridLayoutManager layoutManager = new GridLayoutManager(HeadPhoneActivity.this, 2);
                                headphoneRecyclerView.setLayoutManager(layoutManager);
                                adapter = new CategoryProductAdapter(HeadPhoneActivity.this, datalist);
                                headphoneRecyclerView.setHasFixedSize(true);
                                headphoneRecyclerView.setAdapter(adapter);
                                headphoneRecyclerView.setItemAnimator(new DefaultItemAnimator());
                            }
                        }
                    }
                });
    }

    // Get company wise mobile data
    private void getLaptopCompany(){
        boatlogo.setOnClickListener(v -> getCompany("Boat"));
        jbllogo.setOnClickListener(v -> getCompany("JBL"));
        sonylogo.setOnClickListener(v -> getCompany("Sony"));
        zebronicslogo.setOnClickListener(v -> getCompany("Zebronics"));
        cosmicbytelogo.setOnClickListener(v -> getCompany("Cosmic Byte"));
        sennheiserlogo.setOnClickListener(v -> getCompany("Sennheiser"));
    }


    private void getImageUrls() {
        databaseReference.child("Headphone").child("imgurls").addValueEventListener(new ValueEventListener() {
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

                    headphoneSliderAdapter = new MobileSliderAdapter(HeadPhoneActivity.this, imageUrls);
                    headphoneViewPager.setAdapter(headphoneSliderAdapter);

                    // Setup the slider indicator
                    setupSliderIndicator();

                    sliderHandler.postDelayed(slideRunnable, 3000);

                    // Add listener to reset the auto-slide when the page is changed
                    headphoneViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                        @Override
                        public void onPageSelected(int position) {
                            updateIndicator(position);
                            sliderHandler.removeCallbacks(slideRunnable);
                            sliderHandler.postDelayed(slideRunnable, 3000);
                        }
                    });

                    // Handle touch events to reset auto-slide interval
                    headphoneViewPager.setOnTouchListener((v, event) -> {
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

    private void setupSliderIndicator() {
        dotCount = headphoneSliderAdapter.getItemCount();
        dots = new ImageView[dotCount];

        // Clear any existing dots
        sliderIndicator.removeAllViews();

        // Create and add dots
        for (int i = 0; i < dotCount; i++) {
            dots[i] = new ImageView(HeadPhoneActivity.this);
            dots[i].setImageDrawable(ContextCompat.getDrawable(HeadPhoneActivity.this, R.drawable.indicator_non_active));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(8, 0, 8, 0);
            sliderIndicator.addView(dots[i], params);
        }

        // Activate the first dot
        if (dotCount > 0) {
            dots[0].setImageDrawable(ContextCompat.getDrawable(HeadPhoneActivity.this, R.drawable.indicator_active));
        }
    }

    private void updateIndicator(int position) {
        // Reset all dots to inactive
        for (int i = 0; i < dotCount; i++) {
            dots[i].setImageDrawable(ContextCompat.getDrawable(HeadPhoneActivity.this, R.drawable.indicator_non_active));
        }

        // Set the current dot to active
        if (position >= 0 && position < dotCount) {
            dots[position].setImageDrawable(ContextCompat.getDrawable(HeadPhoneActivity.this, R.drawable.indicator_active));
        }
    }

    private final Runnable slideRunnable = new Runnable() {
        @Override
        public void run() {
            if (headphoneViewPager != null && headphoneSliderAdapter != null) {
                int nextItem = (headphoneViewPager.getCurrentItem() + 1) % headphoneSliderAdapter.getItemCount();
                headphoneViewPager.setCurrentItem(nextItem);
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