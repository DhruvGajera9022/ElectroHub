package com.example.swiftmart.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.swiftmart.Model.OrderModel;
import com.example.swiftmart.OrderTrackingActivity;
import com.example.swiftmart.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class OrderProductAdapter extends RecyclerView.Adapter<OrderProductAdapter.ViewHolder> {

    Context context;
    ArrayList<OrderModel> datalist;

    public OrderProductAdapter(Context context, ArrayList<OrderModel> datalist) {
        this.context = context;
        this.datalist = datalist;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_order_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderModel orderModel = datalist.get(position);

        Glide.with(context)
                .load(orderModel.getImgurls().get(0))
                .placeholder(R.drawable.img_animation)
                .into(holder.productImage);

        holder.productName.setText(orderModel.getName());
        holder.productCompany.setText(orderModel.getCompany());

        double unitPrice = Double.parseDouble(orderModel.getPrice());
        NumberFormat currencyFormat = NumberFormat.getNumberInstance(Locale.getDefault());
        holder.productPrice.setText("₹" + currencyFormat.format(unitPrice));

        holder.trackOrderButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, OrderTrackingActivity.class);
            intent.putExtra("orderID", orderModel.getOid());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return datalist.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView productImage;
        TextView productName, productCompany, productPrice;
        AppCompatButton trackOrderButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            productImage = itemView.findViewById(R.id.productImage);
            productName = itemView.findViewById(R.id.productName);
            productCompany = itemView.findViewById(R.id.productCompany);
            productPrice = itemView.findViewById(R.id.productPrice);
            trackOrderButton = itemView.findViewById(R.id.trackOrderButton);
        }
    }
}