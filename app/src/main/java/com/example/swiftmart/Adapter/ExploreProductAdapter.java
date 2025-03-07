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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.swiftmart.LoginActivity;
import com.example.swiftmart.Model.ProductModel;
import com.example.swiftmart.ProductDetailsActivity;
import com.example.swiftmart.R;
import com.example.swiftmart.Utils.CustomToast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ExploreProductAdapter extends RecyclerView.Adapter<ExploreProductAdapter.ViewHolder> {
    Context context;
    ArrayList<ProductModel> datalist;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public ExploreProductAdapter(Context context, ArrayList<ProductModel> datalist) {
        this.context = context;
        this.datalist = datalist;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_all_product, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("ResourceType")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductModel product = datalist.get(position);

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


        holder.cardProductLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProductDetailsActivity.class);
                intent.putExtra("productId", product.getPid());
                context.startActivity(intent);
            }
        });

        holder.cardProductImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProductDetailsActivity.class);
                intent.putExtra("productId", product.getPid());
                context.startActivity(intent);
            }
        });

        // Handle wishlist button clicks
        holder.wishlistButton.setOnClickListener(v -> {
            // Check if user is logged in
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser == null) {
                context.startActivity(new Intent(context, LoginActivity.class));
                return;
            }

            if (product.getPid() == null || product.getPid().isEmpty()) {
                Log.e("AllProductAdapter", "Error: Product ID is null or empty");
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

        // Check cart status and update UI accordingly
        if (uid != null) {
            db.collection("Users")
                    .document(uid)
                    .collection("Cart")
                    .whereEqualTo("pid", product.getPid())
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            if (value != null && !value.isEmpty()) {
                                holder.addToCartAllProducts.setVisibility(View.GONE);
                                holder.addToCartDoneAllProducts.setVisibility(View.VISIBLE);
                            } else {
                                holder.addToCartAllProducts.setVisibility(View.VISIBLE);
                                holder.addToCartDoneAllProducts.setVisibility(View.GONE);
                            }
                        }
                    });
        } else {
            // If user is not logged in, just show the "Add to Cart" button
            holder.addToCartAllProducts.setVisibility(View.VISIBLE);
            holder.addToCartDoneAllProducts.setVisibility(View.GONE);
        }

        // Handle add to cart button click
        holder.addToCartAllProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if user is logged in
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser == null) {
                    context.startActivity(new Intent(context, LoginActivity.class));
                    return;
                }

                Calendar calForDate = Calendar.getInstance();
                SimpleDateFormat currentDate = new SimpleDateFormat("MM/dd/yyyy");
                SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
                String saveCurrentDate = currentDate.format(calForDate.getTime());
                String saveCurrentTime = currentTime.format(calForDate.getTime());

                Map<String, Object> cartMap = new HashMap<>();
                cartMap.put("imgurls", product.getImgurls());
                cartMap.put("name", product.getName());
                cartMap.put("price", product.getPrice());
                cartMap.put("category", product.getCategory());
                cartMap.put("company", product.getCategory());
                cartMap.put("description", product.getDescription());
                cartMap.put("currentDate", saveCurrentDate);
                cartMap.put("currentTime", saveCurrentTime);
                cartMap.put("pid", product.getPid());
                cartMap.put("qty", "1");

                db.collection("Users")
                        .document(uid)
                        .collection("Cart")
                        .whereEqualTo("pid", product.getPid())
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                CustomToast.showToast(context, "Item is already in your cart");
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
                                                            // Update UI to show done image
                                                            holder.addToCartAllProducts.setVisibility(View.GONE);
                                                            holder.addToCartDoneAllProducts.setVisibility(View.VISIBLE);
                                                        });
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                CustomToast.showToast(context, "Error adding to cart");
                                            }
                                        });
                            }
                        });
            }
        });

        // Handle remove from cart button click
        holder.addToCartDoneAllProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if user is logged in
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser == null) {
                    context.startActivity(new Intent(context, LoginActivity.class));
                    return;
                }

                db.collection("Users")
                        .document(uid)
                        .collection("Cart")
                        .whereEqualTo("pid", product.getPid())
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                String documentId = task.getResult().getDocuments().get(0).getId();
                                db.collection("Users")
                                        .document(uid)
                                        .collection("Cart")
                                        .document(documentId)
                                        .delete()
                                        .addOnSuccessListener(aVoid -> {
                                            // Update UI
                                            holder.addToCartAllProducts.setVisibility(View.VISIBLE);
                                            holder.addToCartDoneAllProducts.setVisibility(View.GONE);
                                        })
                                        .addOnFailureListener(e -> {
                                            CustomToast.showToast(context, "Error removing from cart");
                                        });
                            }
                        });
            }
        });

    }

    @Override
    public int getItemCount() {
        return datalist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView cardProductImage;
        TextView cardProductName, cardProductPrice;
        LinearLayout cardProductLinearLayout;
        ImageView wishlistButton, addToCartAllProducts, addToCartDoneAllProducts;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cardProductLinearLayout = itemView.findViewById(R.id.cardProductLinearLayout);
            cardProductImage = itemView.findViewById(R.id.cardProductImage);
            cardProductName = itemView.findViewById(R.id.cardProductName);
            cardProductPrice = itemView.findViewById(R.id.cardProductPrice);
            wishlistButton = itemView.findViewById(R.id.wishlistButton);
            addToCartAllProducts = itemView.findViewById(R.id.addToCartAllProducts);
            addToCartDoneAllProducts = itemView.findViewById(R.id.addToCartDoneAllProducts);

        }
    }

}
