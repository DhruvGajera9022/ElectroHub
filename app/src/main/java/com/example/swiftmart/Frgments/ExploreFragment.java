package com.example.swiftmart.Frgments;

import android.content.Intent;
import android.os.Bundle;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;

import com.example.swiftmart.Adapter.ExploreProductAdapter;
import com.example.swiftmart.MainActivity;
import com.example.swiftmart.Model.ProductModel;
import com.example.swiftmart.R;
import com.example.swiftmart.Utils.CustomToast;
import com.example.swiftmart.Utils.FilterData;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.slider.RangeSlider;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ExploreFragment extends Fragment {
    private SearchView exploreFragmentSearchView;
    private SwipeRefreshLayout exploreFragmentSwipeRefresh;
    private RecyclerView exploreFragmentRecyclerView;
    private ProgressBar exploreFragmentProgressBar;

    FirebaseFirestore db;
    ArrayList<ProductModel> datalist = new ArrayList<>();
    ExploreProductAdapter adapter;

    public ExploreFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_explore, container, false);

        initialization(view);
        getAllProducts();
        swipeRefresh();
        handleSearch();
        handleOnBackPress();

        return view;
    }

    private void initialization(View view) {
        exploreFragmentSearchView = view.findViewById(R.id.exploreFragmentSearchView);
        exploreFragmentSwipeRefresh = view.findViewById(R.id.exploreFragmentSwipeRefresh);
        exploreFragmentRecyclerView = view.findViewById(R.id.exploreFragmentRecyclerView);
        exploreFragmentProgressBar = view.findViewById(R.id.exploreFragmentProgressBar);
        db = FirebaseFirestore.getInstance();
    }

    private void getAllProducts() {
        exploreFragmentRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        exploreFragmentProgressBar.setVisibility(View.VISIBLE);
        datalist.clear();

        db.collection("Products")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            CustomToast.showToast(getContext(), "Error in data fetching");
                            exploreFragmentProgressBar.setVisibility(View.GONE);
                            return;
                        }

                        if (value != null && !value.isEmpty()) {
                            exploreFragmentProgressBar.setVisibility(View.GONE);
                            datalist.clear();
                            for (QueryDocumentSnapshot documentSnapshot : value) {
                                ProductModel productModel = documentSnapshot.toObject(ProductModel.class);
                                datalist.add(productModel);
                            }
                            updateRecyclerView();
                        }
                    }
                });
    }

    private void swipeRefresh() {
        exploreFragmentSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getAllProducts();
                exploreFragmentSwipeRefresh.setRefreshing(false);
                exploreFragmentSearchView.setQuery("", false);
            }
        });
    }

    private void handleSearch() {
        exploreFragmentSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query != null && !query.trim().isEmpty()) {
                    searchProducts(query.trim());
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText != null && !newText.trim().isEmpty()) {
                    searchProducts(newText.trim());
                } else {
                    getAllProducts();
                }
                return true;
            }
        });
    }

    private void searchProducts(String query) {
        exploreFragmentRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        datalist.clear();

        if (query.isEmpty()) {
            getAllProducts();
        } else {
            db.collection("Products")
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            datalist.clear();
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                ProductModel model = document.toObject(ProductModel.class);
                                if (model.getName().toLowerCase().contains(query.toLowerCase())) {
                                    datalist.add(model);
                                }
                            }
                            updateRecyclerView();
                        }
                    });
        }
    }

    private void updateRecyclerView() {
        if (getContext() == null) return;

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        exploreFragmentRecyclerView.setLayoutManager(layoutManager);

        if (adapter == null) {
            adapter = new ExploreProductAdapter(getContext(), datalist);
            exploreFragmentRecyclerView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }

        exploreFragmentRecyclerView.setHasFixedSize(true);
        exploreFragmentRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private void handleOnBackPress() {
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                startActivity(new Intent(getContext(), MainActivity.class));
                exploreFragmentSearchView.setFocusable(false);
            }
        });
    }
}