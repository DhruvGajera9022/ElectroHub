package com.example.swiftmart;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.swiftmart.Account.Add_Address_Activity;
import com.example.swiftmart.Model.CartModel;
import com.example.swiftmart.Model.ProductModel;
import com.example.swiftmart.Utils.CustomToast;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.razorpay.Checkout;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfirmAddressActivity2 extends AppCompatActivity {

    private TextView confirmAddressAddNew, confirmAddressType, confirmAddressFullName, confirmAddressText, confirmAddressState, confirmAddressNumber, confirmAddressEdit;
    private AppCompatButton confirmAddressDeliver;
    private LinearLayout confirmAddressLinearLayout;
    private TextView noAvailableText;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private String uid, addressID, strTotalAmount;
    private ArrayList<String> productIDs;
    private ArrayList<CartModel> cartProducts;
    private String userName, userPhone;

    private Checkout checkout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_address2);

        initializeViews();
        initializeFirebase();
        loadIntentData();

        fetchDefaultAddress();
        fetchCartProducts();
        fetchUserData();

        setupClickListeners();

        setStatusBarColor(R.color.home);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {

            }
        });

        AdView mAdView;
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    private void initializeViews() {
        confirmAddressAddNew = findViewById(R.id.confirmAddressAddNew);
        confirmAddressType = findViewById(R.id.confirmAddressType);
        confirmAddressFullName = findViewById(R.id.confirmAddressFullName);
        confirmAddressText = findViewById(R.id.confirmAddressText);
        confirmAddressState = findViewById(R.id.confirmAddressState);
        confirmAddressNumber = findViewById(R.id.confirmAddressNumber);
        confirmAddressEdit = findViewById(R.id.confirmAddressEdit);

        confirmAddressDeliver = findViewById(R.id.confirmAddressDeliver);

        confirmAddressLinearLayout = findViewById(R.id.confirmAddressLinearLayout);
        noAvailableText = findViewById(R.id.noAvailableText);

        cartProducts = new ArrayList<>();

        checkout = new Checkout();
        checkout.setImage(R.drawable.app_logo);
    }

    private void initializeFirebase() {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();
    }

    private void loadIntentData() {
        productIDs = getIntent().getStringArrayListExtra("productIDs");
        strTotalAmount = getIntent().getStringExtra("totalAmount");
    }

    private void fetchDefaultAddress() {
        db.collection("Users").document(uid).collection("Addresses")
                .whereEqualTo("isDefault", true)
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null) {
                        Log.e("Address", "Error fetching default address", error);
                        return;
                    }

                    if (snapshots != null && !snapshots.isEmpty()) {
                        for (DocumentSnapshot document : snapshots) {
                            confirmAddressType.setText(document.getString("addressType"));
                            confirmAddressFullName.setText(document.getString("fullName"));
                            confirmAddressText.setText(document.getString("houseNo") + ", " + document.getString("roadName") + ", ");
                            confirmAddressState.setText(document.getString("city") + ", " + document.getString("state") + " - " + document.getString("pinCode"));
                            confirmAddressNumber.setText(document.getString("phoneNumber"));
                            addressID = document.getString("aid");
                        }
                        noAvailableText.setVisibility(View.GONE);
                        confirmAddressLinearLayout.setVisibility(View.VISIBLE);
                        confirmAddressDeliver.setEnabled(true);
                        confirmAddressDeliver.setAlpha(1.0f);
                    } else {
                        noAvailableText.setVisibility(View.VISIBLE);
                        confirmAddressLinearLayout.setVisibility(View.GONE);
                        confirmAddressDeliver.setEnabled(false);
                        confirmAddressDeliver.setAlpha(0.5f);
                    }
                });
    }

    private void fetchCartProducts() {
        db.collection("Users").document(uid).collection("Cart")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        cartProducts = new ArrayList<>(task.getResult().toObjects(CartModel.class));
                    } else {
                        Log.e("Cart", "Error fetching cart products", task.getException());
                    }
                });
    }

    private void fetchUserData() {
        db.collection("Users").document(uid).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                userName = documentSnapshot.getString("Username");
                userPhone = documentSnapshot.getString("Number");
            }
        });
    }

    private void setupClickListeners() {
        confirmAddressAddNew.setOnClickListener(v -> startActivity(new Intent(this, Add_Address_Activity.class)));

        confirmAddressEdit.setOnClickListener(v -> {
            Intent intent = new Intent(this, Add_Address_Activity.class);
            intent.putExtra("addressID", addressID);
            startActivity(intent);
        });

        confirmAddressDeliver.setOnClickListener(v -> initiatePayment());
    }

    private void initiatePayment() {
        try {
            JSONObject options = new JSONObject();
            options.put("name", getString(R.string.app_name));
            options.put("description", "Best E-Commerce app");
            options.put("currency", "INR");

            strTotalAmount = strTotalAmount.replace(",", "");
            options.put("amount", Double.parseDouble(strTotalAmount) * 100);

            JSONObject preFill = new JSONObject();
            preFill.put("email", userName);
            preFill.put("contact", userPhone);

            options.put("prefill", preFill);

            checkout.open(this, options);
        } catch (Exception e) {
            Log.e("Payment", "Error initiating payment", e);
        }
    }

    public void onPaymentSuccess(String razorpayPaymentID) {
        updateProductQuantities();
        createOrders(razorpayPaymentID);
        clearCart();
        finish();
    }

    private void updateProductQuantities() {
        for (CartModel product : cartProducts) {
            String productId = product.getPid();
            int purchasedQty = Integer.parseInt(product.getQty());

            DocumentReference productRef = db.collection("Products").document(productId);
            productRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    long currentStock = Long.parseLong(documentSnapshot.getString("quantity"));
                    if (currentStock >= purchasedQty) {
                        productRef.update("qty", String.valueOf(currentStock - purchasedQty))
                                .addOnSuccessListener(aVoid -> Log.d("Product", "Stock updated for " + productId))
                                .addOnFailureListener(e -> Log.e("Product", "Error updating stock", e));
                    } else {
                        CustomToast.showToast(this, "Product out of stock: " + product.getName());
                    }
                }
            });
        }
    }

    private void createOrders(String paymentID) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss a");

        String orderDate = dateFormat.format(calendar.getTime());
        String orderTime = timeFormat.format(calendar.getTime());

        for (CartModel product : cartProducts) {
            String oid = db.collection("Orders").document().getId();
            double totalAmount = Double.parseDouble(product.getPrice()) * Integer.parseInt(product.getQty());

            Map<String, Object> orderMap = new HashMap<>();
            orderMap.put("uid", uid);
            orderMap.put("pid", product.getPid());
            orderMap.put("name", product.getName());
            orderMap.put("price", product.getPrice());
            orderMap.put("description", product.getDescription());
            orderMap.put("category", product.getCategory());
            orderMap.put("company", product.getCompany());
            orderMap.put("paymentID", paymentID);
            orderMap.put("oid", oid);
            orderMap.put("aid", addressID);
            orderMap.put("quantity", product.getQty());
            orderMap.put("imgurls", product.getImgurls());
            orderMap.put("orderDate", orderDate);
            orderMap.put("orderTime", orderTime);
            orderMap.put("shippingDate", "");
            orderMap.put("shippedDate", "");
            orderMap.put("canceledDate", "");
            orderMap.put("totalAmount", String.valueOf(totalAmount));
            orderMap.put("status", "Pending");

            db.collection("Orders").document(oid).set(orderMap)
                    .addOnSuccessListener(aVoid -> Log.d("Order", "Order created: " + product.getName()))
                    .addOnFailureListener(e -> Log.e("Order", "Error creating order", e));
        }
    }

    private void clearCart() {
        for (CartModel cartModel : cartProducts){
            db.collection("Users")
                    .document(uid)
                    .collection("Cart")
                    .document(cartModel.getOid())
                    .delete();
        }
    }

    private void setStatusBarColor(int colorResource) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(getResources().getColor(colorResource));
        }
    }
}
