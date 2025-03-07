package com.example.swiftmart.Frgments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.swiftmart.Account.Address_Activity;
import com.example.swiftmart.Account.Edit_profile_Activity;
import com.example.swiftmart.Account.Language_Activity;
import com.example.swiftmart.Account.OrdersActivity;
import com.example.swiftmart.Account.WishlistActivity;
import com.example.swiftmart.LoginActivity;
import com.example.swiftmart.MainActivity;
import com.example.swiftmart.R;
import com.example.swiftmart.WelcomeActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


public class AccountFragment extends Fragment {
    private ImageButton btnEditProfile, btnLanguage, btnOrderHistory, btnWishlist, btnAboutUs, btnPrivacyPolicy, btnDeleteUser, profileRateUsBtn, btnAddress;
    private AppCompatButton btnLogout;
    private LinearLayout llWishlist, llEditProfile, llOrderHistory, llAboutUs, llPrivacyPolicy,llLanguage, llDeleteUser, llRateUs,llsavedaddress;
    private TextView accountFragmentUserName;
    private String uid;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private ProgressBar accountFragmentProgressBar;
    private AlertDialog dialog;

    public AccountFragment() {

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_account, container, false);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Check if user is logged in, redirect to login if not
        if (mAuth.getCurrentUser() == null) {
            redirectToLogin();
            return view;
        }

        uid = mAuth.getCurrentUser().getUid();

        accountFragmentUserName = view.findViewById(R.id.accountFragmentUserName);

        llWishlist = view.findViewById(R.id.llwishlist);
        llEditProfile = view.findViewById(R.id.llEditProfile);
        llOrderHistory = view.findViewById(R.id.llOrderHistory);
        llAboutUs = view.findViewById(R.id.llAboutUs);
        llPrivacyPolicy = view.findViewById(R.id.llPrivacyPolicy);
        llLanguage = view.findViewById(R.id.llLanguage);
        llDeleteUser = view.findViewById(R.id.llDeleteUser);
        llRateUs = view.findViewById(R.id.llRateUs);
        llsavedaddress = view.findViewById(R.id.llsavedaddress);

        btnEditProfile = view.findViewById(R.id.profileEditProfileBtn);
        btnLanguage = view.findViewById(R.id.profileSelectLanguageBtn);
        btnOrderHistory = view.findViewById(R.id.profileOrderHistoryBtn);
        btnWishlist = view.findViewById(R.id.wishlist);
        btnAboutUs = view.findViewById(R.id.profileAboutUsBtn);
        btnPrivacyPolicy = view.findViewById(R.id.profilePrivacyPolicyBtn);
        btnDeleteUser = view.findViewById(R.id.profileDeleteUserBtn);
        profileRateUsBtn = view.findViewById(R.id.profileRateUsBtn);
        btnAddress = view.findViewById(R.id.addressBtn);

        btnLogout = view.findViewById(R.id.userLogout);
        accountFragmentProgressBar = view.findViewById(R.id.accountFragmentProgressBar);

        getUserData();

        handleEditProfileClick();
        handleOrderHistoryClick();
        handleSelectLanguageClick();
        handleWishlistClick();
        handleSavedAddressClick();
        handleDeleteAccountClick();

        handleAboutUsClick();
        handlePrivacyPolicyClick();
        handleRateUsClick();

        handleOnBackPress();
        handleUserLogout();

        return view;
    }

    // Redirect to login screen
    private void redirectToLogin() {
        if (getContext() != null) {
            Intent intent = new Intent(getContext(), LoginActivity.class);
            startActivity(intent);
            if (getActivity() != null) {
                getActivity().finish();
            }
        }
    }

    // Get the user data from the database
    private void getUserData(){
        if (mAuth.getCurrentUser() == null) {
            redirectToLogin();
            return;
        }

        uid = mAuth.getCurrentUser().getUid();
        DocumentReference reference = db.collection("Users").document(uid);

        reference.addSnapshotListener((value, error) -> {
            if (value != null && value.exists()){
                String username = value.getString("Username");
                if (username != null && username.contains(" ")) {
                    accountFragmentUserName.setText(username.split(" ")[0]);
                } else {
                    accountFragmentUserName.setText(username);
                }
            }
        });
    }

    // handle edit profile click
    private void handleEditProfileClick(){
        btnEditProfile.setOnClickListener(v -> {
            if (mAuth.getCurrentUser() == null) {
                redirectToLogin();
                return;
            }
            Intent intent = new Intent(getContext(), Edit_profile_Activity.class);
            startActivity(intent);
        });

        llEditProfile.setOnClickListener(v -> {
            if (mAuth.getCurrentUser() == null) {
                redirectToLogin();
                return;
            }
            Intent intent = new Intent(getContext(), Edit_profile_Activity.class);
            startActivity(intent);
        });
    }

    // handle order history click
    private void handleOrderHistoryClick(){
        btnOrderHistory.setOnClickListener(v -> {
            if (mAuth.getCurrentUser() == null) {
                redirectToLogin();
                return;
            }
            Intent intent = new Intent(getContext(), OrdersActivity.class);
            startActivity(intent);
        });

        llOrderHistory.setOnClickListener(v -> {
            if (mAuth.getCurrentUser() == null) {
                redirectToLogin();
                return;
            }
            Intent intent = new Intent(getContext(), OrdersActivity.class);
            startActivity(intent);
        });
    }

    // handle select language click
    private void handleSelectLanguageClick(){
        btnLanguage.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), Language_Activity.class);
            startActivity(intent);
        });

        llLanguage.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), Language_Activity.class);
            startActivity(intent);
        });
    }

    // handle wishlist click
    private void handleWishlistClick(){
        btnWishlist.setOnClickListener(v -> {
            if (mAuth.getCurrentUser() == null) {
                redirectToLogin();
                return;
            }
            Intent intent = new Intent(getContext(), WishlistActivity.class);
            startActivity(intent);
        });

        llWishlist.setOnClickListener(v -> {
            if (mAuth.getCurrentUser() == null) {
                redirectToLogin();
                return;
            }
            Intent intent = new Intent(getContext(), WishlistActivity.class);
            startActivity(intent);
        });
    }

    // handle saved address click
    private void handleSavedAddressClick(){
        llsavedaddress.setOnClickListener(v -> {
            if (mAuth.getCurrentUser() == null) {
                redirectToLogin();
                return;
            }
            Intent intent = new Intent(getContext(), Address_Activity.class);
            startActivity(intent);
        });

        btnAddress.setOnClickListener(v -> {
            if (mAuth.getCurrentUser() == null) {
                redirectToLogin();
                return;
            }
            Intent intent = new Intent(getContext(), Address_Activity.class);
            startActivity(intent);
        });
    }

    // handle delete account click
    private void handleDeleteAccountClick(){
        btnDeleteUser.setOnClickListener(v -> {
            if (mAuth.getCurrentUser() == null) {
                redirectToLogin();
                return;
            }
            displayDialog();
        });

        llDeleteUser.setOnClickListener(v -> {
            if (mAuth.getCurrentUser() == null) {
                redirectToLogin();
                return;
            }
            displayDialog();
        });
    }


    // TODO handle about us click
    private void handleAboutUsClick(){
        // This doesn't require user authentication
    }

    // TODO handle privacy policy click
    private void handlePrivacyPolicyClick(){
        // This doesn't require user authentication
    }

    // TODO handle rate us click
    private void handleRateUsClick(){
        // This doesn't require user authentication
    }

    private void displayDialog(){
        if (mAuth.getCurrentUser() == null) {
            redirectToLogin();
            return;
        }

        dialog = new AlertDialog.Builder(getContext())
                         .setView(R.layout.delete_account_dialog)
                         .setCancelable(false)
                         .create();
        dialog.show();

        AppCompatButton deleteButton = dialog.findViewById(R.id.deleteButton);
        AppCompatButton cancelButton = dialog.findViewById(R.id.cancelButton);


        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAuth.getCurrentUser() == null) {
                    dialog.dismiss();
                    redirectToLogin();
                    return;
                }

                db.collection("Users")
                        .document(uid)
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                mAuth.signOut();
                                if (getContext() != null) {
                                    Intent intent = new Intent(getContext(), WelcomeActivity.class);
                                    startActivity(intent);
                                    if (getActivity() != null) {
                                        getActivity().finish();
                                    }
                                }
                            }
                        });

                dialog.dismiss();
            }
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    // handle onBack press
    private void handleOnBackPress(){
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frameLayout, new HomeFragment())
                        .commit();

//                BottomNavigationView bottomNavigationView = requireActivity().findViewById(R.id.bottomNavigationView);
//                bottomNavigationView.setSelectedItemId(R.id.home);
            }
        });
    }

    // handle user logout
    private void handleUserLogout(){
        btnLogout.setOnClickListener(v -> {
            if (mAuth.getCurrentUser() == null) {
                redirectToLogin();
                return;
            }

            mAuth.signOut();
            if (getContext() != null){
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
                progress();
                if (getActivity() != null) {
                    getActivity().finish();
                }
            }
        });
    }

    // handle progress bar
    public void progress(){
        if (btnLogout.isPressed()){
            btnLogout.setVisibility(View.GONE);
            accountFragmentProgressBar.setVisibility(View.VISIBLE);
        }else {
            btnLogout.setVisibility(View.VISIBLE);
            accountFragmentProgressBar.setVisibility(View.GONE);
        }
    }
}