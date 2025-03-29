package com.example.swiftmart.Account;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.swiftmart.Adapter.OrderProductAdapter;
import com.example.swiftmart.Adapter.TabAdapter;
import com.example.swiftmart.Model.OrderModel;
import com.example.swiftmart.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class OrdersActivity extends AppCompatActivity {
    private TabLayout orderTabs;
    private ViewPager2 orderActivityViewPager;
    private TabAdapter tabAdapter;
    private ImageView backBtn;
    private TextView toolBarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        setStatusBarColor(R.color.home);

        orderTabs = findViewById(R.id.orderTabs);
        orderActivityViewPager = findViewById(R.id.orderActivityViewPager);

        backBtn = findViewById(R.id.backBtn);
        toolBarTitle = findViewById(R.id.toolBarTitle);

        tabAdapter = new TabAdapter(OrdersActivity.this);
        orderActivityViewPager.setAdapter(tabAdapter);

        TabLayoutMediator mediator = new TabLayoutMediator(orderTabs, orderActivityViewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText(R.string.pending);
                            break;
                        case 1:
                            tab.setText(R.string.shipping);
                            break;
                        case 2:
                            tab.setText(R.string.shipped);
                            break;
                        case 3:
                            tab.setText(R.string.canceled);
                            break;
                    }
                }
        );
        mediator.attach();

        toolBarTitle.setText(R.string.orders);
        backBtn.setOnClickListener(v -> onBackPressed());
    }

    private void setStatusBarColor(int colorResource) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(getResources().getColor(colorResource));
        }
    }
}
