package com.example.swiftmart.CategoryScreen;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
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

public class tv_brandActivity extends AppCompatActivity {

    private ImageView backBtn;;
    private TextView toolBarTitle;

    private LinearLayout samsunglogo, lglogo, milogo, tcllogo;
    
    private ViewPager2 tvViewPager;
    private MobileSliderAdapter tvSliderAdapter;
    private Handler sliderHandler = new Handler();
    private RecyclerView tvRecyclerView;
    private ArrayList<ProductModel> datalist = new ArrayList<>();
    private FirebaseFirestore db;
    private CategoryProductAdapter adapter;
    private NestedScrollView tvActivityScrollView;
    private ProgressBar tvActivityProgressBar;

    private DatabaseReference databaseReference;
    private List<String> imageUrls;

    private LinearLayout sliderIndicator;
    private int dotCount;
    private ImageView[] dots;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tv_brand);

        db = FirebaseFirestore.getInstance();

        tvActivityScrollView = findViewById(R.id.tvActivityScrollView);
        databaseReference = FirebaseDatabase.getInstance().getReference("root");
        imageUrls = new ArrayList<>();

        // Initialize views
        samsunglogo = findViewById(R.id.samsunglogo);
        lglogo = findViewById(R.id.lglogo);
        milogo = findViewById(R.id.milogo);
        tcllogo = findViewById(R.id.tcllogo);

        tvRecyclerView=findViewById(R.id.tvRecyclerView);
        tvActivityProgressBar=findViewById(R.id.tvActivityProgressBar);

        backBtn = findViewById(R.id.backBtn);
        toolBarTitle = findViewById(R.id.toolBarTitle);

        tvActivityScrollView.setVerticalScrollBarEnabled(false);

        getTVs();
        getTVsCompany();
        setStatusBarColor(R.color.home);

        getImageUrls();
        sliderIndicator = findViewById(R.id.sliderIndicator);
        tvViewPager = findViewById(R.id.tvViewPager);

        toolBarTitle.setText("TV");
        backBtn.setOnClickListener(v -> onBackPressed());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    // Get all the TVs
    private void getTVs(){
        tvRecyclerView.setLayoutManager(new LinearLayoutManager(tv_brandActivity.this));
        tvActivityProgressBar.setVisibility(View.VISIBLE);
        datalist.clear();

        db.collection("Products")
                .whereEqualTo("category", "TV")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null){
                            CustomToast.showToast(tv_brandActivity.this,  "Error in data fetching");
                            tvActivityProgressBar.setVisibility(View.GONE);
                            return;
                        }


                        if (value != null && !value.isEmpty()){
                            tvActivityProgressBar.setVisibility(View.GONE);
                            for (QueryDocumentSnapshot documentSnapshot : value){
                                ProductModel productModel = documentSnapshot.toObject(ProductModel.class);
                                datalist.add(productModel);

                                GridLayoutManager layoutManager = new GridLayoutManager(tv_brandActivity.this, 2);
                                tvRecyclerView.setLayoutManager(layoutManager);
                                adapter = new CategoryProductAdapter(tv_brandActivity.this, datalist);
                                tvRecyclerView.setHasFixedSize(true);
                                tvRecyclerView.setAdapter(adapter);
                                tvRecyclerView.setItemAnimator(new DefaultItemAnimator());
                            }
                        }
                    }
                });


    }

    // Get single TVs company data
    private void getCompany(String company){
        tvRecyclerView.setLayoutManager(new LinearLayoutManager(tv_brandActivity.this));
        tvActivityProgressBar.setVisibility(View.VISIBLE);
        datalist.clear();

        db.collection("Products")
                .whereEqualTo("category", "TV")
                .whereEqualTo("company", company)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null){
                            CustomToast.showToast(tv_brandActivity.this, "Error in data fetching");
                            tvActivityProgressBar.setVisibility(View.GONE);
                            return;
                        }


                        if (value != null && !value.isEmpty()){
                            tvActivityProgressBar.setVisibility(View.GONE);
                            for (QueryDocumentSnapshot documentSnapshot : value){
                                ProductModel productModel = documentSnapshot.toObject(ProductModel.class);
                                datalist.add(productModel);

                                GridLayoutManager layoutManager = new GridLayoutManager(tv_brandActivity.this, 2);
                                tvRecyclerView.setLayoutManager(layoutManager);
                                adapter = new CategoryProductAdapter(tv_brandActivity.this, datalist);
                                tvRecyclerView.setHasFixedSize(true);
                                tvRecyclerView.setAdapter(adapter);
                                tvRecyclerView.setItemAnimator(new DefaultItemAnimator());
                            }
                        }
                    }
                });


    }

    // Get company wise TV data
    private void getTVsCompany(){
        samsunglogo.setOnClickListener(v -> getCompany("Samsung"));
        lglogo.setOnClickListener(v -> getCompany("LG"));
        milogo.setOnClickListener(v -> getCompany("Xiaomi"));
        tcllogo.setOnClickListener(v -> getCompany("TCL"));
    }

    private void getImageUrls() {
        databaseReference.child("TV").child("imgurls").addValueEventListener(new ValueEventListener() {
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

                    tvSliderAdapter = new MobileSliderAdapter(tv_brandActivity.this, imageUrls);
                    tvViewPager.setAdapter(tvSliderAdapter);

                    // Setup the slider indicator
                    setupSliderIndicator();

                    sliderHandler.postDelayed(slideRunnable, 3000);

                    // Add listener to reset the auto-slide when the page is changed
                    tvViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                        @Override
                        public void onPageSelected(int position) {
                            updateIndicator(position);
                            sliderHandler.removeCallbacks(slideRunnable);
                            sliderHandler.postDelayed(slideRunnable, 3000);
                        }
                    });

                    // Handle touch events to reset auto-slide interval
                    tvViewPager.setOnTouchListener((v, event) -> {
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
        dotCount = tvSliderAdapter.getItemCount();
        dots = new ImageView[dotCount];

        // Clear any existing dots
        sliderIndicator.removeAllViews();

        // Create and add dots
        for (int i = 0; i < dotCount; i++) {
            dots[i] = new ImageView(tv_brandActivity.this);
            dots[i].setImageDrawable(ContextCompat.getDrawable(tv_brandActivity.this, R.drawable.indicator_non_active));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(8, 0, 8, 0);
            sliderIndicator.addView(dots[i], params);
        }

        // Activate the first dot
        if (dotCount > 0) {
            dots[0].setImageDrawable(ContextCompat.getDrawable(tv_brandActivity.this, R.drawable.indicator_active));
        }
    }

    private void updateIndicator(int position) {
        // Reset all dots to inactive
        for (int i = 0; i < dotCount; i++) {
            dots[i].setImageDrawable(ContextCompat.getDrawable(tv_brandActivity.this, R.drawable.indicator_non_active));
        }

        // Set the current dot to active
        if (position >= 0 && position < dotCount) {
            dots[position].setImageDrawable(ContextCompat.getDrawable(tv_brandActivity.this, R.drawable.indicator_active));
        }
    }

    private final Runnable slideRunnable = new Runnable() {
        @Override
        public void run() {
            if (tvViewPager != null && tvSliderAdapter != null) {
                int nextItem = (tvViewPager.getCurrentItem() + 1) % tvSliderAdapter.getItemCount();
                tvViewPager.setCurrentItem(nextItem);
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
