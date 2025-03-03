package com.example.swiftmart;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.Explode;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.swiftmart.Adapter.ExploreProductAdapter;
import com.example.swiftmart.Adapter.ProductAdapter;
import com.example.swiftmart.Model.ProductModel;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SearchActivity extends AppCompatActivity {

	private EditText searchEditText;
	private View searchLayout;
	private Handler searchHandler = new Handler();
	private Runnable searchRunnable;
	private static final long SEARCH_DELAY = 300;

	private RecyclerView searchResultsRecyclerView;
	private TextView noResultsTextView;
	private ExploreProductAdapter productAdapter;
	private List<ProductModel> productList;
	private List<ProductModel> allProductsList;
	private FirebaseFirestore db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);

		// Setup window enter transition
		getWindow().setEnterTransition(new Explode());
		getWindow().getEnterTransition().setDuration(300);

		// Initialize views
		searchLayout = findViewById(R.id.search_container);
		searchEditText = findViewById(R.id.search_edit_text);
		searchResultsRecyclerView = findViewById(R.id.results_recycler_view);
		noResultsTextView = findViewById(R.id.no_results);

		// Initialize Firebase
		db = FirebaseFirestore.getInstance();

		productList = new ArrayList<>();
		allProductsList = new ArrayList<>();

		productAdapter = new ExploreProductAdapter(this, (ArrayList<ProductModel>) productList);
		searchResultsRecyclerView.setAdapter(productAdapter);


		searchResultsRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
		searchResultsRecyclerView.setAdapter(productAdapter);

		// Get position data from intent for animation
		Intent intent = getIntent();
		int startX = intent.getIntExtra("SEARCH_X", 0);
		int startY = intent.getIntExtra("SEARCH_Y", 0);
		int startWidth = intent.getIntExtra("SEARCH_WIDTH", 0);
		int startHeight = intent.getIntExtra("SEARCH_HEIGHT", 0);

		// Apply entrance animation
		animateSearchBar(startX, startY, startWidth, startHeight);

		// Close button or back navigation
		findViewById(R.id.back_button).setOnClickListener(v -> onBackPressed());

		// Setup search functionality with debounce
		setupSearchFunctionality();

		getAllProducts();
	}

	private void getAllProducts() {
		db.collection("Products")
				.addSnapshotListener(new EventListener<QuerySnapshot>() {
					@Override
					public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
						if (error != null) {
							Log.e("Firestore Error", error.getMessage());
							return;
						}

						if (value != null) {
							// Clear previous data to avoid duplication
							allProductsList.clear();
							productList.clear();

							if (!value.isEmpty()) {
								for (QueryDocumentSnapshot documentSnapshot : value) {
									ProductModel productModel = documentSnapshot.toObject(ProductModel.class);
									productModel.setPid(documentSnapshot.getId()); // Ensure PID is set
									allProductsList.add(productModel);
									productList.add(productModel); // Initially show all products
								}
							}

							// Notify the adapter to refresh the RecyclerView
							productAdapter.notifyDataSetChanged();

							// Show or hide "No results" message
							showNoResults(productList.isEmpty());

							// If user already started typing, apply the search
							String currentQuery = searchEditText.getText().toString().trim();
							if (!currentQuery.isEmpty()) {
								performLocalSearch(currentQuery);
							}
						}
					}
				});
	}

	private void setupSearchFunctionality() {
		searchEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// Not used
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// Cancel any pending searches
				if (searchRunnable != null) {
					searchHandler.removeCallbacks(searchRunnable);
				}
			}

			@Override
			public void afterTextChanged(Editable s) {
				String query = s.toString().trim();

				// Only search if there's something to search for
				if (query.isEmpty()) {
					resetToAllProducts();
					return;
				}

				// Create a new search with delay (debounce)
				searchRunnable = () -> performLocalSearch(query);
				searchHandler.postDelayed(searchRunnable, SEARCH_DELAY);
			}
		});
	}

	private void performLocalSearch(String query) {
		// Convert query to lowercase for case-insensitive search
		String lowercaseQuery = query.toLowerCase(Locale.getDefault());

		// Clear current results
		productList.clear();

		// Filter products that contain the query string anywhere in the name
		for (ProductModel product : allProductsList) {
			if (product.getName() != null &&
					    product.getName().toLowerCase(Locale.getDefault()).contains(lowercaseQuery)) {
				productList.add(product);
			}
		}

		// Update UI
		productAdapter.notifyDataSetChanged();
		showNoResults(productList.isEmpty());

		// Log search results
		Log.d("Search", "Found " + productList.size() + " products matching '" + query + "'");
	}

	private void resetToAllProducts() {
		productList.clear();
		productList.addAll(allProductsList);
		productAdapter.notifyDataSetChanged();
		showNoResults(productList.isEmpty());
	}

	private void showNoResults(boolean show) {
		if (noResultsTextView != null) {
			noResultsTextView.setVisibility(show ? View.VISIBLE : View.GONE);
		}
		searchResultsRecyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
	}

	private void showError(String errorMessage) {
		// Show error message
		if (noResultsTextView != null) {
			noResultsTextView.setText(errorMessage);
			noResultsTextView.setVisibility(View.VISIBLE);
		}
		searchResultsRecyclerView.setVisibility(View.GONE);
	}

	private void animateSearchBar(int startX, int startY, int startWidth, int startHeight) {
		searchLayout.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
			@Override
			public boolean onPreDraw() {
				searchLayout.getViewTreeObserver().removeOnPreDrawListener(this);

				// Get the final position
				int finalX = (int) searchLayout.getX();
				int finalY = (int) searchLayout.getY();
				int finalWidth = searchLayout.getWidth();
				int finalHeight = searchLayout.getHeight();

				// Reset to starting position
				searchLayout.setX(startX);
				searchLayout.setY(startY);
				searchLayout.setScaleX((float) startWidth / finalWidth);
				searchLayout.setScaleY((float) startHeight / finalHeight);
				searchLayout.setAlpha(0.8f);

				// Animate to final position
				searchLayout.animate()
						.x(finalX)
						.y(finalY)
						.scaleX(1f)
						.scaleY(1f)
						.alpha(1f)
						.setDuration(300)
						.setInterpolator(new DecelerateInterpolator())
						.start();

				return true;
			}
		});
	}

	@Override
	public void onBackPressed() {
		// Animate search bar back to original position before finishing
		searchLayout.animate()
				.alpha(0.8f)
				.setDuration(200)
				.setInterpolator(new DecelerateInterpolator())
				.withEndAction(() -> {
					// Call finish after animation completes
					ActivityCompat.finishAfterTransition(SearchActivity.this);
				})
				.start();
	}
}