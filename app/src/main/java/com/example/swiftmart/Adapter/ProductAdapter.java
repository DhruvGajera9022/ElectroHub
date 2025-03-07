package com.example.swiftmart.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.swiftmart.LoginActivity;
import com.example.swiftmart.Model.ProductModel;
import com.example.swiftmart.ProductDetailsActivity;
import com.example.swiftmart.R;
import com.example.swiftmart.Utils.CustomToast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
    Context context;
    ArrayList<ProductModel> datalist;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    String uid = mAuth.getUid();

    public ProductAdapter(Context context, ArrayList<ProductModel> datalist) {
        this.context = context;
        this.datalist = datalist;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_home_product, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("ResourceType")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductModel product = datalist.get(position);

        Log.d("PRODUCT_ID", product.getPid());

        Glide.with(holder.cardProductImage.getContext())
                .load(product.getImgurls().get(0))
                .placeholder(R.raw.loading)
                .into(holder.cardProductImage);
        holder.cardProductName.setText(product.getName());

        // Format the price
        double unitPrice = Double.parseDouble(product.getPrice());
        NumberFormat currencyFormat = NumberFormat.getNumberInstance(Locale.getDefault());
        holder.cardProductPrice.setText(currencyFormat.format(unitPrice));

        // Set slide-in animation
        Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.fade_in);
        holder.itemView.startAnimation(animation);

        holder.wishlistButton.setImageResource(
                product.isWishlisted() ? R.drawable.ic_heart_filled : R.drawable.ic_heart_outline);

        // Get the authenticated user
        FirebaseUser user = mAuth.getCurrentUser();
        String uid = (user != null) ? user.getUid() : null;

        // Set wishlist icon based on Firestore data
        if (uid != null) {
            db.collection("Users")
                    .document(uid)
                    .collection("wishlist")
                    .whereEqualTo("pid", product.getPid())
                    .addSnapshotListener((value, error) -> {
                        if (value != null && !value.isEmpty()) {
                            product.setWishlisted(true);
                            holder.wishlistButton.setImageResource(R.drawable.ic_heart_filled);
                        } else {
                            product.setWishlisted(false);
                            holder.wishlistButton.setImageResource(R.drawable.ic_heart_outline);
                        }
                    });
        } else {
            holder.wishlistButton.setImageResource(R.drawable.ic_heart_outline);
        }

        // Handle product item click
        holder.cardProductLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProductDetailsActivity.class);
                intent.putExtra("productId", product.getPid());
                Log.d("PRODUCT_ID_1", product.getPid());
                context.startActivity(intent);
            }
        });

        holder.cardProductImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProductDetailsActivity.class);
                intent.putExtra("productId", product.getPid());
                Log.d("PRODUCT_ID_1", product.getPid());
                context.startActivity(intent);
            }
        });

        // Handle wishlist button clicks (removed the duplicate implementation)
        holder.wishlistButton.setOnClickListener(v -> {
            // Check if user is logged in
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser == null) {
                context.startActivity(new Intent(context, LoginActivity.class));
                return;
            }

            if (product.getPid() == null || product.getPid().isEmpty()) {
                Log.e("ProductAdapter", "Error: Product ID is null or empty");
                CustomToast.showToast(context, "Unable to add to wishlist. Please try again.");
                return;
            }

            boolean isWishlisted = !product.isWishlisted();
            product.setWishlisted(isWishlisted);

            // Update UI
            holder.wishlistButton.setImageResource(
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
        });
    }

    @Override
    public int getItemCount() {
        return datalist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView cardProductImage, wishlistButton;
        TextView cardProductName, cardProductPrice;
        LinearLayout cardProductLinearLayout;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cardProductLinearLayout = itemView.findViewById(R.id.cardProductLinearLayout);
            cardProductImage = itemView.findViewById(R.id.cardProductImage);
            cardProductName = itemView.findViewById(R.id.cardProductName);
            cardProductPrice = itemView.findViewById(R.id.cardProductPrice);
            wishlistButton = itemView.findViewById(R.id.wishlistButton);
        }
    }
}