package com.example.swiftmart;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.swiftmart.Adapter.CategoryProductAdapter;
import com.example.swiftmart.Adapter.ProductAdapter;
import com.example.swiftmart.Adapter.ProductImageSliderAdapter;
import com.example.swiftmart.CategoryScreen.MobilesActivity;
import com.example.swiftmart.Model.ProductModel;
import com.example.swiftmart.Model.RatingModel;
import com.example.swiftmart.Utils.CustomToast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.razorpay.Checkout;

import org.json.JSONObject;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ProductDetailsActivity extends AppCompatActivity {
    private TextView productDetailsProductName, productDetailsProductDescription,
            productDetailsProductPrice, expandDescriptionButton, productDetailsRating,
            productDetailsRatingCount, similarText;
    private String productId, productCategory, productCompany;
    private ViewPager2 productDetailsViewPager;
    private AppCompatButton productBuyNowButton;
    private LinearLayout productAddToCartButton;
    private NestedScrollView productDetailsNestedScrollView;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String uid;
    private List<String> currentImageUrls;

    private Handler sliderHandler = new Handler(Looper.getMainLooper());
    private Runnable sliderRunnable;

    private RelativeLayout productDetailsRelativeLayout;

    private RecyclerView productDetailsSimilarRecyclerView, productDetailsExploreMoreRecyclerView;
    private ArrayList<ProductModel> similarDataList = new ArrayList<>();
    private ArrayList<ProductModel> exploreMoreDataList = new ArrayList<>();
    private ProductAdapter similarAdapter;
    private CategoryProductAdapter exploreMoreAdapter;

    private ImageView productDetailsBackArrow, productDetailsWishlist, productDetailsShare;

    private String userName, userPhone;

    private LinearLayout sliderIndicator;
    private int dotCount;
    private View[] dots;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        // Check for the dynamic link
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, pendingDynamicLinkData -> {
                    if (pendingDynamicLinkData != null) {
                        Uri deepLink = pendingDynamicLinkData.getLink();
                        if (deepLink != null && deepLink.getQueryParameter("productId") != null) {
                            productId = deepLink.getQueryParameter("productId");
                            loadProductData(productId); // Load the product details
                        }
                    }
                })
                .addOnFailureListener(this, e -> Log.e("DynamicLink", "Error fetching dynamic link", e));


        initialization();
        loadProductData(productId);
        handleAddToCartClick();
        handleShare();
        handleBuyClick();
        getUserData();
        handleOnBackArrowPress();
        getProductRating();

        saveRecentlyViewed(productId, ProductDetailsActivity.this);

        setStatusBarColor(R.color.home);

    }

    private void initialization(){

        productDetailsNestedScrollView = findViewById(R.id.productDetailsNestedScrollView);

        productDetailsRelativeLayout = findViewById(R.id.productDetailsRelativeLayout);

        productDetailsProductName = findViewById(R.id.productDetailsProductName);
        productDetailsProductDescription = findViewById(R.id.productDetailsProductDescription);
        productDetailsProductPrice = findViewById(R.id.productDetailsProductPrice);
        productDetailsRating = findViewById(R.id.productDetailsRating);
        productDetailsRatingCount = findViewById(R.id.productDetailsRatingCount);

        sliderIndicator = findViewById(R.id.sliderIndicator);
        productDetailsViewPager = findViewById(R.id.productDetailsViewPager);

        productAddToCartButton = findViewById(R.id.productAddToCartButton);
        productBuyNowButton = findViewById(R.id.productBuyNowButton);

        productDetailsBackArrow = findViewById(R.id.productDetailsBackArrow);
        productDetailsWishlist = findViewById(R.id.productDetailsWishlist);
        productDetailsShare = findViewById(R.id.productDetailsShare);

        productDetailsSimilarRecyclerView = findViewById(R.id.productDetailsSimilarRecyclerView);
        productDetailsExploreMoreRecyclerView = findViewById(R.id.productDetailsExploreMoreRecyclerView);
        similarText = findViewById(R.id.similarText);

        expandDescriptionButton = findViewById(R.id.expandDescriptionButton);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();

        productId = getIntent().getStringExtra("productId");

        productDetailsNestedScrollView.setVerticalScrollBarEnabled(false);

    }


    private void loadProductData(String productId) {
        db.collection("Products").document(productId)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            CustomToast.showToast(ProductDetailsActivity.this,  "Error in fetching details");
                        }

                        if (value != null && value.exists()){
                            ProductModel product = value.toObject(ProductModel.class);
                            if (product != null) {
                                displayProductDetails(product);
                                setupImageSlider(product.getImgurls());
                                currentImageUrls = product.getImgurls();
                                productCategory = product.getCategory();
                                productCompany = product.getCompany();

                                getSimilarProducts(productId, productCategory, productCompany);
                                getExploreMoreProducts(productCategory, productCompany);
                            }
                        }
                    }
                });
    }


    private void displayProductDetails(ProductModel product) {
        productDetailsProductName.setText(product.getName());

        double unitPrice = Double.parseDouble(product.getPrice());
        NumberFormat currencyFormat = NumberFormat.getNumberInstance(Locale.getDefault());
        productDetailsProductPrice.setText(currencyFormat.format(unitPrice));

        String fullDescription = product.getDescription();

        String shortenedDescription = fullDescription.length() > 100 ? fullDescription.substring(0, 100) + "..." : fullDescription;
        productDetailsProductDescription.setText(shortenedDescription);

        expandDescriptionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (productDetailsProductDescription.getMaxLines() == 3) {
                    // Show full description
                    productDetailsProductDescription.setMaxLines(Integer.MAX_VALUE);
                    productDetailsProductDescription.setText(fullDescription);
                    expandDescriptionButton.setText("Less");
                } else {
                    // Show shortened description
                    productDetailsProductDescription.setMaxLines(3);
                    productDetailsProductDescription.setText(shortenedDescription);
                    expandDescriptionButton.setText("More");
                }
            }
        });


        // Check if the product is in the wishlist using QuerySnapshot
        db.collection("Users")
                .document(uid)
                .collection("wishlist")
                .whereEqualTo("pid", product.getPid())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (value != null && !value.isEmpty()){
                            product.setWishlisted(true);
                            productDetailsWishlist.setImageResource(R.drawable.ic_heart_filled);
                        }else {
                            product.setWishlisted(false);
                            productDetailsWishlist.setImageResource(R.drawable.ic_heart_outline);
                        }
                    }
                });

        productDetailsWishlist.setImageResource(
                product.isWishlisted() ? R.drawable.ic_heart_filled : R.drawable.ic_heart_outline);

        handleWishlist(product);
    }

    private void setupImageSlider(List<String> imageUrls) {
        ProductImageSliderAdapter adapter = new ProductImageSliderAdapter(this, imageUrls);
        productDetailsViewPager.setAdapter(adapter);

        // Setup the slider indicator
        setupSliderIndicator(imageUrls.size());

        sliderRunnable = new Runnable() {
            @Override
            public void run() {
                int currentItem = productDetailsViewPager.getCurrentItem();
                int nextItem = (currentItem + 1) % imageUrls.size();
                productDetailsViewPager.setCurrentItem(nextItem, true);
                sliderHandler.postDelayed(this, 3000);
            }
        };

        sliderHandler.postDelayed(sliderRunnable, 3000);

        productDetailsViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                updateIndicator(position);
                sliderHandler.removeCallbacks(sliderRunnable);
                sliderHandler.postDelayed(sliderRunnable, 3000);
            }
        });

        productDetailsViewPager.setOnTouchListener((v, event) -> {
            sliderHandler.removeCallbacks(sliderRunnable);
            sliderHandler.postDelayed(sliderRunnable, 3000);
            return false;
        });
    }

    private void setupSliderIndicator(int count) {
        dotCount = count;
        dots = new View[dotCount]; // Use View[] to support both ImageView and ProgressBar

        // Clear existing indicators
        sliderIndicator.removeAllViews();

        for (int i = 0; i < dotCount; i++) {
            if (i == 0) {
                // First indicator starts as a ProgressBar with an inactive background
                ProgressBar progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
                progressBar.setProgressDrawable(ContextCompat.getDrawable(this, R.drawable.progress_bar_fill)); // Set fill color
                progressBar.setBackground(ContextCompat.getDrawable(this, R.drawable.progress_bar_background)); // Set background color
                progressBar.setLayoutParams(new LinearLayout.LayoutParams(100, 10)); // Adjust width for line effect
                progressBar.setMax(100);
                progressBar.setProgress(0); // Start with 0 progress
                dots[i] = progressBar;
            } else {
                // Other indicators start as dots
                ImageView dot = new ImageView(this);
                dot.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.indicator_non_active));
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(20, 20); // Dot size
                params.setMargins(8, 0, 8, 0);
                dot.setLayoutParams(params);
                dots[i] = dot;
            }
            sliderIndicator.addView(dots[i]);
        }

        startProgressAnimation(0); // Start animation for the first image
    }

    private void startProgressAnimation(int position) {
        if (position < dotCount) {
            if (dots[position] instanceof ProgressBar) {
                ((ProgressBar) dots[position]).setProgress(0);
            }

            ValueAnimator animator = ValueAnimator.ofInt(0, 100);
            animator.setDuration(3000); // Match the slide duration
            animator.addUpdateListener(animation -> {
                int progress = (int) animation.getAnimatedValue();
                if (dots[position] instanceof ProgressBar) {
                    ((ProgressBar) dots[position]).setProgress(progress);
                }
            });

            animator.start();
        }
    }

    private void updateIndicator(int position) {
        for (int i = 0; i < dotCount; i++) {
            if (dots[i] instanceof ProgressBar) {
                sliderIndicator.removeView(dots[i]); // Remove previous progress bar
                ImageView dot = new ImageView(this);
                dot.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.indicator_non_active));
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(20, 20);
                params.setMargins(8, 0, 8, 0);
                dot.setLayoutParams(params);
                dots[i] = dot;
                sliderIndicator.addView(dot, i);
            }
        }

        // Convert the new active position into a progress bar with inactive background
        ProgressBar progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        progressBar.setProgressDrawable(ContextCompat.getDrawable(this, R.drawable.progress_bar_fill)); // Apply fill
        progressBar.setBackground(ContextCompat.getDrawable(this, R.drawable.progress_bar_background)); // Apply background
        progressBar.setLayoutParams(new LinearLayout.LayoutParams(100, 10)); // Set size

        progressBar.setMax(100);
        progressBar.setProgress(0);

        // Center the ProgressBar vertically in the layout
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(100, 10);
        params.gravity = Gravity.CENTER_VERTICAL;
        progressBar.setLayoutParams(params);

        sliderIndicator.removeView(dots[position]);
        dots[position] = progressBar;
        sliderIndicator.addView(progressBar, position);

        startProgressAnimation(position);
    }

    public void saveRecentlyViewed(String productId, Context context) {
        SharedPreferences preferences = context.getSharedPreferences("recently_viewed", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        // Get the current timestamp in milliseconds
        long timestamp = System.currentTimeMillis();

        // Save the product ID and timestamp
        editor.putString("product_" + productId, productId);
        editor.putLong("timestamp_" + productId, timestamp);
        editor.apply();
    }


    // handle wishlist
    private void handleWishlist(ProductModel product){
        productDetailsWishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isWishlisted = !product.isWishlisted();
                product.setWishlisted(isWishlisted);

                // Update UI
                productDetailsWishlist.setImageResource(
                        isWishlisted ? R.drawable.ic_heart_filled : R.drawable.ic_heart_outline);

                // Update Firestore
                if (isWishlisted) {
                    db.collection("Users")
                            .document(uid)
                            .collection("wishlist")
                            .document(product.getPid())
                            .set(product);
                } else {
                    db.collection("Users")
                            .document(uid)
                            .collection("wishlist")
                            .document(product.getPid())
                            .delete();
                }
            }
        });
    }


    // handle share
    private void handleShare(){
        productDetailsShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String deepLink = "https://swiftmartstore.page.link/product?productId=" + productId;

                FirebaseDynamicLinks.getInstance().createDynamicLink()
                        .setLink(Uri.parse(deepLink))
                        .setDomainUriPrefix("https://swiftmartstore.page.link")
                        .setAndroidParameters(
                                new DynamicLink.AndroidParameters.Builder()
                                        .build())
                        .buildShortDynamicLink()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful() && task.getResult() != null) {
                                Uri shortLink = task.getResult().getShortLink();

                                // Share the short link
                                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                                sharingIntent.setType("text/plain");
                                String shareBody = "Check out this product: " + shortLink.toString();
                                sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                                startActivity(Intent.createChooser(sharingIntent, "Share using"));
                            } else {
                                Toast.makeText(ProductDetailsActivity.this, "Failed to generate link", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }


    // handle buy click
    private void handleBuyClick(){
        productBuyNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductDetailsActivity.this, ConfirmAddressActivity.class);
                intent.putExtra("productId", productId);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }

    // handle cart click
    private void handleAddToCartClick(){
        productAddToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToCart();
            }
        });
    }

    // handle add to cart
    private void addToCart() {
        String productName = productDetailsProductName.getText().toString();
        String productPrice = productDetailsProductPrice.getText().toString();
        productPrice = productPrice.replace(",", "");
        String productDescription = productDetailsProductDescription.getText().toString();

        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MM/dd/yyyy");
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        String saveCurrentDate = currentDate.format(calForDate.getTime());
        String saveCurrentTime = currentTime.format(calForDate.getTime());

        Map<String, Object> cartMap = new HashMap<>();
        cartMap.put("imgurls", currentImageUrls);
        cartMap.put("name", productName);
        cartMap.put("price", productPrice);
        cartMap.put("category", productCategory);
        cartMap.put("company", productCompany);
        cartMap.put("description", productDescription);
        cartMap.put("currentDate", saveCurrentDate);
        cartMap.put("currentTime", saveCurrentTime);
        cartMap.put("pid", productId);
        cartMap.put("qty", "1");

        db.collection("Users")
                .document(uid)
                .collection("Cart")
                .whereEqualTo("pid", productId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        CustomToast.showToast(ProductDetailsActivity.this, "Item is already in your cart");
                    } else {
                        db.collection("Users")
                                .document(uid)
                                .collection("Cart")
                                .add(cartMap)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        String oid = documentReference.getId();
                                        documentReference.update("oid", oid)
                                                .addOnSuccessListener(aVoid -> {
                                                    CustomToast.showToast(ProductDetailsActivity.this, "Added to Cart");
                                                    onBackPressed();
                                                })
                                                .addOnFailureListener(e -> {
                                                    CustomToast.showToast(ProductDetailsActivity.this, "Failed to add to cart");
                                                });
                                    }
                                });
                    }
                });
    }


    // handle on back arrow press
    private void handleOnBackArrowPress() {
        productDetailsBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    // get user data
    private void getUserData(){
        uid = mAuth.getCurrentUser().getUid();
        DocumentReference reference = db.collection("Users").document(uid);

        reference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null && value.exists()){
                    userName = value.getString("Username");
                    userPhone = value.getString("Number");
                }
            }
        });

    }


    // get allRating
    private void getProductRating(){
        if (productId == null || productId.isEmpty()) {
            Log.d("ProductDetailsRating", "Product ID is null or empty");
            return;
        }

        db.collection("Ratings")
                .whereEqualTo("pid", productId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.isEmpty()) {
                            Log.d("ProductDetailsRating", "No ratings found for the product.");
                            return;
                        }

                        float totalRating = 0;
                        int ratingCount = 0;

                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                            RatingModel rating = document.toObject(RatingModel.class);
                            if (rating != null) {
                                try {
                                    float ratingValue = Float.parseFloat(rating.getRating());
                                    totalRating += ratingValue;
                                    ratingCount++;
                                } catch (NumberFormatException e) {
                                    Log.e("ProductDetailsRating", "Invalid rating value: " + rating.getRating(), e);
                                }
                            }
                        }
                        if (ratingCount > 0) {
                            float averageRating = totalRating / ratingCount;

                            String averageRatingText = String.format("%.1f", averageRating);

                            productDetailsRating.setText(averageRatingText);
                            productDetailsRatingCount.setText("(" + ratingCount + (ratingCount > 1 ? " Reviews)" : " Review)")); // Handles singular/plural
                        } else {
                            Log.d("ProductDetailsRating", "No valid ratings found.");
                            productDetailsRating.setText("0.0"); // Default rating
                            productDetailsRatingCount.setText("(0 Reviews)"); // Default review count
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("ProductDetailsRating", "Error fetching ratings: " + e.getMessage(), e);
                    }
                });
    }

    private void setStatusBarColor(int colorResource) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(getResources().getColor(colorResource));
        }
    }


    // get similar products
    private void getSimilarProducts(String productId, String productCategory, String productCompany) {
        productDetailsSimilarRecyclerView.setLayoutManager(new LinearLayoutManager(ProductDetailsActivity.this, LinearLayoutManager.HORIZONTAL, false));

        db.collection("Products")
                .whereEqualTo("category", productCategory)
                .whereEqualTo("company", productCompany)
                .limit(10)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            return;
                        }

                        if (value != null && !value.isEmpty()) {
                            similarDataList.clear();
                            for (QueryDocumentSnapshot documentSnapshot : value) {
                                ProductModel productModel = documentSnapshot.toObject(ProductModel.class);

                                // Skip the current product
                                if (!productModel.getPid().equals(productId)) {
                                    similarDataList.add(productModel);
                                }
                            }

                            // Only update adapter after the loop
                            similarAdapter = new ProductAdapter(ProductDetailsActivity.this, similarDataList);
                            productDetailsSimilarRecyclerView.setHasFixedSize(true);
                            productDetailsSimilarRecyclerView.setAdapter(similarAdapter);
                            productDetailsSimilarRecyclerView.setItemAnimator(new DefaultItemAnimator());
                        }
                    }
                });
    }

    // get explore more products
    private void getExploreMoreProducts(String productCategory, String productCompany) {
        productDetailsExploreMoreRecyclerView.setLayoutManager(new LinearLayoutManager(ProductDetailsActivity.this));
        exploreMoreDataList.clear();

        db.collection("Products")
                .whereEqualTo("category", productCategory)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            return;
                        }

                        if (value != null && !value.isEmpty()) {
                            for (QueryDocumentSnapshot documentSnapshot : value) {
                                ProductModel productModel = documentSnapshot.toObject(ProductModel.class);

                                // Skip products of the given company
                                if (!productModel.getCompany().equalsIgnoreCase(productCompany)) {
                                    exploreMoreDataList.add(productModel);
                                }
                            }

                            // Set up RecyclerView after filtering products
                            GridLayoutManager layoutManager = new GridLayoutManager(ProductDetailsActivity.this, 2);
                            productDetailsExploreMoreRecyclerView.setLayoutManager(layoutManager);
                            exploreMoreAdapter = new CategoryProductAdapter(ProductDetailsActivity.this, exploreMoreDataList);
                            productDetailsExploreMoreRecyclerView.setHasFixedSize(true);
                            productDetailsExploreMoreRecyclerView.setAdapter(exploreMoreAdapter);
                            productDetailsExploreMoreRecyclerView.setItemAnimator(new DefaultItemAnimator());
                        }
                    }
                });
    }


    @Override
    protected void onPause() {
        super.onPause();
        sliderHandler.removeCallbacks(sliderRunnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sliderRunnable != null) {
            sliderHandler.postDelayed(sliderRunnable, 3000);
        }
    }

}