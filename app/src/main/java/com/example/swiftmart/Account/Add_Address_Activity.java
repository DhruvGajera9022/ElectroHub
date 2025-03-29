package com.example.swiftmart.Account;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.core.app.ActivityCompat;

import com.example.swiftmart.MainActivity;
import com.example.swiftmart.R;
import com.example.swiftmart.Utils.CustomToast;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.WriteBatch;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class Add_Address_Activity extends AppCompatActivity {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;

    private RadioGroup rgAddressType;
    private FusedLocationProviderClient fusedLocationClient;
    private ImageView backBtn;
    private TextView toolBarTitle;
    private TextInputEditText addAddressFullName, addAddressPhoneNumber, addAddressPincode, addAddressState, addAddressCity, addAddressHouseNo, addAddressRoadName;
    private AppCompatCheckBox isDefaultCheckBox;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String uid;

    private ProgressBar addressProgressBar;
    private AppCompatButton btnSaveAddress;
    private Button btnUseMyLocation;

    private String addressID;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();

        addAddressFullName = findViewById(R.id.addAddressFullName);
        addAddressPhoneNumber = findViewById(R.id.addAddressPhoneNumber);
        addAddressPincode = findViewById(R.id.addAddressPincode);
        addAddressState = findViewById(R.id.addAddressState);
        addAddressCity = findViewById(R.id.addAddressCity);
        addAddressHouseNo = findViewById(R.id.addAddressHouseNo);
        addAddressRoadName = findViewById(R.id.addAddressRoadName);
        rgAddressType = findViewById(R.id.rgAddressType);

        backBtn = findViewById(R.id.backBtn);
        toolBarTitle = findViewById(R.id.toolBarTitle);

        isDefaultCheckBox = findViewById(R.id.isDefaultCheckBox);
        addressProgressBar = findViewById(R.id.addressProgressBar);


        btnUseMyLocation = findViewById(R.id.btnUseMyLocation);
        btnSaveAddress = findViewById(R.id.btnSaveAddress);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Handle "Use my location" button
        btnUseMyLocation.setOnClickListener(v -> requestLocationPermission());

        addressID = getIntent().getStringExtra("addressID");
        if (addressID != null) {
            btnSaveAddress.setText(R.string.edit_address);
            toolBarTitle.setText(R.string.edit_address);
            getAddressData();
            btnSaveAddress.setOnClickListener(v -> editAddress());
        } else {
            btnSaveAddress.setText(R.string.add_address);
            toolBarTitle.setText(R.string.add_address);
            btnSaveAddress.setOnClickListener(v -> {
                if (validateInputs()) {
                    addAddress();
                }
            });
        }
        backBtn.setOnClickListener(v -> onBackPressed());

        setStatusBarColor(R.color.home);
    }

    private void requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            getLocation();
        }
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                fetchAddressFromLocation(location);
            } else {
                Toast.makeText(this, "Unable to fetch location. Try again.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> Toast.makeText(this, "Failed to get location: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void fetchAddressFromLocation(Location location) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);

                // Update UI with fetched address details
                addAddressPincode.setText(address.getPostalCode());
                addAddressState.setText(address.getAdminArea());
                addAddressCity.setText(address.getLocality());
                addAddressRoadName.setText(address.getThoroughfare());

                Toast.makeText(this, "Location fetched successfully!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "No address found for this location.", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            Toast.makeText(this, "Error fetching address: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateInputs() {
        if (addAddressFullName.getText().toString().isEmpty()) {
            addAddressFullName.setError("Full Name is required");
            return false;
        }
        if (addAddressPhoneNumber.getText().toString().isEmpty()) {
            addAddressPhoneNumber.setError("Phone Number is required");
            return false;
        }
        if (addAddressPincode.getText().toString().isEmpty()) {
            addAddressPincode.setError("Pincode is required");
            return false;
        }
        if (addAddressState.getText().toString().isEmpty()) {
            addAddressState.setError("State is required");
            return false;
        }
        if (addAddressCity.getText().toString().isEmpty()) {
            addAddressCity.setError("City is required");
            return false;
        }
        if (addAddressHouseNo.getText().toString().isEmpty()) {
            addAddressHouseNo.setError("House No. is required");
            return false;
        }
        if (addAddressRoadName.getText().toString().isEmpty()) {
            addAddressRoadName.setError("Road name is required");
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            } else {
                Toast.makeText(this, "Location permission denied. Cannot fetch location.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void navigateToCartFragment() {
        // Navigate to MainActivity and pass data to show CartFragment
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("showFragment", "cart");
        startActivity(intent);
        finish(); // Close the current activity if necessary
    }

    private void addAddress() {
        progress();

        String fullName = addAddressFullName.getText().toString();
        String phoneNumber = addAddressPhoneNumber.getText().toString();
        String pincode = addAddressPincode.getText().toString();
        String state = addAddressState.getText().toString();
        String city = addAddressCity.getText().toString();
        String houseNo = addAddressHouseNo.getText().toString();
        String roadName = addAddressRoadName.getText().toString();
        boolean isDefault = isDefaultCheckBox.isChecked();

        String addressType = ((RadioButton) findViewById(rgAddressType.getCheckedRadioButtonId())).getText().toString();

        // Create a HashMap to store the address data
        HashMap<String, Object> addressData = new HashMap<>();
        addressData.put("fullName", fullName);
        addressData.put("phoneNumber", phoneNumber);
        addressData.put("houseNo", houseNo);
        addressData.put("roadName", roadName);
        addressData.put("city", city);
        addressData.put("state", state);
        addressData.put("pinCode", pincode);
        addressData.put("addressType", addressType);
        addressData.put("isDefault", isDefault);

        // If the new address is marked as default, update other addresses first
        if (isDefault) {
            db.collection("Users")
                    .document(uid)
                    .collection("Addresses")
                    .whereEqualTo("isDefault", true)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        // Batch to update existing addresses
                        WriteBatch batch = db.batch();
                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                            batch.update(document.getReference(), "isDefault", false);
                        }
                        batch.commit()
                                .addOnSuccessListener(aVoid -> {
                                    // Proceed to add the new address after updating others
                                    saveNewAddress(addressData);
                                })
                                .addOnFailureListener(e -> {
                                    clearAllValue();
                                    CustomToast.showToast(Add_Address_Activity.this, "Failed to update other addresses");
                                });
                    })
                    .addOnFailureListener(e -> {
                        clearAllValue();
                        CustomToast.showToast(Add_Address_Activity.this, "Failed to check existing addresses");
                    });
        } else {
            saveNewAddress(addressData);
        }
    }

    private void saveNewAddress(HashMap<String, Object> addressData) {
        db.collection("Users")
                .document(uid)
                .collection("Addresses")
                .add(addressData)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        String addressId = documentReference.getId();
                        documentReference.update("aid", addressId)
                                .addOnSuccessListener(aVoid -> {
                                    CustomToast.showToast(Add_Address_Activity.this, "Address added");
                                    clearAllValue();
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    clearAllValue();
                                    CustomToast.showToast(Add_Address_Activity.this, "Failed to update addressId");
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        clearAllValue();
                        CustomToast.showToast(Add_Address_Activity.this, "Failed to add address");
                    }
                });
    }

    private void getAddressData(){
        DocumentReference reference = db.collection("Users").document(uid).collection("Addresses").document(addressID);

        reference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null && value.exists()){
                    addAddressFullName.setText(value.getString("fullName"));
                    addAddressPhoneNumber.setText(value.getString("phoneNumber"));
                    addAddressPincode.setText(value.getString("pinCode"));
                    addAddressState.setText(value.getString("state"));
                    addAddressCity.setText(value.getString("city"));
                    addAddressHouseNo.setText(value.getString("houseNo"));
                    addAddressRoadName.setText(value.getString("roadName"));
                    isDefaultCheckBox.setChecked(value.getBoolean("isDefault"));
                    rgAddressType.check(value.getString("addressType").equals("Home") ? R.id.rbHome : R.id.rbWork);
                }
            }
        });
    }

    private void editAddress(){
        if (addressID == null) {
            Toast.makeText(this, "No address to edit.", Toast.LENGTH_SHORT).show();
            return;
        }

        progress();

        String fullName = addAddressFullName.getText().toString();
        String phoneNumber = addAddressPhoneNumber.getText().toString();
        String pincode = addAddressPincode.getText().toString();
        String state = addAddressState.getText().toString();
        String city = addAddressCity.getText().toString();
        String houseNo = addAddressHouseNo.getText().toString();
        String roadName = addAddressRoadName.getText().toString();
        boolean isDefault = isDefaultCheckBox.isChecked();

        String addressType = ((RadioButton) findViewById(rgAddressType.getCheckedRadioButtonId())).getText().toString();

        HashMap<String, Object> updatedAddressData = new HashMap<>();
        updatedAddressData.put("fullName", fullName);
        updatedAddressData.put("phoneNumber", phoneNumber);
        updatedAddressData.put("houseNo", houseNo);
        updatedAddressData.put("roadName", roadName);
        updatedAddressData.put("city", city);
        updatedAddressData.put("state", state);
        updatedAddressData.put("pinCode", pincode);
        updatedAddressData.put("addressType", addressType);
        updatedAddressData.put("isDefault", isDefault);

        DocumentReference addressRef = db.collection("Users").document(uid).collection("Addresses").document(addressID);

        if (isDefault) {
            db.collection("Users")
                    .document(uid)
                    .collection("Addresses")
                    .whereEqualTo("isDefault", true)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        WriteBatch batch = db.batch();
                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                            if (!document.getId().equals(addressID)) {
                                batch.update(document.getReference(), "isDefault", false);
                            }
                        }
                        batch.commit()
                                .addOnSuccessListener(aVoid -> {
                                    saveEditedAddress(addressRef, updatedAddressData);
                                })
                                .addOnFailureListener(e -> {
                                    clearAllValue();
                                    CustomToast.showToast(Add_Address_Activity.this, "Failed to update other addresses");
                                });
                    })
                    .addOnFailureListener(e -> {
                        clearAllValue();
                        CustomToast.showToast(Add_Address_Activity.this, "Failed to check existing addresses");
                    });
        } else {
            saveEditedAddress(addressRef, updatedAddressData);
        }
    }

    private void saveEditedAddress(DocumentReference addressRef, HashMap<String, Object> updatedAddressData) {
        addressRef.update(updatedAddressData)
                .addOnSuccessListener(aVoid -> {
                    CustomToast.showToast(Add_Address_Activity.this, "Address updated successfully");
                    clearAllValue();
                    finish();
                })
                .addOnFailureListener(e -> {
                    clearAllValue();
                    CustomToast.showToast(Add_Address_Activity.this, "Failed to update address");
                });
    }


    private void clearAllValue(){
        addAddressFullName.setText("");
        addAddressPhoneNumber.setText("");
        addAddressPincode.setText("");
        addAddressState.setText("");
        addAddressCity.setText("");
        addAddressHouseNo.setText("");
        addAddressRoadName.setText("");
        rgAddressType.clearCheck();
        isDefaultCheckBox.setChecked(false);

        addressProgressBar.setVisibility(View.GONE);
        btnSaveAddress.setVisibility(View.VISIBLE);
    }

    // handle progress bar
    public void progress(){
        if (btnSaveAddress.isPressed()){
            btnSaveAddress.setVisibility(View.GONE);
            addressProgressBar.setVisibility(View.VISIBLE);
        }else {
            btnSaveAddress.setVisibility(View.VISIBLE);
            addressProgressBar.setVisibility(View.GONE);
        }
    }

    private void setStatusBarColor(int colorResource) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(getResources().getColor(colorResource));
        }
    }

}
