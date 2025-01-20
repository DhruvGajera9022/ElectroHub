package com.example.swiftmart;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.swiftmart.Model.InvoiceModel;
import com.example.swiftmart.Utils.CustomToast;
import com.example.swiftmart.Utils.InvoiceGenerator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class OrderTrackingActivity extends AppCompatActivity {
    private ImageView backBtn, productImage;
    private TextView toolBarTitle, canceledText, canceledTime;
    private TextView trackOrderName, trackOrderCompany, trackOrderQty, productDetailsProductPrice, trackOrderID;
    private TextView addressFullName, addressText, addressState, addressNumber;
    private ScrollView trackOrderScrollView;
    private AppCompatRatingBar trackOrderRating;
    private CardView invoiceCardView;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String uid, orderID, addressID, productID, userEmail, orderDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_tracking);

        initialization();
        getOrderProductDetails();
        handleRating();
        getUserData();

    }

    private void initialization(){
        backBtn = findViewById(R.id.backBtn);
        toolBarTitle = findViewById(R.id.toolBarTitle);

        productImage = findViewById(R.id.productImage);
        trackOrderName = findViewById(R.id.trackOrderName);
        trackOrderCompany = findViewById(R.id.trackOrderCompany);
        trackOrderQty = findViewById(R.id.trackOrderQty);
        productDetailsProductPrice = findViewById(R.id.productDetailsProductPrice);
        trackOrderID = findViewById(R.id.trackOrderID);
        trackOrderRating = findViewById(R.id.trackOrderRating);

        canceledText = findViewById(R.id.canceledText);
        canceledTime = findViewById(R.id.canceledTime);

        invoiceCardView = findViewById(R.id.invoiceCardView);

        trackOrderScrollView = findViewById(R.id.trackOrderScrollView);

        addressFullName = findViewById(R.id.addressFullName);
        addressText = findViewById(R.id.addressText);
        addressState = findViewById(R.id.addressState);
        addressNumber = findViewById(R.id.addressNumber);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();
        orderID = getIntent().getStringExtra("orderID");

        trackOrderScrollView.setVerticalScrollBarEnabled(false);

        toolBarTitle.setText("Order Details");
        backBtn.setOnClickListener(v -> onBackPressed());
    }

    private void getOrderProductDetails(){
        DocumentReference reference = db.collection("Orders").document(orderID);

        reference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null && value.exists()){
                    trackOrderName.setText(value.getString("name"));
                    trackOrderCompany.setText(value.getString("company"));
                    trackOrderQty.setText(value.getString("quantity"));
                    productDetailsProductPrice.setText(value.getString("totalAmount"));
                    trackOrderID.setText(value.getString("oid"));
                    addressID = value.getString("aid");
                    productID = value.getString("pid");
                    orderDate = value.getString("orderDate");

                    getRatingData(productID);
                    getOrderAddressDetails(addressID);

                    ArrayList<String> imgUrls = (ArrayList<String>) value.get("imgurls");
                    if (imgUrls != null && !imgUrls.isEmpty()) {
                        Picasso.get().load(imgUrls.get(0)).into(productImage);
                    }

                    if ("Canceled".equals(value.getString("status"))) {
                        canceledText.setVisibility(View.VISIBLE);
                        canceledTime.setVisibility(View.VISIBLE);
                        canceledTime.setText(value.getString("orderDate"));
                    }

                    if ("Shipped".equals(value.getString("status"))){
                        invoiceCardView.setVisibility(View.VISIBLE);
                        invoiceCardView.setOnClickListener(v -> generateInvoice());
                    }

                }
            }
        });
    }

    private void getOrderAddressDetails(String addressID){

        if (addressID == null || addressID.isEmpty()) {
            return;
        }
        DocumentReference reference = db.collection("Users").document(uid).collection("Addresses").document(addressID);

        reference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null && value.exists()){
                    addressFullName.setText(value.getString("fullName"));
                    addressText.setText(value.getString("houseNo") + ", " + value.getString("roadName") + ", ");
                    addressState.setText(value.getString("city") + ", " + value.getString("state") + " - " + value.getString("pinCode"));
                    addressNumber.setText(value.getString("phoneNumber"));
                }
            }
        });
    }

    private void getRatingData(String productID){
        if (productID == null || productID.isEmpty()) {
            return;
        }

        if (uid == null || uid.isEmpty()) {
            return;
        }

        db.collection("Ratings")
                .whereEqualTo("pid", productID)
                .whereEqualTo("uid", uid)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            DocumentSnapshot document = task.getResult().getDocuments().get(0);
                            String strRating = document.getString("rating");

                            try {
                                float ratingValue = Float.parseFloat(strRating);
                                trackOrderRating.setRating(ratingValue);
                            } catch (NumberFormatException e) {
                                Log.e("OrderTracking", "Invalid rating format", e);
                            }
                        } else {
                            Log.d("OrderTracking", "No rating found for this product and user.");
                        }
                    } else {
                        Log.e("OrderTracking", "Error fetching rating data", task.getException());
                    }
                });


    }

    private void getUserData(){
        DocumentReference reference = db.collection("Users").document(uid);

        reference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null && value.exists()){
                    userEmail = value.getString("Email");
                }
            }
        });
    }

    private void handleRating() {
        trackOrderRating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                updateRating(rating);
            }
        });
    }

    private void updateRating(float rating) {
        if (productID == null || productID.isEmpty()) {
            return;
        }

        db.collection("Ratings")
                .whereEqualTo("pid", productID)
                .whereEqualTo("uid", uid)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            String existingRatingId = task.getResult().getDocuments().get(0).getId();
                            db.collection("Ratings").document(existingRatingId)
                                    .update(
                                            "rating", String.valueOf(rating),
                                            "timestamp", FieldValue.serverTimestamp()
                                    )
                                    .addOnSuccessListener(aVoid ->
                                            Log.d("OrderTracking", "Rating updated successfully"))
                                    .addOnFailureListener(e ->
                                            Log.e("OrderTracking", "Error updating rating", e));
                        } else {
                            String rid = db.collection("Ratings").document().getId();
                            Map<String, Object> ratingData = new HashMap<>();
                            ratingData.put("rid", rid);
                            ratingData.put("uid", uid);
                            ratingData.put("pid", productID);
                            ratingData.put("rating", String.valueOf(rating));
                            ratingData.put("timestamp", FieldValue.serverTimestamp());

                            db.collection("Ratings")
                                    .document(rid)
                                    .set(ratingData)
                                    .addOnSuccessListener(aVoid ->
                                            Log.d("OrderTracking", "Rating added successfully with ID: " + rid))
                                    .addOnFailureListener(e ->
                                            Log.e("OrderTracking", "Error adding rating", e));
                        }
                    } else {
                        Log.e("OrderTracking", "Error querying existing rating", task.getException());
                    }
                });
    }

    private void generateInvoice(){
        InvoiceModel invoice = new InvoiceModel(
                addressFullName.getText().toString(),
                trackOrderCompany.getText().toString(),
                addressFullName.getText().toString(),
                addressNumber.getText().toString(),
                userEmail,
                generateRandomString(),
                (new Date()).toString(),
                8,
                "Bank Transfer",
                orderDate,
                "1234-5678-9012-3456",
                "Thanks for shopping from our e-store.");

        // Adding services
        List<InvoiceModel.Service> services = List.of(
                new InvoiceModel.Service(trackOrderName.getText().toString(), Integer.parseInt(trackOrderQty.getText().toString()), Double.parseDouble(productDetailsProductPrice.getText().toString()))
        );

        invoice.setServices(services);

        // Generate Invoice
        InvoiceGenerator generator = new InvoiceGenerator();

        try {
            generator.generateInvoice(invoice, "invoice_dhruv.pdf");
            CustomToast.showToast(this, "Invoice generated successfully!");
        } catch (Exception e) {
            CustomToast.showToast(this, "Failed to generate invoice");
        }
    }

    public static String generateRandomString() {
        String prefix = "SI";

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        String year = sdf.format(new Date());

        Random random = new Random();
        int randomNumber = random.nextInt(1000);
        String formattedRandomNumber = String.format("%03d", randomNumber);
        return prefix + year + "-" + formattedRandomNumber;
    }

}