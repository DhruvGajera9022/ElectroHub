package com.example.swiftmart.Frgments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.swiftmart.Adapter.OrderProductAdapter;
import com.example.swiftmart.Model.OrderModel;
import com.example.swiftmart.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class TabFragment extends Fragment {
    private RecyclerView ordersRecyclerView;
    private TextView emptyView;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private ArrayList<OrderModel> orderList;
    private OrderProductAdapter orderAdapter;
    private String uid;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tab, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ordersRecyclerView = view.findViewById(R.id.ordersRecyclerView);
        emptyView = view.findViewById(R.id.emptyView);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
            uid = mAuth.getCurrentUser().getUid();
        } else {
            return;
        }

        orderList = new ArrayList<>();
        orderAdapter = new OrderProductAdapter(getContext(), orderList);

        ordersRecyclerView.setAdapter(orderAdapter);

        ordersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        int position = getArguments().getInt("tab_position", 0);

        switch (position) {
            case 0:
                emptyView.setText("No active items");
                getOrders("Pending");
                break;
            case 1:
                emptyView.setText("No shippinggi items");
                getOrders("Accepted");
                break;
            case 2:
                emptyView.setText("No shipped items");
                getOrders("Shipped");
                break;
            case 3:
                emptyView.setText("No canceled items");
                getOrders("Canceled");
                break;
        }
    }

    private void getOrders(String status){
        if (uid == null || uid.isEmpty()) {
            return;
        }

        db.collection("Orders")
                .whereEqualTo("uid", uid)
                .whereEqualTo("status", status)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.e("Firestore Error", "Error loading orders: ", error);
                            return;
                        }

                        if (value != null && !value.isEmpty()) {
                            orderList.clear();
                            for (QueryDocumentSnapshot documentSnapshot : value) {
                                OrderModel orderModel = documentSnapshot.toObject(OrderModel.class);
                                orderList.add(orderModel);
                            }
                            orderAdapter.notifyDataSetChanged();
                        }

                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();

        if (getArguments() != null) {
            int position = getArguments().getInt("tab_position", 0);
            switch (position) {
                case 0:
                    emptyView.setText("No active items");
                    getOrders("Pending");
                    break;
                case 1:
                    emptyView.setText("No shipping items");
                    getOrders("Accepted");
                    break;
                case 2:
                    emptyView.setText("No shipped items");
                    getOrders("Shipped");
                    break;
                case 3:
                    emptyView.setText("No canceled items");
                    getOrders("Canceled");
                    break;
            }
        }
    }


}