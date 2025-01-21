package com.example.swiftmart.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.swiftmart.Model.ProductModel;
import com.example.swiftmart.ProductDetailsActivity;
import com.example.swiftmart.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.WishlistViewHolder> {
    Context context;
    ArrayList<ProductModel> datalist;

    public WishlistAdapter(Context context, ArrayList<ProductModel> datalist) {
        this.context = context;
        this.datalist = datalist;
    }

    @NonNull
    @Override
    public WishlistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_product_wishlist, parent, false);
        return new WishlistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WishlistViewHolder holder, int position) {
        ProductModel product = datalist.get(position);

        Glide.with(holder.cardWishlistProductImage.getContext())
                .load(product.getImgurls().get(0))
                .placeholder(R.drawable.img_animation)
                .into(holder.cardWishlistProductImage);
        holder.cardWishlistProductName.setText(product.getName());

        String fullDescription = product.getDescription();
        String shortenedDescription = fullDescription.length() > 20 ? fullDescription.substring(0, 23) + "..." : fullDescription;
        holder.cardWishlistProductDescription.setText(shortenedDescription);

        // Format the price
        double unitPrice = Double.parseDouble(product.getPrice());
        NumberFormat currencyFormat = NumberFormat.getNumberInstance(Locale.getDefault());
        holder.cardWishlistProductPrice.setText(currencyFormat.format(unitPrice));

        // Set slide-in animation
        Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.fade_in);
        holder.itemView.startAnimation(animation);

        holder.cardWishlistProductLinearLayout.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductDetailsActivity.class);
            intent.putExtra("productId", product.getPid());
            context.startActivity(intent);
        });

        holder.cardWishlistProductImage.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductDetailsActivity.class);
            intent.putExtra("productId", product.getPid());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return datalist.size();
    }

    public class WishlistViewHolder extends RecyclerView.ViewHolder{
        ImageView cardWishlistProductImage;
        TextView cardWishlistProductName, cardWishlistProductDescription, cardWishlistProductPrice, cardMaxPrice;
        LinearLayout cardWishlistProductLinearLayout;

        public WishlistViewHolder(@NonNull View itemView) {
            super(itemView);

            cardWishlistProductLinearLayout = itemView.findViewById(R.id.cardWishlistProductLinearLayout);
            cardWishlistProductImage = itemView.findViewById(R.id.cardWishlistProductImage);
            cardWishlistProductName = itemView.findViewById(R.id.cardWishlistProductName);
            cardWishlistProductDescription = itemView.findViewById(R.id.cardWishlistProductDescription);
            cardWishlistProductPrice = itemView.findViewById(R.id.cardWishlistProductPrice);

        }
    }

}
