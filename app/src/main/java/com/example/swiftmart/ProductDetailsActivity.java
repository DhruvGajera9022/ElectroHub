package com.example.swiftmart;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.viewpager2.widget.ViewPager2;

import com.example.swiftmart.Adapter.ProductImageSliderAdapter;
import com.example.swiftmart.Model.ProductModel;
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
import com.google.firebase.firestore.QuerySnapshot;
import com.razorpay.Checkout;

import org.json.JSONObject;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ProductDetailsActivity extends AppCompatActivity {
    private TextView productDetailsProductName, productDetailsProductDescription, productDetailsProductPrice, expandDescriptionButton;
    private String productId, productCategory, productCompany;
    private ViewPager2 productDetailsViewPager;
    private AppCompatButton productBuyNowButton;
    private LinearLayout productAddToCartButton;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String uid;
    private List<String> currentImageUrls;

    private Handler sliderHandler = new Handler(Looper.getMainLooper());
    private Runnable sliderRunnable;

    private RelativeLayout productDetailsRelativeLayout;

    private ImageView productDetailsBackArrow, productDetailsWishlist, productDetailsShare;

    private String userName, userPhone;



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

    }

    private void initialization(){

        productDetailsRelativeLayout = findViewById(R.id.productDetailsRelativeLayout);

        productDetailsProductName = findViewById(R.id.productDetailsProductName);
        productDetailsProductDescription = findViewById(R.id.productDetailsProductDescription);
        productDetailsProductPrice = findViewById(R.id.productDetailsProductPrice);
        productDetailsViewPager = findViewById(R.id.productDetailsViewPager);

        productAddToCartButton = findViewById(R.id.productAddToCartButton);
        productBuyNowButton = findViewById(R.id.productBuyNowButton);

        productDetailsBackArrow = findViewById(R.id.productDetailsBackArrow);
        productDetailsWishlist = findViewById(R.id.productDetailsWishlist);
        productDetailsShare = findViewById(R.id.productDetailsShare);

        expandDescriptionButton = findViewById(R.id.expandDescriptionButton);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();

        productId = getIntent().getStringExtra("productId");

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

    private void handleAddToCartClick(){
        productAddToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToCart();
            }
        });
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
        cartMap.put("qty", 1);

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

}