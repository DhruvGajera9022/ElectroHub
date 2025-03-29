package com.example.swiftmart.Account;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.swiftmart.Adapter.WishlistAdapter;
import com.example.swiftmart.Model.ProductModel;
import com.example.swiftmart.R;
import com.example.swiftmart.Utils.CustomToast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class WishlistActivity extends AppCompatActivity {
    private ImageView backBtn;
    private TextView toolBarTitle;
    
    private ProgressBar wishlistProgressBar;
    private RecyclerView wishlistRecyclerView;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String uid;
    private WishlistAdapter adapter;
    private ArrayList<ProductModel> datalist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);

        setStatusBarColor(R.color.home);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();

        // Initialize UI components
        backBtn = findViewById(R.id.backBtn);
        toolBarTitle = findViewById(R.id.toolBarTitle);

        wishlistProgressBar = findViewById(R.id.wishlistProgressBar);
        wishlistRecyclerView = findViewById(R.id.wishlistRecyclerView);

        // Initialize datalist and adapter
        datalist = new ArrayList<>();
        adapter = new WishlistAdapter(this, datalist);

        // Set up RecyclerView
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        wishlistRecyclerView.setLayoutManager(layoutManager);
        wishlistRecyclerView.setHasFixedSize(true);
        wishlistRecyclerView.setAdapter(adapter);
        wishlistRecyclerView.setItemAnimator(new DefaultItemAnimator());

        // Back button listener
        toolBarTitle.setText(R.string.wishlist);
        backBtn.setOnClickListener(v -> onBackPressed());

        // Load wishlist data
        getWishlistData();
    }

    private void getWishlistData() {
        wishlistProgressBar.setVisibility(View.VISIBLE);
        datalist.clear();

        db.collection("Users")
                .document(uid)
                .collection("wishlist")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            CustomToast.showToast(WishlistActivity.this,  "Error in data fetching");
                            wishlistProgressBar.setVisibility(View.GONE);
                            return;
                        }

                        if (value != null) {
                            datalist.clear(); // Clear list to avoid duplicates
                            for (QueryDocumentSnapshot documentSnapshot : value) {
                                ProductModel productModel = documentSnapshot.toObject(ProductModel.class);
                                datalist.add(productModel);
                            }
                            adapter.notifyDataSetChanged(); // Notify adapter of data changes
                        }
                        wishlistProgressBar.setVisibility(View.GONE);
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void setStatusBarColor(int colorResource) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(getResources().getColor(colorResource));
        }
    }
}
