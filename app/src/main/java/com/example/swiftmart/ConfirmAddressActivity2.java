package com.example.swiftmart;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.swiftmart.Account.Add_Address_Activity;
import com.example.swiftmart.Model.ProductModel;
import com.example.swiftmart.Utils.CustomToast;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
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

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String uid, addressID, strTotalAmount;
    private ArrayList<String> productIDs;

    private String productName, productPrice, productDescription, productCompany, productCategory;
    private Double totalAmount;
    private String userName, userPhone;
    private List<String> currentImageUrls;

    private Checkout checkout;

    private TextView noAvailableText;
    private LinearLayout confirmAddressLinearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_address2);

        initialization();
        getAddress();
        handleNewClick();
        handleEditClick();
        handleDeliverClick();

        getProductData();
        getUserData();
        
    }


    private void initialization(){
        confirmAddressAddNew = findViewById(R.id.confirmAddressAddNew);
        confirmAddressType = findViewById(R.id.confirmAddressType);
        confirmAddressFullName = findViewById(R.id.confirmAddressFullName);
        confirmAddressText = findViewById(R.id.confirmAddressText);
        confirmAddressState = findViewById(R.id.confirmAddressState);
        confirmAddressNumber = findViewById(R.id.confirmAddressNumber);
        confirmAddressEdit = findViewById(R.id.confirmAddressEdit);

        confirmAddressDeliver = findViewById(R.id.confirmAddressDeliver);

        noAvailableText = findViewById(R.id.noAvailableText);
        confirmAddressLinearLayout = findViewById(R.id.confirmAddressLinearLayout);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();

        productIDs = getIntent().getStringArrayListExtra("productIDs");
        strTotalAmount = getIntent().getStringExtra("totalAmount");

    }

    private void getAddress() {
        db.collection("Users").document(uid).collection("Addresses")
                .whereEqualTo("isDefault", true)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            return;
                        }

                        if (snapshots != null && !snapshots.isEmpty()) {
                            for (QueryDocumentSnapshot document : snapshots) {
                                confirmAddressType.setText(document.getString("addressType"));
                                confirmAddressFullName.setText(document.getString("fullName"));
                                confirmAddressText.setText(document.getString("houseNo") + ", " + document.getString("roadName") + ", ");
                                confirmAddressState.setText(document.getString("city") + ", " + document.getString("state") + " - " + document.getString("pinCode"));
                                confirmAddressNumber.setText(document.getString("phoneNumber"));
                                addressID = document.getString("aid");
                            }
                            noAvailableText.setVisibility(View.GONE);
                            confirmAddressLinearLayout.setVisibility(View.VISIBLE);
                        }
                        else {
                            noAvailableText.setVisibility(View.VISIBLE);
                            confirmAddressLinearLayout.setVisibility(View.GONE);

                            boolean shouldDisableButton = true;

                            if (shouldDisableButton) {
                                confirmAddressDeliver.setEnabled(false);
                                confirmAddressDeliver.setAlpha(0.5f);
                            } else {
                                confirmAddressDeliver.setEnabled(true);
                                confirmAddressDeliver.setAlpha(1.0f);
                            }
                        }
                    }
                });
    }

    private void handleNewClick(){
        confirmAddressAddNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConfirmAddressActivity2.this, Add_Address_Activity.class);
                startActivity(intent);
            }
        });
    }

    private void handleEditClick(){
        confirmAddressEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConfirmAddressActivity2.this, Add_Address_Activity.class);
                intent.putExtra("addressID", addressID);
                startActivity(intent);
            }
        });
    }

    private void handleDeliverClick(){
        confirmAddressDeliver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handlePayment();
            }
        });
    }

    private void getProductData() {
        for (String productId : productIDs) {
            db.collection("Products").document(productId)
                    .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                            if (error != null) {
                                CustomToast.showToast(ConfirmAddressActivity2.this, "Error in fetching details");
                                return;
                            }

                            if (value != null && value.exists()) {
                                ProductModel product = value.toObject(ProductModel.class);
                                if (product != null) {
                                    productName = product.getName();
                                    productPrice = product.getPrice();
                                    productDescription = product.getDescription();
                                    productCompany = product.getCompany();
                                    productCategory = product.getCategory();
                                    currentImageUrls = product.getImgurls();
                                }
                            }
                        }
                    });
        }
    }

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

    // Handle payment
    private void handlePayment() {
        checkout = new Checkout();
        checkout.setImage(R.drawable.app_logo);

        final Activity activity = this;

        try {
            JSONObject options = new JSONObject();
            options.put("name", getString(R.string.app_name));
            options.put("description", "Best E-Commerce app");
            options.put("send_sms_hash", false);
            options.put("allow_rotation", false);
            options.put("currency", "INR");

            double totalPrice = 0.0;
            for (String productId : productIDs) {
                totalPrice += Double.parseDouble(productPrice);
            }

            options.put("amount", totalPrice * 100);

            JSONObject preFill = new JSONObject();
            preFill.put("email", userName);
            preFill.put("contact", userPhone);

            options.put("prefill", preFill);

            checkout.open(activity, options);
        } catch (Exception exception) {
            Log.e("Payment", "Error in payment: ", exception);
        }
    }

    public void onPaymentSuccess(String razorpayPaymentID) {
        updateQuantity();
        createNewOrder(razorpayPaymentID);
        clearCart();
    }

    void updateQuantity() {
        for (String productId : productIDs) {

            DocumentReference productRef = db.collection("Products").document(productId);

            productRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        Object quantityObj = documentSnapshot.get("quantity");

                        long currentQuantity = 0;
                        if (quantityObj instanceof Number) {
                            currentQuantity = ((Number) quantityObj).longValue();
                        } else if (quantityObj instanceof String) {
                            try {
                                currentQuantity = Long.parseLong((String) quantityObj);
                            } catch (NumberFormatException e) {
                                Log.e("Payment", "Invalid quantity format", e);
                                return;
                            }
                        }

                        if (currentQuantity > 0) {
                            productRef.update("quantity", String.valueOf(currentQuantity - 1))
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d("Payment", "Product quantity updated successfully for product: " + productId);
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("Payment", "Error updating product quantity for product: " + productId, e);
                                    });
                        } else {
                            CustomToast.showToast(ConfirmAddressActivity2.this, "Product out of stock");
                        }
                    } else {
                        Log.e("Payment", "Product not found for productId: " + productId);
                    }
                }
            });
        }
    }


    private void createNewOrder(String paymentID) {
        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MM/dd/yyyy");
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        String saveCurrentDate = currentDate.format(calForDate.getTime());
        String saveCurrentTime = currentTime.format(calForDate.getTime());

        for (String productId : productIDs) {
            db.collection("Products").document(productId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String productName = documentSnapshot.getString("name");
                            String productPrice = documentSnapshot.getString("price");
                            String productDescription = documentSnapshot.getString("description");
                            String productCategory = documentSnapshot.getString("category");
                            String productCompany = documentSnapshot.getString("company");
                            List<String> imageUrls = (List<String>) documentSnapshot.get("imgurls");

                            String oid = db.collection("Orders").document().getId();
                            double totalAmount = Double.parseDouble(productPrice);

                            Map<String, Object> orderMap = new HashMap<>();
                            orderMap.put("uid", uid);
                            orderMap.put("pid", productId);
                            orderMap.put("name", productName);
                            orderMap.put("price", productPrice);
                            orderMap.put("description", productDescription);
                            orderMap.put("category", productCategory);
                            orderMap.put("company", productCompany);
                            orderMap.put("paymentID", paymentID);
                            orderMap.put("oid", oid);
                            orderMap.put("aid", addressID);
                            orderMap.put("quantity", "1");
                            orderMap.put("imgurls", imageUrls);
                            orderMap.put("orderDate", saveCurrentDate);
                            orderMap.put("orderTime", saveCurrentTime);
                            orderMap.put("totalAmount", String.valueOf(totalAmount));
                            orderMap.put("status", "Pending");

                            db.collection("Orders")
                                    .document(oid)
                                    .set(orderMap)
                                    .addOnSuccessListener(aVoid -> Log.d("Order", "Order created for product: " + productName))
                                    .addOnFailureListener(e -> Log.e("Order", "Error creating order", e));
                        }
                    })
                    .addOnFailureListener(e -> Log.e("Order", "Error fetching product details", e));
        }
    }

    private void clearCart() {
        db.collection("Users")
                .document(uid)
                .collection("Cart")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        document.getReference().delete()
                                .addOnSuccessListener(aVoid -> Log.d("Cart", "Item removed from cart: " + document.getId()))
                                .addOnFailureListener(e -> Log.e("Cart", "Error removing item from cart: " + document.getId(), e));
                    }
                    Log.d("Cart", "Cart cleared successfully");
                })
                .addOnFailureListener(e -> Log.e("Cart", "Error fetching cart items", e));
    }

}