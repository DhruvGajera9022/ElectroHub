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

public class MobilesActivity extends AppCompatActivity {

    LinearLayout iphone,vivo,oppo,mi,realme,samsung,motorola,poco,goggle,oneplues, iqoo, nothing;
    private ViewPager2 mobileViewPager;
    private MobileSliderAdapter mobilesliderAdapter;
    private Handler sliderHandler = new Handler();
    
    private ImageView backBtn;
    private TextView toolBarTitle;
    
    ArrayList<ProductModel> datalist = new ArrayList<>();
    private RecyclerView mobileRecyclerView;
    private FirebaseFirestore db;
    private CategoryProductAdapter adapter;
    private NestedScrollView mobileScrollView;
    private HorizontalScrollView mobileHorizontalScrollView;
    private ProgressBar mobileActivityProgressBar;

    private DatabaseReference databaseReference;
    private List<String> imageUrls;

    private LinearLayout sliderIndicator;
    private int dotCount;
    private ImageView[] dots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobiles);

        db = FirebaseFirestore.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("root");
        imageUrls = new ArrayList<>();

        mobileScrollView=findViewById(R.id.mobileScrollView);
        mobileHorizontalScrollView=findViewById(R.id.mobileHorizontalScrollView);

        iphone=findViewById(R.id.iphone);
        vivo=findViewById(R.id.vivo);
        oppo=findViewById(R.id.oppo);
        mi=findViewById(R.id.mi);
        realme=findViewById(R.id.realme);
        samsung=findViewById(R.id.samsung);
        motorola=findViewById(R.id.motorola);
        poco=findViewById(R.id.poco);
        goggle=findViewById(R.id.goggle);
        oneplues=findViewById(R.id.oneplues);
        iqoo=findViewById(R.id.iqoo);
        nothing=findViewById(R.id.nothing);

        backBtn=findViewById(R.id.backBtn);
        toolBarTitle=findViewById(R.id.toolBarTitle);

        mobileRecyclerView=findViewById(R.id.mobileRecyclerView);
        mobileActivityProgressBar=findViewById(R.id.mobileActivityProgressBar);

        mobileScrollView.setVerticalScrollBarEnabled(false);
        mobileHorizontalScrollView.setHorizontalScrollBarEnabled(false);

        getMobiles();
        getMobileCompany();
        setStatusBarColor(R.color.home);

        toolBarTitle.setText("Mobiles");
        backBtn.setOnClickListener(v -> onBackPressed());

        getImageUrls();
        sliderIndicator = findViewById(R.id.sliderIndicator);
        mobileViewPager = findViewById(R.id.mobileViewPager);

    }

    // handle on back press
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    // Get all the mobiles
    private void getMobiles(){
        mobileRecyclerView.setLayoutManager(new LinearLayoutManager(MobilesActivity.this));
        mobileActivityProgressBar.setVisibility(View.VISIBLE);
        datalist.clear();

        db.collection("Products")
                .whereEqualTo("category", "Mobile")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null){
                            CustomToast.showToast(MobilesActivity.this,  "Error in data fetching");
                            mobileActivityProgressBar.setVisibility(View.GONE);
                            return;
                        }


                        if (value != null && !value.isEmpty()){
                            mobileActivityProgressBar.setVisibility(View.GONE);
                            for (QueryDocumentSnapshot documentSnapshot : value){
                                ProductModel productModel = documentSnapshot.toObject(ProductModel.class);
                                datalist.add(productModel);

                                GridLayoutManager layoutManager = new GridLayoutManager(MobilesActivity.this, 2);
                                mobileRecyclerView.setLayoutManager(layoutManager);
                                adapter = new CategoryProductAdapter(MobilesActivity.this, datalist);
                                mobileRecyclerView.setHasFixedSize(true);
                                mobileRecyclerView.setAdapter(adapter);
                                mobileRecyclerView.setItemAnimator(new DefaultItemAnimator());
                            }
                        }
                    }
                });


    }

    // Get single mobile company data
    private void getCompany(String company){
        mobileRecyclerView.setLayoutManager(new LinearLayoutManager(MobilesActivity.this));
        mobileActivityProgressBar.setVisibility(View.VISIBLE);
        datalist.clear();

        db.collection("Products")
                .whereEqualTo("category", "Mobile")
                .whereEqualTo("company", company)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null){
                            CustomToast.showToast(MobilesActivity.this, "Error in data fetching");
                            mobileActivityProgressBar.setVisibility(View.GONE);
                            return;
                        }

                        if (value != null && !value.isEmpty()){
                            mobileActivityProgressBar.setVisibility(View.GONE);
                            for (QueryDocumentSnapshot documentSnapshot : value){
                                ProductModel productModel = documentSnapshot.toObject(ProductModel.class);
                                datalist.add(productModel);

                                GridLayoutManager layoutManager = new GridLayoutManager(MobilesActivity.this, 2);
                                mobileRecyclerView.setLayoutManager(layoutManager);
                                adapter = new CategoryProductAdapter(MobilesActivity.this, datalist);
                                mobileRecyclerView.setHasFixedSize(true);
                                mobileRecyclerView.setAdapter(adapter);
                                mobileRecyclerView.setItemAnimator(new DefaultItemAnimator());
                            }
                        }
                    }
                });
    }

    // Get company wise mobile data
    private void getMobileCompany(){
        iphone.setOnClickListener(v -> getCompany("Apple"));
        vivo.setOnClickListener(v -> getCompany("Vivo"));
        oppo.setOnClickListener(v -> getCompany("Oppo"));
        mi.setOnClickListener(v -> getCompany("Xiaomi"));
        realme.setOnClickListener(v -> getCompany("Realme"));
        samsung.setOnClickListener(v -> getCompany("Samsung"));
        motorola.setOnClickListener(v -> getCompany("Motorola"));
        poco.setOnClickListener(v -> getCompany("Poco"));
        goggle.setOnClickListener(v -> getCompany("Google"));
        oneplues.setOnClickListener(v -> getCompany("OnePlus"));
        iqoo.setOnClickListener(v -> getCompany("Nothing"));
        nothing.setOnClickListener(v -> getCompany("Iqoo"));
    }

    private void getImageUrls() {
        databaseReference.child("Mobile").child("imgurls").addValueEventListener(new ValueEventListener() {
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

                    mobilesliderAdapter = new MobileSliderAdapter(MobilesActivity.this, imageUrls);
                    mobileViewPager.setAdapter(mobilesliderAdapter);

                    // Setup the slider indicator
                    setupSliderIndicator();

                    sliderHandler.postDelayed(slideRunnable, 3000);

                    // Add listener to reset the auto-slide when the page is changed
                    mobileViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                        @Override
                        public void onPageSelected(int position) {
                            updateIndicator(position);
                            sliderHandler.removeCallbacks(slideRunnable);
                            sliderHandler.postDelayed(slideRunnable, 3000);
                        }
                    });

                    // Handle touch events to reset auto-slide interval
                    mobileViewPager.setOnTouchListener((v, event) -> {
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
        dotCount = mobilesliderAdapter.getItemCount();
        dots = new ImageView[dotCount];

        // Clear any existing dots
        sliderIndicator.removeAllViews();

        // Create and add dots
        for (int i = 0; i < dotCount; i++) {
            dots[i] = new ImageView(MobilesActivity.this);
            dots[i].setImageDrawable(ContextCompat.getDrawable(MobilesActivity.this, R.drawable.indicator_non_active));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(8, 0, 8, 0);
            sliderIndicator.addView(dots[i], params);
        }

        // Activate the first dot
        if (dotCount > 0) {
            dots[0].setImageDrawable(ContextCompat.getDrawable(MobilesActivity.this, R.drawable.indicator_active));
        }
    }

    private void updateIndicator(int position) {
        // Reset all dots to inactive
        for (int i = 0; i < dotCount; i++) {
            dots[i].setImageDrawable(ContextCompat.getDrawable(MobilesActivity.this, R.drawable.indicator_non_active));
        }

        // Set the current dot to active
        if (position >= 0 && position < dotCount) {
            dots[position].setImageDrawable(ContextCompat.getDrawable(MobilesActivity.this, R.drawable.indicator_active));
        }
    }

    private final Runnable slideRunnable = new Runnable() {
        @Override
        public void run() {
            if (mobileViewPager != null && mobilesliderAdapter != null) {
                int nextItem = (mobileViewPager.getCurrentItem() + 1) % mobilesliderAdapter.getItemCount();
                mobileViewPager.setCurrentItem(nextItem);
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