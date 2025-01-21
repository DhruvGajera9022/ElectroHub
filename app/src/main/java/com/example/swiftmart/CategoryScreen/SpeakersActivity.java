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

public class SpeakersActivity extends AppCompatActivity {
    private ImageView backBtn;
    private TextView toolBarTitle;
    
    private LinearLayout lglogo, boatlogo, boultlogo, jbllogo;
    private NestedScrollView speakerScrollView;
    private HorizontalScrollView speakerHorizontalScrollView;

    private MobileSliderAdapter speakerSliderAdapter;
    private ViewPager2 speakerViewPager;
    private Handler sliderHandler = new Handler();

    private FirebaseFirestore db;
    private DatabaseReference databaseReference;
    private List<String> imageUrls;

    private ArrayList<ProductModel> datalist = new ArrayList<>();
    private CategoryProductAdapter adapter;
    private RecyclerView speakerRecyclerView;
    private ProgressBar SpeakersActivityProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speakers);

        initialization();
        getLaptops();
        getSpeakerCompany();
        getImageUrls();
        setStatusBarColor(R.color.home);

        toolBarTitle.setText("Speakers");
        backBtn.setOnClickListener(v -> onBackPressed());

    }

    private void initialization(){

        db = FirebaseFirestore.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("root");
        imageUrls = new ArrayList<>();

        speakerScrollView = findViewById(R.id.speakerScrollView);
        speakerHorizontalScrollView = findViewById(R.id.speakerHorizontalScrollView);

        speakerViewPager = findViewById(R.id.speakerViewPager);

        speakerRecyclerView = findViewById(R.id.speakerRecyclerView);
        SpeakersActivityProgressBar = findViewById(R.id.speakerActivityProgressBar);

        backBtn = findViewById(R.id.backBtn);
        toolBarTitle = findViewById(R.id.toolBarTitle);

        lglogo = findViewById(R.id.lglogo);
        boatlogo = findViewById(R.id.boatlogo);
        boultlogo = findViewById(R.id.boultlogo);
        jbllogo = findViewById(R.id.jbllogo);

        speakerScrollView.setVerticalScrollBarEnabled(false);
        speakerHorizontalScrollView.setHorizontalScrollBarEnabled(false);
    }

    // Get all the laptops
    private void getLaptops(){
        speakerRecyclerView.setLayoutManager(new LinearLayoutManager(SpeakersActivity.this));
        SpeakersActivityProgressBar.setVisibility(View.VISIBLE);
        datalist.clear();

        db.collection("Products")
                .whereEqualTo("category", "Speaker")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null){
                            CustomToast.showToast(SpeakersActivity.this,  "Error in data fetching");
                            SpeakersActivityProgressBar.setVisibility(View.GONE);
                            return;
                        }


                        if (value != null && !value.isEmpty()){
                            SpeakersActivityProgressBar.setVisibility(View.GONE);
                            for (QueryDocumentSnapshot documentSnapshot : value){
                                ProductModel productModel = documentSnapshot.toObject(ProductModel.class);
                                datalist.add(productModel);

                                GridLayoutManager layoutManager = new GridLayoutManager(SpeakersActivity.this, 2);
                                speakerRecyclerView.setLayoutManager(layoutManager);
                                adapter = new CategoryProductAdapter(SpeakersActivity.this, datalist);
                                speakerRecyclerView.setHasFixedSize(true);
                                speakerRecyclerView.setAdapter(adapter);
                                speakerRecyclerView.setItemAnimator(new DefaultItemAnimator());
                            }
                        }
                    }
                });


    }

    // Get single mobile company data
    private void getCompany(String company){
        speakerRecyclerView.setLayoutManager(new LinearLayoutManager(SpeakersActivity.this));
        SpeakersActivityProgressBar.setVisibility(View.VISIBLE);
        datalist.clear();

        db.collection("Products")
                .whereEqualTo("category", "Speaker")
                .whereEqualTo("company", company)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null){
                            CustomToast.showToast(SpeakersActivity.this, "Error in data fetching");
                            SpeakersActivityProgressBar.setVisibility(View.GONE);
                            return;
                        }

                        if (value != null && !value.isEmpty()){
                            SpeakersActivityProgressBar.setVisibility(View.GONE);
                            for (QueryDocumentSnapshot documentSnapshot : value){
                                ProductModel productModel = documentSnapshot.toObject(ProductModel.class);
                                datalist.add(productModel);

                                GridLayoutManager layoutManager = new GridLayoutManager(SpeakersActivity.this, 2);
                                speakerRecyclerView.setLayoutManager(layoutManager);
                                adapter = new CategoryProductAdapter(SpeakersActivity.this, datalist);
                                speakerRecyclerView.setHasFixedSize(true);
                                speakerRecyclerView.setAdapter(adapter);
                                speakerRecyclerView.setItemAnimator(new DefaultItemAnimator());
                            }
                        }
                    }
                });
    }

    // Get company wise mobile data
    private void getSpeakerCompany(){
        lglogo.setOnClickListener(v -> getCompany("LG"));
        boatlogo.setOnClickListener(v -> getCompany("Boat"));
        boultlogo.setOnClickListener(v -> getCompany("Boult"));
        jbllogo.setOnClickListener(v -> getCompany("JBL"));
    }

    private void getImageUrls() {
        databaseReference.child("Speaker").child("imgurls").addValueEventListener(new ValueEventListener() {
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

                    speakerSliderAdapter = new MobileSliderAdapter(SpeakersActivity.this, imageUrls);
                    speakerViewPager.setAdapter(speakerSliderAdapter);

                    sliderHandler.postDelayed(slideRunnable, 3000);

                    // Add listener to reset the auto-slide when the page is changed
                    speakerViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                        @Override
                        public void onPageSelected(int position) {
                            sliderHandler.removeCallbacks(slideRunnable);
                            sliderHandler.postDelayed(slideRunnable, 3000);
                        }
                    });

                    // Handle touch events to reset auto-slide interval
                    speakerViewPager.setOnTouchListener((v, event) -> {
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
            if (speakerViewPager != null && speakerSliderAdapter != null) {
                int nextItem = (speakerViewPager.getCurrentItem() + 1) % speakerSliderAdapter.getItemCount();
                speakerViewPager.setCurrentItem(nextItem);
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