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

import com.example.swiftmart.Adapter.CategoryProductAdapter;
import com.example.swiftmart.Adapter.MobileSliderAdapter;
import com.example.swiftmart.Adapter.ProductAdapter;
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

public class CameraActivity extends AppCompatActivity {
    private ImageView backBtn;
    private TextView toolBarTitle;

    private LinearLayout cameraNikon, cameraCanon, cameraSony, cameraFujifilm, cameraPanasonic;
    private NestedScrollView cameraScrollView;
    private HorizontalScrollView cameraHorizontalScrollView;

    private MobileSliderAdapter cameraSliderAdapter;
    private ViewPager2 cameraViewPager;
    private Handler sliderHandler = new Handler();

    private FirebaseFirestore db;
    private DatabaseReference databaseReference;
    private List<String> imageUrls;

    private ArrayList<ProductModel> datalist = new ArrayList<>();
    private CategoryProductAdapter adapter;
    private RecyclerView cameraRecyclerView;
    private ProgressBar cameraActivityProgressBar;

    private LinearLayout sliderIndicator;
    private int dotCount;
    private ImageView[] dots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        initialization();
        getCameras();
        getCamerasCompany();
        getImageUrls();

        setStatusBarColor(R.color.home);

        toolBarTitle.setText("Camera");
        backBtn.setOnClickListener(v -> onBackPressed());
        
    }

    private void initialization(){

        db = FirebaseFirestore.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("root");
        imageUrls = new ArrayList<>();

        cameraScrollView = findViewById(R.id.cameraScrollView);
        cameraHorizontalScrollView = findViewById(R.id.cameraHorizontalScrollView);

        sliderIndicator = findViewById(R.id.sliderIndicator);
        cameraViewPager = findViewById(R.id.cameraViewPager);

        cameraRecyclerView = findViewById(R.id.cameraRecyclerView);
        cameraActivityProgressBar = findViewById(R.id.cameraActivityProgressBar);

        backBtn = findViewById(R.id.backBtn);
        toolBarTitle = findViewById(R.id.toolBarTitle);

        cameraNikon = findViewById(R.id.cameraNikon);
        cameraCanon = findViewById(R.id.cameraCanon);
        cameraSony = findViewById(R.id.cameraSony);
        cameraFujifilm = findViewById(R.id.cameraFujifilm);
        cameraPanasonic = findViewById(R.id.cameraPanasonic);

        cameraScrollView.setVerticalScrollBarEnabled(false);
        cameraHorizontalScrollView.setHorizontalScrollBarEnabled(false);
    }

    // Get all the laptops
    private void getCameras(){
        cameraRecyclerView.setLayoutManager(new LinearLayoutManager(CameraActivity.this));
        cameraActivityProgressBar.setVisibility(View.VISIBLE);
        datalist.clear();

        db.collection("Products")
                .whereEqualTo("category", "Camera")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null){
                            CustomToast.showToast(CameraActivity.this,  "Error in data fetching");
                            cameraActivityProgressBar.setVisibility(View.GONE);
                            return;
                        }


                        if (value != null && !value.isEmpty()){
                            cameraActivityProgressBar.setVisibility(View.GONE);
                            for (QueryDocumentSnapshot documentSnapshot : value){
                                ProductModel productModel = documentSnapshot.toObject(ProductModel.class);
                                datalist.add(productModel);

                                GridLayoutManager layoutManager = new GridLayoutManager(CameraActivity.this, 2);
                                cameraRecyclerView.setLayoutManager(layoutManager);
                                adapter = new CategoryProductAdapter(CameraActivity.this, datalist);
                                cameraRecyclerView.setHasFixedSize(true);
                                cameraRecyclerView.setAdapter(adapter);
                                cameraRecyclerView.setItemAnimator(new DefaultItemAnimator());
                            }
                        }
                    }
                });


    }

    // Get single mobile company data
    private void getCompany(String company){
        cameraRecyclerView.setLayoutManager(new LinearLayoutManager(CameraActivity.this));
        cameraActivityProgressBar.setVisibility(View.VISIBLE);
        datalist.clear();

        db.collection("Products")
                .whereEqualTo("category", "Camera")
                .whereEqualTo("company", company)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null){
                            CustomToast.showToast(CameraActivity.this, "Error in data fetching");
                            cameraActivityProgressBar.setVisibility(View.GONE);
                            return;
                        }

                        if (value != null && !value.isEmpty()){
                            cameraActivityProgressBar.setVisibility(View.GONE);
                            for (QueryDocumentSnapshot documentSnapshot : value){
                                ProductModel productModel = documentSnapshot.toObject(ProductModel.class);
                                datalist.add(productModel);

                                GridLayoutManager layoutManager = new GridLayoutManager(CameraActivity.this, 2);
                                cameraRecyclerView.setLayoutManager(layoutManager);
                                adapter = new CategoryProductAdapter(CameraActivity.this, datalist);
                                cameraRecyclerView.setHasFixedSize(true);
                                cameraRecyclerView.setAdapter(adapter);
                                cameraRecyclerView.setItemAnimator(new DefaultItemAnimator());
                            }
                        }
                    }
                });
    }

    // Get company wise mobile data
    private void getCamerasCompany(){
        cameraNikon.setOnClickListener(v -> getCompany("Nikon"));
        cameraCanon.setOnClickListener(v -> getCompany("Canon"));
        cameraSony.setOnClickListener(v -> getCompany("Sony"));
        cameraFujifilm.setOnClickListener(v -> getCompany("Fujifilm"));
        cameraPanasonic.setOnClickListener(v -> getCompany("Panasonic"));
    }


    private void getImageUrls() {
        databaseReference.child("Camera").child("imgurls").addValueEventListener(new ValueEventListener() {
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

                    cameraSliderAdapter = new MobileSliderAdapter(CameraActivity.this, imageUrls);
                    cameraViewPager.setAdapter(cameraSliderAdapter);

                    // Setup the slider indicator
                    setupSliderIndicator();

                    sliderHandler.postDelayed(slideRunnable, 3000);

                    // Handle page change
                    cameraViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                        @Override
                        public void onPageSelected(int position) {
                            updateIndicator(position);
                            sliderHandler.removeCallbacks(slideRunnable);
                            sliderHandler.postDelayed(slideRunnable, 3000);
                        }
                    });

                    // Reset auto-slide interval on touch
                    cameraViewPager.setOnTouchListener((v, event) -> {
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
        dotCount = cameraSliderAdapter.getItemCount();
        dots = new ImageView[dotCount];

        // Clear any existing dots
        sliderIndicator.removeAllViews();

        // Create and add dots
        for (int i = 0; i < dotCount; i++) {
            dots[i] = new ImageView(CameraActivity.this);
            dots[i].setImageDrawable(ContextCompat.getDrawable(CameraActivity.this, R.drawable.indicator_non_active));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(8, 0, 8, 0);
            sliderIndicator.addView(dots[i], params);
        }

        // Activate the first dot
        if (dotCount > 0) {
            dots[0].setImageDrawable(ContextCompat.getDrawable(CameraActivity.this, R.drawable.indicator_active));
        }
    }

    private void updateIndicator(int position) {
        // Reset all dots to inactive
        for (int i = 0; i < dotCount; i++) {
            dots[i].setImageDrawable(ContextCompat.getDrawable(CameraActivity.this, R.drawable.indicator_non_active));
        }

        // Set the current dot to active
        if (position >= 0 && position < dotCount) {
            dots[position].setImageDrawable(ContextCompat.getDrawable(CameraActivity.this, R.drawable.indicator_active));
        }
    }

    private final Runnable slideRunnable = new Runnable() {
        @Override
        public void run() {
            if (cameraViewPager != null && cameraSliderAdapter != null) {
                int nextItem = (cameraViewPager.getCurrentItem() + 1) % cameraSliderAdapter.getItemCount();
                cameraViewPager.setCurrentItem(nextItem);
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