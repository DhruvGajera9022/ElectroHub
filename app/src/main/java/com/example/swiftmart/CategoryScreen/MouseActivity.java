package com.example.swiftmart.CategoryScreen;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
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

public class MouseActivity extends AppCompatActivity {
    private ImageView backBtn;
    private TextView toolBarTitle;

    private LinearLayout mouseHp, mouseDell, mouseZebronics, mouseLogitech;
    private NestedScrollView mouseScrollView;
    private HorizontalScrollView mouseHorizontalScrollView;

    private MobileSliderAdapter mouseSliderAdapter;
    private ViewPager2 mouseViewPager;
    private Handler sliderHandler = new Handler();

    private FirebaseFirestore db;
    private DatabaseReference databaseReference;
    private List<String> imageUrls;

    private ArrayList<ProductModel> datalist = new ArrayList<>();
    private CategoryProductAdapter adapter;
    private RecyclerView mouseRecyclerView;
    private ProgressBar mouseActivityProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mouse);

        initialization();
        getMouse();
        getMouseCompany();
        getImageUrls();

        toolBarTitle.setText("Mouse");
        backBtn.setOnClickListener(v -> onBackPressed());
    }

    private void initialization(){

        db = FirebaseFirestore.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("root");
        imageUrls = new ArrayList<>();

        mouseScrollView = findViewById(R.id.mouseScrollView);
        mouseHorizontalScrollView = findViewById(R.id.mouseHorizontalScrollView);

        mouseViewPager = findViewById(R.id.mouseViewPager);

        mouseRecyclerView = findViewById(R.id.mouseRecyclerView);
        mouseActivityProgressBar = findViewById(R.id.mouseActivityProgressBar);

        backBtn = findViewById(R.id.backBtn);
        toolBarTitle = findViewById(R.id.toolBarTitle);

        mouseHp = findViewById(R.id.mouseHp);
        mouseDell = findViewById(R.id.mouseDell);
        mouseZebronics = findViewById(R.id.mouseZebronics);
        mouseLogitech = findViewById(R.id.mouseLogitech);

        mouseScrollView.setVerticalScrollBarEnabled(false);
        mouseHorizontalScrollView.setHorizontalScrollBarEnabled(false);
    }

    // Get all the laptops
    private void getMouse(){
        mouseRecyclerView.setLayoutManager(new LinearLayoutManager(MouseActivity.this));
        mouseActivityProgressBar.setVisibility(View.VISIBLE);
        datalist.clear();

        db.collection("Products")
                .whereEqualTo("category", "Mouse")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null){
                            CustomToast.showToast(MouseActivity.this,  "Error in data fetching");
                            mouseActivityProgressBar.setVisibility(View.GONE);
                            return;
                        }


                        if (value != null && !value.isEmpty()){
                            mouseActivityProgressBar.setVisibility(View.GONE);
                            for (QueryDocumentSnapshot documentSnapshot : value){
                                ProductModel productModel = documentSnapshot.toObject(ProductModel.class);
                                datalist.add(productModel);

                                GridLayoutManager layoutManager = new GridLayoutManager(MouseActivity.this, 2);
                                mouseRecyclerView.setLayoutManager(layoutManager);
                                adapter = new CategoryProductAdapter(MouseActivity.this, datalist);
                                mouseRecyclerView.setHasFixedSize(true);
                                mouseRecyclerView.setAdapter(adapter);
                                mouseRecyclerView.setItemAnimator(new DefaultItemAnimator());
                            }
                        }
                    }
                });


    }

    // Get single mobile company data
    private void getCompany(String company){
        mouseRecyclerView.setLayoutManager(new LinearLayoutManager(MouseActivity.this));
        mouseActivityProgressBar.setVisibility(View.VISIBLE);
        datalist.clear();

        db.collection("Products")
                .whereEqualTo("category", "Mouse")
                .whereEqualTo("company", company)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null){
                            CustomToast.showToast(MouseActivity.this, "Error in data fetching");
                            mouseActivityProgressBar.setVisibility(View.GONE);
                            return;
                        }

                        if (value != null && !value.isEmpty()){
                            mouseActivityProgressBar.setVisibility(View.GONE);
                            for (QueryDocumentSnapshot documentSnapshot : value){
                                ProductModel productModel = documentSnapshot.toObject(ProductModel.class);
                                datalist.add(productModel);

                                GridLayoutManager layoutManager = new GridLayoutManager(MouseActivity.this, 2);
                                mouseRecyclerView.setLayoutManager(layoutManager);
                                adapter = new CategoryProductAdapter(MouseActivity.this, datalist);
                                mouseRecyclerView.setHasFixedSize(true);
                                mouseRecyclerView.setAdapter(adapter);
                                mouseRecyclerView.setItemAnimator(new DefaultItemAnimator());
                            }
                        }
                    }
                });
    }

    // Get company wise mobile data
    private void getMouseCompany(){
        mouseHp.setOnClickListener(v -> getCompany("HP"));
        mouseDell.setOnClickListener(v -> getCompany("Dell"));
        mouseZebronics.setOnClickListener(v -> getCompany("Zebronics"));
        mouseLogitech.setOnClickListener(v -> getCompany("Logitech"));
    }


    private void getImageUrls() {
        databaseReference.child("Mouse").child("imgurls").addValueEventListener(new ValueEventListener() {
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

                    mouseSliderAdapter = new MobileSliderAdapter(MouseActivity.this, imageUrls);
                    mouseViewPager.setAdapter(mouseSliderAdapter);

                    sliderHandler.postDelayed(slideRunnable, 3000);

                    // Add listener to reset the auto-slide when the page is changed
                    mouseViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                        @Override
                        public void onPageSelected(int position) {
                            sliderHandler.removeCallbacks(slideRunnable);
                            sliderHandler.postDelayed(slideRunnable, 3000);
                        }
                    });

                    // Handle touch events to reset auto-slide interval
                    mouseViewPager.setOnTouchListener((v, event) -> {
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
            if (mouseViewPager != null && mouseSliderAdapter != null) {
                int nextItem = (mouseViewPager.getCurrentItem() + 1) % mouseSliderAdapter.getItemCount();
                mouseViewPager.setCurrentItem(nextItem);
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