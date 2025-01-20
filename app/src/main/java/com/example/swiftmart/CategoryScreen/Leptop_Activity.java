package com.example.swiftmart.CategoryScreen;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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

public class Leptop_Activity extends AppCompatActivity {
    private ImageView backBtn;
    private TextView toolBarTitle;
    private LinearLayout hplogo, delllogo, lenovologo, acerlogo, asusogo, applelogo, msilogo;
    private NestedScrollView laptopScrollView;
    private HorizontalScrollView laptopHorizontalScrollView;

    private MobileSliderAdapter laptopSliderAdapter;
    private ViewPager2 laptopViewPager;
    private Handler sliderHandler = new Handler();

    private FirebaseFirestore db;
    private DatabaseReference databaseReference;
    private List<String> imageUrls;

    private ArrayList<ProductModel> datalist = new ArrayList<>();
    private CategoryProductAdapter adapter;
    private RecyclerView laptopRecyclerView;
    private ProgressBar laptopActivityProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leptop);

        initialization();
        getLaptops();
        getLaptopCompany();
        getImageUrls();

        toolBarTitle.setText("Laptops");
        backBtn.setOnClickListener(v -> onBackPressed());
    }

    private void initialization(){

        db = FirebaseFirestore.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("root");
        imageUrls = new ArrayList<>();

        laptopScrollView = findViewById(R.id.laptopScrollView);
        laptopHorizontalScrollView = findViewById(R.id.laptopHorizontalScrollView);

        laptopViewPager = findViewById(R.id.laptopViewPager);

        laptopRecyclerView = findViewById(R.id.laptopRecyclerView);
        laptopActivityProgressBar = findViewById(R.id.laptopActivityProgressBar);

        backBtn = findViewById(R.id.backBtn);
        toolBarTitle = findViewById(R.id.toolBarTitle);

        hplogo = findViewById(R.id.hplogo);
        delllogo = findViewById(R.id.delllogo);
        lenovologo = findViewById(R.id.lenovologo);
        acerlogo = findViewById(R.id.acerlogo);
        asusogo = findViewById(R.id.asusogo);
        applelogo = findViewById(R.id.applelogo);
        msilogo = findViewById(R.id.msilogo);

        laptopScrollView.setVerticalScrollBarEnabled(false);
        laptopHorizontalScrollView.setHorizontalScrollBarEnabled(false);
    }

    // Get all the laptops
    private void getLaptops(){
        laptopRecyclerView.setLayoutManager(new LinearLayoutManager(Leptop_Activity.this));
        laptopActivityProgressBar.setVisibility(View.VISIBLE);
        datalist.clear();

        db.collection("Products")
                .whereEqualTo("category", "Laptop")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null){
                            CustomToast.showToast(Leptop_Activity.this,  "Error in data fetching");
                            laptopActivityProgressBar.setVisibility(View.GONE);
                            return;
                        }


                        if (value != null && !value.isEmpty()){
                            laptopActivityProgressBar.setVisibility(View.GONE);
                            for (QueryDocumentSnapshot documentSnapshot : value){
                                ProductModel productModel = documentSnapshot.toObject(ProductModel.class);
                                datalist.add(productModel);

                                GridLayoutManager layoutManager = new GridLayoutManager(Leptop_Activity.this, 2);
                                laptopRecyclerView.setLayoutManager(layoutManager);
                                adapter = new CategoryProductAdapter(Leptop_Activity.this, datalist);
                                laptopRecyclerView.setHasFixedSize(true);
                                laptopRecyclerView.setAdapter(adapter);
                                laptopRecyclerView.setItemAnimator(new DefaultItemAnimator());
                            }
                        }
                    }
                });


    }

    // Get single mobile company data
    private void getCompany(String company){
        laptopRecyclerView.setLayoutManager(new LinearLayoutManager(Leptop_Activity.this));
        laptopActivityProgressBar.setVisibility(View.VISIBLE);
        datalist.clear();

        db.collection("Products")
                .whereEqualTo("category", "Laptop")
                .whereEqualTo("company", company)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null){
                            CustomToast.showToast(Leptop_Activity.this, "Error in data fetching");
                            laptopActivityProgressBar.setVisibility(View.GONE);
                            return;
                        }

                        if (value != null && !value.isEmpty()){
                            laptopActivityProgressBar.setVisibility(View.GONE);
                            for (QueryDocumentSnapshot documentSnapshot : value){
                                ProductModel productModel = documentSnapshot.toObject(ProductModel.class);
                                datalist.add(productModel);

                                GridLayoutManager layoutManager = new GridLayoutManager(Leptop_Activity.this, 2);
                                laptopRecyclerView.setLayoutManager(layoutManager);
                                adapter = new CategoryProductAdapter(Leptop_Activity.this, datalist);
                                laptopRecyclerView.setHasFixedSize(true);
                                laptopRecyclerView.setAdapter(adapter);
                                laptopRecyclerView.setItemAnimator(new DefaultItemAnimator());
                            }
                        }
                    }
                });
    }

    // Get company wise mobile data
    private void getLaptopCompany(){
        hplogo.setOnClickListener(v -> getCompany("HP"));
        delllogo.setOnClickListener(v -> getCompany("Dell"));
        lenovologo.setOnClickListener(v -> getCompany("Lenovo"));
        acerlogo.setOnClickListener(v -> getCompany("Acer"));
        asusogo.setOnClickListener(v -> getCompany("Asus"));
        applelogo.setOnClickListener(v -> getCompany("Apple"));
        msilogo.setOnClickListener(v -> getCompany("MSI"));
    }


    private void getImageUrls() {
        databaseReference.child("Laptop").child("imgurls").addValueEventListener(new ValueEventListener() {
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

                    laptopSliderAdapter = new MobileSliderAdapter(Leptop_Activity.this, imageUrls);
                    laptopViewPager.setAdapter(laptopSliderAdapter);

                    sliderHandler.postDelayed(slideRunnable, 3000);

                    // Add listener to reset the auto-slide when the page is changed
                    laptopViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                        @Override
                        public void onPageSelected(int position) {
                            sliderHandler.removeCallbacks(slideRunnable);
                            sliderHandler.postDelayed(slideRunnable, 3000);
                        }
                    });

                    // Handle touch events to reset auto-slide interval
                    laptopViewPager.setOnTouchListener((v, event) -> {
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
            if (laptopViewPager != null && laptopSliderAdapter != null) {
                int nextItem = (laptopViewPager.getCurrentItem() + 1) % laptopSliderAdapter.getItemCount();
                laptopViewPager.setCurrentItem(nextItem);
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

}
