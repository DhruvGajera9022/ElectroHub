package com.example.swiftmart.Account;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.swiftmart.MainActivity;
import com.example.swiftmart.R;
import com.example.swiftmart.Utils.CustomToast;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Edit_profile_Activity extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String uid;

    private TextInputEditText txtEditProfileName, txtEditProfileNumber, txtEditProfileEmail;
    private ImageView editProfileSelectImage, userImage, backBtn;
    private TextView toolBarTitle;
    private AppCompatButton editProfileBtn;
    private ProgressBar editProfileProgressBar;

    private Uri imgUpdateUri;
    private boolean isImageSelected = false;

    private final ActivityResultLauncher<String> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    imgUpdateUri = uri;
                    userImage.setImageURI(uri);
                    isImageSelected = true;
                }
            });

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        initViews();
        setupToolbar();
        loadUserData();
        setupListeners();
        setupAds();
        setStatusBarColor(R.color.home);
    }

    private void initViews() {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();

        txtEditProfileName = findViewById(R.id.txtEditProfileName);
        txtEditProfileNumber = findViewById(R.id.txtEditProfileNumber);
        txtEditProfileEmail = findViewById(R.id.txtEditProfileEmail);
        editProfileSelectImage = findViewById(R.id.editProfileSelectImage);
        userImage = findViewById(R.id.userImage);
        editProfileBtn = findViewById(R.id.editProfileBtn);
        backBtn = findViewById(R.id.backBtn);
        toolBarTitle = findViewById(R.id.toolBarTitle);
        editProfileProgressBar = findViewById(R.id.editProfileProgressBar);
    }

    private void setupToolbar() {
        toolBarTitle.setText(R.string.edit_profile);
        backBtn.setOnClickListener(v -> onBackPressed());
    }

    private void setupListeners() {
        editProfileSelectImage.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));
        editProfileBtn.setOnClickListener(v -> {
            if (validateInputs()) {
                updateUserData();
            }
        });
    }

    private boolean validateInputs() {
        String name = txtEditProfileName.getText() != null ? txtEditProfileName.getText().toString().trim() : "";
        String email = txtEditProfileEmail.getText() != null ? txtEditProfileEmail.getText().toString().trim() : "";
        String number = txtEditProfileNumber.getText() != null ? txtEditProfileNumber.getText().toString().trim() : "";

        if (name.isEmpty()) {
            txtEditProfileName.setError("Name is required");
            return false;
        }
        if (email.isEmpty() || !email.matches("^[\\w.-]+@[\\w.-]+\\.\\w+$")) {
            txtEditProfileEmail.setError("Valid email is required");
            return false;
        }
        if (number.isEmpty() || !number.matches("^[0-9]{10}$")) {
            txtEditProfileNumber.setError("Valid 10-digit number is required");
            return false;
        }
        return true;
    }

    private void loadUserData() {
        db.collection("Users").document(uid)
                .addSnapshotListener((value, error) -> {
                    if (value != null && value.exists()) {
                        txtEditProfileName.setText(value.getString("Username"));
                        txtEditProfileEmail.setText(value.getString("Email"));
                        txtEditProfileNumber.setText(value.getString("Number"));

                        String imageUrl = value.getString("Image");
                        if (imageUrl != null && !imageUrl.isEmpty()) {
                            Picasso.get().load(imageUrl).into(userImage);
                        }
                    }
                });
    }

    private void updateUserData() {
        toggleLoading(true);
        if (isImageSelected && imgUpdateUri != null) {
            uploadImageToCloudinary();
        } else {
            saveUserData(null);
        }
    }

    private void uploadImageToCloudinary() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss", Locale.UK);
        String timestamp = format.format(new Date());

        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", "dbbdbt7z1");
        config.put("api_key", "731466623192577");
        config.put("api_secret", "C9mFzlUvIQCzbzumNK7C0hz1gHo");
        Cloudinary cloudinary = new Cloudinary(config);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try (InputStream inputStream = getContentResolver().openInputStream(imgUpdateUri)) {
                if (inputStream != null) {
                    Map<String, Object> uploadResult = cloudinary.uploader().upload(inputStream, ObjectUtils.asMap(
                            "folder", "user_profiles/" + timestamp,
                            "public_id", "profile_image_" + System.currentTimeMillis()
                    ));
                    String imageUrl = (String) uploadResult.get("secure_url");
                    runOnUiThread(() -> saveUserData(imageUrl));
                }
            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    CustomToast.showToast(Edit_profile_Activity.this, "Image upload failed");
                    toggleLoading(false);
                });
            }
        });
    }

    private void saveUserData(@Nullable String imageUrl) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("Username", txtEditProfileName.getText().toString().trim());
        userData.put("Email", txtEditProfileEmail.getText().toString().trim());
        userData.put("Number", txtEditProfileNumber.getText().toString().trim());
        if (imageUrl != null) {
            userData.put("Image", imageUrl);
        }

        db.collection("Users").document(uid)
                .update(userData)
                .addOnSuccessListener(unused -> {
                    CustomToast.showToast(Edit_profile_Activity.this, "Profile updated successfully");
                    toggleLoading(false);
                })
                .addOnFailureListener(e -> {
                    CustomToast.showToast(Edit_profile_Activity.this, "Failed to update profile");
                    toggleLoading(false);
                });
    }

    private void toggleLoading(boolean isLoading) {
        editProfileProgressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        editProfileBtn.setVisibility(isLoading ? View.GONE : View.VISIBLE);
    }

    private void setupAds() {
        MobileAds.initialize(this, initializationStatus -> {
        });
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    private void setStatusBarColor(int colorResource) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(getResources().getColor(colorResource));
        }
    }
}
