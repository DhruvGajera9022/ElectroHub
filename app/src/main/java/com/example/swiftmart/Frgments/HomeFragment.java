package com.example.swiftmart.Frgments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.widget.SearchView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.swiftmart.Adapter.MobileSliderAdapter;
import com.example.swiftmart.Adapter.ProductAdapter;
import com.example.swiftmart.AllProducts;
import com.example.swiftmart.CategoryScreen.CameraActivity;
import com.example.swiftmart.CategoryScreen.EarphoneActivity;
import com.example.swiftmart.Account.Edit_profile_Activity;
import com.example.swiftmart.CategoryScreen.HeadPhoneActivity;
import com.example.swiftmart.CategoryScreen.KeyboardActivity;
import com.example.swiftmart.CategoryScreen.Leptop_Activity;
import com.example.swiftmart.CategoryScreen.MobilesActivity;
import com.example.swiftmart.CategoryScreen.MouseActivity;
import com.example.swiftmart.CategoryScreen.SmartWatchActivity;
import com.example.swiftmart.CategoryScreen.SpeakersActivity;
import com.example.swiftmart.CategoryScreen.TabletsActivity;
import com.example.swiftmart.Model.ProductModel;
import com.example.swiftmart.R;
import com.example.swiftmart.Utils.CustomToast;
import com.example.swiftmart.CategoryScreen.tv_brandActivity;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeFragment extends Fragment {

    LinearLayout mobiles, earbuds, tv, laptop, headphone, speaker, keyword, mouse, camera, smartwatch, tablet;
    FirebaseFirestore db;
    RecyclerView homeFragmentFeaturedRecyclerView, homeFragmentMostPopularRecyclerView, homeFragmentNewRecyclerView;
    CircleImageView homeFragmentUserAvatar;
    LinearLayout topPanel;
    TextView homeFragmentUserName, seeAll1, seeAll2, seeAll3;
    FirebaseAuth mAuth;
    String uid;
    NestedScrollView homeFragmentScrollView;
    BottomSheetDialog sheetDialog;
    SearchView homeFragmentSearchView;

    private LinearLayout sliderIndicator;
    private int dotCount;
    private ImageView[] dots;

    private HorizontalScrollView homeFragmentHorizontalScrollView;
    private ViewPager2 mainViewPager;
    private MobileSliderAdapter mobilesliderAdapter;
    private Handler sliderHandler = new Handler();

    private DatabaseReference databaseReference;
    private List<String> imageUrls;

    private ArrayList<ProductModel> featuredDataList = new ArrayList<>();
    private ArrayList<ProductModel> mostPopularDataList = new ArrayList<>();
    private ArrayList<ProductModel> newArrivedDataList = new ArrayList<>();

    private ProductAdapter featuredAdapter;
    private ProductAdapter mostPopularAdapter;
    private ProductAdapter newArrivedAdapter;

    // Categories
    private ImageView homeMobileImage, homeEarbudsImage, homeTVImage, homeLaptopImage, homeHeadphoneImage, homeSpeakersImage, homeKeyboardImage, homeMouseImage, homeCameraImage, homeSmartwatchImage, homeTabletImage;

    public HomeFragment() {

    }


    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("root");
        imageUrls = new ArrayList<>();

        homeFragmentScrollView = view.findViewById(R.id.homeFragmentScrollView);
        homeFragmentHorizontalScrollView = view.findViewById(R.id.homeFragmentHorizontalScrollView);

        homeFragmentFeaturedRecyclerView = view.findViewById(R.id.homeFragmentFeaturedRecyclerView);
        homeFragmentMostPopularRecyclerView = view.findViewById(R.id.homeFragmentMostPopularRecyclerView);
        homeFragmentNewRecyclerView = view.findViewById(R.id.homeFragmentNewRecyclerView);

        mobiles = view.findViewById(R.id.mobiles);
        earbuds = view.findViewById(R.id.earbuds);
        tv = view.findViewById(R.id.tv);
        laptop = view.findViewById(R.id.laptop);
        headphone = view.findViewById(R.id.headphone);
        speaker = view.findViewById(R.id.speaker);
        keyword = view.findViewById(R.id.keyword);
        mouse = view.findViewById(R.id.mouse);
        camera = view.findViewById(R.id.camera);
        smartwatch = view.findViewById(R.id.smartwatch);
        tablet = view.findViewById(R.id.tablet);
        homeFragmentUserAvatar = view.findViewById(R.id.homeFragmentUserAvatar);
        homeFragmentUserName = view.findViewById(R.id.homeFragmentUserName);
        homeFragmentSearchView = view.findViewById(R.id.homeFragmentSearchView);
        topPanel = view.findViewById(R.id.topPanel);

        sliderIndicator = view.findViewById(R.id.sliderIndicator);
        mainViewPager = view.findViewById(R.id.mainViewPager);

        seeAll1 = view.findViewById(R.id.seeAll1);
        seeAll2 = view.findViewById(R.id.seeAll2);
        seeAll3 = view.findViewById(R.id.seeAll3);

        homeMobileImage = view.findViewById(R.id.homeMobileImage);
        homeEarbudsImage = view.findViewById(R.id.homeEarbudsImage);
        homeTVImage = view.findViewById(R.id.homeTVImage);
        homeLaptopImage = view.findViewById(R.id.homeLaptopImage);
        homeHeadphoneImage = view.findViewById(R.id.homeHeadphoneImage);
        homeSpeakersImage = view.findViewById(R.id.homeSpeakersImage);
        homeKeyboardImage = view.findViewById(R.id.homeKeyboardImage);
        homeMouseImage = view.findViewById(R.id.homeMouseImage);
        homeCameraImage = view.findViewById(R.id.homeCameraImage);
        homeSmartwatchImage = view.findViewById(R.id.homeSmartwatchImage);
        homeTabletImage = view.findViewById(R.id.homeTabletImage);

        homeFragmentScrollView.setVerticalScrollBarEnabled(false);
        homeFragmentHorizontalScrollView.setHorizontalScrollBarEnabled(false);


        getUserData();
        handleHomeFragmentUserAvtarClick();
        handleTopPanelClick();
//        handleSearch();
//        getAllProducts();
        getTrendingData();
        getMostPopularData();
        getNewArrivedData();
        handleSeeAllClick();
        getImageUrls();

        handleMobileClick();
        handleEarbudsClick();
        handleTVClick();
        handleLaptopClick();
        handleHeadphoneClick();
        handleSpeakerClick();
        handleKeyboardClick();
        handleMouseClick();
        handleCameraClick();
        handleSmartWatchClick();
        handleTabletClick();

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                showBottomSheetDialog();
            }
        });

        return view;
    }

    // Get the user data from the database
    private void getUserData(){
        uid = mAuth.getCurrentUser().getUid();
        DocumentReference reference = db.collection("Users").document(uid);

        reference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null && value.exists()){
                    homeFragmentUserName.setText(value.getString("Username").split(" ")[0]);
                    Picasso.get().load(value.getString("Image")).into(homeFragmentUserAvatar);
                }
            }
        });

    }

    // handle homeFragmentUserAvatar click
    private void handleHomeFragmentUserAvtarClick(){
        homeFragmentUserAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homeFragmentUserAvatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), Edit_profile_Activity.class);
                        startActivity(intent);
                    }
                });
            }
        });
    }

    private void handleTopPanelClick(){
        topPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), Edit_profile_Activity.class);
                startActivity(intent);
            }
        });
    }

    // get Featured Data
    private void getTrendingData(){
        homeFragmentFeaturedRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        db.collection("Products")
                .whereIn("category", Arrays.asList("Mobile", "AirBuds", "Laptop"))
                .limit(10)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null){
                            CustomToast.showToast(getContext(), "Error in data fetching");
                            return;
                        }

                        if (value != null && !value.isEmpty()){
                            featuredDataList.clear();
                            for (QueryDocumentSnapshot documentSnapshot : value){
                                ProductModel productModel = documentSnapshot.toObject(ProductModel.class);
                                featuredDataList.add(productModel);

                                featuredAdapter = new ProductAdapter(getContext(), featuredDataList);
                                homeFragmentFeaturedRecyclerView.setHasFixedSize(true);
                                homeFragmentFeaturedRecyclerView.setAdapter(featuredAdapter);
                                homeFragmentFeaturedRecyclerView.setItemAnimator(new DefaultItemAnimator());
                            }
                        }
                    }
                });
    }

    // get Most Popular Data
    private void getMostPopularData(){
        homeFragmentMostPopularRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        db.collection("Products")
                .orderBy("price", Query.Direction.DESCENDING)
                .limit(10)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null){
                            CustomToast.showToast(getContext(), "Error in data fetching");
                            return;
                        }

                        if (value != null && !value.isEmpty()){
                            mostPopularDataList.clear();
                            for (QueryDocumentSnapshot documentSnapshot : value){
                                ProductModel productModel = documentSnapshot.toObject(ProductModel.class);
                                mostPopularDataList.add(productModel);

                                mostPopularAdapter = new ProductAdapter(getContext(), mostPopularDataList);
                                homeFragmentMostPopularRecyclerView.setHasFixedSize(true);
                                homeFragmentMostPopularRecyclerView.setAdapter(mostPopularAdapter);
                                homeFragmentMostPopularRecyclerView.setItemAnimator(new DefaultItemAnimator());
                            }
                        }
                    }
                });
    }

    // get New Arrived Data
    private void getNewArrivedData(){
        homeFragmentNewRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        db.collection("Products")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(10)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null){
                            CustomToast.showToast(getContext(), "Error in data fetching");
                            return;
                        }

                        if (value != null && !value.isEmpty()){
                            newArrivedDataList.clear();
                            for (QueryDocumentSnapshot documentSnapshot : value){
                                ProductModel productModel = documentSnapshot.toObject(ProductModel.class);
                                newArrivedDataList.add(productModel);

                                newArrivedAdapter = new ProductAdapter(getContext(), newArrivedDataList);
                                homeFragmentNewRecyclerView.setHasFixedSize(true);
                                homeFragmentNewRecyclerView.setAdapter(newArrivedAdapter);
                                homeFragmentNewRecyclerView.setItemAnimator(new DefaultItemAnimator());
                            }
                        }
                    }
                });
    }

    // handle see all click
    private void handleSeeAllClick(){
        seeAll1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AllProducts.class);
                startActivity(intent);
            }
        });
        seeAll2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AllProducts.class);
                startActivity(intent);
            }
        });
        seeAll3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AllProducts.class);
                startActivity(intent);
            }
        });
    }


    // handle mobile click
    private void handleMobileClick(){
        homeMobileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MobilesActivity.class);
                startActivity(intent);
            }
        });
    }

    // handle earbuds click
    private void handleEarbudsClick(){
        homeEarbudsImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), EarphoneActivity.class);
                startActivity(intent);
            }
        });
    }

    // handle tv click
    private void handleTVClick(){
        homeTVImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), tv_brandActivity.class);
                startActivity(intent);
            }
        });
    }

    // handle laptop click
    private void handleLaptopClick(){
        homeLaptopImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), Leptop_Activity.class);
                startActivity(intent);
            }
        });
    }

    // handle speaker click
    private void handleHeadphoneClick(){
        homeHeadphoneImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), HeadPhoneActivity.class);
                startActivity(intent);
            }
        });
    }

    // handle speaker click
    private void handleSpeakerClick(){
        homeSpeakersImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), SpeakersActivity.class);
                startActivity(intent);
            }
        });
    }

    // handle keyboard click
    private void handleKeyboardClick(){
        homeKeyboardImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), KeyboardActivity.class);
                startActivity(intent);
            }
        });
    }

    // handle mouse click
    private void handleMouseClick(){
        homeMouseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MouseActivity.class);
                startActivity(intent);
            }
        });
    }

    // handle camera click
    private void handleCameraClick(){
        homeCameraImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), CameraActivity.class);
                startActivity(intent);
            }
        });
    }

    // handle smart watch click
    private void handleSmartWatchClick(){
        homeSmartwatchImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), SmartWatchActivity.class);
                startActivity(intent);
            }
        });
    }

    // handle tablet click
    private void handleTabletClick(){
        homeTabletImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), TabletsActivity.class);
                startActivity(intent);
            }
        });
    }



    private void getImageUrls() {
        databaseReference.child("Home").child("imgurls").addValueEventListener(new ValueEventListener() {
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

                    // Set adapter to ViewPager2
                    mobilesliderAdapter = new MobileSliderAdapter(getContext(), imageUrls);
                    mainViewPager.setAdapter(mobilesliderAdapter);

                    // Setup the slider indicator
                    setupSliderIndicator();

                    // Auto-slide functionality
                    sliderHandler.postDelayed(slideRunnable, 3000);

                    // Handle page change
                    mainViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                        @Override
                        public void onPageSelected(int position) {
                            updateIndicator(position);
                            sliderHandler.removeCallbacks(slideRunnable);
                            sliderHandler.postDelayed(slideRunnable, 3000);
                        }
                    });

                    // Reset auto-slide interval on touch
                    mainViewPager.setOnTouchListener((v, event) -> {
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
        if (getContext() == null) {
            Log.e("HomeFragment", "setupSliderIndicator: Context is null, skipping setup.");
            return;
        }

        dotCount = mobilesliderAdapter.getItemCount();
        dots = new ImageView[dotCount];

        // Clear any existing dots
        sliderIndicator.removeAllViews();

        // Create and add dots
        for (int i = 0; i < dotCount; i++) {
            dots[i] = new ImageView(getContext()); // Ensure getContext() is not null before calling this
            dots[i].setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.indicator_non_active));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(8, 0, 8, 0);
            sliderIndicator.addView(dots[i], params);
        }

        // Activate the first dot
        if (dotCount > 0) {
            dots[0].setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.indicator_active));
        }
    }

    private void updateIndicator(int position) {
        // Reset all dots to inactive
        for (int i = 0; i < dotCount; i++) {
            dots[i].setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.indicator_non_active));
        }

        // Set the current dot to active
        if (position >= 0 && position < dotCount) {
            dots[position].setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.indicator_active));
        }
    }

    private final Runnable slideRunnable = new Runnable() {
        @Override
        public void run() {
            if (mainViewPager != null && mobilesliderAdapter != null) {
                int nextItem = (mainViewPager.getCurrentItem() + 1) % mobilesliderAdapter.getItemCount();
                mainViewPager.setCurrentItem(nextItem);
                sliderHandler.postDelayed(this, 3000);
            }
        }
    };


    @Override
    public void onPause() {
        super.onPause();
        sliderHandler.removeCallbacks(slideRunnable);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mobilesliderAdapter != null && getContext() != null) {
            setupSliderIndicator();
        }

        sliderHandler.postDelayed(slideRunnable, 3000);
    }


    private void showBottomSheetDialog() {
        sheetDialog = new BottomSheetDialog(requireContext(), R.style.BottomSheetDialog);
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.bottom_sheet_dialog,
                (LinearLayout) getView().findViewById(R.id.bottomSheetLinearLayout));

        Button bottomSheetCancelButton = dialogView.findViewById(R.id.bottomSheetCancelButton);
        Button bottomSheetOkayButton = dialogView.findViewById(R.id.bottomSheetOkayButton);

        sheetDialog.setContentView(dialogView);
        sheetDialog.setCancelable(false);
        sheetDialog.show();

        bottomSheetCancelButton.setOnClickListener(v -> sheetDialog.dismiss());

        bottomSheetOkayButton.setOnClickListener(v -> {
            sheetDialog.dismiss();
            requireActivity().finish();
        });
    }

}