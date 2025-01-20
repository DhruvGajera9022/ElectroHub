package com.example.swiftmart.Account;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.swiftmart.MainActivity;
import com.example.swiftmart.R;
import com.example.swiftmart.Utils.CustomToast;
import com.google.android.gms.tasks.OnFailureListener;
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

    private String uid;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private TextInputEditText txtEditProfileName, txtEditProfileNumber, txtEditProfileEmail;
    private ImageView editProfileSelectImage, userImage, cart, backBtn;
    private TextView toolBarTitle;
    private AppCompatButton editProfileBtn;
    private Uri imgUpdateUri;
    private boolean isImageSelected = false;
    private ProgressBar editProfileProgressBar;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();

        txtEditProfileName = findViewById(R.id.txtEditProfileName);
        txtEditProfileNumber = findViewById(R.id.txtEditProfileNumber);
        txtEditProfileEmail = findViewById(R.id.txtEditProfileEmail);
        editProfileSelectImage = findViewById(R.id.editProfileSelectImage);
        editProfileBtn = findViewById(R.id.editProfileBtn);
        userImage = findViewById(R.id.userImage);
        editProfileProgressBar = findViewById(R.id.editProfileProgressBar);

        backBtn = findViewById(R.id.backBtn);
        toolBarTitle = findViewById(R.id.toolBarTitle);

        getUserData();

        toolBarTitle.setText("Edit Profile");
        backBtn.setOnClickListener(v -> onBackPressed());

        handleEditProfileSelectImageClick();
        handleEditProfileBtnClick();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void navigateToCartFragment() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("showFragment", "cart");
        startActivity(intent);
        finish();
    }

    private void getUserData() {
        DocumentReference reference = db.collection("Users").document(uid);

        reference.addSnapshotListener((value, error) -> {
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

    private void handleEditProfileSelectImageClick() {
        editProfileSelectImage.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, 100);
        });
    }

    private void handleEditProfileBtnClick() {
        editProfileBtn.setOnClickListener(v -> updateUserData());
    }

    private void updateUserData() {
        if (isImageSelected) {
            uploadImageAndSaveData();
        } else {
            saveUserData(null);
        }
    }

    private void uploadImageAndSaveData() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss", Locale.UK);
        String timestamp = format.format(new Date());

        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", "dbbdbt7z1");
        config.put("api_key", "731466623192577");
        config.put("api_secret", "C9mFzlUvIQCzbzumNK7C0hz1gHo");
        Cloudinary cloudinary = new Cloudinary(config);

        // Show progress bar and disable button at the start
        runOnUiThread(() -> {
            editProfileProgressBar.setVisibility(View.VISIBLE);
            editProfileBtn.setVisibility(View.GONE);
        });

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            if (imgUpdateUri != null) {
                try (InputStream inputStream = getContentResolver().openInputStream(imgUpdateUri)) {
                    if (inputStream != null) {
                        Map<String, Object> uploadResult = cloudinary.uploader().upload(inputStream, ObjectUtils.asMap(
                                "folder", "user_profiles/" + timestamp,
                                "public_id", "profile_image_" + System.currentTimeMillis()
                        ));
                        String imageUrl = (String) uploadResult.get("secure_url");
                        // Save user data and reset progress
                        runOnUiThread(() -> {
                            saveUserData(imageUrl);
                            editProfileProgressBar.setVisibility(View.GONE);
                            editProfileBtn.setVisibility(View.VISIBLE);
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    runOnUiThread(() -> {
                        runOnUiThread(() -> {
                            CustomToast.showToast(Edit_profile_Activity.this, "Error uploading image");
                            editProfileProgressBar.setVisibility(View.GONE);
                            editProfileBtn.setVisibility(View.VISIBLE);
                        });
                    });
                }
            } else {
                runOnUiThread(() -> {
                    runOnUiThread(() -> {
                        CustomToast.showToast(Edit_profile_Activity.this,"No image selected");
                        editProfileProgressBar.setVisibility(View.GONE);
                        editProfileBtn.setVisibility(View.VISIBLE);
                    });
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
        progress();
        db.collection("Users").document(uid)
                .update(userData)
                .addOnSuccessListener(unused -> {
                    CustomToast.showToast(Edit_profile_Activity.this,"Profile updated successfully");
                    editProfileBtn.setVisibility(View.VISIBLE);
                    editProfileProgressBar.setVisibility(View.GONE);
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        editProfileBtn.setVisibility(View.VISIBLE);
                        editProfileProgressBar.setVisibility(View.GONE);
                        CustomToast.showToast(Edit_profile_Activity.this, "Error updating profile");
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imgUpdateUri = data.getData();
            userImage.setImageURI(imgUpdateUri);
            isImageSelected = true;
        }
    }

    // handle progress bar
    public void progress(){
        if (editProfileBtn.isPressed()){
            editProfileBtn.setVisibility(View.GONE);
            editProfileProgressBar.setVisibility(View.VISIBLE);
        }else {
            editProfileBtn.setVisibility(View.VISIBLE);
            editProfileProgressBar.setVisibility(View.GONE);
        }
    }
}
