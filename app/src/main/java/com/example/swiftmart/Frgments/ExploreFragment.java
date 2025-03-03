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

    private ImageButton exploreFilterIcon;

    FirebaseFirestore db;
    ArrayList<ProductModel> datalist = new ArrayList<>();
    ExploreProductAdapter adapter;

    // Current filter state variables
    private String currentSortBy = "name";
    private boolean isAscending = true;
    private float minPrice = 0;
    private float maxPrice = 250000;
    private String selectedCategory = "";
    private String selectedCompany = "";

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

        exploreFilterIcon = view.findViewById(R.id.exploreFilterIcon);
        exploreFilterIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFilterBottomSheet();
            }
        });

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

    // Method to show the filter bottom sheet
    private void showFilterBottomSheet() {
        BottomSheetDialog filterDialog = new BottomSheetDialog(getContext());
        View bottomSheetView = LayoutInflater.from(getContext())
                                       .inflate(R.layout.filter_bottom_sheet, null);
        filterDialog.setContentView(bottomSheetView);

        // Initialize UI components
        RadioGroup sortByGroup = bottomSheetView.findViewById(R.id.sortByRadioGroup);
        RadioGroup orderGroup = bottomSheetView.findViewById(R.id.orderRadioGroup);
        RangeSlider priceRangeSlider = bottomSheetView.findViewById(R.id.priceRangeSlider);
        ChipGroup categoryChipGroup = bottomSheetView.findViewById(R.id.categoryChipGroup);
        ChipGroup companyChipGroup = bottomSheetView.findViewById(R.id.companyChipGroup);
        Button applyFilterBtn = bottomSheetView.findViewById(R.id.applyFilterBtn);
        Button resetFilterBtn = bottomSheetView.findViewById(R.id.resetFilterBtn);

        // Set initial values based on current filter state
        setInitialRadioButton(sortByGroup, currentSortBy);
        setInitialOrderRadioButton(orderGroup, isAscending);

        // Setup price range slider
        priceRangeSlider.setValues(minPrice, maxPrice);

        // Load categories and companies dynamically
        loadCategories(categoryChipGroup);
        loadCompanies(companyChipGroup);

        // Set selected chip if any
        setSelectedChip(categoryChipGroup, selectedCategory);
        setSelectedChip(companyChipGroup, selectedCompany);

        // Apply filter button click listener
        applyFilterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get selected sort field
                int sortBySelectedId = sortByGroup.getCheckedRadioButtonId();
                if (sortBySelectedId == R.id.sortByName) {
                    currentSortBy = "name";
                } else if (sortBySelectedId == R.id.sortByPrice) {
                    currentSortBy = "price";
                } else if (sortBySelectedId == R.id.sortByCompany) {
                    currentSortBy = "company";
                }

                // Get selected order
                int orderSelectedId = orderGroup.getCheckedRadioButtonId();
                isAscending = (orderSelectedId == R.id.orderAscending);

                // Get price range
                List<Float> priceValues = priceRangeSlider.getValues();
                minPrice = priceValues.get(0);
                maxPrice = priceValues.get(1);

                // Get selected category and company
                selectedCategory = getSelectedChipText(categoryChipGroup);
                selectedCompany = getSelectedChipText(companyChipGroup);

                // Apply filters
                applyFilters();
                filterDialog.dismiss();
            }
        });

        // Reset filter button click listener
        resetFilterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetFilters();
                filterDialog.dismiss();
            }
        });

        filterDialog.show();
    }

    private void setInitialRadioButton(RadioGroup group, String value) {
        int id;
        switch (value) {
            case "price":
                id = R.id.sortByPrice;
                break;
            case "company":
                id = R.id.sortByCompany;
                break;
            case "name":
            default:
                id = R.id.sortByName;
                break;
        }
        group.check(id);
    }

    private void setInitialOrderRadioButton(RadioGroup group, boolean isAscending) {
        group.check(isAscending ? R.id.orderAscending : R.id.orderDescending);
    }

    private void loadCategories(ChipGroup chipGroup) {
        chipGroup.removeAllViews();

        // Add "All" category chip
        Chip allChip = createChip("All");
        chipGroup.addView(allChip);

        // Get unique categories from Firebase
        db.collection("Products")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        ArrayList<String> categories = new ArrayList<>();

                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            ProductModel model = document.toObject(ProductModel.class);
                            if (model.getCategory() != null && !model.getCategory().isEmpty()
                                        && !categories.contains(model.getCategory())) {
                                categories.add(model.getCategory());
                            }
                        }

                        // Sort categories alphabetically
                        Collections.sort(categories);

                        // Add category chips
                        for (String category : categories) {
                            Chip chip = createChip(category);
                            chipGroup.addView(chip);
                        }

                        // Set selected chip if any
                        setSelectedChip(chipGroup, selectedCategory);
                    }
                });
    }

    private void loadCompanies(ChipGroup chipGroup) {
        chipGroup.removeAllViews();

        // Add "All" company chip
        Chip allChip = createChip("All");
        chipGroup.addView(allChip);

        // Get unique companies from Firebase
        db.collection("Products")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        ArrayList<String> companies = new ArrayList<>();

                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            ProductModel model = document.toObject(ProductModel.class);
                            if (model.getCompany() != null && !model.getCompany().isEmpty()
                                        && !companies.contains(model.getCompany())) {
                                companies.add(model.getCompany());
                            }
                        }

                        // Sort companies alphabetically
                        Collections.sort(companies);

                        // Add company chips
                        for (String company : companies) {
                            Chip chip = createChip(company);
                            chipGroup.addView(chip);
                        }

                        // Set selected chip if any
                        setSelectedChip(chipGroup, selectedCompany);
                    }
                });
    }

    private Chip createChip(String text) {
        Chip chip = new Chip(getContext());
        chip.setText(text);
        chip.setCheckable(true);
        chip.setClickable(true);
        return chip;
    }

    private void setSelectedChip(ChipGroup chipGroup, String selectedText) {
        if (TextUtils.isEmpty(selectedText) || selectedText.equals("All")) {
            // Select "All" chip by default or if selected is "All"
            for (int i = 0; i < chipGroup.getChildCount(); i++) {
                Chip chip = (Chip) chipGroup.getChildAt(i);
                if (chip.getText().toString().equals("All")) {
                    chip.setChecked(true);
                    break;
                }
            }
        } else {
            for (int i = 0; i < chipGroup.getChildCount(); i++) {
                Chip chip = (Chip) chipGroup.getChildAt(i);
                if (chip.getText().toString().equals(selectedText)) {
                    chip.setChecked(true);
                    break;
                }
            }
        }
    }

    private String getSelectedChipText(ChipGroup chipGroup) {
        int selectedChipId = chipGroup.getCheckedChipId();
        if (selectedChipId != View.NO_ID) {
            Chip selectedChip = chipGroup.findViewById(selectedChipId);
            return selectedChip.getText().toString();
        }
        return "All"; // Default to "All" if no chip is selected
    }

    private void applyFilters() {
        exploreFragmentProgressBar.setVisibility(View.VISIBLE);
        datalist.clear();

        // Start building the query
        Query query = db.collection("Products");

        // Apply category filter if not "All"
        if (!TextUtils.isEmpty(selectedCategory) && !selectedCategory.equals("All")) {
            query = query.whereEqualTo("category", selectedCategory);
        }

        // Apply company filter if not "All"
        if (!TextUtils.isEmpty(selectedCompany) && !selectedCompany.equals("All")) {
            query = query.whereEqualTo("company", selectedCompany);
        }

        // Execute the query
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                exploreFragmentProgressBar.setVisibility(View.GONE);
                datalist.clear();

                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    ProductModel model = document.toObject(ProductModel.class);

                    // Apply price range filter
                    float productPrice = 0;
                    try {
                        productPrice = Float.parseFloat(model.getPrice());
                    } catch (NumberFormatException e) {
                        // Handle price parsing error
                    }

                    if (productPrice >= minPrice && productPrice <= maxPrice) {
                        datalist.add(model);
                    }
                }

                // Apply sorting
                applySorting();

                // Update the RecyclerView
                updateRecyclerView();
            }
        });
    }

    private void applySorting() {
        // Sort the data list based on current sort and order
        Collections.sort(datalist, new Comparator<ProductModel>() {
            @Override
            public int compare(ProductModel p1, ProductModel p2) {
                int result = 0;

                switch (currentSortBy) {
                    case "name":
                        result = p1.getName().compareToIgnoreCase(p2.getName());
                        break;
                    case "price":
                        try {
                            float price1 = Float.parseFloat(p1.getPrice());
                            float price2 = Float.parseFloat(p2.getPrice());
                            result = Float.compare(price1, price2);
                        } catch (NumberFormatException e) {
                            result = 0;
                        }
                        break;
                    case "company":
                        result = p1.getCompany().compareToIgnoreCase(p2.getCompany());
                        break;
                }

                // Apply order (ascending or descending)
                return isAscending ? result : -result;
            }
        });
    }

    private void resetFilters() {
        // Reset filter state variables
        currentSortBy = "name";
        isAscending = true;
        minPrice = 0;
        maxPrice = 250000; // Changed from 10000 to 250000
        selectedCategory = "";
        selectedCompany = "";

        // Reload all products
        getAllProducts();
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