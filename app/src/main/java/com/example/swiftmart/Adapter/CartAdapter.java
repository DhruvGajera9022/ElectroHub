package com.example.swiftmart.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.swiftmart.Model.CartModel;
import com.example.swiftmart.Model.ProductModel;
import com.example.swiftmart.R;
import com.example.swiftmart.Utils.CustomToast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private Context context;
    private ArrayList<CartModel> datalist;
    private int maxQuantity = 5;
    private int minQuantity = 1;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String uid;

    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(String data, boolean isPlus, int position);
        void onItemDeleted(String oid, double price, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public CartAdapter(Context context, ArrayList<CartModel> datalist) {
        this.context = context;
        this.datalist = (datalist != null) ? datalist : new ArrayList<>();
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_cart_product, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        if (datalist != null && !datalist.isEmpty()) {
            CartModel product = datalist.get(position);

            if (product.getImgurls() != null && !product.getImgurls().isEmpty()) {
                Glide.with(holder.cartImage.getContext())
                        .load(product.getImgurls().get(0))
                        .apply(new RequestOptions().placeholder(R.drawable.img_animation))
                        .into(holder.cartImage);
            }

            holder.cartProductName.setText(product.getName());

            // Format the price
            double unitPrice = Double.parseDouble(product.getPrice());
            NumberFormat currencyFormat = NumberFormat.getNumberInstance(Locale.getDefault());

            // Set initial price and quantity
            double totalPrice = unitPrice * Integer.parseInt(product.getQty());
            holder.cartProductPrice.setText(currencyFormat.format(totalPrice));
            holder.cartProductQuantity.setText(String.valueOf(product.getQty()));

            // Handle quantity plus
            holder.cartPlusButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int currentQuantity = Integer.parseInt(product.getQty());
                    if (currentQuantity < maxQuantity) {
                        int newQuantity = currentQuantity + 1;
                        product.setQty(String.valueOf(newQuantity));

                        // Update quantity display
                        holder.cartProductQuantity.setText(String.valueOf(newQuantity));

                        // Recalculate the total price for this item
                        double totalPrice = unitPrice * newQuantity;
                        holder.cartProductPrice.setText(currencyFormat.format(totalPrice));

                        updateQTY(product.getOid(), String.valueOf(newQuantity), holder.getAdapterPosition());

                        if (listener != null) {
                            listener.onItemClick(String.valueOf(totalPrice), true, holder.getAdapterPosition());
                        }
                    } else {
                        CustomToast.showToast(context, "Maximum quantity is 10");
                    }
                }
            });

            // Handle quantity minus
            holder.cartMinusButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int currentQuantity = Integer.parseInt(product.getQty());
                    if (currentQuantity > minQuantity) {
                        int newQuantity = currentQuantity - 1;
                        product.setQty(String.valueOf(newQuantity));

                        // Update quantity display
                        holder.cartProductQuantity.setText(String.valueOf(newQuantity));

                        double totalPrice = unitPrice * newQuantity;
                        holder.cartProductPrice.setText(currencyFormat.format(totalPrice));

                        updateQTY(product.getOid(), String.valueOf(newQuantity), holder.getAdapterPosition());

                        if (listener != null) {
                            listener.onItemClick(String.valueOf(totalPrice), false, holder.getAdapterPosition());
                        }
                    } else {
                        CustomToast.showToast(context, "Minimum quantity is 1");
                    }
                }
            });

            // handle product delete from cart
            holder.cartTrashButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    db = FirebaseFirestore.getInstance();
                    mAuth = FirebaseAuth.getInstance();
                    uid = mAuth.getCurrentUser().getUid();

                    String oid = product.getOid();
                    // Calculate total price of the item being removed
                    double itemTotal = Double.parseDouble(product.getPrice()) * Integer.parseInt(product.getQty());

                    db.collection("Users")
                            .document(uid)
                            .collection("Cart")
                            .document(oid)
                            .get()
                            .addOnSuccessListener(documentSnapshot -> {
                                if (documentSnapshot.exists()) {
                                    documentSnapshot.getReference().delete()
                                            .addOnSuccessListener(aVoid -> {
                                                int itemPosition = holder.getAdapterPosition();
                                                if (itemPosition != RecyclerView.NO_POSITION) {
                                                    if (listener != null) {
                                                        listener.onItemDeleted(oid, itemTotal, itemPosition);
                                                    }
                                                    datalist.remove(itemPosition);
                                                    notifyDataSetChanged();
                                                }
                                            });
                                } else {
                                    Log.d("Firestore", "Document does not exist.");
                                }
                            });
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return datalist.size();
    }

    public void updateQTY(String oid, String qty, int position) {
        if (db == null) {
            db = FirebaseFirestore.getInstance();
        }
        if (mAuth == null) {
            mAuth = FirebaseAuth.getInstance();
        }

        uid = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;
        if (uid == null) {
            Log.e("Firestore", "User is not authenticated.");
            return;
        }

        DocumentReference productRef = db.collection("Users")
                .document(uid)
                .collection("Cart")
                .document(oid);

        productRef.update("qty", qty)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "Quantity updated successfully for product ID: " + oid);
                    notifyItemChanged(position); // Notify adapter about the updated item
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error updating quantity", e));
    }



    public class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView cartImage;
        TextView cartProductName, cartProductPrice, cartProductQuantity;
        ImageButton cartPlusButton, cartMinusButton, cartTrashButton;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);

            cartImage = itemView.findViewById(R.id.cartActivityImage);
            cartProductName = itemView.findViewById(R.id.cartProductName);
            cartProductPrice = itemView.findViewById(R.id.cartProductPrice);
            cartProductQuantity = itemView.findViewById(R.id.cartProductQuantity);
            cartPlusButton = itemView.findViewById(R.id.cartPlusButton);
            cartMinusButton = itemView.findViewById(R.id.cartMinusButton);
            cartTrashButton = itemView.findViewById(R.id.cartTrashButton);
        }
    }
}