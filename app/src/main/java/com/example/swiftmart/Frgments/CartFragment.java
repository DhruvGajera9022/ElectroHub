package com.example.swiftmart.Frgments;

import android.content.Intent;
import android.os.Bundle;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.swiftmart.Adapter.CartAdapter;
import com.example.swiftmart.ConfirmAddressActivity2;
import com.example.swiftmart.Model.ProductModel;
import com.example.swiftmart.R;
import com.example.swiftmart.Utils.CustomToast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CartFragment extends Fragment {
    private RecyclerView cartRecyclerView;
    private ArrayList<ProductModel> datalist = new ArrayList<>();
    private CartAdapter adapter;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String uid;
    private TextView cartProductTotal, cartProductDeliveryTotal, cartProductVoucherTotal, cartProductFinalTotal;
    private double totalPrice = 0;
    private int deliveryCharges = 50;
    private AppCompatButton cartFragmentCheckout;

    private Map<String, Double> itemTotals = new HashMap<>();

    public CartFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        initialization(view);
        getCartData();
        handleOnBackPress();
        handleCheckoutClick();
        return view;
    }

    private void initialization(View view) {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();

        cartRecyclerView = view.findViewById(R.id.cartRecyclerView);
        cartProductTotal = view.findViewById(R.id.cartProductTotal);
        cartProductDeliveryTotal = view.findViewById(R.id.cartProductDeliveryTotal);
        cartProductVoucherTotal = view.findViewById(R.id.cartProductVoucherTotal);
        cartProductFinalTotal = view.findViewById(R.id.cartProductFinalTotal);

        cartFragmentCheckout = view.findViewById(R.id.cartFragmentCheckout);

        adapter = new CartAdapter(getContext(), datalist);
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        cartRecyclerView.setItemAnimator(new DefaultItemAnimator());
        cartRecyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new CartAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String data, boolean isPlus, int position) {
                if (position != RecyclerView.NO_POSITION) {
                    updateItemTotal(datalist.get(position).getOid(), Double.parseDouble(data));
                }
            }

            @Override
            public void onItemDeleted(String oid, double price, int position) {
                if (itemTotals.containsKey(oid)) {
                    totalPrice -= itemTotals.get(oid);
                    itemTotals.remove(oid);
                    updateTotal();
                }

                if (datalist.isEmpty()) {
                    resetTotals();
                }
            }
        });
    }

    private void getCartData() {
        db.collection("Users").document(uid).collection("Cart")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        error.printStackTrace();
                        CustomToast.showToast(getContext(), "Error listening for cart updates");
                        return;
                    }

                    if (value != null && !value.isEmpty()) {
                        datalist.clear();
                        itemTotals.clear();
                        totalPrice = 0;

                        for (QueryDocumentSnapshot documentSnapshot : value) {
                            ProductModel product = documentSnapshot.toObject(ProductModel.class);
                            String priceString = product.getPrice();
                            if (priceString != null) {
                                try {
                                    double price = Double.parseDouble(priceString);
                                    double itemTotal = price * product.getQty();
                                    itemTotals.put(product.getOid(), itemTotal);
                                    totalPrice += itemTotal;
                                } catch (NumberFormatException e) {
                                    e.printStackTrace();
                                    CustomToast.showToast(getContext(), "Invalid price format detected");
                                }
                            }
                            datalist.add(product);
                        }

                        adapter.notifyDataSetChanged();
                        updateTotal();
                    } else {
                        datalist.clear();
                        itemTotals.clear();
                        adapter.notifyDataSetChanged();
                        resetTotals();
                    }
                });
    }

    private void resetTotals() {
        totalPrice = 0;
        itemTotals.clear();
        cartProductTotal.setText("0");
        cartProductVoucherTotal.setText("0");
        cartProductDeliveryTotal.setText("0");
        cartProductFinalTotal.setText("0");
    }

    private void updateTotal() {
        NumberFormat currencyFormat = NumberFormat.getNumberInstance(Locale.getDefault());
        cartProductTotal.setText(currencyFormat.format(totalPrice));
        cartProductDeliveryTotal.setText(currencyFormat.format(deliveryCharges));
        double finalTotal = totalPrice;
        cartProductFinalTotal.setText(currencyFormat.format(finalTotal));
    }

    private void updateItemTotal(String itemId, double newItemTotal) {
        if (itemId != null && itemTotals.containsKey(itemId)) {
            // Subtract old total and add new total
            totalPrice = totalPrice - itemTotals.get(itemId) + newItemTotal;
            // Update the stored item total
            itemTotals.put(itemId, newItemTotal);
            updateTotal();
        }
    }

    private void handleCheckoutClick(){

        cartFragmentCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ArrayList<String> productIds = new ArrayList<>();
                for (ProductModel product : datalist) {
                    productIds.add(product.getPid());
                }

                Intent intent = new Intent(getContext(), ConfirmAddressActivity2.class);
                intent.putStringArrayListExtra("productIDs", productIds);
                intent.putExtra("totalAmount", cartProductFinalTotal.getText().toString());
                startActivity(intent);
            }
        });
    }

    private void handleOnBackPress() {
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frameLayout, new HomeFragment())
                        .commit();
            }
        });
    }
}