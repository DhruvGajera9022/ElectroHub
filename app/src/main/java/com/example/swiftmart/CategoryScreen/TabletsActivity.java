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

public class TabletsActivity extends AppCompatActivity {
    private ImageView backBtn;
    private TextView toolBarTitle;
    
    private LinearLayout tabletRealme, tabletApple, tabletSamsung, tabletOneplus, tabletRedmi, tabletPoco;
    private NestedScrollView tabletScrollView;
    private HorizontalScrollView tabletHorizontalScrollView;

    private MobileSliderAdapter tabletSliderAdapter;
    private ViewPager2 tabletViewPager;
    private Handler sliderHandler = new Handler();

    private FirebaseFirestore db;
    private DatabaseReference databaseReference;
    private List<String> imageUrls;

    private ArrayList<ProductModel> datalist = new ArrayList<>();
    private CategoryProductAdapter adapter;
    private RecyclerView tabletRecyclerView;
    private ProgressBar tabletActivityProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tablets);

        initialization();
        getTablets();
        getTabletsCompany();
        getImageUrls();
        setStatusBarColor(R.color.home);

        toolBarTitle.setText("Tablets");
        backBtn.setOnClickListener(v -> onBackPressed());
        
    }


    private void initialization(){

        db = FirebaseFirestore.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("root");
        imageUrls = new ArrayList<>();

        tabletScrollView = findViewById(R.id.tabletScrollView);
        tabletHorizontalScrollView = findViewById(R.id.tabletHorizontalScrollView);

        tabletViewPager = findViewById(R.id.tabletViewPager);

        tabletRecyclerView = findViewById(R.id.tabletRecyclerView);
        tabletActivityProgressBar = findViewById(R.id.tabletActivityProgressBar);

        backBtn = findViewById(R.id.backBtn);
        toolBarTitle = findViewById(R.id.toolBarTitle);

        tabletRealme = findViewById(R.id.tabletRealme);
        tabletApple = findViewById(R.id.tabletApple);
        tabletSamsung = findViewById(R.id.tabletSamsung);
        tabletOneplus = findViewById(R.id.tabletOneplus);
        tabletRedmi = findViewById(R.id.tabletRedmi);
        tabletPoco = findViewById(R.id.tabletPoco);

        tabletScrollView.setVerticalScrollBarEnabled(false);
        tabletHorizontalScrollView.setHorizontalScrollBarEnabled(false);
    }

    // Get all the laptops
    private void getTablets(){
        tabletRecyclerView.setLayoutManager(new LinearLayoutManager(TabletsActivity.this));
        tabletActivityProgressBar.setVisibility(View.VISIBLE);
        datalist.clear();

        db.collection("Products")
                .whereEqualTo("category", "Tablet")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null){
                            CustomToast.showToast(TabletsActivity.this,  "Error in data fetching");
                            tabletActivityProgressBar.setVisibility(View.GONE);
                            return;
                        }


                        if (value != null && !value.isEmpty()){
                            tabletActivityProgressBar.setVisibility(View.GONE);
                            for (QueryDocumentSnapshot documentSnapshot : value){
                                ProductModel productModel = documentSnapshot.toObject(ProductModel.class);
                                datalist.add(productModel);

                                GridLayoutManager layoutManager = new GridLayoutManager(TabletsActivity.this, 2);
                                tabletRecyclerView.setLayoutManager(layoutManager);
                                adapter = new CategoryProductAdapter(TabletsActivity.this, datalist);
                                tabletRecyclerView.setHasFixedSize(true);
                                tabletRecyclerView.setAdapter(adapter);
                                tabletRecyclerView.setItemAnimator(new DefaultItemAnimator());
                            }
                        }
                    }
                });


    }

    // Get single mobile company data
    private void getCompany(String company){
        tabletRecyclerView.setLayoutManager(new LinearLayoutManager(TabletsActivity.this));
        tabletActivityProgressBar.setVisibility(View.VISIBLE);
        datalist.clear();

        db.collection("Products")
                .whereEqualTo("category", "Tablet")
                .whereEqualTo("company", company)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null){
                            CustomToast.showToast(TabletsActivity.this, "Error in data fetching");
                            tabletActivityProgressBar.setVisibility(View.GONE);
                            return;
                        }

                        if (value != null && !value.isEmpty()){
                            tabletActivityProgressBar.setVisibility(View.GONE);
                            for (QueryDocumentSnapshot documentSnapshot : value){
                                ProductModel productModel = documentSnapshot.toObject(ProductModel.class);
                                datalist.add(productModel);

                                GridLayoutManager layoutManager = new GridLayoutManager(TabletsActivity.this, 2);
                                tabletRecyclerView.setLayoutManager(layoutManager);
                                adapter = new CategoryProductAdapter(TabletsActivity.this, datalist);
                                tabletRecyclerView.setHasFixedSize(true);
                                tabletRecyclerView.setAdapter(adapter);
                                tabletRecyclerView.setItemAnimator(new DefaultItemAnimator());
                            }
                        }
                    }
                });
    }

    // Get company wise mobile data
    private void getTabletsCompany(){
        tabletRealme.setOnClickListener(v -> getCompany("Realme"));
        tabletApple.setOnClickListener(v -> getCompany("Apple"));
        tabletSamsung.setOnClickListener(v -> getCompany("Samsung"));
        tabletOneplus.setOnClickListener(v -> getCompany("OnePlus"));
        tabletRedmi.setOnClickListener(v -> getCompany("Redmi"));
        tabletPoco.setOnClickListener(v -> getCompany("Poco"));
    }


    private void getImageUrls() {
        databaseReference.child("Tablet").child("imgurls").addValueEventListener(new ValueEventListener() {
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

                    tabletSliderAdapter = new MobileSliderAdapter(TabletsActivity.this, imageUrls);
                    tabletViewPager.setAdapter(tabletSliderAdapter);

                    sliderHandler.postDelayed(slideRunnable, 3000);

                    // Add listener to reset the auto-slide when the page is changed
                    tabletViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                        @Override
                        public void onPageSelected(int position) {
                            sliderHandler.removeCallbacks(slideRunnable);
                            sliderHandler.postDelayed(slideRunnable, 3000);
                        }
                    });

                    // Handle touch events to reset auto-slide interval
                    tabletViewPager.setOnTouchListener((v, event) -> {
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
            if (tabletViewPager != null && tabletSliderAdapter != null) {
                int nextItem = (tabletViewPager.getCurrentItem() + 1) % tabletSliderAdapter.getItemCount();
                tabletViewPager.setCurrentItem(nextItem);
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