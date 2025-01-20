package com.example.swiftmart.CategoryScreen;

import android.annotation.SuppressLint;
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

public class EarphoneActivity extends AppCompatActivity {

    LinearLayout boatlogo,realmelogo,onepluslogo,nothinglogo,triggerlogo,trukelogo;
    private ViewPager2 earbudsViewPager;
    private MobileSliderAdapter earphoneSliderAdapter;
    private Handler sliderHandler = new Handler();
    private ImageView backBtn;
    private TextView toolBarTitle;
    private RecyclerView earphoneRecyclerView;
    ArrayList<ProductModel> datalist = new ArrayList<>();
    private FirebaseFirestore db;
    CategoryProductAdapter adapter;
    NestedScrollView earphoneScrollView;
    HorizontalScrollView earphoneHorizontalScrollView;
    ProgressBar earphoneActivityProgressBar;

    private DatabaseReference databaseReference;
    private List<String> imageUrls;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earphone);

        db = FirebaseFirestore.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("root");
        imageUrls = new ArrayList<>();

        earphoneScrollView = findViewById(R.id.earphoneScrollView);
        earphoneHorizontalScrollView = findViewById(R.id.earphoneHorizontalScrollView);

        boatlogo=findViewById(R.id.boatlogo);
        nothinglogo=findViewById(R.id.nothinglogo);
        realmelogo=findViewById(R.id.realmelogo);
        onepluslogo=findViewById(R.id.onepluslogo);
        triggerlogo=findViewById(R.id.triggerlogo);
        trukelogo=findViewById(R.id.trukelogo);

        backBtn=findViewById(R.id.backBtn);
        toolBarTitle=findViewById(R.id.toolBarTitle);

        earphoneRecyclerView=findViewById(R.id.earphoneRecyclerView);
        earphoneActivityProgressBar=findViewById(R.id.earphoneActivityProgressBar);

        earphoneScrollView.setVerticalScrollBarEnabled(false);
        earphoneHorizontalScrollView.setHorizontalScrollBarEnabled(false);

        getEarbuds();
        getEarbudsCompany();

        toolBarTitle.setText("Earbuds");
        backBtn.setOnClickListener(v -> onBackPressed());

        getImageUrls();
        earbudsViewPager = findViewById(R.id.earbudsViewPager);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    // Get all the earbuds
    private void getEarbuds(){
        earphoneRecyclerView.setLayoutManager(new LinearLayoutManager(EarphoneActivity.this));
        earphoneActivityProgressBar.setVisibility(View.VISIBLE);
        datalist.clear();

        db.collection("Products")
                .whereEqualTo("category", "AirBuds")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null){
                            CustomToast.showToast(EarphoneActivity.this, "Error in data fetching");
                            earphoneActivityProgressBar.setVisibility(View.GONE);
                            return;
                        }


                        if (value != null && !value.isEmpty()){
                            earphoneActivityProgressBar.setVisibility(View.GONE);
                            for (QueryDocumentSnapshot documentSnapshot : value){
                                ProductModel productModel = documentSnapshot.toObject(ProductModel.class);
                                datalist.add(productModel);

                                GridLayoutManager layoutManager = new GridLayoutManager(EarphoneActivity.this, 2);
                                earphoneRecyclerView.setLayoutManager(layoutManager);
                                adapter = new CategoryProductAdapter(EarphoneActivity.this, datalist);
                                earphoneRecyclerView.setHasFixedSize(true);
                                earphoneRecyclerView.setAdapter(adapter);
                                earphoneRecyclerView.setItemAnimator(new DefaultItemAnimator());
                            }
                        }
                    }
                });


    }

    // Get single earbuds company data
    private void getCompany(String company){
        earphoneRecyclerView.setLayoutManager(new LinearLayoutManager(EarphoneActivity.this));
        earphoneActivityProgressBar.setVisibility(View.VISIBLE);
        datalist.clear();

        db.collection("Products")
                .whereEqualTo("category", "AirBuds")
                .whereEqualTo("company", company)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null){
                            CustomToast.showToast(EarphoneActivity.this,  "Error in data fetching");
                            earphoneActivityProgressBar.setVisibility(View.GONE);
                            return;
                        }


                        if (value != null && !value.isEmpty()){
                            earphoneActivityProgressBar.setVisibility(View.GONE);
                            for (QueryDocumentSnapshot documentSnapshot : value){
                                ProductModel productModel = documentSnapshot.toObject(ProductModel.class);
                                datalist.add(productModel);

                                GridLayoutManager layoutManager = new GridLayoutManager(EarphoneActivity.this, 2);
                                earphoneRecyclerView.setLayoutManager(layoutManager);
                                adapter = new CategoryProductAdapter(EarphoneActivity.this, datalist);
                                earphoneRecyclerView.setHasFixedSize(true);
                                earphoneRecyclerView.setAdapter(adapter);
                                earphoneRecyclerView.setItemAnimator(new DefaultItemAnimator());
                            }
                        }
                    }
                });


    }

    // Get company wise earbuds data
    private void getEarbudsCompany(){
        boatlogo.setOnClickListener(v -> getCompany("Boat"));
        realmelogo.setOnClickListener(v -> getCompany("Realme"));
        onepluslogo.setOnClickListener(v -> getCompany("OnePlus"));
        nothinglogo.setOnClickListener(v -> getCompany("Nothing"));
        triggerlogo.setOnClickListener(v -> getCompany("Trigger"));
        trukelogo.setOnClickListener(v -> getCompany("Truke"));
    }

    private void getImageUrls() {
        databaseReference.child("AirBuds").child("imgurls").addValueEventListener(new ValueEventListener() {
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

                    earphoneSliderAdapter = new MobileSliderAdapter(EarphoneActivity.this, imageUrls);
                    earbudsViewPager.setAdapter(earphoneSliderAdapter);

                    sliderHandler.postDelayed(slideRunnable, 3000);

                    // Add listener to reset the auto-slide when the page is changed
                    earbudsViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                        @Override
                        public void onPageSelected(int position) {
                            sliderHandler.removeCallbacks(slideRunnable);
                            sliderHandler.postDelayed(slideRunnable, 3000);
                        }
                    });

                    // Handle touch events to reset auto-slide interval
                    earbudsViewPager.setOnTouchListener((v, event) -> {
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
            if (earbudsViewPager != null && earbudsViewPager != null) {
                int nextItem = (earbudsViewPager.getCurrentItem() + 1) % earphoneSliderAdapter.getItemCount();
                earbudsViewPager.setCurrentItem(nextItem);
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