package com.example.swiftmart;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RatingBar;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;

import com.example.swiftmart.Utils.CustomToast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class RateUsDialog extends Dialog {

	private FirebaseAuth mAuth;
	private FirebaseFirestore firestore;
	private String uid;
	private float userRate = 0;
	private RatingBar ratingBar;
	private ImageView ratingImage;

	public RateUsDialog(@NonNull Context context) {
		super(context);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rate_us_dialog_layout);

		final AppCompatButton rateNowBtn = findViewById(R.id.rateNowBtn);
		final AppCompatButton laterBtn = findViewById(R.id.laterBtn);
		ratingBar = findViewById(R.id.ratingBar);
		ratingImage = findViewById(R.id.ratingImage);

		mAuth = FirebaseAuth.getInstance();
		firestore = FirebaseFirestore.getInstance();

		if (mAuth.getCurrentUser() != null) {
			uid = mAuth.getCurrentUser().getUid();
			fetchUserRating(); // Fetch existing rating when dialog opens
		} else {
			CustomToast.showToast(getContext(), "User not logged in");
			return;
		}

		rateNowBtn.setOnClickListener(v -> submitRating(userRate));
		laterBtn.setOnClickListener(v -> dismiss());

		ratingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
			updateRatingImage(rating);
			animateImage(ratingImage);
			userRate = rating;
		});
	}

	private void fetchUserRating() {
		DocumentReference ratingRef = firestore.collection("AppRating").document(uid);
		ratingRef.get().addOnSuccessListener(documentSnapshot -> {
			if (documentSnapshot.exists()) {
				userRate = documentSnapshot.getDouble("Rating").floatValue(); // Get previous rating
				ratingBar.setRating(userRate);
				updateRatingImage(userRate);
			}
		}).addOnFailureListener(e ->
				                        CustomToast.showToast(getContext(), "Error fetching rating: " + e.getMessage())
		);
	}

	private void submitRating(float userRate) {
		Map<String, Object> ratingMap = new HashMap<>();
		ratingMap.put("Rating", userRate);
		ratingMap.put("UserId", uid);

		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
		LocalDateTime now = LocalDateTime.now();
		ratingMap.put("date", dtf.format(now));

		firestore.collection("AppRating")
				.document(uid)
				.set(ratingMap) // This will update the existing document
				.addOnSuccessListener(aVoid -> {
					CustomToast.showToast(getContext(), "Rating submitted successfully");
					dismiss();
				})
				.addOnFailureListener(e ->
						                      CustomToast.showToast(getContext(), "Error submitting rating: " + e.getMessage())
				);
	}

	private void updateRatingImage(float rating) {
		if (rating <= 1) ratingImage.setImageResource(R.drawable.one_star);
		else if (rating <= 2) ratingImage.setImageResource(R.drawable.two_star);
		else if (rating <= 3) ratingImage.setImageResource(R.drawable.three_star);
		else if (rating <= 4) ratingImage.setImageResource(R.drawable.four_star);
		else ratingImage.setImageResource(R.drawable.five_star);
	}

	private void animateImage(ImageView ratingImage) {
		ScaleAnimation scaleAnimation = new ScaleAnimation(0, 1f, 0, 1f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.0f);
		scaleAnimation.setFillAfter(true);
		scaleAnimation.setDuration(200);
		ratingImage.startAnimation(scaleAnimation);
	}
}
